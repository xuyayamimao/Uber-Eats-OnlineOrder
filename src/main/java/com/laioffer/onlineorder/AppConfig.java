package com.laioffer.onlineorder;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;


import javax.sql.DataSource;


//
@Configuration
public class AppConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //cross-site request forgery（一种attack的方式）disabled，不过在industry中都是disabled的
                .csrf().disable()
                //把一些路径变成public access
                .authorizeHttpRequests(auth ->
                        auth
                                //permit all白名单
                                //让所有前端的web resource都可以被browser拿到
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers(HttpMethod.GET, "/", "/index.html", "/*.json", "/*.png", "/static/**").permitAll()//web resource
                                //login, logout, signup都允许public access
                                .requestMatchers(HttpMethod.POST, "/login", "/logout", "/signup").permitAll()
                                //restaurants和restaurants后面包含的都是可以public access，这样在postman中发关于restaurant的request就会是200ok
                                .requestMatchers(HttpMethod.GET, "/restaurants/**", "/restaurant/**").permitAll()
                                .anyRequest().authenticated()
                )
                //出错的话，直接丢UNAUTHORIZED error
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                //一种login的方式
                .formLogin()
                .successHandler((req, res, auth) -> res.setStatus(HttpStatus.OK.value()))
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
                //如果logout，就returnHttpStatus.OK
                .logout()
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
        return http.build();
    }











    //UserDetailsManager是用来告诉spring你装user information的db在哪里，并且让spring连接上这样我们可以直接用spring来改数据，不需要自己写repository
    @Bean
    UserDetailsManager users(DataSource dataSource) {
        //JdbcUserDetailsManager uses the JDBC data source connection (SQL) to store and read user information
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        //创建新的customer的时候可以使用spring的userDetailsManager中的method来创建
        //下面的代码是为了告诉spring怎么往db里面放东西
        //创建用户：问号是place holder，用来放customerservice里面我们传进去的参数
        userDetailsManager.setCreateUserSql("INSERT INTO customers (email, password, enabled) VALUES (?,?,?)");
        userDetailsManager.setCreateAuthoritySql("INSERT INTO authorities (email, authority) values (?,?)");
        //这一步中spring就会验证输进去的密码是不是这个用户的密码
        userDetailsManager.setUsersByUsernameQuery("SELECT email, password, enabled FROM customers WHERE email = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery("SELECT email, authorities FROM authorities WHERE email = ?");
        return userDetailsManager;
    }

    //因为db里面不能直接存用户的密码，要存的是encrypted的密码->我们用PasswordEncoder class里method来给密码加密
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}

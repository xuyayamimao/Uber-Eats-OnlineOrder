package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.CartEntity;
import com.laioffer.onlineorder.entity.CustomerEntity;
import com.laioffer.onlineorder.repository.CartRepository;
import com.laioffer.onlineorder.repository.CustomerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;

    //在AppConfig中的东西
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsManager userDetailsManager;

    public CustomerService(CartRepository cartRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder, UserDetailsManager userDetailsManager){
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsManager = userDetailsManager;
    }


    //加transactional是为了如果中间的某个步骤出错的话，整一个signup过程都作废，避免signup出现混沌的状态

    /**
     * new user signup
     * @param email
     * @param password
     * @param firstName
     * @param lastName
     */
    @Transactional
    public void signUp(String email, String password, String firstName, String lastName){
        //因为email是不区分大小写的，所以我们要先改成lowercase
        email = email.toLowerCase();

        //创建UserDetails object，根据builder pattern来build，我们提供所有需要的信息
        //builder pattern用来创建新的object的，并且我们怎么改builder(), build()之间信息的顺序都是一样的
        UserDetails user = User.builder()
                .username(email)
                .password(passwordEncoder.encode(password))
                .roles("USER")
                .build();

        //userDetailsManager只管用户名和密码，所以我们要用customerRepository把user的email， firstname，lastname改一下
        userDetailsManager.createUser(user);
        customerRepository.updateNameByEmail(email, firstName, lastName);


        //get the new CustomerEntity并且把对应的cart放进db里面
        CustomerEntity savedCustomer = customerRepository.findByEmail(email);
        CartEntity cart = new CartEntity(null, savedCustomer.id(), 0.0);
        cartRepository.save(cart);

    }

    public CustomerEntity getCustomerByEmail(String email){
        return customerRepository.findByEmail(email);
    }
}

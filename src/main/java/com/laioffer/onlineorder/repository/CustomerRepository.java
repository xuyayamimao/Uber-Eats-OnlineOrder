package com.laioffer.onlineorder.repository;

import com.laioffer.onlineorder.entity.CustomerEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

//给repository用interface的原因是我们不需要实现这些代码，spring帮我们实现
//ListCrudRepository<CustomerEntity, Long>,<>中的第一个决定你repository.save的时候可以塞进去什么东西
//第二个element决定什么是primary key，这里可以是这三种type：String, UUID，long
public interface CustomerRepository extends ListCrudRepository<CustomerEntity, Long> {
    //Spring会根据这个function name来自动implement sql query
    //spring之所以知道怎么implement是因为它已经知道你的db structure
    List<CustomerEntity> findByFirstName(String firstName);


    List<CustomerEntity> findByLastName(String lastName);


    CustomerEntity findByEmail(String email);


    //@Query是告诉spring这个是自定义的query
    //@Modifying是告诉spring这个是修改db的操作，因为你在update table
    @Modifying
    @Query("UPDATE customers SET first_name = :firstName, last_name = :lastName WHERE id = :id")
    void updateNameById(long id, String firstName, String lastName);

    @Modifying
    @Query("UPDATE customers SET first_name = :firstName, last_name = :lastName WHERE email = :email")
    void updateNameByEmail(String email, String firstName, String lastName);
}

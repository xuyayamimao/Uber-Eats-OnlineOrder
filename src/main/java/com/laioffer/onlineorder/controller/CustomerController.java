package com.laioffer.onlineorder.controller;

import com.laioffer.onlineorder.model.RegisterBody;
import com.laioffer.onlineorder.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    private final CustomerService customerService;


    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @PostMapping("/signup")
    @ResponseStatus(value = HttpStatus.CREATED)//在postman里面成功的话，return的是200ok，但是我们想要他return CREATED,所以写这一行
    public void signUp(@RequestBody RegisterBody body) {
        customerService.signUp(body.email(), body.password(), body.firstName(), body.lastName());
    }
    //之所以这里没有login和logout是因为spring已经提供这些操作了

}

package com.laioffer.onlineorder.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterBody(
        String email,
        String password,

        //下面两个之所以要用@JsonProperty是因为我们要读取first_name来放进firstName，上面两个不用是因为他们的snake case和camel case是长一样的
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName
) {
}

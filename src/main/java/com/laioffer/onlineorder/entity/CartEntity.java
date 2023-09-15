package com.laioffer.onlineorder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("carts")
public record CartEntity(
        //@Id是为了告诉spring这个是table的primary key
        @Id Long id,
        Long customerId,
        Double totalPrice

) {
}

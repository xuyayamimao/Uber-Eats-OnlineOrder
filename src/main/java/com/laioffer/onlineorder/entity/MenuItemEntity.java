package com.laioffer.onlineorder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("menu_items")
public record MenuItemEntity(
        @Id Long id,

        //menuItem和restaurant是多对一的关系，我们用restaurantId这个foreign key来建立这个关系
        Long restaurantId,
        String name,
        String description,
        Double price,
        String imageUrl
) {

}

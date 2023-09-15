package com.laioffer.onlineorder.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddToCartBody(
        //@JsonProperty是为了可以把menu_id直接map到menuId里面，这样可以直接被CartController中的addToCart使用
        @JsonProperty("menu_id") Long menuId
) {
}

package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.CartEntity;
import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.entity.OrderItemEntity;
import com.laioffer.onlineorder.model.CartDto;
import com.laioffer.onlineorder.model.OrderItemDto;
import com.laioffer.onlineorder.repository.CartRepository;
import com.laioffer.onlineorder.repository.MenuItemRepository;
import com.laioffer.onlineorder.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;


    public CartService(CartRepository cartRepository, MenuItemRepository menuItemRepository, OrderItemRepository orderItemRepository){
        this.cartRepository = cartRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

//    //@Transactional
//    @Transactional
//    public void addMenuItemToCart(long customerId, long menuItemId){
//        CartEntity cart = cartRepository.getByCustomerId(customerId);
//        MenuItemEntity menuItem = menuItemRepository.findById(menuItemId).get();
//        OrderItemEntity orderItem = orderItemRepository.findByCartIdAndMenuItemId(cart.id(), menuItemId);
//        Long orderItemId;
//        int quantity;
//
//        //如果是第一个加进去order的item，那么orderitem是null，quantity就只有一个，然后orderItemId为null是为了之后加进cartRepository为了autoincrement
//        if(orderItem == null){
//            orderItemId = null;
//            quantity = 1;
//        }else{
//            //如果不是第一次加进去的，那么increment quantity
//            orderItemId = orderItem.id();
//            quantity = orderItem.quantity() +1;
//        }
//
//        //更新这个order里面的东西
//        OrderItemEntity newOrderItem = new OrderItemEntity(orderItemId, menuItemId, cart.id(), menuItem.price(), quantity);
//        orderItemRepository.save(newOrderItem);
//        //更新cart里面的total price
//        cartRepository.updateTotalPrice(cart.id(), cart.totalPrice() + menuItem.price());
//
//    }

    @Transactional
    public void addMenuItemToCart(long customerId, long menuItemId) {
        CartEntity cart = cartRepository.getByCustomerId(customerId);
        MenuItemEntity menuItem = menuItemRepository.findById(menuItemId).get();
        OrderItemEntity orderItem = orderItemRepository.findByCartIdAndMenuItemId(cart.id(), menuItem.id());
        Long orderItemId;
        int quantity;


        if (orderItem == null) {
            orderItemId = null;
            quantity = 1;
        } else {
            orderItemId = orderItem.id();
            quantity = orderItem.quantity() + 1;
        }
        OrderItemEntity newOrderItem = new OrderItemEntity(orderItemId, menuItemId, cart.id(), menuItem.price(), quantity);
        orderItemRepository.save(newOrderItem);
        cartRepository.updateTotalPrice(cart.id(), cart.totalPrice() + menuItem.price());
    }



    public CartDto getCart(Long customerId){
        CartEntity cart = cartRepository.getByCustomerId(customerId);
        List<OrderItemEntity> orderItems = orderItemRepository.getAllByCartId(cart.id());
        List<OrderItemDto> orderItemDtos = getOrderItemDtos(orderItems);
        return new CartDto(cart, orderItemDtos);
    }

    //transactional确保的是如果其中一个步骤出错的话，那么整一个更改都会roll back
    @Transactional
    public void clearCart(Long custormerId){
        //get出来customerId对应的cartEntity
        CartEntity cartEntity = cartRepository.getByCustomerId(custormerId);
        //把这个cartId对应的orderItems给删掉
        orderItemRepository.deleteByCartId(cartEntity.id());
        //update total price
        cartRepository.updateTotalPrice(cartEntity.id(), 0.0);
    }

    public List<OrderItemDto> getOrderItemDtos(List<OrderItemEntity> orderItems){
        /*
        OrderItemDto需要
        1. OrderItemEntity (我们已经有了）2. MenuItemEntity:需要用OrderItemEntity的menuItemId拿出来
         */

        //先把所有的OrderItemEntity对应的menuItemId拿出来
        Set<Long> menuItemIds = new HashSet<>();
        for(OrderItemEntity orderItem : orderItems){
            menuItemIds.add(orderItem.menuItemId());
        }

        //get出所有符合id的menuItems
        List<MenuItemEntity> menuItems = menuItemRepository.findAllById(menuItemIds);
        //用hashmap装id -》 MenuItemEntity方便查找
        Map<Long, MenuItemEntity> menuItemMap = new HashMap<>();
        for(MenuItemEntity menuItem : menuItems){
            menuItemMap.put(menuItem.id(), menuItem);
        }

        //把每一个orderItemEntity里面对应的MenuItemEntity给拿出来，并且生成List<OrderItemDto>
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for(OrderItemEntity orderItem :orderItems){
            MenuItemEntity menuItem = menuItemMap.get(orderItem.menuItemId());
            OrderItemDto orderItemDto = new OrderItemDto(orderItem, menuItem);
        }

        //        // Stream version:
//        Set<Long> menuItemIds = orderItems.stream()
//                .map(OrderItemEntity::menuItemId)
//                .collect(Collectors.toSet());
//
//        List<MenuItemEntity> menuItems = menuItemRepository.findAllById(menuItemIds);
//
//        Map<Long, MenuItemEntity> menuItemMap = menuItems.stream()
//                .collect(Collectors.toMap(MenuItemEntity::id, menuItem -> menuItem));
//
//        List<OrderItemDto> orderItemDtos = orderItems.stream()
//                .map(orderItem -> {
//                    MenuItemEntity menuItem = menuItemMap.get(orderItem.menuItemId());
//                    return new OrderItemDto(orderItem, menuItem);
//                })
//                .toList();
        return orderItemDtos;
    }
}

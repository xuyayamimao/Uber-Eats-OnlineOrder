package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.entity.RestaurantEntity;
import com.laioffer.onlineorder.model.MenuItemDto;
import com.laioffer.onlineorder.model.RestaurantDto;
import com.laioffer.onlineorder.repository.MenuItemRepository;
import com.laioffer.onlineorder.repository.RestaurantRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestaurantService {
    //之所以是final因为是不能修改的，因为repository不用修改，要修改也是修改db本身
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;


    public RestaurantService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        //这两个repository不是我们自己创建的，这是springboot自己创建的
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Get all restaurant as DTO
     * @return a list of RestaurantDto
     */
    @Cacheable("restaurants")
    public List<RestaurantDto> getRestaurants() {
        //get出所有的restarurant entities以及所有的menu entities
        List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();
        List<MenuItemEntity> menuItemEntities = menuItemRepository.findAll();

        //用hashmap来存放RestaurantDto需要的每个restaurant的menuitems
        Map<Long, List<MenuItemDto>> groupedMenuItems = new HashMap<>();

        //对于每一个menuEntity,我们要把restaurantId和menuItemDto对应起来
        for (MenuItemEntity menuItemEntity : menuItemEntities) {
            //如果这个menuItemEntity.restaurantId()在hashmap中还没有对应的value，就创建一个arraylist
            //如果这个menuItemEntity.restaurantId()在hashmap中已经有对应的value，那么group就等于那个value
            //如果这个menuItemEntity.restaurantId()在hashmap中不存在，那么加入新的key和对应的computed value
            //节省了很多if statements
            List<MenuItemDto> group = groupedMenuItems.computeIfAbsent(menuItemEntity.restaurantId(), k -> new ArrayList<>());

            //get出menuItemDtp并加到这个hashmap的value list里面
            MenuItemDto menuItemDto = new MenuItemDto(menuItemEntity);
            group.add(menuItemDto);
        }

        List<RestaurantDto> results = new ArrayList<>();
        for (RestaurantEntity restaurantEntity : restaurantEntities) {
            RestaurantDto restaurantDto = new RestaurantDto(restaurantEntity, groupedMenuItems.get(restaurantEntity.id()));
            results.add(restaurantDto);
        }




        return results;
    }


}

package com.laioffer.onlineorder.repository;

import com.laioffer.onlineorder.entity.RestaurantEntity;
import org.springframework.data.repository.ListCrudRepository;

//之所以这个interface里面没有methods是因为我们没有特定的methods要用，除了spring提供的methods
public interface RestaurantRepository extends ListCrudRepository<RestaurantEntity, Long> {
}

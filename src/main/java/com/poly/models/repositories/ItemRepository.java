package com.poly.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.models.entities.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}

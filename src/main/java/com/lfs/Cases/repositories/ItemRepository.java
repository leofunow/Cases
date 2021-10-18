package com.lfs.Cases.repositories;

import com.lfs.Cases.models.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Long> {}
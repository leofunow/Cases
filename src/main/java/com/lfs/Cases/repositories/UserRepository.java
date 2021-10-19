package com.lfs.Cases.repositories;

import com.lfs.Cases.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {}

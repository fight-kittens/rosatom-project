package com.github.fightkittens.kronos.repositories;

import com.github.fightkittens.kronos.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Users findByUsername(String username);
}
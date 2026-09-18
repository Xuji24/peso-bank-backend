package com.ciicc.peso_bank.repository;

import com.ciicc.peso_bank.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User, Long>{

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.profile")
    List<User> findAllWithProfile();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.userId = :id")
    Optional<User> findWithProfileById(Long id);

}

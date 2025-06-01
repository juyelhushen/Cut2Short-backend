package com.url.shortner.repository;

import com.url.shortner.entity.User;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT U FROM User U WHERE U.email = :email")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Optional<User> findByEmail(@Param("email") String email);

    Optional<User> findByOauth2Id(String oauth2Id);
}

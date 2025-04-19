package com.url.shortner.repository;

import com.url.shortner.entity.Url;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Integer> {

    @Query("SELECT U FROM Url U WHERE U.shortenUrl = :shortenUrl")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Optional<Url> findByShortenUrl(String shortenUrl);

    @Query("SELECT U FROM Url U WHERE U.originalUrl = :originalUrl")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Optional<Url> findByOriginalUrl(@Param("originalUrl") String originalUrl);

    Page<Url> findUrlsByUserId(int userId, Pageable pageable);
}

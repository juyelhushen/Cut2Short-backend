package com.url.shortner.repository;

import com.url.shortner.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Integer> {
    Optional<Url> findByShortUrl(String shortUrl);
}

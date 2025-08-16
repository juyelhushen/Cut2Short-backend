package com.url.shortner.repository;

import com.url.shortner.entity.QRCode;
import com.url.shortner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    List<QRCode> findByUrlUserId(Integer userId);
}

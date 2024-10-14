package com.url.shortner.service;

import com.url.shortner.entity.Url;
import com.url.shortner.repository.UrlRepository;
import com.url.shortner.utils.HashGenerator;
import com.url.shortner.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

import static java.util.Base64.*;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    @Override
    public List<Url> getUrls() {
        return urlRepository.findAll();
    }


    @Override
    public String filterUrl(String OUrl) {
        //TODO
        return "";
    }


    @Override
    public String createUrl(String filteredUrl, String originalUrl) {
        //TODO
        return "";
    }

    @Override
    public String shortenUrl(String originalUrl) throws NoSuchAlgorithmException {
        String shortenedUrl = HashGenerator.generateHash(originalUrl);

    }

    private String generateShortUrl() {
        return getEncoder().encode();
    }
}

package com.url.shortner.service;

import com.url.shortner.entity.Url;
import com.url.shortner.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
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
}

package com.url.shortner.service;

import com.url.shortner.entity.Url;
import com.url.shortner.repository.UrlRepository;
import com.url.shortner.utils.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


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
        Optional<Url> exitingUrl = urlRepository.findByOriginalUrl(originalUrl);
        if (exitingUrl.isPresent()) {
            return exitingUrl.get().getShortUrl();
        }

        String shortenedUrl = HashGenerator.generateHash(originalUrl);
        Url newUrl = new Url();
        newUrl.setOriginalUrl(originalUrl);
        newUrl.setShortUrl(shortenedUrl);
        urlRepository.save(newUrl);
        return newUrl.getShortUrl();
    }

    @Override
    public String getOriginalUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("Url not found"));
        return url.getOriginalUrl();
    }
}

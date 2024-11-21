package com.url.shortner.service.imple;

import com.url.shortner.entity.Url;
import com.url.shortner.payload.UrlRequest;
import com.url.shortner.repository.UrlRepository;
import com.url.shortner.service.UrlService;
import com.url.shortner.utils.HashGenerator;
import com.url.shortner.wrapper.UrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UrlServiceImpl implements UrlService {

    @Value("${url.prefix}")
    private String prefix;

    private final UrlRepository urlRepository;

    @Override
    public List<UrlResponse> findAllUrl() {
        return urlRepository.findAll().stream().map(UrlResponse::new).toList();
    }


    @Override
    public String filterUrl(UrlRequest request) {
        StringBuilder sb = new StringBuilder();
        for (char ch : request.originalUrl().toCharArray()) {
            if (((int) ch > (int) 'a' && (int) ch < (int) 'z') || ((int) ch > (int) 'A' && (int) ch < (int) 'Z') )
                sb.append(ch);
        }
        return sb.toString();
    }


    @Override
    public UrlResponse createUrl(String filteredUrl, String originalUrl) {

        Set<String> urlSet = urlRepository.findAll().stream()
                .map(Url::getShortenUrl)
                .collect(Collectors.toSet());

        StringBuilder res = new StringBuilder();
        StringBuilder suffix = new StringBuilder();

        while(true) {
            for (int i = 0; i < 6; i++) suffix.append(filteredUrl.charAt(new Random().nextInt(filteredUrl.length())));
            res.append(prefix).append(suffix);
            if (!urlSet.contains(res.toString())) break;
            else res = new StringBuilder();
        }

        Url url = Url.builder()
                .originalUrl(originalUrl)
                .shortenUrl(res.toString())
                .build();

        urlRepository.save(url);
        return new UrlResponse(url);
    }

//    @Override
//    public String shortenUrl(UrlRequest request) throws NoSuchAlgorithmException {
//        Optional<Url> exitingUrl = urlRepository.findByOriginalUrl(request.originalUrl());
//        if (exitingUrl.isPresent()) {
//            return exitingUrl.get().getShortUrl();
//        }
//
//        String shortenedUrl = HashGenerator.generateHash(request.originalUrl());
//        Url newUrl = new Url();
//        newUrl.setOriginalUrl(request.originalUrl());
//        newUrl.setShortUrl(shortenedUrl);
//        urlRepository.save(newUrl);
//        return newUrl.getShortUrl();
//    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String getOriginalUrl(String shortUrl) {
        Url url = urlRepository.findByShortenUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("Url not found"));
        return url.getOriginalUrl();
    }
}

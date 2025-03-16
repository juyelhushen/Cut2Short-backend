package com.url.shortner.service.imple;

import com.url.shortner.entity.Url;
import com.url.shortner.entity.User;
import com.url.shortner.exception.ResourceNotFound;
import com.url.shortner.payload.UrlRequest;
import com.url.shortner.payload.UserRequest;
import com.url.shortner.repository.UrlRepository;
import com.url.shortner.repository.UserRepository;
import com.url.shortner.service.UrlService;
import com.url.shortner.wrapper.UrlResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UrlServiceImpl implements UrlService {

    @Value("${url.prefix}")
    private String prefix;

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<UrlResponse> findAllUrl() {
        return urlRepository.findAll().stream().map(UrlResponse::new).toList();
    }


    @Override
    public String filterUrl(UrlRequest request) {
        StringBuilder sb = new StringBuilder();
        for (char ch : request.originalUrl().toCharArray()) {
            if (((int) ch > (int) 'a' && (int) ch < (int) 'z') || ((int) ch > (int) 'A' && (int) ch < (int) 'Z'))
                sb.append(ch);
        }
        return sb.toString();
    }


    @Override
    public UrlResponse createUrlForUser(String filteredUrl, UrlRequest request) {
        Optional<Url> url = urlRepository.findByOriginalUrl(request.originalUrl());
        if (url.isPresent()) return new UrlResponse(url.get());

        Set<String> urlSet = urlRepository.findAll().stream()
                .map(Url::getShortenUrl)
                .collect(Collectors.toSet());

        StringBuilder res = new StringBuilder();
        StringBuilder suffix = new StringBuilder();

        while (true) {
            for (int i = 0; i < 6; i++) suffix.append(filteredUrl.charAt(new Random().nextInt(filteredUrl.length())));
            res.append(suffix);
            if (!urlSet.contains(res.toString())) break;
            else res = new StringBuilder();
        }

        Url newUrl = new Url();
        newUrl.setOriginalUrl(request.originalUrl());
        newUrl.setShortenUrl(res.toString());

        var user = findByUserId(request.userId());
        newUrl.setUser(user);
        newUrl.setTitle(request.title());
        newUrl.setExpires(Instant.now().plus(1, ChronoUnit.YEARS));
        Url save = urlRepository.save(newUrl);
        return new UrlResponse(save);
    }

    @Override
    public UrlResponse createUrl(String filteredUrl, String originalUrl) {

        Optional<Url> url = urlRepository.findByOriginalUrl(originalUrl);
        if (url.isPresent()) return new UrlResponse(url.get());

        Set<String> urlSet = urlRepository.findAll().stream()
                .map(Url::getShortenUrl)
                .collect(Collectors.toSet());

        StringBuilder res = new StringBuilder();
        StringBuilder suffix = new StringBuilder();

        while (true) {
            for (int i = 0; i < 6; i++) suffix.append(filteredUrl.charAt(new Random().nextInt(filteredUrl.length())));
            res.append(suffix);
            if (!urlSet.contains(res.toString())) break;
            else res = new StringBuilder();
        }

        Url newUrl = new Url();
        newUrl.setOriginalUrl(originalUrl);
        newUrl.setShortenUrl(res.toString());


        var save = urlRepository.save(newUrl);
        return new UrlResponse(save);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public CompletableFuture<String> getOriginalUrl(String shortUrl) {
        return CompletableFuture.supplyAsync(() -> {
            Url url = urlRepository.findByShortenUrl(shortUrl)
                    .orElseThrow(() -> new RuntimeException("Url not found"));
            return url.getOriginalUrl();
        });
    }


    @Override
    public List<UrlResponse> findAllUrlByUserId(int userId) {
        var urlListByUserId = urlRepository.findUrlsByUserId(userId);
        return urlListByUserId.stream().map(UrlResponse::new).toList();
    }

    @Override
    public boolean deleteUrlById(int id) {
        if (urlRepository.existsById(id)) {
            urlRepository.deleteById(id);
            return true;
        } else return false;
    }

    @Override
    public UrlResponse updateUrl(UrlRequest request) {
        var url = findUrlById(request.id());
        url.setOriginalUrl(request.originalUrl());
        url.setTitle(request.title());
        var updatedUrl = urlRepository.save(url);
        return new UrlResponse(updatedUrl);
    }

    private Url findUrlById(int id) {
        return urlRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Url not found with id " + id));
    }

    private User findByUserId(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found with id: " + userId));
    }
}

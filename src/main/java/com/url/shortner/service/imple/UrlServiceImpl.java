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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UrlServiceImpl implements UrlService {

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
    public UrlResponse createUrlForUser(String filteredUrl, UrlRequest request, String username) {
        Optional<Url> url = urlRepository.findByOriginalUrl(request.originalUrl());
        if (url.isPresent()) return new UrlResponse(url.get());

        Set<String> urlSet = urlRepository.findAll().stream()
                .map(Url::getShortenUrl)
                .collect(Collectors.toSet());

        String res;
        if (request.suffix() != null && !request.suffix().isBlank()) {
            res = request.suffix();
            if (urlSet.contains(res)) throw new IllegalArgumentException("Custom back-half already in use.");
        } else {
            StringBuilder suffix = new StringBuilder();
            Random random = new Random();

            do {
                suffix.setLength(0);
                for (int i = 0; i < 6; i++) {
                    suffix.append(filteredUrl.charAt(random.nextInt(filteredUrl.length())));
                }
                res = suffix.toString();
            } while (urlSet.contains(res));
        }

        Url newUrl = new Url();
        newUrl.setOriginalUrl(request.originalUrl());
        newUrl.setShortenUrl(res);

        var user = findByUsername(username);
        newUrl.setUser(user);
        newUrl.setTitle(request.title());
        newUrl.setHitCount(0L);
        newUrl.setSuffix(res);

        newUrl.setExpires(LocalDate.now().plusYears(1));

        Url savedUrl = urlRepository.save(newUrl);
        return new UrlResponse(savedUrl);
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
        newUrl.setHitCount(0L);

        var save = urlRepository.save(newUrl);
        return new UrlResponse(save);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public CompletableFuture<String> getOriginalUrl(String shortUrl) {
        return CompletableFuture.supplyAsync(() -> {
            Url url = urlRepository.findByShortenUrl(shortUrl)
                    .orElseThrow(() -> new RuntimeException("Url not found"));
            urlRepository.incrementHitCount(url.getId());
            return url.getOriginalUrl();
        });
    }

    @Override
    public UrlResponse findUrlById(int id) {
        var urlResponse = findById(id);
        return new UrlResponse(urlResponse);
    }

    @Override
    public List<UrlResponse> findAllUrlByUserId(int userid) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());
        Page<Url> urlList = urlRepository.findUrlsByUserId(userid,pageable);
        var urlListByUserId = urlList.getContent();
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
        var url = findById(request.id());
//        url.setOriginalUrl(request.originalUrl());
        url.setTitle(request.title());
        if (request.suffix() != null && !request.suffix().isBlank()) {
            url.setSuffix(request.suffix());
            url.setShortenUrl(request.suffix());
        }
        var updatedUrl = urlRepository.save(url);
        return new UrlResponse(updatedUrl);
    }

    private Url findById(int id) {
        return urlRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Url not found with id " + id));
    }

    private User findByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found with email: " + email));
    }

}

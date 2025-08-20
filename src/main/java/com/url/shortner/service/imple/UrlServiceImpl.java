package com.url.shortner.service.imple;


import com.url.shortner.entity.QRCode;
import com.url.shortner.entity.Url;
import com.url.shortner.entity.User;
import com.url.shortner.exception.ResourceNotFound;
import com.url.shortner.payload.QRCodeRequest;
import com.url.shortner.payload.UrlRequest;
import com.url.shortner.repository.QRCodeRepository;
import com.url.shortner.repository.UrlRepository;
import com.url.shortner.repository.UserRepository;
import com.url.shortner.service.UrlService;
import com.url.shortner.wrapper.QRCodeResponse;
import com.url.shortner.wrapper.UrlResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final QRCodeRepository qrCodeRepository;
    @PersistenceContext
    private EntityManager entityManager;


    @Value("${app.base-url}")
    private String BASE_URL;

    @Override
    public List<UrlResponse> findAllUrl() {
        return urlRepository.findAll()
                .stream()
                .map((url) -> new UrlResponse(url, BASE_URL))
                .toList();
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


    private String shortenUrl(String filteredUrl, UrlRequest request) {
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
        return res;
    }


    @Override
    public UrlResponse createUrlForUser(String filteredUrl, UrlRequest request, String username) {
        Optional<Url> url = urlRepository.findByOriginalUrl(request.originalUrl());
        if (url.isPresent()) return new UrlResponse(url.get(), BASE_URL);
        var res = shortenUrl(filteredUrl, request);

        Url newUrl = new Url();
        newUrl.setOriginalUrl(request.originalUrl());
        newUrl.setShortenUrl(res);

        var user = findByUsername(username);
        newUrl.setUser(user);
        newUrl.setTitle(request.title());
        newUrl.setHitCount(0L);
        newUrl.setSuffix(res);

        newUrl.setExpires(LocalDate.now().plusYears(1));

        if(Objects.nonNull(request.qrCodeData())) {
            QRCode qrCode = new QRCode();
            qrCode.setUrl(newUrl);
            qrCode.setQrCode(Base64.getDecoder().decode(request.qrCodeData()));
            newUrl.setQrCode(qrCode);
        }

        Url savedUrl = urlRepository.save(newUrl);
        return new UrlResponse(savedUrl, BASE_URL);
    }


    @Override
    public UrlResponse createUrl(String filteredUrl, String originalUrl) {

        Optional<Url> url = urlRepository.findByOriginalUrl(originalUrl);
        if (url.isPresent()) return new UrlResponse(url.get(), BASE_URL);

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
        return new UrlResponse(save, BASE_URL);
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
        return new UrlResponse(urlResponse, BASE_URL);
    }


    @Override
    public Page<UrlResponse> findAllUrlByUserId(int userid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Url> urlList = urlRepository.findUrlsByUserId(userid, pageable);
        Page<UrlResponse> responsePage = urlList
                .map(url -> {
                    if (!url.getShortenUrl().isBlank()) {
                        return new UrlResponse(url, BASE_URL);
                    }
                    return null;
                });

        List<UrlResponse> filteredList = responsePage.getContent()
                .stream()
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(filteredList, pageable, filteredList.size());
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
        return new UrlResponse(updatedUrl, BASE_URL);
    }


    @Override
    public byte[] getQRCodeByUrlId(Integer urlId) {
        var url = urlRepository.findById(urlId)
                .orElseThrow(() -> new ResourceNotFound("URL not found"));
        var qrCode = url.getQrCode();
        if (qrCode == null || qrCode.getQrCode() == null)
            throw new RuntimeException("QR code not found for this URL");

        return qrCode.getQrCode();
    }

    private Url findById(int id) {
        return urlRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Url not found with id " + id));
    }

    private User findByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found with email: " + email));
    }

    private Url findByOriginalUrl(String originalUrl) {
        return urlRepository.findByOriginalUrl(originalUrl)
                .orElse(null);
    }

    private QRCode findQrCodeById(Long id) {
        return qrCodeRepository.findById(id)
                .orElse(null);
    }

    @Override
    public QRCodeRequest saveQRCode(QRCodeRequest request) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        Url url = new Url();
        url.setOriginalUrl(request.url());

        if (request.generateShortLink()) {
            UrlRequest urlRequest = UrlRequest
                    .builder()
                    .originalUrl(request.url())
                    .email(username)
                    .build();

            var filteredUrl = filterUrl(urlRequest);
            Optional<Url> optionalUrl = urlRepository.findByOriginalUrl(request.url());
            String shortenUrl = optionalUrl.map(Url::getShortenUrl)
                    .orElseGet(() -> shortenUrl(filteredUrl, urlRequest));
            url.setShortenUrl(shortenUrl);
        }

        QRCode code = new QRCode();
        code.setUrl(url);

        byte[] qrCodeBytes = Base64.getDecoder()
                .decode(request.qrCodeBase64());

        code.setQrCode(qrCodeBytes);
        url.setQrCode(code);
        url.setHitCount(0L);
        url.setTitle(request.title());
        url.setExpires(LocalDate.now().plusYears(1));

        var user  = findByUsername(username);

        url.setUser(user);
        Url saveUrl = urlRepository.save(url);

        return new QRCodeRequest(saveUrl);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<QRCodeResponse> getQrCodeList(int userid, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Url> urlList = urlRepository.findUrlsByUserId(userid, pageable);
        Page<QRCodeResponse> responsePage = urlList
                .map(url -> {
                    if (Objects.nonNull(url.getQrCode())) {
                        return new QRCodeResponse(url);
                    }
                    return null;
                });

        List<QRCodeResponse> filteredList = responsePage.getContent()
                .stream()
                .filter(Objects::nonNull)
                .toList();

        return  new PageImpl<>(filteredList, pageable, filteredList.size());

    }
}

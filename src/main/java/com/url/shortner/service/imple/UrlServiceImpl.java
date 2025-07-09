package com.url.shortner.service.imple;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.url.shortner.entity.QRCode;
import com.url.shortner.entity.Url;
import com.url.shortner.entity.User;
import com.url.shortner.exception.ResourceNotFound;
import com.url.shortner.payload.UrlRequest;
import com.url.shortner.payload.UserRequest;
import com.url.shortner.repository.QRCodeRepository;
import com.url.shortner.repository.UrlRepository;
import com.url.shortner.repository.UserRepository;
import com.url.shortner.service.UrlService;
import com.url.shortner.wrapper.UrlResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        Url savedUrl = urlRepository.save(newUrl);
        return new UrlResponse(savedUrl, BASE_URL);
    }


    @Override
    public UrlResponse createUrl(String filteredUrl, String originalUrl) {

        Optional<Url> url = urlRepository.findByOriginalUrl(originalUrl);
        if (url.isPresent()) return new UrlResponse(url.get(),BASE_URL);

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
    public List<UrlResponse> findAllUrlByUserId(int userid) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdDate").descending());
        Page<Url> urlList = urlRepository.findUrlsByUserId(userid,pageable);
        var urlListByUserId = urlList.getContent();
        return urlListByUserId.stream()
                .map((url) -> new UrlResponse(url, BASE_URL))
                .toList();
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
    @Transactional
    public byte[] generateAndSaveQRCode(String url, String title) throws URISyntaxException, WriterException {
        // Enhanced validation
        URI uri = new URI(url);
        if (uri.getScheme() == null || (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme()))) {
            throw new URISyntaxException(url, "URL must use http or https scheme");
        }
        if (uri.getHost() == null || uri.getHost().trim().isEmpty()) {
            throw new URISyntaxException(url, "URL must contain a valid host");
        }

        var urlRequest = UrlRequest.builder()
                .title(title)
                .originalUrl(url)
                .build();
        var filteredUrl = filterUrl(urlRequest);
        var res = shortenUrl(filteredUrl, urlRequest);

        // Generate QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        byte[] imageBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            imageBytes = baos.toByteArray();
            System.out.println("After ImageIO.write - imageBytes type: " + imageBytes.getClass().getName());
            System.out.println("After ImageIO.write - imageBytes length: " + imageBytes.length);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write QR code image: " + e.getMessage(), e);
        }

        System.out.println("Before QRCode assignment - imageBytes type: " + imageBytes.getClass().getName());
        System.out.println("Before QRCode assignment - imageBytes length: " + imageBytes.length);

        // Find or create URL entity
        Url urlEntity = urlRepository.findByOriginalUrl(url)
                .orElseGet(() -> {
                    Url newUrl = new Url();
                    newUrl.setOriginalUrl(url);
                    newUrl.setTitle(title);
                    newUrl.setShortenUrl(res);
                    newUrl.setCreatedDate(Instant.now());
                    newUrl.setLastModifiedDate(Instant.now());
                    return urlRepository.save(newUrl);
                });

        // Save QR code using native query
        QRCode qrCode = urlEntity.getQrCode();
        if (qrCode == null) {
            Query query = entityManager.createNativeQuery(
                    "INSERT INTO qr_code (id, created_date, qr_code_data, url_id) VALUES (nextval('qr_code_sequence'), ?, ?, ?)"
            );
            query.setParameter(1, Instant.now());
            query.setParameter(2, imageBytes);
            query.setParameter(3, urlEntity.getId());
            query.executeUpdate();
            Long newId = (Long) entityManager.createNativeQuery("SELECT currval('qr_code_sequence')").getSingleResult();
            qrCode = qrCodeRepository.findById(newId).orElseThrow(() -> new RuntimeException("Failed to retrieve new QRCode"));
            urlEntity.setQrCode(qrCode);
            urlRepository.save(urlEntity);
        } else {
            qrCode.setQrCodeData(imageBytes);
            qrCodeRepository.save(qrCode);
            urlRepository.save(urlEntity);
        }

        return imageBytes;
    }

    @Override
    public byte[] getQRCodeByUrlId(Integer urlId) {
        Url url = urlRepository.findById(urlId)
                .orElseThrow(() -> new ResourceNotFound("URL not found"));
        QRCode qrCode = url.getQrCode();
        if (qrCode == null || qrCode.getQrCodeData() == null) {
            throw new RuntimeException("QR code not found for this URL");
        }
        return qrCode.getQrCodeData();
    }

    private Url findById(int id) {
        return urlRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Url not found with id " + id));
    }

    private User findByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found with email: " + email));
    }

}

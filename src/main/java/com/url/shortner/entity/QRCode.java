package com.url.shortner.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "qr_code", schema = "public")
public class QRCode {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qr_code_seq")
    @SequenceGenerator(name = "qr_code_seq", sequenceName = "qr_code_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_date")
    private Instant createdDate;

    @Lob
    @Column(name = "qr_code_data", columnDefinition = "BYTEA")
    private byte[] qrCodeData;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false, unique = true)
    private Url url;

    // Default constructor required by Hibernate
    public QRCode() {
        this.createdDate = Instant.now();
    }

    // Constructor with required fields
    public QRCode(byte[] qrCodeData, Url url) {
        this.qrCodeData = qrCodeData;
        this.url = url;
        this.createdDate = Instant.now();
    }

    // Override equals and hashCode for entity comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QRCode qrCode = (QRCode) o;
        return id != null && id.equals(qrCode.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
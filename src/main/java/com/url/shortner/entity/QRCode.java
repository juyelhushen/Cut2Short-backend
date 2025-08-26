package com.url.shortner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "qr_code", schema = "public")
public class QRCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qr_code_seq")
    @SequenceGenerator(name = "qr_code_seq", sequenceName = "qr_code_sequence", allocationSize = 1)
    private Long id;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "qr_code", nullable = false)
    private byte[] qrCode;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "url_id", nullable = false, unique = true)
    private Url url;
}
package com.url.shortner.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "qr_code", schema = "public")
data class QRCode(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qr_code_seq")
    @SequenceGenerator(name = "qr_code_seq", sequenceName = "qr_code_sequence", allocationSize = 1)
    var id: Long? = null,

    @Lob
    @Column(name = "qr_code_data", columnDefinition = "BYTEA")
    var qrCodeData: ByteArray? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false, unique = true)
    var url: Url? = null,

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QRCode) return false
        return id == other.id
    }
    override fun hashCode(): Int = id?.hashCode() ?: 0
}

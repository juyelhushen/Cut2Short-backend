package com.url.shortner.entity

import jakarta.persistence.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.time.Instant
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicLong


@Entity
@Table(name = "url", schema = "public", indexes = [Index(name = "shortenUrlIdx", columnList = "shortenUrl")])
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "urlDetailsCache")
data class Url(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "url_seq")
    @SequenceGenerator(name = "url_seq", sequenceName = "url_sequence", allocationSize = 1)
    var id: Int = 0,

    var originalUrl: String = "",
    var shortenUrl: String = "",
    var title: String = "",
    var expires: LocalDate? = null,
    var hitCount: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null
) : Auditable() {}

package com.url.shortner.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Auditable {

    @CreatedBy
    var createdBy: String? = null

    @CreatedDate
    var createdDate: Instant? = null

    @LastModifiedBy
    var lastModifiedBy: String? = null

    @LastModifiedDate
    var lastModifiedDate: Instant? = null

}
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
    open var createdBy: String? = null

    @CreatedDate
    open var createdDate: Instant? = null

    @LastModifiedBy
    open var lastModifiedBy: String? = null

    @LastModifiedDate
    open var lastModifiedDate: Instant? = null

}
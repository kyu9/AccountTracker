package com.kyu9.accountbook.domain

import com.kyu9.accountbook.common.BaseEntity
import lombok.Builder
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@Builder
data class User(
    @Id
    val id: String,
    val password: String,
    val name: String,
): BaseEntity() {

}

package com.kyu9.accountbook.application.repository

import com.kyu9.accountbook.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository: JpaRepository<User, String> {

    fun findByName(name: String): Optional<User>
}
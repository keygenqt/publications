package com.example

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * Table.
 */
object Users : IntIdTable() {
    val idTg = long("idTg").uniqueIndex()
    val name = varchar("name", length = 255)
}

/**
 * Entity.
 */
class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var idTg by Users.idTg
    var name by Users.name
}

/**
 * Service entity user table.
 */
class UserService(private val database: Database) {
    suspend fun getAll() = transaction {
        UserEntity.all().toList()
    }

    suspend fun isHash(idTg: Long) = transaction {
        UserEntity.find { (Users.idTg eq idTg) }.count() != 0L
    }

    suspend fun insert(idTg: Long, name: String) = transaction {
        UserEntity.new {
            this.idTg = idTg
            this.name = name
        }
    }

    private suspend fun <T> transaction(block: suspend () -> T): T {
        return newSuspendedTransaction(
            context = Dispatchers.IO,
            db = database
        ) { block() }
    }
}

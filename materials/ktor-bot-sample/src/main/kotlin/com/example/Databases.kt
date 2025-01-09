package com.example

import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.dsl.module as moduleKoin

/**
 * Init database with migration.
 * @see <a href="https://jetbrains.github.io/Exposed/home.html">Exposed</a>
 * @see <a href="https://www.red-gate.com/products/flyway/">Flyway</a>
 */
fun Application.configureDatabases() {
    // Get data from config
    val url = environment.config.property("postgres.url").getString()
    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()
    // Connect to db with migrations
    val database = Database.connectWithFlyway(
        url = url,
        user = user,
        password = password,
        migration = "com/example/migration"
    ) ?: throw RuntimeException("Error connect to DB!")
    // Add services DB to DI
    loadKoinModules(moduleKoin {
        single { UserService(database) }
    })
}

/**
 * Method init
 */
private fun Database.Companion.connectWithFlyway(
    url: String,
    user: String,
    password: String,
    migration: String
): Database? {
    return try {
        val database = connect(
            url = url,
            user = user,
            password = password,
        )
        val flyway = Flyway.configure()
            .locations(migration)
            .dataSource(url, user, password)
            .load()
        flyway.info()
        flyway.migrate()
        database
    } catch (e: Exception) {
        null
    }
}

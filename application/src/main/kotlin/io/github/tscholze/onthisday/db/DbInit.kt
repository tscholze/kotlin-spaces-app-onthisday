package io.github.tscholze.onthisday.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

/**
 * Configuration of the database.
 *
 * It will create the database and needed tables by itself.
 */
fun configureDatabase() {

    Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.createDatabase("data")
        SchemaUtils.create(AppInstallation)
        SchemaUtils.create(RefreshToken)
    }
}
package io.github.tscholze.onthisday.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabase() {

    // TODO: Fix `Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException: Bedingung "APP_INSTALLATION_CLIENT_ID_UNIQUE" besteht bereits`
    Database.connect("jdbc:h2:file:./build/db", "org.h2.Driver")

    transaction {
        SchemaUtils.createMissingTablesAndColumns(AppInstallation, RefreshToken)
    }
}
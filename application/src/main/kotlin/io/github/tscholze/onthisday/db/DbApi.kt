package io.github.tscholze.onthisday.db

import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.transactions.transaction
import space.jetbrains.api.runtime.types.RefreshTokenPayload

/**
 * Saves refresh token data
 *
 * @param payload Request's payload to work on
 */
fun saveRefreshTokenData(payload: RefreshTokenPayload) = transaction {
    with(RefreshToken) {
        replace {
            it[clientId] = payload.clientId
            it[userId] = payload.userId
            it[refreshToken] = payload.refreshToken
            it[scope] = payload.scope
        }
    }
}

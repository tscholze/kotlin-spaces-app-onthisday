package io.github.tscholze.onthisday

import space.jetbrains.api.runtime.SpaceAppInstance
import space.jetbrains.api.runtime.SpaceAuth
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.ktorClientForSpace
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.types.ChannelIdentifier
import space.jetbrains.api.runtime.types.ChatMessage
import space.jetbrains.api.runtime.types.ProfileIdentifier

// MARK: - Public Properties -

/**
 * Gets the current Space's app instance.
 */
val spaceAppInstance = SpaceAppInstance(
    clientId = config.getString("space.clientId"),
    clientSecret = config.getString("space.clientSecret"),
    spaceServerUrl = config.getString("space.serverUrl"),
)

// MARK: - Private properties -

/**
 * Space Client used to call API methods in Space.
 * Note the usage of [SpaceAuth.ClientCredentials] for authorization: the application will
 * authorize in Space based on clientId+clientSecret and will act on behalf of itself (not
 * on behalf of a Space user).
 */
val spaceClient = SpaceClient(
    ktorClient = ktorClientForSpace(),
    appInstance = spaceAppInstance,
    auth = SpaceAuth.ClientCredentials()
)

// MARK: - Public helpers -

/**
 * Call API method in Space to send a message to the user.
 */
suspend fun sendMessage(userId: String, message: ChatMessage) {
    spaceClient.chats.messages.sendMessage(
        channel = ChannelIdentifier.Profile(ProfileIdentifier.Id(userId)),
        content = message
    )
}

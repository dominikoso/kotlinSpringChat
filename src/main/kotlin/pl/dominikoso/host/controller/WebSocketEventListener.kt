package pl.dominikoso.host.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import pl.dominikoso.host.model.ChatMessage

@Component
class WebSocketEventListener {

    val logger : Logger ?= LoggerFactory.getLogger(WebSocketEventListener::class.java)!!

    @Autowired
    lateinit var messagingTemplate: SimpMessagingTemplate

    @EventListener
    fun handleWebSocketConnectListener(event : SessionConnectedEvent){
        logger ?.info("Received a new web socket connection")
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event : SessionDisconnectEvent){
        var headerAccessor : StompHeaderAccessor = StompHeaderAccessor.wrap(event.message)

        var username : String ?= headerAccessor.sessionAttributes?.get("username") as String
        if (username != null){
            logger?.info("User Disconnected : " + username)

            var chatMessage : ChatMessage = ChatMessage(ChatMessage.MessageType.LEAVE, username, "")

            messagingTemplate.convertAndSend("/topic/public", chatMessage)
        }
    }
}
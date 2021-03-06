package pl.dominikoso.host.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller
import pl.dominikoso.host.model.ChatMessage
import pl.dominikoso.host.tools.ChatCommandHandler
import pl.dominikoso.host.tools.UserSecurityHandler

@Controller
class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    fun sendMessage(@Payload chatMessage: ChatMessage) : ChatMessage {
        return chatMessage
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    fun addUser(@Payload chatMessage: ChatMessage, headerAccessor: SimpMessageHeaderAccessor) : ChatMessage {
            headerAccessor.sessionAttributes?.put("username", chatMessage.sender)
            UserSecurityHandler.addUser(chatMessage.sender!!)
            return chatMessage
    }

    @MessageMapping("/chat.processCommand")
    @SendTo("/topic/public")
    fun commandProcessor(@Payload chatMessage: ChatMessage, headerAccessor: SimpMessageHeaderAccessor) :ChatMessage {
        return ChatCommandHandler.processCommand(chatMessage, headerAccessor)
    }

}
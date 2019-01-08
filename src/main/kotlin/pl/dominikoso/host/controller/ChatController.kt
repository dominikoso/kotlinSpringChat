package pl.dominikoso.host.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller
import pl.dominikoso.host.model.ChatMessage

@Controller
class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    fun sendMessage(@Payload chatMessage: ChatMessage) : ChatMessage {
        System.out.println("${chatMessage.sender}: ${chatMessage.content}")
        return chatMessage
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    fun addUser(@Payload chatMessage: ChatMessage, headerAccessor: SimpMessageHeaderAccessor) : ChatMessage {
        headerAccessor.sessionAttributes ?.put("username", chatMessage.sender)
        return chatMessage
    }

    @MessageMapping("/chat.changeUser")
    @SendTo("/topic/public")
    fun changeUser(@Payload chatMessage: ChatMessage, headerAccessor: SimpMessageHeaderAccessor) : ChatMessage {
        headerAccessor.sessionAttributes ?.remove("username", chatMessage.sender)
        var spaceIdx : Int = chatMessage.content ?.indexOf(" ")!!
        val newUser : String = chatMessage.content.substring(spaceIdx+1)
        headerAccessor.sessionAttributes ?.put("username", newUser)
        return ChatMessage(ChatMessage.MessageType.CHANGED, newUser, "")
    }

    @MessageMapping("/chat.showHelp")
    @SendTo("/topic/public")
    fun showHelp() : ChatMessage {
        return ChatMessage(ChatMessage.MessageType.CHAT, "SYSTEM", "List of available commands: \n /setnick {nickname} - changes your nickname \n /help - shows this list")
    }

}
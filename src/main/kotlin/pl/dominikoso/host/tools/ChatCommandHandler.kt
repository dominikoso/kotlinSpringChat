package pl.dominikoso.host.tools

import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import pl.dominikoso.host.model.ChatMessage
import java.util.*

class ChatCommandHandler {
    companion object {

        private var commands : List<String> = Arrays.asList("/setnick", "/help")

        private fun recognizeCommand(chatMessage: ChatMessage): String {

            return if (commands.contains(chatMessage.content?.substringBefore(" "))) {
                chatMessage.content?.substringBefore(" ") as String
            } else {
                "Command not found"
            }
        }

        fun processCommand(chatMessage: ChatMessage, headerAccessor: SimpMessageHeaderAccessor): ChatMessage {

            val command = recognizeCommand(chatMessage)
            return when (command) {
                "/setnick" -> {
                    val spaceIdx: Int = chatMessage.content?.indexOf(" ")!!
                    val newUser: String = chatMessage.content.substring(spaceIdx + 1)
                    if (newUser.length > 20) {
                        ChatMessage(ChatMessage.MessageType.SYSTEM,
                                "SYSTEM",
                                "Maximum nick length is 20 chars")
                    }else {
                        headerAccessor.sessionAttributes?.remove("username", chatMessage.sender)
                        headerAccessor.sessionAttributes?.put("username", newUser)
                        ChatMessage(ChatMessage.MessageType.CHANGED, newUser, "")
                    }
                }
                "/help" -> {
                    ChatMessage(ChatMessage.MessageType.SYSTEM,
                            "SYSTEM",
                            "List of available commands: \n " +
                                    "/setnick {nickname} - changes your nickname \n" +
                                    " /help - shows this list")
                }
                else -> {
                    ChatMessage(ChatMessage.MessageType.SYSTEM, "SYSTEM", "Requested command not found")
                }
            }

        }
    }

}
package pl.dominikoso.host.tools

import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import pl.dominikoso.host.model.ChatMessage
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader
import java.net.URLConnection
import java.util.*
import javax.imageio.ImageIO

class ChatCommandHandler {
    companion object {

        private var commands : List<String> = Arrays.asList("/setnick", "/help", "/img")

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
                    val oldUser: String = chatMessage.sender as String
                    val spaceIdx: Int = chatMessage.content?.indexOf(" ")!!
                    val newUser: String = chatMessage.content.substring(spaceIdx + 1)
                    if (newUser.length > 20 || newUser.length < 2 || spaceIdx == -1) {
                        ChatMessage(ChatMessage.MessageType.SYSTEM,
                                chatMessage.sender,
                                "Nick length must me min 2 chars and maximum 20 chars")
                    }else {
                        return if (UserSecurityHandler.findUser(newUser)){
                            ChatMessage(ChatMessage.MessageType.SYSTEM,
                                    chatMessage.sender,
                                    "This nickname is already taken")
                        }else {
                            headerAccessor.sessionAttributes?.remove("username", chatMessage.sender)
                            UserSecurityHandler.removeUser(chatMessage.sender)
                            headerAccessor.sessionAttributes?.put("username", newUser)
                            UserSecurityHandler.addUser(newUser)
                            ChatMessage(ChatMessage.MessageType.CHANGED, newUser, oldUser)
                        }
                    }
                }
                "/help" -> {
                    ChatMessage(ChatMessage.MessageType.SYSTEM,
                            chatMessage.sender,
                            "List of available commands: \n " +
                                    "/setnick {nickname} - changes your nickname \n " +
                                    "/img {link} - send image to chat \n " +
                                    "/help - shows this list")
                }
                "/img" -> {
                    val spaceIdx: Int = chatMessage.content?.indexOf(" ")!!
                    val imgLink : String = chatMessage.content.substring(spaceIdx+1)
                    if (spaceIdx == -1){
                        ChatMessage(ChatMessage.MessageType.SYSTEM,
                                chatMessage.sender,
                                "Image link cannot be empty")
                    }
                    try {
                        val url = URL(imgLink)
                        val image : BufferedImage ?= ImageIO.read(url)
                        return if (image != null){
                            ChatMessage(ChatMessage.MessageType.IMAGE, chatMessage.sender, imgLink)
                        }else{
                            ChatMessage(ChatMessage.MessageType.SYSTEM,
                                    chatMessage.sender,
                                    "Requested link is not an image")
                        }
                    }catch (e : MalformedURLException){
                        return ChatMessage(ChatMessage.MessageType.SYSTEM,
                                chatMessage.sender,
                                "Invalid image link")
                    }catch (e : IOException){
                        return ChatMessage(ChatMessage.MessageType.SYSTEM,
                                chatMessage.sender,
                                "Cannot access image link")
                    }
                }
                else -> {
                    ChatMessage(ChatMessage.MessageType.SYSTEM, chatMessage.sender, "Requested command not found")
                }
            }

        }
    }

}
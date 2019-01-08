package pl.dominikoso.host.model

import javax.validation.constraints.Size

data class ChatMessage (val type : MessageType,
                        @Size(min=2,max=20, message = "Length of username must be greater than or equal 2 and less than or equal 20")
                        val sender : String?,
                        @Size(max = 300, message = "Maximum length of text message is 300 characters")
                        val content : String?){
    enum class MessageType {CHAT, JOIN, LEAVE}
}
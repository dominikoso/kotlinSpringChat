package pl.dominikoso.host.tools

import pl.dominikoso.host.model.User


class UserSecurityHandler {
    companion object {

        private var users = mutableListOf<User>()

        fun addUser(nickname: String) {
            val newUser = User(nickname)
            users.add(newUser)
        }

        fun removeUser(nickname: String) {
            val removedUser = User(nickname)
            users.remove(removedUser)
        }

        fun findUser(nickname: String): Boolean {
            val searchUser = User(nickname)
            return users.contains(searchUser)
        }
    }
}
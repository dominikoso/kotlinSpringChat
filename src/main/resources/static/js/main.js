'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
        if (messageContent && stompClient) {
            var chatMessage = {
                sender: username,
                content: messageInput.value,
                type: 'CHAT'
            };

            // if (messageContent.startsWith("/setnick")) {
            //     stompClient.send("/app/chat.changeUser", {}, JSON.stringify(chatMessage));
            //     messageInput.value = '';
            // }else if (messageContent.startsWith("/help")){
            //     stompClient.send("/app/chat.showHelp");
            //     messageInput.value = '';
            if (messageContent.startsWith("/")) {
                stompClient.send("/app/chat.processCommand", {}, JSON.stringify(chatMessage));
                messageInput.value = '';
            }else {
                stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
                messageInput.value = '';
            }
        }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else if (message.type === 'CHANGED') {
        messageElement.classList.add('event-message');
        if (username === message.content) {
            username = message.sender;
        }
        message.content = message.content + ' is now known as ' + message.sender + '!';
    }else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText;
        if (message.type === 'SYSTEM') {
            usernameText = document.createTextNode(message.sender +' '+ String.fromCodePoint(0x1F916))
        }else {
            usernameText = document.createTextNode(message.sender);
        }
        //console.log(usernameText);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    if (message.type === 'IMAGE'){
        var imageLinkElement = document.createElement('a');
        imageLinkElement.href = message.content;
        var imgElement = document.createElement('img');
        imgElement.src = message.content;
        textElement.appendChild(imgElement);
        imageLinkElement.appendChild(textElement);
        messageElement.appendChild(imageLinkElement);
    }else if (linkify(message.content) !== -1){
        textElement.appendChild(messageText);
        var linkElement = document.createElement('a');
        linkElement.href = message.content;
        linkElement.appendChild(textElement);
        messageElement.appendChild(linkElement);
    }else {
        textElement.appendChild(messageText);
        messageElement.appendChild(textElement);
    }

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function linkify(text) {
    var urlRegex =/(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
    return text.search(urlRegex);
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
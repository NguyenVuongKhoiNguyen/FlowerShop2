package com.poly.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.poly.models.entities.ChatMessage;
import com.poly.models.services.AccountService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
	
	private final AccountService aService;
	
    @MessageMapping("/chat")          // client sends to /app/chat
    @SendTo("/topic/messages")        // broadcast to subscribers
    public ChatMessage send(ChatMessage message) {
    	if (message.getSender() != null && !message.getSender().equals("Guess")) {
    		String messengerPhoto = aService.findById(message.getSender()).getPhoto();
    		message.setPhoto(messengerPhoto);
		}
        return message;
    }
}

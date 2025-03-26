package com.example.websocket_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author Jerry
 * @create 2025/3/20下午 05:30
 * @desc：
 */
@Service
public class WebSocketSessionManager {
    private final ArrayList<String> activeUsername =new ArrayList<>();//確保不會出現一樣的user出現在此陣列中
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketSessionManager(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public void addUsername(String username){
        activeUsername.add(username);
    }

    public void removeUsername(String username){
        activeUsername.remove(username);
    }

    public void broadcastActiveUsername(){
        messagingTemplate.convertAndSend("/topic/user",activeUsername);
        System.out.println("Broadcasting active users to /topic /users"+activeUsername);
    }
}

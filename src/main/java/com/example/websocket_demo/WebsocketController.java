package com.example.websocket_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * @author Jerry
 * @create 2025/3/20下午 01:19
 * @desc：
 */
@Controller
public class WebsocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    @Autowired //Spring boot幫忙做的自動連線
    public WebsocketController(SimpMessagingTemplate messagingTemplate,WebSocketSessionManager sessionManager){
        this.sessionManager=sessionManager;
        this.messagingTemplate=messagingTemplate;
    }
    @MessageMapping("/message") //將訊息傳到伺服器(終端機)，然後再轉傳到每個使用者
        public void handleMessage(Message message){
        System.out.println("Received message from user: "+message.getUser()+":"+message.getMessage());
        messagingTemplate.convertAndSend("/topic/messages",message);
        System.out.println("Send message to /topic/messages :"+message.getUser()+":"+message.getMessage());
    }

    @MessageMapping("/connect") //加入對話
    public void connectUser(String username){
        sessionManager.addUsername(username);
        sessionManager.broadcastActiveUsername();
        System.out.println(username+"Connected !");
    }

    @MessageMapping("/disconnect")  //離開對話
    public void disconnectUser(String username){
        sessionManager.removeUsername(username);
        sessionManager.broadcastActiveUsername();
        System.out.println(username+"Discannection !");
    }

    @MessageMapping("/request-users")
    public void requestUsers(){
        sessionManager.broadcastActiveUsername();
        System.out.println("Requesting Users:");
    }

}

package com.example.websocket_demo.client;

import com.example.websocket_demo.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Jerry
 * @create 2025/3/20下午 01:34
 * @desc：
     * 初始化 WebSocket 連線
     * 使用 STOMP 協議來發送和接收訊息
     * 支援 JSON 訊息格式
     * 支援 SockJS，確保在不支援 WebSocket 的情況下仍可通訊
 */

public class MyStompClient {
    private StompSession session;//WebSocket STOMP的會話，接收與傳遞STOMP訊息
    private String username;

    public MyStompClient(MessageListener messageListener,String username) throws ExecutionException, InterruptedException {
        this.username=username;

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));//StandardWebSocketClient負責與WebSocket伺服器建立連線

        SockJsClient sockJsClient = new SockJsClient(transports);//使WebSocket兼容不同的網路環境
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());//轉換成json格式

        StompSessionHandler sessionHandler = new MyStompSessionHandler(messageListener,username);
        String url = "https://chat-app-0yb2.onrender.com/ws";//給websocket

        session = stompClient.connectAsync(url,sessionHandler).get();
    }

    public void sendMessage(Message message){
        try{
            session.send("/app/message",message);
            System.out.println("Message Sent: "+message.getMessage());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disconnectUser(String username){
        session.send("/app/disconnect",username);
        System.out.println("Disconnect User:"+username);
    }
}

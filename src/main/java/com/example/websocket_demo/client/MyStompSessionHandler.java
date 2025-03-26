package com.example.websocket_demo.client;


import com.example.websocket_demo.Message;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author Jerry
 * @create 2025/3/20下午 02:10
 * @desc：
 */
public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    private String username;
    private MessageListener messageListener;

    public MyStompSessionHandler(MessageListener messageListener, String username) {
        this.username = username;
        this.messageListener=messageListener;
    }

    @Override

    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Client Connected");
        //在這裡訂閱了這個路徑下的message
        session.subscribe("/topic/messages", new StompFrameHandler() {//客戶端訂閱
            @Override
            public Type getPayloadType(StompHeaders headers) {//獲得payload物件
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try{
                    if(payload instanceof Message){//將json檔案轉換成Message類
                        Message message =(Message)payload;
                        messageListener.onMessageRecieve(message);
                        System.out.println("Received message:"+message.getUser()+":"+message.getMessage());
                    }else{
                        System.out.println("Received unexpected payload type:"+payload.getClass());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        System.out.println("Client Subscribe to/topic/messages");


        session.subscribe("/topic/user", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return new ArrayList<String>().getClass();
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try{
                    if(payload instanceof ArrayList){
                        ArrayList<String> activeUsers = (ArrayList<String>)payload;
                        messageListener.onActiveUserUpdated(activeUsers);
                        System.out.println("Received active user:"+activeUsers);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        System.out.println("Subscribed to /topic/users");

        session.send("/app/connect",username);
        session.send("/app/request-users","");

    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        super.handleTransportError(session, exception);
    }

}

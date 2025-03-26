package com.example.websocket_demo;

/**
 * @author Jerry
 * @create 2025/3/20下午 01:29
 * @desc：
 */
public class Message {
    private String user;
    private String message;

    public Message() {
    }

    public Message(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getUser() {return user;}

    public String getMessage() {return message;}


}

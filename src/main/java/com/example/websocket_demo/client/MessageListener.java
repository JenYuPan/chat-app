package com.example.websocket_demo.client;

import com.example.websocket_demo.Message;

import java.util.ArrayList;

/**
 * @author Jerry
 * @create 2025/3/21下午 02:23
 * @desc：
 */
public interface MessageListener {
    void onMessageRecieve(Message message);
    void onActiveUserUpdated(ArrayList<String> users);
}

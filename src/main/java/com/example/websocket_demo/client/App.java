package com.example.websocket_demo.client;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Jerry
 * @create 2025/3/21上午 09:32
 * @desc：
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //透過對話視窗取得使用者姓名
                String username = JOptionPane.showInputDialog(null,
                        "Enter Yout User Name(Max 16 Characters):"
                        , "對話視窗"
                        , JOptionPane.QUESTION_MESSAGE);
                //判斷使用者的姓名是否合法
                if (username == null || username.length() > 16 || username.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "您輸入的資訊是不合法的。請重新輸入。",
                            "錯誤訊息!",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //加入使用者姓名，並且進入對話框
                ClientGUI clientGUI = null;
                try {
                    clientGUI = new ClientGUI(username);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                clientGUI.setVisible(true);
            }
        });
    }
}

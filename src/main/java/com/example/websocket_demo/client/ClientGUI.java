package com.example.websocket_demo.client;

import com.example.websocket_demo.Message;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * @author Jerry
 * @create 2025/3/20下午 04:01
 * @desc：
 */
public class ClientGUI extends JFrame implements MessageListener{
    private JPanel connectedUsersPanel,messagePanel;
    private MyStompClient myStompClient;
    private String username;
    //對話視窗的Bar
    private JScrollPane messagePanelScrollPane;
    //對話聊天室的bar
    private JScrollPane  userPanelScrollPane;
    public ClientGUI(String username) throws ExecutionException, InterruptedException {
        super("User:"+username);
        this.username = username;
        myStompClient = new MyStompClient(this,username);


        setSize(1218,685);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(ClientGUI.this,"你確定要離開嗎?","離開確認",JOptionPane.YES_NO_OPTION);

                if (option==JOptionPane.YES_OPTION){
                    myStompClient.disconnectUser(username);
                    ClientGUI.this.dispose();

            }

            }
        });

        //視窗監視器
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateMessageSize();
            }
        });


        getContentPane().setBackground(Utilities.PRIMARY_COLOR);//透過工具類設定背景顏色
        addGuiComponents();
    }
    private void addGuiComponents(){
        addConnectedUserComponents();
        addChatComponents();
    }

    private void addConnectedUserComponents(){
        connectedUsersPanel = new JPanel();
        connectedUsersPanel.setBorder(Utilities.addPadding(10,10,10,10));


        connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel,BoxLayout.Y_AXIS));//獲得上限的人員的Username並且以垂直方向排列
        connectedUsersPanel.setBackground(Utilities.SECONDARY_COLOR);//獲得使用者清單的底色
        connectedUsersPanel.setPreferredSize(new Dimension(200,getHeight()));//獲得使用者名單寬和高(高與聊天視窗同高)

        //加入聊天室成員的bar
        userPanelScrollPane = new JScrollPane(connectedUsersPanel);
        userPanelScrollPane.setBackground(Utilities.SECONDARY_COLOR);
        userPanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        userPanelScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        userPanelScrollPane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                revalidate();
                repaint();
            }
        });

        JLabel connectedUsersLabel =new JLabel("Connected Users");
        connectedUsersLabel.setFont(new Font("Inter",Font.BOLD,18));
        connectedUsersLabel.setForeground(Utilities.TEXT_COLOR);
        connectedUsersPanel.add(connectedUsersLabel);

        add(userPanelScrollPane,BorderLayout.WEST);

    }
    private void addChatComponents(){
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel,BoxLayout.Y_AXIS));
        messagePanel.setBackground(Utilities.TRANSPARENT_COLOR);

        //加入滾動的bar //聊天室成員的部分還沒做(完成)
        messagePanelScrollPane = new JScrollPane(messagePanel);
        messagePanelScrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        messagePanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messagePanelScrollPane.getVerticalScrollBar().setUnitIncrement(12);
        messagePanelScrollPane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                revalidate();
                repaint();
            }
        });

        chatPanel.add(messagePanelScrollPane,BorderLayout.CENTER);

//        //測試訊息，
//        JLabel message = new JLabel("測試訊息");
//        message.setFont(new Font("Inter",Font.BOLD,18));
//        message.setForeground(Utilities.TEXT_COLOR);
//        messagePanel.add(message);

//        測試訊息
//        messagePanel.add(createChatMessageComponent(new Message("Jerry","訊息測試!")));

        //輸入聊天訊息的框框
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(Utilities.addPadding(10,10,10,10));
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        //輸入框，User輸入訊息的地方
        JTextField inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    String input = inputField.getText();

                    //防止沒有輸入任何東西按下Enter就送出空訊息。
                    if(input.isEmpty())return;

                    inputField.setText("");

//                    messagePanel.add(createChatMessageComponent(new Message("Jerry",input)));
//                    //強制畫面更新
//                    repaint();
//                    revalidate();


                    myStompClient.sendMessage(new Message(username,input));

                }
            }
        });
        inputField.setBackground(Utilities.SECONDARY_COLOR);
        inputField.setForeground(Utilities.TEXT_COLOR);
        inputField.setBorder(Utilities.addPadding(0,10,0,10));
        inputField.setFont(new Font("Inter",Font.PLAIN,16));
        inputField.setPreferredSize(new Dimension(inputPanel.getWidth(),50));//設定文字輸入框的高度
        inputPanel.add(inputField,BorderLayout.CENTER);
        chatPanel.add(inputPanel,BorderLayout.SOUTH);

        add(chatPanel,BorderLayout.CENTER);

    }

    //建立一個傳出訊息的方法，可以回傳誰傳出訊息，以及訊息的內容。
    private JPanel createChatMessageComponent(Message message){
        //聊天的畫面中每個人傳出的訊息的訊息的那個標籤元件
        JPanel chatMessage = new JPanel();
        chatMessage.setBackground(Utilities.TRANSPARENT_COLOR);
        chatMessage.setLayout(new BoxLayout(chatMessage,BoxLayout.Y_AXIS));//設定每一個在聊天格子中的顯示格式
        chatMessage.setBorder(Utilities.addPadding(10,10,10,10));

        JLabel usernameLable = new JLabel(message.getUser());
        usernameLable.setFont(new Font("Inter",Font.BOLD,18));//設定Username這一航的自型
        usernameLable.setForeground(Utilities.TEXT_COLOR);//這裡在設定使用者送出的文字得顏色
        chatMessage.add(usernameLable);

        JLabel messageLable= new JLabel();
        messageLable.setText(
                "<html>"+
                    "<body style='width:"+(0.70*getWidth())+"'px>"+
                        message.getMessage() +
                    "</body>"+
                "</html>");
        messageLable.setFont(new Font("Inter",Font.PLAIN,18));
        messageLable.setForeground(Utilities.TEXT_COLOR);
        chatMessage.add(messageLable);

        return chatMessage;

    }

    @Override
    public void onMessageRecieve(Message message) {
        messagePanel.add(createChatMessageComponent(message));
        //強制更新websocket的東西
        revalidate();
        repaint();
        //設定Bar永遠自動更新到最底層
        messagePanelScrollPane.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
    }

    @Override
    public void onActiveUserUpdated(ArrayList<String> users) {

        if(connectedUsersPanel.getComponents().length>=2){
            connectedUsersPanel.remove(1);
        }

        JPanel userListPanel = new JPanel();
        userListPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        userListPanel.setLayout(new BoxLayout(userListPanel,BoxLayout.Y_AXIS));

        for(String user : users){
            JLabel username = new JLabel();
            username.setText(user);
            username.setForeground(Utilities.TEXT_COLOR);
            username.setFont(new Font("Inter",Font.BOLD,16));
            userListPanel.add(username);
        }

        connectedUsersPanel.add(userListPanel);
        revalidate();
        repaint();

    }

    private void updateMessageSize(){
        for(int i = 0;i<messagePanel.getComponents().length;i++){
            Component component = messagePanel.getComponent(i);
            if(component instanceof JPanel){
                JPanel chatMessage = (JPanel) component;
                if(chatMessage.getComponent(1) instanceof JLabel){
                    JLabel messageLable = (JLabel) chatMessage.getComponent(1);
                    messageLable.setText(
                            "<html>"+
                                    "<body style='width:"+(0.70*getWidth())+"'px>"+
                                        messageLable.getText() +
                                    "</body>"+
                            "</html>");
                }
            }
        }
    }
}



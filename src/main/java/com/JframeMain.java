package com;

import com.google.gson.Gson;
import com.leo.common.ServerResponse;
import com.leo.model.LeoMessage;
import com.leo.service.impl.MyWebSocket;
import com.leo.util.DateUtil;
import com.leo.util.ExecutorPool;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import javax.swing.*;

/**
 * @author lil
 * @description: TODO
 * @date 2019/5/30 0030上午 8:09
 */
@Component
public class JframeMain extends JFrame implements ApplicationListener<ContextRefreshedEvent> {

    static boolean started = false;
    static JLabel userLabel = new JLabel("正在启动中...");
    static JButton loginButton = new JButton("打开精灵网站");
    static JButton jb = new JButton("退出精灵平台");
    static JPanel panel = new JPanel();

    public void launchFrame() {

        this.setTitle("精灵平台");
        this.setVisible(true);
        this.setSize(400, 400);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        //显示位置是屏幕的宽度-JFrame宽度的一半，高度类似。
        this.setLocation((screenSize.width - 400)/2, (screenSize.height - 400)/2);

        //单击右上角容器的行为
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // 添加面板
        this.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);
    }

    private void placeComponents(JPanel panel) {
        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

//        // 创建 JLabel

        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        userLabel.setBounds(10,20,180,25);
        panel.add(userLabel);
//
//        /*
//         * 创建文本域用于用户输入
//         */
//        JTextField userText = new JTextField(20);
//        userText.setBounds(100,20,165,25);
//        panel.add(userText);
//
//        // 输入密码的文本域
//        JLabel passwordLabel = new JLabel("Password:");
//        passwordLabel.setBounds(10,50,80,25);
//        panel.add(passwordLabel);
//
//        /*
//         *这个类似用于输入的文本域
//         * 但是输入的信息会以点号代替，用于包含密码的安全性
//         */
//        JPasswordField passwordText = new JPasswordField(20);
//        passwordText.setBounds(100,50,165,25);
//        panel.add(passwordText);

        // 创建登录按钮

        loginButton.setBounds(10, 80, 280, 35);
        loginButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try {
                    if(started){
                        Runtime.getRuntime().exec(
                                "cmd   /c   start   http://localhost:8085/build/index.html");
                    }else {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "请稍候！正在启动中。。。", "提示",JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });


        jb.setBounds(10, 180, 280, 35);
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);//退出
            }
        });

        panel.add(jb);
        panel.add(loginButton);

        ExecutorPool.executeOnCachedPool(() ->{
            int i = 1;
            while (!started){
                try {
                    if(i<98){
                        i++;
                    }
                    userLabel.setText("正在启动中... (已加载"+i+"%)");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void main(String[] args) {
        JframeMain f = new JframeMain();
        f.launchFrame();
    }

//    public static void started(){
//        started = true;
//        userLabel.setText("平台启动完成！");
//    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        started = true;
        userLabel.setText("启动完成");

    }
}

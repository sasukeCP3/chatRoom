package com.zsy.homework.chatroom;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 10000);
        System.out.println("成功连接服务器");

        while (true){
            System.out.println("~~~~~~~~~~~~~~~~~~聊天室~~~~~~~~~~~~~~~~~~~");
            System.out.println("1 登录");
            System.out.println("2 注册");
            System.out.println("输入1  或  2");
            Scanner sc = new Scanner(System.in);
            String choose = sc.nextLine();
            switch (choose) {
                case "1" ->
                        login(socket);
                case "2" ->
                    System.out.println("用户选择注册");
                default ->
                    System.out.println("无此选项");
            }
        }
    }
    public static void login(Socket socket) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        //user password
        Scanner sc = new Scanner(System.in);
        System.out.println("输入用户名");
        String username = sc.nextLine();
        System.out.println("输入密码");
        String password = sc.nextLine();

//        拼接数据
        StringBuilder sb = new StringBuilder();
        sb.append("username=").append(username).append("&password=").append(password);
//
        bwProcess(bw, "login");
        bwProcess(bw,sb.toString());

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String msg = br.readLine();
        if("1".equals(msg)){
            System.out.println("登录成功");
//            Thread 聊天记录
            new Thread(new ClientMyRunnable(socket)).start();
            talk2All(bw);
        } else if("2".equals(msg)) {
            System.out.println("密码错误");
        }else if("3".equals(msg)) {
            System.out.println("用户名不存在");
        }
    }

    private static void talk2All(BufferedWriter bw) throws IOException {
        Scanner sc = new Scanner(System.in);
        while (true){
            System.out.println("输入文字");
            String msg = sc.nextLine();
            bwProcess(bw,msg);
        }
    }

    public static void bwProcess(BufferedWriter bw, String msg) throws IOException {
        bw.write(msg);
        bw.newLine();
        bw.flush();
    }


}

class ClientMyRunnable implements Runnable{
    Socket socket;
    public ClientMyRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg = br.readLine();
                System.out.println(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
package com.zsy.homework.chatroom;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

public class Server {
    static ArrayList<Socket> list = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(10000);

//        1
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream("src/main/resources/userinfo.txt");
        prop.load(fis);
        fis.close();

//        2
        while (true){
            Socket socket = ss.accept();
            System.out.println("客户端连接");
            new Thread(new MyRunnable(socket, prop)).start();
        }

    }
}

class MyRunnable implements Runnable{
    Socket socket;
    Properties prop;

    public MyRunnable(Socket socket, Properties prop) {
        this.socket = socket;
        this.prop = prop;
    }

    @Override
    public void run(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String choose = br.readLine();
                switch (choose){
                    case "login"-> login(br);
                    case "register"-> System.out.println("用户注册");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void login(BufferedReader br) throws IOException {
        System.out.println("用户登录");
        String userinfo = br.readLine();
        String[] userinfoArr = userinfo.split("&");
        String usernameInput = userinfoArr[0].split("=")[1];
        String passwordInput = userinfoArr[1].split("=")[1];

        if(prop.containsKey(usernameInput)){
            String rightpassword = prop.get(usernameInput) + "";
            if(rightpassword.equals(passwordInput)){
                writeMsg2Client("1");
                Server.list.add(socket);
//              接受消息并打印
                talk2All(br, usernameInput);
            }else {
                writeMsg2Client("2");
            }
        }else {
            writeMsg2Client("3");
        }

    }

    public void writeMsg2Client(String msg) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bwProcess(bw,msg);
    }

    public void writeMsg2Client(Socket s, String msg) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        bwProcess(bw,msg);
    }

    public void talk2All(BufferedReader br, String usernameInput) throws IOException {
        while (true){
            String msg = br.readLine();
            System.out.println(usernameInput + "发送了" + msg);
            for (Socket s : Server.list) {
                writeMsg2Client(s, usernameInput + "发送了" + msg);
            }
        }
    }
    public static void bwProcess(BufferedWriter bw, String msg) throws IOException {
        bw.write(msg);
        bw.newLine();
        bw.flush();
    }

}
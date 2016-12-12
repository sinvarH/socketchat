package meizhuo.sinvar.olchat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by sinvar on 16-12-12.
 */

public class Client {
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public Client(String ip,int port) {

        try {
            socket = new Socket(ip, port);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        new Thread(){
            public void run(){
                try {
                    BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    String str = null;
                    while((str= line.readLine()) != null){
                        out.println(str);
                    }
                    line.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    out.close();
                }
            }
        }.start();

        new Thread(){
            public void run(){
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = null;
                    while((str = in.readLine()) != null){
                        System.out.println("receive from server:" + str);
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

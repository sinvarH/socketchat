package meizhuo.sinvar.olchat.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sinvar on 16-12-11.
 * 这个类是服务器端的等待客户端发送信息
 */
public class Server{
    private ServerSocket ss;
    private Socket socket;
    private List<Socket> list = new ArrayList<Socket>();
    PrintWriter out;

    private Activity activity;
    private TextView showView;
    private int port;

    public Server(int port, final Activity activity, final TextView showView, Button send, final EditText sendContent) {
        this.port = port;
        this.activity = activity;
        this.showView = showView;
//        控制台测试代码
//        new Thread(){
//            public void run(){
//                BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
//                try {
//
//                    String s;
//                    while((s= buf.readLine()) != null){
//                        broadcast(list,s);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sendString = sendContent.getText().toString().trim();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showView.append("from me:"+sendString+"\n");
                    }
                });
                Log.e("test","send");
                Server.this.broadcast(sendString);
            }
        });

        try {
            Log.e("test","into");
            ss = new ServerSocket(port);
            while (true) {
                //The method blocks until a connection is made.
                socket = ss.accept();
                list.add(socket);
                updateUI(activity,showView,"在线人数"+list.size());
//                System.out.println("在线人数：" + list.size());
                new Thread(new ChatThread(socket, list)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //广播给所有的socket链接
    private void broadcast(List<Socket> socketList, String msg){
        for(Socket s : socketList){
            PrintWriter pw = this.getSocketPrintWriter(s);
            pw.println(msg);
        }
    }

    public void broadcast(String msg){
        broadcast(list,msg);
    }

    private PrintWriter getSocketPrintWriter(Socket socket){
        OutputStream os=null;
        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStreamWriter osw=new OutputStreamWriter(os);
        BufferedWriter bw=new BufferedWriter(osw);
        return new PrintWriter(bw, true);
    }

    class ChatThread implements Runnable{
        private Socket socket;
        private List<Socket> socketList;
        private BufferedReader bufferedReader;

        public ChatThread(Socket socket, List<Socket> socketList) {
            super();
            this.socket = socket;
            this.socketList = socketList;
        }
        private void  preRun(){
            InputStreamReader r=null;
            try {
                r = new InputStreamReader(this.socket.getInputStream());
                this.bufferedReader=new BufferedReader(r);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            String msg="";
            this.preRun();
            while(true){
                try {
                    msg="from client: "+this.bufferedReader.readLine();
                    updateUI(activity,showView,msg);
                    //System.out.println(msg);
                    broadcast(this.socketList,msg);
                } catch (IOException e) {
                    try {
                        this.bufferedReader.close();
                        this.socket.close();
                        this.socketList.remove(this.socket);
                        broadcast(this.socketList,"somebody exist....people size :" +socketList.size());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                    return ;
                }
            }
        }

    }

    private void updateUI(Activity activity , final TextView textView, final String msg){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(msg+"\n");
            }
        });
    }
}

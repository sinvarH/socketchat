package meizhuo.sinvar.olchat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import meizhuo.sinvar.olchat.R;
import meizhuo.sinvar.olchat.utils.Server;

/**
 * Created by sinvar on 16-12-12.
 */

public class ChatActivity extends Activity {
    private Button send;
    private EditText sendContent;
    private Integer port ;
    private String ip;
    private TextView content;
    private String sign;
    //client用到的变量
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        send = (Button) findViewById(R.id.send);
        content = (TextView)findViewById(R.id.content);
        sendContent = (EditText) findViewById(R.id.send_content);
        Intent intent = getIntent();
        port = intent.getIntExtra("port",0);
        ip = intent.getStringExtra("ip");
        sign = intent.getStringExtra("from");
        if (sign.equals("create")){
            initServer();
        }else if (sign.equals("join")){
            initClient();
        }
    }

    private void initServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("test","open");
                //这里有线程问题，暂时使用构造方法解决
                new Server(port,ChatActivity.this,content,send,sendContent);
            }
        }).start();
    }

    private void initClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    getInput();
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getOutput();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void getInput(){
        new Thread(){
            public void run(){
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = null;
                    while((str = in.readLine()) != null){
                        final String finalStr = str;
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                content.append("消息："+ finalStr +"\n");
                            }
                        });
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getOutput(){
        try {
            final String sendString = sendContent.getText().toString().trim();
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendContent.setText("");
                }
            });
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(sendString);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

package meizhuo.sinvar.olchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import meizhuo.sinvar.olchat.R;
import meizhuo.sinvar.olchat.utils.Util;

public class MainActivity extends AppCompatActivity {
    private Button join;
    private Button create ;
    private TextView myIP;
    private EditText ip;
    private EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        join = (Button) findViewById(R.id.join);
        create = (Button) findViewById(R.id.create);
        myIP = (TextView)findViewById(R.id.my_ip_address);
        ip = (EditText)findViewById(R.id.ip_address);
        port = (EditText)findViewById(R.id.port);

        String myIPStr = Util.getIP(this);
        if (myIPStr!=null){
            myIP.append(myIPStr);
        }else {
            myIP.append("请连接wife或者开启热点");
        }
        init();
    }

    private void init(){
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int portNum = Integer.valueOf(port.getText().toString().trim());
                String ipAddress = ip.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                intent.putExtra("port",portNum);
                intent.putExtra("ip",ipAddress);
                intent.putExtra("from","join");
                MainActivity.this.startActivity(intent);
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int portNum = Integer.valueOf(port.getText().toString().trim());
                String ipAddress = ip.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                intent.putExtra("port",portNum);
                intent.putExtra("ip",ipAddress);
                intent.putExtra("from","create");
                MainActivity.this.startActivity(intent);
            }
        });

    }
}

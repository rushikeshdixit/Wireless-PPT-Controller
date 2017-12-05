

package com.example.rushikesh.wifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.example.rushikesh.wifi.R;

import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;


public class remote_control extends AppCompatActivity {

    int initial_total_number;
    Socket client;
    public Button start, pause, next, previous, go;
    public TextView currentpage, totalpage;
    public EditText slideToGo;

    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_control);
        initializeData();      //Initializing the parameters

        Log.d(TAG, "We are in remote_control");
        Intent in = getIntent();

        Bundle b = in.getExtras();      // IpAddress and port number of the Pc that's running the server
        final String ipadd = b.get("ip_address").toString();
        final  int  port = Integer.parseInt(b.get("port_address").toString());



        // ppt start button listener
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=sendMessage(start.getText().toString(), ipadd, port);
                Log.d(TAG, ": " + message);
                currentpage.setText(message);

            }
        });

        // ppt stop button listener
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message= sendMessage(pause.getText().toString(), ipadd, port);
                Log.d(TAG, ": " + message);
                currentpage.setText(message);
            }
        });

        // ppt next page button listener
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=sendMessage(next.getText().toString(), ipadd, port);
                Log.d(TAG, ": " + message);
                currentpage.setText(message);
            }
        });

        // ppt previous button listener
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=sendMessage(previous.getText().toString(), ipadd, port);
                Log.d(TAG, ": " + message);
                currentpage.setText(message);
            }
        });

        // ppt go to specific page button listener
        go.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int num=Integer.parseInt(slideToGo.getText().toString());
                if (num>initial_total_number){
                    Toast.makeText(getApplicationContext(), "There is no such slide", Toast.LENGTH_SHORT).show();
                }
                else {
                    String message = sendMessage(slideToGo.getText().toString(), ipadd, port);
                    Log.d(TAG, ": " + message);
                    currentpage.setText(message);
                }
                slideToGo.setText("");
            }
        });


    }

    // initialization of buttons and textviews
    public void initializeData()
    {
        start=(Button)findViewById(R.id.start_slide);
        pause=(Button)findViewById(R.id.pause_slide);
        next=(Button)findViewById(R.id.next_slide);
        previous=(Button)findViewById(R.id.previous_slide);
        go=(Button)findViewById(R.id.go_slide);
        totalpage=(TextView)findViewById(R.id.totalpageNO);
        currentpage=(TextView)findViewById(R.id.currentpageNO);
        slideToGo=(EditText)findViewById(R.id.slide_No);

        //start.setText("");
    }


    // communicating with the server for changing the slides.
    public String sendMessage(String message,String ipadd,int port){
        String reply=null;
        String page=null;
        String m=null;
        try {
            //client = new Socket(ipadd, port);
            client = new Socket();
            client.connect(new InetSocketAddress(ipadd,port), 15000);

            DataOutputStream dt = new DataOutputStream(client.getOutputStream());
            dt.writeUTF(message);

            Log.d(TAG, ": " + message);

            DataInputStream dam=new DataInputStream(client.getInputStream());
            Log.d(TAG, ": " + dam);

            List<String> strArray = new ArrayList<String>();

            byte[] bs = new byte[8];
            dam.read(bs);
            Log.d(TAG, ":"+ bs);
            StringBuilder sb=new StringBuilder();
            for (byte b:bs)
            {
                // convert byte into character
                char c = (char)b;
                if (Character.isDigit(c) || c==',') {
                    sb.append(c);
                }

            }
            Log.d(TAG, ": " + sb);
            String string = sb.toString();
            String[] parts = string.split(",");
            reply = parts[0];
            page = parts[1];
         
            initial_total_number=Integer.parseInt(page);

            totalpage.setText(page);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            m="error";
        }
        if (m=="error")
        {
            Toast.makeText(getApplicationContext(), "Error....", Toast.LENGTH_SHORT).show();
        }
        return reply;
    }

}

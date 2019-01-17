package com.lichfaker.mqttclientandroid.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lichfaker.log.Logger;
import com.lichfaker.mqttclientandroid.R;
import com.lichfaker.mqttclientandroid.mqtt.MqttManager;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    public static final String URL = "tcp://112.74.185.230:1883";
    private String userName = "userName";
    private String password = "password";
    private String clientId = "clientId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = MqttManager.getInstance().creatConnect(URL, userName, password, clientId);
                        Logger.d("isConnected: " + b);
                        if (b) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"连接成功", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = MqttManager.getInstance().publish("/aaa/bbb/547176052", 0, "on".getBytes());
                        Logger.d("published: " + b);
                        Looper.prepare();
                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                //execute the task
                                boolean b = MqttManager.getInstance().publish("/aaa/bbb/547176052", 0, "off".getBytes());
                                Logger.d("published: " + b);
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this,"执行完毕", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }, 5000);
                        Looper.loop();
                    }
                }).start();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = MqttManager.getInstance().subscribe("/aaa/bbb/547176052", 0);
                        if (b) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"订阅成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MqttManager.getInstance().disConnect();
                        } catch (MqttException e) {

                        }
                    }
                }).start();
            }
        });


    }

    /**
     * 订阅接收到的消息
     * 这里的Event类型可以根据需要自定义, 这里只做基础的演示
     *
     * @param message
     */
    @Subscribe
    public void onEvent(MqttMessage message) {
        Logger.d(message.toString());
        Looper.prepare();
        com.alibaba.fastjson.JSONObject bb = JSON.parseObject(message.toString());
        com.alibaba.fastjson.JSONObject payload = bb.getJSONObject("payload");
        final String deviceType = payload.getString("deviceType");
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,"deviceType： "+deviceType, Toast.LENGTH_SHORT).show();
            }
        });

        Looper.loop();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}

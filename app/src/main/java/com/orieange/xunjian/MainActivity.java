package com.orieange.xunjian;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button sanner ;
    ImageView imageViewScanning;
    final String TAG = "lwj--------------";
    BluetoothAdapter mBluetoothAdapter;
    int searchMiles = 3000;//搜索时间
    String devicesNames ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sanner = (Button) findViewById(R.id.sanner);
        sanner.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                //这是个啥？？注册广播？
                IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver,filter);
                if(mBluetoothAdapter.isEnabled()){
                    sanner.setText("正在搜索...");
                    imageViewScanning = (ImageView) findViewById(R.id.imageScanning);
                    imageViewScanning.setVisibility(View.VISIBLE);
                    RotateAnimation animation = new RotateAnimation(0, 360,Animation.RELATIVE_TO_SELF,
                            0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    animation.setDuration(1000);//设定转一圈的时间
//                    animation.setRepeatCount(Animation.INFINITE);//设定无限循环
                    animation.setRepeatCount(searchMiles/1000);
                    animation.setRepeatMode(Animation.ZORDER_NORMAL);
                    imageViewScanning.startAnimation(animation);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            BTScan();


                        }
                    }).start();
                    Handler handler = new Handler();
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    //页面跳转
                                    gotoActivity();
                                }
                            }
                            , searchMiles);
                }else {
                    mBluetoothAdapter.enable();//打开蓝牙

                }





            }
        });




    }
    //页面跳转
    public void  gotoActivity(){
        Intent intent = new Intent(this,ScannerResult.class);
        startActivity(intent);
    }
    //搜索蓝牙
    public void BTScan(){


        Log.i(TAG,"搜索蓝牙开始执行了");
        mBluetoothAdapter.startDiscovery();
    }


    @Override
    protected void onRestart(){
        super.onRestart();
        sanner = (Button) findViewById(R.id.sanner);

        sanner.setText("开始扫描");
    }

    //TODO 这是网上乱找的，不会用，研究下或者重新找办法
    //定义广播接收
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG,action);
            if(!action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME);
                devicesNames += "\n"+device.getName()+"==>"+device.getAddress()+"\n";
                Log.i(TAG,devicesNames);

            }
        }
    };
}

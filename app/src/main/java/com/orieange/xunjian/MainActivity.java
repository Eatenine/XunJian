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
    Button sannerButton ;
    ImageView imageViewScanning;
    final String TAG = "lwj--------------";
    BluetoothAdapter mBluetoothAdapter;
    int searchMiles = 5000;//搜索时间
    String devicesNames ="111111111111111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //找到搜索按钮，以及监听点击
        sannerButton = (Button) findViewById(R.id.sannerButton);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断蓝牙状态
//        if(mBluetoothAdapter.getState()==BluetoothAdapter.STATE_OFF){
//            sannerButton.setText("打开蓝牙");
//        }

        sannerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //这是个啥？？注册广播？
                IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver,filter);
                //先判断手机蓝牙是否打开，
                if(mBluetoothAdapter.isEnabled()){
                    //临时代码，学习一下
                    String localBTName = mBluetoothAdapter.getName();
                    String localBTMac = mBluetoothAdapter.getAddress();

                    Log.i(TAG,"蓝牙名字为："+localBTName+",MAC地址为："+localBTMac+"。");

                    //界面文字变更
                    sannerButton.setText("正在搜索...");
                    sannerButton.setClickable(false);
                    //图片旋转，首页动画效果
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
                            //蓝牙搜索
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
                                    mBluetoothAdapter.cancelDiscovery();
                                    Log.i(TAG,"蓝牙搜索停止了");
                                    Log.i(TAG,devicesNames);
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
        sannerButton = (Button) findViewById(R.id.sannerButton);

        sannerButton.setText("开始扫描");
        sannerButton.setClickable(true);
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

package com.example.WiFiReader;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.sdk_20140210.R;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;



public class WiFiReaderActivity extends Activity {

	private static final int REQUEST_CONNECT_DEVICE = 1;
	public static AndroidAcceptServer readerServer;
	
	private Button btnOpenWiFi;
	private Button btnCloseWiFi;
	private Button btnClear;
	private ListView lvMessage;
	private ArrayAdapter<String> arrayAdapterMessage;
	
	private WifiAdmin mWifiAdmin;  
	
	private static String ipAddress ="192.168.1.39";
	private static Socket socket;
	private static int port =50000;
	
	private ProgressDialog progressDialog = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mWifiAdmin = new WifiAdmin(WiFiReaderActivity.this);
		
		arrayAdapterMessage = new ArrayAdapter<String>(this, R.layout.device_name);
		lvMessage = (ListView) findViewById(R.id.lvMessage);
		lvMessage.setAdapter(arrayAdapterMessage);
		
		btnOpenWiFi = (Button)findViewById(R.id.btnOpenWiFi);
		
		/*****客户端模式****/
		btnOpenWiFi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				openSocket();
				
			}
		});
		btnCloseWiFi = (Button)findViewById(R.id.btnCloseWiFi);
		

		/*****客户端模式****/
		btnCloseWiFi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				closeSocket();
			}
		});

		btnClear = (Button)findViewById(R.id.btnClear);
		btnClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(arrayAdapterMessage!=null)
	        		arrayAdapterMessage.clear();
				
			}
		});
	}

	
	private void openSocket(){
		
		SharedPreferences Device = getSharedPreferences("Device", 0);  
        String DeviceIP = Device.getString("DeviceIP", "");    
		if(!DeviceIP.equals(""))
			ipAddress = DeviceIP;
		String DevicePort = Device.getString("DevicePort", "");    
		if(!DevicePort.equals(""))
			port = Integer.valueOf(DevicePort) ;
		new Thread(){
			public void run(){
				try {
					if(socket!= null){
						Message msg = new Message();
						msg.what = comm.SOCKETOPENED;
						msg.obj = "socket is opened";
						mHandler.sendMessage(msg);
					}else{
						Message msg = new Message();
						msg.what = comm.OPEN_SOCKETING;
						msg.obj = "socket is opening";
						mHandler.sendMessage(msg);
						socket = new Socket(ipAddress, port);
						if(socket != null ){
							msg = new Message();
							msg.what = comm.OPEN_SOCKET_SUCCESS;
							msg.obj = "open socket success";
							mHandler.sendMessage(msg);
							new AndroidAcceptClicent(socket,mHandler).start();
						}else{
							msg = new Message();
							msg.what = comm.OPEN_SOCKET_FAIL;
							msg.obj = "open socket fail";
							mHandler.sendMessage(msg);
						}
						
					}
				} catch (UnknownHostException e) {
					Message msg = new Message();
					msg.what = comm.SOCKETOPENED;
					msg.obj = e.toString();
					mHandler.sendMessage(msg);
					
				} catch (IOException e) {
					Message msg = new Message();
					msg.what = comm.SOCKETOPENED;
					msg.obj = e.toString();
					mHandler.sendMessage(msg);
				}
			}
		}.start();
		
		
	}
	private void closeSocket(){
		
			Message msg = new Message();
			msg.what = comm.CLOSESOCKET;
			try {
				if(socket !=null){
					socket.close();
					socket = null;
					msg.obj = "close socket  success";
				}
				else{
					msg.obj = "socket  is closed";
				}
			} catch (IOException e) {
				msg.obj = e.toString();
			}finally{
				mHandler.sendMessage(msg);
			}
	}
	

	@SuppressWarnings("deprecation")
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
            AlertDialog isExit = new AlertDialog.Builder(this).create();  
            isExit.setTitle("系统提示");  
            isExit.setMessage("确定要退出吗");  
            isExit.setButton("确定", listener);  
            isExit.setButton2("取消", listener);  
            isExit.show();  
  
        }  
          
        return false;  
          
    }  
	
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()  
    {  
        public void onClick(DialogInterface dialog, int which)  
        {  
            switch (which)  
            {  
            case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序  
                finish();  
                break;  
            case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框  
                break;  
            default:  
                break;  
            }  
        }  
    };   
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
        	if(resultCode == Activity.RESULT_OK){
        		Bundle b = data.getExtras(); 
        		WifiConfiguration wifiInfo  = b.getParcelable("wifi_info");
        		if(wifiInfo == null)
        			Toast.makeText(this, "wifi 无效", Toast.LENGTH_SHORT).show();
        		else if(mWifiAdmin.addNetWork(wifiInfo)){
        			Toast.makeText(this, "连接成功:"+wifiInfo.SSID, Toast.LENGTH_SHORT).show();
        		}else
        			Toast.makeText(this, "连接失败:"+wifiInfo.SSID, Toast.LENGTH_SHORT).show();
        	}
            break;
        default: break;
        }
    }
    
    
	private MessageHandler mHandler = new MessageHandler();
    
class MessageHandler extends Handler {
		
		public MessageHandler() {      
        }  
		public MessageHandler(Looper looper) {   
			super(looper);   
        }   
  
        @Override   
        public void handleMessage(Message msg) {
        	SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");     
        	Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
        	String str = formatter.format(curDate); 

        	switch (msg.what){
	        	case comm.ACCECP:
	        		arrayAdapterMessage.add(str+":"+(String)msg.obj);
	            	break;
	        	case comm.CONNECT_KILL:
	        		arrayAdapterMessage.add(str+":"+(String)msg.obj);
	        		closeSocket();
	            	break;
	        	case comm.SOCKETOPENED:
	        		if(progressDialog !=null)
	        			progressDialog.dismiss();
	        		arrayAdapterMessage.add(str+":"+(String)msg.obj);
	            	break;
	        	case comm.OPEN_SOCKETING:
	        		progressDialog = ProgressDialog.show(WiFiReaderActivity.this, "打开", "正在操作,请稍候！"); 
	        		arrayAdapterMessage.add( str+":"+(String)msg.obj);
	            	break;
	        	case comm.OPEN_SOCKET_SUCCESS:
	        		if(progressDialog !=null)
	        			progressDialog.dismiss();
	        		arrayAdapterMessage.add( str+":"+(String)msg.obj);
	        		break;
	        	case comm.OPEN_SOCKET_FAIL:
	        		if(progressDialog !=null)
	        			progressDialog.dismiss();
	        		arrayAdapterMessage.add( str+":"+(String)msg.obj);
	        		break;
	        	case comm.CLOSESOCKET:
	        		arrayAdapterMessage.add(str +":"+(String)msg.obj);
	        		break;
	            default :break;
            }  
        }
	}   

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.Search:
        	Intent deviceListIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(deviceListIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.ChangeDeivceIP:
        	
        	Intent ReaderListIntent = new Intent(this, ChangeDeivceIPAcitvity.class);
        	startActivity(ReaderListIntent);
        	return true;
        case R.id.Exit:
            finish();
            return true;
        }
        return false;
    }

}

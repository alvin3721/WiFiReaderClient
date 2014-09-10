package com.example.WiFiReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.WiFiReader.WiFiReaderActivity.MessageHandler;

import android.os.Message;
import android.util.Log;

public class AndroidAcceptClicent extends Thread{
	private Socket socket;
	private MessageHandler mHandler;
	private static final String TAG="LogActivity";
	public AndroidAcceptClicent(Socket socket,MessageHandler mHandler){
		this.socket = socket;
		this.mHandler = mHandler;
	}
	@SuppressWarnings("unused")
	public void run() {
		
		try {
			InputStream in = socket.getInputStream();
			while(true){
				
				if (socket.isConnected()) {  
                    if (!socket.isInputShutdown()) { 
                    	
                    	byte[] charBuf = new byte[3]; 

                    	in.read(charBuf , 0 , 3); 
                    	int head = (int)charBuf[0] & 0xff;
                    	if(head!=0xAA){
                    		Message msg = new Message();
    						msg.what = comm.CONNECT_KILL;
    						msg.obj="socket断开1";
    						mHandler.sendMessage(msg); 
    						break;
                    	}
  
                    	int size =((int)charBuf[0] & 0xff)+2;
                    	byte[] DataHeader = new byte[size]; 
                    	in.read(DataHeader , 0 , size); 
                    	if(DataHeader.length<=2){
                    		Message msg = new Message();
    						msg.what = comm.CONNECT_KILL;
    						msg.obj="socket断开2";
    						mHandler.sendMessage(msg); 
    						break;
                    	}
                    	
                    	
						Message msg = new Message();
						msg.what = comm.ACCECP;
                    	
                    	if(DataHeader[0] == 0x00){
                    		int sum = (int)DataHeader[1] & 0xff;
    						int deviceNum = (int)DataHeader[2] & 0xff;
    						int snr = (int)DataHeader[4] & 0xff;
    						msg.obj = msg.obj ="总工序:"+sum+ ",刷卡设备号:"+deviceNum+",上次刷卡设备号:"+snr+":"+"操作成功";
    			       	}else if(DataHeader[0] == 0x01){
    			       		
    			       		int flag = (int) DataHeader[1]&0xff;
    			       		int sum = (int)DataHeader[2] & 0xff;
    						int deviceNum = (int)DataHeader[3] & 0xff;
    						int snr = (int)DataHeader[4] & 0xff;
    			       		if(flag==0x82){
    			       			msg.obj ="总工序:"+sum+ ",刷卡设备号:"+deviceNum+",上次刷卡设备号:"+snr+":"+"未知错误";
    			       		}else if(flag==0x83){
    			       			msg.obj ="总工序:"+sum+ ",刷卡设备号:"+deviceNum+",上次刷卡设备号:"+snr+":"+"读写卡错误";
    			       		}else if(flag==0x85){
    			       			msg.obj ="总工序:"+sum+ ",刷卡设备号:"+deviceNum+",上次刷卡设备号:"+snr+":"+"未知错误";
    			       		}else if(flag==0x87){
    			       			int snr1 = snr+1;
    			       			msg.obj ="总工序:"+sum+ ",刷卡设备号:"+deviceNum+",上次刷卡设备号:"+snr+":"+"工序错误,请刷工序"+snr1;
    			       		}else if(flag==0x89){
    			       			msg.obj ="总工序:"+sum+ ",刷卡设备号:"+deviceNum+",上次刷卡设备号:"+snr+":"+"重复刷卡";
    			       		}
    			       		
    					}else {
							msg.obj = comm.bin2hex(DataHeader);// bin2hex(String.valueOf(buffer));
			       		}
                    	mHandler.sendMessage(msg); 
                    }  
                }else{
                	Message msg = new Message();
					msg.what = comm.ACCECP;
					msg.obj ="Socket 断开";
					mHandler.sendMessage(msg); 
                	break;
                } 
			}
		} catch (UnknownHostException e1) {
			Message msg = new Message();
			msg.what =  comm.CONNECT_KILL;
			msg.obj = e1.toString();
			mHandler.sendMessage(msg);
			
		} catch (IOException e1) {
			Message msg = new Message();
			msg.what = comm.CONNECT_KILL;
			msg.obj = e1.toString();
			mHandler.sendMessage(msg);
		}
	}
}
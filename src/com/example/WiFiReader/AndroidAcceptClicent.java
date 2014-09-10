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
    						msg.obj="socket�Ͽ�1";
    						mHandler.sendMessage(msg); 
    						break;
                    	}
  
                    	int size =((int)charBuf[0] & 0xff)+2;
                    	byte[] DataHeader = new byte[size]; 
                    	in.read(DataHeader , 0 , size); 
                    	if(DataHeader.length<=2){
                    		Message msg = new Message();
    						msg.what = comm.CONNECT_KILL;
    						msg.obj="socket�Ͽ�2";
    						mHandler.sendMessage(msg); 
    						break;
                    	}
                    	
                    	
						Message msg = new Message();
						msg.what = comm.ACCECP;
                    	
                    	if(DataHeader[0] == 0x00){
                    		int sum = (int)DataHeader[1] & 0xff;
    						int deviceNum = (int)DataHeader[2] & 0xff;
    						int snr = (int)DataHeader[4] & 0xff;
    						msg.obj = msg.obj ="�ܹ���:"+sum+ ",ˢ���豸��:"+deviceNum+",�ϴ�ˢ���豸��:"+snr+":"+"�����ɹ�";
    			       	}else if(DataHeader[0] == 0x01){
    			       		
    			       		int flag = (int) DataHeader[1]&0xff;
    			       		int sum = (int)DataHeader[2] & 0xff;
    						int deviceNum = (int)DataHeader[3] & 0xff;
    						int snr = (int)DataHeader[4] & 0xff;
    			       		if(flag==0x82){
    			       			msg.obj ="�ܹ���:"+sum+ ",ˢ���豸��:"+deviceNum+",�ϴ�ˢ���豸��:"+snr+":"+"δ֪����";
    			       		}else if(flag==0x83){
    			       			msg.obj ="�ܹ���:"+sum+ ",ˢ���豸��:"+deviceNum+",�ϴ�ˢ���豸��:"+snr+":"+"��д������";
    			       		}else if(flag==0x85){
    			       			msg.obj ="�ܹ���:"+sum+ ",ˢ���豸��:"+deviceNum+",�ϴ�ˢ���豸��:"+snr+":"+"δ֪����";
    			       		}else if(flag==0x87){
    			       			int snr1 = snr+1;
    			       			msg.obj ="�ܹ���:"+sum+ ",ˢ���豸��:"+deviceNum+",�ϴ�ˢ���豸��:"+snr+":"+"�������,��ˢ����"+snr1;
    			       		}else if(flag==0x89){
    			       			msg.obj ="�ܹ���:"+sum+ ",ˢ���豸��:"+deviceNum+",�ϴ�ˢ���豸��:"+snr+":"+"�ظ�ˢ��";
    			       		}
    			       		
    					}else {
							msg.obj = comm.bin2hex(DataHeader);// bin2hex(String.valueOf(buffer));
			       		}
                    	mHandler.sendMessage(msg); 
                    }  
                }else{
                	Message msg = new Message();
					msg.what = comm.ACCECP;
					msg.obj ="Socket �Ͽ�";
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
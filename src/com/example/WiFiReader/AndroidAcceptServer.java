package com.example.WiFiReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.example.WiFiReader.WiFiReaderActivity.MessageHandler;

import android.os.Message;

public class AndroidAcceptServer extends Thread{
	
	public Map<String,Socket> outputStreamList = new HashMap<String, Socket>();
	private ServerSocket serverSocket;
	private MessageHandler mHandler;
	
	public AndroidAcceptServer(ServerSocket serverSocket,MessageHandler mHandler){
		this.serverSocket = serverSocket;
		this.mHandler = mHandler;
		
	}
	 public class readData extends Thread{
		 private InputStream in;
		 private Socket client;
		 public readData(Socket client){
			 try {
				 this.client = client;
				 this.in = client.getInputStream();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			 
		 }
		 public void run() { 
        	 while(true){
        		 if(in == null)
        			 return;
        		 
				try{
					//接受客户端信息
					byte[] DataHeader = new byte[3]; 
					in.read(DataHeader,0,3); 
					if(DataHeader[0]==0x00){
						client.close();
						if(outputStreamList.containsKey(client.getInetAddress().getHostAddress())){
							outputStreamList.remove(client.getInetAddress().getHostAddress());
						}
						return;
                	}
					
					int size =(int) DataHeader[2]+2;
                	byte[] charBuf = new byte[size]; 
                	in.read(charBuf,0,size); 
                	
                	
                	if(charBuf[0] == 0x00){
                		int temp =((int) charBuf[2] )&0xff;
                		Message msg = new Message();
						msg.what = comm.ACCECP;
						msg.obj = "第"+temp+"道工序完成";
						mHandler.sendMessage(msg); 
                	}else if(charBuf[0] == 0x01){
                		Message msg = new Message();
						msg.what = comm.ACCECP;
						int temp = ((int) charBuf[1] )&0xff;
                		if(temp==0x82){
                			msg.obj = "未知错误";
                		}else if(temp==0x83){
                			msg.obj = "读写卡错误";
                		}else if(temp==0x85){
                			msg.obj = "";
                		}else if(temp==0x87){
                			int snr = (int)charBuf[4] & 0xff;
                			
                			msg.obj = "目前已经执行到第"+snr+"道工序，请执行第"+(snr+1)+"道工序";
                		}else if(temp==0x89){
                			int snr = (int)charBuf[4] & 0xff;
                			msg.obj = "第"+snr+"道工序已经完成，请不要重复刷卡";
                		}
                		else {
							msg.obj = comm.bin2hex(DataHeader) + comm.bin2hex(charBuf);// bin2hex(String.valueOf(buffer));
                		}
                		
						mHandler.sendMessage(msg); 
                		
                	}
				}catch(Exception ex){
					System.out.println(ex.getMessage());
					ex.printStackTrace();
				}
			}
         }
	 }
	
	public void run() {
		try {
			int i =0;
			while(true){
				//接受客户端请求
				Socket client=serverSocket.accept();
				
				if(outputStreamList.containsKey(client.getInetAddress().getHostAddress())){
					outputStreamList.remove(client.getInetAddress().getHostAddress());
				}
				
				outputStreamList.put(client.getInetAddress().getHostAddress(),client);
				new readData(client).start();
				//client.close();
			}
		} catch (IOException e) {}
	}
	public void writeData(String ipaddr,byte[] buffer){
		if(outputStreamList.containsKey(ipaddr)==false)
			return;
		Socket client = outputStreamList.get(ipaddr);
		
		try {
			OutputStream outputStream =   client.getOutputStream();
			outputStream.write(buffer);
			
			InputStream in = client.getInputStream();
			
			byte[] DataHeader = new byte[3]; 
			in.read(DataHeader,0,3); 
			if(DataHeader[0]==0x00){
				client.close();
				if(outputStreamList.containsKey(client.getInetAddress().getHostAddress())){
					outputStreamList.remove(client.getInetAddress().getHostAddress());
				}
				return;
        	}
			
			int size =(int) DataHeader[2]+2;
        	byte[] charBuf = new byte[size]; 
        	in.read(charBuf,0,size); 
			
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	public void closeSocket(){
		Iterator iter  = outputStreamList.entrySet().iterator() ;
        while (iter.hasNext()) {  
        	 Map.Entry entry = (Map.Entry) iter.next(); 
        	 Socket client =  (Socket) entry.getValue(); 
        	 try {
				client.close();
				iter.remove();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
        }             
	}
}

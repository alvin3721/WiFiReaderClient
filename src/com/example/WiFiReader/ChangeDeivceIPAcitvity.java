package com.example.WiFiReader;

import com.example.sdk_20140210.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeDeivceIPAcitvity extends Activity {
	
	private EditText etDeviceIP;
	private EditText etDevicePort;
	private Button btnChangeIP;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.device_ip);
	    
	    etDeviceIP = (EditText)findViewById(R.id.eTDeviceIP);
	    etDevicePort = (EditText)findViewById(R.id.eTDevicePort);
	    
	    SharedPreferences Device = getSharedPreferences("Device", 0);  
        String DeviceIP = Device.getString("DeviceIP", "");    
		if(!DeviceIP.equals(""))
			etDeviceIP.setText(DeviceIP);
		String DevicePort = Device.getString("DevicePort", "");    
		if(!DevicePort.equals(""))
			etDevicePort.setText(DevicePort);
		
	    
	    
	    btnChangeIP = (Button)findViewById(R.id.btnChange);
	    btnChangeIP.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedPreferences Device = getSharedPreferences("Device", 0);  
				Device.edit().putString("DeviceIP", etDeviceIP.getText().toString()).commit(); 
				Device.edit().putString("DevicePort", etDevicePort.getText().toString()).commit(); 
				finish();
			}
		});
	    
	}
	
}

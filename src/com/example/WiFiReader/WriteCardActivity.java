package com.example.WiFiReader;

import com.example.sdk_20140210.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WriteCardActivity  extends Activity  {
	private Button btnWriteCard;
	private EditText eTBlockNum;
	private EditText etPassword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.write_card);
	    
	    Intent intent= getIntent();
	    final String IPAddr = intent.getStringExtra("ipAddr");
	    
	    eTBlockNum = (EditText)findViewById(R.id.eTWriteBlockNum);
	    etPassword = (EditText)findViewById(R.id.eTWriteBlockPassword);
	    
	    
	    
	    btnWriteCard = (Button)findViewById(R.id.btnWrite);
	    btnWriteCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				byte[] outputData = new byte[15];
				outputData[0] = (byte)0xAA;
				outputData[1] = (byte)0x00;
				outputData[2] = (byte)0x0A;
				outputData[3] = (byte)0x20;
				outputData[4] = (byte)0x00;
				outputData[5] = (byte)0x01;
				outputData[6] = (byte) (Integer.valueOf(eTBlockNum.getText().toString())&0xFF);

				String password = etPassword.getText().toString();
				byte[] psw = comm.getPassword(password.subSequence(0, 12).toString());
				System.arraycopy(psw, 0,outputData, 7,  6);
				outputData[13] = (byte) 0x94;
				outputData[14] = (byte) 0xBB;
		        WiFiReaderActivity.readerServer.writeData(IPAddr,outputData );
		        
		        finish();
			}
		});
	    
	}
}

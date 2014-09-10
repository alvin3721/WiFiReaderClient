package com.example.WiFiReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.sdk_20140210.R;

public class ReaderInfoActivity extends Activity {
	private Button btnSave;
	private Button btnCancel;
	
	private EditText etReaderPSN;
	private EditText etReaderName;
	private EditText etIPAddr;
	private EditText etPort;
	private EditText etBlock;
	private EditText etPassword;
	private Spinner spMode;
	
	private ArrayAdapter<String> adapter;
	private final String[] m={"����ģʽ","����ģʽ"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �Զ����ɵķ������
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.reader_info);

	    etReaderPSN = (EditText) findViewById(R.id.eTReaderPSN);
	    etReaderName = (EditText) findViewById(R.id.eTName);
	    etIPAddr = (EditText) findViewById(R.id.eTIpAddr);
	    etPort = (EditText) findViewById(R.id.eTPort);
	    etBlock = (EditText)findViewById(R.id.eTBlock);
	    etPassword = (EditText)findViewById(R.id.eTPassword);
	    
	    spMode = (Spinner)findViewById(R.id.spMode);
	    
	    
	  //����ѡ������ArrayAdapter��������
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);
         
        //���������б�ķ��
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         
        //��adapter ��ӵ�spinner��
        spMode.setAdapter(adapter);
         
        //����¼�Spinner�¼�����  
        spMode.setOnItemSelectedListener(new SpinnerSelectedListener());
         
        //����Ĭ��ֵ
        spMode.setVisibility(View.VISIBLE);

	    Intent intent= getIntent();
	    String IPAddr = intent.getStringExtra("ipAddr");
	    etIPAddr.setText(IPAddr);
	    

	    btnSave = (Button) findViewById(R.id.btnSave);
	    btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				String  ip = etIPAddr.getText().toString();
				byte[] outputData = new byte[15];
				outputData[0] = (byte)0xAA;
				outputData[1] = (byte)0x00;
				outputData[2] = (byte)0x0A;
				outputData[3] = (byte)0x8E;
				outputData[4] = spMode.getSelectedItem().toString()=="����ģʽ"?(byte)0x00: (byte)0x10;
				outputData[5] = (byte) (Integer.valueOf(etReaderPSN.getText().toString())&0xFF);
				outputData[6] = (byte) (Integer.valueOf(etBlock.getText().toString())&0xFF);
				
				String password = etPassword.getText().toString();
				
				//password.subSequence(0, 1).
				byte[] psw = comm.getPassword(password.subSequence(0, 12).toString());
				System.arraycopy(psw, 0,outputData, 7,  6);
				outputData[13] = (byte) 0x94;
				outputData[14] = (byte) 0xBB;
		        WiFiReaderActivity.readerServer.writeData(ip, new byte[]{(byte) 0xAA,0x00,0x0A,(byte) 0x8E,0x10,0x02,0x01,(byte) 0xFF, (byte) 0xFF, (byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0x97,(byte) 0xBB});
		        
		        finish();
		        
			}
		});
	    btnCancel = (Button) findViewById(R.id.btnCancel);
	    btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	    
	}
	
	//ʹ��������ʽ����
    class SpinnerSelectedListener implements  android.widget.AdapterView.OnItemSelectedListener{
 
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
        	//ReaderInfoActivity.this.setTitle("���Ѫ���ǣ�"+m[arg2]);
        }
 
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}


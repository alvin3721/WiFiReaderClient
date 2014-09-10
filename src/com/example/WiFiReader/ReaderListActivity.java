package com.example.WiFiReader;

import java.util.Iterator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.sdk_20140210.R;

public class ReaderListActivity extends Activity {
	
	private ArrayAdapter<String> readerArrayAdapter;
	private ListView newDevicesListView;
	private String readerNameSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.reader_list);
	    
	    readerArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
	    newDevicesListView = (ListView) findViewById(R.id.tvReaderList);
        newDevicesListView.setAdapter(readerArrayAdapter);
        newDevicesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				readerNameSelected = readerArrayAdapter.getItem(arg2);
				Intent ReaderInfoIntent = new Intent(ReaderListActivity.this, ReaderInfoActivity.class);
				ReaderInfoIntent.putExtra("ipAddr", readerNameSelected);
	        	startActivity(ReaderInfoIntent);
				
			}
		});
        newDevicesListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu conMenu, View arg1,
					ContextMenuInfo arg2) {
				conMenu.setHeaderTitle("ContextMenu");       
                conMenu.add(0, 0, 0, "查看"); 
                conMenu.add(0, 1, 1, "读卡");
                conMenu.add(0, 2, 2, "写卡"); 
				
			}
		});
      //监听列表项目的长按事件  
        newDevicesListView.setOnItemLongClickListener(new OnItemLongClickListener(){  
  
            @Override  
            public boolean onItemLongClick(AdapterView<?> parent, View view,  
                    int position, long id) {   
                readerNameSelected = readerArrayAdapter.getItem(position);
                return false;  
            }  
              
        });  
	}
	
	@Override
	protected void onResume() {
		
		showAllReader();
		super.onResume();
	}

	private void showAllReader(){
		readerArrayAdapter.clear();
		Iterator<String> iterator_2 = WiFiReaderActivity.readerServer.outputStreamList.keySet().iterator();    
        while (iterator_2.hasNext()) {  
         Object key = iterator_2.next();    
         readerArrayAdapter.add(key.toString());
        }             
		 
	}
	@Override  
    public boolean onContextItemSelected(MenuItem item) {//执行上下文菜单中的菜单  
        int itemId = item.getItemId();  
        Intent ReaderInfoIntent = null;
        switch(itemId){
        case 0:
        	ReaderInfoIntent = new Intent(this, ReaderInfoActivity.class);
        	ReaderInfoIntent.putExtra("ipAddr", readerNameSelected);
        	startActivity(ReaderInfoIntent);
        	break;
        case 1:
        	ReaderInfoIntent = new Intent(this, ReadCardActivity.class);
        	ReaderInfoIntent.putExtra("ipAddr", readerNameSelected);
        	startActivity(ReaderInfoIntent);
        	break;
        case 2:
        	ReaderInfoIntent = new Intent(this, WriteCardActivity.class);
        	ReaderInfoIntent.putExtra("ipAddr", readerNameSelected);
        	startActivity(ReaderInfoIntent);
        	break;
        default:break;
        }
        return super.onContextItemSelected(item);  
    }  
	@Override  
    public void onContextMenuClosed(Menu menu) {//关闭上下文菜单  
        
        super.onContextMenuClosed(menu);  
    }  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.reader_list, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.addReader:
        	Intent ReaderInfoIntent = new Intent(this, ReaderInfoActivity.class);
        	startActivity(ReaderInfoIntent);
            return true;
        case R.id.deleteReader:
            finish();
            return true;
        }
        return false;
    }

}

package com.why.ipcbinddeath;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.why.ipcbinddeath.server.IPCService;


public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startService(new Intent(getApplicationContext(), IPCService.class));

	}

}

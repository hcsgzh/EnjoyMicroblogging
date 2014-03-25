package com.aisidi.kuaiyue.activity;

import com.aisidi.kuaiyue.bean.HomeTimeLineBean;
import com.aisidi.kuaiyue.pager.UpdateWeibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

public class ReplayActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		setContentView(new UpdateWeibo(this, intent.getIntExtra("state", 0), 
				(HomeTimeLineBean) intent.getSerializableExtra("bean")),
				new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		
	}

	
}

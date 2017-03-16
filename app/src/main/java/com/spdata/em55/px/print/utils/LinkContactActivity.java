package com.spdata.em55.px.print.utils;

import android.app.Activity;
import android.os.Bundle;

import com.spdata.em55.R;
import com.spdata.em55.lr.GpsAct;

public class LinkContactActivity extends Activity {
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationContext.getInstance().addActivity(LinkContactActivity.this);
		setContentView(R.layout.activity_linkcontact);
	}
}

package com.coolweather.app.receiver;


import com.coolweather.app.service.AotuUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AutoUpdateReceiver", "Æô¶¯broadcast");
		Intent i = new Intent(context, AotuUpdateService.class);
		context.startService(i);
	}

}

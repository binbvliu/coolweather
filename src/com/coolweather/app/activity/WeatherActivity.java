package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.service.AotuUpdateService;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	/**
	 * 城市名字显示
	 */
	private TextView cityNameText;
	/**
	 * 发布时间显示
	 */
	private TextView  publishText;
	/**
	 * 显示天气描述
	 */
	private TextView weatherDespText;
	/**
	 * 显示气温1
	 */
	private TextView temp1Text;
	/**
	 * 显示气温2
	 */
	private TextView temp2Text;
	/**
	 * 显示当前日期
	 */
	private TextView currentText;
	
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshweather;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		currentText = (TextView) findViewById(R.id.current_date);
		publishText = (TextView) findViewById(R.id.publish_text);
		refreshweather = (Button) findViewById(R.id.refresh_city);
		switchCity = (Button) findViewById(R.id.switch_city);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		
		switchCity.setOnClickListener(this);
		refreshweather.setOnClickListener(this);
		
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			//县级代号查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			//查询天气 
			queryWeatherCode(countyCode);
		}else{
			showWeather();
		}
	}
	/**
	 * 根据县代号查询天气代号
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address  = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address,"countyCode");
	}
	
	private void queryWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/adat/cityinfo/"+weatherCode+".html";
		queryFromServer(address,"weatherCode");
	}
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			@Override
			public void onFinsh(String response) {
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] array = response.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					//处理器返回天气数据
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});
		
	}
	
	private void showWeather(){
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(preference.getString("cityName", ""));
		currentText.setText(preference.getString("currentTime", "")); 
		publishText.setText("今天 "+preference.getString("publishTime", "")+" 发布") ;
		temp1Text.setText(preference.getString("temp1", ""));
		temp2Text.setText(preference.getString("temp2", ""));
		weatherDespText.setText(preference.getString("weatherDesp", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
		//启动服务
		Intent intent = new Intent(this, AotuUpdateService.class);
		startService(intent);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;

		case R.id.refresh_city:
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = preferences.getString("cityid", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		}
	}
}

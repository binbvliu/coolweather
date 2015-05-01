package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallBackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	
	/**
	 * 市列表
	 */
	private List<City> cityList;
	
	/**
	 * 县列表
	 */
	private List<County> countyList;
	
	/**
	 * 选中的省
	 */
	private Province selectProvince;
	
	/**
	 * 选中的市
	 */
	private City selectCity;
	
	/**
	 * 选中的县
	 */
	private County selectCounty;
	
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题
		setContentView(R.layout.choose_area);
		titleView = (TextView) findViewById(R.id.title_text);
		listView = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		queryProvince();
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (currentLevel) {
				case LEVEL_PROVINCE:
					selectProvince = provinceList.get(position);
					queryCity();
					break;

				case LEVEL_CITY:
					selectCity = cityList.get(position);
					queryCounty();
					break;
				}
			}
		});
	}
	
	/**
	 * 查询市中所有的县，优先数据库，再服务器
	 */
		private void queryCounty() {
			countyList = coolWeatherDB.loadCounty(selectCity.getId());
			if(countyList.size()>0){
				dataList.clear();
				for (County i : countyList) {
					dataList.add(i.getCountyName());
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleView.setText(selectCity.getCityName());
				currentLevel = LEVEL_COUNTY;
			}else{
				queryFromServer(selectCity.getCityCode(), "county");
			}
		}
		/**
		 * 查询市消息，没有请求服务器
		 */
		private void queryCity() {
			cityList = coolWeatherDB.loadCitys(selectProvince.getId());
			if(cityList.size()>0){
				dataList.clear();
				for (City c : cityList) {
					dataList.add(c.getCityName());
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleView.setText(selectProvince.getProvinceName());
				currentLevel = LEVEL_CITY;
			}else{
				queryFromServer(selectProvince.getProvinceCode(), "city");
			}
			
		}
	
		/**
		 * 查询所有的省优先在数据库中查找，没有在去网上查找
		 */
		private void queryProvince() {
			provinceList = coolWeatherDB.loadProvinces();
			if(provinceList.size()>0){
				dataList.clear();
				for (Province p : provinceList) {
					dataList.add(p.getProvinceName());
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleView.setText("中国");
				currentLevel = LEVEL_PROVINCE;
			}else{
				queryFromServer(null, "province");
			}
		}
		
		/**
		 * 查询服务器返回的数据
		 * @param code
		 * @param type
		 */
	
	private void queryFromServer(final String code, final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			//查询省代号名称
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			@Override
			public void onFinsh(String response) {
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handelProvinceResonse(coolWeatherDB, response);
				}else if("city".equals(type)){
					result = Utility.handelCityResonse(coolWeatherDB, response, selectProvince.getId());
				}else if("county".equals(type)){
					result = Utility.handelCountyResonse(coolWeatherDB, response, selectCity.getId());
				}
				
				if(result){
					//通过runOnUiThread()方法回到线程的处理逻辑
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvince();
							}else if("city".equals(type)){
								queryCity();
							}else if("county".equals(type)){
								queryCounty();
							}
						}

						
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//通过runOnUiThread()方法处理返回逻辑
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * 显示进度条对话框
	 */
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度条对话框
	 */
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * back建，返回 省，市列表或者退出
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel ==LEVEL_CITY){
			queryProvince();
		}else if(currentLevel ==LEVEL_COUNTY){
			queryCity();
		}else if(currentLevel ==LEVEL_PROVINCE){
			finish();
		}
	}
}



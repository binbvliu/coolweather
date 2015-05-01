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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
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
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	
	/**
	 * ���б�
	 */
	private List<City> cityList;
	
	/**
	 * ���б�
	 */
	private List<County> countyList;
	
	/**
	 * ѡ�е�ʡ
	 */
	private Province selectProvince;
	
	/**
	 * ѡ�е���
	 */
	private City selectCity;
	
	/**
	 * ѡ�е���
	 */
	private County selectCounty;
	
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;

	/**
	 * �Ƿ���weatherActivity��ת����
	 */
	private boolean isFormWeatherActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFormWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		//����Ѿ�ѡ������Ҳ��Ǵ�WeatherActivity��ת�����ģ�����WeatherActivity
		if(preferences.getBoolean("selectCity", false)&&!isFormWeatherActivity){
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);//�����ޱ���
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
				case LEVEL_COUNTY:
					selectCounty =countyList.get(position);
					String countyCode = selectCounty.getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
					break;	
				}
			}
		});
	}
	
	/**
	 * ��ѯ�������е��أ��������ݿ⣬�ٷ�����
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
		 * ��ѯ����Ϣ��û�����������
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
		 * ��ѯ���е�ʡ���������ݿ��в��ң�û����ȥ���ϲ���
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
				titleView.setText("�й�");
				currentLevel = LEVEL_PROVINCE;
			}else{
				queryFromServer(null, "province");
			}
		}
		
		/**
		 * ��ѯ���������ص�����
		 * @param code
		 * @param type
		 */
	
	private void queryFromServer(final String code, final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			//��ѯʡ��������
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
					//ͨ��runOnUiThread()�����ص��̵߳Ĵ����߼�
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
				//ͨ��runOnUiThread()�����������߼�
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	/**
	 * ��ʾ�������Ի���
	 */
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * �رս������Ի���
	 */
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * back�������� ʡ�����б�����˳�
	 */
	@Override
	public void onBackPressed() {
		if(currentLevel ==LEVEL_CITY){
			queryProvince();
		}else if(currentLevel ==LEVEL_COUNTY){
			queryCity();
		}else /*if(currentLevel ==LEVEL_PROVINCE){
			finish();
		}*/
		{
			if(isFormWeatherActivity){
				Intent intent =new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}



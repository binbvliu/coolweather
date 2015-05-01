package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {
	
	/**
	 * �������������ص�ʡ�����ݲ��������ݿ�
	 * @param coolWeatherDB
	 * @param ʡ�������ַ��� 01|����,02|�Ϻ�,03|���,04|����
	 * @return
	 */
	public  synchronized static boolean handelProvinceResonse(CoolWeatherDB coolWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces  = response.split(",");
			if(allProvinces!=null&&allProvinces.length>0){
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province  = new Province();
					province.setProvinceName(array[1]);
					province.setProvinceCode(array[0]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param coolWeatherDB
	 * @param response
	 * @param provinceId
	 * @return
	 */
	public  synchronized static boolean handelCityResonse(CoolWeatherDB coolWeatherDB, String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities  = response.split(",");
			if(allCities!=null&&allCities.length>0){
				for (String p : allCities) {
					String[] array = p.split("\\|");
					City city  = new City();
					city.setCityName(array[1]);
					city.setCityCode(array[0]);
					city.setProvinceId(provinceId);
					coolWeatherDB.insertCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	
	public  synchronized static boolean handelCountyResonse(CoolWeatherDB coolWeatherDB, String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties  = response.split(",");
			if(allCounties!=null&&allCounties.length>0){
				for (String p : allCounties) {
					String[] array = p.split("\\|");
					County county  = new County();
					county.setCountyName(array[1]);
					county.setCountyCode(array[0]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����������json����
	 * @param context{�����Ķ���}
	 * @param response{������json����}
	 */
	public static void handleWeatherResponse(Context context, String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherinfo.getString("city");
			String cityCode = weatherinfo.getString("cityid");
			String temp1 = weatherinfo.getString("temp1");
			String temp2 = weatherinfo.getString("temp2");
			String weatherDesp = weatherinfo.getString("weather");
			String publishTime = weatherinfo.getString("ptime");
			//�������ݵ�sharedPreferences��
			saveWeatherInfo(context, cityName, cityCode, temp1, temp2, weatherDesp, publishTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param context 
	 * @param cityName ��������
	 * @param cityCode ������������
	 * @param temp1 ����¶�
	 * @param temp2 ����¶�
	 * @param weatherDesp ��������
	 * @param publishTime ����ʱ��
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String cityCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy��MM��dd��",Locale.CHINA);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putBoolean("selectCity", true);
		editor.putString("cityName", cityName);
		editor.putString("cityCode", cityCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weatherDesp", weatherDesp);
		editor.putString("publishTime", publishTime);
		editor.putString("currentTime", format.format(new Date()));
		editor.commit();
	}
}

package com.coolweather.app.util;

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
}

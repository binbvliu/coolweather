package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	/**
	 * ���ݿ�����
	 */
	public static final  String DB_NAME = "cool_weather";
	/**
	 * ���ݿ�汾
	 */
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	/**
	 * ˽�л����췽��
	 * @param context
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * ��ȡʵ��
	 */
		public synchronized static CoolWeatherDB getInstance(Context context){
			if(coolWeatherDB == null){
				coolWeatherDB = new CoolWeatherDB(context);
			}
			return coolWeatherDB;
		}
		
		/**
		 * ��ʡ��ʵ���洢�����ݿ�
		 */
		public void saveProvince(Province province){
			if(province!=null){
				db.execSQL("insert into province (province_name,province_code) values (?,?)", new Object[]{province.getProvinceName(),province.getProvinceCode()});
			}
		}
		/**
		 * ��ȡ���е�ʡ����Ϣ
		 * @return
		 */
		public List<Province> loadProvinces(){
			List<Province> provinces = new ArrayList<Province>();
			Cursor cursor = db.rawQuery("select * from province", null);
			while(cursor.moveToNext()){
				Province province  = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				provinces.add(province);
			}
			return provinces;
		}
		/**
		 * cityʵ����ŵ����ݿ�
		 * @param city
		 */
		public void insertCity(City city){
			if(city!=null){
				db.execSQL("insert into city (city_name,city_code,province_id) values (?,?,?)", new Object[]{city.getCityName(),city.getCityCode(),city.getProvinceId()});
			}
		}
		
		/**
		 * �����ݿ��ж�ȡʡ���е�����Ϣ
		 * @param provinceId
		 * @return �����б�
		 */
		public List<City> loadCitys(int provinceId){
			List<City> cities = new ArrayList<City>();
			Cursor cursor = db.rawQuery("select * from city where province_id = ?", new String[]{String.valueOf(provinceId)});
			while(cursor.moveToNext()){
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				cities.add(city);
			}
			return cities;
		}
		/**
		 * ��County���ݱ������ݿ�
		 * @param county
		 */
		public void saveCounty(County county){
			if(county!=null){
				db.execSQL("insert into county (county_name, county_code,city_id) values (?,?,?)", new Object[]{county.getCountyName(),county.getCountyCode(),county.getCityId()});
			}
		}
		
		public List<County> loadCounty(int cityId){
			List<County> counties = new ArrayList<County>();
			Cursor cursor = db.rawQuery("select * from county where city_id = ?", new String[]{String.valueOf(cityId)});
			while(cursor.moveToNext()){
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				counties.add(county);
			}
			return counties;
		}
}

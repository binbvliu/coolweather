package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	/**
	 * 创建province表语句
	 */
	public static final String CREATE_PROVINCE = "create table province ("
			+ "id integer primary key autoincrement, "
			+ "province_name text, "
			+ "province_code text)";
	/**
	 * 创建city表的语句
	 */
	public static final String CREATE_CITY = "create table city ("
			+ "id integer primary key autoincrement, "
			+ "city_name text, "
			+ "city_code text, "
			+ "province_id integer)";
	/**
	 * 创建county表语句
	 */
	public static final String CREATE_COUNTY = "create table county ("
			+ "id integer primary key autoincrement,"
			+ "county_name text,"
			+ "county_code text,"
			+ "city_id integer)";
	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_PROVINCE);
			db.execSQL(CREATE_CITY);
			db.execSQL(CREATE_COUNTY);
			Log.e("SQLiteDatabase", "success");
		} catch (Exception e) {
			Log.e("SQLiteDatabase", "error");
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}

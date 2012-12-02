package com.example.dbadapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.gnychis.awmon.DeviceAbstraction.Interface;
import com.gnychis.awmon.DeviceAbstraction.WirelessInterface;

public class WirelessIfaceTable extends DBTable {

	public static String TABLE_NAME = "WIRELESS_IFACE_DATA";
	private static String TABLE_KEY = "ifaceKey";
	
	static List<Field> FIELDS = Arrays.asList(
    		new Field("frequency",	Integer.class,	false),
    		new Field("SSID",		String.class,	false),
    		new Field("BSSID", 		String.class,	false),
    		new Field("ifaceKey",	Integer.class,	true)
    		);
		
	public WirelessIfaceTable(DBAdapter dba) {
		super(dba, TABLE_NAME, FIELDS, TABLE_KEY);
	}
	
	public ArrayList<Object> resultsToObjects(Cursor cursor) {
		ArrayList<Object> interfaces = new ArrayList<Object>();
		
		if(cursor!=null)
			cursor.moveToFirst();
		else
			return interfaces;
		
		do {
			int ifaceKey = cursor.getInt(3);
			Interface iface = _dbAdapter.getInterfaceFromKey(ifaceKey);
			WirelessInterface wiface = new WirelessInterface(iface);
			wiface._frequency = cursor.getInt(0);
			wiface._SSID = 		cursor.getString(1);
			wiface._BSSID =		cursor.getString(2);
			interfaces.add(wiface);
		} while (cursor.moveToNext());
		
		return interfaces;
	}

	@Override
	public ArrayList<ContentValues> getInsertContentValues(Object obj) {
		
		if(obj.getClass()!=WirelessInterface.class)
			return null;
			
		WirelessInterface iface = (WirelessInterface) obj;
		ArrayList<ContentValues> list = new ArrayList<ContentValues>();
		ContentValues values = new ContentValues();
    	
    	for(Field field : _fields) {
    		String key = field._fieldName;
    		
    		if(field._fieldName=="frequency")
    			values.put(key, iface._frequency);
    		
    		if(field._fieldName=="SSID")
    			values.put(key, iface._SSID);
    		
    		if(field._fieldName=="BSSID")
    			values.put(key, iface._BSSID);
    		
    		if(field._fieldName=="ifaceKey")
    			values.put(key, iface.getKey());
    	}
    	list.add(values);
    	return list;
	}
}

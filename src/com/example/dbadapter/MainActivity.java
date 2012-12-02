package com.example.dbadapter;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.dbadapter.DBAdapter.NameUpdate;
import com.gnychis.awmon.DeviceAbstraction.Device;
import com.gnychis.awmon.DeviceAbstraction.Device.Mobility;
import com.gnychis.awmon.DeviceAbstraction.Interface;
import com.gnychis.awmon.DeviceAbstraction.Snapshot;
import com.gnychis.awmon.DeviceAbstraction.WiredInterface;
import com.gnychis.awmon.DeviceAbstraction.WirelessInterface;

public class MainActivity extends Activity {

	DBAdapter dbAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();
        
        Log.d("DBTests", "Interfaces: " + testInterface());
        Log.d("DBTests", "Wired Interface: " + testWiredInterface());
        Log.d("DBTests", "Wireless Interface: " + testWirelessInterface());
        Log.d("DBTests", "Device/Interface Association: " + testDeviceAssociation());
        Log.d("DBTests", "Devices: " + testDevices());
        Log.d("DBTests", "Device Naming: " + testDeviceNaming());
        Log.d("DBTests", "Snapshots: " + testSnapshots());
        Log.d("DBTests", "Morphing: " + testMorphing());
    }
    
    boolean testDeviceNaming() {
    	
    	Device d = new Device();
    	d.setUserName("Name1");
    	dbAdapter.updateDevice(d, NameUpdate.UPDATE);	// Have to insert it first
    	
    	// Make sure it doesn't update if not set to update
    	d.setUserName("Name2");
    	dbAdapter.updateDevice(d, NameUpdate.DO_NOT_UPDATE);
    	Device d2 = dbAdapter.getDevice(d.getKey());
    	if(!d2.getUserName().equals("Name1"))
    		return false;
    	
    	// Now set it to actually update and make sure it changes
    	d.setUserName("Name2");
    	dbAdapter.updateDevice(d, NameUpdate.UPDATE);
    	d2 = dbAdapter.getDevice(d.getKey());
    	if(!d2.getUserName().equals("Name2"))
    		return false;
    	
    	// Try a safe update that should fail
    	d.setUserName("Name3");
    	dbAdapter.updateDevice(d, NameUpdate.SAFE_UPDATE);
    	d2 = dbAdapter.getDevice(d.getKey());
    	if(d2.getUserName().equals("Name3"))
    		return false;
    	
    	// Try a safe update that should succeed
    	d.setUserName(null);
    	dbAdapter.updateDevice(d, NameUpdate.UPDATE);
    	d2 = dbAdapter.getDevice(d.getKey());
    	if(d2.getUserName()!=null)
    		return false;
    	d.setUserName("Name3");
    	dbAdapter.updateDevice(d, NameUpdate.SAFE_UPDATE);
    	d2 = dbAdapter.getDevice(d.getKey());
    	if(!d2.getUserName().equals("Name3"))
    		return false;
    	
    	return true;
    }
    
    boolean testMorphing() {
    	Interface i1 = new Interface();
    	i1._MAC="morphme";
    	i1._IP="192.168.2.1";
    	i1._ouiName="morph OUI";
    	
    	dbAdapter.storeInterface(i1, NameUpdate.UPDATE);
    	
    	// Upgrade it to a wireless interface and make sure it is stored as a wireless interface
    	WirelessInterface w1 = new WirelessInterface(i1);
    	dbAdapter.updateInterface(w1, NameUpdate.UPDATE);
    	if(dbAdapter.getInterface(i1._MAC).getClass() != WirelessInterface.class)
    		return false;
    	
    	// Make it a wired interface and make sure it is still stored as a wired interface
    	WiredInterface wired1 = new WiredInterface(i1);
    	dbAdapter.updateInterface(wired1, NameUpdate.UPDATE);
    	if(dbAdapter.getInterface(i1._MAC).getClass() != WirelessInterface.class)
    		return false;
    	
    	return true;
    }
    
    boolean testSnapshots() {
    	
    	Snapshot s1 = new Snapshot();
    	
    	Interface i1 = new Interface();
        i1._MAC="as:so:ci:at:em:ee";
        i1._IP="192.168.1.4";
        i1._ouiName="I be the OUI4";
        i1._ifaceName="iface name4";
        i1._type=null;
        s1.add(i1);
    	
        dbAdapter.storeSnapshot(s1);
        
        Snapshot s2 = dbAdapter.getSnapshot(s1.getSnapshotTime());
        
        if(s2==null)
        	return false;
        
        if(s2.getInterfaces().size()!=1)
        	return false;
        
        WirelessInterface i2 = new WirelessInterface(Integer.class);
        i2._MAC="wi:re:less";
        i2._IP="127.0.0.1";
        i2._ouiName="I be OUI 2222";
        i2._ifaceName="meh";
        i2.addRSSI(-45);
        i2.addRSSI(-60);
        
        Snapshot s3 = new Snapshot();
        s3.add(i2);
        s3.add(i1);
        s3.setAnchor(i2);
        
        dbAdapter.storeSnapshot(s3);
        
        Snapshot s4 = dbAdapter.getSnapshot(s3.getSnapshotTime());
        
        if(s4==null)
        	return false;
        
        if(s4.getInterfaces().size()!=2)
        	return false;
       
        Snapshot s5 = new Snapshot();
        
        dbAdapter.storeSnapshot(s5);
        
        Snapshot s6 = dbAdapter.getSnapshot(s5.getSnapshotTime());
    	
    	return true;
    }
    
    boolean testDevices() {
    	
    	Device d1 = new Device();
    	d1.setUserName("George");
    	d1.setInternal(true);
    	d1.setMobility(Mobility.MOBILE);
    	
    	dbAdapter.storeDevice(d1, NameUpdate.UPDATE);
    	
    	Device d2 = dbAdapter.getDevice(d1.getKey());
    	
    	if(!d1.equals(d2))
    		return false;
    	
    	d1.setMobility(Mobility.UNKNOWN);
    	
    	if(d1.equals(d2))
    		return false;
    	
    	d1.setMobility(Mobility.MOBILE);
    	
    	Interface i1 = new Interface();
        i1._MAC="as:so:ci:at:em:ee";
        i1._IP="192.168.1.4";
        i1._ouiName="I be the OUI4";
        i1._ifaceName="iface name4";
        i1._type=null;
        d1.addInterface(i1);
        
        if(d1.equals(d2))
        	return false;
        
        dbAdapter.updateDevice(d1, NameUpdate.UPDATE);
        
        d2 = dbAdapter.getDevice(d1.getKey());
        
        if(!d1.equals(d2))
        	return false;
        
        i1._IP="192.168.1.5";
        dbAdapter.updateInterface(i1, NameUpdate.UPDATE);
        
        d2 = dbAdapter.getDevice(d1.getKey());
        
        if(!d2.getInterfaces().get(0)._IP.equals("192.168.1.5"))
        	return false;
        
    	WirelessInterface i2 = new WirelessInterface(WirelessInterface.class);
        i2._MAC="wi:re:le:ss:bu:ya";
        i2._IP="192.168.1.3";
        i2._ouiName="I be the OUI3";
        i2._ifaceName="iface name3";
        i2._type=null;
        i2._frequency=5;
        i2._SSID="SSID";
        i2._BSSID="BSSID";
        i2.setKey(99999);
        d1.addInterface(i2);
        dbAdapter.updateDevice(d1, NameUpdate.UPDATE);
        
        if(dbAdapter.getInternalDevices().size()!=1)
        	return false;
        
        if(dbAdapter.getInternalInterfaces().size()!=2)
        	return false;
        
    	Device d3 = new Device();
    	d3.setUserName("b");
    	d3.setInternal(false);
    	d3.setMobility(Mobility.FIXED);
    	
    	WiredInterface i3 = new WiredInterface();
        i3._MAC="wi:re:di:fa:ce:ya";
        i3._IP="192.168.1.2";
        i3._ouiName="I be the OUI2";
        i3._ifaceName="iface name2";
        i3._type=null;
        i3.setKey(54321);
        i3._gateway=true;
        
        d3.addInterface(i3);
        
        dbAdapter.storeDevice(d3, NameUpdate.UPDATE);
        
        if(dbAdapter.getExternalDevices().size()!=1)
        	return false;
        
        if(dbAdapter.getExternalInterfaces().size()!=1)
        	return false;
        
        Device d4 = dbAdapter.getExternalDevices().get(0);
        Interface i4 = dbAdapter.getExternalInterfaces().get(0);
        
        if(!d4.equals(d3))
        	return false;
        
        if(!i4.equals(i3))
        	return false;
    	
    	return true;
    }
    
    boolean testDeviceAssociation() {
    	Interface i = new Interface();
        i._MAC="as:so:ci:at:em:ee";
        i._IP="192.168.1.4";
        i._ouiName="I be the OUI4";
        i._ifaceName="iface name4";
        i._type=null;
        i.setKey(44444);
        
        dbAdapter.storeInterface(i, NameUpdate.UPDATE);
        
    	dbAdapter.associateInterfaceWithDevice("as:so:ci:at:em:ee", 555555);
    	
    	ArrayList<Interface> ifaces = dbAdapter.getInterfaces(555555);
    	
    	if(ifaces.size()==0)
    		return false;
    	
    	dbAdapter.removeDeviceAssociation("as:so:ci:at:em:ee");
    	
    	return true;
    }
    
    boolean testWirelessInterface() {
    	WirelessInterface i = new WirelessInterface(WirelessInterface.class);
        i._MAC="wi:re:le:ss:bu:ya";
        i._IP="192.168.1.3";
        i._ouiName="I be the OUI3";
        i._ifaceName="iface name3";
        i._type=null;
        i._frequency=5;
        i._SSID="SSID";
        i._BSSID="BSSID";
        i.setKey(99999);
        
        dbAdapter.storeInterface(i, NameUpdate.UPDATE);
        
        WirelessInterface i2 = (WirelessInterface) dbAdapter.getInterface("wi:re:le:ss:bu:ya");
        
        if(!i2.equals(i))
        	return false;
        
        i2._MAC="what";
        
        if(i2.equals(i))
        	return false;
        
        i2 = (WirelessInterface) dbAdapter.getInterface("wi:re:le:ss:bu:ya");
        i2._SSID="";
        
        if(i2.equals(i))
        	return false;
        
        i._SSID="";
        dbAdapter.updateInterface(i, NameUpdate.UPDATE);
        
        i = (WirelessInterface) dbAdapter.getInterface("wi:re:le:ss:bu:ya");
        
        if(!i2.equals(i))
        	return false;
        
        return true;
    }
    
    boolean testWiredInterface() {
    	WiredInterface i = new WiredInterface();
        i._MAC="wi:re:di:fa:ce:ya";
        i._IP="192.168.1.2";
        i._ouiName="I be the OUI2";
        i._ifaceName="iface name2";
        i._type=null;
        i.setKey(54321);
        i._gateway=true;
        
        dbAdapter.storeInterface(i, NameUpdate.UPDATE);
        
        WiredInterface i2 = (WiredInterface) dbAdapter.getInterface("wi:re:di:fa:ce:ya");
        
        if(!i2.equals(i))
        	return false;
        
        i2._MAC="what";
        
        if(i2.equals(i))
        	return false;
        
        i2 = (WiredInterface) dbAdapter.getInterface("wi:re:di:fa:ce:ya");
        i2._gateway=false;
        
        if(i2.equals(i))
        	return false;
        
        i._gateway=false;
        dbAdapter.updateInterface(i, NameUpdate.UPDATE);
        
        i = (WiredInterface) dbAdapter.getInterface("wi:re:di:fa:ce:ya");
        
        if(!i2.equals(i))
        	return false;
        
    	return true;
    }
    
    boolean testInterface() {
    	Interface i = new Interface();
        i._MAC="aa:bb:cc:dd:ee:ff";
        i._IP="192.168.1.1";
        i._ouiName="George's interface";
        i._ifaceName="iface name";
        i._type=null;
        i.setKey(12345);
        
        // Insert an interface, try to get it back, and make sure it's the safe
        dbAdapter.insertInterface(i);
        Interface i2 = dbAdapter.getInterface(i._MAC);
        if(!i.equals(i2))
        	return false;
        
        // Change the IP and make sure they are not the same.        
        i2._IP="192.168.1.7";
        if(i.equals(i2))
        	return false;
        
        // Update the IP address, then get it back from the database
        i._IP="192.168.1.7";
        dbAdapter.updateInterface(i, NameUpdate.UPDATE);
        i = dbAdapter.getInterface("aa:bb:cc:dd:ee:ff");
        if(!i.equals(i2))	// Now they should be here.
        	return false;
        
        // Change the name of i and insert it, but specify no name update.  Make sure
        // that the name doesn't update.
        i._ifaceName="New Name";
        dbAdapter.updateInterface(i, NameUpdate.DO_NOT_UPDATE);
        i2 = dbAdapter.getInterface(i._MAC);
        if(i2._ifaceName.equals("New Name"))	// should not have the updated name
        	return false;
        
        // Now really change the name and make sure they aren't the same.
        i._ifaceName="New Name";
        dbAdapter.updateInterface(i, NameUpdate.UPDATE);
        i2 = dbAdapter.getInterface(i._MAC);
        if(!i2._ifaceName.equals("New Name"))	// should have the updated name
        	return false;
        
        // Now make sure that the name is "New Name"
        if(!i2._ifaceName.equals("New Name"))
        	return false;
        
        // Try a "safe update" when the name is not null
        i._ifaceName="New Name2";
        dbAdapter.updateInterface(i, NameUpdate.SAFE_UPDATE);
        i2 = dbAdapter.getInterface(i._MAC);
        if(!i2._ifaceName.equals("New Name"))	// should have the original name still
        	return false;
               
        // Now update it to null and do a safe update to make sure that it gets overwritten
        i._ifaceName=null;
        dbAdapter.updateInterface(i, NameUpdate.UPDATE);
        i2 = dbAdapter.getInterface(i._MAC);
        if(i2._ifaceName!=null)	// should have the original name still
        	return false;
        i._ifaceName="Safety Name";
        dbAdapter.updateInterface(i, NameUpdate.SAFE_UPDATE);
        i2 = dbAdapter.getInterface(i._MAC);
        if(!i2._ifaceName.equals("Safety Name"))	// should have the original name still
        	return false;
        
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}

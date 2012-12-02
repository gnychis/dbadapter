package com.gnychis.awmon.DeviceAbstraction;

import java.util.Random;

import org.apache.commons.lang3.builder.EqualsBuilder;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Interface implements Parcelable {
	
	public String _MAC;							// The MAC address of the interface, or some address.
	public String _IP;							// The IP address associated to the interface (null if none)
	public String _ouiName;						// The associated manufacturer OUI name (null if none)
	public String _ifaceName;					// A name associated with the specific interface
	public Class<?> _type;						// The interface type (should be a class in HardwareHandlers that extended InternalRadio)
	private int _ifaceKey;						// This is a random long to denote a unique Interface that we can track, ask George for importance

	public Interface() {
		_MAC=null;
		_IP=null;
		_ouiName=null;
		_ifaceName=null;
		_type=null;	
		_ifaceKey = generateKey();
	}
	
	public Interface(Class<?> type) {
		_MAC=null;
		_IP=null;
		_ouiName=null;
		_ifaceName=null;
		_type=type;
		_ifaceKey = generateKey();
	}
	
	public Interface(Interface i) {
		_MAC=i._MAC;
		_IP=i._IP;
		_ouiName=i._ouiName;
		_ifaceName=i._ifaceName;
		_type=i._type;
		_ifaceKey=i._ifaceKey;
	}
	
	@Override
	public boolean equals(Object obj) {
		
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;
        
        Interface iface = (Interface) obj;
        
        return new EqualsBuilder().
                //appendSuper(super.equals(obj)).
                append(_MAC, iface._MAC).
                append(_IP, iface._IP).
                append(_ouiName, iface._ouiName).
                append(_ifaceName, iface._ifaceName).
                append(_type, iface._type).
                append(_ifaceKey, iface._ifaceKey).
                isEquals();
	}
	
	/** Set the key for the interface.  This should really only be used when reading/creating
	 * from the database.  Otherwise it should be set using generateKey();
	 * @param value the value to force the key to.
	 */
	public void setKey(int value) { _ifaceKey=value; }
	
	/** Returns the unique key for the interface which is persistent as interfaces are copied
	 * with broadcasts, merged in to devices, etc.
	 * @return
	 */
	public int getKey() { return _ifaceKey; }
	
	/** This method generates a random long value which can be used for Interface
	 * keys to track them as they get "copied" but we need unique values for them
	 * that are persistent.
	 * @return returns a random long for use as a key
	 */
	public static int generateKey() {
		Random r = new Random();
		return r.nextInt();
	}

	// ********************************************************************* //
	// This code is to make this class parcelable and needs to be updated if
	// any new members are added to the Device class
	// ********************************************************************* //
	public int describeContents() {
		return this.hashCode();
	}

	public static final Parcelable.Creator<Interface> CREATOR = new Parcelable.Creator<Interface>() {
		public Interface createFromParcel(Parcel in) {
			return new Interface(in);
		}

		public Interface[] newArray(int size) {
			return new Interface[size];
		}
	};
	
	public void writeToParcel(Parcel dest, int parcelableFlags) { writeInterfaceToParcel(dest, parcelableFlags); }
	private Interface(Parcel source) { readInterfaceParcel(source); }
	
	public void writeInterfaceToParcel(Parcel dest, int parcelableFlags) {
		dest.writeString(_MAC);
    	dest.writeString(_IP);
    	dest.writeString(_ouiName);
    	dest.writeString(_ifaceName);
    	dest.writeString(_type.getName());
    	dest.writeInt(_ifaceKey);
	}

	public void readInterfaceParcel(Parcel source) {
		_MAC = source.readString();
        _IP = source.readString();
        _ouiName = source.readString();
        _ifaceName = source.readString();
        try {
        _type = Class.forName(source.readString());
        } catch(Exception e) { Log.e("Interface", "Error getting class in Interface parcel"); }
        _ifaceKey = source.readInt();
	}	
}

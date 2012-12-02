package com.gnychis.awmon.DeviceAbstraction;
import java.util.ArrayList;
import java.util.List;


/** The purpose of this class is to be a helper for keeping pairs of interfaces together, mainly
 * used for working with the interface connectivity graph in an easy way.
 * @author George Nychis (gnychis)
 */
public class InterfacePair {

	Interface _left;
	Interface _right;
	
	public InterfacePair(Interface iface1, Interface iface2) {
		_left=iface1;
		_right=iface2;
	}
	
	public List<Interface> asList() {
		List<Interface> list = new ArrayList<Interface>();
		list.add(_left);
		list.add(_right);
		return list;
	}
	
	public Interface getLeft() { return _left; }
	public Interface getRight() { return _right; }
	
}

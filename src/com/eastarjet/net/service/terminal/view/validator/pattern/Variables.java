package com.eastarjet.net.service.terminal.view.validator.pattern;

import java.util.Hashtable;
import java.util.Map;
/*
 * 
 * */
public class Variables 
{
	Map map;
	public Variables(Map map){ this.map=map;}
	public Variables(){this.map=new Hashtable();}
	
	public int 		getInteger(Object k)
	{
		Integer i=(Integer)map.get(k);
		if(i==null) return 0;
		return i.intValue();
	}
	public boolean  getBoolean(Object k)
	{
		Boolean v=(Boolean)map.get(k);
		if(v==null) return false;
		return v.booleanValue();
	}
	public Object 	getObject(Object k)
	{
		return map.get(k);
	}
	
	public String  	getString(Object k)
	{
		String v=(String)map.get(k);
		//if(v==null) return null;
		return v;
	}

	public void 	setInteger(Object k,int i){ map.put(k, new Integer(i));}
	public void		setBoolean(Object k,boolean v){map.put(k, new Boolean(v));}
	public void 	setObject(Object k,Object v){map.put(k, v);}
	
	public Object	remove(Object k)
	{
		return map.remove(k);
	} 
}

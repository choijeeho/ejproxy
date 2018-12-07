package com.eastarjet.net.service.terminal.view.validator;

import java.util.Hashtable;

import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.Validator;

public abstract class AbstractValidator implements Validator 
{
	Hashtable<String,Object> attributes=new Hashtable<String,Object>();
	int target;
	
	public int getTarget(){return target;}
	public void setTarget(int target){this.target=target;}
	
	@Override
	public Object getAttribute(String key) {
		// TODO Auto-generated method stub
		return attributes.get(key);
	}


	@Override
	public void setAttribute(String key, Object value) {
		// TODO Auto-generated method stub
		attributes.put(key, value);
	}

}

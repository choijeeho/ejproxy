package com.eastarjet.net.service.terminal.view;

public interface Validator 
{
	public Object getAttribute(String key);
	public void setAttribute(String key, Object value);
	public int getTarget();
	public void setTarget(int target);
	public boolean hasInterest(Session session,Request request);
	public boolean isHandleable(Session session,Request request);
}//class

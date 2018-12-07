package com.eastarjet.net.service.terminal.view;

import java.util.Iterator;
import java.util.List;

public interface Handler 
{
	public Validator getValidator();
	public void setValidator(Validator validator);
	public boolean hasInterest(Session ses,Request req);
	public boolean hasChildHandler(Session ses);
	public boolean isHandleable(Session ses,Request req);
	public boolean isWorking(Session ses,Request req);
	public boolean handleRequest(Session ses,Request req,Response resp);
	public List<Handler> getHandlers();
	public void addHandler(Handler hd);
	public void removeHandler(Handler hd);
	public int getTarget();
	public void setTarget(int target);
	public String getID();
	public void setID(String id);
	public void setAttribute(String k,Object v);
	public Object getAttribute(String k);
	
}//interface

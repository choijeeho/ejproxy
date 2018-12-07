package com.eastarjet.net.service.terminal.view.handler;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.eastarjet.net.service.terminal.view.Handler;
import com.eastarjet.net.service.terminal.view.Request;
import com.eastarjet.net.service.terminal.view.Response;
import com.eastarjet.net.service.terminal.view.Session;
import com.eastarjet.net.service.terminal.view.Validator;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * 
 * @author clouddrd
 *
 */
public abstract class AbstractHandler implements Handler 
{
	static Logger log = Toolkit.getLogger(AbstractHandler.class);
	Validator validator;
	List<Handler> handlers = new LinkedList<Handler>();
	Hashtable<String,Object> attributes = new Hashtable<String,Object>();
	
	int target;
	String id;
	
	
	@Override
	public String getID(){return id;}

	@Override
	public void setID(String id){this.id=id;}

	@Override
	public int getTarget(){return target;}
	
	@Override
	public void setTarget(int type){this.target=target;}
	
	public Validator getValidator(){return validator;}
	public void setValidator(Validator validator)
	{ this.validator=validator;}

	public boolean hasChildHandler(Session session)
	{
		return handlers.size()>0;
	}
	
	public void setWorking(Session session ,boolean v)
	{
		session.setAttribute(this,"isWorking",""+v);
	}
	
	public boolean isWorking(Session session,Request request)
	{
		return "true".equals(session.getAttribute(this,"isWorking"));
	}

	
	@Override
	public boolean isHandleable(Session session,Request request)
	{
		if(log.isDebugEnabled()) log.debug("handler:"+getID());
		
		int v=validator.getTarget();
		if(v!=0 && v!=session.getTarget()) return false;
		return validator.isHandleable(session, request);
	}

	@Override
	public boolean hasInterest(Session session, Request request) 
	{
		// TODO Auto-generated method stub
	
		int v=validator.getTarget();
		if(v!=0 && v!=session.getTarget()) return false;
		return validator.hasInterest(session, request)	;
	}
	
	@Override
	public void addHandler(Handler hd) 
	{
		// TODO Auto-generated method stub
		handlers.add(hd);
	}

	@Override
	public List<Handler> getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}



	@Override
	public void removeHandler(Handler hd) 
	{
		// TODO Auto-generated method stub
		handlers.remove(hd);
	}
	
	@Override
	public boolean handleRequest(Session session,Request request,Response response)
	{
		int v=getTarget();
		int st=session.getTarget();
		if(v!=0 && v!=st) return false;
		return handleTargetRequest(st,session,request,response);
	}
	
	public abstract boolean handleTargetRequest(int target,Session session,Request request,Response response);
	

	public void setAttribute(String k,Object v){ attributes.put(k,v);}
	public Object getAttribute(String k){ return attributes.get(k);}
}//class

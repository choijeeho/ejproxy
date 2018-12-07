package com.eastarjet.net.service.terminal.view;

import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import com.eastarjet.net.service.Service;
import com.eastarjet.net.service.terminal.view.handler.ViewHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class Session 
{
	
	
	protected Hashtable<String, Object> attributes = 
				new Hashtable<String, Object>();
	protected  Phase [] phases = new Phase[2];
	Stack[] stacks= new Stack[2];
	protected int index=0;
	protected ViewService service;
	
	Request [] requests = new Request [2];
	Response [] responses = new Response [2];
	
	public final static int BOTH=0;
	public final static int INPUT=1;
	public final static int OUTPUT=2;
	
	final static int INPUT_INDEX=0;
	final static int OUTPUT_INDEX=1;
	
	ViewManager viewManager;
	View  currentView;
	View  nextView;
	ViewManager manager;
	static Logger log = Toolkit.getLogger(Session.class);

	
	public Session(ViewService service)
	{
		this.service=service;
		phases[INPUT_INDEX]=new Phase();
		phases[OUTPUT_INDEX]=new Phase();
		
		stacks[INPUT_INDEX]=new Stack();
		stacks[OUTPUT_INDEX]=new Stack();
	}

	public ViewService getService(){return service;}
	
	public void setViewManager(ViewManager viewManager)
	{this.viewManager=viewManager;}
	
	public void setRequestResponse(int target, Request req,Response res)
	{
		int tindex=target-1;
		requests[tindex]=req;
		responses[tindex]=res;
	}
	
	public Request getRequest(int target)
	{
		return requests[target-1]; 
	}

	public Response getResponse(int target)
	{
		return responses[target-1]; 
	}
	

	public int getTarget(){return index+1;}
	
	public void setCurrentView(String viewid)
	{
		currentView=(View)viewManager.getHandler(viewid);;
		index=INPUT_INDEX;
		attributes.clear();
		phases[INPUT_INDEX].clear();
		phases[INPUT_INDEX].setHandlers(currentView.getHandlers());
		phases[OUTPUT_INDEX].clear();
		phases[OUTPUT_INDEX].setHandlers(currentView.getHandlers());
	}
	
	
	public void setNextView(String view)
	{
		if(log.isDebugEnabled()) log.debug(">>>> NextView ID:"+view);
		nextView=(View)viewManager.getHandler(view);	
	}
	
	public Handler getHandler(String id)
	{
		return viewManager.getHandler(id);
	}
	
	public boolean hasNextView()
	{	return nextView!=null;	}
	
	public View currentView(){ return currentView;}
	
	public Handler goFirstView()
	{
		//View view = viewManager.getStartView();
		nextView= viewManager.getStartView();
		nextView();
		return phases[index].currentHandler;
	}
	
	public void nextView()
	{ 
		if(log.isDebugEnabled())	log.debug(">>>> NextView ID="+nextView.getID());
		currentView=nextView;	
		nextView=null;
		phases[INPUT_INDEX].clear();
		phases[OUTPUT_INDEX].clear();
		phases[INPUT_INDEX].goFirstHandler();
		phases[OUTPUT_INDEX].goFirstHandler();
		phases[INPUT_INDEX].setHandlers(currentView.getHandlers());
		phases[OUTPUT_INDEX].setHandlers(currentView.getHandlers());
		
	}
	

	public Object getAttribute(String key)
	{ return attributes.get(key);}
	public void  setAttribute(String key,Object v)
	{  attributes.put(key,v);}
	public void  removeAttribute(String key)
	{  attributes.remove(key);}
	
	
	public void setTarget(int target){this.index=target-1;}
	
	public void goFirstHandler()
	{	phases[index].goFirstHandler();	}
	
	public Handler nextHandler()
	{	return phases[index].nextHandler();	}

	public boolean hasWaitHandler()
	{	return phases[index].waitHandler!=null;	}
	
	public void setWaitHandler(Handler handler)
	{
		phases[index].waitHandler=handler;	
	}
	
	public void setWaitHandler(int target,Handler handler)
	{
		phases[target-1].waitHandler=handler;	
	}
	
	public Handler forceWaitHandler()
	{
		phases[index].currentHandler=phases[index].waitHandler;
		phases[index].waitHandler=null;
		phases[index].handlerIndex=0;
		return phases[index].currentHandler; 
	}
	

	
	public Handler currentHandler()
	{	return phases[index].currentHandler;	}
	
	public void clearCurrentHandler()
	{
		phases[index].clear();
	}

	
	public int getIntAttribute(Object validator, String key)
	{
		Integer v=((Integer)phases[index].getAttribute(validator.toString()+key));
		if(v==null)  return 0;
		return v.intValue();
	}
	
	public void setIntAttribute(Object validator, String key,int v)
	{ 
		phases[index].setAttribute(validator.toString()+key,new Integer(v));
	}

	public Object getAttribute(Object validator, String key)
	{ return phases[index].getAttribute(validator.toString()+key);}
	
	public void  setAttribute(Object validator,String key,Object v)
	{ phases[index].setAttribute(validator.toString()+key,v); }
	public void  removeAttribute(Object validator,String key )
	{ phases[index].removeAttribute(validator.toString()+key); }
		
	
	public Handler pop()
	{
	
		if(stacks[index].isEmpty()) return null;
		
		Phase phase=(Phase)stacks[index].pop();
		phases[index]=phase;
			
		return phase.currentHandler;
	}
	
	public Handler push()
	{
		stacks[index].push(phases[index]);
		Phase phase=phases[index];
		List <Handler>  handlers=  phase.handlers.get(phase.handlerIndex).getHandlers();
		Handler handler=handlers.get(0);
		Phase nphase=new Phase();
		nphase.clear();
		
		phases[index]=nphase;
		nphase.currentHandler=handler;
		
		return handler;
	}
	

	class Phase
	{
		Handler currentHandler;
		Handler waitHandler;
		int handlerIndex=0;

		List<Handler> handlers;
		Hashtable<String , Object> attributes=new Hashtable<String,Object>();
		
		
		public void setHandlers(List<Handler> handlers)
		{this.handlers=handlers;}
		
		public void clear()
		{
			currentHandler=null;
			//waitHandler=null;
			attributes.clear();
			//handlerIndex=0;
		}
		
		public void goFirstHandler()
		{
			handlerIndex=0;
		}
		
		public Handler nextHandler()
		{
			if(waitHandler!=null)
			{ currentHandler=waitHandler;   waitHandler=null; return currentHandler; }
			
			if(handlerIndex >= handlers.size()) currentHandler = null;
			else currentHandler=handlers.get(handlerIndex++);
			return currentHandler;
		}
		
	 
		
		
		
		public Object getAttribute(String key){ return attributes.get(key);}
		public void  setAttribute(String key,Object v)
		{  attributes.put(key,v);}
		public void removeAttribute(String key)
		{ attributes.remove(key);}

	}//class

}//class



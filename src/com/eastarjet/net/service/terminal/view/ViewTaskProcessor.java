package com.eastarjet.net.service.terminal.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import com.eastarjet.net.service.Connection;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class ViewTaskProcessor 
{
	static Logger log = Toolkit.getLogger(ViewTaskProcessor.class);
	View rootView;
	protected Request sourceRequest;
	protected Response sourceResponse;

	protected Connection source;
	
	public void initialize(Session session) throws IOException
	{
		//session.setSource(source);
	}
	
	public ViewTaskProcessor(Connection source)
	{
		this.source=source;
	}

	
	public Iterator select(Session session) throws IOException
	{
		return null;
	}
	
	public int readRequest(Session session,Object source) throws IOException
	{
		return 0;
	}
	
	public void clearSelector() throws IOException
	{
		
	}
	
	public boolean processRequest(Session session, int type)
	{
		return processRequest(session,sourceRequest,sourceResponse);
	}
	
	
	void debug(Handler handler,Session session,Request request)
	{
		if(log.isTraceEnabled()) 
		{
			int ch=request.peek();
			int pos = request.getPeekPosition();
			int ppos = request.getPollPosition();
			int epos = request.getEndPosition();
			boolean b= request.isPeekable();
			log.trace("ch["+pos+"/"+ppos+"/"+epos+"]='"+(char)ch+"'target="+session.getTarget()+",handler="+handler);
		}		
	}
	
	long prevTime=0;
	protected boolean processRequest(Session session,
			Request request,Response response)
	{
		boolean ret=true;
		
		Handler handler=session.currentHandler();
		long tm = System.currentTimeMillis();
		if(tm-prevTime > 5000)
		{
			log.debug("id="+session.currentView().getID() + "."+((handler==null)?"":handler.getID()));
			prevTime=tm;
			
		}
		while(!request.isEmpty())
		{
			//if there is no current handler,
			

			if(handler==null)
			{
				handler=session.nextHandler();
				request.markPeek();
				
				//@debug(handler,session,request);
				
				//if no more handler
				if(handler==null)
				{
					try{
						handler=session.pop();
					}catch(RuntimeException e)
					{ log.debug("Target"+session.getTarget(),e);}
					if(handler==null)
					{
						if(log.isInfoEnabled())
								log.info("goto FirstView");
						handler=session.goFirstView();
						ret=false; break;
					}
					
					continue; //go to inspect next token
				}//if handler
			}
			//@ else debug(handler,session,request);
			
			if(!handler.isWorking(session,request))
			{
				
				//if handler don't have interest
				if(!handler.hasInterest(session, request))
				{
					handler=null; session.clearCurrentHandler();
					request.resetPeek();
					
					continue;//go to next Handler
				}

				//debug(request);
				
				if(!handler.isHandleable(session, request))
				{

					//debug(handler,session,request);
					
					if(!request.incPeek())
					{
						break; //go to wait next packet
					}
					continue; // go to next token for current handler
				}
				
			}//working
			
			
			if(handler.hasChildHandler(session))
			{
				request.resetPeek();
				handler=session.push();
				continue;
			}//if
			
			int tv=handler.getTarget();
			
			if(tv!=0 && tv != session.getTarget())
			{ 
				session.clearCurrentHandler();
				session.setWaitHandler(handler.getTarget(),handler);
				handler=null;
				continue;//
			}
			
				
			 
			if(handler.handleRequest(session, request, response))
				continue;
			
			//move first Handler
			handler=null; 
			session.clearCurrentHandler();
			session.goFirstHandler();//
			
			//if next view, clear all state and move view
			if(session.hasNextView())
			{	
				handler=null; 	session.nextView();
				request.resetPeek();
				continue;
			}
			
			//if wait handler is exist, currenrHandler and cur index is cleared
			if(session.hasWaitHandler())
			{	
				handler=session.forceWaitHandler();	
			}
			
		}//while
		
		response.flush();
		return ret;
	}//method

}//class

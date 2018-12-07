package com.eastarjet.net.service.terminal.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.eastarjet.net.service.terminal.view.handler.AbstractHandler;

public abstract class View extends AbstractHandler 
{
 
	List<View> views= new LinkedList<View>();
	
	
	public boolean handleTargetRequest(int target,Session session,Request request, 
			Response response)
	{
		//session.setNextView(nextView);
		return false;
	}

	
	public abstract void initialize(Session session);
	public abstract void update(Session session);
	public abstract void finalize(Session session);
	
	
	public void addView(View view){ views.add(view);}
	public List<View> getNextViews(){return views;}
}


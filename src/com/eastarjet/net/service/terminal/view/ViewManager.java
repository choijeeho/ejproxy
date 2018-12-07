package com.eastarjet.net.service.terminal.view;

import java.util.Map;
import java.util.Properties;

import com.eastarjet.net.service.terminal.view.config.ViewConfigure;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class ViewManager 
{

	Properties attributes;
	ViewConfigure configure;
	View startView;
	static Logger log= Toolkit.getLogger(ViewManager.class);
	Map<String,Handler> handlers;
	
	public void load(String conf) throws Exception
	{
		String viewConfig=conf;
		if(log.isInfoEnabled()) log.info(">>>> VewConfig is going to be loaded :"+viewConfig);
		configure = new ViewConfigure(viewConfig);
		
		handlers=configure.load();//attributes
		String sv=configure.getStartView();
		startView=(View)handlers.get(sv);
		
	}
	
	public Handler getHandler(String viewid)
	{ return handlers.get(viewid);	}
	
	public View getStartView()
	{return startView;}
	
	public String getStartViewName()
	{ return startView.getID();}
	
}//class

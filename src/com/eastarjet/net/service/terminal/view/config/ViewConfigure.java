package com.eastarjet.net.service.terminal.view.config;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import com.eastarjet.net.service.terminal.view.DefaultView;
import com.eastarjet.net.service.terminal.view.Handler;
import com.eastarjet.net.service.terminal.view.Validator;
import com.eastarjet.net.service.terminal.view.View;
import com.eastarjet.net.service.terminal.view.handler.DefaultHandler;
import com.eastarjet.net.service.terminal.view.handler.ReferenceHandler;
import com.eastarjet.net.service.terminal.view.handler.ViewReferenceHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.Converter;
import com.eastarjet.util.log.Logger;


/** *****************************************************************
 * <pre>
 * ViewConfiture make real view tree from virtual view tree
 * 
 * 	  default config file in classpath : /viewconfig.xml
 *    viewNode -1 validatorNode
 *             -* HandlerNode
 *             -* Reference(view,handler)    
 * </pre>
 *    
 * 
 * @author clouddrd
 * @since 2010.10
 *
 ***************************************************************** */

public class ViewConfigure 
{
	ConfigLoader loader;
	String startView;
	Map<String,Handler> handlers = new Hashtable<String,Handler>();
	List<ReferenceHandler> references =new LinkedList<ReferenceHandler>();
	
	private final static Logger log = Toolkit.getLogger(ViewConfigure.class);
	
	public ViewConfigure()
	{	this("/viewconfig.xml");	}
	
	public ViewConfigure(String filename)
	{
		loader= new ConfigLoader(filename);
	}
	
	public String getStartView(){ return startView;}
	
	public Map<String,Handler> load() throws Exception
	{
		Map<String,ViewNode> views =loader.load();
		startView=loader.getStartView();
		
		if(log.isInfoEnabled())log.info(">>>> Views Model will be made");
		Map<String,Handler> ret=makeRealTree(views);
		return ret;
	}
	
	
	Map<String,Handler> makeRealTree(Map<String,ViewNode> nodes) throws Exception
	{
	
		Iterator<ViewNode> it= nodes.values().iterator();
		
		while(it.hasNext())
		{
			ViewNode nd=it.next();
			if(log.isDebugEnabled()) log.debug("id="+nd.id);
			
			View view = createView(nd);
			putHandler(nd.id,view);
		}//whie
		
		rearrange(references);
		return handlers;
	}
	
	void putHandler(String id,Handler hd) throws Exception
	{
		if(handlers.get(id)!=null) throw new Exception("the id already exist : "+id); 
		handlers.put(id,hd);
	}
	
	
	
	View createView(ViewNode node) throws Exception
	{
		View ret=null;
		 String clazz=node.className;
		 
		 if(clazz==null)  ret= new DefaultView();
		 else ret=(View)Toolkit.createInstance(clazz);
		 
		 initializeHandler(ret,node);
		 
		return ret;
	}	
	
	
	Handler createHandler(HandlerNode node) throws Exception
	{
		Handler ret=null;
		 String clazz=node.className;
		 
		 if(clazz==null)  ret= new DefaultHandler();
		 else ret=(Handler)Toolkit.createInstance(clazz);
		 if(node.id!=null) putHandler(node.id,ret);
		 
		 initializeHandler(ret,node);
		 return ret;
	}
	
	int getTarget(String v)
	{
		if("output".equals(v)) return 2;
		if("out".equals(v)) return 2;
		if("input".equals(v)) return 1;
		if("in".equals(v)) return 1;
		return 0;
	}
	
	/**
	 * //index := -1:last, 1: first, 0: low priority
	 * 
	 * @param handler
	 * @param node
	 * @throws Exception
	 */
	void initializeHandler(Handler handler,HandlerNode node) throws Exception
	{
		if(node.id!=null) handler.setID(node.id);
		int target=getTarget(node.target);
		handler.setTarget(target);
		
		 Enumeration<String> e=node.attributes.keys();
		 
		 while(e.hasMoreElements())
		 {
			 String key=e.nextElement();
			 Object v=node.attributes.get(key);
			 handler.setAttribute(key,v);
		 }
		
		if(node.validator!=null)
		{
			Validator v = createValidator(node.validator);
			if(v!=null) handler.setValidator(v);
		}

		
		// Handler ========================
		List <Handler> thandlers= new LinkedList<Handler>();
		
		//-1 last, 1: first, 0: low priority
		Handler last=null;
		Handler first=null;
		 Iterator<HandlerNode> it = node.handlers.iterator();
		 while(it.hasNext())
		 {
			 HandlerNode hnd= it.next();
			
			 Handler chandler=createHandler(hnd);
			 if(hnd.index==HandlerNode.LAST) last=chandler;
			 else if(hnd.index==HandlerNode.FIRST) first=chandler;
			 else thandlers.add(chandler);
		 }//while
		 
		 
		// Reference ========================
		Iterator<ReferenceNode> its= node.nexts.iterator();
		while(its.hasNext())
		{
			ReferenceNode nd= its.next();
			ReferenceHandler h=null;
			if(nd.type==ReferenceNode.TYPE_HANDLER)
			{
				h = new ReferenceHandler();
			}
			else
			{
				h = new ViewReferenceHandler();
			}
			
			if(nd.id==null) throw new Exception("config don't hava Reference ID at <reference id=''>");
			references.add(h);
			h.setReferenceID(nd.id);
			if(nd.validatorState==nd.DISABLE) 
				h.setValidatorEnabled(false);
			else h.setValidatorEnabled(true);
			
			if(nd.index==HandlerNode.LAST) last=h;
			else if(nd.index==HandlerNode.FIRST) first=h;
			else thandlers.add(h);
		}//while
		
		if(last!=null) thandlers.add(last);
		if(first!=null)thandlers.add(0, first);
		
		//copy to handler
		Iterator<Handler> its2 = thandlers.iterator();
		while(its2.hasNext())
		{
			Handler hd=its2.next();
			handler.addHandler(hd);
		}//while
		//if(first!=null) handler.addHandler(last);
	}//method
	
	Validator createValidator(ValidatorNode node) throws Exception
	{
		Validator ret=null;
		
		String clazz=node.className;
		 
		 if(clazz!=null)
		 {
			 ret=(Validator)Toolkit.createInstance(clazz);
			 initializeValidator(ret,node);
		 }
		
		return ret; 
	}
	
	void initializeValidator(Validator validator,ValidatorNode vnode)
	{
		 //vnode.target;
		int target=getTarget(vnode.target);
		 
		 validator.setTarget(target);

		 
		 Enumeration<String> e=vnode.attributes.keys();
		 
		 while(e.hasMoreElements())
		 {
			 String key=e.nextElement();
			 Object v=vnode.attributes.get(key);
			 validator.setAttribute(key,v);
		 }
	}//method

	
	void rearrange( List<ReferenceHandler> hds) throws Exception
	{
		Iterator<ReferenceHandler> it=hds.iterator();
		
		while(it.hasNext())
		{
			ReferenceHandler rhd=it.next();
			String id= rhd.getReferenceID();
			Handler hd=handlers.get(id);
			if(hd==null)
				throw new Exception("Handler not found : '"+id+"'");
			
			rhd.setReferenceHandler(hd);
			
		}//while
	}
	//void initializeNode(Handler )
}//class

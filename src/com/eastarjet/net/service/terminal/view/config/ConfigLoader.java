package com.eastarjet.net.service.terminal.view.config;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

 
import com.eastarjet.net.service.terminal.view.View;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.Converter;
import com.eastarjet.util.XMLConfig;
import com.eastarjet.util.log.Logger;


/**
 * <pre>ConfigLoader make  virtual view tree from config
 * 
 * 	  default config file in classpath : /viewconfig.xml
 *    viewNode -1 validatorNode
 *             -* HandlerNode
 *             -* Reference(view,handler)    
 * </pre>
 *    
 * 
 * @author clouddrd
 *
 */

public class ConfigLoader 
{
	String configFile;
	Map<String,ViewNode> views;
	Properties attributes=new Properties();
	String startView;
	
	XMLConfig config;
	private final static Logger log = Toolkit.getLogger(ConfigLoader.class);
	
	public ConfigLoader()
	{	configFile="/viewconfig.xml";	}
	
	public ConfigLoader(String filename)
	{
		configFile=filename;
	}
	
	public String getStartView(){return startView;}
	
	public Map<String,ViewNode> load() throws Exception
	{
		views=new Hashtable<String,ViewNode>();
		parseConfig();
		return views;
	}
	
	void parseConfig() throws Exception
	{
		 config = new XMLConfig(configFile);
		 
		Node root=config.getNode("/ViewConfig/views");
		if(root.getAttributes().getNamedItem("start")==null)
			throw new Exception("No start view in <views start='startView'>");
		
		startView= root.getAttributes().getNamedItem("start").getNodeValue();
		if(log.isDebugEnabled())log.debug("startView="+startView);

		NodeList list=config.getNodes("/ViewConfig/views/view");
		int sz=list.getLength();
		
		if(log.isDebugEnabled())log.debug("views count ="+sz);
		
		for(int i=0;i<sz;i++)
		{
			Node node= list.item(i);
			ViewNode view=new ViewNode();
			parseView(view,node);
			views.put(view.id, view);
		}//for
	}//method
	

	/** *************************************************************
	 * <pre>
	 * view has attributes like [id, target, validator, validatorTarget]
	 *   id : type is String and unique key in views
	 *   class : class Name
	 *   target : which is handled mainly  on input or  output stream
	 *            (in,out,input,output, both)
	 *   validator : validtor class name
	 *   validatorTarget : in, out, input, output, both      
	 * view has elements like [validator,handler,nexts]
	 *  
	 * </pre>
	 * 
	 * 
	 * @param node
	 * @return ViewNode
	 ************************************************************* */
	void parseView( ViewNode view, Node node ) throws Exception
	{ 
		parseHandler(view,node);
		if(view.id==null)
			throw new Exception("View ID doesn't exist !!");
		
	}

 
	/** *************************************************************
	 * <pre>
	 *  &lt;handler id='tid' target='input' class='com.hd.className' 
	 *      validator='com.ad.ClassName' validatorTarget='output' &gt;
	 *      &lt;validator class='com.vd.ClassName' target='output'/&gt;
	 *      &lt;handler ... /&gt;
	 *      &lt;attribute name='attr1' value='value1' &gt;
	 *      &lt;attribute name='attr1' value='value1' &gt;
	 *  &lt;/handler&gt;    
	 * </pre>
	 * @param node
	 * @param xmlnode
	 ************************************************************* */
	void parseHandler(HandlerNode node,Node xmlnode)
	{
		parseNode(node,xmlnode);
		//parse index;
		String sindex=config.getValue(xmlnode,"index");
		if(sindex!=null)
		{
			//-1 last, 1: first, 0: low priority
			if(sindex.equals("last"))node.index=-1;
			else if(sindex.equals("first"))node.index=1;
			else node.index= Converter.parseInt(sindex);
		}//if
		
		if(log.isDebugEnabled()) log.debug("node["+node.id+"].index="+node.index);
		
		//validator
		Node validator=config.getNode(xmlnode,"validator");
		if(log.isDebugEnabled()) log.debug("validator node="+validator);
		
		if(validator!=null)
		{
			ValidatorNode vnode = new ValidatorNode(); 
			parseNode(vnode,validator);
			node.validator=vnode;
		}
		

		//handlers
		NodeList handlers=config.getNodes(xmlnode,"handler");
		if(log.isDebugEnabled()) log.debug("handlers="+handlers.getLength());
		int len=handlers.getLength();
		for(int i=0;i<len;i++)
		{
			HandlerNode hnode=new HandlerNode();
			parseHandler(hnode,handlers.item(i));
			node.handlers.add(hnode);
		}//for
		
		//nexts
		NodeList nexts=config.getNodes(xmlnode,"reference");
		if(log.isDebugEnabled()) log.debug("references="+nexts.getLength());
		len=nexts.getLength();
		for(int i=0;i<len; i++)
		{
			ReferenceNode next=new ReferenceNode(); 
			parseNext(next,nexts.item(i));
			node.nexts.add(next);
		}
		
	}//method
	
	/** *************************************************************
	 * <pre>
	 * Node likes blow.
	 * 	 &lt;XXX id="nodeid" target="input" validator="myvalid" validatorTarget="input" &gt;
	 * 			&ltattribute name="aname" value="avalue" /&gt;
	 *       &lt;/XXX&gt;
	 * </pre>
	 * @param node is result
	 * @param xmlnode is config source
	 * *************************************************************
	 */
	void parseNode(com.eastarjet.net.service.terminal.view.config.ConfigNode node,Node xmlnode)
	{
		//id
		String id=config.getValue(xmlnode,"id");
		if(log.isDebugEnabled()) log.debug("node.id="+id);
		node.id=id;
		
		//target
		String target=config.getValue(xmlnode,"target");
		node.target=target;
		if(log.isDebugEnabled()) log.debug("node["+id+"].target="+target);
		
		//class
		String clazz =config.getValue(xmlnode,"class");
		node.className=clazz;
		if(log.isDebugEnabled()) log.debug("node["+id+"].class="+clazz);
	
		//attributes(xml elements like "<attribute name="aaa" value="avalue"/>")
		NodeList attributes=config.getNodes(xmlnode,"attribute");
		if(log.isDebugEnabled()) log.debug("node["+id+"].attributes.size="+attributes.getLength());
		
		int len=attributes.getLength();
		for(int i=0;i<len;i++)
		{
			Node attr=attributes.item(i);
			String aname=attr.getAttributes().getNamedItem("name").getNodeValue();
			String avalue=attr.getAttributes().getNamedItem("value").getNodeValue();
			if(log.isDebugEnabled()) log.debug("view["+id+"].attributes={"+aname+","+avalue+"}");
			if(!node.attributes.containsKey(aname))
			{
				node.attributes.put(aname,avalue);
			}
			else
			{
				Object v= node.attributes.get(aname);
				Vector<String> l;
				if(v instanceof String)
				{
					l=new Vector<String>() ;
					l.add((String)v);
					node.attributes.put(aname, l);
				}
				else l=(Vector)v;
				
				l.add(avalue);
				
			}
		}
	}//method
	
	
	/** *************************************************************
	 * <pre>
	 *   'next' tag has 'validator' attribute
	 *   if validator's value has 'disable',
	 *   validator must be disabled.
	 *   
	 *   &lt;next validator="disable" /&gt;
	 * </pre>
	 * 
	 * @param nextViewRef
	 * @param xmlnode
	 ************************************************************* */
	void parseNext(ReferenceNode nextViewRef, Node xmlNode)
	{
		parseNode(nextViewRef,xmlNode);

		String s=config.getValue(xmlNode, "validator");
		
		if(s!=null && "DISABLE".equals(s.toUpperCase()))
		{ 
			nextViewRef.validatorState=ReferenceNode.DISABLE;
		}//if
		
		String t=config.getValue(xmlNode, "type");
		if(t!=null && "HANDLER".equals(t.toUpperCase()))
		{ 
			nextViewRef.type=ReferenceNode.TYPE_HANDLER;
		}
		else nextViewRef.type=ReferenceNode.TYPE_VIEW;
		//if
		if(log.isDebugEnabled()) log.debug("reference["+nextViewRef.id+"],validator="+s+",type="+t);
	}

}//class

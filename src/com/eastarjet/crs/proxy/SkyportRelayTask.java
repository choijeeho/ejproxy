package com.eastarjet.crs.proxy;



import com.eastarjet.crs.proxy.skyport.PrintingHandler;
import com.eastarjet.net.service.relay.RelayHandler;
import com.eastarjet.net.service.task.AdvancedRelayTask;


public class SkyportRelayTask extends AdvancedRelayTask 
{
	RelayHandler handler = new PrintingHandler();
	public SkyportRelayTask(){}
	
	public RelayHandler getTargetHandler(){return handler;}
	
}//class



//class DefaultHandler implements Handler{}

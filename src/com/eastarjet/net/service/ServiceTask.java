package com.eastarjet.net.service;

import java.nio.channels.SocketChannel;

public interface ServiceTask 
{
	public Service getService();
	public void setService(Service service);
	public String getTitle();
	public void setTitle(String title);
//	public void setSourceSocket(Object socket);
	public void initialize(Service service)throws Exception;
	public void finalize(Service service)throws Exception;
	public void doWork();
}//interface

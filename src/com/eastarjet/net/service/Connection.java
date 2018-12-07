package com.eastarjet.net.service;

import java.io.IOException;

public interface Connection 
{
	public Object getSocket();
	public boolean isConnect();
	public String getTargetAddress();
	public int getTargetPort();
	public void connect()throws IOException;
	public void close() throws IOException;
	
}//interface

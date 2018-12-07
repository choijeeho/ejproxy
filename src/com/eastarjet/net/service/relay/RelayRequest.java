package com.eastarjet.net.service.relay;

public interface RelayRequest 
{
	public RelaySession getSession();
 
	public RelayBuffer getBuffer();
	
	public void setNextHandlePosition(int pos);
	public int getNextHandlePosition();

}

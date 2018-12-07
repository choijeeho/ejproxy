package com.eastarjet.net.service.relay.imp;

import java.nio.channels.SocketChannel;

import com.eastarjet.net.service.relay.RelayBuffer;
import com.eastarjet.net.service.relay.RelayRequest;
import com.eastarjet.net.service.relay.RelaySession;

public class DefaultRelayRequest implements RelayRequest 
{

	SocketChannel socket;
	RelaySession session;
	RelayBuffer buffer;
	
	public DefaultRelayRequest(RelaySession session)
	{
		this.session = session;
		buffer = new RelayBuffer();
	}
	
	public void clear()
	{
		socket=null;
		buffer.clear();
	}
	
	public void setSocket(SocketChannel socket)
	{this.socket=socket;}
	
 
	
	@Override
	public RelayBuffer getBuffer() {
		// TODO Auto-generated method stub
		return buffer;
	}

	@Override
	public RelaySession getSession() {
		// TODO Auto-generated method stub
		return session;
	}
	
	int nextPos=-1;;
	public void setNextHandlePosition(int pos){nextPos=pos;}
	public int getNextHandlePosition(){return nextPos;}


}

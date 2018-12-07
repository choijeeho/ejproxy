package com.eastarjet.net.service.relay;

import java.io.IOException;

public class DefaultRelayHandler implements RelayHandler 
{

	public DefaultRelayHandler()
	{}
	
	public int getValidCount(RelaySession session,RelayBuffer buf)
	{
		return 0;
	}
	

	
	
	@Override
	public void handleRequest(RelayRequest request, RelayResponse response)
			throws IOException 
	{
		// TODO Auto-generated method stub
		
		RelayBuffer buf= request.getBuffer();
		response.writeAll(buf,0,buf.length());
	}

	@Override
	public boolean hasEnoughData(RelaySession session,RelayBuffer buf) throws IOException {
		// TODO Auto-generated method stub
		return true;
	}

}

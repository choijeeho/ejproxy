package com.eastarjet.net.service.relay;

import java.io.IOException;

public interface RelayHandler 
{
	public int  getValidCount(RelaySession session, RelayBuffer buf);
	public boolean hasEnoughData(RelaySession session, RelayBuffer buf) throws IOException;
	public void handleRequest(RelayRequest request,RelayResponse response) throws IOException;
	
}

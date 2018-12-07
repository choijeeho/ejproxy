package com.eastarjet.net.service.relay;

public interface RelaySession 
{
	public RelayRequest getSourceRequest();
	public RelayResponse getSourceReponse();
	
	public RelayRequest getTargetRequest();
	public RelayResponse getTargetReponse();
	
	public RelayHandler getHandler();
	public void setHandler(RelayHandler handler);
}

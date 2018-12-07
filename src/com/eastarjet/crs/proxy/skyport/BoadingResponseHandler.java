package com.eastarjet.crs.proxy.skyport;

import java.io.IOException;

import com.eastarjet.net.service.relay.RelayBuffer;
import com.eastarjet.net.service.relay.RelayHandler;
import com.eastarjet.net.service.relay.RelayRequest;
import com.eastarjet.net.service.relay.RelayResponse;
import com.eastarjet.net.service.relay.RelaySession;

/*
 * */
public class BoadingResponseHandler implements RelayHandler 
{

	static byte[][] tokens = 
	{
			"Passenger has already been boarded.".getBytes(),
			"CommandBoardPax: Value was either too large or too small for an Int32.".getBytes(),
			"Unrecognized CheckIn Command.".getBytes(),
			"Sequence number not found.".getBytes(),
			"Invalid input. ".getBytes()
	};
	
	@Override
	public int getValidCount(RelaySession session, RelayBuffer buf) 
	{
		// TODO Auto-generated method stub
		buf.mark();
		int ret=-1;
		for(int i=0;i<tokens.length;i++)
		{
			int tret=buf.compareWith(tokens[i]);
			if(tret>=0){ ret=tret; break;} 
		}//for
		
		buf.rewind();
		if(ret==0) session.setHandler(this);
		return ret;
	}

	@Override
	public void handleRequest(RelayRequest request, RelayResponse response)
			throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasEnoughData(RelaySession session, RelayBuffer buf)
			throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

}

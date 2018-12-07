package com.eastarjet.net.service.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.eastarjet.net.service.relay.RelayBuffer;
import com.eastarjet.net.service.relay.RelayHandler;
import com.eastarjet.net.service.relay.RelayRequest;
import com.eastarjet.net.service.relay.RelayResponse;
import com.eastarjet.net.service.relay.RelaySession;
import com.eastarjet.net.service.relay.imp.DefaultRelayRequest;
import com.eastarjet.net.service.relay.imp.DefaultRelayResponse;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;
/**
 * 핸들러를 통한 필터링  중계서버 모델 
 * 
 * @author clouddrd
 *
 */
public class AdvancedRelayTask extends RelayTask implements RelaySession
{
	static Logger log = Toolkit.getLogger(AdvancedRelayTask.class);

	
	RelayHandler activeSourceHandler ;
	RelayHandler activeTargetHandler ;
	
	DefaultRelayRequest sourceRequest;
	DefaultRelayResponse sourceResponse;
	
	DefaultRelayRequest targetRequest;
	DefaultRelayResponse targetResponse;
	
	protected RelayHandler getSourceHandler(){ return null;}
	protected RelayHandler getTargetHandler(){ return null;}
	
	public RelayRequest getSourceRequest(){return sourceRequest;}
	public RelayResponse getSourceReponse(){return sourceResponse;}
	
	public RelayRequest getTargetRequest(){return targetRequest;}
	public RelayResponse getTargetReponse(){return targetResponse;}
	
	
	public AdvancedRelayTask()
	{
		sourceRequest=new DefaultRelayRequest(this);
		sourceResponse=new DefaultRelayResponse(this);
		
		targetRequest=new DefaultRelayRequest(this);
		targetResponse=new DefaultRelayResponse(this);
	}
	
	
	protected void processLoopStart()
	{
		sourceResponse.setSocket((SocketChannel)source.getSocket());
		sourceRequest.setSocket((SocketChannel)source.getSocket());
		
		targetRequest.setSocket(this.targetSocket);
		targetResponse.setSocket(this.targetSocket);
	}
	
	protected void processLoopStop()
	{
		sourceResponse.clear();
		sourceRequest.clear();
		
		targetRequest.clear();
		targetResponse.clear();
	}
	
	
	RelayHandler _handler;
	public void setHandler(RelayHandler handler )
	{_handler=handler;}
	
	public RelayHandler getHandler( )
	{
		return _handler;
	}

	/*
	protected void processSourceReceived(ByteBuffer buf) throws IOException 
	{
		 super.processSourceReceived(buf);
	}//method
	*/

	RelayBuffer validBuffer=new RelayBuffer();
	
	protected void processTargetReceived(ByteBuffer buf)throws IOException 
	{
		DefaultRelayRequest request  	= targetRequest;
		DefaultRelayResponse response 	= targetResponse;
		 
		RelaySession session			= targetRequest.getSession();
		RelayHandler handler 			= getTargetHandler();
		

		int sz=buf.position();
		buf.position(0);
		int spos=0,pcount=0;
	
		
		for(int i=0;i<sz;i++)
		{
			byte b=buf.get();
			validBuffer.add(b);
			
			if(activeTargetHandler==null)
			{
				int rsz = handler.getValidCount(session,validBuffer);
				if(rsz < 0) 
				{	
					validBuffer.feed(1); //next tok
					continue;
				}
				else if(rsz > 0)
				{	
					//if it is not a dominate handler, next tok    
					if(pcount==rsz){ validBuffer.feed(pcount); pcount=0;}
					else pcount=rsz; //add current tok
					
					continue; 
				}
				activeTargetHandler=session.getHandler();
				sendBeforeData(validBuffer,spos);
				validBuffer.trim();
			}
			else 
			{
				
				if(!activeTargetHandler.hasEnoughData(session, validBuffer)) continue; 
				request.clear(); 
				request.getBuffer().add(validBuffer);
				byte[]rbuf=new byte[4096];
				validBuffer.getBytes(rbuf, 0);
				activeTargetHandler.handleRequest(request, response);
				validBuffer.trim();
				
				spos=validBuffer.getPosition();
				activeTargetHandler=null;
			}
		}//for
			
		if(activeTargetHandler==null)
		{ sendBeforeData(validBuffer,spos); validBuffer.trim();}
	}
	
	protected void sendBeforeData(RelayBuffer buf,int beforePos) throws IOException 
	{
		int epos = buf.getPosition();
		if(epos==beforePos) return;
		buf.setPosition(beforePos);
		sourceResponse.writeAll(buf, 0, epos-beforePos);
			//finding
	}
	
	
	
}//class

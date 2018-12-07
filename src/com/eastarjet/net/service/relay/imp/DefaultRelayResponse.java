package com.eastarjet.net.service.relay.imp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.eastarjet.net.service.relay.RelayBuffer;
import com.eastarjet.net.service.relay.RelayResponse;
import com.eastarjet.net.service.relay.RelaySession;

public class DefaultRelayResponse implements RelayResponse {

	RelaySession session;
	SocketChannel socket;
	ByteBuffer sendBuffer;
	public DefaultRelayResponse(RelaySession session)
	{
		this.session=session;
		sendBuffer= ByteBuffer.allocate(40960);
	}

	public void clear()
	{
		sendBuffer.clear();
	}
	
	public void setSocket(SocketChannel socket)
	{ this.socket=socket;}

	@Override
	public RelaySession getSession() {
		// TODO Auto-generated method stub
		return session;
	}

	@Override
	public int writeAll(byte[] sbuf) throws IOException 
	{
		return writeAll(sbuf, 0, sbuf.length);
	}

		@Override
	public int writeAll(byte[] sbuf,int spos, int tlen) throws IOException 
	{
		ByteBuffer buf=sendBuffer;
		buf.put(sbuf);
		return writeAll(buf,spos,tlen);
	}

	@Override
	public int writeAll(RelayBuffer sbuf) throws IOException 
	{
		return this.writeAll(sbuf, 0, sbuf.leftSize());
	}

	@Override
	public int writeAll(RelayBuffer sbuf,int spos, int tlen) throws IOException 
	{
		ByteBuffer buf=sendBuffer;
		sbuf.getByteBuffer(sendBuffer,spos,tlen);
		return writeAll(buf,0,tlen);
	}
	
	public int writeAll(ByteBuffer buf,int spos,int tlen)  throws IOException
	{
		
		//int sz=buf.position();
		
		int sz=tlen;
		int count=0;
	//	buf.limit(tlen);
		buf.position(spos);
		buf.limit(spos+sz);
		
	//	buf.position(0);
		while(count<sz)
		{
			count+=socket.write(buf);
			//sock.w
		}//while
		
		//sz=buf.capacity();
		buf.clear();
		return count;
	}
	
}//class

package com.eastarjet.net.service.terminal.view;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.eastarjet.util.ByteQueue;

public class Response 
{
	int type;
	SocketChannel socket;
	ByteQueue queue;
	ByteBuffer byteBuffer;
	
	public Response(int size)
	{
		queue= new ByteQueue(size);
		byteBuffer=ByteBuffer.allocate(size);
	}
	
	
	public void setSocket(SocketChannel socket)
	{this.socket=socket;}
	
	public int getType(){return type;}
	public void setType(int type){this.type=type;}
	
	public void writeByte(int ch)
	{  queue.add((byte)ch); 	}
	
	public void write(byte[] buf, int pos,int len )
	{
		queue.add(buf,pos,len);
	}
	
	public void writePeekAll(Request request)
	{
		int pos=request.getPeekPosition()+1;
		for(int i=0;i<pos;i++)
			queue.add((byte)request.read());
	}
	
	public void flush()
	{
		try
		{
			int sz=queue.size();
			queue.poll(byteBuffer,sz);
			byteBuffer.flip();
			socket.write(byteBuffer);
			byteBuffer.clear();
		}catch(Exception e)
		{}
	}
}//class

package com.eastarjet.net.service.relay;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.eastarjet.crs.proxy.skyport.PrintingHandler;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

public class RelayBuffer 
{
	byte [] buffer;
	int pos;
	int length;
	int limit;
	int mark=-1;
	static Logger log = Toolkit.getLogger(RelayBuffer.class); 
	
	public RelayBuffer (){ this(40960);}
	public RelayBuffer (int size)
	{
		buffer = new byte[size];
		limit=size;
	}
	
	public void clear(){ pos=0; length=0; mark=-1;}
	public void setLimit(int limit){ this.limit=limit;}
	public int  getLimit(){ return this.limit;}
	public int  leftSize(){ return length-pos;}
	public int 	getPosition(){return pos;}
	public void setPosition(int tpos){ pos=tpos;}
	public int 	length(){return length;}
	public int 	find(byte[] buf)
	{
		return find(buf,0);
	}
	
	public void feed(int s){ pos+=s;}
	
	
	public int find(byte[] buf,int startPos)
	{
		int ret=-1,count;
		String dbg="";
		for(int i=pos+startPos;i<length;i++)
		{
			count=0;
			for(int j=0;j<buf.length;j++)
			{
				boolean tret=buffer[i+j]==buf[j];
				
			//	dbg+=buffer[i+j]+((tret)?"==":"!=")+buf[j]+"\r\n";
				if(!tret) break;

				count++;
			}//for
			
			if(count==buf.length) { ret=(i-pos); break;}
		}//for
		
//		log.debug(dbg);

		
		return ret;
	}

	
	public int compareWith(byte [] buf)
	{
		String dbg="";
		int count=0;
		for(int j=0;pos+j<length &&  j<buf.length;j++)
		{
			byte c1=buffer[pos+j];
			byte c2=buf[j];
			
			boolean tret=c1==c2;
			//dbg+=String.format("%02x",c1)+((tret)?"==":"!=")+String.format("%02x",c2)+", ";
			if(!tret) break;

			count++;
		}//for
		
		
		if(count==0 ) count=-1;
		if(count==buf.length) count=0;
		//log.debug("ret="+count+" : "+dbg);
		return count;
	}
	
	public boolean startsWith(byte [] buf)
	{
		String dbg="";
		int count=0;
		for(int j=0;pos+j<length &&  j<buf.length;j++)
		{
			byte c1=buffer[pos+j];
			byte c2=buf[j];
			
			boolean tret=c1==c2;
		//	dbg+=String.format("%02x",c1)+((tret)?"==":"!=")+String.format("%02x",c2)+", ";
			if(!tret) break;

			count++;
		}//for
		
		//log.debug("ret="+count+" : "+dbg);
		
		return count==buf.length;
	}
	
	public boolean endsWith(byte [] buf)
	{
		String dbg="";
		int count=0;
		
		if(length- pos <buf.length ) return false;
		
		for(int j=1;pos+j<=length &&  j<=buf.length;j++)
		{
			byte c1=buffer[length  - j];
			byte c2=buf[buf.length- j];
			
			boolean tret=c1==c2;
		//	dbg+=String.format("%02x",c1)+((tret)?"==":"!=")+String.format("%02x",c2)+", ";
			if(!tret) break;

			count++;
		}//for
		
		//log.debug("ret="+count+" : "+dbg);
		
		return count==buf.length;
	}	
	
	public void skip(int ix){pos+=ix;}
	
	public void mark()
	{		 mark=pos;	}
	
	public void rewind(){ if(mark>=0) pos=mark; }
	
	public void add(byte[] buf)
	{
		System.arraycopy(buf, 0, buffer,pos,buf.length);
		length+=buf.length;
	}
	
	public void add(byte ch)
	{
		buffer[length++]=ch;
	}	
	
	public void add(RelayBuffer bbuf)
	{
		int len=bbuf.leftSize();
		System.arraycopy(bbuf.buffer, bbuf.pos, buffer,pos,len);
		bbuf.pos+=len;
	 
		length+=len;
	}
	
	public void add(ByteBuffer bbuf)
	{
		int tpos=bbuf.position();
		bbuf.flip();
		bbuf.get(buffer, pos, tpos );
	 
		length+=tpos;
	}
	
	public int getBytes(byte[] rbuf,int tpos)
	{
		int len=length-pos;
		System.arraycopy(buffer, pos, rbuf, tpos, len);
		pos+=len;
		return len;
	}
	
	public int getByteBuffer(ByteBuffer bbuf,int off,int tlen)
	{
		int len=tlen;
		bbuf.put(buffer,pos+off,len);
		pos+=len;
		return len;
	}
	
	
	public String readLine()
	{
		String ret="";
		if(pos==length) return null;
		for(int i=pos;i<length;i++)
		{
			pos++;
			if(buffer[i]=='\r') continue;
			if(buffer[i]=='\n') break;
			ret+=(char)buffer[i];
		}
		
		return ret;
	}
	
	public void trim()
	{
		int len=leftSize();
		System.arraycopy(buffer, pos, buffer, 0, len);
		pos=0; length=len;
		mark=-1;
	}
	
	public String toString()
	{
		return new String(buffer,pos,length);
	}
}//class

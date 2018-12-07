package com.eastarjet.net.service.relay;

import java.io.IOException;

public interface RelayResponse 
{
	public RelaySession getSession();
	
	public int writeAll(byte[] sbuf) throws IOException ;
	public int writeAll(RelayBuffer sbuf) throws IOException ;
	public int writeAll(byte[]buf,int spos, int tlen) throws IOException;
	public int writeAll(RelayBuffer buf,int spos, int tlen) throws IOException;
}

package com.eastarjet.net.service.terminal.view.validator.pattern;

import java.io.IOException;

public interface Reader 
{
	public int read() throws IOException;
	public int peek() ;
	public void reset(int position);
	public int getPosition();
}

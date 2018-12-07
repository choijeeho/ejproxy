package com.eastarjet.net.service;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface RelayFilter {
	public boolean isFilterRejected(SocketChannel socket,ByteBuffer buf);

}

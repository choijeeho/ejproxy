package com.eastarjet.net.service.terminal.view;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Queue;

import com.eastarjet.util.ByteQueue;

public class Request {
	int type;
	int peekPos;
	ByteQueue queue;
	RequestInputStream in;

	public Request(int size) {
		queue = new ByteQueue(size);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public void markPeek() {
		queue.markPeek();
	}

	public void resetPeek() {
		queue.resetPeek();
	}

	public boolean incPeek() {
		boolean ret = false;
		if (queue.isPeekable()) {
			queue.incPeek();
			ret = true;
		}
		return ret;
	}

	public void skipRead(int pos) {
		for (int i = 0; i < pos; i++)
			queue.poll();
	}

	public int peek() {
		return queue.peek();
	}

	public void peek(byte[] buf, int pos, int len) {
		queue.peek(buf, pos, len);
	}

	public int read() {
		return queue.poll();
	}

	public void read(byte[] buf, int pos, int len) {
		queue.poll(buf, pos, len);
	}

	public void add(ByteBuffer buf, int size) {
		queue.add(buf, size);
	}

	public void add(byte[] buf, int size) {
		queue.add(buf, 0, size);
	}

	public int getPeekPosition() {
		return queue.getPeekPosition();
	}

	public void setPeekPosition(int p) {
		queue.setPeekPosition(p);
	}

	public int getPollPosition() {
		return queue.getPollIndex();
	}

	public int getEndPosition() {
		return queue.getEndIndex();
	}

	public boolean isPeekable() {
		return queue.isPeekable();
	}

	public InputStream getInputStream() {
		if (in == null)
			in = new RequestInputStream(this);

		return in;
	}

}// class

class RequestInputStream extends InputStream {
	Request request;

	RequestInputStream(Request request) {
		this.request = request;
	}

	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		if (request.queue.isEmpty())
			return -1;
		return request.read();
	}
}

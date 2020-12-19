package com.landry.digitalcircuitsimulator.util;

public class CircularBuffer {

	private byte[] data;
	private int read = 0;
	private int write = 0;
	
	public CircularBuffer(int size) {
		data = new byte[size];
	}
	
	public CircularBuffer() {
		this(1000);
	}
	
	public void write(byte input) {
		data[write] = input;
		write = (write + 1) % data.length;
	}
	
	public byte read() {
		byte result = data[read];
		read = (read + 1) % data.length;
		return result;
	}
	
	public byte peek() {
		return data[read];
	}
	
	public int getFreeSpace() {
		return (read - write) % data.length;
	}
	
	public int getUsedSpace() {
		return (write - read) % data.length;
	}
	
	public void setReadHead(int value) {
		read = value;
	}
	
	public void setWriteHead(int value) {
		write = value;
	}
	
	public int getReadHead() {
		return read;
	}
	
	public int getWriteHead() {
		return write;
	}
	
	public byte[] readAll() {
		byte[] result = new byte[data.length];
		for(int i = 0; getUsedSpace() > 0; i++) {
			result[i] = read();
		}
		return result;
	}
	
	/**
	 * changes the size of the data array. Clears the array as a side effect.
	 * @param size the new size of the data array.
	 */
	void setSize(int size) {
		data = new byte[size];
	}
}

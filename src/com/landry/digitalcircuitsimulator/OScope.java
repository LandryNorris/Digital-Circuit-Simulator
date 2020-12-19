package com.landry.digitalcircuitsimulator;

import com.landry.digitalcircuitsimulator.util.CircularBuffer;

public class OScope {
	CircularBuffer[] channels;
	
	OScope(int numChannels, int size) {
		channels = new CircularBuffer[numChannels];
		for(int i = 0; i < channels.length; i++) {
			channels[i] = new CircularBuffer(size);
		}
	}
	
	byte[] readChannel(int channelIndex) {
		channels[channelIndex].setReadHead(channels[channelIndex].getWriteHead());
		channels[channelIndex].read(); //move the read head one space.
		return channels[channelIndex].readAll();
	}
}

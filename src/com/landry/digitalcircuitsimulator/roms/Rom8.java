package com.landry.digitalcircuitsimulator.roms;

import com.landry.digitalcircuitsimulator.Component;
import com.landry.digitalcircuitsimulator.Pin;
import com.landry.digitalcircuitsimulator.State;

public class Rom8 extends Rom {
	byte lastState = State.UNDEFINED;
	
	{
		inputs = new Pin[9];
		outputs = new Pin[8];
		
		w = 6;
		h = 18;

		data = new byte[256];

		s = "ROM";
	}

	public Rom8(int componentNumber) {
		initPins(componentNumber);
		fillData(0);
	}

	public Rom8() {
		initPins();
		fillData(0);
	}

	@Override
	protected void update() {
		//inputs[8] will represent CLK
		if(inputs[8].getState() == State.LOW && lastState == State.HIGH) {
			int address = decodeAddress(inputs, 0, 8);
			System.out.println(address);
			int output = read(address);
			writeOutputs(output);
		}
		lastState = inputs[8].getState();
	}
	
	void writeOutputs(int output) {
		for(int i = 0; i < outputs.length; i++) {
			int mask = 1 << i;
			byte state = ((output & mask) != 0) ? State.HIGH : State.LOW;
			outputs[outputs.length-i].setState(state);
		}
	}

	@Override
	protected int getType() {
		return Component.Type.ROM8;
	}

	@Override
	protected void setPinLocations() {
		for(int i = 0; i < 8; i++) {
			inputs[i].setXY(x, y+i*2+2);
			outputs[i].setXY(x+w, y+i*2+2);
			inputs[8].setXY(x+1, y+h);
		}
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY) {
		return super.clicked(mouseX, mouseY);
	}
	
}
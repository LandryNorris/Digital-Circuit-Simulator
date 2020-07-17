/*
 Copyright (c) 2020 Landry Norris. This file is part of
 Digital Circuit Simulator, an open source simulator for digital circuits.
 Digital Circuit Simulator is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 Digital Circuit Simulator is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with the Digital Circuit Simulator Library.  If not, see http://www.gnu.org/licenses.
 */
package com.landry.digitalcircuitsimulator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.landry.digitalcircuitsimulator.flipflops.DFlipFlop;
import com.landry.digitalcircuitsimulator.flipflops.TFlipFlop;
import com.landry.digitalcircuitsimulator.gates.AndGate;
import com.landry.digitalcircuitsimulator.gates.Inverter;
import com.landry.digitalcircuitsimulator.gates.NandGate;
import com.landry.digitalcircuitsimulator.gates.NorGate;
import com.landry.digitalcircuitsimulator.gates.OrGate;
import com.landry.digitalcircuitsimulator.gates.XnorGate;
import com.landry.digitalcircuitsimulator.gates.XorGate;
import com.landry.digitalcircuitsimulator.inputs.Input;
import com.landry.digitalcircuitsimulator.outputs.Output;
import com.landry.digitalcircuitsimulator.roms.Rom8;

public abstract class Component {

	public static class Type {
		public static final int AND = 0;
		public static final int OR = 1;
		public static final int XOR = 2;
		public static final int NAND = 3;
		public static final int NOR = 4;
		public static final int NXOR = 5;
		public static final int INVERTER = 6;
		public static final int INPUT = 7;
		public static final int OUTPUT = 8;
		public static final int DFF = 9;
		public static final int TFF = 10;
		public static final int ROM8 = 11;
	}
	public byte state = State.UNDEFINED;

	protected Pin[] inputs;
	protected Pin[] outputs;

	protected int x = 0;
	protected int y = 0;
	protected int w = 0;
	protected int h = 0;

	byte getState() {
		return state;
	}

	void setState(byte s) {
		state = s;
	}

	void setState(boolean s) {
		state = (byte) ((s) ? 1 : 0);
	}

	protected abstract void draw(Graphics2D g, int gridX, int gridY, int gridSize);

	protected abstract void update();

	protected boolean inputsDefined() {
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i] == null)
				return false;
		}
		return true;
	}
	
	protected abstract int getType();

	protected void initInputPins(int componentNumber) {
		for (int i = 0; i < inputs.length; i++) {
			Pin pin = new Pin();
			pin.componentNumber = componentNumber;
			pin.pinNumber = i;
			pin.isInput = true;
			inputs[i] = pin;
		}
	}

	protected void initOutputPins(int componentNumber) {
		for (int i = 0; i < outputs.length; i++) {
			Pin pin = new Pin();
			pin.componentNumber = componentNumber;
			pin.pinNumber = i;
			pin.isInput = false;
			outputs[i] = pin;
		}
	}

	protected void initPins(int componentNumber) {
		initInputPins(componentNumber);
		initOutputPins(componentNumber);
	}
	
	protected void initPins() {
		initPins(0);
	}

	protected void initInputPins(int componentNumber, int r) {
		for (int i = 0; i < inputs.length; i++) {
			Pin pin = new Pin();
			pin.componentNumber = componentNumber;
			pin.pinNumber = i;
			pin.isInput = true;
			pin.r = r;
			inputs[i] = pin;
		}
	}
	
	protected abstract void setPinLocations();

	protected void initOutputPins(int componentNumber, int r) {
		for (int i = 0; i < outputs.length; i++) {
			Pin pin = new Pin();
			pin.componentNumber = componentNumber;
			pin.pinNumber = i;
			pin.isInput = false;
			pin.r = r;
			outputs[i] = pin;
		}
	}

	protected void initPins(int componentNumber, int r) {
		initInputPins(componentNumber, r);
		initOutputPins(componentNumber, r);
	}

	static void setState(ArrayList<Component> components, Pin pin, byte state) {
		components.get(pin.componentNumber).inputs[pin.pinNumber].setState(state);
	}

	static byte getState(ArrayList<Component> components, Pin pin) {
		return components.get(pin.componentNumber).outputs[pin.pinNumber].getState();
	}

	protected boolean clicked(double mouseX, double mouseY) {
		return (mouseX > x && mouseX < x+w && (mouseY > y && mouseY < y+h));
	}
	
	protected Pin pinAt(int x, int y) {
		if (inputs != null) {
			for (int i = 0; i < inputs.length; i++) {
				
				if (inputs[i].points[0].x == x && inputs[i].points[0].y == y) {
					return inputs[i];
				}
			}
		}

		if (outputs != null) {
			for (int i = 0; i < outputs.length; i++) {
				if (outputs[i].points[0].x == x && outputs[i].points[0].y == y) {
					return outputs[i];
				}
			}
		}

		return null;
	}

	protected Pin pinClicked(double mouseX, double mouseY) {
		if (inputs != null) {
			for (int i = 0; i < inputs.length; i++) {
				
				if (inputs[i].clicked(mouseX, mouseY)) {
					return inputs[i];
				}
			}
		}

		if (outputs != null) {
			for (int i = 0; i < outputs.length; i++) {
				if (outputs[i].clicked(mouseX, mouseY)) {
					return outputs[i];
				}
			}
		}

		return null;
	}
	
	protected boolean onScreen(int x, int y, int width, int height) {
		return (this.x + this.w > x && this.x < x+width) && (this.y + this.h > y && this.y < y+height);
	}
	
	static Component create(int type) {
		switch(type) {
			case Component.Type.AND: return new AndGate();
			case Component.Type.OR: return new OrGate();
			case Component.Type.XOR: return new XorGate();
			case Component.Type.NAND: return new NandGate();
			case Component.Type.NOR: return new NorGate();
			case Component.Type.NXOR: return new XnorGate();
			case Component.Type.INVERTER: return new Inverter();
			case Component.Type.INPUT: return new Input();
			case Component.Type.OUTPUT: return new Output();
			case Component.Type.DFF: return new DFlipFlop();
			case Component.Type.TFF: return new TFlipFlop();
			case Component.Type.ROM8: return new Rom8();
			default: throw new IllegalArgumentException(type + " is not a valid component type.");
		}
	}

	public void rightClick() {}
}

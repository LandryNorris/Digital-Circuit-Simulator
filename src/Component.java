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

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public abstract class Component {

	static class Type {
		static final int AND = 0;
		static final int OR = 1;
		static final int XOR = 2;
		static final int NAND = 3;
		static final int NOR = 4;
		static final int NXOR = 5;
		static final int INVERTER = 6;
		static final int INPUT = 7;
		static final int OUTPUT = 8;
		static final int DFF = 9;
		static final int TFF = 10;
	}
	byte state = State.UNDEFINED;

	Pin[] inputs;
	Pin[] outputs;

	int x = 0;
	int y = 0;
	int w = 0;
	int h = 0;

	byte getState() {
		return state;
	}

	void setState(byte s) {
		state = s;
	}

	void setState(boolean s) {
		state = (byte) ((s) ? 1 : 0);
	}

	abstract void draw(Graphics2D g, int gridX, int gridY, int gridSize);

	abstract void update();

	boolean inputsDefined() {
		for (int i = 0; i < inputs.length; i++) {
			if (inputs[i] == null)
				return false;
		}
		return true;
	}
	
	abstract int getType();

	void initInputPins(int componentNumber) {
		for (int i = 0; i < inputs.length; i++) {
			Pin pin = new Pin();
			pin.componentNumber = componentNumber;
			pin.pinNumber = i;
			pin.isInput = true;
			inputs[i] = pin;
		}
	}

	void initOutputPins(int componentNumber) {
		for (int i = 0; i < outputs.length; i++) {
			Pin pin = new Pin();
			pin.componentNumber = componentNumber;
			pin.pinNumber = i;
			pin.isInput = false;
			outputs[i] = pin;
		}
	}

	void initPins(int componentNumber) {
		initInputPins(componentNumber);
		initOutputPins(componentNumber);
	}

	void initInputPins(int componentNumber, int r) {
		for (int i = 0; i < inputs.length; i++) {
			Pin pin = new Pin();
			pin.componentNumber = componentNumber;
			pin.pinNumber = i;
			pin.isInput = true;
			pin.r = r;
			inputs[i] = pin;
		}
	}
	
	abstract void setPinLocations();

	void initOutputPins(int componentNumber, int r) {
		for (int i = 0; i < outputs.length; i++) {
			Pin pin = new Pin();
			pin.componentNumber = componentNumber;
			pin.pinNumber = i;
			pin.isInput = false;
			pin.r = r;
			outputs[i] = pin;
		}
	}

	void initPins(int componentNumber, int r) {
		initInputPins(componentNumber, r);
		initOutputPins(componentNumber, r);
	}

	static void setState(ArrayList<Component> components, Pin pin, byte state) {
		components.get(pin.componentNumber).inputs[pin.pinNumber].setState(state);
	}

	static byte getState(ArrayList<Component> components, Pin pin) {
		return components.get(pin.componentNumber).outputs[pin.pinNumber].getState();
	}

	boolean clicked(double mouseX, double mouseY) {
		return (mouseX > x && mouseX < x+w && (mouseY > y && mouseY < y+h));
	}
	
	Pin pinAt(int x, int y) {
		if (inputs != null) {
			for (int i = 0; i < inputs.length; i++) {
				
				if (inputs[i].x == x && inputs[i].y == y) {
					return inputs[i];
				}
			}
		}

		if (outputs != null) {
			for (int i = 0; i < outputs.length; i++) {
				if (outputs[i].x == x && outputs[i].y == y) {
					return outputs[i];
				}
			}
		}

		return null;
	}

	Pin pinClicked(double mouseX, double mouseY) {
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
	
	boolean onScreen(int x, int y, int width, int height) {
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
			default: throw new IllegalArgumentException(type + " is not a valid component type.");
		}
	}
	
	static Component create(int type, int componentNum) {
		switch(type) {
			case Component.Type.AND: return new AndGate(componentNum);
			case Component.Type.OR: return new OrGate(componentNum);
			case Component.Type.XOR: return new XorGate(componentNum);
			case Component.Type.NAND: return new NandGate(componentNum);
			case Component.Type.NOR: return new NorGate(componentNum);
			case Component.Type.NXOR: return new XnorGate(componentNum);
			case Component.Type.INVERTER: return new Inverter(componentNum);
			case Component.Type.INPUT: return new Input(componentNum);
			case Component.Type.OUTPUT: return new Output(componentNum);
			case Component.Type.DFF: return new DFlipFlop(componentNum);
			case Component.Type.TFF: return new TFlipFlop(componentNum);
			default: throw new IllegalArgumentException(type + " is not a valid component type.");
		}
	}
}

class AndGate extends Component {

	{
		w = 2;
		h = (int) (w * 2);
	}

	AndGate() {
		inputs = new Pin[2];
		outputs = new Pin[1];
	}

	AndGate(int componentNumber) {
		inputs = new Pin[2];
		outputs = new Pin[1];
		initPins(componentNumber);
		setPinLocations();
	}
	
	void setPinLocations() {
		
		outputs[0].setXY(x + w, y + h / 2);
		inputs[0].setXY(x, y + h / 4);
		inputs[1].setXY(x, y + h * 3 / 4);
	}

	@Override
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		int lineX = (x + gridOffsetX)*gridSize + xOffset;
		int lineY = (y + gridOffsetY)*gridSize + yOffset;
		g.drawLine(lineX, lineY, lineX, lineY + h*gridSize);
		g.drawArc(lineX - w*gridSize, lineY, (w * 2)*gridSize, h*gridSize, 90, -180);
		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		if (inputsDefined()) {
			outputs[0].setState((byte) ((inputs[0].getState() == 1 && inputs[1].getState() == 1) ? 1 : 0));
		}

		else {
			outputs[0].setState(State.LOW);
		}
	}
	
	int getType() {
		return Component.Type.AND;
	}

}

class NandGate extends Component {

	{
		w = 2;
		h = (int) (w * 2);
	}

	NandGate() {
		inputs = new Pin[2];
		outputs = new Pin[1];
	}

	NandGate(int componentNumber) {
		inputs = new Pin[2];
		outputs = new Pin[1];
		initPins(componentNumber);
		setPinLocations();
	}
	
	void setPinLocations() {
		outputs[0].setXY((x + w) + 1, (y + h / 2));

		inputs[0].setXY(x, (y + h / 4));
		inputs[1].setXY(x, (y + h * 3 / 4));
	}

	@Override
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		int lineX = (x + gridOffsetX)*gridSize + xOffset;
		int lineY = (y + gridOffsetY)*gridSize + yOffset;
		g.drawLine(lineX, lineY, lineX, lineY + h*gridSize);
		g.drawArc(lineX - w*gridSize, lineY, (w * 2)*gridSize, h*gridSize, 90, -180);
		int notR = gridSize/2;
		g.drawOval((x+w+gridOffsetX)*gridSize+xOffset, (y+h/2+gridOffsetY)*gridSize-notR + yOffset, 2*notR, 2*notR);
		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		if (inputsDefined()) {
			outputs[0].setState((byte) ((inputs[0].getState() == 1 && inputs[1].getState() == 1) ? 0 : 1));
		}

		else {
			outputs[0].setState(State.LOW);
		}
	}

	int getType() {
		return Component.Type.NAND;
	}
}

class OrGate extends Component {

	{
		w = 2;
		h = (int) (w * 2);
	}

	OrGate() {
		inputs = new Pin[2];
		outputs = new Pin[1];
	}

	OrGate(int componentNumber) {
		inputs = new Pin[2];
		outputs = new Pin[1];
		initPins(componentNumber);
		setPinLocations();
	}
	
	void setPinLocations() {
		outputs[0].setXY((x + w), (y + h / 2));
		inputs[0].setXY(x, (y + h / 4));
		inputs[1].setXY(x, (y + h * 3 / 4));
	}

	@Override
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		int curveR = (int) (1.5*h*gridSize);
		int dx = (int) Math.sqrt(curveR*curveR - (h/2)*h/2*gridSize*gridSize);
		int angle = (int) Math.toDegrees(Math.asin(h*gridSize/(2.0*curveR)));
		g.drawArc((x+gridOffsetX)*gridSize+xOffset-curveR-dx, (y + h/2 + gridOffsetY)*gridSize + yOffset - curveR, curveR*2, curveR*2, angle, -2*angle);
		g.drawArc((x - w + gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w * 2*gridSize, h*gridSize, 90, -180);
		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		if (inputsDefined()) {
			outputs[0].setState((byte) ((inputs[0].getState() == 1 || inputs[1].getState() == 1) ? 1 : 0));
		}

		else {
			outputs[0].setState(State.LOW);
		}
	}

	int getType() {
		return Component.Type.OR;
	}
}

class NorGate extends Component {

	{
		w = 2;
		h = (int) (w * 2);
	}

	NorGate() {
		inputs = new Pin[2];
		outputs = new Pin[1];
	}

	NorGate(int componentNumber) {
		inputs = new Pin[2];
		outputs = new Pin[1];
		initPins(componentNumber);
	}
	
	void setPinLocations() {
		outputs[0].setXY((x + w) + 1, (y + h / 2));
		inputs[0].setXY(x, (y + h / 4));
		inputs[1].setXY(x, (y + h * 3 / 4));
	}

	@Override
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		int curveR = (int) (1.5*h*gridSize);
		int dx = (int) Math.sqrt(curveR*curveR - (h/2)*h/2*gridSize*gridSize);
		int angle = (int) Math.toDegrees(Math.asin(h*gridSize/(2.0*curveR)));
		g.drawArc((x+gridOffsetX)*gridSize+xOffset-curveR-dx, (y + h/2 + gridOffsetY)*gridSize + yOffset - curveR, curveR*2, curveR*2, angle, -2*angle);
		g.drawArc((x - w + gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w * 2*gridSize, h*gridSize, 90, -180);
		int notR = gridSize/2;
		g.drawOval((x+w+gridOffsetX)*gridSize + xOffset, (y+h/2+gridOffsetY)*gridSize-notR + yOffset, 2*notR, 2*notR);
		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		if (inputsDefined()) {
			outputs[0].setState((byte) ((inputs[0].getState() == 1 || inputs[1].getState() == 1) ? 0 : 1));
		}

		else {
			outputs[0].setState(State.LOW);
		}
	}

	int getType() {
		return Component.Type.NOR;
	}
}

class XorGate extends Component {

	{
		w = 2;
		h = (int) (w * 2);
	}

	XorGate() {
		inputs = new Pin[2];
		outputs = new Pin[1];
	}

	XorGate(int componentNumber) {
		inputs = new Pin[2];
		outputs = new Pin[1];
		initPins(componentNumber);
	}
	
	void setPinLocations() {
		outputs[0].setXY((x + w), (y + h / 2));
		inputs[0].setXY(x, (y + h / 4));
		inputs[1].setXY(x, (y + h * 3 / 4));
	}

	@Override
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		int curveR = (int) (1.5*h*gridSize);
		int dx = (int) Math.sqrt(curveR*curveR - (h/2)*h/2*gridSize*gridSize);
		int angle = (int) Math.toDegrees(Math.asin(h*gridSize/(2.0*curveR)));
		g.drawArc((x+gridOffsetX)*gridSize+xOffset-curveR-dx, (y + h/2 + gridOffsetY)*gridSize + yOffset - curveR, curveR*2, curveR*2, angle, -2*angle);
		g.drawArc((x - w + gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w * 2*gridSize, h*gridSize, 90, -180);
		g.drawArc((x+gridOffsetX)*gridSize - w*gridSize/4+xOffset-curveR-dx, (y + h/2 + gridOffsetY)*gridSize + yOffset - curveR, curveR*2, curveR*2, angle, -2*angle);

		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		if (inputsDefined()) {
			outputs[0].setState((byte) ((inputs[0].getState() == 1 ^ inputs[1].getState() == 1) ? 1 : 0));
		}

		else {
			outputs[0].setState(State.LOW);
		}
	}

	int getType() {
		return Component.Type.XOR;
	}
}

class XnorGate extends Component {

	{
		w = 2;
		h = (int) (w * 2);
	}

	XnorGate() {
		inputs = new Pin[2];
		outputs = new Pin[1];
	}

	XnorGate(int componentNumber) {
		inputs = new Pin[2];
		outputs = new Pin[1];
		initPins(componentNumber);
	}
	
	void setPinLocations() {
		outputs[0].setXY((x + w) + 1, y + h / 2);
		inputs[0].setXY(x, (y + h / 4));
		inputs[1].setXY(x, (y + h * 3 / 4));
	}

	@Override
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		int curveR = (int) (1.5*h*gridSize);
		int dx = (int) Math.sqrt(curveR*curveR - (h/2)*h/2*gridSize*gridSize);
		int angle = (int) Math.toDegrees(Math.asin(h*gridSize/(2.0*curveR)));
		g.drawArc((x+gridOffsetX)*gridSize+xOffset-curveR-dx, (y + h/2 + gridOffsetY)*gridSize + yOffset - curveR, curveR*2, curveR*2, angle, -2*angle);
		g.drawArc((x - w + gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w * 2*gridSize, h*gridSize, 90, -180);
		g.drawArc((x+gridOffsetX)*gridSize - w*gridSize/4 +xOffset-curveR-dx, (y + h/2 + gridOffsetY)*gridSize + yOffset - curveR, curveR*2, curveR*2, angle, -2*angle);
		
		int notR = gridSize/2;
		g.drawOval((x+w+gridOffsetX)*gridSize + xOffset, (y+h/2 + gridOffsetY)*gridSize + yOffset - notR, 2*notR, 2*notR);
		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		if (inputsDefined()) {
			outputs[0].setState((byte) ((inputs[0].getState() == 1 ^ inputs[1].getState() == 1) ? 0 : 1));
		}

		else {
			outputs[0].setState(State.LOW);
		}
	}

	int getType() {
		return Component.Type.NXOR;
	}
}

class Inverter extends Component {
	{
		w = 2;
		h = w;
	}
	
	Inverter() {
		inputs = new Pin[1];
		outputs = new Pin[1];
	}
	
	Inverter(int componentNumber) {
		inputs = new Pin[1];
		outputs = new Pin[1];
		initPins(componentNumber);
	}
	
	void setPinLocations() {
		outputs[0].setXY((this.x+w), (this.y+h/2));
		inputs[0].setXY((this.x), (this.y+h/2));
	}
	
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		int[] x = new int[3];
		int[] y = new int[3];
		
		x[0] = (this.x + gridOffsetX)*gridSize + xOffset;
		x[1] = (this.x + gridOffsetX)*gridSize + xOffset;
		x[2] = (this.x + gridOffsetX + w)*gridSize + xOffset;
		
		y[0] = (this.y + gridOffsetY)*gridSize + yOffset;
		y[1] = (this.y + gridOffsetY + h)*gridSize + yOffset;
		y[2] = (this.y + gridOffsetY + h/2)*gridSize + yOffset;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		g.drawPolygon(x, y, 3);
		
		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
		inputs[0].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		if(inputsDefined()) {
			outputs[0].setState((byte) ((inputs[0].getState() == 1) ? 0 : 1));
		}
	}
	
	int getType() {
		return Component.Type.INVERTER;
	}
}

class Input extends Component {

	{
		w = 2;
		h = w;
		state = State.LOW;
	}

	Input() {
		inputs = new Pin[0];
		outputs = new Pin[1];
	}

	Input(int componentNumber) {
		inputs = new Pin[0];
		outputs = new Pin[1];
		initPins(componentNumber);
		setPinLocations();
	}
	
	String text() {
		switch (state) {
			case State.TRUE:
				return "1";
			case State.FALSE:
				return "0";
			case State.UNDEFINED:
				return "X";
		}
		return "error: invalid state";
	}
	
	void setPinLocations() {
		outputs[0].setXY((x + w), (y + h / 2));
	}

	@Override
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		String s = text();
		int textWidth = g.getFontMetrics().stringWidth(s);
		int textHeight = g.getFontMetrics().getHeight();
		g.drawRect((x+gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w*gridSize, h*gridSize);
		g.drawString(s, (x + gridOffsetX + w / 2)*gridSize + xOffset - textWidth / 2, (y + gridOffsetY + h / 2)*gridSize + yOffset + textHeight / 2);

		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		outputs[0].setState(state);
	}

	void toggle() {
		if (state == State.LOW)
			state = State.HIGH;
		else if (state == State.HIGH)
			state = State.LOW;
	}

	int getType() {
		return Component.Type.INPUT;
	}
}

class Output extends Component {
	{
		w = 2;
		h = w;
	}

	Output() {
		inputs = new Pin[1];
		outputs = new Pin[0];
	}
	
	Output(int componentNumber) {
		inputs = new Pin[1];
		outputs = new Pin[0];
		initPins(componentNumber);
	}


	void setPinLocations() {
		inputs[0].setXY(x, (y + h / 2));
	}
	
	String text() {
		switch (state) {
			case State.TRUE:
				return "1";
			case State.FALSE:
				return "0";
			case State.UNDEFINED:
				return "X";
		}
		return "error: invalid state";
	}

	@Override
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {

		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		String s = text();
		int textWidth = g.getFontMetrics().stringWidth(s);
		int textHeight = g.getFontMetrics().getHeight();
		g.drawRect((x + gridOffsetX)*gridSize + xOffset, (y + gridOffsetY)*gridSize + yOffset, w*gridSize, h*gridSize);
		g.drawString(s, (x + gridOffsetX + w / 2)*gridSize + xOffset - textWidth / 2, (y + gridOffsetY + h / 2)*gridSize + yOffset + textHeight / 2);

		setPinLocations();
		inputs[0].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		state = inputs[0].getState();
	}

	int getType() {
		return Component.Type.OUTPUT;
	}
}

class DFlipFlop extends Component {

	byte lastState = State.UNDEFINED;
	{
		w = 4;
		h = w;
		state = State.LOW;
	}

	DFlipFlop() {
		inputs = new Pin[0];
		outputs = new Pin[1];
	}

	DFlipFlop(int componentNumber) {
		inputs = new Pin[2];
		outputs = new Pin[2];
		initPins(componentNumber);
		setPinLocations();
	}
	
	String text() {
		switch (state) {
			case State.TRUE:
				return "1";
			case State.FALSE:
				return "0";
			case State.UNDEFINED:
				return "X";
		}
		return "error: invalid state";
	}
	
	void setPinLocations() {
		outputs[0].setXY((x + w), (y + 1));
		outputs[1].setXY((x+w), y+3);
		inputs[0].setXY(x, y+1);
		inputs[1].setXY(x, y+3);
	}

	@Override
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		String s = "D";
		int textWidth = g.getFontMetrics().stringWidth(s);
		int textHeight = g.getFontMetrics().getHeight();
		g.drawRect((x+gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w*gridSize, h*gridSize);
		g.drawString(s, (x + gridOffsetX + w / 2)*gridSize + xOffset - textWidth / 2, (y + gridOffsetY + h / 2)*gridSize + yOffset + textHeight / 2);

		setPinLocations();
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
		outputs[0].draw(g, gridX, gridY, gridSize);
		outputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		if(inputs[1].getState() == State.LOW && lastState == State.HIGH) {
			outputs[0].setState(inputs[0].getState());
			outputs[1].setState((byte) ((inputs[0].getState() == 1) ? 0 : 1));
		}
		lastState = inputs[1].getState();
	}

	int getType() {
		return Component.Type.DFF;
	}
}

class TFlipFlop extends Component {

	byte lastState = State.UNDEFINED;
	{
		w = 4;
		h = w;
		state = State.LOW;
	}

	TFlipFlop() {
		inputs = new Pin[3];
		outputs = new Pin[2];
	}

	TFlipFlop(int componentNumber) {
		inputs = new Pin[3];
		outputs = new Pin[2];
		initPins(componentNumber);
		setPinLocations();
	}
	
	String text() {
		switch (state) {
			case State.TRUE:
				return "1";
			case State.FALSE:
				return "0";
			case State.UNDEFINED:
				return "X";
		}
		return "error: invalid state";
	}
	
	void setPinLocations() {
		outputs[0].setXY((x + w), (y + 1));
		outputs[1].setXY((x+w), y+3);
		inputs[0].setXY(x, y+1);
		inputs[1].setXY(x, y+3);
		inputs[2].setXY(x+1, y+h);
	}

	@Override
	void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		String s = "T";
		int textWidth = g.getFontMetrics().stringWidth(s);
		int textHeight = g.getFontMetrics().getHeight();
		g.drawRect((x+gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w*gridSize, h*gridSize);
		g.drawString(s, (x + gridOffsetX + w / 2)*gridSize + xOffset - textWidth / 2, (y + gridOffsetY + h / 2)*gridSize + yOffset + textHeight / 2);

		setPinLocations();
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
		inputs[2].draw(g, gridX, gridY, gridSize);
		outputs[0].draw(g, gridX, gridY, gridSize);
		outputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	void update() {
		//active low.
		if(inputs[2].getState() == State.LOW) {
			outputs[0].setState(State.LOW);
			outputs[1].setState(State.HIGH);
		}
		else if(inputs[1].getState() == State.LOW && lastState == State.HIGH && inputs[0].getState() == State.HIGH) {
			outputs[0].setState((byte) ((outputs[0].getState() == 1) ? 0 : 1));
			outputs[1].setState((byte) ((outputs[0].getState() == 1) ? 0 : 1));
		}
		lastState = inputs[1].getState();
	}

	int getType() {
		return Component.Type.TFF;
	}
}
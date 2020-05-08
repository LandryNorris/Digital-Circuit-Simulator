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
import java.awt.Graphics;
import java.util.ArrayList;

public class Wire {
	Pin input;
	Pin[] outputs;
	
	Coordinate[] points;
	
	byte state = -1;
	
	static Wire start(Pin pin) {
		Wire w = new Wire();
		if(pin.isInput) {
			w.outputs = new Pin[1];
			w.outputs[0] = pin;
		}
		
		else {
			w.input = pin;
			w.outputs = new Pin[0];
		}
		w.points = new Coordinate[2];
		w.points[0] = new Coordinate((pin.x), (pin.y));
		w.points[1] = new Coordinate(pin.x, pin.y);
		return w;
	}
	
	static byte getState(ArrayList<Wire> wires, Pin pin) {
		return wires.get(pin.componentNumber).outputs[pin.pinNumber].getState();
	}
	
	void addPoint(Coordinate c) {
		int newSize = points.length+1;
		Coordinate[] newPoints = new Coordinate[newSize];
		for(int i = 0; i < newSize-1; i++) {
			newPoints[i] = points[i];
		}
		newPoints[newSize-1] = c;
		points = newPoints;
	}
	
	byte getState() {
		return state;
	}
	
	void setState(byte s) {
		state = s;
	}
	
	void setState(boolean s) {
		state = (byte) ((s) ? 1 : 0);
	}
	
	Color getColor() {
		return (state == 1) ? new Color(0, 255, 51) : new Color(0, 200, 0);
	}
	
	void draw(Graphics g, int gridX, int gridY, int gridSize) {
		if(points == null || points.length == 0) return;
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(getColor());
		Coordinate last = points[0];
		for(int i = 1; i < points.length; i++) {
			g.drawLine((last.x+gridOffsetX)*gridSize+xOffset, (last.y+gridOffsetY)*gridSize+yOffset, (points[i].x+gridOffsetX)*gridSize+xOffset, (points[i].y+gridOffsetY)*gridSize+yOffset);

			//g.drawLine(last.x*gridSize, last.y*gridSize, points[i].x*gridSize, points[i].y*gridSize);
			last = points[i];
		}
	}
	
	void update(ArrayList<Component> components, ArrayList<Wire> wires) {
		state = (input != null) ? Component.getState(components, input) : -1;
		if(outputs == null) return;
		for(Pin output: outputs) {
			Component.setState(components, output, state);
		}
	}
	
	Pin addOutput(Pin pin) {
		if(outputs == null) outputs = new Pin[0];
		int newSize = outputs.length+1;
		Pin[] newPins = new Pin[newSize];
		for(int i = 0; i < newSize-1; i++) {
			newPins[i] = outputs[i];
		}
		newPins[newSize-1] = pin;
		outputs = newPins;
		return pin;
	}
	
	Coordinate pointAt(int x, int y) {
		//inputs in grid coordinates
		for(int i = 0; i < points.length; i++) {
			if(points[i].x == x && points[i].y == y) {
				return points[i];
			}
		}
		return null;
	}
	
	void setEndpoint(Coordinate c) {
		points[points.length-1] = c;
	}
	
	void end() {
		int newSize = points.length-1;
		Coordinate[] newPoints = new Coordinate[newSize];
		for(int i = 0; i < newSize; i++) {
			newPoints[i] = points[i];
		}
		points = newPoints;
	}
}

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
import java.awt.Graphics;
import java.util.ArrayList;

public class Wire {
	Wire input;
	Wire[] outputs;
	
	Coordinate[] points;
	
	byte state = -1;
	
	boolean hasInput = false;
	
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
		w.points[0] = new Coordinate((pin.points[0].x), (pin.points[0].y));
		w.points[1] = new Coordinate(pin.points[0].x, pin.points[0].y);
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
		if(outputs == null) {
			return;
		}
		for(int i = 0; i < outputs.length; i++) {
			Wire wire = outputs[i];
			wire.setState(s);
		}
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
			int r = gridSize/3;
			g.drawLine((last.x+gridOffsetX)*gridSize+xOffset, (last.y+gridOffsetY)*gridSize+yOffset, (points[i].x+gridOffsetX)*gridSize+xOffset, (points[i].y+gridOffsetY)*gridSize+yOffset);
			if(i != points.length-1) {
				g.fillOval((points[i].x+gridOffsetX)*gridSize+xOffset-r, (points[i].y+gridOffsetY)*gridSize+yOffset-r, 2*r, 2*r);
			}
			last = points[i];
		}
	}
	
	Wire addOutput(Wire pin) {
		if(outputs == null) outputs = new Pin[0];
		int newSize = outputs.length+1;
		Wire[] newPins = new Wire[newSize];
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
	
	boolean hasPointAt(Coordinate point) {
		//inputs in grid coordinates
		for(int i = 0; i < points.length; i++) {
			if(points[i].x == point.x && points[i].y == point.y) {
				return true;
			}
		}
		return false;
	}
	
	boolean isOutput(Wire other) {
		if(outputs == null) return false;
		for(int i = 0; i < outputs.length; i++) {
			if(outputs[i].equals(other)) return true;
		}
		return false;
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
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Wire)) return false;
		Wire other = (Wire) o;
		if(points == null || other.points == null || points.length != other.points.length) return false;
		for(int i = 0; i < points.length; i++) {
			if(points[i] != other.points[i]) return false;
		}
		return true;
	}
}

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

public class Pin {
	protected byte state = -1;
	boolean isInput;
	boolean isWire = false;
	
	int pinNumber = 0;
	int componentNumber = 0;
	
	int x;
	int y;
	double r = 0.5;
	
	byte getState() {
		return state;
	}
	
	void setState(byte s) {
		state = s;
	}
	
	void draw(Graphics g, int gridX, int gridY, int gridSize) {
		int ovalX = (int) (Util.map(x, 0, 1, gridX, gridX + gridSize) - r*gridSize);
		int ovalY = (int) (Util.map(y, 0, 1, gridY, gridY + gridSize) - r*gridSize);
		g.setColor(Color.GREEN);
		g.drawOval(ovalX, ovalY, (int) (r*2*gridSize), (int) (r*2*gridSize));
	}
	
	void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	boolean clicked(double mouseX, double mouseY) {
		//x and y are in grid space
		double distance = Math.sqrt((mouseX-x)*(mouseX-x) + (mouseY-y)*(mouseY-y));
		return distance < r;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this) return true;
		if(!(o instanceof Pin)) return false;
		Pin other = (Pin) o;
		return pinNumber == other.pinNumber && componentNumber == other.componentNumber;
	}
}

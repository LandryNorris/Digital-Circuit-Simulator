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
 along with the Digital Circuit Simulator Library.  If not, see <http://www.gnu.org/licenses.
 */
package com.landry.digitalcircuitsimulator;

public class Coordinate {
	int x, y;
	Coordinate(){}
	Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	void print() {
		System.out.println(x + ", " + y);
	}
	
	protected Coordinate clone() {
		Coordinate result = new Coordinate();
		result.x = x;
		result.y = y;
		return result;
	}
	
	int distanceTo(int x, int y) {
		return (int) Math.sqrt((this.x-x)*(this.x-x) + (this.y-y)*(this.y-y));
	}
}

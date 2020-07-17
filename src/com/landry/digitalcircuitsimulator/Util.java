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

public class Util {

	public static int modPos(int a, int b) {
		int result = a % b;
		if(result < 0) result += b;
		return result;
	}
	
	public static double map(double input, double minIn, double maxIn, double minOut, double maxOut) {
		return (input - minIn) / (maxIn - minIn) * (maxOut - minOut) + minOut;
	}
	
	public static Coordinate gridToAbsolute(Coordinate c, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		//in grid coordinates
		Coordinate result = c.clone();
		
		result.x = (result.x + gridOffsetX)*gridSize + xOffset;
		result.y = (result.y + gridOffsetY)*gridSize + yOffset;
		return result;
	}
	
	public static Coordinate absoluteToGrid(Coordinate c, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		Coordinate result = c.clone();
		result.x = (result.x - xOffset)/gridSize - gridOffsetX;
		result.y = (result.y - yOffset)/gridSize - gridOffsetY;
		
		return result;
	}
}

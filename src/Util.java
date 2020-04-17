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

public class Util {

	static int modPos(int a, int b) {
		int result = a % b;
		if(result < 0) result += b;
		return result;
	}
	
	static Coordinate gridToAbsolute(Coordinate c, int gridX, int gridY) {
		int xOffset = Util.modPos(gridX, Simulator.gridSize);
		int gridOffsetX = (gridX - xOffset) / Simulator.gridSize;

		int yOffset = Util.modPos(gridY, Simulator.gridSize);
		int gridOffsetY = (gridY - yOffset) / Simulator.gridSize;
		
		//in grid coordinates
		Coordinate result = c.clone();
		
		result.x = (result.x + gridOffsetX)*Simulator.gridSize + xOffset;
		result.y = (result.y + gridOffsetY)*Simulator.gridSize + yOffset;
		return result;
	}
	
	static Coordinate absoluteToGrid(Coordinate c, int gridX, int gridY) {
		int xOffset = Util.modPos(gridX, Simulator.gridSize);
		int gridOffsetX = (gridX - xOffset) / Simulator.gridSize;

		int yOffset = Util.modPos(gridY, Simulator.gridSize);
		int gridOffsetY = (gridY - yOffset) / Simulator.gridSize;
		
		Coordinate result = c.clone();
		result.x = (result.x - xOffset)/Simulator.gridSize - gridOffsetX;
		result.y = (result.y - yOffset)/Simulator.gridSize - gridOffsetY;
		
		return result;
	}
}

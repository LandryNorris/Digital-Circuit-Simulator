
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

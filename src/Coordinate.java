
public class Coordinate {
	int x, y;
	Coordinate(){}
	Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	Coordinate snapToGrid() {
		return new Coordinate(Simulator.gridSize*((int) x/Simulator.gridSize), Simulator.gridSize*((int) y/Simulator.gridSize));
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

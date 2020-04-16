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
	
	int gridX;
	int gridY;
	int r = Simulator.gridSize/2;
	
	byte getState() {
		return state;
	}
	
	void setState(byte s) {
		state = s;
	}
	
	void draw(Graphics g) {
		int xOffset = Util.modPos(gridX, Simulator.gridSize);
		int gridOffsetX = (gridX - xOffset) / Simulator.gridSize;

		int yOffset = Util.modPos(gridY, Simulator.gridSize);
		int gridOffsetY = (gridY - yOffset) / Simulator.gridSize;
		g.setColor(Color.GREEN);
		g.drawOval((x+gridOffsetX)*Simulator.gridSize-r + xOffset, (y+gridOffsetY)*Simulator.gridSize-r + yOffset, r*2, r*2);
	}
	
	void setXY(int x, int y, int gridX, int gridY) {

		this.gridX = gridX;
		this.gridY = gridY;

		this.x = x;
		this.y = y;
	}
	
	boolean clicked(int mouseX, int mouseY) {
		Coordinate c = Util.gridToAbsolute(new Coordinate(x, y), gridX, gridY);
		double distance = Math.sqrt((mouseX-c.x)*(mouseX-c.x) + (mouseY-c.y)*(mouseY-c.y));
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

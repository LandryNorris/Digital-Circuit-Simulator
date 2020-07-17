package com.landry.digitalcircuitsimulator.gates;

import java.awt.Color;
import java.awt.Graphics2D;

import com.landry.digitalcircuitsimulator.Component;
import com.landry.digitalcircuitsimulator.Pin;
import com.landry.digitalcircuitsimulator.Simulator;
import com.landry.digitalcircuitsimulator.Util;

public class Inverter extends Component {
	{
		w = 2;
		h = w;
	}
	
	public Inverter() {
		inputs = new Pin[1];
		outputs = new Pin[1];
		initPins();
	}
	
	public Inverter(int componentNumber) {
		inputs = new Pin[1];
		outputs = new Pin[1];
		initPins(componentNumber);
	}
	
	protected void setPinLocations() {
		outputs[0].setXY((this.x+w), (this.y+h/2));
		inputs[0].setXY((this.x), (this.y+h/2));
	}
	
	protected void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
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
	protected void update() {
		if(inputsDefined()) {
			outputs[0].setState((byte) ((inputs[0].getState() == 1) ? 0 : 1));
		}
	}
	
	protected int getType() {
		return Component.Type.INVERTER;
	}
}

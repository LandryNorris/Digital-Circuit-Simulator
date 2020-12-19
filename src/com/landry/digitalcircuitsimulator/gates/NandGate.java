package com.landry.digitalcircuitsimulator.gates;

import java.awt.Color;
import java.awt.Graphics2D;

import com.landry.digitalcircuitsimulator.Component;
import com.landry.digitalcircuitsimulator.Pin;
import com.landry.digitalcircuitsimulator.Simulator;
import com.landry.digitalcircuitsimulator.State;
import com.landry.digitalcircuitsimulator.Util;

public class NandGate extends Component {

	{
		w = 2;
		h = (int) (w * 2);
	}

	public NandGate() {
		inputs = new Pin[2];
		outputs = new Pin[1];
		initPins();
	}

	public NandGate(int componentNumber) {
		inputs = new Pin[2];
		outputs = new Pin[1];
		initPins(componentNumber);
		setPinLocations();
	}
	
	protected void setPinLocations() {
		outputs[0].setXY((x + w) + 1, (y + h / 2));

		inputs[0].setXY(x, (y + h / 4));
		inputs[1].setXY(x, (y + h * 3 / 4));
	}

	@Override
	protected void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		int lineX = (x + gridOffsetX)*gridSize + xOffset;
		int lineY = (y + gridOffsetY)*gridSize + yOffset;
		g.drawLine(lineX, lineY, lineX, lineY + h*gridSize);
		g.drawArc(lineX - w*gridSize, lineY, (w * 2)*gridSize, h*gridSize, 90, -180);
		int notR = gridSize/2;
		g.drawOval((x+w+gridOffsetX)*gridSize+xOffset, (y+h/2+gridOffsetY)*gridSize-notR + yOffset, 2*notR, 2*notR);
		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	protected void update() {
		if (inputsDefined()) {
			outputs[0].setState((byte) ((inputs[0].getState() == 1 && inputs[1].getState() == 1) ? 0 : 1));
		}

		else {
			outputs[0].setState(State.LOW);
		}
	}

	protected int getType() {
		return Component.Type.NAND;
	}
}

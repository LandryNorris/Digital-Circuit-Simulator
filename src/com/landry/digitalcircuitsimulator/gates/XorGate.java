package com.landry.digitalcircuitsimulator.gates;

import java.awt.Color;
import java.awt.Graphics2D;

import com.landry.digitalcircuitsimulator.Component;
import com.landry.digitalcircuitsimulator.Pin;
import com.landry.digitalcircuitsimulator.Simulator;
import com.landry.digitalcircuitsimulator.State;
import com.landry.digitalcircuitsimulator.Util;

public class XorGate extends Component {

	{
		w = 2;
		h = (int) (w * 2);
	}

	public XorGate() {
		inputs = new Pin[2];
		outputs = new Pin[1];
		initPins();
	}

	public XorGate(int componentNumber) {
		inputs = new Pin[2];
		outputs = new Pin[1];
		initPins(componentNumber);
	}
	
	protected void setPinLocations() {
		outputs[0].setXY((x + w), (y + h / 2));
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
		int curveR = (int) (1.5*h*gridSize);
		int dx = (int) Math.sqrt(curveR*curveR - (h/2)*h/2*gridSize*gridSize);
		int angle = (int) Math.toDegrees(Math.asin(h*gridSize/(2.0*curveR)));
		g.drawArc((x+gridOffsetX)*gridSize+xOffset-curveR-dx, (y + h/2 + gridOffsetY)*gridSize + yOffset - curveR, curveR*2, curveR*2, angle, -2*angle);
		g.drawArc((x - w + gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w * 2*gridSize, h*gridSize, 90, -180);
		g.drawArc((x+gridOffsetX)*gridSize - w*gridSize/4+xOffset-curveR-dx, (y + h/2 + gridOffsetY)*gridSize + yOffset - curveR, curveR*2, curveR*2, angle, -2*angle);

		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	protected void update() {
		if (inputsDefined()) {
			outputs[0].setState((byte) ((inputs[0].getState() == 1 ^ inputs[1].getState() == 1) ? 1 : 0));
		}

		else {
			outputs[0].setState(State.LOW);
		}
	}

	protected int getType() {
		return Component.Type.XOR;
	}
}

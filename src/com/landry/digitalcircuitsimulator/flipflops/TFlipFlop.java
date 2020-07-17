package com.landry.digitalcircuitsimulator.flipflops;

import java.awt.Color;
import java.awt.Graphics2D;

import com.landry.digitalcircuitsimulator.Component;
import com.landry.digitalcircuitsimulator.Pin;
import com.landry.digitalcircuitsimulator.Simulator;
import com.landry.digitalcircuitsimulator.State;
import com.landry.digitalcircuitsimulator.Util;

public class TFlipFlop extends Component {

	byte lastState = State.UNDEFINED;
	{
		w = 4;
		h = w;
		state = State.LOW;
	}

	public TFlipFlop() {
		inputs = new Pin[3];
		outputs = new Pin[2];
		initPins();
	}

	public TFlipFlop(int componentNumber) {
		inputs = new Pin[3];
		outputs = new Pin[2];
		initPins(componentNumber);
		setPinLocations();
	}
	
	protected void setPinLocations() {
		outputs[0].setXY((x + w), (y + 1));
		outputs[1].setXY((x+w), y+3);
		inputs[0].setXY(x, y+1);
		inputs[1].setXY(x, y+3);
		inputs[2].setXY(x+1, y+h);
	}

	@Override
	protected void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		String s = "T";
		int textWidth = g.getFontMetrics().stringWidth(s);
		int textHeight = g.getFontMetrics().getHeight();
		g.drawRect((x+gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w*gridSize, h*gridSize);
		g.drawString(s, (x + gridOffsetX + w / 2)*gridSize + xOffset - textWidth / 2, (y + gridOffsetY + h / 2)*gridSize + yOffset + textHeight / 2);

		setPinLocations();
		inputs[0].draw(g, gridX, gridY, gridSize);
		inputs[1].draw(g, gridX, gridY, gridSize);
		inputs[2].draw(g, gridX, gridY, gridSize);
		outputs[0].draw(g, gridX, gridY, gridSize);
		outputs[1].draw(g, gridX, gridY, gridSize);
	}

	@Override
	protected void update() {
		//active low.
		if(inputs[2].getState() == State.LOW) {
			outputs[0].setState(State.LOW);
			outputs[1].setState(State.HIGH);
		}
		else if(inputs[1].getState() == State.LOW && lastState == State.HIGH && inputs[0].getState() == State.HIGH) {
			outputs[0].setState((byte) ((outputs[0].getState() == 1) ? 0 : 1));
			outputs[1].setState((byte) ((outputs[0].getState() == 1) ? 0 : 1));
		}
		lastState = inputs[1].getState();
	}

	protected int getType() {
		return Component.Type.TFF;
	}
}

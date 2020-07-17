

package com.landry.digitalcircuitsimulator.inputs;

import java.awt.Color;
import java.awt.Graphics2D;

import com.landry.digitalcircuitsimulator.Component;
import com.landry.digitalcircuitsimulator.Pin;
import com.landry.digitalcircuitsimulator.Simulator;
import com.landry.digitalcircuitsimulator.State;
import com.landry.digitalcircuitsimulator.Util;

public class Input extends Component {

	{
		w = 2;
		h = w;
		state = State.LOW;
	}

	public Input() {
		inputs = new Pin[0];
		outputs = new Pin[1];
		initPins();
	}

	public Input(int componentNumber) {
		inputs = new Pin[0];
		outputs = new Pin[1];
		initPins(componentNumber);
		setPinLocations();
	}
	
	String text() {
		switch (state) {
			case State.TRUE:
				return "1";
			case State.FALSE:
				return "0";
			case State.UNDEFINED:
				return "X";
		}
		return "error: invalid state";
	}
	
	protected void setPinLocations() {
		outputs[0].setXY((x + w), (y + h / 2));
	}

	@Override
	protected void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;
		
		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		String s = text();
		int textWidth = g.getFontMetrics().stringWidth(s);
		int textHeight = g.getFontMetrics().getHeight();
		g.drawRect((x+gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w*gridSize, h*gridSize);
		g.drawString(s, (x + gridOffsetX + w / 2)*gridSize + xOffset - textWidth / 2, (y + gridOffsetY + h / 2)*gridSize + yOffset + textHeight / 2);

		setPinLocations();
		outputs[0].draw(g, gridX, gridY, gridSize);
	}

	@Override
	protected void update() {
		outputs[0].setState(state);
	}
	
	@Override
	public void rightClick() {
		toggle();
	}

	void toggle() {
		if (state == State.LOW)
			state = State.HIGH;
		else if (state == State.HIGH)
			state = State.LOW;
	}

	protected int getType() {
		return Component.Type.INPUT;
	}
}
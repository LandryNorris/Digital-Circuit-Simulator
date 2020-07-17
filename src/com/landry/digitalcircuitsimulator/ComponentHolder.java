package com.landry.digitalcircuitsimulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class ComponentHolder extends JComponent {
	
	Component component;
	/**height in grid space*/
	int h = 4;
	/**width in grid space*/
	int w = 4;
	
	int gridSize;
	
	ComponentHolder(Component c) {
		component = c;
		w = (c.w > c.h) ? c.w : c.h;
		h = w;
	}
	
	ComponentHolder(Component c, int size) {
		component = c;
		component.x = 1;
		component.y = 1;
		w = (c.w > c.h) ? c.w + 2: c.h + 2;
		h = w;
		setPreferredSize(new Dimension(size, size));
	}

	@Override
	protected void paintComponent(Graphics g) {

		int width = getWidth();
		int height = getHeight();
		int maxSize = (w > h) ? w : h;
		gridSize = height / (maxSize);
		
		System.out.println(height + ", " + gridSize);
		
		Graphics2D graphics = (Graphics2D) g;
		graphics.setColor(Color.BLACK);
		for(int i = 0; i < height; i += gridSize) {
			int lineY = i;
			graphics.drawLine(0, lineY, getWidth(), lineY);
		}
		
		for(int i = 0; i < width; i += gridSize) {
			int lineX = i;
			graphics.drawLine(lineX, 0, lineX, getHeight());
		}
		
		component.draw(graphics, 0, 0, gridSize);
		
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(10));
		graphics.drawLine(0, 0, 0, getHeight());
		graphics.drawLine(0, 0, getWidth(), 0);
		graphics.drawLine(getWidth(), 0, getWidth(), getHeight());
		graphics.drawLine(0, getHeight(), getWidth(), getHeight());
	}
}

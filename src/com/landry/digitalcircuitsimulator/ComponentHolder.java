package com.landry.digitalcircuitsimulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JComponent;

public class ComponentHolder extends JComponent {
	
	private static final long serialVersionUID = -7053238690079273907L;
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
		w = (c.w > c.h) ? c.w + 2 : c.h + 2;
		h = w;
		component.x = (w - c.w)/2;
		component.y = (h - c.h)/2;
		setPreferredSize(new Dimension(size, size));
		setMaximumSize(new Dimension(size, size));
		//System.out.println("size = " + size);
	}

	@Override
	protected void paintComponent(Graphics g) {

		int strokeSize = 5;
		//width and height should be identical
		int width = getWidth();
		int height = getHeight() - strokeSize;
		int maxSize = (w > h) ? w : h;
		gridSize = height / (maxSize);
		//System.out.println(maxSize);
		
		//System.out.println(height + ", " + gridSize);
		
		Graphics2D graphics = (Graphics2D) g;
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(Color.BLACK);
		for(int i = strokeSize; i < height; i += gridSize) {
			int lineY = i;
			graphics.drawLine(strokeSize, lineY, getWidth() - strokeSize, lineY);
		}
		
		for(int i = 0; i < width; i += gridSize) {
			int lineX = i;
			graphics.drawLine(lineX, 0, lineX, getHeight() - 0);
		}
		
		component.draw(graphics, 0, strokeSize, gridSize);
		
		//draw border.
		Stroke defaultStroke = graphics.getStroke();
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke(strokeSize*2));
		graphics.drawLine(0, 0, 0, getHeight());
		graphics.drawLine(0, 0, getWidth(), 0);
		graphics.drawLine(getWidth(), 0, getWidth(), getHeight());
		graphics.drawLine(0, getHeight(), getWidth(), getHeight());
		
		graphics.setStroke(defaultStroke);
	}
}

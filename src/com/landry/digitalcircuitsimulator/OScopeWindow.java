package com.landry.digitalcircuitsimulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class OScopeWindow extends JPanel {

	private static final long serialVersionUID = -6571386642913654710L;

	@Override
	public void paint(Graphics graphics) {
		int width = getWidth();
		int height = getHeight();
		Graphics2D g = (Graphics2D) graphics;
		
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, width, height);
		
		g.setColor(Color.BLACK);
	}
}

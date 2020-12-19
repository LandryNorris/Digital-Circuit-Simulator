/*
 Copyright (c) 2020 Landry Norris. This file is part of
 Digital Circuit Simulator, an open source simulator for digital circuits.
 Digital Circuit Simulator is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 Digital Circuit Simulator is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with the Digital Circuit Simulator Library.  If not, see http://www.gnu.org/licenses.
 */
package com.landry.digitalcircuitsimulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.landry.digitalcircuitsimulator.flipflops.DFlipFlop;
import com.landry.digitalcircuitsimulator.flipflops.TFlipFlop;
import com.landry.digitalcircuitsimulator.gates.AndGate;
import com.landry.digitalcircuitsimulator.gates.Inverter;
import com.landry.digitalcircuitsimulator.gates.NandGate;
import com.landry.digitalcircuitsimulator.gates.NorGate;
import com.landry.digitalcircuitsimulator.gates.OrGate;
import com.landry.digitalcircuitsimulator.gates.XnorGate;
import com.landry.digitalcircuitsimulator.gates.XorGate;
import com.landry.digitalcircuitsimulator.inputs.Input;
import com.landry.digitalcircuitsimulator.outputs.Output;
import com.landry.digitalcircuitsimulator.roms.Rom8;

public class Simulator extends JComponent implements MouseListener, MouseMotionListener, KeyListener {

	String name = "";
	int selectedComponentIndex = -1;
	int drawWireIndex = -1;

	int lastMouseX = -1;
	int lastMouseY = -1;

	// int getWidth();
	// int getHeight();
	int x; // x in the window
	int y; // y in the window

	int gridX; // x coordinate of the top left grid corner (absolute coordinate)
	int gridY; // y coordinate of the top left grid corner (absolute coordinate)

	boolean requestFocus = true;

	int gridSize = 10;

	static Stroke thinStroke = new BasicStroke(1F);
	public static Stroke thickStroke = new BasicStroke(2F);

	MouseEvent mousePressedEvent;
	MouseEvent mouseDraggedEvent;
	MouseEvent mouseMovedEvent;

	KeyEvent keyPressedEvent;
	KeyEvent keyTypedEvent;

	ArrayList<Component> components = new ArrayList<Component>();
	ArrayList<Wire> wires = new ArrayList<Wire>();

	SaveManager saveManager;
	
	OScope oscope;

	Simulator() {
	}

	Pin pinAt(int x, int y) {
		// inputs are grid coordinates
		for (int i = 0; i < components.size(); i++) {
			Pin pin = components.get(i).pinAt(x, y);
			if (pin != null)
				return pin;
		}
		return null;
	}

	Pin pinClicked(double x, double y) {
		// inputs are absolute coordinates
		for (int i = 0; i < components.size(); i++) {
			Pin pin = components.get(i).pinClicked(x, y);
			if (pin != null)
				return pin;
		}
		return null;
	}

	@Override
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.LIGHT_GRAY);
		g.setStroke(thinStroke);
		int xOffset = gridX % gridSize;
		int yOffset = gridY % gridSize;
		for (int x = xOffset; x < getWidth(); x += gridSize) {
			g.drawLine(x, 0, x, getHeight());
		}
		for (int y = yOffset; y < getHeight(); y += gridSize) {
			g.drawLine(0, y, getWidth(), y);
		}
		
		//draw (0, 0) marker
		g.setColor(Color.BLACK);
		Coordinate c = Util.gridToAbsolute(new Coordinate(0, 0), gridX, gridY, gridSize);
		g.drawOval(c.x - gridSize/2, c.y - gridSize/2, gridSize, gridSize);
		
		for (Component component : components) {
			if (true || component.onScreen(gridX, gridY, getWidth() / gridSize, getHeight() / gridSize)) // placeholder for future functionality.
				component.draw(g, gridX, gridY, gridSize);
		}

		for (Wire wire : wires) {
			wire.draw(g, gridX, gridY, gridSize);
		}
	}

	void update() {
		for (Component component : components) {
			component.update();
		}
	}
	
	void attachWires() {
		System.out.println("clearing wire outputs");
		for(int i = 0; i < wires.size(); i++) {
			wires.get(i).outputs = new Wire[0];
			wires.get(i).hasInput = false;
		}
		
		for(int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			for(int j = 0; j < component.outputs.length; j++) {
				component.outputs[j].outputs = new Wire[0];
			}
		}
		
		System.out.println("starting attachWires");
		for(int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			for(int j = 0; j < component.outputs.length; j++) {
				System.out.println("attaching outputs");
				attachToOutputs(component.outputs[j], -1);
			}
		}
	}
	
	void attachToOutputs(Wire wire, int index) {
		for(int i = 0; i < components.size(); i++) {
			Component component = components.get(i);
			for(int j = 0; j < wire.points.length; j++) {
				Coordinate point = wire.points[j];
				Pin pin = component.pinAt(point.x, point.y);
				if(pin != null && !pin.equals(wire) && pin.isInput) {
					System.out.println("attaching to pin");
					wire.addOutput(pin);
					continue;
				}
			}
		}
		
		for(int i = 0; i < wires.size(); i++) {
			Wire other = wires.get(i);
			if(index == i) continue;
			if(contains(other.outputs, wire) || other.hasInput) {
				continue;
			}
			
			for(int j = 0; j < wire.points.length; j++) {
				Coordinate point = wire.points[j];
				if(other.hasPointAt(point)) {
					wire.addOutput(other);
					other.hasInput = true;
					attachToOutputs(other, i);
				}
			}
		}
	}
	
	boolean contains(Object[] array, Object item) {
		if(array == null) return false;
		for(int i = 0; i < array.length; i++) {
			if(array[i].equals(item)) return true;
		}
		return false;
	}

	int snapToGridX(int coordinate) {
		int offset = Util.modPos(gridX, gridSize);
		int gridOffset = (gridX - offset) / gridSize;

		// returns value in grid coordinates
		return ((int) (coordinate - offset) / gridSize - gridOffset);
	}

	int snapToGridY(int coordinate) {
		int offset = Util.modPos(gridY, gridSize);
		int gridOffset = (gridY - offset) / gridSize;

		// returns value in grid coordinates
		return ((int) (coordinate - offset) / gridSize - gridOffset);
	}

	void onMousePressed(MouseEvent e) {
		double mouseX = Util.map(e.getX(), gridX, gridX + gridSize, 0, 1); // in grid space
		double mouseY = Util.map(e.getY(), gridY, gridY + gridSize, 0, 1); // in grid space
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (selectedComponentIndex != -1) {
				selectedComponentIndex = -1;
				attachWires();
				return;
			}

			if (drawWireIndex == -1) {
				Pin pin = pinClicked(mouseX, mouseY);
				if (pin != null) {
					//starting a wire
					wires.add(Wire.start(pin));
					drawWireIndex = wires.size() - 1;
				} else {
					for (int i = 0; i < components.size(); i++) {
						if (components.get(i).clicked(mouseX, mouseY)) {
							//component selected
							selectedComponentIndex = i;
						}
					}
				}
			}

			else if (drawWireIndex != -1) {
				//add a point to a wire.
				wires.get(drawWireIndex).addPoint((new Coordinate(snapToGridX(e.getX()), snapToGridY(e.getY()))));
				attachWires();
			}
		}

		// right-click
		else if (e.getButton() == MouseEvent.BUTTON3) {
			for (Component component : components) {
				if(component.clicked(mouseX, mouseY)) {
					component.rightClick();
				}
			}
		}
	}
	
	void startOScope(int channels, int size) {
		oscope = new OScope(channels, size);
	}

	void print() {
		// Canvas canvas = new Canvas();
		BufferedImage bImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = (Graphics) bImg.createGraphics();
		paintComponent(g);
		g.dispose();
		try {
			ImageIO.write(bImg, "png", new File("Image.png"));
			System.out.println("created png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void onMouseDragged(MouseEvent e) {
		if (selectedComponentIndex != -1) {
			components.get(selectedComponentIndex).x = snapToGridX(e.getX());
			components.get(selectedComponentIndex).y = snapToGridY(e.getY());
		}

		if (drawWireIndex != -1 && pinClicked(snapToGridX(e.getX()), snapToGridY(e.getY())) != null) {
			System.out.println("Attaching wire.");
			wires.get(drawWireIndex).setEndpoint((new Coordinate(snapToGridX(e.getX()), snapToGridY(e.getY()))));
		}

		else {
			if (lastMouseX != -1) {
				gridX += e.getX() - lastMouseX;
				gridY += e.getY() - lastMouseY;
			}
			lastMouseX = e.getX();
			lastMouseY = e.getY();
		}
	}

	void onMouseMoved(MouseEvent e) {
		if (selectedComponentIndex != -1) {
			components.get(selectedComponentIndex).x = snapToGridX(e.getX());
			components.get(selectedComponentIndex).y = snapToGridY(e.getY());
		}

		if (drawWireIndex != -1) {
			wires.get(drawWireIndex).setEndpoint((new Coordinate(snapToGridX(e.getX()), snapToGridY(e.getY()))));
		}
	}

	void onKeyTyped(KeyEvent e) {
		if (selectedComponentIndex == -1 && e.getKeyChar() == 'a') {
			selectedComponentIndex = components.size();
			components.add(new AndGate(selectedComponentIndex));
		}

		else if (selectedComponentIndex == -1 && e.getKeyChar() == 'A') {
			System.out.println("test");
			selectedComponentIndex = components.size();
			components.add(new NandGate(selectedComponentIndex));
		}

		else if (selectedComponentIndex == -1 && e.getKeyChar() == 'i') {
			selectedComponentIndex = components.size();
			components.add(new Input(selectedComponentIndex));
		}

		else if (selectedComponentIndex == -1 && e.getKeyChar() == 'o') {
			selectedComponentIndex = components.size();
			components.add(new Output(selectedComponentIndex));
		}

		else if (selectedComponentIndex == -1 && e.getKeyChar() == 'r') {
			selectedComponentIndex = components.size();
			components.add(new OrGate(selectedComponentIndex));
		}

		else if (selectedComponentIndex == -1 && e.getKeyChar() == 'R') {
			selectedComponentIndex = components.size();
			components.add(new NorGate(selectedComponentIndex));
		}

		else if (selectedComponentIndex == -1 && e.getKeyChar() == 'I') {
			selectedComponentIndex = components.size();
			components.add(new Inverter(selectedComponentIndex));
		}

		else if (selectedComponentIndex == -1 && e.getKeyChar() == 'x') {
			selectedComponentIndex = components.size();
			components.add(new XorGate(selectedComponentIndex));
		}

		else if (selectedComponentIndex == -1 && e.getKeyChar() == 'X') {
			selectedComponentIndex = components.size();
			components.add(new XnorGate(selectedComponentIndex));
		}

		else if (selectedComponentIndex == -1 && e.getKeyChar() == 'd') {
			selectedComponentIndex = components.size();
			components.add(new DFlipFlop(selectedComponentIndex));
		}

		else if (selectedComponentIndex == -1 && e.getKeyChar() == 't') {
			selectedComponentIndex = components.size();
			components.add(new TFlipFlop(selectedComponentIndex));
		}
		
		else if (selectedComponentIndex == -1 && e.getKeyChar() == 'm') {
			selectedComponentIndex = components.size();
			components.add(new Rom8(selectedComponentIndex));
		}
		
		else if(e.getKeyChar() == 'l') {
			new LegalWindow();
		}
	}

	void onKeyPressed(KeyEvent e) {

		if (e.getKeyCode() == 27) {
			System.out.println("escapePressed");
			if (drawWireIndex != -1) {
				wires.get(drawWireIndex).end();
				drawWireIndex = -1;
			}
		}

		if (e.isControlDown() && e.getKeyCode() == 83) {
			System.out.println("Saving!");
			saveManager = new SaveManager(name);
			try {
				saveManager.save(this);
				saveManager.flush();
			} catch (IOException e1) {
				System.out.println("failed to save");
				e1.printStackTrace();
			}
		}

		if (selectedComponentIndex != -1 && e.getKeyCode() == KeyEvent.VK_DELETE) {
			System.out.println("deleting component");
			components.remove(selectedComponentIndex);
			selectedComponentIndex = -1;
		}
	}

	void checkEvents() {
		if (mousePressedEvent != null) {
			onMousePressed(mousePressedEvent);
			mousePressedEvent = null;
		}

		if (mouseDraggedEvent != null) {
			onMouseDragged(mouseDraggedEvent);
			mouseDraggedEvent = null;
		}

		if (mouseMovedEvent != null) {
			onMouseMoved(mouseMovedEvent);
			mouseMovedEvent = null;
		}

		if (keyTypedEvent != null) {
			onKeyTyped(keyTypedEvent);
			keyTypedEvent = null;
		}

		if (keyPressedEvent != null) {
			onKeyPressed(keyPressedEvent);
			keyPressedEvent = null;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressedEvent = e;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		lastMouseX = -1;
		lastMouseY = -1;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		requestFocus = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseDraggedEvent = e;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseMovedEvent = e;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		keyTypedEvent = e;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyPressedEvent = e;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}

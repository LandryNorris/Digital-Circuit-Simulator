import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;

public class Simulator implements MouseListener, MouseMotionListener, KeyListener {

	String name = "";
	int selectedComponentIndex = -1;
	int drawWireIndex = -1;

	int lastMouseX = -1;
	int lastMouseY = -1;

	int width;
	int height;
	int x; // x in the window
	int y; // y in the window

	int gridX; // x coordinate of the top left grid corner (absolute coordinate)
	int gridY; // y coordinate of the top left grid corner (absolute coordinate)

	boolean requestFocus = true;

	static int gridSize = 10;

	static Stroke thinStroke = new BasicStroke(1F);
	static Stroke thickStroke = new BasicStroke(2F);

	MouseEvent mousePressedEvent;
	MouseEvent mouseDraggedEvent;
	MouseEvent mouseMovedEvent;

	KeyEvent keyPressedEvent;
	KeyEvent keyTypedEvent;

	ArrayList<Component> components = new ArrayList<Component>();
	ArrayList<Wire> wires = new ArrayList<Wire>();

	SaveManager saveManager;

	Simulator() {
	}

	Pin pinAt(int x, int y) {
		//inputs are grid coordinates
		for(int i = 0; i < components.size(); i++) {
			Pin pin = components.get(i).pinAt(x, y);
			if(pin != null)
				return pin;
		}
		return null;
	}

	Pin pinClicked(int x, int y) {
//		// inputs are absolute coordinates
		for(int i = 0; i < components.size(); i++) {
			Pin pin = components.get(i).pinClicked(x, y);
			if(pin != null)
				return pin;
		}
		return null;
	}

	void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.LIGHT_GRAY);
		g.setStroke(thinStroke);
		int xOffset = gridX % Simulator.gridSize;
		int yOffset = gridY % Simulator.gridSize;
		for(int x = xOffset; x < width; x += gridSize) {
			g.drawLine(x, 0, x, height);
		}
		for(int y = yOffset; y < height; y += gridSize) {
			g.drawLine(0, y, width, y);
		}

		for(Component component: components) {
			if(component.onScreen(x, y, width, height))
				component.draw(g, gridX, gridY);
		}

		for(Wire wire: wires) {
			wire.draw(g, gridX, gridY);
		}
		Coordinate c = Util.gridToAbsolute(new Coordinate(0, 0), gridX, gridY);
		g.drawOval(c.x, c.y, 5, 5);
	}

	void update() {
		for(Wire wire: wires) {
			wire.update(components, wires);
		}
		for(Component component: components) {
			component.update();
		}
	}
	
	void attachWires() {
		//clear wires
		for(int i = 0; i < wires.size(); i++) {
			wires.get(i).outputs = new Pin[0];
			wires.get(i).input = null;
		}
		
		//first, attach wires to components.
		for(int i = 0; i < wires.size(); i++) {
			Wire w = wires.get(i);
			for(int j = 0; j < w.points.length; j++) {
				Coordinate point = wires.get(i).points[j];
				Pin pin = pinAt(point.x, point.y);
				if(pin != null) {
					if(pin.isInput) {
						wires.get(i).addOutput(pin);
					} else {
						wires.get(i).input = pin;
					}
				}
			}
		}
	}

	int snapToGridX(int coordinate) {
		int offset = Util.modPos(gridX, Simulator.gridSize);
		int gridOffset = (gridX - offset) / Simulator.gridSize;

		// returns value in grid coordinates
		return ((int) (coordinate - offset) / Simulator.gridSize - gridOffset);
	}

	int snapToGridY(int coordinate) {
		int offset = Util.modPos(gridY, Simulator.gridSize);
		int gridOffset = (gridY - offset) / Simulator.gridSize;

		// returns value in grid coordinates
		return ((int) (coordinate - offset) / Simulator.gridSize - gridOffset);
	}

	void onMousePressed(MouseEvent e) {

		if(e.getButton() == MouseEvent.BUTTON1) {
			System.out.println("left click!");
			if(selectedComponentIndex != -1) {
				selectedComponentIndex = -1;
				System.out.println("deselecting component");
				return;
			}

			if(drawWireIndex == -1) {
				Pin pin = pinClicked(e.getX(), e.getY());
				if(pin != null) {
					wires.add(Wire.start(pin));
					System.out.println("starting wire");
					drawWireIndex = wires.size() - 1;
				} else {
					for(int i = 0; i < components.size(); i++) {
						System.out.println("checking components at " + e.getX() + ", " + e.getY());
						if(components.get(i).clicked(e.getX(), e.getY(), gridX, gridY)) {
							selectedComponentIndex = i;
							System.out.println("dragging component");
						}
					}
				}
			}

			else if(drawWireIndex != -1) {
				wires.get(drawWireIndex).addPoint((new Coordinate(snapToGridX(e.getX()), snapToGridY(e.getY()))));
				System.out.println("adding point to wire");

				Pin pin = pinAt(snapToGridX(e.getX()), snapToGridY(e.getY()));
				if(pin != null) {
					System.out.println("attaching to pin.");
					if(pin.isInput) {
						wires.get(drawWireIndex).addOutput(pin);
					} else if(wires.get(drawWireIndex).input == null) {
						wires.get(drawWireIndex).input = pin;
					}
				}
			}
		}

		else if(e.getButton() == MouseEvent.BUTTON3) {
			System.out.println("Button 3 pressed");
			for(Component component: components) {
				if(component instanceof Input && component.clicked(e.getX(), e.getY(), gridX, gridY)) {
					((Input) component).toggle();
				}
			}
		}
	}

	void onMouseDragged(MouseEvent e) {
		if(selectedComponentIndex != -1) {
			components.get(selectedComponentIndex).x = snapToGridX(e.getX());
			components.get(selectedComponentIndex).y = snapToGridY(e.getY());
		}

		if(drawWireIndex != -1 && pinClicked(snapToGridX(e.getX()), snapToGridY(e.getY())) != null) {
			wires.get(drawWireIndex).setEndpoint((new Coordinate(snapToGridX(e.getX()), snapToGridY(e.getY()))));
		}

		else {
			if(lastMouseX != -1) {
				gridX += e.getX() - lastMouseX;
				gridY += e.getY() - lastMouseY;
			}
			lastMouseX = e.getX();
			lastMouseY = e.getY();
		}
	}

	void onMouseMoved(MouseEvent e) {
		if(selectedComponentIndex != -1) {
			components.get(selectedComponentIndex).x = snapToGridX(e.getX());
			components.get(selectedComponentIndex).y = snapToGridY(e.getY());
		}

		if(drawWireIndex != -1) {
			wires.get(drawWireIndex).setEndpoint((new Coordinate(snapToGridX(e.getX()), snapToGridY(e.getY()))));
		}
	}

	void onKeyTyped(KeyEvent e) {
		if(selectedComponentIndex == -1 && e.getKeyChar() == 'a') {
			selectedComponentIndex = components.size();
			components.add(new AndGate(selectedComponentIndex));
		}

		else if(selectedComponentIndex == -1 && e.getKeyChar() == 'A') {
			selectedComponentIndex = components.size();
			components.add(new NandGate(selectedComponentIndex));
		}

		else if(selectedComponentIndex == -1 && e.getKeyChar() == 'i') {
			selectedComponentIndex = components.size();
			components.add(new Input(selectedComponentIndex));
		}

		else if(selectedComponentIndex == -1 && e.getKeyChar() == 'o') {
			selectedComponentIndex = components.size();
			components.add(new Output(selectedComponentIndex));
		}

		else if(selectedComponentIndex == -1 && e.getKeyChar() == 'r') {
			selectedComponentIndex = components.size();
			components.add(new OrGate(selectedComponentIndex));
		}

		else if(selectedComponentIndex == -1 && e.getKeyChar() == 'R') {
			selectedComponentIndex = components.size();
			components.add(new NorGate(selectedComponentIndex));
		}

		else if(selectedComponentIndex == -1 && e.getKeyChar() == 'I') {
			selectedComponentIndex = components.size();
			components.add(new Inverter(selectedComponentIndex));
		}

		else if(selectedComponentIndex == -1 && e.getKeyChar() == 'x') {
			selectedComponentIndex = components.size();
			components.add(new XorGate(selectedComponentIndex));
		}

		else if(selectedComponentIndex == -1 && e.getKeyChar() == 'X') {
			selectedComponentIndex = components.size();
			components.add(new XnorGate(selectedComponentIndex));
		}
		
		else if(e.getKeyChar() == 'u') {
			attachWires();
		}
	}

	void onKeyPressed(KeyEvent e) {

		if(e.getKeyCode() == 27) {
			System.out.println("escapePressed");
			if(drawWireIndex != -1) {
				wires.get(drawWireIndex).end();
				drawWireIndex = -1;
			}
		}

		if(e.isControlDown() && e.getKeyCode() == 83) {
			System.out.println("Saving!");
			saveManager = new SaveManager(name);
			try {
				saveManager.save(this);
				saveManager.flush();
			} catch(IOException e1) {
				System.out.println("failed to save");
				e1.printStackTrace();
			}
		}
	}

	void checkEvents() {
		if(mousePressedEvent != null) {
			onMousePressed(mousePressedEvent);
			mousePressedEvent = null;
		}

		if(mouseDraggedEvent != null) {
			onMouseDragged(mouseDraggedEvent);
			mouseDraggedEvent = null;
		}

		if(mouseMovedEvent != null) {
			onMouseMoved(mouseMovedEvent);
			mouseMovedEvent = null;
		}

		if(keyTypedEvent != null) {
			onKeyTyped(keyTypedEvent);
			keyTypedEvent = null;
		}

		if(keyPressedEvent != null) {
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

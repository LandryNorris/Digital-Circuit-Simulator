package com.landry.digitalcircuitsimulator;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;

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

public class ComponentSelector extends JScrollPane implements ComponentListener {

	private static final long serialVersionUID = -1499163292165502902L;

	JPanel panel = new JPanel();
	int rows;
	private OnComponentSelectedListener onComponentSelectedListener;
	
	Popup popup;
	PopupFactory factory = new PopupFactory();
	
	boolean componentsAdded = false;

	
	ComponentSelector(int rows, int cols) {
		this.rows = rows;
		panel.setLayout(new GridLayout(rows, cols));
		addComponentListener(this);
		setViewportView(panel);
	}
	
	void setOnComponentSelectedListener(OnComponentSelectedListener listener) {
		onComponentSelectedListener = listener;
	}

	void addComponents() {
		//System.out.println("scrollbar width = " + getVerticalScrollBar().getWidth());
		int size = getWidth()/2 - getVerticalScrollBar().getWidth();
		panel.add(new ComponentHolder(new Input(0), size));
		panel.add(new ComponentHolder(new Output(0), size));
		panel.add(new ComponentHolder(new AndGate(0), size));
		panel.add(new ComponentHolder(new NandGate(0), size));
		panel.add(new ComponentHolder(new OrGate(0), size));
		panel.add(new ComponentHolder(new NorGate(0), size));
		panel.add(new ComponentHolder(new XorGate(0), size));
		panel.add(new ComponentHolder(new XnorGate(0), size));
		panel.add(new ComponentHolder(new DFlipFlop(0), size));
		panel.add(new ComponentHolder(new TFlipFlop(0), size));
		panel.add(new ComponentHolder(new Rom8(0), size)); //adding extra to fill up space.
		panel.add(new ComponentHolder(new Inverter(0), size));
		panel.add(new ComponentHolder(new Inverter(0), size));
		panel.add(new ComponentHolder(new Inverter(0), size));
		panel.add(new ComponentHolder(new Inverter(0), size));
		panel.add(new ComponentHolder(new AndGate(0), size));
		panel.add(new ComponentHolder(new AndGate(0), size));
		panel.add(new ComponentHolder(new AndGate(0), size));
		panel.add(new ComponentHolder(new AndGate(0), size));
		
		addMouseListeners();
	}
	
	void addMouseListeners() {
		java.awt.Component[] components = panel.getComponents();
		for(int i = 0; i < components.length; i++) {
			//System.out.println("adding listener " + i);
			components[i].addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent event) {
					System.out.println("selecting a component");
					ComponentHolder holder = (ComponentHolder) event.getComponent();
					onComponentSelectedListener.onComponentSelected(((Component) holder.component).getType());
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
				}

				@Override
				public void mousePressed(MouseEvent event) {
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
				}
				
			});
		}
	}
	
	void removeAllMouseListeners(java.awt.Component component) {
		MouseListener[] listeners = component.getMouseListeners();
		for(int i = 0; i < listeners.length; i++) {
			component.removeMouseListener(listeners[i]);
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		panel.setPreferredSize(new Dimension(getWidth()/2, getWidth()*rows/2));
		panel.removeAll();
		addComponents();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		System.out.println("size is: " + panel.getWidth());
		panel.setPreferredSize(new Dimension(getWidth()/2, getWidth()*rows/2));
		
	}
	
	interface OnComponentSelectedListener {
		public void onComponentSelected(int componentType);
	}
}

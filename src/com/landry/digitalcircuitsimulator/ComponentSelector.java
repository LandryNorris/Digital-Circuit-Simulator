package com.landry.digitalcircuitsimulator;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

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

public class ComponentSelector extends JScrollPane implements ComponentListener {

	private static final long serialVersionUID = -1499163292165502902L;

	JPanel panel = new JPanel();
	int rows;
	
	ComponentSelector(int rows, int cols) {
		this.rows = rows;
		panel.setLayout(new GridLayout(rows, cols));
		addComponentListener(this);
		setViewportView(panel);
		addComponents();
	}

	void addComponents() {
		int size = getWidth()/2;
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
		panel.add(new ComponentHolder(new Inverter(0), size)); //adding extra to fill up space.
		panel.add(new ComponentHolder(new Inverter(0), size));
		panel.add(new ComponentHolder(new Inverter(0), size));
		panel.add(new ComponentHolder(new Inverter(0), size));
		panel.add(new ComponentHolder(new Inverter(0), size));
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
		
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		System.out.println("size is: " + panel.getWidth());
		panel.setPreferredSize(new Dimension(getWidth()/2, getWidth()*rows/2));
		
	}
}

package com.landry.digitalcircuitsimulator.roms;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.landry.digitalcircuitsimulator.Component;
import com.landry.digitalcircuitsimulator.Pin;
import com.landry.digitalcircuitsimulator.Simulator;
import com.landry.digitalcircuitsimulator.State;
import com.landry.digitalcircuitsimulator.Util;

public abstract class Rom extends Component {
	public byte[] data;
	
	protected class RomDataModel extends AbstractTableModel implements TableModelListener {
		private static final long serialVersionUID = -1789570436902173602L;
		int rows, cols;
		
		RomDataModel(int r, int c) {
			rows = r;
			cols = c;
			//addTableModelListener(this);
		}
		
		@Override
		public boolean isCellEditable(int row, int col) {
			return true;
		}
		
		@Override
		public int getColumnCount() {
			return cols;
		}

		@Override
		public int getRowCount() {
			return rows;
		}

		@Override
		public Object getValueAt(int row, int col) {
			return String.valueOf((int)data[row*cols + col]+128);
		}

		@Override
		public void tableChanged(TableModelEvent event) {
			System.out.println(data.length);
			int row = event.getFirstRow();
			int col = event.getColumn();
			
			data[row*cols+col] = getByte((String) getValueAt(row, col));
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) {
			data[row*cols+col] = getByte((String) value);
		}
		
	}
	
	void fillData(int value) throws IllegalArgumentException {
		for(int i = 0; i < data.length; i++) {
			write(i, value);
		}
	}
	
	byte getByte(String s) {
		int value = Integer.parseInt(s);
		return (byte) (value-128);
	}
	
	String s;
	
	int decodeAddress(Pin[] pins, int start, int size) {
		int result = 0;
		int multiplier = 1;
		for(int i = 0; i < size; i++) {
			byte pinState = pins[start+i].getState();
			int state = (pinState == State.HIGH) ? 1 : 0;
			result += multiplier*state;
			multiplier *= 2;
		}
		return result;
	}
	
	@Override
	protected void draw(Graphics2D g, int gridX, int gridY, int gridSize) {
		int xOffset = Util.modPos(gridX, gridSize);
		int gridOffsetX = (gridX - xOffset) / gridSize;

		int yOffset = Util.modPos(gridY, gridSize);
		int gridOffsetY = (gridY - yOffset) / gridSize;

		g.setColor(Color.BLACK);
		g.setStroke(Simulator.thickStroke);
		int textWidth = g.getFontMetrics().stringWidth(s);
		int textHeight = g.getFontMetrics().getHeight();
		g.drawRect((x+gridOffsetX)*gridSize + xOffset, (y+gridOffsetY)*gridSize + yOffset, w*gridSize, h*gridSize);
		g.drawString(s, (x + gridOffsetX + w / 2)*gridSize + xOffset - textWidth / 2, (y + gridOffsetY + h / 2)*gridSize + yOffset + textHeight / 2);
		
		setPinLocations();
		for(int i = 0; i < inputs.length; i++) {
			inputs[i].draw(g, gridX, gridY, gridSize);
		}
		

		for(int i = 0; i < outputs.length; i++) {
			outputs[i].draw(g, gridX, gridY, gridSize);
		}
	}
	
	void write(int address, int b) throws IllegalArgumentException {
		if(b > 255 || b < 0) throw new IllegalArgumentException("value must be between 0 and 256. " + b + " is out of range.");
		data[address] = (byte)(b-128);
	}
	
	int read(int address) {
		return data[address] + 128;
	}
	
	void openDataDialog() {
		int cols = 10;
		int rows = (int) Math.ceil(data.length/cols);
		
		JDialog dialog = new JDialog();
		TableModel model = new RomDataModel(rows, cols);
		JTable table = new JTable(model);
		dialog.add(table);
		
		dialog.pack();
		dialog.setVisible(true);
	}
	
	@Override
	public void rightClick() {
		openDataDialog();
	}
}

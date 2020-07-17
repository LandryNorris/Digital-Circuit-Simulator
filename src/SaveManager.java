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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class SaveManager {
	String fileType = ".dcs";

	String directory = Application.settings.workspaceDirectory;
	String fileName;
	File file;
	FileOutputStream fileOut;
	BufferedOutputStream writer;
	FileReader fileReader;
	BufferedReader reader;
	ByteArrayOutputStream data = new ByteArrayOutputStream();
	DataOutputStream stream = new DataOutputStream(data);

	SaveManager(String fileName) {
		String fileText = directory + fileName;
		if(!fileText.contains(fileType)) {
			fileText += fileType;
		}
		file = new File(fileText);

		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	SaveManager() {
	}
	
	void saveAs(JFrame frame, Simulator simulator) throws IOException {
		JFileChooser fc = new JFileChooser();
		File f = new File(directory);
		if(!f.exists()) {
			f.mkdir();
		}
		System.out.println(f.getPath());
		System.out.println(System.getProperty("user.dir"));
		fc.setCurrentDirectory(f);
		
		System.out.println(fc.getCurrentDirectory());
		int returnVal = fc.showSaveDialog(frame);
		System.out.println("opening save dialog");
		while(returnVal != JFileChooser.APPROVE_OPTION && returnVal != JFileChooser.CANCEL_OPTION && returnVal != JFileChooser.ERROR_OPTION);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("Selected file is " + fc.getSelectedFile());
			file = fc.getSelectedFile();
			simulator.name = file.getName();
			save(simulator);
			flush();
		}
		System.out.println("leaving Save As " + returnVal);
	}

	void flush() {
		try {
			if(!file.exists()) {
				System.out.println("creating new file");
				file.createNewFile();
			}
			fileOut = new FileOutputStream(file);
			writer = new BufferedOutputStream(fileOut);
			byte[] bytes = data.toByteArray();
			System.out.println("Flushing " + bytes.length + " bytes to file.");
			writer.write(bytes);
			writer.flush();
			data.reset();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	void close() throws IOException {
		if(fileOut != null)
			fileOut.close();
		if(fileReader != null)
			fileReader.close();
		if(writer != null)
			writer.close();
		if(reader != null)
			reader.close();
		if(stream != null)
			stream.close();
		if(data != null)
			data.close();
	}

	void save(Simulator simulator) throws IOException {
		stream.writeShort(simulator.wires.size());
		for(int i = 0; i < simulator.wires.size(); i++) {
			stream.write(saveWire(simulator.wires.get(i)));
		}

		stream.writeShort(simulator.components.size());
		for(int i = 0; i < simulator.components.size(); i++) {
			stream.write(saveComponent(simulator.components.get(i)));
		}
		stream.flush();
	}

	byte[] saveWire(Wire w) throws IOException {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(data);
		stream.writeShort(w.points.length);
		for(Coordinate c: w.points) {
			stream.writeInt(c.x);
			stream.writeInt(c.y);
		}
		return data.toByteArray();
	}

	byte[] saveComponent(Component c) throws IOException {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(data);
		stream.writeInt(c.getType());
		stream.writeInt(c.x);
		stream.writeInt(c.y);
		if(c instanceof Rom) {
			byte[] d = ((Rom) c).data;
			stream.writeInt(d.length);
			data.write(d);
		}
		byte[] result = data.toByteArray();
		return result;
	}

	Simulator loadSimulator() throws IOException {
		if(file == null) return null;
		FileInputStream fileInputStream = null;
	    byte[] bytes = new byte[(int) file.length()];
	    //convert file into array of bytes
	    fileInputStream = new FileInputStream(file);
	    fileInputStream.read(bytes);
	    fileInputStream.close();
	    print(bytes);
		//byte[] bytes = Files.readAllBytes(file.toPath());
		ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
		DataInputStream in = new DataInputStream(bytesIn);
		Simulator s = new Simulator();
		short numWires = in.readShort();
		
		for(int i = 0; i < numWires; i++) {
			Wire w = loadWire(in);
			s.wires.add(w);
		}

		short numComponents = in.readShort();
		for(int i = 0; i < numComponents; i++) {
			Component c = loadComponent(in, i);
			s.components.add(c);
		}

		for(int i = 0; i < s.wires.size(); i++) {
			for(int j = 0; j < s.wires.get(i).points.length; j++) {
				Coordinate point = s.wires.get(i).points[j];
				Pin pin = pinAt(s.components, point.x, point.y);
				if(pin != null) {
					if(pin.isInput) {
						s.wires.get(i).addOutput(pin);
					} else {
						s.wires.get(i).input = pin;
					}
				}
			}
		}
		
		String name = file.getName();
		name.replace(fileType, "");
		s.name = name;
		return s;
	}

	Pin pinAt(ArrayList<Component> components, int x, int y) {
		System.out.println("checking for pin at " + x + ", " + y);
		for(int i = 0; i < components.size(); i++) {
			Pin pin = components.get(i).pinAt(x, y);
			if(pin != null) {
				System.out.println("found pin");
				return pin;
			}
		}
		return null;
	}

	Component loadComponent(DataInputStream in, int componentNum) throws IOException {

		int type = in.readInt();
		
		Component c = Component.create(type, componentNum);
		int x = in.readInt();
		int y = in.readInt();
		c.x = x;
		c.y = y;
		c.setPinLocations();
		
		if(c instanceof Rom) {
			int size = in.readInt();
			byte[] data = ((Rom) c).data;
			for(int i = 0; i < size; i++) {
				data[i] = in.readByte();
			}
		}
		return c;
	}

	Wire loadWire(DataInputStream in) throws IOException {
		Wire w = new Wire();
		short size = in.readShort();
		w.points = new Coordinate[size];
		for(int i = 0; i < size; i++) {
			int x = in.readInt();
			int y = in.readInt();
			w.points[i] = new Coordinate(x, y);
		}
		return w;
	}

	File askForFileBlocking(JFrame frame) throws IOException {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(directory));
		int returnVal = fc.showOpenDialog(frame);
		while(returnVal != JFileChooser.APPROVE_OPTION && returnVal != JFileChooser.CANCEL_OPTION && returnVal != JFileChooser.ERROR_OPTION);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			return fc.getSelectedFile();
		}
		return null;
	}
	
	void print(byte[] b) {
		System.out.println(b.length);
		for(int i = 0; i < b.length; i++) {
			System.out.printf("%x", b[i]);
		}
		System.out.println();
	}
}

package com.landry.digitalcircuitsimulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class Toolbar extends JMenuBar {
	private static final long serialVersionUID = 3818960967862962532L;

	JMenuItem nw;
	JMenuItem open;
	JMenuItem openLast;
	JMenuItem recents;

	public JMenuItem save;

	public JMenuItem saveAs;

	public JMenuItem saveAll;

	public JMenuItem print;

	public JMenuItem changeWorkspace;

	public JMenuItem settings;

	public JMenuItem license;
	
	Toolbar() {
		JMenu file = new JMenu("File");
		
		nw = createItem("New");
		open = createItem("Open");
		openLast = createItem("Open Last");
		recents = createItem("Recents");
		save = createItem("Save");
		saveAs = createItem("Save As");
		saveAll = createItem("Save All");
		print = createItem("Print");
		changeWorkspace = createItem("Change Workspace");
		settings = createItem("Settings");
		file.add(nw);
		file.add(open);
		file.add(openLast);
		file.add(recents);
		file.add(save);
		file.add(saveAs);
		file.add(saveAll);
		file.add(print);
		file.add(changeWorkspace);
		file.add(settings);
		
		JMenu edit = new JMenu("Edit");
		JMenuItem delete = createItem("Delete");
		
		edit.add(delete);
		
		
		JMenu view = new JMenu("View");
		JMenu sim = new JMenu("Simulator");
		JMenu window = new JMenu("Window");
		
		JMenu help = new JMenu("Help");
		
		license = createItem("License");
		
		help.add(license);
		
		add(file);
		add(edit);
		add(view);
		add(sim);
		add(window);
		add(help);
	}

	JMenuItem createItem(String name) {
		return new JMenuItem(name);
	}
}

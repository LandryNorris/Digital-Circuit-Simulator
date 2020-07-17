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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main implements ComponentListener {

	int width = 1000;
	int height = 500;
	int toolbarHeight = height/25;

	String title = "Simulator";
	Toolbar toolbar;
	
	JFrame frame;
	JPanel panel;

	SaveManager saveManager;
	Simulator simulator;
	ComponentSelector selector = new ComponentSelector(8, 2);
	
	Dimension lastSize;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		Main main = new Main();
		
		main.start();
		main.run();
	}
	
	void openWorkspace() {
		saveManager = new SaveManager();
		try {
			saveManager.askForFileBlocking(frame);
			panel.remove(simulator);
			simulator = saveManager.loadSimulator();
			panel.add(simulator, BorderLayout.CENTER);
			attachSimulatorListeners(simulator);
			frame.pack();
		} catch(IOException e) {
			System.out.println("failed to load simulator.");
			e.printStackTrace();
		}
	}
	
	void start() {
		height = Application.settings.screenHeight;
		width = Application.settings.screenWidth;
		simulator = new Simulator();
		createFrame();
		attachSimulatorListeners(simulator);
	}
	
	void run() {
		int frameRate = 30;
		int updateFrequency = 120;
		int frameDelay = 1000 / frameRate;
		int updateDelay = 1000 / updateFrequency;
		long lastFrameTime = System.currentTimeMillis();
		long lastUpdateTime = System.currentTimeMillis();
		while(true) {
			long now = System.currentTimeMillis();
			if(now > lastFrameTime + frameDelay) {
				draw();
				lastFrameTime = now;
				if(simulator.requestFocus) simulator.requestFocusInWindow();
			}
			now = System.currentTimeMillis();
			if(System.currentTimeMillis() > lastUpdateTime) {
				simulator.update();
				simulator.checkEvents();
				lastUpdateTime = now;
			}
		}
	}
	
	void createFrame() {
		frame = new JFrame(title);
		System.out.println(toolbarHeight);
		simulator.setMaximumSize(new Dimension(10000, 10000));
		simulator.setMinimumSize(new Dimension(width, height-toolbarHeight));
		simulator.setPreferredSize(new Dimension(width, height-toolbarHeight));
		
		panel = new JPanel();
		toolbar = createToolbar();
		int selectorW = width/20;
		selector.setPreferredSize(new Dimension(selectorW*2, selectorW*5));

		toolbar.setPreferredSize(new Dimension(width, toolbarHeight));
		
	    panel.setLayout(new BorderLayout());
		//panel.add(toolbar, BorderLayout.NORTH);
		panel.add(simulator, BorderLayout.CENTER);
		panel.add(selector, BorderLayout.WEST);

	    frame.setJMenuBar(toolbar);
		frame.add(panel);
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent e) {
		    	onExit();
		    }
		});
		frame.pack();
		System.out.println(toolbar.getHeight());
		System.out.println(simulator.getHeight());
	}
	
	void attachSimulatorListeners(Simulator simulator) {
		frame.addKeyListener(simulator);
		frame.addComponentListener(this);
		simulator.addMouseListener(simulator);
		simulator.addMouseMotionListener(simulator);
		simulator.addKeyListener(simulator);
		System.out.println("listeners attached");
	}
	
	void removeSimulatorListeners(Simulator simulator) {
		frame.removeKeyListener(simulator);
		simulator.removeMouseListener(simulator);
		simulator.removeMouseMotionListener(simulator);
		simulator.addKeyListener(simulator);
	}
	
	void draw() {
		simulator.repaint();
		//System.out.println("drawing");
		toolbar.repaint();
	}

	void createNewSimulator() {
		removeSimulatorListeners(simulator);
		panel.remove(simulator);
		simulator = new Simulator();
		simulator.setPreferredSize(new Dimension(width, height-toolbarHeight));
		panel.add(simulator, BorderLayout.CENTER);
		attachSimulatorListeners(simulator);
		frame.pack();
	}
	
	void onExit() {
		System.out.println("closing program...");
		saveProperties();
        if(saveManager != null) {
        	try {
				saveManager.close();
			} catch(IOException e1) {
				e1.printStackTrace();
			}
        }
        frame.dispose();
    	System.out.println("program closed. Goodbye.");
        System.exit(0);
	}
	
	void saveProperties() {
		Application.settings.screenWidth = frame.getWidth();
		Application.settings.screenHeight = frame.getHeight();
		
		try {
			Application.settings.writeToFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Toolbar createToolbar() {
		Toolbar t = new Toolbar();
		
		t.nw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("creating new simulator");
				createNewSimulator();
			}
		});
		
		t.open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				openWorkspace();
			}
		});
		
		t.openLast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    System.out.println("Popup menu item ["
			            + event.getActionCommand() + "] was pressed.");
			}
		});
		
		t.recents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    System.out.println("Popup menu item ["
			            + event.getActionCommand() + "] was pressed.");
			}
		});
		
		t.save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    saveManager = new SaveManager(simulator.name);
			    try {
					saveManager.save(simulator);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		t.saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveManager = new SaveManager();
				try {
					saveManager.saveAs(frame, simulator);
					System.out.println("simulator name after save: " + simulator.name);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		t.saveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveManager = new SaveManager();
				try {
					saveManager.saveAs(frame, simulator);
					System.out.println("simulator name after save: " + simulator.name);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		t.print.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    simulator.print();
			}
		});
		
		t.changeWorkspace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			}
		});
		
		t.settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			    SettingsWindow sw = new SettingsWindow();
			    System.out.println("opening settings");
			}
		});
		
		t.license.addActionListener((event) -> {
			new LegalWindow();
		});
		return t;
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		panel.setPreferredSize(new Dimension(frame.getContentPane().getWidth(), frame.getContentPane().getHeight()));
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}


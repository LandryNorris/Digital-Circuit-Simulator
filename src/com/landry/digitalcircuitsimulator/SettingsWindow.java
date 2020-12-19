package com.landry.digitalcircuitsimulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class SettingsWindow extends JDialog {

	private static final long serialVersionUID = -4063832613440880079L;
	JPanel panel = new JPanel();
	String title = "Settings";
	
	int width, height;
	private JTextField workspaceText;
	private JTextField autosaveTime;
	private JCheckBox shouldAutosave;
	private JButton btnExit;
	private JButton btnClose;
	
	SettingsWindow() {
		setTitle(title);
		setModal(true);
		setLayout(new BorderLayout());
		layoutSettings();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		btnExit = new JButton("Exit and save");
		btnClose = new JButton("Close");
		
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Application.settings.workspaceDirectory = workspaceText.getText();
				Application.settings.shouldAutoSave = shouldAutosave.isSelected();
				Application.settings.autoSaveTime = Integer.parseInt(autosaveTime.getText());
				
				try {
					System.out.println(Application.settings.shouldAutoSave);
					Application.settings.writeToFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				dispose();
			}
		});
		
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonPanel.add(btnExit);
		buttonPanel.add(btnClose);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	void layoutSettings() {
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.LINE_AXIS));
		
		workspaceText = new JTextField(Application.settings.workspaceDirectory);
		workspaceText.setMaximumSize(workspaceText.getPreferredSize());
		shouldAutosave = new JCheckBox();
		shouldAutosave.setSelected(Application.settings.shouldAutoSave); 
		autosaveTime = new JTextField(String.valueOf(Application.settings.autoSaveTime));
		autosaveTime.setMaximumSize(autosaveTime.getPreferredSize());
		
		panel.add(createSettingItem("Workspace Directory:  ", workspaceText));
		panel.add(createSettingItem("Autosave enabled:        ", shouldAutosave));
		panel.add(createSettingItem("Autosave timing (ms):  ", autosaveTime));
		
		System.out.println("Finished laying out settings");
		
		setSize(1000, 500);
		JScrollPane scroll = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, BorderLayout.NORTH);
		panel.setBackground(Color.WHITE);
		setVisible(true);
	}
	
	JPanel createSettingItem(String labelText, JComponent component)  {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.LINE_AXIS));
		JLabel label = new JLabel(labelText);
		result.setBackground(Color.WHITE);
		result.add(label);
		result.add(component);
		result.setAlignmentX(0.0F);
		return result;
	}
}

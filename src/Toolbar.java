import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class Toolbar implements MouseListener {

	int width, height;
	int itemWidth;

	JPanel frame;

	String[] options = { "File", "Edit", "View", "Simulator", "Window", "Help" };

	JPopupMenu popup = new JPopupMenu();
	
	ActionListener newListener;
	ActionListener openFileListener;
	ActionListener openLastListener;
	ActionListener recentsListener;
	ActionListener closeListener;
	ActionListener saveListener;
	ActionListener saveAsListener;
	ActionListener printListener;
	ActionListener settingsListener;

	Toolbar(JPanel f) {
		frame = f;
	}

	void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont(18f));
		itemWidth = width / 12;
		for(int i = 0; i < options.length; i++) {
			g.drawString(options[i], itemWidth * i,
					height / 2 + g.getFontMetrics().getHeight() / 2 - g.getFontMetrics().getDescent());
		}
	}

	void onClick(int clickX, int clickY) {
		if(clickX <= options.length * itemWidth) {
			int index = clickX / itemWidth;
			System.out.println(index);
			openPopupMenu(index);
		}
	}

	void openPopupMenu(int index) {
		popup = createMenu(index);
		popup.show(frame, index * itemWidth, height);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		onClick(e.getX(), e.getY());

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	JPopupMenu createMenu(int option) {
		//"New", "Open File", "Open Last", "Sbmnu:Recents,test1,test2,test3,", "Close", "Save", "Save As",
		//"Print", "Settings"
		JPopupMenu popup = new JPopupMenu();
		switch(option) {
			case 0: { //File
				JMenuItem n = new JMenuItem("New");
				JMenuItem openFile = new JMenuItem("Open File");
				JMenuItem openLast = new JMenuItem("Open Last");
				JMenu recents = new JMenu("Recents");
				JMenuItem close = new JMenuItem("Close");
				JMenuItem save = new JMenuItem("Save");
				JMenuItem saveAs = new JMenuItem("Save As");
				JMenuItem print = new JMenuItem("Print");
				JMenuItem settings = new JMenuItem("Settings");
				
				n.addActionListener(newListener);
				openFile.addActionListener(openFileListener);
				openLast.addActionListener(openLastListener);
				recents.addActionListener(recentsListener);
				close.addActionListener(closeListener);
				save.addActionListener(saveListener);
				saveAs.addActionListener(saveAsListener);
				print.addActionListener(printListener);
				settings.addActionListener(settingsListener);
				
				popup.add(n);
				popup.add(openFile);
				popup.add(openLast);
				popup.add(recents);
				popup.add(close);
				popup.add(save);
				popup.add(saveAs);
				popup.add(print);
				popup.add(settings);
			}
		}
		return popup;
	}

}

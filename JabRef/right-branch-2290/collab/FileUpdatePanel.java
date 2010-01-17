package net.sf.jabref.collab;

import net.sf.jabref.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.IOException;
import java.io.File;

public class FileUpdatePanel extends SidePaneComponent implements ActionListener {

    public static final String NAME = "fileUpdate";

	JButton test = new JButton(Globals.lang("Review changes"));

	BasePanel panel;

	JabRefFrame frame;

	SidePaneManager manager;

	JLabel message;

	ChangeScanner scanner;

	public FileUpdatePanel(JabRefFrame frame, BasePanel panel, SidePaneManager manager, File file,
		ChangeScanner scanner) {
		super(manager, GUIGlobals.getIconUrl("save"), Globals.lang("File changed"));
		this.panel = panel;
		this.frame = frame;
		this.manager = manager;
		this.scanner = scanner;

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());

		message = new JLabel("<html><center>"
			+ Globals.lang("The file<BR>'%0'<BR>has been modified<BR>externally!", file.getName())
			+ "</center></html>", JLabel.CENTER);

		main.add(message, BorderLayout.CENTER);
		main.add(test, BorderLayout.SOUTH);
		main.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		add(main, BorderLayout.CENTER);
		test.addActionListener(this);
	}

	
	public void componentClosing() {
	    manager.unregisterComponent(NAME);
	}

	
	public void actionPerformed(ActionEvent e) {
		manager.hideComponent(this);
		
		
		
		scanner.displayResult();
		
		panel.setUpdatedExternally(false);
		
		
		
	}
}

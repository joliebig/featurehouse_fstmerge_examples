package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
import net.sourceforge.squirrel_sql.client.session.CancelStatementThread;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;



public class PleaseWaitDialog extends BaseInternalFrame implements ActionListener {
	private static final long serialVersionUID = 8870277695490954084L;

	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(PleaseWaitDialog.class);
	
	private JButton cancelButton;
	private IMessageHandler messageHandler;
	private Statement stmt;
	
	
	public PleaseWaitDialog(Statement stmt, IMessageHandler messageHandler) {
        
		super(stringMgr.getString("PleaseWaitDialog.queryExecuting"), true);
		this.messageHandler = messageHandler;
		this.stmt = stmt;

		GUIUtils.makeToolWindow(this, true);

		final JPanel content = new JPanel(new BorderLayout());
		content.add(createMainPanel(), BorderLayout.CENTER);
        setContentPane(content);
        pack();
	}
	
	private Component createMainPanel()
	{

		final FormLayout layout = new FormLayout(
			
			"center:pref",
			
			"pref, 6dlu, pref, 6dlu, pref, 6dlu, pref");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();

		int y = 1;
		builder.addSeparator(title, cc.xywh(1, y, 1, 1));

		y += 2;
		
		builder.addLabel(stringMgr.getString("PleaseWaitDialog.pleaseWait"), cc.xy(1, y));

		y += 2;
		builder.addSeparator("", cc.xywh(1, y, 1, 1));

		
		cancelButton = new JButton(stringMgr.getString("PleaseWaitDialog.cancel"));
		cancelButton.addActionListener(this);
		
		y += 2;
		builder.add(cancelButton, cc.xywh(1, y, 1, 1));

		return builder.getPanel();
	}
	
	public void actionPerformed(ActionEvent e) {
	      if (stmt != null) {
	         CancelStatementThread cst = new CancelStatementThread(stmt, messageHandler);
	         cst.tryCancel();
	      }
	}
	
	
	public void showDialog(IApplication app) {
        app.getMainFrame().addInternalFrame(this, true);
        this.moveToFront();
        this.setLayer(JLayeredPane.MODAL_LAYER);
        GUIUtils.centerWithinDesktop(this);
        this.setVisible(true);
	}
}

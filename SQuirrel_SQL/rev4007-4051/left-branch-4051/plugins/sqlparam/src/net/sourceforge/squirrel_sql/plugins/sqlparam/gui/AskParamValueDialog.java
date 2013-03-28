package net.sourceforge.squirrel_sql.plugins.sqlparam.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;



public class AskParamValueDialog extends BaseInternalFrame {
	private static final long serialVersionUID = 3470927611018381204L;


	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(AskParamValueDialog.class);
	
	private OkClosePanel btnsPnl = new OkClosePanel();
	private JTextField value = new JTextField();
	private JCheckBox quote = new JCheckBox();
	private String parameter = null;
	private String oldValue = null;
	
	private boolean done = false;
	private boolean cancelled = false;
	
	
	public AskParamValueDialog(String parameter, String oldValue) {
        
		super(stringMgr.getString("sqlparam.inputParameterValues"), true);
		this.parameter = parameter;
		this.oldValue = oldValue;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		GUIUtils.makeToolWindow(this, true);

		final JPanel content = new JPanel(new BorderLayout());
		content.add(createMainPanel(), BorderLayout.CENTER);
        setContentPane(content);
		btnsPnl.makeOKButtonDefault();
		btnsPnl.getRootPane().setDefaultButton(btnsPnl.getOKButton());
        pack();
	}
	
	
	public boolean isDone() {
		return done;
	}
	
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	
	public void setValue(String defaultValue) {
		value.setText(defaultValue);
	}
	
	
	public String getValue() {
		return value.getText();
	}
	
	
	public boolean isQuotingNeeded() {
		return quote.isSelected();
	}
	
	private void updateCheckbox() {
		boolean isNumber = false;
		
		try {
			Float.parseFloat(value.getText());
			isNumber = true;
		} catch (NumberFormatException nfe) { 
			isNumber = false;
		}
		
		if (isNumber) {
			quote.setSelected(false);
			quote.setEnabled(true);
		} else {
			quote.setSelected(true);
			quote.setEnabled(false);
		}
	}
	
	private Component createMainPanel()
	{
		value.setColumns(20);
		value.setText(oldValue);
		value.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) { AskParamValueDialog.this.updateCheckbox(); }
			public void insertUpdate(DocumentEvent e) { AskParamValueDialog.this.updateCheckbox(); }
			public void removeUpdate(DocumentEvent e) { AskParamValueDialog.this.updateCheckbox(); }
		});
		updateCheckbox();
		btnsPnl.addListener(new MyOkClosePanelListener());

		final FormLayout layout = new FormLayout(
			
			"right:pref, 8dlu, left:min(100dlu;pref):grow",
			
			"pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 6dlu, pref, 3dlu, pref, 3dlu, pref");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();

		int y = 1;
		builder.addSeparator(title, cc.xywh(1, y, 3, 1));

		y += 2;
		
		builder.addLabel(stringMgr.getString("sqlparam.valueFor", parameter), cc.xy(1, y));
		builder.add(value, cc.xywh(3, y, 1, 1));

		y += 2;
		
		builder.addLabel(stringMgr.getString("sqlparam.quoteValues"), cc.xy(1, y));
		builder.add(quote, cc.xywh(3, y, 1, 1));

		y += 2;
		builder.addSeparator("", cc.xywh(1, y, 3, 1));

		y += 2;
		builder.add(btnsPnl, cc.xywh(1, y, 3, 1));

		return builder.getPanel();
	}
	
	
	public void cancel() {
		done = true;
		cancelled = true;
		dispose();
	}

	
	public void ok() {
		done = true;
		dispose();
	}


	
	private final class MyOkClosePanelListener implements IOkClosePanelListener
	{
		
		public void okPressed(OkClosePanelEvent evt)
		{
			AskParamValueDialog.this.ok();
		}

		
		public void closePressed(OkClosePanelEvent evt)
		{
			AskParamValueDialog.this.cancel();
		}

		
		public void cancelPressed(OkClosePanelEvent evt)
		{
			AskParamValueDialog.this.cancel();
		}
	}
	
}

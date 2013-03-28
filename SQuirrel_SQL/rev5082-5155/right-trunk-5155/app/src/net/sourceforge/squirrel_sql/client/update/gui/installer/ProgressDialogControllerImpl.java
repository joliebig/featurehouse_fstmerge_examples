
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class ProgressDialogControllerImpl implements ProgressDialogController
{
	
	private JDialog currentDialog = null;

	
	private JLabel currentMessage = null;

	
	private JLabel detailMessage = null;
	
	
	private JProgressBar currentProgressBar = null;

	
	private static ILogger s_log = LoggerController.createLogger(ProgressDialogControllerImpl.class);
	
	
	public void hideProgressDialog()
	{
		s_log.info("Hiding dialog");
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				currentDialog.setVisible(false);
			}
		}, true);
	}

	
	public void incrementProgress()
	{
		s_log.info("incrementing progress");
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				int currentValue = currentProgressBar.getValue();
				currentProgressBar.setValue(currentValue + 1);
			}
		}, true);
	}

	
	public void setDetailMessage(final String msg)
	{
		s_log.info("Setting detail message: "+msg);
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				detailMessage.setText(msg);
			}
		}, true);
	}

	
	public void showProgressDialog(final String title, final String msg, final int total)
	{
		s_log.info("showing progress dialog");
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				currentDialog = new JDialog((Frame) null, title);
				currentMessage = new JLabel(msg);
				detailMessage = new JLabel("...");
				currentProgressBar = new JProgressBar(0, total - 1);
				
				JPanel panel = new JPanel(new BorderLayout());
				JPanel messagePanel = new JPanel(new GridLayout(2,1));
				messagePanel.add(currentMessage);
				messagePanel.add(detailMessage);
				panel.add(messagePanel, BorderLayout.CENTER);
				panel.add(currentProgressBar, BorderLayout.SOUTH);
				
				currentDialog.getContentPane().add(panel);
				currentDialog.setSize(300, 100);
				GUIUtils.centerWithinScreen(currentDialog);
				currentDialog.setVisible(true);
			}
		}, true);

	}

	public void resetProgressDialog(final String title, final String msg, final int total)
	{
		GUIUtils.processOnSwingEventThread(new Runnable() {
			public void run() {
				currentDialog.setTitle(title);
				currentMessage.setText(msg);
				currentProgressBar.setValue(0);
				currentProgressBar.setMinimum(0);
				currentProgressBar.setMaximum(total);
			}
		});
		
	}

}

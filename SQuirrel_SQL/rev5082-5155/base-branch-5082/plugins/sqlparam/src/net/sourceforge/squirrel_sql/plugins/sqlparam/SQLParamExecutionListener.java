package net.sourceforge.squirrel_sql.plugins.sqlparam;


import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SelectWidgetCommand;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlparam.gui.AskParamValueDialog;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SQLParamExecutionListener implements ISQLExecutionListener {

	private final static ILogger log = LoggerController.createLogger(SQLParamPlugin.class);
	private ISession session = null;
	private SQLParamPlugin plugin = null;
	private AskParamValueDialog dialog = null;

	
	public SQLParamExecutionListener(SQLParamPlugin plugin, ISession session) {
		this.session = session;
		this.plugin = plugin;
	}

	
	public void statementExecuted(String sql) {
		
	}

	
	public String statementExecuting(String sql) {
		
		StringBuffer buffer = new StringBuffer(sql);
		Map<String, String> cache = plugin.getCache();
		Map<String, String> currentCache = new HashMap<String, String>();
		Pattern p = Pattern.compile(":[a-zA-Z]\\w+");

		Matcher m = p.matcher(buffer);

		while (m.find()) {
			if (isQuoted(buffer, m.start()))
				continue;
			final String var = m.group();
			String value = null;
			if (currentCache.containsKey(var)) {
				value = currentCache.get(var);
			} else {
				final String oldValue = cache.get(var);
				if (SwingUtilities.isEventDispatchThread()) {
					createParameterDialog(var, oldValue);
					while (!dialog.isDone()) {
						try {
							AWTEvent event = Toolkit.getDefaultToolkit().getSystemEventQueue().getNextEvent();
							Object source = event.getSource();
							if (event instanceof ActiveEvent) {
								((ActiveEvent)event).dispatch();
							} else if (source instanceof Component) {
								((Component)source).dispatchEvent(
										event);
							} else if (source instanceof MenuComponent) {
								((MenuComponent)source).dispatchEvent(
										event);
							} else {
								System.err.println(
										"Unable to dispatch: " + event);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								createParameterDialog(var, oldValue);
							}
						});
						while (!dialog.isDone()) {
							wait();
						}
					} catch (InvocationTargetException ite) {
						ite.printStackTrace();
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
				if (dialog.isCancelled()) {
					dialog = null;
					return null;
				}
				value = sanitizeValue(dialog.getValue(), dialog.isQuotingNeeded());
				cache.put(var, dialog.getValue());
				currentCache.put(var, value);
				dialog = null;
			}
			buffer.replace(m.start(), m.end(), value);
			m.reset();
		}

		GUIUtils.processOnSwingEventThread(new Runnable() {
			public void run() {
				new SelectWidgetCommand(session.getActiveSessionWindow()).execute();
			}
		});
		
		return buffer.toString();
	}

	private void createParameterDialog(String parameter, String oldValue) {
		dialog = new AskParamValueDialog(parameter, oldValue, session.getApplication());
		session.getApplication().getMainFrame().addWidget(dialog);
		dialog.setLayer(JLayeredPane.MODAL_LAYER);
		dialog.moveToFront();
		DialogWidget.centerWithinDesktop(dialog);
		dialog.setVisible(true);
	}

	private String sanitizeValue(String value, boolean quoting) {
		String retValue = value;
		boolean quotesNeeded = quoting;

		try {
			Float.parseFloat(value);
		} catch (NumberFormatException nfe) {
			quotesNeeded = true;
		}

		if (quotesNeeded) {
			retValue = "'" + value + "'";
		}
		return retValue;
	}

	private boolean isQuoted(StringBuffer buffer, int position) {
		String part = buffer.substring(0, position);
		if (searchAllOccurences(part, "\"") % 2 != 0) 
			return true;
		if (searchAllOccurences(part, "'") % 2 != 0)
			return true;
		return false;
	}

	private int searchAllOccurences(String haystack, String needle) {
		int i = 0;
		int pos = 0;
		while ((pos = haystack.indexOf(needle, pos + 1)) > -1) {
			i++;
		}
		return i;
	}

}

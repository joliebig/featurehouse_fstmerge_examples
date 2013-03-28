package net.sourceforge.squirrel_sql.client.gui;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.text.DateFormat;

import javax.swing.*;
import javax.swing.Timer;

public class MemoryPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MemoryPanel.class);


	private JProgressBar _bar;
	private JButton _btnGarbage;
	private JButton _btnSessionGCStatus;
	transient private IApplication _app;
	private HashMap<IIdentifier, MemorySessionInfo> _sessionInfosBySessionIDs = 
        new HashMap<IIdentifier, MemorySessionInfo>();

	public MemoryPanel(IApplication app)
	{
		_app = app;

		_bar = new JProgressBar();

		_bar.setStringPainted(true);

		_btnGarbage = new JButton();
		
		_btnGarbage.setToolTipText(s_stringMgr.getString("MemoryPanel.runGC"));
		_btnGarbage.setBorder(null);

		ImageIcon trashIcon = _app.getResources().getIcon(SquirrelResources.IImageNames.TRASH);
		_btnGarbage.setIcon(trashIcon);

		Dimension prefButtonSize = new Dimension(trashIcon.getIconWidth(), trashIcon.getIconHeight());

		_btnGarbage.setPreferredSize(prefButtonSize);

		_btnGarbage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.gc();
			}
		});

		_btnSessionGCStatus = new JButton()
		{
            private static final long serialVersionUID = 1L;

            public void paint(Graphics g)
			{
				super.paint(g);

			}
		};

		_btnSessionGCStatus.setBorder(null);

		updateGcStatus();

		_btnSessionGCStatus.setBorder(null);
		_btnSessionGCStatus.setPreferredSize(prefButtonSize);


		_btnSessionGCStatus.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showSessionGCStatus();
			}
		});


		JPanel pnlButtons = new JPanel(new GridLayout(1,2,3,0));
		pnlButtons.add(_btnSessionGCStatus);
		pnlButtons.add(_btnGarbage);


		this.setLayout(new BorderLayout(5,0));
		this.add(pnlButtons, BorderLayout.EAST);
		this.add(_bar, BorderLayout.CENTER);

		this.setBorder(null);

		_app.getSessionManager().addSessionListener(new SessionAdapter()
		{
			public void sessionClosed(SessionEvent evt)
			{
				IIdentifier id = evt.getSession().getIdentifier();
				MemorySessionInfo msi = _sessionInfosBySessionIDs.get(id);
				if(null == msi)
				{
					throw new IllegalStateException("A session with ID " + id + " has not been created");
				}
				msi.closed = new Date();
				updateGcStatus();
			}

			public void sessionConnected(SessionEvent evt)
			{
				IIdentifier id = evt.getSession().getIdentifier();
				if(null != _sessionInfosBySessionIDs.get(id))
				{
					throw new IllegalStateException("A session with ID " + id + " has already been created");
				}
				MemorySessionInfo msi = new MemorySessionInfo(id, evt.getSession().getAlias().getName());
				_sessionInfosBySessionIDs.put(id, msi);

			}

			public void sessionFinalized(IIdentifier sessionId)
			{
				MemorySessionInfo msi = _sessionInfosBySessionIDs.get(sessionId);
				if(null == msi)
				{
					throw new IllegalStateException("A session with ID " + sessionId + " has not been created");
				}
				msi.finalized = new Date();
				updateGcStatus();
			}
		});

		Timer t = new Timer(500, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateLabel();
			}
		});
		t.start();
	}

	private void updateGcStatus()
	{
		SessionGCStatus gcStat = getSessionGCStatus();
		_btnSessionGCStatus.setToolTipText(gcStat.tooltip);
		_btnSessionGCStatus.setBackground(gcStat.color);
		_btnSessionGCStatus.setText(gcStat.numSessAwaitingGC);
	}



	private SessionGCStatus getSessionGCStatus()
	{
		SessionGCStatus ret = new SessionGCStatus();

		int numSessAwaitingGC = 0;
		for(Iterator<MemorySessionInfo> i = 
            _sessionInfosBySessionIDs.values().iterator(); i.hasNext();)
		{
			MemorySessionInfo msi =  i.next();
			if(null != msi.closed && null == msi.finalized)
			{
				++numSessAwaitingGC;
			}
		}

		ret.numSessAwaitingGC = "" + numSessAwaitingGC;

		
		ret.tooltip = s_stringMgr.getString("MemoryPanel.gcStatusToolTip", new Integer(ret.numSessAwaitingGC));

		ret.color = Color.yellow;
		if(numSessAwaitingGC < 2)
		{
			ret.color = Color.green;
		}
		else if(numSessAwaitingGC > 4)
		{
			ret.color = Color.red;
		}

		return ret;


	}

	private void updateLabel()
	{
		long total = Runtime.getRuntime().totalMemory() >> 10 >> 10;
		long free = Runtime.getRuntime().freeMemory() >> 10 >> 10;
		long just = total-free;

		_bar.setMinimum(0);
		_bar.setMaximum((int)total);
		_bar.setValue((int)just);

		Object[] params = new Long[]
			{
				Long.valueOf(just),
				Long.valueOf(total)
			};

		
		String msg = s_stringMgr.getString("MemoryPanel.memSize", params);
		_bar.setString(msg);
	}

	private void showSessionGCStatus()
	{
		StringBuffer[] params = new StringBuffer[]
			{
				new StringBuffer(getSessionGCStatus().tooltip),
				new StringBuffer(),
				new StringBuffer(),
				new StringBuffer()
			};


		MemorySessionInfo[] msis = 
            _sessionInfosBySessionIDs.values().toArray(new MemorySessionInfo[0]);

		Arrays.sort(msis);

		for (int i = 0; i < msis.length; i++)
		{
			if(null != msis[i].closed && null == msis[i].finalized)
			{
				params[1].append(msis[i].toString()).append('\n');
			}
			else if(null == msis[i].closed)
			{
				params[2].append(msis[i].toString()).append('\n');
			}
			else if(null != msis[i].finalized)
			{
				params[3].append(msis[i].toString()).append('\n');
			}
		}


		
		
		
		
		
		
		
		
		
		
		String msg = s_stringMgr.getString("MemoryPanel.gcStatus", (Object[])params);
		ErrorDialog errorDialog = new ErrorDialog(_app.getMainFrame(), msg);


		
		errorDialog.setTitle(s_stringMgr.getString("MemoryPanel.statusDialogTitle"));
		errorDialog.setVisible(true);
	}

	private static class MemorySessionInfo implements Comparable<MemorySessionInfo>
	{
		MemorySessionInfo(IIdentifier sessionId, String aliasName)
		{
			this.sessionId = sessionId;
			this.aliasName = aliasName;
		}

		IIdentifier sessionId;
		String aliasName;
		java.util.Date created = new Date();
		java.util.Date closed;
		java.util.Date finalized;

		public String toString()
		{
			DateFormat df = DateFormat.getInstance();

			Object[] params = new Object[]
				{
					sessionId,
					aliasName,
					df.format(created),
					null == closed ? "" : df.format(closed),
					null == finalized ? "" :df.format(finalized)
				};

			if(null != closed && null == finalized)
			{
				
				return s_stringMgr.getString("MemoryPanel.sessionInfo.toString1", params);
			}
			else if(null == closed)
			{
				
				return s_stringMgr.getString("MemoryPanel.sessionInfo.toString2", params);
			}
			else if(null != finalized)
			{
				
				return s_stringMgr.getString("MemoryPanel.sessionInfo.toString3", params);
			}
			else
			{
				throw new IllegalStateException("Unknown Session state");
			}
		}

		public int compareTo(MemorySessionInfo other)
		{
			return Integer.valueOf(sessionId.toString()).compareTo(Integer.valueOf(other.sessionId.toString()));
		}

        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((sessionId == null) ? 0 : sessionId.hashCode());
            return result;
        }

        
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final MemorySessionInfo other = (MemorySessionInfo) obj;
            if (sessionId == null) {
                if (other.sessionId != null)
                    return false;
            } else if (!sessionId.equals(other.sessionId))
                return false;
            return true;
        }
		
		
	}



	private static class SessionGCStatus
	{
		String tooltip;
		Color color;
		String numSessAwaitingGC;
	}

}
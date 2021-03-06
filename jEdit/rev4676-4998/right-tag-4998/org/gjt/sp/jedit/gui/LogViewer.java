

package org.gjt.sp.jedit.gui;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;


public class LogViewer extends JPanel implements DefaultFocusComponent
{
	
	public LogViewer()
	{
		super(new BorderLayout());

		JPanel caption = new JPanel();
		caption.setLayout(new BoxLayout(caption,BoxLayout.X_AXIS));
		caption.setBorder(new EmptyBorder(6,6,6,6));

		String settingsDirectory = jEdit.getSettingsDirectory();
		if(settingsDirectory != null)
		{
			String[] args = { MiscUtilities.constructPath(
				settingsDirectory, "activity.log") };
			JLabel label = new JLabel(jEdit.getProperty(
				"log-viewer.caption",args));
			caption.add(label);
		}

		caption.add(Box.createHorizontalGlue());

		tailIsOn = jEdit.getBooleanProperty("log-viewer.tail", false);
		tail = new JCheckBox(
			jEdit.getProperty("log-viewer.tail.label"),tailIsOn);
		tail.addActionListener(new ActionHandler());
		caption.add(tail);

		caption.add(Box.createHorizontalStrut(12));

		copy = new JButton(jEdit.getProperty("log-viewer.copy"));
		copy.addActionListener(new ActionHandler());
		caption.add(copy);

		ListModel model = Log.getLogListModel();
		model.addListDataListener(new ListHandler());
		list = new LogList(model);
		list.setVisibleRowCount(24);
		list.setFont(jEdit.getFontProperty("view.font"));

		add(BorderLayout.NORTH,caption);
		JScrollPane scroller = new JScrollPane(list);
		Dimension dim = scroller.getPreferredSize();
		dim.width = Math.min(600,dim.width);
		scroller.setPreferredSize(dim);
		add(BorderLayout.CENTER,scroller);
	} 

	
	public void addNotify()
	{
		super.addNotify();
		if(tailIsOn)
		{
			int index = list.getModel().getSize() - 1;
			list.ensureIndexIsVisible(index);
		}
	} 

	
	public void focusOnDefaultComponent()
	{
		list.requestFocus();
	} 

	
	private JList list;
	private JButton copy;
	private JCheckBox tail;
	private boolean tailIsOn;
	

	
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Object src = e.getSource();
			if(src == tail)
			{
				tailIsOn = !tailIsOn;
				jEdit.setBooleanProperty("log-viewer.tail",tailIsOn);
				if(tailIsOn)
				{
					int index = list.getModel().getSize();
					if(index != 0)
					{
						list.ensureIndexIsVisible(index - 1);
					}
				}
			}
			else if(src == copy)
			{
				Object[] selected = list.getSelectedValues();
				StringBuffer buf = new StringBuffer();
				for(int i = 0; i < selected.length; i++)
				{
					buf.append(selected[i]);
					buf.append('\n');
				}
				Registers.setRegister('$',buf.toString());
			}
		}
	} 

	
	class ListHandler implements ListDataListener
	{
		public void intervalAdded(ListDataEvent e)
		{
			contentsChanged(e);
		}

		public void intervalRemoved(ListDataEvent e)
		{
			contentsChanged(e);
		}

		public void contentsChanged(ListDataEvent e)
		{
			if(tailIsOn)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						int index = list.getModel().getSize() - 1;
						list.ensureIndexIsVisible(index);
					}
				});
			}
		}
	} 

	
	class LogList extends JList
	{
		LogList(ListModel model)
		{
			super(model);
		}

		public void processMouseMotionEvent(MouseEvent evt)
		{
			if(evt.getID() == MouseEvent.MOUSE_DRAGGED)
			{
				int row = list.locationToIndex(evt.getPoint());
				if(row != -1)
				{
					list.addSelectionInterval(row,row);
					evt.consume();
				}
			}
			else
				super.processMouseMotionEvent(evt);
		}
	} 
}

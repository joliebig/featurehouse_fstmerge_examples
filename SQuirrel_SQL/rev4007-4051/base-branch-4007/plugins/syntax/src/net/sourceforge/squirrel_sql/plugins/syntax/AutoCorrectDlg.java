package net.sourceforge.squirrel_sql.plugins.syntax;


import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class AutoCorrectDlg extends JDialog
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AutoCorrectDlg.class);


   JCheckBox chkEnable;
   JTable tblAutoCorrects;
   MultipleLineLabel lblNewLineNote;

   JButton btnApply;
   JButton btnAddRow;
   JButton btnRemoveRows;
   JButton btnClose;


   public AutoCorrectDlg(JFrame parent)
   {
		
		super(parent, s_stringMgr.getString("syntax.configAutoCorr"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;


		
		chkEnable = new JCheckBox(s_stringMgr.getString("syntax.enableAutoCorr"));
      gbc = new GridBagConstraints(0,0,3,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(chkEnable, gbc);

      tblAutoCorrects = new JTable();
      gbc = new GridBagConstraints(0,1,4,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      getContentPane().add(new JScrollPane(tblAutoCorrects), gbc);

      
      lblNewLineNote = new MultipleLineLabel(s_stringMgr.getString("syntax.abrevNewLineNote"));
      lblNewLineNote.setForeground(Color.red);
      gbc = new GridBagConstraints(0,2,4,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      getContentPane().add(lblNewLineNote, gbc);


      
      btnApply = new JButton(s_stringMgr.getString("syntax.autoCorrApply"));
      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(btnApply, gbc);

		
      btnAddRow = new JButton(s_stringMgr.getString("syntax.addRow"));
      gbc = new GridBagConstraints(1,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(btnAddRow, gbc);

		
      btnRemoveRows = new JButton(s_stringMgr.getString("syntax.removeRows"));
      gbc = new GridBagConstraints(2,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(btnRemoveRows, gbc);

      
      btnClose = new JButton(s_stringMgr.getString("syntax.abrevclose"));
      gbc = new GridBagConstraints(3,3,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(btnClose, gbc);


      getRootPane().setDefaultButton(btnApply);

      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            setVisible(false);
            dispose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);
   }

}

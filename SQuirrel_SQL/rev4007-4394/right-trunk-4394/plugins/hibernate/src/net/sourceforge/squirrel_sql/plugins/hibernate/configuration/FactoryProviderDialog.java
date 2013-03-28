package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class FactoryProviderDialog extends JDialog
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(FactoryProviderDialog.class);
   

   JTextField txtClassName;
   JButton btnWriteExampleFactorProvider;
   JButton btnOk;
   JButton btnCancel;

   public FactoryProviderDialog(JFrame mainFrame)
   {
      
      super(mainFrame, s_stringMgr.getString("FactoryProviderDialog.title"), true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      
      
      MultipleLineLabel lbl = new MultipleLineLabel(s_stringMgr.getString("FactoryProviderDialog.desc"));
      gbc = new GridBagConstraints(0,0,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      getContentPane().add(lbl, gbc);

      txtClassName = new JTextField();
      gbc = new GridBagConstraints(0,1,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(txtClassName, gbc);


      
      btnWriteExampleFactorProvider = new JButton(s_stringMgr.getString("FactoryProviderDialog.example"));
      gbc = new GridBagConstraints(0,2,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(btnWriteExampleFactorProvider, gbc);


      
      btnOk = new JButton(s_stringMgr.getString("FactoryProviderDialog.ok"));
      gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,5,5,5), 0,0);
      getContentPane().add(btnOk, gbc);

      
      btnCancel = new JButton(s_stringMgr.getString("FactoryProviderDialog.cancel"));
      gbc = new GridBagConstraints(1,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,5,5,5), 0,0);
      getContentPane().add(btnCancel, gbc);

      
      setSize(500, 250);


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

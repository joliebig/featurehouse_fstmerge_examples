package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import org.netbeans.editor.DialogSupport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.awt.*;
import java.util.Hashtable;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;


public class SquirrelNBDialogFactory extends WindowAdapter implements DialogSupport.DialogFactory, ActionListener
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SquirrelNBDialogFactory.class);

   private JButton cancelButton;
   private SyntaxPugin _plugin;
   private boolean _findHintProvided;


   public SquirrelNBDialogFactory(SyntaxPugin plugin)
   {

      _plugin = plugin;
   }

   
   JPanel createButtonPanel(JButton[] buttons, boolean sidebuttons)
   {
      int count = buttons.length;

      JPanel outerPanel = new JPanel(new BorderLayout());
      outerPanel.setBorder(new EmptyBorder(new Insets(sidebuttons ? 5 : 0, sidebuttons ? 0 : 5, 5, 5)));

      LayoutManager lm = new GridLayout(
         sidebuttons ? count : 1, sidebuttons ? 1 : count, 5, 5);

      JPanel innerPanel = new JPanel(lm);

      for (int i = 0; i < count; i++) innerPanel.add(buttons[i]);

      outerPanel.add(innerPanel,
         sidebuttons ? BorderLayout.NORTH : BorderLayout.EAST);
      return outerPanel;
   }

   public Dialog createDialog(String title, JPanel panel, boolean modal,
                              JButton[] buttons, boolean sidebuttons, int defaultIndex,
                              int cancelIndex, ActionListener listener)
   {

      if(false == _findHintProvided && "find".equalsIgnoreCase(title))
      {
         
         ISession[] activeSessions = new ISession[]{_plugin.getApplication().getSessionManager().getActiveSession()};


         for (int i = 0; i < activeSessions.length; i++)
         {
				
				activeSessions[i].showMessage(s_stringMgr.getString("syntax.findExplain"));
         }
         _findHintProvided = true;
      }


      
      JDialog dlg = new JDialog(_plugin.getApplication().getMainFrame(), title, modal);
      dlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      dlg.getContentPane().add(panel, BorderLayout.CENTER);

      
      JPanel buttonPanel = createButtonPanel(buttons, sidebuttons);
      String buttonAlign = sidebuttons ? BorderLayout.EAST : BorderLayout.SOUTH;
      dlg.getContentPane().add(buttonPanel, buttonAlign);

      
      if (listener != null)
      {
         for (int i = 0; i < buttons.length; i++)
         {
            ActionListener[] actionListeners = buttons[i].getActionListeners();

            boolean found = false;
            for (int j = 0; j < actionListeners.length; j++)
            {
               if (actionListeners[j].equals(listener))
               {
                  
                  
                  
                  
                  
                  
                  
                  
                  
                  found = true;
               }
            }

            if (false == found)
            {
               buttons[i].addActionListener(listener);
            }
         }
      }

      
      if (defaultIndex >= 0)
      {
         dlg.getRootPane().setDefaultButton(buttons[defaultIndex]);
      }

      
      if (cancelIndex >= 0)
      {
         cancelButton = buttons[cancelIndex];
         
         dlg.getRootPane().registerKeyboardAction(this,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

         
         dlg.addWindowListener(this);
      }

      dlg.pack();


      GUIUtils.centerWithinParent(dlg);
      return dlg;
   }

   public void actionPerformed(ActionEvent evt)
   {
      cancelButton.doClick(10);
   }

   public void windowClosing(WindowEvent evt)
   {
      cancelButton.doClick(10);
   }
}


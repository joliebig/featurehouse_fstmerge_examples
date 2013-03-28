package net.sourceforge.squirrel_sql.client.update.gui;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import net.sourceforge.squirrel_sql.client.update.UpdateController;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class UpdateSummaryDialog extends JDialog {
   private static final long serialVersionUID = 1L;

   
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(UpdateSummaryDialog.class);

   private UpdateSummaryTable _updateSummaryTable;

   private JLabel installedVersionLabel = null;
   
   private JLabel availableVersionLabel = null;
   
   static interface i18n {
      
      String APPLY_LABEL = s_stringMgr.getString("UpdateSummaryDialog.applyLabel");

      
      String TITLE = s_stringMgr.getString("UpdateSummaryDialog.title");
      
      
      String AVAILABLE_VERSION_PREFIX = 
         s_stringMgr.getString("UpdateSummaryDialog.currentVersionPrefix");
      
      
      String INSTALLED_VERSION_PREFIX = 
         s_stringMgr.getString("UpdateSummaryDialog.installedVersionPrefix");
      
      
      String CLOSE_LABEL = 
         s_stringMgr.getString("UpdateSummaryDialog.close");
   }

   public UpdateSummaryDialog(Frame owner, List<ArtifactStatus> artifactStatus,
         UpdateController updateController) 
   {
      super(owner, i18n.TITLE);
      createGUI(artifactStatus, updateController);
   }

   public void setInstalledVersion(String installedVersion) {
      StringBuilder tmp = new StringBuilder(i18n.INSTALLED_VERSION_PREFIX);
      tmp.append(" ");
      tmp.append(installedVersion);
      installedVersionLabel.setText(tmp.toString());
   }
   
   public void setAvailableVersion(String availableVersion) {
      StringBuilder tmp = new StringBuilder(i18n.AVAILABLE_VERSION_PREFIX);
      tmp.append(" ");
      tmp.append(availableVersion);
      availableVersionLabel.setText(tmp.toString());      
   }
   
   private void createGUI(final List<ArtifactStatus> artifactStatus, 
                          final UpdateController updateController) {
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      final Container contentPane = getContentPane();
      contentPane.setLayout(new BorderLayout());

      installedVersionLabel = new JLabel();
      installedVersionLabel.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
      
      availableVersionLabel = new JLabel();
      availableVersionLabel.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
      
      JPanel labelPanel = new JPanel();
      labelPanel.add(installedVersionLabel);
      labelPanel.add(availableVersionLabel);
      
      contentPane.add(labelPanel, BorderLayout.NORTH);

      _updateSummaryTable = new UpdateSummaryTable(artifactStatus, updateController);
      contentPane.add(new JScrollPane(_updateSummaryTable), BorderLayout.CENTER);

      final JPanel btnsPnl = new JPanel();
      final JButton okBtn = new JButton(i18n.APPLY_LABEL);
      okBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            List<ArtifactStatus> changes = 
               _updateSummaryTable.getUserRequestedChanges();
            updateController.applyChanges(changes);
         }
      });
      btnsPnl.add(okBtn);

      final JButton closeBtn = new JButton(i18n.CLOSE_LABEL);
      closeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            dispose();
         }
      });
      btnsPnl.add(closeBtn);
      contentPane.add(btnsPnl, BorderLayout.SOUTH);

      AbstractAction closeAction = new AbstractAction() {
         private static final long serialVersionUID = 1L;

         public void actionPerformed(ActionEvent actionEvent) {
            setVisible(false);
            dispose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                   .put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                   .put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke,
                                                             "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);

      pack();
      setSize(655, 500);
      GUIUtils.centerWithinParent(this);
      setResizable(true);

   }
}

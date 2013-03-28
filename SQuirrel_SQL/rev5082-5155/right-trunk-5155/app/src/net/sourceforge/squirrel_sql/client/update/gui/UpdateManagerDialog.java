package net.sourceforge.squirrel_sql.client.update.gui;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class UpdateManagerDialog extends JDialog {
   private static final long serialVersionUID = 1L;

   
   private static final StringManager s_stringMgr = 
      StringManagerFactory.getStringManager(UpdateManagerDialog.class);

   

   
   private JButton _checkBtn = null;

   
   private JButton _configBtn = null;
   
   
   private JButton _closeBtn = null;

   
   private JTextField _updateServerNameTF = null;
   
   
   private JTextField _updateServerPortTF = null;
   
   
   private JTextField _updateServerPathTF = null;
   
   
   private JTextField _updateServerChannelTF = null;
   
   
   private JTextField _localUpdatePath = null;
      
   
   private ArrayList<CheckUpdateListener> _checkUpdateListeners = 
      new ArrayList<CheckUpdateListener>();
   
   private boolean isRemoteUpdateSite = true;
   
   static interface i18n {
      
      String CHANNEL_LABEL = s_stringMgr.getString("UpdateManagerDialog.channelLabel");
      
      
      String CHECK_LABEL = s_stringMgr.getString("UpdateManagerDialog.checkButtonLabel");

      
      String CLOSE_LABEL = s_stringMgr.getString("UpdateManagerDialog.closeLabel");

      
      String HOST_LABEL = s_stringMgr.getString("UpdateManagerDialog.hostLabel");

      String LOCAL_UPDATE_PATH_LABEL = "Local Update Path:";
      
      
      String LOCATION_TAB_LABEL = s_stringMgr.getString("UpdateManagerDialog.repositoryTabLabel");

      
      String PATH_LABEL = s_stringMgr.getString("UpdateManagerDialog.pathLabel");

      
      String PORT_LABEL = s_stringMgr.getString("UpdateManagerDialog.portLabel");

      
      String SETTINGS_LABEL = s_stringMgr.getString("UpdateManagerDialog.settingsLabel");

      
      String TITLE = s_stringMgr.getString("UpdateManagerDialog.title");
   }

   
   public UpdateManagerDialog(JFrame parent, boolean isRemoteUpdateSite) {
      super(parent, i18n.TITLE, true);
      setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
      this.isRemoteUpdateSite = isRemoteUpdateSite;
      init();
   }

   
   public void setUpdateServerName(String nameStr) {
      _updateServerNameTF.setText(nameStr);
   }
   
   
   public void setUpdateServerPort(String portStr) {
      _updateServerPortTF.setText(portStr);
   }
   
   
   public void setUpdateServerPath(String pathStr) {
      _updateServerPathTF.setText(pathStr);
   }
   
   
   public void setUpdateServerChannel(String channelStr) {
      _updateServerChannelTF.setText(channelStr);
   }
   
   public void setLocalUpdatePath(String path) {
      _localUpdatePath.setText(path);
   }
   
   
   
   public void addCheckUpdateListener(CheckUpdateListener listener) {
      this._checkUpdateListeners.add(listener);
   }
   
   
   private void init() {
      this.setLayout(new BorderLayout());
      JPanel locationPanel = null;
      if (isRemoteUpdateSite) {
         locationPanel = getRemoteLocationPanel();
      } else {
         locationPanel = getLocalUpdateSitePanel();
      }

      this.add(locationPanel, BorderLayout.CENTER);
      this.add(getButtonPanel(), BorderLayout.SOUTH);
      this.setSize(300, 200);
      GUIUtils.centerWithinParent(this);
   }

   private JPanel getRemoteLocationPanel() {
      EmptyBorder border = new EmptyBorder(new Insets(5, 5, 5, 5));
      Dimension mediumField = new Dimension(200, 20);
      Dimension portField = new Dimension(50, 20);

      JPanel locationPanel = new JPanel();
      locationPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
      locationPanel.setLayout(new GridBagLayout());

      int x = 0;
      int y = -1;

      GridBagConstraints c = new GridBagConstraints();
      c.gridx = x;
      c.gridy = y;
      c.anchor = GridBagConstraints.NORTH;

      JLabel hostLabel = getBorderedLabel(i18n.HOST_LABEL, border);
      _updateServerNameTF = getSizedTextField(mediumField);

      locationPanel.add(hostLabel, getLabelConstraints(c));
      locationPanel.add(_updateServerNameTF, getFieldFillHorizontalConstaints(c));

      JLabel portLabel = getBorderedLabel(i18n.PORT_LABEL, border);
      _updateServerPortTF = getSizedTextField(portField);

      locationPanel.add(portLabel, getLabelConstraints(c));
      locationPanel.add(_updateServerPortTF, getFieldConstraints(c));

      JLabel pathLabel = getBorderedLabel(i18n.PATH_LABEL, border);
      _updateServerPathTF = getSizedTextField(mediumField);

      locationPanel.add(pathLabel, getLabelConstraints(c));
      locationPanel.add(_updateServerPathTF, getFieldFillHorizontalConstaints(c));

      JLabel channelLabel = getBorderedLabel(i18n.CHANNEL_LABEL, border);
      _updateServerChannelTF = getSizedTextField(mediumField);
      
      locationPanel.add(channelLabel, getLabelConstraints(c));
      locationPanel.add(_updateServerChannelTF, getFieldFillHorizontalConstaints(c));
      locationPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
      
      return locationPanel;
   }
   
   private JPanel getLocalUpdateSitePanel() {
      EmptyBorder border = new EmptyBorder(new Insets(5, 5, 5, 5));
      Dimension mediumField = new Dimension(200, 20);

      JPanel locationPanel = new JPanel();
      locationPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
      locationPanel.setLayout(new GridBagLayout());

      int x = 0;
      int y = -1;

      GridBagConstraints c = new GridBagConstraints();
      c.gridx = x;
      c.gridy = y;
      c.anchor = GridBagConstraints.NORTH;

      JLabel localUpdatePathLabel = getBorderedLabel(i18n.LOCAL_UPDATE_PATH_LABEL, border);
      _localUpdatePath = getSizedTextField(mediumField);

      locationPanel.add(localUpdatePathLabel, getLabelConstraints(c));
      locationPanel.add(_localUpdatePath, getFieldFillHorizontalConstaints(c));
      
      return locationPanel;
   }
   
   private JPanel getButtonPanel() {
      JPanel result = new JPanel();

      _checkBtn = new JButton(i18n.CHECK_LABEL);
      _checkBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            UpdateManagerDialog.this.setVisible(false);
            for (CheckUpdateListener listener: _checkUpdateListeners) {
               try {
                  listener.checkUpToDate();
               } catch (Exception ex) {
                  listener.showErrorMessage(
                     "Unexpected Exception",
                     "Update check failed with Exception: " + ex.getMessage(),
                     ex);
               }
            }            
         }
      });

      _configBtn = new JButton(i18n.SETTINGS_LABEL);
      _configBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            UpdateManagerDialog.this.setVisible(false);
            for (CheckUpdateListener listener: _checkUpdateListeners) {
               listener.showPreferences();
            }
         }
      });
      
      _closeBtn = new JButton(i18n.CLOSE_LABEL);
      _closeBtn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            UpdateManagerDialog.this.setVisible(false);
         }
      });

      result.add(_checkBtn);
      result.add(_configBtn);
      result.add(_closeBtn);
      return result;
   }

   private GridBagConstraints getLabelConstraints(GridBagConstraints c) {
      c.gridx = 0;
      c.gridy++;
      c.anchor = GridBagConstraints.EAST;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0;
      c.weighty = 0;
      return c;
   }

   private GridBagConstraints getFieldFillHorizontalConstaints(
         GridBagConstraints c) {
      c.gridx++;
      c.anchor = GridBagConstraints.WEST;
      c.weightx = 1;
      c.weighty = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.insets = new Insets(0, 0, 0, 5);
      return c;
   }

   private GridBagConstraints getFieldConstraints(GridBagConstraints c) {
      c.gridx++;
      c.anchor = GridBagConstraints.WEST;
      c.weightx = 1;
      c.weighty = 1;
      c.fill = GridBagConstraints.NONE;
      return c;
   }

   private JLabel getBorderedLabel(String text, Border border) {
      JLabel result = new JLabel(text);
      result.setBorder(border);
      result.setPreferredSize(new Dimension(115, 20));
      result.setHorizontalAlignment(SwingConstants.RIGHT);
      return result;
   }

   private JTextField getSizedTextField(Dimension preferredSize) {
      JTextField result = new JTextField();
      result.setPreferredSize(preferredSize);
      result.setMinimumSize(preferredSize);
      result.setEditable(false);
      return result;
   }

}

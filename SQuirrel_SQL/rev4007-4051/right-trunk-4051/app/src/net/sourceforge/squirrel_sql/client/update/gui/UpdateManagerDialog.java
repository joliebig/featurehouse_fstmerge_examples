package net.sourceforge.squirrel_sql.client.update.gui;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.sourceforge.squirrel_sql.client.update.UpdateController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class UpdateManagerDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    
    private final static ILogger s_log = LoggerController
            .createLogger(UpdateManagerDialog.class);

    
    private static final StringManager s_stringMgr = StringManagerFactory
            .getStringManager(UpdateManagerDialog.class);

    
    private UpdateController _controller = null; 
    
    
    
    
    
    private JButton _checkBtn = null;
    
    
    private JButton _closeBtn = null;

    
    private JFrame _parent = null;
        
    
    private JComboBox channelSelector = null; 
    
    static interface i18n {
        
        String TITLE = s_stringMgr.getString("UpdateManagerDialog.title");
        
        
        String CLOSE_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.closeLabel");
        
        
        String LOCATION_TAB_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.repositoryTabLabel");

        
        String CHANNEL_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.channelLabel");        
        
        
        String HOST_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.hostLabel");

        
        String PATH_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.pathLabel");
        
        
        String PORT_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.portLabel");

        
        String CHECK_LABEL = 
            s_stringMgr.getString("UpdateManagerDialog.checkButtonLabel");
    }
    
    
    public static String STABLE_CHANNEL_VALUE = "stable"; 

    
    public static String SNAPSHOT_CHANNEL_VALUE = "snapshot"; 
    
    public UpdateManagerDialog(JFrame parent, UpdateController controller) {
        super(parent, i18n.TITLE, true);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        this._parent = parent; 
        init();
    }

    public void setHost(String host) {
        
    }
    
    private void init() {
        EmptyBorder border = new EmptyBorder(new Insets(5,5,5,5));
        Dimension mediumField = new Dimension(200, 20);
        Dimension portField = new Dimension(50, 20);

        
        this.setLayout(new BorderLayout());
                
        
        JPanel locationPanel = new JPanel();
        locationPanel.setBorder(new EmptyBorder(0,0,0,10));
        locationPanel.setLayout(new GridBagLayout());
        
        int x = 0;
        int y = -1;
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.anchor = GridBagConstraints.NORTH;
        
        
        JLabel hostLabel = getBorderedLabel(i18n.HOST_LABEL, border);
        JTextField hostTF = getSizedTextField(mediumField);

        locationPanel.add(hostLabel, getLabelConstraints(c));
        locationPanel.add(hostTF, getFieldFillHorizontalConstaints(c));
        
        JLabel portLabel = getBorderedLabel(i18n.PORT_LABEL, border);
        JTextField portTF = getSizedTextField(portField);

        locationPanel.add(portLabel, getLabelConstraints(c));
        locationPanel.add(portTF, getFieldConstraints(c));        

        JLabel pathLabel = getBorderedLabel(i18n.PATH_LABEL, border);
        JTextField pathTF = getSizedTextField(mediumField);

        locationPanel.add(pathLabel, getLabelConstraints(c));
        locationPanel.add(pathTF, getFieldFillHorizontalConstaints(c));                
        
        JLabel channelLabel = getBorderedLabel(i18n.CHANNEL_LABEL, border);
        channelSelector = 
            new JComboBox(new Object[] {STABLE_CHANNEL_VALUE, 
                                        SNAPSHOT_CHANNEL_VALUE});
        
        locationPanel.add(channelLabel, getLabelConstraints(c));
        locationPanel.add(channelSelector, getFieldConstraints(c));
        locationPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
                
        this.add(locationPanel, BorderLayout.CENTER);
        this.add(getButtonPanel(), BorderLayout.SOUTH);
        this.setSize(300,200);
    }
    
    private JPanel getButtonPanel() {
        JPanel result = new JPanel();

        _checkBtn = new JButton(i18n.CHECK_LABEL);
        _closeBtn = new JButton(i18n.CLOSE_LABEL);
        
        _closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UpdateManagerDialog.this.setVisible(false);
            }
        });
        
        result.add(_checkBtn);
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
    
    private GridBagConstraints getFieldFillHorizontalConstaints(GridBagConstraints c) {
        c.gridx++;
        c.anchor = GridBagConstraints.WEST;   
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,0,5);
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
        return result;
    }
    
}

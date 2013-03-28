package net.sourceforge.squirrel_sql.client.gui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class ProgessCallBackDialog extends JDialog 
                                   implements ProgressCallBack {

    
    private final static ILogger s_log =
        LoggerController.createLogger(ProgessCallBackDialog.class);
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ProgessCallBackDialog.class);
    
    static interface i18n {
        
        String DEFAULT_LOADING_PREFIX = 
            s_stringMgr.getString("ProgressCallBackDialog.defaultLoadingPrefix");
        
        
        String INITIAL_LOADING_PREFIX = 
            s_stringMgr.getString("ProgressCallBackDialog.initialLoadingPrefix");
    }
                
    private int itemCount = 0;
    
    private JProgressBar progressBar = null;
    
    private JLabel statusLabel = null;
    
    private String _loadingPrefix = i18n.DEFAULT_LOADING_PREFIX;
    
    public ProgessCallBackDialog(Dialog owner, String title, int totalItems) {
        super(owner, title);
        init(totalItems);
    }

    public ProgessCallBackDialog(Frame owner, String title, int totalItems) {
        super(owner, title);
        setLocationRelativeTo(owner);
        init(totalItems);
    }

    private void init(int totalItems) {
        itemCount = totalItems;
        final Window owner = super.getOwner(); 
        final ProgessCallBackDialog dialog = this;
        
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                createGUI();
                setLocationRelativeTo(owner);
                dialog.setVisible(true);
            }
        }, true);
        
    }
    
    public void setTotalItems(int totalItems) {
        itemCount = totalItems;
        progressBar.setMaximum(totalItems);
    }
    
    
    public void setLoadingPrefix(String loadingPrefix) {
        if (loadingPrefix != null) {
            _loadingPrefix = loadingPrefix;
        }
    }
    
    
    public void currentlyLoading(final String simpleName) {
        final StringBuilder statusText = new StringBuilder();
        statusText.append(_loadingPrefix);
        statusText.append(" ");
        statusText.append(simpleName);
        try {
            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    statusLabel.setText(statusText.toString());
                    progressBar.setValue(progressBar.getValue() + 1);
                    if (finishedLoading()) {
                        ProgessCallBackDialog.this.setVisible(false);
                        return;
                    }                    
                }
            });
        } catch (Exception e) {
            s_log.error("Unexpected exception: "+e.getMessage(), e);
        }
    }

    public boolean finishedLoading() {
        return progressBar.getValue() >= itemCount - 1;
    }
        
    private void createGUI() {
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        
        statusLabel = new JLabel(i18n.INITIAL_LOADING_PREFIX);
        dialogPanel.add(statusLabel, c);
        
        progressBar = new JProgressBar(0, itemCount);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,10,0);
        c.weightx = 1.0;

        dialogPanel.add(progressBar, c);
        super.getContentPane().add(dialogPanel);
        super.pack();
        super.setSize(new Dimension(400, 100));  
    }
    
    
    public static void main(String[] args) throws Exception {
        ApplicationArguments.initialize(new String[] {});
        String[] tables = new String[] { 
            "table_a",
            "table_b",
            "table_c",
            "table_d",
            "table_e",
        };
        JFrame parent = new JFrame();
        GUIUtils.centerWithinScreen(parent);
        parent.setSize(new Dimension(200, 200));
        parent.setVisible(true);
        ProgessCallBackDialog dialog = 
            new ProgessCallBackDialog(parent, "test", 5);  
        
        dialog.setVisible(true);
        for (int i = 0; i < 5; i++) {
            dialog.currentlyLoading(tables[i]);
            Thread.sleep(1000);
        }
        System.exit(0);
    }

}

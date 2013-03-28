
package net.sourceforge.squirrel_sql.plugins.oracle.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePluginPreferencesPanel;
import net.sourceforge.squirrel_sql.plugins.oracle.prefs.OraclePreferenceBean;

public class TestOraclePluginPreferencesPanel {

    
    public static void main(String[] args) throws Exception {
        JFrame f = new JFrame();
        
        f.getContentPane().setLayout(new BorderLayout());
        ApplicationArguments.initialize(new String[0]);
        final PluginQueryTokenizerPreferencesManager prefsManager = 
            new PluginQueryTokenizerPreferencesManager();
        prefsManager.initialize(new DummyPlugin(), new OraclePreferenceBean());
        final PluginQueryTokenizerPreferencesPanel p = 
            new OraclePluginPreferencesPanel(prefsManager);
        JScrollPane sp = new JScrollPane(p);
        f.getContentPane().add(sp, BorderLayout.CENTER);
        JButton button = new JButton("Save");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                p.applyChanges();
                prefsManager.unload();
            }
        });
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(button);
        buttonPanel.add(exitButton);
        f.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        f.setBounds(200, 50,700, 700);
        f.setVisible(true);
        
    }

}

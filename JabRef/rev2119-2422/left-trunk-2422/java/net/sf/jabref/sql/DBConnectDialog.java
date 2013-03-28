

package net.sf.jabref.sql;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Color;

import javax.swing.*;
import javax.swing.SwingConstants.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

import net.sf.jabref.*;



public class DBConnectDialog extends JDialog {

    
    JLabel lblServerType     = new JLabel();
    JLabel lblServerHostname = new JLabel();
    JLabel lblDatabase       = new JLabel();
    JLabel lblUsername       = new JLabel();
    JLabel lblPassword       = new JLabel();

    
    JComboBox cmbServerType = new JComboBox();
    JTextField txtServerHostname = new JTextField(40);
    JTextField txtDatabase = new JTextField(40);
    JTextField txtUsername = new JTextField(40);        
    JPasswordField pwdPassword = new JPasswordField(40);
    JButton btnConnect = new JButton();
    JButton btnCancel = new JButton();

    
    ArrayList<JLabel> lhs = new ArrayList<JLabel>();
    ArrayList<JComponent> rhs = new ArrayList<JComponent>();

    DBStrings dbStrings = new DBStrings();


    
    public DBConnectDialog( JFrame parent, DBStrings dbs) {

        super(parent, Globals.lang("Connect to SQL Database"), true);

        this.setResizable(false);
        this.setLocationRelativeTo(parent);

        dbStrings = dbs;

        
        lhs.add(lblServerType);
        lhs.add(lblServerHostname);
        lhs.add(lblDatabase);
        lhs.add(lblUsername);
        lhs.add(lblPassword);

        rhs.add(cmbServerType);
        rhs.add(txtServerHostname);
        rhs.add(txtDatabase);
        rhs.add(txtUsername);
        rhs.add(pwdPassword);

        
        lblServerType.setText(Globals.lang("Server Type :"));
        lblServerHostname.setText(Globals.lang("Server Hostname :"));
        lblDatabase.setText(Globals.lang("Database :"));
        lblUsername.setText(Globals.lang("Username :"));
        lblPassword.setText(Globals.lang("Password :"));

        
        for (Iterator it = lhs.iterator(); it.hasNext(); ) {
            JLabel lbl = (JLabel) it.next();
            lbl.setHorizontalAlignment(JLabel.RIGHT);
        }

        
        
        btnConnect.setText(Globals.lang("Connect"));
        btnCancel.setText(Globals.lang("Cancel"));

        
        String[] srv = dbStrings.getServerTypes();
        for (int i=0; i<srv.length; i++) {
           cmbServerType.addItem(srv[i]);
        }

        txtServerHostname.setText(dbStrings.getServerHostname());
        txtDatabase.setText(dbStrings.getDatabase());
        txtUsername.setText(dbStrings.getUsername());
        pwdPassword.setText(dbStrings.getPassword());


        
        DefaultFormBuilder builder = new DefaultFormBuilder(new
                                 FormLayout("right:pref, 4dlu, fill:pref", ""));

        builder.getPanel().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));


        
        builder.append(lblServerType);
        builder.append(cmbServerType);
        builder.nextLine();
        builder.append(lblServerHostname);
        builder.append(txtServerHostname);
        builder.nextLine();
        builder.append(lblDatabase);
        builder.append(txtDatabase);
        builder.nextLine();
        builder.append(lblUsername);
        builder.append(txtUsername);
        builder.nextLine();
        builder.append(lblPassword);
        builder.append(pwdPassword);
        builder.nextLine();

        
        getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

        
        ButtonBarBuilder bb = new ButtonBarBuilder();
        bb.addGlue();
        bb.addGridded(btnConnect);
        bb.addGridded(btnCancel);
        bb.addGlue();

        
        getContentPane().add(bb.getPanel(), BorderLayout.SOUTH);
        pack();

        btnConnect.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                storeSettings();
                setVisible(false);
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

    }

    private void storeSettings () {
        dbStrings.setServerType(cmbServerType.getSelectedItem().toString());
        dbStrings.setServerHostname(txtServerHostname.getText());
        dbStrings.setDatabase(txtDatabase.getText());
        dbStrings.setUsername(txtUsername.getText());

        char[] pwd = pwdPassword.getPassword();
        String tmp = "";
        for (int i=0; i<pwd.length; i++) {
            tmp = tmp + pwd[i];
        }
        dbStrings.setPassword(tmp);
        tmp = "";
        Arrays.fill(pwd, '0');

    }

    public DBStrings getDBStrings() {
        return dbStrings;
    }

    public void setDBStrings(DBStrings dbStrings) { 
        this.dbStrings = dbStrings;
    }

}
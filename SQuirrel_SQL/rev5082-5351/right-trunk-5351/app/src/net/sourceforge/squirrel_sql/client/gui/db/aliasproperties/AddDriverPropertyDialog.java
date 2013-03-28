
package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;


public class AddDriverPropertyDialog extends JDialog
{
	private static final long serialVersionUID = 4889632277323001185L;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddDriverPropertyDialog.class);

	private static interface i18n
	{
		
		String addButtonLabel = s_stringMgr.getString("AddDriverPropertyDialog.addButtonLabel");

		
		String invalidNameMessage = s_stringMgr.getString("AddDriverPropertyDialog.invalidNameMessage");

		
		String invalidNameTitle = s_stringMgr.getString("AddDriverPropertyDialog.invalidNameTitle");

		
		String propertyDescriptionLabel =
			s_stringMgr.getString("AddDriverPropertyDialog.propertyDescriptionLabel");

		
		String propertyNameLabel = s_stringMgr.getString("AddDriverPropertyDialog.propertyNameLabel");

		
		String propertyValueLabel = s_stringMgr.getString("AddDriverPropertyDialog.propertyValueLabel");

	}

	JLabel propertyNameLbl = new JLabel(i18n.propertyNameLabel);

	JTextField propertyNameTF = new JTextField();

	JLabel propertyValueLbl = new JLabel(i18n.propertyValueLabel);

	JTextField propertyValueTF = new JTextField();

	JLabel propertyDescriptionLbl = new JLabel(i18n.propertyDescriptionLabel);

	JTextField propertyDescriptionTF = new JTextField();

	JButton addButton = new JButton(i18n.addButtonLabel);

	DriverPropertiesTable driverPropertiesTable = null;

	public AddDriverPropertyDialog(DriverPropertiesTable table)
	{
		this.driverPropertiesTable = table;
		JPanel panel = new JPanel(new GridLayout(3, 2));
		JPanel buttonPanel = new JPanel(new FlowLayout());
		panel.add(propertyNameLbl);
		panel.add(propertyNameTF);
		panel.add(propertyValueLbl);
		panel.add(propertyValueTF);
		panel.add(propertyDescriptionLbl);
		panel.add(propertyDescriptionTF);

		addButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String name = propertyNameTF.getText();
				if (!StringUtilities.isEmpty(name))
				{
					String value = propertyValueTF.getText();
					String desc = propertyDescriptionTF.getText();
					driverPropertiesTable.addProperty(name, value, desc);
					AddDriverPropertyDialog.this.setVisible(false);
				}
				else
				{
					JOptionPane.showMessageDialog(AddDriverPropertyDialog.this, i18n.invalidNameMessage,
						i18n.invalidNameTitle, JOptionPane.ERROR_MESSAGE);
				}
				
			}

		});
		buttonPanel.add(addButton);

		super.getContentPane().setLayout(new BorderLayout());
		super.getContentPane().add(panel, BorderLayout.CENTER);
		super.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		setSize(295, 120);
	}

}
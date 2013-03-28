package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

public class CodeCompletionPreferencesPanel extends JPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CodeCompletionPreferencesPanel.class);


	JRadioButton optSPWithParams;
	JRadioButton optSPWithoutParams;
	JRadioButton optUDFWithParams;
	JRadioButton optUDFWithoutParams;

	JTable tblPrefixes;

	JButton btnNewRow;
	JButton btnDeleteRows;
   JTextField txtMaxLastSelectedCompletionNamesPanel;


   public CodeCompletionPreferencesPanel()
	{
		setLayout(new GridBagLayout());

		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0 );
		
		
		
		
		add(new MultipleLineLabel(s_stringMgr.getString("codeCompletion.prefsExplain")), gbc);

		gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0 );
		
		add(new JLabel(s_stringMgr.getString("codeCompletion.globalFunctCompltion")),gbc);

		ButtonGroup grp = new ButtonGroup();

		
		optSPWithParams = new JRadioButton(s_stringMgr.getString("codeCompletion.spWithParams"));
		gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add(optSPWithParams,gbc);
		grp.add(optSPWithParams);

		
		optSPWithoutParams = new JRadioButton(s_stringMgr.getString("codeCompletion.spWithoutParams"));
		gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add(optSPWithoutParams,gbc);
		grp.add(optSPWithoutParams);

		
		optUDFWithParams = new JRadioButton(s_stringMgr.getString("codeCompletion.UDFWithParams"));
		gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add(optUDFWithParams,gbc);
		grp.add(optUDFWithParams);

		
		optUDFWithoutParams = new JRadioButton(s_stringMgr.getString("codeCompletion.UDFWithoutParams"));
		gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add(optUDFWithoutParams,gbc);
		grp.add(optUDFWithoutParams);


		gbc = new GridBagConstraints(0,6,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5),0,0 );
		
		add(new JLabel(s_stringMgr.getString("codeCompletion.prefixConfig")), gbc);


		tblPrefixes = new JTable();
		gbc = new GridBagConstraints(0,7,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0 );
		add(new JScrollPane(tblPrefixes), gbc);


		gbc = new GridBagConstraints(0,8,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0 );
		add( createButtonsPanel(), gbc);


      gbc = new GridBagConstraints(0,9,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(15,5,5,5),0,0 );
      add(createMaxLastSelectedCompletionNamesPanel(),gbc);
   }


   private JPanel createMaxLastSelectedCompletionNamesPanel()
   {
      JPanel ret = new JPanel();

      ret.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      
      
      MultipleLineLabel lbl = new MultipleLineLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.maxLastSelectedCompletionNames"));
      
      gbc = new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
      ret.add(lbl, gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0);
      NumberFormat format = NumberFormat.getIntegerInstance();
      txtMaxLastSelectedCompletionNamesPanel = new JFormattedTextField(format);
      txtMaxLastSelectedCompletionNamesPanel.setPreferredSize(new Dimension(30, txtMaxLastSelectedCompletionNamesPanel.getPreferredSize().height));

      ret.add(txtMaxLastSelectedCompletionNamesPanel, gbc);

      
      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0);
      ret.add(new JLabel(s_stringMgr.getString("CodeCompletionPreferencesPanel.numberOfTables")), gbc);

      ret.setBorder(BorderFactory.createEtchedBorder());

      return ret;
   }


   private JPanel createButtonsPanel()
   {
      GridBagConstraints gbc;
      JPanel pnlButtons = new JPanel(new GridBagLayout());

      
      btnNewRow = new JButton(s_stringMgr.getString("codeCompletion.prefixConfig.newRow"));
      gbc = new GridBagConstraints(0,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,5,5),0,0);
      pnlButtons.add(btnNewRow, gbc);

      
      btnDeleteRows = new JButton(s_stringMgr.getString("codeCompletion.prefixConfig.deleteSelRows"));
      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5),0,0);
      pnlButtons.add(btnDeleteRows, gbc);
      return pnlButtons;
   }

}

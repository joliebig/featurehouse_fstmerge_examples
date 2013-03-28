package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public abstract class FloatingPointBase extends BaseDataTypeComponent
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FloatingPointBase.class);

	
	private static boolean propertiesAlreadyLoaded = false;


	
	
	protected static boolean useJavaDefaultFormat = false;

	
	public static OkJPanel getControlPanel() {

	  

		
		
		loadProperties();

	  return new FloatingPointOkJPanel();
	}


	public FloatingPointBase()
	{
		loadProperties();
	}


	private static void loadProperties() {

		
		
		
		if (propertiesAlreadyLoaded == false) {
			
			useJavaDefaultFormat = false;	
			String useJavaDefaultFormatString = DTProperties.get(DataTypeBigDecimal.class.getName(), "useJavaDefaultFormat");

			if (useJavaDefaultFormatString != null && useJavaDefaultFormatString.equals("true"))
			{
				useJavaDefaultFormat =true;
			}
		}
	}


	
	private static class FloatingPointOkJPanel extends OkJPanel
	{
        private static final long serialVersionUID = 3745853322636427759L;

        JRadioButton optUseDefaultFormat;
		JRadioButton optUseLocaleDependendFormat;

		public FloatingPointOkJPanel()
		{
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setMaximumFractionDigits(5);

			optUseDefaultFormat =
				new JRadioButton(s_stringMgr.getString("floatingPointBase.useDefaultFormat", new Double(3.14159).toString()));
			optUseLocaleDependendFormat =
				new JRadioButton(s_stringMgr.getString("floatingPointBase.uselocaleDependendFormat", numberFormat.format(new Double(3.14159))));



			setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("floatingPointBase.typeBigDecimal")));

			setLayout(new GridBagLayout());

			GridBagConstraints gbc;
			gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4),0,0);
			add(optUseLocaleDependendFormat, gbc);
			gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4),0,0);
			add(optUseDefaultFormat, gbc);



			ButtonGroup bg = new ButtonGroup();
			bg.add(optUseDefaultFormat);
			bg.add(optUseLocaleDependendFormat);

			optUseLocaleDependendFormat.setSelected(!useJavaDefaultFormat);
			optUseDefaultFormat.setSelected(useJavaDefaultFormat);
		}

		
		public void ok()
		{
			
			useJavaDefaultFormat = optUseDefaultFormat.isSelected();
			DTProperties.put(DataTypeBigDecimal.class.getName(), 
			                 "useJavaDefaultFormat", 
			                 Boolean.valueOf(useJavaDefaultFormat).toString());
		}
	} 



}

package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class SchemaPropertiesPanel extends JPanel
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SchemaPropertiesPanel.class);


   JRadioButton radLoadAllAndCacheNone;
   JRadioButton radLoadAndCacheAll;
   JRadioButton radSpecifySchemas;

   JButton btnUpdateSchemas;

   JTable tblSchemas;

   JComboBox cboSchemaTableUpdateWhat;
   JComboBox cboSchemaTableUpdateTo;
   JButton btnSchemaTableUpdateApply;


   JCheckBox chkCacheSchemaIndepndentMetaData;

   JButton btnPrintCacheFileLocation;
   JButton btnDeleteCache;


   public SchemaPropertiesPanel()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      
      
      
      
      
      
      MultipleLineLabel lblHint = new MultipleLineLabel(s_stringMgr.getString("SchemaPropertiesPanel.hint"));
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      add(lblHint, gbc);

      
      radLoadAllAndCacheNone = new JRadioButton(s_stringMgr.getString("SchemaPropertiesPanel.loadAllAndCacheNone"));
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      add(radLoadAllAndCacheNone, gbc);

      
      radLoadAndCacheAll= new JRadioButton(s_stringMgr.getString("SchemaPropertiesPanel.loadAndCacheAll"));
      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(radLoadAndCacheAll, gbc);

      
      radSpecifySchemas= new JRadioButton(s_stringMgr.getString("SchemaPropertiesPanel.specifySchemas"));
      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(radSpecifySchemas, gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radLoadAllAndCacheNone);
      bg.add(radLoadAndCacheAll);
      bg.add(radSpecifySchemas);


      
      btnUpdateSchemas = new JButton(s_stringMgr.getString("SchemaPropertiesPanel.refreshSchemas"));
      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      add(btnUpdateSchemas, gbc);


      
      JLabel lblSchemaTableTitle = new JLabel(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableTitle"));
      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      add(lblSchemaTableTitle, gbc);

      tblSchemas = new JTable();
      gbc = new GridBagConstraints(0,6,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5), 0,0);
      add(new JScrollPane(tblSchemas), gbc);

      gbc = new GridBagConstraints(0,7,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(createSchemaTableUpdatePanel(), gbc);


      
      chkCacheSchemaIndepndentMetaData = new JCheckBox(s_stringMgr.getString("SchemaPropertiesPanel.CacheSchemaIndependentMetaData"));
      gbc = new GridBagConstraints(0,8,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5), 0,0);
      add(chkCacheSchemaIndepndentMetaData, gbc);


      gbc = new GridBagConstraints(0,9,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5), 0,0);
      add(createCacheFilePanel(), gbc);

   }

   private JPanel createCacheFilePanel()
   {
      JPanel ret = new JPanel();

      ret.setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      
      btnPrintCacheFileLocation = new JButton(s_stringMgr.getString("SchemaPropertiesPanel.printCacheFileLocation"));
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,5), 0,0);
      ret.add(btnPrintCacheFileLocation, gbc);

      
      btnDeleteCache = new JButton(s_stringMgr.getString("SchemaPropertiesPanel.deleteCache"));
      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      ret.add(btnDeleteCache, gbc);

      return ret;
   }


   private JPanel createSchemaTableUpdatePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      
      ret.add(new JLabel(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableUpdateLable1")), gbc);

      cboSchemaTableUpdateWhat = new JComboBox();
      gbc = new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      ret.add(cboSchemaTableUpdateWhat, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      
      ret.add(new JLabel(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableUpdateLable2")), gbc);

      cboSchemaTableUpdateTo = new JComboBox();
      gbc = new GridBagConstraints(3,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      ret.add(cboSchemaTableUpdateTo, gbc);

      
      btnSchemaTableUpdateApply = new JButton(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableUpdateApply"));
      gbc = new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      ret.add(btnSchemaTableUpdateApply, gbc);

      return ret;
   }


}

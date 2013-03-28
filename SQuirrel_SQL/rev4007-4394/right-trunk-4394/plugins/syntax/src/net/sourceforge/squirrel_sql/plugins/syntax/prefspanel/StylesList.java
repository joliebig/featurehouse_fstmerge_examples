
package net.sourceforge.squirrel_sql.plugins.syntax.prefspanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class StylesList extends JList
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(StylesList.class);


	
	public interface IStylesListIndices
	{
		int COLUMNS = 0;
		int COMMENTS = 1;
		int DATA_TYPES = 2;
		int ERRORS = 3;
		int FUNCTIONS = 4;
		int IDENTIFIERS = 5;
		int LITERALS = 6;
		int OPERATORS = 7;
		int RESERVED_WORDS = 8;
		int SEPARATORS = 9;
		int TABLES = 10;
		int WHITE_SPACE = 11;

		int LIST_SIZE = 12;
	}


	
	private final static String[] s_styleTitles = new String[]
	{
		
		s_stringMgr.getString("syntax.cols"),
		
		s_stringMgr.getString("syntax.comments"),
		
		s_stringMgr.getString("syntax.dataTypes"),
		
		s_stringMgr.getString("syntax.errors"),
		
		s_stringMgr.getString("syntax.functions"),
		
		s_stringMgr.getString("syntax.identifiers"),
		
		s_stringMgr.getString("syntax.literals"),
		
		s_stringMgr.getString("syntax.operators"),
		
		s_stringMgr.getString("syntax.resWords"),
		
		s_stringMgr.getString("syntax.separators"),
		
		s_stringMgr.getString("syntax.tables"),
		
		s_stringMgr.getString("syntax.whiteSpace"),
	};

	private SyntaxStyle[] _styles = new SyntaxStyle[IStylesListIndices.LIST_SIZE];

	public StylesList()
	{
		super(new DefaultListModel());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellRenderer(new ListRenderer());
		setBorder(BorderFactory.createLineBorder(Color.gray));
	}

	public void loadData(SyntaxPreferences prefs)
	{
		removeAll();

		_styles[IStylesListIndices.COLUMNS] = new SyntaxStyle(prefs.getColumnStyle());
		_styles[IStylesListIndices.COMMENTS] = new SyntaxStyle(prefs.getCommentStyle());
		_styles[IStylesListIndices.DATA_TYPES] = new SyntaxStyle(prefs.getDataTypeStyle());
		_styles[IStylesListIndices.ERRORS] = new SyntaxStyle(prefs.getErrorStyle());
		_styles[IStylesListIndices.FUNCTIONS] = new SyntaxStyle(prefs.getFunctionStyle());
		_styles[IStylesListIndices.IDENTIFIERS] = new SyntaxStyle(prefs.getIdentifierStyle());
		_styles[IStylesListIndices.LITERALS] = new SyntaxStyle(prefs.getLiteralStyle());
		_styles[IStylesListIndices.OPERATORS] = new SyntaxStyle(prefs.getOperatorStyle());
		_styles[IStylesListIndices.RESERVED_WORDS] = new SyntaxStyle(prefs.getReservedWordStyle());
		_styles[IStylesListIndices.SEPARATORS] = new SyntaxStyle(prefs.getSeparatorStyle());
		_styles[IStylesListIndices.TABLES] = new SyntaxStyle(prefs.getTableStyle());
		_styles[IStylesListIndices.WHITE_SPACE] = new SyntaxStyle(prefs.getWhiteSpaceStyle());

		final DefaultListModel model = (DefaultListModel)getModel();
		for (int i = 0; i < _styles.length; ++i)
		{
			model.addElement(_styles[i]);
		}

		setSelectedIndex(0);
	}

	public SyntaxStyle getSelectedSyntaxStyle()
	{
		return (SyntaxStyle)getSelectedValue();
	}

	public SyntaxStyle getSyntaxStyleAt(int idx)
	{
		return (SyntaxStyle)getModel().getElementAt(idx);
	}

	
	private static final class ListRenderer extends JLabel
											implements ListCellRenderer
	{
		ListRenderer()
		{
			super();
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list,
																	 Object value, int idx, boolean isSelected,
																	 boolean cellHasFocus)
		{
			final SyntaxStyle style = (SyntaxStyle)value;
			setForeground(new Color(style.getTextRGB()));
			setBackground(new Color(style.getBackgroundRGB()));


			setText(s_styleTitles[idx]);

			if (isSelected)
			{
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			}
			else
			{
				setBorder(BorderFactory.createEmptyBorder());
			}

			return this;
		}
	}
}


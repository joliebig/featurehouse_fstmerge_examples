package net.sourceforge.squirrel_sql.plugins.syntax.prefspanel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPluginResources;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class StyleMaintenancePanel extends JToolBar
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(StyleMaintenancePanel.class);

	private final StylesList _list;
	private final JToggleButton _boldChk;
	private final JToggleButton _italicChk;
	private final JButton _fontColorBtn;
	private final JButton _backgroundColorBtn;
	private FontColorButtonListener _fontColorBtnLis;
	private BackgroundColorButtonListener _backgroundColorBtnLis;
	private ActionListener _toggleLis;

	private SyntaxStyle _style;

	public StyleMaintenancePanel(StylesList list, SyntaxPluginResources rsrc)
	{
		super();
		_list = list;

		this.setFloatable(false);

		_boldChk = new JToggleButton(rsrc.getIcon(SyntaxPluginResources.IKeys.BOLD_IMAGE));
		
		_boldChk.setToolTipText(s_stringMgr.getString("syntax.bold"));
		_italicChk = new JToggleButton(rsrc.getIcon(SyntaxPluginResources.IKeys.ITALIC_IMAGE));
		
		_italicChk.setToolTipText(s_stringMgr.getString("syntax.italic"));

		_fontColorBtn = new JButton(rsrc.getIcon(SyntaxPluginResources.IKeys.FOREGROUND_IMAGE));
		
		_fontColorBtn.setToolTipText(s_stringMgr.getString("syntax.font"));
		_backgroundColorBtn = new JButton(rsrc.getIcon(SyntaxPluginResources.IKeys.BACKGROUND_IMAGE));
		
		_backgroundColorBtn.setToolTipText(s_stringMgr.getString("syntax.background"));

		add(_boldChk);
		add(_italicChk);
		add(_fontColorBtn);
		add(_backgroundColorBtn);
	}

	
	public void addNotify()
	{
		super.addNotify();

		if (_fontColorBtnLis == null)
		{
			_fontColorBtnLis = new FontColorButtonListener(_list);
			_fontColorBtn.addActionListener(_fontColorBtnLis);
			_backgroundColorBtnLis = new BackgroundColorButtonListener(_list);
			_backgroundColorBtn.addActionListener(_backgroundColorBtnLis);
		}

		if (_toggleLis == null)
		{
			_toggleLis = new ToggleButtonListener();
			_boldChk.addActionListener(_toggleLis);
			_italicChk.addActionListener(_toggleLis);
		}
	}

	
	public void removeNotify()
	{
		if (_fontColorBtnLis != null)
		{
			_fontColorBtn.removeActionListener(_fontColorBtnLis);
			_backgroundColorBtn.removeActionListener(_backgroundColorBtnLis);
			_fontColorBtnLis = null;
			_backgroundColorBtnLis = null;
		}
		if (_toggleLis != null)
		{
			_boldChk.removeActionListener(_toggleLis);
			_italicChk.removeActionListener(_toggleLis);
			_toggleLis = null;
		}

		super.removeNotify();
	}

	public void setEnabled(boolean enable)
	{
		_boldChk.setEnabled(enable);
		_italicChk.setEnabled(enable);
		_fontColorBtn.setEnabled(enable);
		_backgroundColorBtn.setEnabled(enable);
	}

	public void setStyle(SyntaxStyle style)
	{
		_boldChk.setSelected(style.isBold());
		_italicChk.setSelected(style.isItalic());
		_style = style;
	}

	private final class ToggleButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			_style.setBold(_boldChk.isSelected());
			_style.setItalic(_italicChk.isSelected());
			_list.repaint();
		}
	}

	
	private static class FontColorButtonListener implements ActionListener
	{
		private final StylesList _list;

		FontColorButtonListener(StylesList list)
		{
			super();
			_list = list;
		}

		public void actionPerformed(ActionEvent evt)
		{
			final SyntaxStyle style = _list.getSelectedSyntaxStyle();
			final int origRGB = style.getTextRGB();
			final Color color = JColorChooser.showDialog(null,
				
												s_stringMgr.getString("syntax.selColor"), new Color(origRGB));
			if (color != null)
			{
				style.setTextRGB(color.getRGB());
			}
		}
	}

	
	private static class BackgroundColorButtonListener implements ActionListener
	{
		private final StylesList _list;

		BackgroundColorButtonListener(StylesList list)
		{
			super();
			_list = list;
		}

		public void actionPerformed(ActionEvent evt)
		{
			final SyntaxStyle style = _list.getSelectedSyntaxStyle();
			final int origRGB = style.getBackgroundRGB();
			final Color color = JColorChooser.showDialog(null,
				
												s_stringMgr.getString("syntax.selColor2"), new Color(origRGB));
			if (color != null)
			{
				style.setBackgroundRGB(color.getRGB());
			}

		}
	}
}

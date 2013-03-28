package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;



public class LineNumber extends JComponent
{
	private final static Color DEFAULT_BACKGROUND = new Color(204, 204, 255);
	private final static Color DEFAULT_FOREGROUND = Color.black;
	private final static Font  DEFAULT_FONT = new Font("monospaced", Font.PLAIN, 12);

	
	private final static int HEIGHT = Integer.MAX_VALUE - 1000000;

	
	private final static int MARGIN = 5;

	
	private FontMetrics fontMetrics;
	private int lineHeight;
	private int currentDigits;

	
	private JComponent component;
	private int componentFontHeight;
	private int componentFontAscent;

	
	public LineNumber(JComponent component)
	{
		if (component == null)
		{
			setFont( DEFAULT_FONT );
			this.component = this;
		}
		else
		{
			setFont( component.getFont() );
			this.component = component;
		}

		setBackground( DEFAULT_BACKGROUND );
		setForeground( DEFAULT_FOREGROUND );
		setPreferredWidth( 99 );
	}

	
	public void setPreferredWidth(int lines)
	{
		int digits = String.valueOf(lines).length();

		

		if (digits != currentDigits && digits > 1)
		{
			currentDigits = digits;
			int width = fontMetrics.charWidth( '0' ) * digits;
			Dimension d = getPreferredSize();
			d.setSize(2 * MARGIN + width, HEIGHT);
			setPreferredSize( d );
			setSize( d );
		}
	}

	
	public void setFont(Font font)
	{
		super.setFont(font);
		fontMetrics = getFontMetrics( getFont() );
		componentFontHeight = fontMetrics.getHeight();
		componentFontAscent = fontMetrics.getAscent();
	}

	
	public int getLineHeight()
	{
		if (lineHeight == 0)
			return componentFontHeight;
		else
			return lineHeight;
	}

	
	public void setLineHeight(int lineHeight)
	{
		if (lineHeight > 0)
			this.lineHeight = lineHeight;
	}

	public int getStartOffset()
	{
		return component.getInsets().top + componentFontAscent;
	}

	public void paintComponent(Graphics g)
	{
		int lineHeight = getLineHeight();
		int startOffset = getStartOffset();
		Rectangle drawHere = g.getClipBounds();

		

		g.setColor( getBackground() );
		g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

		

		g.setColor( getForeground() );
		int startLineNumber = (drawHere.y / lineHeight) + 1;
		int endLineNumber = startLineNumber + (drawHere.height / lineHeight);

		int start = (drawHere.y / lineHeight) * lineHeight + startOffset;

		for (int i = startLineNumber; i <= endLineNumber; i++)
		{
			String lineNumber = String.valueOf(i);
			int stringWidth = fontMetrics.stringWidth( lineNumber );
			int rowWidth = getSize().width;
			g.drawString(lineNumber, rowWidth - stringWidth - MARGIN, start);
			start += lineHeight;
		}

		int rows = component.getSize().height / componentFontHeight;
		setPreferredWidth( rows );
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("LineNumberDemo");
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		JPanel panel = new JPanel();
		frame.setContentPane( panel );
		panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		panel.setLayout(new BorderLayout());

		JTextPane textPane = new JTextPane();
		textPane.setFont( new Font("monospaced", Font.PLAIN, 12) );
		textPane.setText("abc");

		JScrollPane scrollPane = new JScrollPane(textPane);
		panel.add(scrollPane);
		scrollPane.setPreferredSize(new Dimension(300, 250));

		LineNumber lineNumber = new LineNumber( textPane );
		scrollPane.setRowHeaderView( lineNumber );

		frame.pack();
		frame.setVisible(true);
	}
}

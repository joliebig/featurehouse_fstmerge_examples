

package org.jfree.chart.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.Title;
import org.jfree.chart.title.TextTitle;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.FontChooserPanel;
import org.jfree.ui.FontDisplayField;
import org.jfree.ui.PaintSample;


class DefaultTitleEditor extends JPanel implements ActionListener {

    
    private boolean showTitle;

    
    private JCheckBox showTitleCheckBox;

    
    private JTextField titleField;

    
    private Font titleFont;

    
    private JTextField fontfield;

    
    private JButton selectFontButton;

    
    private PaintSample titlePaint;

    
    private JButton selectPaintButton;

    
    protected static ResourceBundle localizationResources 
        = ResourceBundle.getBundle("org.jfree.chart.editor.LocalizationBundle");

    
    public DefaultTitleEditor(Title title) {

        TextTitle t = (title != null ? (TextTitle) title 
                : new TextTitle(localizationResources.getString("Title")));
        this.showTitle = (title != null);
        this.titleFont = t.getFont();
        this.titleField = new JTextField(t.getText());
        this.titlePaint = new PaintSample(t.getPaint());

        setLayout(new BorderLayout());

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                localizationResources.getString("General")
            )
        );

        JPanel interior = new JPanel(new LCBLayout(4));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        interior.add(new JLabel(localizationResources.getString("Show_Title")));
        this.showTitleCheckBox = new JCheckBox();
        this.showTitleCheckBox.setSelected(this.showTitle);
        this.showTitleCheckBox.setActionCommand("ShowTitle");
        this.showTitleCheckBox.addActionListener(this);
        interior.add(new JPanel());
        interior.add(this.showTitleCheckBox);

        JLabel titleLabel = new JLabel(localizationResources.getString("Text"));
        interior.add(titleLabel);
        interior.add(this.titleField);
        interior.add(new JPanel());

        JLabel fontLabel = new JLabel(localizationResources.getString("Font"));
        this.fontfield = new FontDisplayField(this.titleFont);
        this.selectFontButton = new JButton(
            localizationResources.getString("Select...")
        );
        this.selectFontButton.setActionCommand("SelectFont");
        this.selectFontButton.addActionListener(this);
        interior.add(fontLabel);
        interior.add(this.fontfield);
        interior.add(this.selectFontButton);

        JLabel colorLabel = new JLabel(
            localizationResources.getString("Color")
        );
        this.selectPaintButton = new JButton(
            localizationResources.getString("Select...")
        );
        this.selectPaintButton.setActionCommand("SelectPaint");
        this.selectPaintButton.addActionListener(this);
        interior.add(colorLabel);
        interior.add(this.titlePaint);
        interior.add(this.selectPaintButton);

        this.enableOrDisableControls();

        general.add(interior);
        add(general, BorderLayout.NORTH);
    }

    
    public String getTitleText() {
        return this.titleField.getText();
    }

    
    public Font getTitleFont() {
        return this.titleFont;
    }

    
    public Paint getTitlePaint() {
        return this.titlePaint.getPaint();
    }

    
    public void actionPerformed(ActionEvent event) {

        String command = event.getActionCommand();

        if (command.equals("SelectFont")) {
            attemptFontSelection();
        }
        else if (command.equals("SelectPaint")) {
            attemptPaintSelection();
        }
        else if (command.equals("ShowTitle")) {
            attemptModifyShowTitle();
        }
    }

    
    public void attemptFontSelection() {

        FontChooserPanel panel = new FontChooserPanel(this.titleFont);
        int result = 
            JOptionPane.showConfirmDialog(
                this, panel, localizationResources.getString("Font_Selection"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

        if (result == JOptionPane.OK_OPTION) {
            this.titleFont = panel.getSelectedFont();
            this.fontfield.setText(
                this.titleFont.getFontName() + " " + this.titleFont.getSize()
            );
        }
    }

    
    public void attemptPaintSelection() {
        Paint p = this.titlePaint.getPaint();
        Color defaultColor = (p instanceof Color ? (Color) p : Color.blue);
        Color c = JColorChooser.showDialog(
            this, localizationResources.getString("Title_Color"), defaultColor
        );
        if (c != null) {
            this.titlePaint.setPaint(c);
        }
    }

    
    private void attemptModifyShowTitle() {
        this.showTitle = this.showTitleCheckBox.isSelected();
        this.enableOrDisableControls();
    }

    
    private void enableOrDisableControls() {
        boolean enabled = (this.showTitle == true);
        this.titleField.setEnabled(enabled);
        this.selectFontButton.setEnabled(enabled);
        this.selectPaintButton.setEnabled(enabled);
    }

    
    public void setTitleProperties(JFreeChart chart) {
        if (this.showTitle) {
            TextTitle title = chart.getTitle();
            if (title == null) {
                title = new TextTitle();
                chart.setTitle(title);
            }
            title.setText(getTitleText());
            title.setFont(getTitleFont());
            title.setPaint(getTitlePaint());
        }
        else {
            chart.setTitle((TextTitle) null);
        }
    }

}



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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.ui.FontChooserPanel;
import org.jfree.chart.ui.FontDisplayField;
import org.jfree.chart.ui.LCBLayout;
import org.jfree.chart.ui.PaintSample;
import org.jfree.chart.util.RectangleInsets;



class DefaultAxisEditor extends JPanel implements ActionListener {

    
    private JTextField label;

    
    private Font labelFont;

    
    private PaintSample labelPaintSample;

    
    private JTextField labelFontField;

    
    private Font tickLabelFont;

    
    private JTextField tickLabelFontField;

    
    private PaintSample tickLabelPaintSample;

    
    private JPanel slot1;

    
    private JPanel slot2;

    
    private JCheckBox showTickLabelsCheckBox;

    
    private JCheckBox showTickMarksCheckBox;







    
    private RectangleInsets tickLabelInsets;

    
    private RectangleInsets labelInsets;

    
    private JTabbedPane otherTabs;

    
    protected static ResourceBundle localizationResources = 
        ResourceBundle.getBundle("org.jfree.chart.editor.LocalizationBundle");

    
    public static DefaultAxisEditor getInstance(Axis axis) {

        if (axis != null) {
            
            
            if (axis instanceof NumberAxis) {
                return new DefaultNumberAxisEditor((NumberAxis) axis);
            }
            else {
                return new DefaultAxisEditor(axis);
            }
        }
        else {
            return null;
        }

    }

    
    public DefaultAxisEditor(Axis axis) {

        this.labelFont = axis.getLabelFont();
        this.labelPaintSample = new PaintSample(axis.getLabelPaint());
        this.tickLabelFont = axis.getTickLabelFont();
        this.tickLabelPaintSample = new PaintSample(axis.getTickLabelPaint());

        
        this.tickLabelInsets = axis.getTickLabelInsets();
        this.labelInsets = axis.getLabelInsets();

        setLayout(new BorderLayout());

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                localizationResources.getString("General")
            )
        );

        JPanel interior = new JPanel(new LCBLayout(5));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        interior.add(new JLabel(localizationResources.getString("Label")));
        this.label = new JTextField(axis.getLabel());
        interior.add(this.label);
        interior.add(new JPanel());

        interior.add(new JLabel(localizationResources.getString("Font")));
        this.labelFontField = new FontDisplayField(this.labelFont);
        interior.add(this.labelFontField);
        JButton b = new JButton(localizationResources.getString("Select..."));
        b.setActionCommand("SelectLabelFont");
        b.addActionListener(this);
        interior.add(b);

        interior.add(new JLabel(localizationResources.getString("Paint")));
        interior.add(this.labelPaintSample);
        b = new JButton(localizationResources.getString("Select..."));
        b.setActionCommand("SelectLabelPaint");
        b.addActionListener(this);
        interior.add(b);






















        general.add(interior);

        add(general, BorderLayout.NORTH);

        this.slot1 = new JPanel(new BorderLayout());

        JPanel other = new JPanel(new BorderLayout());
        other.setBorder(BorderFactory.createTitledBorder(
                             BorderFactory.createEtchedBorder(), 
                             localizationResources.getString("Other")));

        this.otherTabs = new JTabbedPane();
        this.otherTabs.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JPanel ticks = new JPanel(new LCBLayout(3));
        ticks.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        this.showTickLabelsCheckBox = new JCheckBox(
            localizationResources.getString("Show_tick_labels"), 
            axis.isTickLabelsVisible()
        );
        ticks.add(this.showTickLabelsCheckBox);
        ticks.add(new JPanel());
        ticks.add(new JPanel());

        ticks.add(
            new JLabel(localizationResources.getString("Tick_label_font"))
        );
        this.tickLabelFontField = new FontDisplayField(this.tickLabelFont);
        ticks.add(this.tickLabelFontField);
        b = new JButton(localizationResources.getString("Select..."));
        b.setActionCommand("SelectTickLabelFont");
        b.addActionListener(this);
        ticks.add(b);

        this.showTickMarksCheckBox = new JCheckBox(
            localizationResources.getString("Show_tick_marks"), 
            axis.isTickMarksVisible()
        );
        ticks.add(this.showTickMarksCheckBox);
        ticks.add(new JPanel());
        ticks.add(new JPanel());

        this.otherTabs.add(localizationResources.getString("Ticks"), ticks);

        other.add(this.otherTabs);

        this.slot1.add(other);

        this.slot2 = new JPanel(new BorderLayout());
        this.slot2.add(this.slot1, BorderLayout.NORTH);
        add(this.slot2);

    }

    
    public String getLabel() {
        return this.label.getText();
    }

    
    public Font getLabelFont() {
        return this.labelFont;
    }

    
    public Paint getLabelPaint() {
        return this.labelPaintSample.getPaint();
    }

    
    public boolean isTickLabelsVisible() {
        return this.showTickLabelsCheckBox.isSelected();
    }

    
    public Font getTickLabelFont() {
        return this.tickLabelFont;
    }

    
    public Paint getTickLabelPaint() {
        return this.tickLabelPaintSample.getPaint();
    }

    
    public boolean isTickMarksVisible() {
        return this.showTickMarksCheckBox.isSelected();
    }

    
    public RectangleInsets getTickLabelInsets() {
        return (this.tickLabelInsets == null)
            ? new RectangleInsets(0, 0, 0, 0)
            : this.tickLabelInsets;
    }

    
    public RectangleInsets getLabelInsets() {
        return (this.labelInsets == null) 
            ? new RectangleInsets(0, 0, 0, 0) : this.labelInsets;
    }

    
    public JTabbedPane getOtherTabs() {
        return this.otherTabs;
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("SelectLabelFont")) {
            attemptLabelFontSelection();
        }
        else if (command.equals("SelectLabelPaint")) {
            attemptModifyLabelPaint();
        }
        else if (command.equals("SelectTickLabelFont")) {
            attemptTickLabelFontSelection();
        }






    }

    
    private void attemptLabelFontSelection() {

        FontChooserPanel panel = new FontChooserPanel(this.labelFont);
        int result = JOptionPane.showConfirmDialog(this, panel,
            localizationResources.getString("Font_Selection"),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            this.labelFont = panel.getSelectedFont();
            this.labelFontField.setText(
                this.labelFont.getFontName() + " " + this.labelFont.getSize()
            );
        }

    }

    
    private void attemptModifyLabelPaint() {
        Color c;
        c = JColorChooser.showDialog(
            this, localizationResources.getString("Label_Color"), Color.blue
        );
        if (c != null) {
            this.labelPaintSample.setPaint(c);
        }
    }

    
    public void attemptTickLabelFontSelection() {

        FontChooserPanel panel = new FontChooserPanel(this.tickLabelFont);
        int result = JOptionPane.showConfirmDialog(this, panel,
            localizationResources.getString("Font_Selection"),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            this.tickLabelFont = panel.getSelectedFont();
            this.tickLabelFontField.setText(
                this.tickLabelFont.getFontName() + " "
                + this.tickLabelFont.getSize()
            );
        }

    }





































    
    public void setAxisProperties(Axis axis) {
        axis.setLabel(getLabel());
        axis.setLabelFont(getLabelFont());
        axis.setLabelPaint(getLabelPaint());
        axis.setTickMarksVisible(isTickMarksVisible());
        
        axis.setTickLabelsVisible(isTickLabelsVisible());
        axis.setTickLabelFont(getTickLabelFont());
        axis.setTickLabelPaint(getTickLabelPaint());
        axis.setTickLabelInsets(getTickLabelInsets());
        axis.setLabelInsets(getLabelInsets());
    }

}

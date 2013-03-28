

package org.jfree.chart.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.ui.LCBLayout;
import org.jfree.chart.ui.PaintSample;
import org.jfree.chart.ui.StrokeChooserPanel;
import org.jfree.chart.ui.StrokeSample;


class DefaultNumberAxisEditor extends DefaultAxisEditor 
                              implements FocusListener {

    
    private boolean autoRange;

    
    private double minimumValue;

    
    private double maximumValue;

    
    private JCheckBox autoRangeCheckBox;

    
    private JTextField minimumRangeValue;

    
    private JTextField maximumRangeValue;

    
    private PaintSample gridPaintSample;

    
    private StrokeSample gridStrokeSample;

    
    private StrokeSample[] availableStrokeSamples;
    
    
    protected static ResourceBundle localizationResources = 
        ResourceBundle.getBundle("org.jfree.chart.editor.LocalizationBundle");

    
    public DefaultNumberAxisEditor(NumberAxis axis) {

        super(axis);

        this.autoRange = axis.isAutoRange();
        this.minimumValue = axis.getLowerBound();
        this.maximumValue = axis.getUpperBound();

        this.gridPaintSample = new PaintSample(Color.blue);
        this.gridStrokeSample = new StrokeSample(new BasicStroke(1.0f));

        this.availableStrokeSamples = new StrokeSample[3];
        this.availableStrokeSamples[0] = new StrokeSample(
                new BasicStroke(1.0f));
        this.availableStrokeSamples[1] = new StrokeSample(
                new BasicStroke(2.0f));
        this.availableStrokeSamples[2] = new StrokeSample(
                new BasicStroke(3.0f));

        JTabbedPane other = getOtherTabs();

        JPanel range = new JPanel(new LCBLayout(3));
        range.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        range.add(new JPanel());
        this.autoRangeCheckBox = new JCheckBox(
            localizationResources.getString("Auto-adjust_range"), this.autoRange
        );
        this.autoRangeCheckBox.setActionCommand("AutoRangeOnOff");
        this.autoRangeCheckBox.addActionListener(this);
        range.add(this.autoRangeCheckBox);
        range.add(new JPanel());

        range.add(
            new JLabel(localizationResources.getString("Minimum_range_value"))
        );
        this.minimumRangeValue = new JTextField(
            Double.toString(this.minimumValue)
        );
        this.minimumRangeValue.setEnabled(!this.autoRange);
        this.minimumRangeValue.setActionCommand("MinimumRange");
        this.minimumRangeValue.addActionListener(this);
        this.minimumRangeValue.addFocusListener(this);
        range.add(this.minimumRangeValue);
        range.add(new JPanel());

        range.add(
            new JLabel(localizationResources.getString("Maximum_range_value"))
        );
        this.maximumRangeValue = new JTextField(
            Double.toString(this.maximumValue)
        );
        this.maximumRangeValue.setEnabled(!this.autoRange);
        this.maximumRangeValue.setActionCommand("MaximumRange");
        this.maximumRangeValue.addActionListener(this);
        this.maximumRangeValue.addFocusListener(this);
        range.add(this.maximumRangeValue);
        range.add(new JPanel());

        other.add(localizationResources.getString("Range"), range);

    }

    
    public boolean isAutoRange() {
        return this.autoRange;
    }

    
    public double getMinimumValue() {
        return this.minimumValue;
    }

    
    public double getMaximumValue() {
        return this.maximumValue;
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("GridStroke")) {
            attemptGridStrokeSelection();
        }
        else if (command.equals("GridPaint")) {
            attemptGridPaintSelection();
        }
        else if (command.equals("AutoRangeOnOff")) {
            toggleAutoRange();
        }
        else if (command.equals("MinimumRange")) {
            validateMinimum();
        }
        else if (command.equals("MaximumRange")) {
            validateMaximum();
        }
        else {
            
            super.actionPerformed(event);
        }
    }

    
    private void attemptGridStrokeSelection() {
        StrokeChooserPanel panel = new StrokeChooserPanel(
            null, this.availableStrokeSamples
        );
        int result = JOptionPane.showConfirmDialog(
            this, panel, localizationResources.getString("Stroke_Selection"),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            this.gridStrokeSample.setStroke(panel.getSelectedStroke());
        }
    }

    
    private void attemptGridPaintSelection() {
        Color c;
        c = JColorChooser.showDialog(
            this, localizationResources.getString("Grid_Color"), Color.blue
        );
        if (c != null) {
            this.gridPaintSample.setPaint(c);
        }
    }

    
    public void focusGained(FocusEvent event) {
        
    }

    
    public void focusLost(FocusEvent event) {
        if (event.getSource() == this.minimumRangeValue) {
            validateMinimum();
        }
        else if (event.getSource() == this.maximumRangeValue) {
            validateMaximum();
        }
    }

    
    public void toggleAutoRange() {
        this.autoRange = this.autoRangeCheckBox.isSelected();
        if (this.autoRange) {
            this.minimumRangeValue.setText(Double.toString(this.minimumValue));
            this.minimumRangeValue.setEnabled(false);
            this.maximumRangeValue.setText(Double.toString(this.maximumValue));
            this.maximumRangeValue.setEnabled(false);
        }
        else {
            this.minimumRangeValue.setEnabled(true);
            this.maximumRangeValue.setEnabled(true);
        }
    }

    
    public void validateMinimum() {
        double newMin;
        try {
            newMin = Double.parseDouble(this.minimumRangeValue.getText());
            if (newMin >= this.maximumValue) {
                newMin = this.minimumValue;
            }
        }
        catch (NumberFormatException e) {
            newMin = this.minimumValue;
        }

        this.minimumValue = newMin;
        this.minimumRangeValue.setText(Double.toString(this.minimumValue));
    }

    
    public void validateMaximum() {
        double newMax;
        try {
            newMax = Double.parseDouble(this.maximumRangeValue.getText());
            if (newMax <= this.minimumValue) {
                newMax = this.maximumValue;
            }
        }
        catch (NumberFormatException e) {
            newMax = this.maximumValue;
        }

        this.maximumValue = newMax;
        this.maximumRangeValue.setText(Double.toString(this.maximumValue));
    }

    
    public void setAxisProperties(Axis axis) {
        super.setAxisProperties(axis);
        NumberAxis numberAxis = (NumberAxis) axis;
        numberAxis.setAutoRange(this.autoRange);
        if (!this.autoRange) {
            numberAxis.setRange(this.minimumValue, this.maximumValue);
        }
    }

}

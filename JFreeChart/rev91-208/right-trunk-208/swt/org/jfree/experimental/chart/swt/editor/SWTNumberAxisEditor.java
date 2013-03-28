

package org.jfree.experimental.chart.swt.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;


class SWTNumberAxisEditor extends SWTAxisEditor implements FocusListener {
    
    
    private boolean autoRange;

    
    private double minimumValue;

    
    private double maximumValue;

    
    private Button autoRangeCheckBox;

    
    private Text minimumRangeValue;

    
    private Text maximumRangeValue;

    
    public SWTNumberAxisEditor(Composite parent, int style, NumberAxis axis) {
        super(parent, style, axis);
        this.autoRange = axis.isAutoRange();
        this.minimumValue = axis.getLowerBound();
        this.maximumValue = axis.getUpperBound();

        TabItem item2 = new TabItem(getOtherTabs(), SWT.NONE);
        item2.setText(" " + localizationResources.getString("Range") + " ");
        Composite range = new Composite(getOtherTabs(), SWT.NONE);
        range.setLayout(new GridLayout(2, true));
        item2.setControl(range);
        
        autoRangeCheckBox = new Button(range, SWT.CHECK);
        autoRangeCheckBox.setText(localizationResources.getString(
                "Auto-adjust_range"));
        autoRangeCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, 
                true, false, 2, 1));
        autoRangeCheckBox.setSelection(this.autoRange);
        autoRangeCheckBox.addSelectionListener( 
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) { 
                        toggleAutoRange();
                    }
                });
        new Label(range, SWT.NONE).setText(localizationResources.getString(
                "Minimum_range_value"));
        this.minimumRangeValue = new Text(range, SWT.BORDER);
        this.minimumRangeValue.setText(String.valueOf(this.minimumValue));
        this.minimumRangeValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false));
        this.minimumRangeValue.setEnabled(!this.autoRange);
        
        
        this.minimumRangeValue.addFocusListener(this);
        new Label(range, SWT.NONE).setText(localizationResources.getString(
                "Maximum_range_value"));
        this.maximumRangeValue = new Text(range, SWT.BORDER);
        this.maximumRangeValue.setText(String.valueOf(this.maximumValue));
        this.maximumRangeValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false));
        this.maximumRangeValue.setEnabled(!this.autoRange);
        
        
        this.maximumRangeValue.addFocusListener(this);
    }

    
    public void toggleAutoRange() {
        this.autoRange = this.autoRangeCheckBox.getSelection();
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

    
    public boolean validateMinimum(String candidate)
    {
        boolean valid = true;
        try {
            if (Double.parseDouble(candidate) >= this.maximumValue) {
                valid = false;
            }
        }
        catch (NumberFormatException e) {
            valid = false;
        }
        return valid;
    }

    
    public boolean validateMaximum(String candidate)
    {
        boolean valid = true;
        try {
            if (Double.parseDouble(candidate) <= this.minimumValue) {
                valid = false;
            }
        }
        catch (NumberFormatException e) {
            valid = false;
        }
        return valid;
    }

    
    public void focusGained(FocusEvent e) {
        
    }

    
    public void focusLost(FocusEvent e) {
        if (e.getSource() == this.minimumRangeValue) {
            
            if (! validateMinimum( this.minimumRangeValue.getText()))
                this.minimumRangeValue.setText(String.valueOf(
                        this.minimumValue));
            else
                this.minimumValue = Double.parseDouble(
                        this.minimumRangeValue.getText());
        }
        else if (e.getSource() == this.maximumRangeValue) {
            
            if (! validateMaximum(this.maximumRangeValue.getText()))
                this.maximumRangeValue.setText(String.valueOf(
                        this.maximumValue));
            else
                this.maximumValue = Double.parseDouble(
                        this.maximumRangeValue.getText());
        }
    }

    
    public void setAxisProperties(Axis axis) {
        super.setAxisProperties(axis);
        NumberAxis numberAxis = (NumberAxis) axis;
        numberAxis.setAutoRange(this.autoRange);
        if (! this.autoRange)
            numberAxis.setRange(this.minimumValue, this.maximumValue);
    }
}

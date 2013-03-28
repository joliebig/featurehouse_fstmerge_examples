

package org.jfree.experimental.chart.swt.editor;

import java.awt.Paint;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.experimental.swt.SWTPaintCanvas;
import org.jfree.experimental.swt.SWTUtils;


class SWTAxisEditor extends Composite {
    
    
    private Text label;
    
    
    private FontData labelFont;

    
    private Color labelPaintColor;

    
    private FontData tickLabelFont;

    
    private Color tickLabelPaintColor;

    
    private Text labelFontField;
    
    
    private Text tickLabelFontField;

    
    protected static ResourceBundle localizationResources = 
        ResourceBundle.getBundle("org.jfree.chart.editor.LocalizationBundle");

    
    private Font font;

    
    private Button showTickLabelsCheckBox;
    
    
    private Button showTickMarksCheckBox;
    
    
    private TabFolder otherTabs;
    
    
    public SWTAxisEditor(Composite parent, int style, Axis axis) {
        super(parent, style);
        this.labelFont = SWTUtils.toSwtFontData(getDisplay(), 
                axis.getLabelFont(), true);
        this.labelPaintColor = SWTUtils.toSwtColor(getDisplay(), 
                axis.getLabelPaint());
        this.tickLabelFont = SWTUtils.toSwtFontData(getDisplay(), 
                axis.getTickLabelFont(), true);
        this.tickLabelPaintColor = SWTUtils.toSwtColor(getDisplay(), 
                axis.getTickLabelPaint());
        
        FillLayout layout = new FillLayout(SWT.VERTICAL);
        layout.marginHeight = layout.marginWidth = 4;
        this.setLayout(layout);
        Group general = new Group(this, SWT.NONE);
        general.setLayout(new GridLayout(3, false));
        general.setText(localizationResources.getString("General"));
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Label"));
        label = new Text(general, SWT.BORDER);
        if (axis.getLabel() != null) {
            label.setText(axis.getLabel());
        }
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        new Label(general, SWT.NONE).setText(""); 
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Font"));
        labelFontField = new Text(general, SWT.BORDER);
        labelFontField.setText(this.labelFont.toString());
        labelFontField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, 
                false));
        Button selectFontButton = new Button(general, SWT.PUSH);
        selectFontButton.setText(localizationResources.getString("Select..."));
        selectFontButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        
                        FontDialog dlg = new FontDialog(getShell());
                        dlg.setText(localizationResources.getString(
                                "Font_Selection"));
                        dlg.setFontList(new FontData[] { labelFont });
                        if (dlg.open() != null) {
                            
                            if (font != null) {
                                font.dispose();
                            }
                            
                            
                            font = new Font(getShell().getDisplay(), 
                                    dlg.getFontList());
                            
                            labelFontField.setText(
                                    font.getFontData()[0].toString());
                            labelFont = font.getFontData()[0];
                        }
                    }
                }
        );
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Paint"));
        
        final SWTPaintCanvas colorCanvas = new SWTPaintCanvas(general, 
                SWT.NONE, this.labelPaintColor);
        GridData canvasGridData = new GridData(SWT.FILL, SWT.CENTER, true, 
                false);
        canvasGridData.heightHint = 20;
        colorCanvas.setLayoutData(canvasGridData);
        Button selectColorButton = new Button(general, SWT.PUSH);
        selectColorButton.setText(localizationResources.getString("Select..."));
        selectColorButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        
                        ColorDialog dlg = new ColorDialog(getShell());
                        dlg.setText(localizationResources.getString(
                                "Title_Color"));
                        dlg.setRGB(labelPaintColor.getRGB());
                        RGB rgb = dlg.open();
                        if (rgb != null) {
                          
                          
                          labelPaintColor = new Color(getDisplay(), rgb);
                          colorCanvas.setColor(labelPaintColor);
                        }
                    }
                }
        );
        Group other = new Group(this, SWT.NONE);
        FillLayout tabLayout = new FillLayout();
        tabLayout.marginHeight = tabLayout.marginWidth = 4;
        other.setLayout(tabLayout);
        other.setText(localizationResources.getString("Other"));
        
        otherTabs = new TabFolder(other, SWT.NONE);
        TabItem item1 = new TabItem(otherTabs, SWT.NONE);
        item1.setText(" " + localizationResources.getString("Ticks") + " ");
        Composite ticks = new Composite(otherTabs, SWT.NONE);
        ticks.setLayout(new GridLayout(3, false));
        showTickLabelsCheckBox = new Button(ticks, SWT.CHECK);
        showTickLabelsCheckBox.setText(localizationResources.getString(
                "Show_tick_labels"));
        showTickLabelsCheckBox.setSelection(axis.isTickLabelsVisible());
        showTickLabelsCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, false, 3, 1));
        new Label(ticks, SWT.NONE).setText(localizationResources.getString(
                "Tick_label_font"));
        tickLabelFontField = new Text(ticks, SWT.BORDER);
        tickLabelFontField.setText(this.tickLabelFont.toString());
        
        
        tickLabelFontField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, 
                true, false));
        Button selectTickLabelFontButton = new Button(ticks, SWT.PUSH);
        selectTickLabelFontButton.setText(localizationResources.getString(
                "Select..."));
        selectTickLabelFontButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        
                        FontDialog dlg = new FontDialog(getShell());
                        dlg.setText( localizationResources.getString(
                                "Font_Selection"));
                        dlg.setFontList(new FontData[] {tickLabelFont});
                        if (dlg.open() != null) {
                            
                            if (font != null) font.dispose();
                            
                            font = new Font(getShell().getDisplay(), 
                                    dlg.getFontList());
                            
                            tickLabelFontField.setText(
                                    font.getFontData()[0].toString());
                            tickLabelFont = font.getFontData()[0];
                        }
                    }
                }
        );
        showTickMarksCheckBox = new Button(ticks, SWT.CHECK);
        showTickMarksCheckBox.setText(localizationResources.getString(
                "Show_tick_marks"));
        showTickMarksCheckBox.setSelection(axis.isTickMarksVisible());
        showTickMarksCheckBox.setLayoutData(new GridData( SWT.FILL, SWT.CENTER,
                true, false, 3, 1));
        item1.setControl(ticks);
    }

    
    public static SWTAxisEditor getInstance(Composite parent, int style, 
            Axis axis) {
        
        if (axis != null) {
            
            if (axis instanceof NumberAxis)
                return new SWTNumberAxisEditor(parent, style, 
                        (NumberAxis) axis );
            else return new SWTAxisEditor(parent, style, axis);
        }
        else return null;
    }
    
    
    public TabFolder getOtherTabs() {
        return this.otherTabs;
    }

    
    public String getLabel() {
        return this.label.getText();
    }

    
    public java.awt.Font getLabelFont() {
        return SWTUtils.toAwtFont(getDisplay(), this.labelFont, true);
    }

    
    public Paint getTickLabelPaint() {
        return SWTUtils.toAwtColor(this.tickLabelPaintColor);
    }
    
    
    public java.awt.Font getTickLabelFont() {
        return SWTUtils.toAwtFont(getDisplay(), tickLabelFont, true);
    }

    
    public Paint getLabelPaint() {
        return SWTUtils.toAwtColor(this.labelPaintColor);
    }
    
    
    public void setAxisProperties(Axis axis) {
        axis.setLabel(getLabel());
        axis.setLabelFont(getLabelFont());
        axis.setLabelPaint(getLabelPaint());
        axis.setTickMarksVisible(showTickMarksCheckBox.getSelection());
        axis.setTickLabelsVisible(showTickLabelsCheckBox.getSelection());
        axis.setTickLabelFont(getTickLabelFont());
        axis.setTickLabelPaint(getTickLabelPaint());
    }
}

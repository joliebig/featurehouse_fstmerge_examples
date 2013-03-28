

package org.jfree.experimental.chart.swt.editor;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.swt.SWTPaintCanvas;
import org.jfree.experimental.swt.SWTUtils;


class SWTOtherEditor extends Composite {
    
    
    private Button antialias;

    
    private SWTPaintCanvas backgroundPaintCanvas;

    
    protected static ResourceBundle localizationResources 
        = ResourceBundle.getBundle("org.jfree.chart.editor.LocalizationBundle");

    
    public SWTOtherEditor(Composite parent, int style, JFreeChart chart)
    {
        super(parent, style);
        FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = 4;
        setLayout(layout);

        Group general = new Group(this, SWT.NONE);
        general.setLayout(new GridLayout(3, false));
        general.setText(localizationResources.getString("General"));
        
        
        antialias = new Button(general, SWT.CHECK);
        antialias.setText(localizationResources.getString("Draw_anti-aliased"));
        antialias.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false,
                3, 1));
        antialias.setSelection(chart.getAntiAlias());
        
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Background_paint"));
        backgroundPaintCanvas = new SWTPaintCanvas(general, SWT.NONE, 
                SWTUtils.toSwtColor(getDisplay(), chart.getBackgroundPaint()));
        GridData bgGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        bgGridData.heightHint = 20;
        backgroundPaintCanvas.setLayoutData(bgGridData);
        Button selectBgPaint = new Button(general, SWT.PUSH);
        selectBgPaint.setText(localizationResources.getString("Select..."));
        selectBgPaint.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
                false));
        selectBgPaint.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        ColorDialog dlg = new ColorDialog(getShell());
                        dlg.setText(localizationResources.getString(
                                "Background_paint"));
                        dlg.setRGB(backgroundPaintCanvas.getColor().getRGB());
                        RGB rgb = dlg.open();
                        if (rgb != null) {
                            backgroundPaintCanvas.setColor(
                                    new Color(getDisplay(), rgb));
                        }
                    }
                }
        );
    }
    
    
    public void updateChartProperties(JFreeChart chart) {
        chart.setAntiAlias(this.antialias.getSelection());
        chart.setBackgroundPaint(SWTUtils.toAwtColor(
                this.backgroundPaintCanvas.getColor()));        
    }

}

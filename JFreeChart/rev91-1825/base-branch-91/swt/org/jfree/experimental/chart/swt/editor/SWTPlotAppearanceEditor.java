

package org.jfree.experimental.chart.swt.editor;

import java.awt.BasicStroke;
import java.awt.Stroke;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.experimental.swt.SWTPaintCanvas;
import org.jfree.experimental.swt.SWTUtils;


class SWTPlotAppearanceEditor extends Composite
{
    
    private Spinner selectStroke;
    
    
    private SWTStrokeCanvas strokeCanvas;
    
    
    private SWTPaintCanvas backgroundPaintCanvas;

    
    private SWTPaintCanvas outlinePaintCanvas;

    
    private PlotOrientation plotOrientation;

    private Combo orientation;
    
    
    private final static String[] orientationNames = { "Vertical", 
        "Horizontal" };
    private final static int ORIENTATION_VERTICAL = 0;
    private final static int ORIENTATION_HORIZONTAL = 1;

    
    protected static ResourceBundle localizationResources 
        = ResourceBundle.getBundle("org.jfree.chart.editor.LocalizationBundle");

    SWTPlotAppearanceEditor(Composite parent, int style, Plot plot)
    {
        super(parent, style);
        FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = 4;
        this.setLayout(layout);

        Group general = new Group(this, SWT.NONE);
        GridLayout groupLayout = new GridLayout(3, false);
        groupLayout.marginHeight = groupLayout.marginWidth = 4;
        general.setLayout(groupLayout);
        general.setText( localizationResources.getString("General"));
        
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Outline_stroke"));
        strokeCanvas = new SWTStrokeCanvas(general, SWT.NONE);
        strokeCanvas.setStroke(plot.getOutlineStroke());
        GridData strokeGridData = new GridData(SWT.FILL, SWT.CENTER, true, 
                false);
        strokeGridData.heightHint = 20;
        strokeCanvas.setLayoutData(strokeGridData);
        selectStroke = new Spinner(general, SWT.BORDER);
        selectStroke.setMinimum(1);
        selectStroke.setMaximum(3);
        selectStroke.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, 
                false));
        selectStroke.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        int w = selectStroke.getSelection();
                        if (w > 0) {
                            strokeCanvas.setStroke(new BasicStroke(w));
                            strokeCanvas.redraw();
                        }
                    }
                }
        );
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Outline_Paint"));
        outlinePaintCanvas = new SWTPaintCanvas(general, SWT.NONE, 
                SWTUtils.toSwtColor(getDisplay(), plot.getOutlinePaint()));
        GridData outlineGridData = new GridData(SWT.FILL, SWT.CENTER, true, 
                false);
        outlineGridData.heightHint = 20;
        outlinePaintCanvas.setLayoutData(outlineGridData);
        Button selectOutlineColor = new Button(general, SWT.PUSH);
        selectOutlineColor.setText(localizationResources.getString(
                "Select..."));
        selectOutlineColor.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, 
                false, false));
        selectOutlineColor.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        ColorDialog dlg = new ColorDialog(getShell());
                        dlg.setText(localizationResources.getString(
                                "Outline_Paint"));
                        dlg.setRGB(outlinePaintCanvas.getColor().getRGB());
                        RGB rgb = dlg.open();
                        if (rgb != null) {
                            outlinePaintCanvas.setColor(new Color(getDisplay(),
                                    rgb));
                        }
                    }
                }
        );
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Background_paint"));
        backgroundPaintCanvas = new SWTPaintCanvas(general, SWT.NONE, 
                SWTUtils.toSwtColor(getDisplay(), plot.getBackgroundPaint()));
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
        
        if (plot instanceof CategoryPlot) {
            this.plotOrientation = ((CategoryPlot) plot).getOrientation();
        }
        else if (plot instanceof XYPlot) {
            this.plotOrientation = ((XYPlot) plot).getOrientation();
        }
        if (this.plotOrientation != null) {
            boolean isVertical 
                    = this.plotOrientation.equals(PlotOrientation.VERTICAL);
            int index = isVertical ? ORIENTATION_VERTICAL 
                    : ORIENTATION_HORIZONTAL;
            new Label(general, SWT.NONE).setText(
                    localizationResources.getString("Orientation"));
            orientation = new Combo(general, SWT.DROP_DOWN);
            orientation.setItems(orientationNames);
            orientation.select(index);
            orientation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
                    false, 2, 1));
            orientation.addSelectionListener( 
                    new SelectionAdapter() {
                        public void widgetSelected(SelectionEvent event) {
                            switch (orientation.getSelectionIndex()) {
                                case ORIENTATION_VERTICAL:
                                    plotOrientation = PlotOrientation.VERTICAL;
                                    break;
                                case ORIENTATION_HORIZONTAL:
                                    plotOrientation 
                                        = PlotOrientation.HORIZONTAL;
                                    break;
                                default:
                                    plotOrientation = PlotOrientation.VERTICAL;
                            }
                        }
                    }
            );
        }
    }

    
    public PlotOrientation getPlotOrientation() {
        return this.plotOrientation;
    }
    
    
    public Color getBackGroundPaint() {
        return backgroundPaintCanvas.getColor();
    }

    
    public Color getOutlinePaint() {
        return outlinePaintCanvas.getColor();
    }

    
    public Stroke getStroke() {
        return strokeCanvas.getStroke();
    }
}

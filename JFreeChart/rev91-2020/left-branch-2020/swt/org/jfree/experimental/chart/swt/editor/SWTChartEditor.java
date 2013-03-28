

package org.jfree.experimental.chart.swt.editor;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.util.ResourceBundleWrapper;


public class SWTChartEditor implements ChartEditor {

    
    private Shell shell;

    
    private JFreeChart chart;

    
    private SWTTitleEditor titleEditor;

    
    private SWTPlotEditor plotEditor;

    
    private SWTOtherEditor otherEditor;

    
    protected static ResourceBundle localizationResources
            = ResourceBundleWrapper.getBundle(
                    "org.jfree.chart.editor.LocalizationBundle");

    
    public SWTChartEditor(Display display, JFreeChart chart2edit) {
        this.shell = new Shell(display, SWT.DIALOG_TRIM);
        this.shell.setSize(400, 500);
        this.chart = chart2edit;
        this.shell.setText(ResourceBundleWrapper.getBundle(
                "org.jfree.chart.LocalizationBundle").getString(
                        "Chart_Properties"));
        GridLayout layout = new GridLayout(2, true);
        layout.marginLeft = layout.marginTop = layout.marginRight
                = layout.marginBottom = 5;
        this.shell.setLayout(layout);
        Composite main = new Composite(this.shell, SWT.NONE);
        main.setLayout(new FillLayout());
        main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        TabFolder tab = new TabFolder(main, SWT.BORDER);
        
        TabItem item1 = new TabItem(tab, SWT.NONE);
        item1.setText(" " + localizationResources.getString("Title") + " ");
        this.titleEditor = new SWTTitleEditor(tab, SWT.NONE,
                this.chart.getTitle());
        item1.setControl(this.titleEditor);
        
        TabItem item2 = new TabItem(tab, SWT.NONE);
        item2.setText(" " + localizationResources.getString("Plot") + " ");
        this.plotEditor = new SWTPlotEditor(tab, SWT.NONE,
                this.chart.getPlot());
        item2.setControl(this.plotEditor);
        
        TabItem item3 = new TabItem(tab, SWT.NONE);
        item3.setText(" " + localizationResources.getString("Other") + " ");
        this.otherEditor = new SWTOtherEditor(tab, SWT.NONE, this.chart);
        item3.setControl(this.otherEditor);

        
        Button ok = new Button(this.shell, SWT.PUSH | SWT.OK);
        ok.setText(" Ok ");
        ok.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        ok.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateChart(SWTChartEditor.this.chart);
                SWTChartEditor.this.shell.dispose();
            }
        });
        Button cancel = new Button(this.shell, SWT.PUSH);
        cancel.setText(" Cancel ");
        cancel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        cancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                SWTChartEditor.this.shell.dispose();
            }
        });
    }

    
    public void open() {
        this.shell.open();
        this.shell.layout();
        while (!this.shell.isDisposed()) {
            if (!this.shell.getDisplay().readAndDispatch()) {
                this.shell.getDisplay().sleep();
            }
        }
    }

    
    public void updateChart(JFreeChart chart)
    {
        this.titleEditor.setTitleProperties(chart);
        this.plotEditor.updatePlotProperties(chart.getPlot());
        this.otherEditor.updateChartProperties(chart);
    }

}

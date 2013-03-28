

package org.jfree.chart.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.Title;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.layout.LCBLayout;
import org.jfree.ui.PaintSample;


class DefaultChartEditor extends JPanel implements ActionListener, ChartEditor {

    
    private DefaultTitleEditor titleEditor;

    
    private DefaultPlotEditor plotEditor;

    
    private JCheckBox antialias;

    
    private PaintSample background;

    
    protected static ResourceBundle localizationResources
            = ResourceBundleWrapper.getBundle(
                    "org.jfree.chart.editor.LocalizationBundle");

    
    public DefaultChartEditor(JFreeChart chart) {
        setLayout(new BorderLayout());

        JPanel other = new JPanel(new BorderLayout());
        other.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            localizationResources.getString("General")));

        JPanel interior = new JPanel(new LCBLayout(6));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        this.antialias = new JCheckBox(localizationResources.getString(
                "Draw_anti-aliased"));
        this.antialias.setSelected(chart.getAntiAlias());
        interior.add(this.antialias);
        interior.add(new JLabel(""));
        interior.add(new JLabel(""));
        interior.add(new JLabel(localizationResources.getString(
                "Background_paint")));
        this.background = new PaintSample(chart.getBackgroundPaint());
        interior.add(this.background);
        JButton button = new JButton(localizationResources.getString(
                "Select..."));
        button.setActionCommand("BackgroundPaint");
        button.addActionListener(this);
        interior.add(button);

        interior.add(new JLabel(localizationResources.getString(
                "Series_Paint")));
        JTextField info = new JTextField(localizationResources.getString(
                "No_editor_implemented"));
        info.setEnabled(false);
        interior.add(info);
        button = new JButton(localizationResources.getString("Edit..."));
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel(localizationResources.getString(
                "Series_Stroke")));
        info = new JTextField(localizationResources.getString(
                "No_editor_implemented"));
        info.setEnabled(false);
        interior.add(info);
        button = new JButton(localizationResources.getString("Edit..."));
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel(localizationResources.getString(
                "Series_Outline_Paint")));
        info = new JTextField(localizationResources.getString(
                "No_editor_implemented"));
        info.setEnabled(false);
        interior.add(info);
        button = new JButton(localizationResources.getString("Edit..."));
        button.setEnabled(false);
        interior.add(button);

        interior.add(new JLabel(localizationResources.getString(
                "Series_Outline_Stroke")));
        info = new JTextField(localizationResources.getString(
                "No_editor_implemented"));
        info.setEnabled(false);
        interior.add(info);
        button = new JButton(localizationResources.getString("Edit..."));
        button.setEnabled(false);
        interior.add(button);

        general.add(interior, BorderLayout.NORTH);
        other.add(general, BorderLayout.NORTH);

        JPanel parts = new JPanel(new BorderLayout());

        Title title = chart.getTitle();
        Plot plot = chart.getPlot();

        JTabbedPane tabs = new JTabbedPane();

        this.titleEditor = new DefaultTitleEditor(title);
        this.titleEditor.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tabs.addTab(localizationResources.getString("Title"), this.titleEditor);

        this.plotEditor = new DefaultPlotEditor(plot);
        this.plotEditor.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        tabs.addTab(localizationResources.getString("Plot"), this.plotEditor);

        tabs.add(localizationResources.getString("Other"), other);
        parts.add(tabs, BorderLayout.NORTH);
        add(parts);
    }

    
    public DefaultTitleEditor getTitleEditor() {
      return this.titleEditor;
    }

    
    public DefaultPlotEditor getPlotEditor() {
        return this.plotEditor;
    }

    
    public boolean getAntiAlias() {
        return this.antialias.isSelected();
    }

    
    public Paint getBackgroundPaint() {
        return this.background.getPaint();
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("BackgroundPaint")) {
            attemptModifyBackgroundPaint();
        }
    }

    
    private void attemptModifyBackgroundPaint() {
        Color c;
        c = JColorChooser.showDialog(this, localizationResources.getString(
                "Background_Color"), Color.blue);
        if (c != null) {
            this.background.setPaint(c);
        }
    }

    
    public void updateChart(JFreeChart chart) {

        this.titleEditor.setTitleProperties(chart);
        this.plotEditor.updatePlotProperties(chart.getPlot());

        chart.setAntiAlias(getAntiAlias());
        chart.setBackgroundPaint(getBackgroundPaint());
    }

}

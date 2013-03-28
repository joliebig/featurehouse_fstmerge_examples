
package genj.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.NumberFormat;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.ui.RectangleInsets;


public class Chart extends JPanel {
  
  
  private void init(String title, Plot plot, boolean legend) {
    setLayout(new BorderLayout());
    ChartPanel panel = new ChartPanel(new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend));

    panel.setDomainZoomable(true);
    

    panel.setRangeZoomable(true);
    
    add(panel, BorderLayout.CENTER);
  }
  
  
  public Chart(String title, String labelAxisX, String labelAxisY, IndexedSeries[] series, NumberFormat format, boolean stacked) {

    
    NumberAxis xAxis = new NumberAxis(labelAxisX);
    xAxis.setAutoRangeIncludesZero(false);
    
    NumberAxis yAxis = new NumberAxis(labelAxisY);
    yAxis.setNumberFormatOverride(format);
    
    XYItemRenderer renderer = stacked ? new StackedXYAreaRenderer2() : new XYAreaRenderer();
    XYPlot plot = new XYPlot(IndexedSeries.asTableXYDataset(series), xAxis, yAxis, renderer);

    
    init(title, plot, true);
    
    
  }
  
  
  public Chart(String title, String labelAxisX, String labelAxisY, XYSeries[] series, NumberFormat format, boolean shapes) {
    
    
    NumberAxis xAxis = new NumberAxis(labelAxisX);
    xAxis.setAutoRangeIncludesZero(false);
    
    NumberAxis yAxis = new NumberAxis(labelAxisY);
    yAxis.setNumberFormatOverride(format);
    
    XYItemRenderer renderer = new StandardXYItemRenderer(shapes ? StandardXYItemRenderer.SHAPES_AND_LINES : StandardXYItemRenderer.LINES);
    
    XYPlot plot = new XYPlot(XYSeries.toXYDataset(series), xAxis, yAxis, renderer);

    
    init(title, plot, true);
    
    
  }
  
  
  public Chart(String title, IndexedSeries series, String[] categories, boolean legend) {
    
    PiePlot plot = new PiePlot(IndexedSeries.asPieDataset(series, categories));
    plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1}"));
    plot.setInsets(new RectangleInsets(0, 5, 5, 10));
    
    init(title, plot, legend);
    
  }
  
  
  public Chart(String title, String labelCatAxis, IndexedSeries[] series, String[] categories, NumberFormat format, boolean isStacked, boolean isVertical) {

    
    CategoryAxis categoryAxis = new CategoryAxis(labelCatAxis);
    NumberAxis valueAxis = new NumberAxis();
    valueAxis.setNumberFormatOverride(format);

    BarRenderer renderer;
    if (isStacked) {
      renderer = new StackedBarRenderer();
    } else {
      renderer = new BarRenderer();
    }
    
    
    renderer.setSeriesPaint(0, Color.BLUE);
    renderer.setSeriesPaint(1, Color.RED);
    
    
    CategoryPlot plot = new CategoryPlot(IndexedSeries.asCategoryDataset(series, categories), categoryAxis, valueAxis, renderer);
    plot.setOrientation(!isVertical ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL);

    
    init(title, plot, true);
    
    
  }

} 

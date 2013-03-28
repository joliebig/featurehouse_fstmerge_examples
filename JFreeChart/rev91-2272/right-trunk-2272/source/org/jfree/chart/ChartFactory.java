

package org.jfree.chart;

import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryAxis3D;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.Timeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.WaferMapPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.renderer.WaferMapRenderer;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.category.WaterfallBarRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.GradientXYBarPainter;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.WindItemRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYBoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.util.Layer;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.chart.util.TableOrder;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.pie.DefaultPieDataset;
import org.jfree.data.pie.PieDataset;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.WindDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;


public abstract class ChartFactory {

    
    private static ChartTheme currentTheme = new StandardChartTheme("JFree");

    
    public static ChartTheme getChartTheme() {
        return currentTheme;
    }

    
    public static void setChartTheme(ChartTheme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Null 'theme' argument.");
        }
        currentTheme = theme;

        
        
        if (theme instanceof StandardChartTheme) {
            StandardChartTheme sct = (StandardChartTheme) theme;
            if (sct.getName().equals("Legacy")) {
                BarRenderer.setDefaultBarPainter(new StandardBarPainter());
                XYBarRenderer.setDefaultBarPainter(new StandardXYBarPainter());
            }
            else {
                BarRenderer.setDefaultBarPainter(new GradientBarPainter());
                XYBarRenderer.setDefaultBarPainter(new GradientXYBarPainter());
            }
        }
    }

    
    public static JFreeChart createPieChart(String title, PieDataset dataset,
            boolean legend) {
        return createPieChart(title, dataset, legend, Locale.getDefault());
    }

    
    public static JFreeChart createPieChart(String title, PieDataset dataset,
            boolean legend, Locale locale) {

        PiePlot plot = new PiePlot(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(locale));
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        plot.setToolTipGenerator(new StandardPieToolTipGenerator(locale));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createPieChart(String title, PieDataset dataset,
            PieDataset previousDataset, int percentDiffForMaxScale,
            boolean greenForIncrease, boolean legend, Locale locale,
            boolean subTitle, boolean showDifference) {

        PiePlot plot = new PiePlot(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(locale));
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        plot.setToolTipGenerator(new StandardPieToolTipGenerator(locale));

        List keys = dataset.getKeys();
        DefaultPieDataset series = null;
        if (showDifference) {
            series = new DefaultPieDataset();
        }

        double colorPerPercent = 255.0 / percentDiffForMaxScale;
        for (Iterator it = keys.iterator(); it.hasNext();) {
            Comparable key = (Comparable) it.next();
            Number newValue = dataset.getValue(key);
            Number oldValue = previousDataset.getValue(key);

            if (oldValue == null) {
                if (greenForIncrease) {
                    plot.setSectionPaint(key, Color.green);
                }
                else {
                    plot.setSectionPaint(key, Color.red);
                }
                if (showDifference) {
                    series.setValue(key + " (+100%)", newValue);
                }
            }
            else {
                double percentChange = (newValue.doubleValue()
                        / oldValue.doubleValue() - 1.0) * 100.0;
                double shade
                    = (Math.abs(percentChange) >= percentDiffForMaxScale ? 255
                    : Math.abs(percentChange) * colorPerPercent);
                if (greenForIncrease
                        && newValue.doubleValue() > oldValue.doubleValue()
                        || !greenForIncrease && newValue.doubleValue()
                        < oldValue.doubleValue()) {
                    plot.setSectionPaint(key, new Color(0, (int) shade, 0));
                }
                else {
                    plot.setSectionPaint(key, new Color((int) shade, 0, 0));
                }
                if (showDifference) {
                    series.setValue(key + " (" + (percentChange >= 0 ? "+" : "")
                            + NumberFormat.getPercentInstance().format(
                            percentChange / 100.0) + ")", newValue);
                }
            }
        }

        if (showDifference) {
            plot.setDataset(series);
        }

        JFreeChart chart =  new JFreeChart(title,
                JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        if (subTitle) {
            TextTitle subtitle = null;
            subtitle = new TextTitle("Bright " + (greenForIncrease ? "red"
                    : "green") + "=change >=-" + percentDiffForMaxScale
                    + "%, Bright " + (!greenForIncrease ? "red" : "green")
                    + "=change >=+" + percentDiffForMaxScale + "%",
                    new Font("Tahoma", Font.PLAIN, 10));
            chart.addSubtitle(subtitle);
        }
        currentTheme.apply(chart);
        return chart;
    }

    
    public static JFreeChart createPieChart(String title, PieDataset dataset,
            PieDataset previousDataset, int percentDiffForMaxScale,
            boolean greenForIncrease, boolean legend, boolean subTitle,
            boolean showDifference) {

        PiePlot plot = new PiePlot(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator());
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        plot.setToolTipGenerator(new StandardPieToolTipGenerator());
        
        List keys = dataset.getKeys();
        DefaultPieDataset series = null;
        if (showDifference) {
            series = new DefaultPieDataset();
        }

        double colorPerPercent = 255.0 / percentDiffForMaxScale;
        for (Iterator it = keys.iterator(); it.hasNext();) {
            Comparable key = (Comparable) it.next();
            Number newValue = dataset.getValue(key);
            Number oldValue = previousDataset.getValue(key);

            if (oldValue == null) {
                if (greenForIncrease) {
                    plot.setSectionPaint(key, Color.green);
                }
                else {
                    plot.setSectionPaint(key, Color.red);
                }
                if (showDifference) {
                    series.setValue(key + " (+100%)", newValue);
                }
            }
            else {
                double percentChange = (newValue.doubleValue()
                        / oldValue.doubleValue() - 1.0) * 100.0;
                double shade
                    = (Math.abs(percentChange) >= percentDiffForMaxScale ? 255
                    : Math.abs(percentChange) * colorPerPercent);
                if (greenForIncrease
                        && newValue.doubleValue() > oldValue.doubleValue()
                        || !greenForIncrease && newValue.doubleValue()
                        < oldValue.doubleValue()) {
                    plot.setSectionPaint(key, new Color(0, (int) shade, 0));
                }
                else {
                    plot.setSectionPaint(key, new Color((int) shade, 0, 0));
                }
                if (showDifference) {
                    series.setValue(key + " (" + (percentChange >= 0 ? "+" : "")
                            + NumberFormat.getPercentInstance().format(
                            percentChange / 100.0) + ")", newValue);
                }
            }
        }

        if (showDifference) {
            plot.setDataset(series);
        }

        JFreeChart chart =  new JFreeChart(title,
                JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        if (subTitle) {
            TextTitle subtitle = null;
            subtitle = new TextTitle("Bright " + (greenForIncrease ? "red"
                    : "green") + "=change >=-" + percentDiffForMaxScale
                    + "%, Bright " + (!greenForIncrease ? "red" : "green")
                    + "=change >=+" + percentDiffForMaxScale + "%",
                    new Font("Tahoma", Font.PLAIN, 10));
            chart.addSubtitle(subtitle);
        }
        currentTheme.apply(chart);
        return chart;
    }

    
    public static JFreeChart createRingChart(String title, PieDataset dataset,
            boolean legend) {

        return createRingChart(title, dataset, legend, Locale.getDefault());

    }

    
    public static JFreeChart createRingChart(String title, PieDataset dataset,
            boolean legend, Locale locale) {

        RingPlot plot = new RingPlot(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(locale));
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        plot.setToolTipGenerator(new StandardPieToolTipGenerator(locale));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    
    public static JFreeChart createMultiplePieChart(String title,
            CategoryDataset dataset, TableOrder order, boolean legend) {

        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        MultiplePiePlot plot = new MultiplePiePlot(dataset);
        plot.setDataExtractOrder(order);
        plot.setBackgroundPaint(null);
        plot.setOutlineStroke(null);
        PieToolTipGenerator tooltipGenerator
                = new StandardPieToolTipGenerator();
        PiePlot pp = (PiePlot) plot.getPieChart().getPlot();
        pp.setToolTipGenerator(tooltipGenerator);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createPieChart3D(String title, PieDataset dataset,
            boolean legend, Locale locale) {

        PiePlot3D plot = new PiePlot3D(dataset);
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        plot.setToolTipGenerator(new StandardPieToolTipGenerator(locale));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createPieChart3D(String title, PieDataset dataset,
            boolean legend) {

        PiePlot3D plot = new PiePlot3D(dataset);
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        plot.setToolTipGenerator(new StandardPieToolTipGenerator());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createMultiplePieChart3D(String title,
            CategoryDataset dataset, TableOrder order, boolean legend) {

        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");
        }
        MultiplePiePlot plot = new MultiplePiePlot(dataset);
        plot.setDataExtractOrder(order);
        plot.setBackgroundPaint(null);
        plot.setOutlineStroke(null);

        JFreeChart pieChart = new JFreeChart(new PiePlot3D(null));
        TextTitle seriesTitle = new TextTitle("Series Title",
                new Font("Tahoma", Font.BOLD, 12));
        seriesTitle.setPosition(RectangleEdge.BOTTOM);
        pieChart.setTitle(seriesTitle);
        pieChart.removeLegend();
        pieChart.setBackgroundPaint(null);
        plot.setPieChart(pieChart);

        PieToolTipGenerator tooltipGenerator
                = new StandardPieToolTipGenerator();
        PiePlot pp = (PiePlot) plot.getPieChart().getPlot();
        pp.setToolTipGenerator(tooltipGenerator);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createBarChart(String title,
            String categoryAxisLabel, String valueAxisLabel,
            CategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        BarRenderer renderer = new BarRenderer();
        ItemLabelPosition position1 = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        renderer.setBasePositiveItemLabelPosition(position1);
        ItemLabelPosition position2 = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        renderer.setBaseNegativeItemLabelPosition(position2);
        renderer.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator());
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createStackedBarChart(String title,
            String domainAxisLabel, String rangeAxisLabel,
            CategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis(domainAxisLabel);
        ValueAxis valueAxis = new NumberAxis(rangeAxisLabel);
        StackedBarRenderer renderer = new StackedBarRenderer();
        renderer.setBaseToolTipGenerator(
                    new StandardCategoryToolTipGenerator());
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createBarChart3D(String title,
            String categoryAxisLabel, String valueAxisLabel,
            CategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis3D(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis3D(valueAxisLabel);
        BarRenderer3D renderer = new BarRenderer3D();
        renderer.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator());
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        plot.setForegroundAlpha(0.75f);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createStackedBarChart3D(String title,
            String categoryAxisLabel, String valueAxisLabel,
            CategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis3D(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis3D(valueAxisLabel);
        CategoryItemRenderer renderer = new StackedBarRenderer3D();
        renderer.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator());
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createAreaChart(String title,
            String categoryAxisLabel, String valueAxisLabel,
            CategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        categoryAxis.setCategoryMargin(0.0);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        AreaRenderer renderer = new AreaRenderer();
        renderer.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator());
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createStackedAreaChart(String title,
            String categoryAxisLabel, String valueAxisLabel,
            CategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        categoryAxis.setCategoryMargin(0.0);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        StackedAreaRenderer renderer = new StackedAreaRenderer();
        renderer.setBaseToolTipGenerator(
                    new StandardCategoryToolTipGenerator());
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createLineChart(String title,
            String categoryAxisLabel, String valueAxisLabel,
            CategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, false);
        renderer.setBaseToolTipGenerator(
                    new StandardCategoryToolTipGenerator());
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createLineChart3D(String title,
            String categoryAxisLabel, String valueAxisLabel,
            CategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis3D(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis3D(valueAxisLabel);

        LineRenderer3D renderer = new LineRenderer3D();
        renderer.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator());
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createGanttChart(String title,
            String categoryAxisLabel, String dateAxisLabel,
            IntervalCategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        DateAxis dateAxis = new DateAxis(dateAxisLabel);
        CategoryItemRenderer renderer = new GanttRenderer();
        renderer.setBaseToolTipGenerator(
                    new IntervalCategoryToolTipGenerator(
                    "{3} - {4}", DateFormat.getDateInstance()));
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, dateAxis,
                renderer);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createWaterfallChart(String title,
            String categoryAxisLabel, String valueAxisLabel,
            CategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        categoryAxis.setCategoryMargin(0.0);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        WaterfallBarRenderer renderer = new WaterfallBarRenderer();
        ItemLabelPosition position = new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER,
                TextAnchor.CENTER, 0.0);
        renderer.setBasePositiveItemLabelPosition(position);
        renderer.setBaseNegativeItemLabelPosition(position);
        StandardCategoryToolTipGenerator generator
                = new StandardCategoryToolTipGenerator();
        renderer.setBaseToolTipGenerator(generator);
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        plot.clearRangeMarkers();
        Marker baseline = new ValueMarker(0.0);
        baseline.setPaint(Color.black);
        plot.addRangeMarker(baseline, Layer.FOREGROUND);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createPolarChart(String title, XYDataset dataset,
            boolean legend) {

        PolarPlot plot = new PolarPlot();
        plot.setDataset(dataset);
        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setTickMarksVisible(false);
        rangeAxis.setTickLabelInsets(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        plot.setAxis(rangeAxis);
        plot.setRenderer(new DefaultPolarItemRenderer());
        JFreeChart chart = new JFreeChart(
                title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createScatterPlot(String title, String xAxisLabel,
            String yAxisLabel, XYDataset dataset, boolean legend) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createXYBarChart(String title, String xAxisLabel,
            boolean dateAxis, String yAxisLabel, IntervalXYDataset dataset,
            boolean legend) {

        ValueAxis domainAxis = null;
        if (dateAxis) {
            domainAxis = new DateAxis(xAxisLabel);
        }
        else {
            NumberAxis axis = new NumberAxis(xAxisLabel);
            axis.setAutoRangeIncludesZero(false);
            domainAxis = axis;
        }
        ValueAxis valueAxis = new NumberAxis(yAxisLabel);

        XYBarRenderer renderer = new XYBarRenderer();
        XYToolTipGenerator tt;
        if (dateAxis) {
            tt = StandardXYToolTipGenerator.getTimeSeriesInstance();
        }
        else {
            tt = new StandardXYToolTipGenerator();
        }
        renderer.setBaseToolTipGenerator(tt);

        XYPlot plot = new XYPlot(dataset, domainAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createXYAreaChart(String title, String xAxisLabel,
            String yAxisLabel, XYDataset dataset, boolean legend) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setForegroundAlpha(0.5f);
        XYAreaRenderer renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createStackedXYAreaChart(String title,
            String xAxisLabel, String yAxisLabel, TableXYDataset dataset,
            boolean legend) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        renderer.setOutline(true);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setRangeAxis(yAxis);  
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createXYLineChart(String title, String xAxisLabel,
            String yAxisLabel, XYDataset dataset, boolean legend) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createXYStepChart(String title, String xAxisLabel,
            String yAxisLabel, XYDataset dataset, boolean legend) {

        DateAxis xAxis = new DateAxis(xAxisLabel);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        XYItemRenderer renderer = new XYStepRenderer();
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setRenderer(renderer);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createXYStepAreaChart(String title, 
            String xAxisLabel, String yAxisLabel, XYDataset dataset,
            boolean legend) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYItemRenderer renderer = new XYStepAreaRenderer(
                XYStepAreaRenderer.AREA_AND_SHAPES);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setRenderer(renderer);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    
    public static JFreeChart createTimeSeriesChart(String title,
            String timeAxisLabel, String valueAxisLabel, XYDataset dataset,
            boolean legend) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        timeAxis.setLowerMargin(0.02);  
        timeAxis.setUpperMargin(0.02);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);  
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);
        XYToolTipGenerator toolTipGenerator = null;
        toolTipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,
                false);
        renderer.setBaseToolTipGenerator(toolTipGenerator);
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createCandlestickChart(String title,
            String timeAxisLabel, String valueAxisLabel, OHLCDataset dataset,
            boolean legend) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);
        plot.setRenderer(new CandlestickRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createHighLowChart(String title,
            String timeAxisLabel, String valueAxisLabel, OHLCDataset dataset,
            boolean legend) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        HighLowRenderer renderer = new HighLowRenderer();
        renderer.setBaseToolTipGenerator(new HighLowItemLabelGenerator());
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createHighLowChart(String title, 
            String timeAxisLabel, String valueAxisLabel,
            OHLCDataset dataset, Timeline timeline, boolean legend) {

        DateAxis timeAxis = new DateAxis(timeAxisLabel);
        timeAxis.setTimeline(timeline);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        HighLowRenderer renderer = new HighLowRenderer();
        renderer.setBaseToolTipGenerator(new HighLowItemLabelGenerator());
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createBubbleChart(String title, String xAxisLabel,
            String yAxisLabel, XYZDataset dataset, boolean legend) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        XYItemRenderer renderer = new XYBubbleRenderer(
                XYBubbleRenderer.SCALE_ON_RANGE_AXIS);
        renderer.setBaseToolTipGenerator(new StandardXYZToolTipGenerator());
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createHistogram(String title, String xAxisLabel,
            String yAxisLabel, IntervalXYDataset dataset, boolean legend) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        ValueAxis yAxis = new NumberAxis(yAxisLabel);
        XYItemRenderer renderer = new XYBarRenderer();
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createBoxAndWhiskerChart(String title,
            String categoryAxisLabel, String valueAxisLabel,
            BoxAndWhiskerCategoryDataset dataset, boolean legend) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);

        BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator());

        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    
    public static JFreeChart createBoxAndWhiskerChart(String title,
            String timeAxisLabel, String valueAxisLabel,
            BoxAndWhiskerXYDataset dataset, boolean legend) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);
        XYBoxAndWhiskerRenderer renderer = new XYBoxAndWhiskerRenderer(10.0);
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createWindPlot(String title,
            String xAxisLabel, String yAxisLabel, WindDataset dataset,
            boolean legend) {

        ValueAxis xAxis = new DateAxis(xAxisLabel);
        ValueAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setRange(-12.0, 12.0);

        WindItemRenderer renderer = new WindItemRenderer();
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;

    }

    
    public static JFreeChart createWaferMapChart(String title,
            WaferMapDataset dataset, boolean legend) {

        WaferMapPlot plot = new WaferMapPlot(dataset);
        WaferMapRenderer renderer = new WaferMapRenderer();
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        currentTheme.apply(chart);
        return chart;
        
    }

}


package genj.chart;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;


public class XYSeries {

  
  private LinkedList points = new LinkedList();
  
  
  private String name;
  
  
  public XYSeries(String name) {
    this.name = name;
  }
    
  
  public int getSize() {
    return points.size();
  }
  
  
  private Point2D.Float getPointByIndex(int i) {
    return (Point2D.Float)points.get(i);
  }
    
  
  private Point2D.Float getPointForX(float x) {
    
    
    for (int i=0;i<points.size();i++) {
      
      Point2D.Float p = (Point2D.Float)points.get(i);
      
      
      if (p.getX()==x) 
        return p;
      
      
      if (p.getX()>x) {
        p = new Point2D.Float(x,0);
        points.add(i, p);
        return p;
      }
      
      
    }
    
    
    Point2D.Float p = new Point2D.Float(x,0);
    points.add(p);
    return p;
  }
  
  
  public void set(float x, float y) {
    
    Point2D.Float point = getPointForX(x);
    
    point.y = y;
    
  }
  
  
  public void inc(float x) {
    
    Point2D.Float point = getPointForX(x);
    
    point.y++;
    
  }
  
  
  public static XYSeries[] toArray(Collection c) {
    return (XYSeries[])c.toArray(new XYSeries[c.size()]);
  }
  
   static XYDataset toXYDataset(XYSeries[] series) {
    return new XYDatasetImpl(series);
  }
  
  
  private static class XYDatasetImpl extends AbstractXYDataset {
    
    
    private XYSeries[] series;
    
    
    private XYDatasetImpl(XYSeries[] series) {
      this.series = series;
    }
    
    
    public int getSeriesCount() {
      return series.length;
    }

    
    public String getSeriesName(int s) {
      return series[s].name;
    }

    
    public int getItemCount(int s) {
      return series[s].getSize();
    }

    
    public Number getX(int i, int item) {
      Point2D.Float p = series[i].getPointByIndex(item);
      return new Float(p.x);
    }

    
    public Number getY(int i, int item) {
      Point2D.Float p = series[i].getPointByIndex(item);
      return new Float(p.y);
    }

	@Override
	public Comparable getSeriesKey(int series) {
		
		return this.series[series].name;
	}

  } 
  
} 

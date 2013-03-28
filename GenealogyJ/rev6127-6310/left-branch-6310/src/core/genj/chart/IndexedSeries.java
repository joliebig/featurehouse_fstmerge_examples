
package genj.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.TableXYDataset;


public class IndexedSeries {

  
  private String name;
  
  
  private float[] values;
  
  
  private int start;
  
  
  public IndexedSeries(String name, IndexedSeries template) {
    this(name, template.start, template.values.length);
  }
  
  
  public IndexedSeries(String name, int size) {
    this(name,0,size);
  }
  
  
  public IndexedSeries(String name, int start, int size) {
    this.name = name;
    this.start = start;
    this.values = new float[size];
  }
  
  
  public void setName(String set) {
    name = set;
  }
  
  
  public String getName() {
    return name;
  }
  
  
  public float get(int i) {
    
    i = i-start;
    
    return values[i];
  }
  
  
  public void set(int i, float val) {
    
    i = i-start;
    
    if (i<0||i>=values.length)
      return;
    
    values[i] = val;
  }
  
  
  public void inc(int i) {
    
    i = i-start;
    
    if (i<0||i>=values.length)
      return;
    
    values[i]++;
  }
  
  
  public void dec(int i) {
    
    i = i-start;
    
    if (i<0||i>=values.length)
      return;
    
    values[i]--;
  }
  
  
  public static IndexedSeries[] toArray(Collection c) {
    return (IndexedSeries[])c.toArray(new IndexedSeries[c.size()]);
  }

  
   static PieDataset asPieDataset(IndexedSeries series, String[] categories) {
    return new PieDatasetImpl(series, categories);
  }
  
  
   static CategoryDataset asCategoryDataset(IndexedSeries[] series, String[] categories) {
    return new CategoryDatasetImpl(series, categories);
  }
  
  
   static TableXYDataset asTableXYDataset(IndexedSeries[] series) {
    return new TableXYDatasetImpl(series);
  }
  
  
  private static class PieDatasetImpl extends AbstractDataset implements PieDataset {
    
    
    private IndexedSeries series;
    
    private String[] categories;
    
    
    private PieDatasetImpl(IndexedSeries series, String[] categories) {
      this.series = series;
      this.categories = categories;
    }

    
    public Comparable getKey(int i) {
      return categories[i];
    }

    public int getIndex(Comparable key) {
      for (int i=0;i<categories.length;i++) {
        if (categories[i].equals(key))
          return i;
      }
      throw new IllegalArgumentException();
    }

    public List getKeys() {
      return Arrays.asList(categories);
    }

    public Number getValue(Comparable cat) {
      return getValue(getIndex(cat));
    }

    public int getItemCount() {
      return categories.length;
    }

    public Number getValue(int i) {
      return new Float(series.get(i));
    }
    
  } 
  
  
  private static class TableXYDatasetImpl extends AbstractXYDataset implements TableXYDataset {
    
    
    private IndexedSeries[] series;
    
    
    private int start, length;
    
    
    public TableXYDatasetImpl(IndexedSeries[] series) {
      
      this.series = series;
      
      if (series.length>0) {
        start = series[0].start;
        length = series[0].values.length;

        for (int i=1;i<series.length;i++) {
          if (series[i].start!=start||series[i].values.length!=length)
            throw new IllegalArgumentException("series can't be combined into table dataset");
        }
        
      }
    }
    
    
    public int getItemCount() {
      return length;
    }
    
    
    public int getSeriesCount() {
      return series.length;
    }

    
    public String getSeriesName(int s) {
      return series[s].name;
    }

    
    public int getItemCount(int s) {
      return length;
    }

    
    public Number getX(int s, int i) {
      return new Integer(start + i);
    }

    
    public Number getY(int s, int i) {
      return new Float(series[s].get(start+i));
    }

	@Override
	public Comparable getSeriesKey(int series) {
		
		
		  return this.series[series].name;
	}

  } 
  
  
  private static class CategoryDatasetImpl extends AbstractDataset implements CategoryDataset {
    
    
    private IndexedSeries[] series;
    
    
    private String[] categories;
    
    
    private CategoryDatasetImpl(IndexedSeries[] series, String[] categories) {
      this.series = series;
      this.categories = categories;
      
      for (int i=0;i<series.length;i++) {
        if (series[i].values.length!=categories.length)
          throw new IllegalArgumentException("series doesn't match categories");
      }
    }

    
    public Comparable getRowKey(int row) {
      return series[row].name;
    }

    
    public int getRowIndex(Comparable row) {
      for (int i=0; i<series.length; i++) 
        if (series[i].name.equals(row)) 
          return i;
      throw new IllegalArgumentException();
    }

    
    public List getRowKeys() {
      ArrayList result = new ArrayList();
      for (int i=0;i<series.length;i++)
        result.add(series[i].name);
      return result;
    }

    
    public Comparable getColumnKey(int col) {
      return categories[col];
    }

    
    public int getColumnIndex(Comparable col) {
      for (int i=0; i<categories.length; i++) 
        if (categories[i].equals(col)) return i;
      throw new IllegalArgumentException();
    }

    
    public List getColumnKeys() {
      return Arrays.asList(categories);
    }

    
    public Number getValue(Comparable row, Comparable col) {
      return getValue(getRowIndex(row), getColumnIndex(col));
    }

    
    public Number getValue(int row, int col) {
      return new Float(series[row].get(col));
    }
    
    
    public int getRowCount() {
      return series.length;
    }

    
    public int getColumnCount() {
      return categories.length;
    }
    
  } 

} 
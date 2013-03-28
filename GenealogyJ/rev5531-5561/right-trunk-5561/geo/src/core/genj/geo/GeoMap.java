
package genj.geo;

import genj.util.Origin;
import genj.util.Resources;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;

import org.geotools.shapefile.Shapefile;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.BasicFeature;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.feature.FeatureDataset;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;


public class GeoMap {
  
  private final static String 
    SUFFIX_SHP = ".shp",
    PROPERTIES = "geo.properties";
  
  
  private Origin origin;
  
  
  private Resources resources;
  
  
  private String name;
  
  
  private Color background = Color.WHITE;
  
  
   GeoMap(File fileOrDir) throws IOException {
    
    
    origin = Origin.create(fileOrDir.toURL());

    
    loadProperties();
    
    
  }
  
  
  private void loadProperties() {
    
    
    try {
      resources = new Resources(origin.open(PROPERTIES));
    } catch (IOException e) {
    }
    
    
    name = translate("name", origin.getName());
    try {
      background =  new Color(Integer.decode(translate("color.background", "")).intValue());
    } catch (Throwable t) {
      background = new Color(0xccffff);
    }
    
    
  }
  
  
  public String getKey() {
    return origin.getName();
  }
  
  
  private String translate(String key, String fallback) {
    
    if (resources==null)
      return fallback;
    
    
    String result = resources.getString(key+"."+Locale.getDefault().getLanguage().toLowerCase(), false);
    if (result==null) 
      result = resources.getString(key, false);
    return result!=null ? result : fallback;
  }
  
  
  public String getName() {
    return name;
  }
  
  
  public Color getBackground() {
    return background;
  }
 
  private final static Color[] PALETTE = {
    new Color(0xfbb3ad), new Color(0xb2cce2), new Color(0xccebc5), new Color(0xdecbe4), new Color(0xfed9a5), new Color(0xffffcc), new Color(0xe4d7bc), new Color(0xfddaec), new Color(0xf2f2f2)
  };
  
  
  void load(LayerManager manager) throws IOException {
    
    
    loadProperties();

    
    String[] shapes = origin.list();
    Arrays.sort(shapes);
    for (int i=0;i<shapes.length;i++) {
      
      
      String shape = shapes[i];
      if (!shape.endsWith(SUFFIX_SHP)) 
        continue;
      String name = shape.substring(0, shape.length()-SUFFIX_SHP.length());
      
      
      FeatureCollection fc = load(origin.open(shape));
      
      
      Layer layer = manager.addLayer(getName(), name, fc);
      
      
      if (Character.isDigit(name.charAt(0))) name = name.substring(1);
      String color = translate("color."+name, "");

      
      try {
        Color c = new Color(Integer.decode(color).intValue());
        BasicStyle style = layer.getBasicStyle();
        style.setFillColor(c);
        style.setAlpha(255);
        style.setLineColor(Layer.defaultLineColor(c));
      } catch (NumberFormatException nfe) {
        
        
        layer.removeStyle(layer.getBasicStyle());
        layer.addStyle(new BasicStyle() {
          public void paint(Feature feature, Graphics2D graphics2d, Viewport viewport) throws NoninvertibleTransformException {
            Color c = PALETTE[feature.getGeometry().getNumPoints()%PALETTE.length];
            setFillColor(c);
            setLineColor(Layer.defaultLineColor(c));
            super.paint(feature, graphics2d, viewport);
          }
        });
        
      }
      
      
    }

    
  }
  
  
  private FeatureCollection load(InputStream in) throws IOException {

    
    GeometryCollection gc;
    try {
      gc = new Shapefile(in).read(new GeometryFactory());
    } catch (Throwable t) {
      if (t instanceof IOException)
        throw (IOException)t;
      throw new IOException(t.getMessage());
    } finally {
      if (in!=null) in.close();
    }

    
    FeatureSchema schema = new FeatureSchema();
    schema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
    
    FeatureDataset result = new FeatureDataset(schema);
    
    for (int i = 0; i < gc.getNumGeometries(); i++) {
      Feature feature = new BasicFeature(schema);
      Geometry geo = gc.getGeometryN(i);
      feature.setGeometry(geo);
      result.add(feature);
    }
    
    return result;
    
  }
  
}

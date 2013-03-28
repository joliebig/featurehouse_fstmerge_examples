
package org.openscience.jmol.app.jmolpanel;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;


class JmolResourceHandler {

  private static JmolResourceHandler instance;
  private ResourceBundle stringsResourceBundle;
  private ResourceBundle generalResourceBundle;

  private JmolResourceHandler() {
    String language = "en";
    String country = "";
    String localeString = System.getProperty("user.language");
    if (localeString != null) {
      StringTokenizer st = new StringTokenizer(localeString, "_");
      if (st.hasMoreTokens()) {
        language = st.nextToken();
      }
      if (st.hasMoreTokens()) {
        country = st.nextToken();
      }
    }
    Locale locale = new Locale(language, country);
    stringsResourceBundle =
      ResourceBundle.getBundle("org.openscience.jmol.app.jmolpanel.Properties.Jmol", locale);

    try {
      String t = "/org/openscience/jmol/app/jmolpanel/Properties/Jmol-resources.properties";
      generalResourceBundle =
        new PropertyResourceBundle(getClass().getResourceAsStream(t));
    } catch (IOException ex) {
      throw new RuntimeException(ex.toString());
    }
  }

  static JmolResourceHandler getInstance() {
    if (instance == null) {
      instance = new JmolResourceHandler();
    }
    return instance;
  }

  static String getStringX(String key) {
    return getInstance().getString(key);
  }

  static ImageIcon getIconX(String key){ 
    return getInstance().getIcon(key);
  }

  private synchronized ImageIcon getIcon(String key) {

    String resourceName = null;
    try {
      resourceName = getString(key);
    } catch (MissingResourceException e) {
    }

    if (resourceName != null) {
      String imageName = "org/openscience/jmol/app/images/" + resourceName;
      URL imageUrl = this.getClass().getClassLoader().getResource(imageName);
      if (imageUrl != null) {
        return new ImageIcon(imageUrl);
      }
      
    }
    return null;
  }

  private synchronized String getString(String key) {

    String result = null;
    try {
      result = stringsResourceBundle.getString(key);
    } catch (MissingResourceException e) {
    }
    if (result == null) {
      try {
        result = generalResourceBundle.getString(key);
      } catch (MissingResourceException e) {
      }
    }
    return result != null ? result : key;
  }

  
  

}


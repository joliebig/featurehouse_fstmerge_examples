

import java.applet.*;

public class SystemGetProperty extends Applet {

  public String systemGetProperty(String propertyName) {
    return System.getProperty(propertyName);
  }
}



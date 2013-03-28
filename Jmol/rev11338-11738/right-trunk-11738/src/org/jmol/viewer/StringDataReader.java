package org.jmol.viewer;

import java.io.StringReader;

public class StringDataReader extends DataReader {

  public StringDataReader(String data) {
    super(new StringReader(data));
  }

}

package org.jmol.api;

import org.jmol.viewer.Viewer;

public interface JmolMovieCreatorInterface {

  
  
  
  
  abstract public String createMovie(Viewer viewer, String[] files, int width,
                                     int height, int fps, String fileName);
}



package edu.rice.cs.drjava.model;

import javax.swing.text.Highlighter;


public interface IHighlighted {
  
  public static class PainterTag {
    public final Highlighter.HighlightPainter painter;
    public final Object tag;
    public PainterTag(Highlighter.HighlightPainter p, Object t) { painter = p; tag = t; }
  }
  
  
  public PainterTag addPainter(Highlighter.HighlightPainter p);
  
  
  public void removePainter(PainterTag t);
  
  
  public void movePainterToFront(PainterTag t);  
  
  
  public void clearPainters();
}

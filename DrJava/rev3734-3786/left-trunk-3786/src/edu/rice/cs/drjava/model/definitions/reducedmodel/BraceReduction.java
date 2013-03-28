

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import java.util.Vector;



public interface BraceReduction {
  
  public int absOffset();

  
  ReducedToken currentToken();

  
  ReducedModelState getStateAtCurrent();

  
  public void insertChar(char ch);

  
  public void move( int count );

  
  public void delete( int count );


  
  public int balanceForward();

  
  public int balanceBackward();

  
  public IndentInfo getIndentInformation();

  
  public int getDistToPreviousNewline(int relativeLoc);

  
  public int getDistToNextNewline();

  
  public String simpleString();

  
  public Vector<HighlightStatus> getHighlightStatus(int start, int length);

  
  public ReducedModelState moveWalkerGetState(int relLocation);

  
  public void resetLocation();
}

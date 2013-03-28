

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import java.util.ArrayList;



public interface BraceReduction {
  
  public int absOffset();

  
  public ReducedToken currentToken();

  
  public ReducedModelState getStateAtCurrent();

  
  public void insertChar(char ch);

  
  public void move(int count );

  
  public void delete(int count );


  
  public int balanceForward();

  
  public int balanceBackward();




  
  public int getDistToStart(int relativeLoc);

  
  public int getDistToNextNewline();

  
  public String simpleString();

  
  public ArrayList<HighlightStatus> getHighlightStatus(int start, int length);

  
  public ReducedModelState moveWalkerGetState(int relLocation);

  
  public void resetLocation();
}

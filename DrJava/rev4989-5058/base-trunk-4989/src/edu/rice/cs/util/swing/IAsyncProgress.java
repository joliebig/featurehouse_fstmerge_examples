

package edu.rice.cs.util.swing;

public interface IAsyncProgress {
  public void close();
  public int  getMaximum();
  public int  getMillisToDecideToPopup();
  public int  getMillisToPopup();
  public int  getMinimum();
  public String  getNote();
  public boolean  isCanceled();
  public void  setMaximum(int m);
  public void  setMinimum(int m);
  public void  setNote(String note);
  public void  setProgress(int nv);
}
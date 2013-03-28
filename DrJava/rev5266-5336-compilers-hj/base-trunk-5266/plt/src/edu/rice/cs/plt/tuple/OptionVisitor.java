

package edu.rice.cs.plt.tuple;


public interface OptionVisitor<T, Ret> {
  public Ret forSome(T value);
  public Ret forNone();
}

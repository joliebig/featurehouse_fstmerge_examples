

package edu.rice.cs.plt.tuple;


public final class Null<T> extends Option<T> {
  
  
  private Null() {}
  
  
  public static final Null<Void> INSTANCE = new Null<Void>();
  
  
  public <Ret> Ret apply(OptionVisitor<? super T, ? extends Ret> visitor) {
    return visitor.forNone();
  }
  
  public boolean isSome() { return false; }
  
  public T unwrap() { throw new OptionUnwrapException(); }
  
  public T unwrap(T forNone) { return forNone; }
  
  
  public String toString() { return "()"; }
  
  
  public boolean equals(Object o) { return this == o; }
  
  
  protected int generateHashCode() { return System.identityHashCode(this); }
  
  
  @SuppressWarnings("unchecked")
  public static <T> Null<T> make() { return (Null<T>) INSTANCE; }
}  

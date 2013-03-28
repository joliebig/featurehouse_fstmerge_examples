package edu.rice.cs.dynamicjava;

import edu.rice.cs.dynamicjava.symbol.TypeSystem;
import edu.rice.cs.dynamicjava.symbol.ExtendedTypeSystem;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.lambda.LazyThunk;
import edu.rice.cs.plt.lambda.Thunk;

public class Options {
  
  public static final Options DEFAULT = new Options();
  
  private final Thunk<? extends TypeSystem> _tsFactory;
  
  
  protected Options() { _tsFactory = typeSystemFactory(); }
  
  
  protected Thunk<? extends TypeSystem> typeSystemFactory() {
    return LambdaUtil.valueLambda(new ExtendedTypeSystem(this));
  }
  
  public final TypeSystem typeSystem() { return _tsFactory.value(); }
  
  public boolean requireSemicolon() { return false; }
  
  public boolean requireVariableType() { return false; }
  
  public boolean enforceAllAccess() { return false; }
  
  public boolean enforcePrivateAccess() { return false; }
  
  public boolean prohibitBoxing() { return false; }
  
  public boolean prohibitUncheckedCasts() { return true; }
}

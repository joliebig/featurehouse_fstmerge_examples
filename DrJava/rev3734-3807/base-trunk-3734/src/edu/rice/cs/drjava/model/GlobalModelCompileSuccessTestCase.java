

package edu.rice.cs.drjava.model;

import  junit.framework.*;

import edu.rice.cs.drjava.model.compiler.*;


public abstract class GlobalModelCompileSuccessTestCase extends GlobalModelTestCase {

  protected static final String FOO_PACKAGE_AS_PART_OF_FIELD =
    "class DrJavaTestFoo { int cur_package = 5; }";

  protected static final String FOO2_EXTENDS_FOO_TEXT =
    "class DrJavaTestFoo2 extends DrJavaTestFoo {}";
  
  protected static final String FOO_NON_PUBLIC_CLASS_TEXT =
    "class DrJavaTestFoo {} class Foo{}";
  
  protected static final String FOO2_REFERENCES_NON_PUBLIC_CLASS_TEXT =
    "class DrJavaTestFoo2 extends Foo{}";
  
  protected static final String FOO_WITH_ASSERT =
    "class DrJavaTestFoo { void foo() { assert true; } }";

  protected static final String FOO_WITH_GENERICS =
    "class DrJavaTestFooGenerics<T> {}";

  
  public void runBare() throws Throwable {
    CompilerInterface[] compilers = CompilerRegistry.ONLY.getAvailableCompilers();
    for (int i = 0; i < compilers.length; i++) {
      
      setUp();
      _model.getCompilerModel().setActiveCompiler(compilers[i]);

      try { runTest(); }
      finally { tearDown(); }
    }
  }

  protected String _name() {
    return "compiler=" + _model.getCompilerModel().getActiveCompiler().getName() + ": ";
  }

  
  protected boolean _isGenericCompiler() { return ! CompilerProxy.VERSION.equals("1.4"); }
}



package edu.rice.cs.drjava.model;

import edu.rice.cs.plt.reflect.JavaVersion;


public abstract class GlobalModelCompileSuccessTestCase extends GlobalModelTestCase {

  protected static final String FOO_PACKAGE_AS_PART_OF_FIELD = "class DrJavaTestFoo { int cur_package = 5; }";
  protected static final String FOO2_EXTENDS_FOO_TEXT = "class DrJavaTestFoo2 extends DrJavaTestFoo {}";
  protected static final String FOO_NON_PUBLIC_CLASS_TEXT = "class DrJavaTestFoo {} class Foo{}";
  protected static final String FOO2_REFERENCES_NON_PUBLIC_CLASS_TEXT = "class DrJavaTestFoo2 extends Foo{}";
  protected static final String FOO_WITH_ASSERT = "class DrJavaTestFoo { void foo() { assert true; } }";
  protected static final String FOO_WITH_GENERICS = "class DrJavaTestFooGenerics<T> {}";
















  protected String _name() { return "compiler=" + _model.getCompilerModel().getActiveCompiler().getName() + ": "; }

  
  protected boolean _isGenericCompiler() { return JavaVersion.CURRENT.supports(JavaVersion.JAVA_5); }
}

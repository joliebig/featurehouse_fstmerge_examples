

package koala.dynamicjava.util;

import koala.dynamicjava.interpreter.error.WrongVersionException;

import java.lang.reflect.*;


public class TigerUtilitiesTest extends DynamicJavaTestCase {
  
  
  public void setUp() {
    setTigerEnabled(true);    
  }
  
  public void tearDown() {
    TigerUtilities.resetVersion();    
  }
  
  
  public void testResetVersion() {
    TigerUtilities.resetVersion();
    assertEquals("Did not reset runtime version correctly",TigerUtilities.VERSION>=1.5,TigerUtilities.isTigerEnabled());
  }
   
  
  public void testIsVarArgs() {
    try {    
      Method m1 = java.io.PrintStream.class.getMethod("printf", new Class<?>[]{String.class, Object[].class});
      Method m2 = java.io.PrintStream.class.getMethod("println",new Class<?>[]{ });
      assertEquals("The method should have variable arguments",TigerUtilities.isVarArgs(m1),true);
      assertEquals("The method should not have variable arguments",TigerUtilities.isVarArgs(m2),false);   
      
      
      TigerUtilities.setTigerEnabled(false);
      assertEquals("Tiger features are disabled, isVarArgs should return false",TigerUtilities.isVarArgs(m1),false);
      assertEquals("Tiger features are disabled, isVarArgs should return false",TigerUtilities.isVarArgs(m2),false);
      TigerUtilities.setTigerEnabled(true);
    }
    catch(NoSuchMethodException e) {
      throw new RuntimeException(e.toString());
    }
    catch(WrongVersionException e) {
      throw new RuntimeException("Should not have thrown a Wrong Version Exception");
    }
  }
  
  
  public void testCorrespondingBoxingType() {
    assertEquals("Should have returned boxed Boolean class",TigerUtilities.correspondingBoxingType(boolean.class),Boolean.class); 
    assertEquals("Should have returned boxed Byte class",TigerUtilities.correspondingBoxingType(byte.class),Byte.class);
    assertEquals("Should have returned boxed Character class",TigerUtilities.correspondingBoxingType(char.class),Character.class);
    assertEquals("Should have returned boxed Short class",TigerUtilities.correspondingBoxingType(short.class),Short.class);
    assertEquals("Should have returned boxed Long class",TigerUtilities.correspondingBoxingType(long.class),Long.class);
    assertEquals("Should have returned boxed Integer class",TigerUtilities.correspondingBoxingType(int.class),Integer.class);
    assertEquals("Should have returned boxed Float class",TigerUtilities.correspondingBoxingType(float.class),Float.class);
    assertEquals("Should have returned boxed Double class",TigerUtilities.correspondingBoxingType(double.class),Double.class);
    
    
    
  }
  
  
  public void testCorrespondingPrimType() {
    assertEquals("Should have returned primitive boolean class",TigerUtilities.correspondingPrimType(Boolean.class),boolean.class);
    assertEquals("Should have returned primitive byte class",TigerUtilities.correspondingPrimType(Byte.class),byte.class);
    assertEquals("Should have returned primitive char class",TigerUtilities.correspondingPrimType(Character.class),char.class);
    assertEquals("Should have returned primitive short class",TigerUtilities.correspondingPrimType(Short.class),short.class);
    assertEquals("Should have returned primitive long class",TigerUtilities.correspondingPrimType(Long.class),long.class);
    assertEquals("Should have returned primitive int class",TigerUtilities.correspondingPrimType(Integer.class),int.class);
    assertEquals("Should have returned primitive float class",TigerUtilities.correspondingPrimType(Float.class),float.class);
    assertEquals("Should have returned primitive double class",TigerUtilities.correspondingPrimType(Double.class),double.class);
  }
  
  
  public void testIsBoxingType() {
    assertEquals("Should be a boxing type",TigerUtilities.isBoxingType(Boolean.class),true);
    assertEquals("Should be a boxing type",TigerUtilities.isBoxingType(Byte.class),true);
    assertEquals("Should be a boxing type",TigerUtilities.isBoxingType(Character.class),true);
    assertEquals("Should be a boxing type",TigerUtilities.isBoxingType(Short.class),true);
    assertEquals("Should be a boxing type",TigerUtilities.isBoxingType(Long.class),true);
    assertEquals("Should be a boxing type",TigerUtilities.isBoxingType(Integer.class),true);
    assertEquals("Should be a boxing type",TigerUtilities.isBoxingType(Float.class),true);
    assertEquals("Should be a boxing type",TigerUtilities.isBoxingType(Double.class),true);
    assertEquals("Should not be a boxing type",TigerUtilities.isBoxingType(String.class),false);
  }
   
  
  public void testIsIntegralType() {
   assertEquals("Should be an integral type",TigerUtilities.isIntegralType(int.class),true); 
   assertEquals("Should be an integral type",TigerUtilities.isIntegralType(Integer.class),true);
   assertEquals("Should be an integral type",TigerUtilities.isIntegralType(short.class),true);
   assertEquals("Should be an integral type",TigerUtilities.isIntegralType(Short.class),true);
   assertEquals("Should be an integral type",TigerUtilities.isIntegralType(long.class),true);
   assertEquals("Should be an integral type",TigerUtilities.isIntegralType(Long.class),true);
   assertEquals("Should be an integral type",TigerUtilities.isIntegralType(byte.class),true);
   assertEquals("Should be an integral type",TigerUtilities.isIntegralType(Byte.class),true);
   assertEquals("Should be an integral type",TigerUtilities.isIntegralType(char.class),true);
   assertEquals("Should be an integral type",TigerUtilities.isIntegralType(Character.class),true);
   assertEquals("Should not be an integral type",TigerUtilities.isIntegralType(double.class),false);
   assertEquals("Should not be an integral type",TigerUtilities.isIntegralType(Double.class),false);
   assertEquals("Should not be an integral type",TigerUtilities.isIntegralType(float.class),false);
   assertEquals("Should not be an integral type",TigerUtilities.isIntegralType(Float.class),false);
   assertEquals("Should not be an integral type",TigerUtilities.isIntegralType(boolean.class),false);
   assertEquals("Should not be an integral type",TigerUtilities.isIntegralType(Boolean.class),false);
   assertEquals("Should not be an integral type",TigerUtilities.isIntegralType(String.class),false);
  }
  
  
  public void testBoxesTo() {
    assertEquals("Should be able to box primitive to reference type",TigerUtilities.boxesTo(boolean.class,Boolean.class),true);
    
    assertEquals("Should be able to box primitive to reference type",TigerUtilities.boxesTo(int.class,Integer.class),true);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(int.class,Long.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(int.class,Double.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(int.class,Float.class),false);
    
    assertEquals("Should be able to box primitive to reference type",TigerUtilities.boxesTo(double.class,Double.class),true);
    
    assertEquals("Should be able to box primitive to reference type",TigerUtilities.boxesTo(float.class,Float.class),true);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(float.class,Double.class),false);
    
    assertEquals("Should be able to box primitive to reference type",TigerUtilities.boxesTo(long.class,Long.class),true);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(long.class,Double.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(long.class,Float.class),false);
    
    assertEquals("Should be able to box primitive to reference type",TigerUtilities.boxesTo(byte.class,Byte.class),true);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(byte.class,Integer.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(byte.class,Short.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(byte.class,Long.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(byte.class,Float.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(byte.class,Double.class),false);
    
    assertEquals("Should be able to box primitive to reference type",TigerUtilities.boxesTo(char.class,Character.class),true);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(char.class,Integer.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(char.class,Long.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(char.class,Double.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(char.class,Float.class),false);
    
    assertEquals("Should be able to box primitive to reference type",TigerUtilities.boxesTo(short.class,Short.class),true);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(short.class,Integer.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(short.class,Long.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(short.class,Double.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(short.class,Float.class),false);
    
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(int.class,Byte.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(int.class,Character.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(int.class,Short.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(int.class,Boolean.class),false);
    
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(double.class,Float.class),false);
    assertEquals("Should not be able to box primitive to reference type",TigerUtilities.boxesTo(double.class,Integer.class),false);
  }
}


package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.DrJavaTestCase;


public class AbstractDJDocumentTest extends DrJavaTestCase {
  public void testIsNum() {
    assertTrue(AbstractDJDocument._isNum("0"));
    assertTrue(AbstractDJDocument._isNum("1"));
    assertTrue(AbstractDJDocument._isNum("12"));
    
    assertTrue(AbstractDJDocument._isNum("0l"));
    assertTrue(AbstractDJDocument._isNum("1l"));
    assertTrue(AbstractDJDocument._isNum("12l"));
    assertTrue(AbstractDJDocument._isNum("0L"));
    assertTrue(AbstractDJDocument._isNum("1L"));
    assertTrue(AbstractDJDocument._isNum("12L"));
    
    assertTrue(AbstractDJDocument._isNum("00"));
    assertTrue(AbstractDJDocument._isNum("01"));
    assertTrue(AbstractDJDocument._isNum("012"));
    assertTrue(AbstractDJDocument._isNum("00l"));
    assertTrue(AbstractDJDocument._isNum("01l"));
    assertTrue(AbstractDJDocument._isNum("012l"));
    assertTrue(AbstractDJDocument._isNum("00L"));
    assertTrue(AbstractDJDocument._isNum("01L"));
    assertTrue(AbstractDJDocument._isNum("012L"));
    
    assertTrue(AbstractDJDocument._isNum("0X0"));
    assertTrue(AbstractDJDocument._isNum("0X1"));
    assertTrue(AbstractDJDocument._isNum("0X12"));
    assertTrue(AbstractDJDocument._isNum("0Xff"));
    assertTrue(AbstractDJDocument._isNum("0XFF"));
    assertTrue(AbstractDJDocument._isNum("0XFFFFFFFF"));
    assertFalse(AbstractDJDocument._isNum("0XFFFFFFFFF"));
    assertFalse(AbstractDJDocument._isNum("0Xg"));
    assertTrue(AbstractDJDocument._isNum("0X0l"));
    assertTrue(AbstractDJDocument._isNum("0X1l"));
    assertTrue(AbstractDJDocument._isNum("0X12l"));
    assertTrue(AbstractDJDocument._isNum("0Xffl"));
    assertTrue(AbstractDJDocument._isNum("0XFFl"));
    assertTrue(AbstractDJDocument._isNum("0XFFFFFFFFFFFFFFFFl"));
    assertFalse(AbstractDJDocument._isNum("0XFFFFFFFFFFFFFFFFFl"));
    assertFalse(AbstractDJDocument._isNum("0Xgl"));
    assertTrue(AbstractDJDocument._isNum("0X0L"));
    assertTrue(AbstractDJDocument._isNum("0X1L"));
    assertTrue(AbstractDJDocument._isNum("0X12L"));
    assertTrue(AbstractDJDocument._isNum("0XffL"));
    assertTrue(AbstractDJDocument._isNum("0XFFL"));
    assertFalse(AbstractDJDocument._isNum("0XgL"));
    assertTrue(AbstractDJDocument._isNum("0XFFFFFFFFFFFFFFFFL"));
    assertFalse(AbstractDJDocument._isNum("0XFFFFFFFFFFFFFFFFFL"));
    
    assertTrue(AbstractDJDocument._isNum("0x0"));
    assertTrue(AbstractDJDocument._isNum("0x1"));
    assertTrue(AbstractDJDocument._isNum("0x12"));
    assertTrue(AbstractDJDocument._isNum("0xff"));
    assertTrue(AbstractDJDocument._isNum("0xFF"));
    assertTrue(AbstractDJDocument._isNum("0xFFFFFFFF"));
    assertFalse(AbstractDJDocument._isNum("0xFFFFFFFFF"));
    assertFalse(AbstractDJDocument._isNum("0xg"));
    assertTrue(AbstractDJDocument._isNum("0x0l"));
    assertTrue(AbstractDJDocument._isNum("0x1l"));
    assertTrue(AbstractDJDocument._isNum("0x12l"));
    assertTrue(AbstractDJDocument._isNum("0xffl"));
    assertTrue(AbstractDJDocument._isNum("0xFFl"));
    assertTrue(AbstractDJDocument._isNum("0xFFFFFFFFFFFFFFFFl"));
    assertFalse(AbstractDJDocument._isNum("0xFFFFFFFFFFFFFFFFFl"));
    assertFalse(AbstractDJDocument._isNum("0xgl"));
    assertTrue(AbstractDJDocument._isNum("0x0L"));
    assertTrue(AbstractDJDocument._isNum("0x1L"));
    assertTrue(AbstractDJDocument._isNum("0x12L"));
    assertTrue(AbstractDJDocument._isNum("0xffL"));
    assertTrue(AbstractDJDocument._isNum("0xFFL"));
    assertFalse(AbstractDJDocument._isNum("0xgL"));
    assertTrue(AbstractDJDocument._isNum("0xFFFFFFFFFFFFFFFFL"));
    assertFalse(AbstractDJDocument._isNum("0xFFFFFFFFFFFFFFFFFL"));
    
    assertTrue(AbstractDJDocument._isNum("1.0"));
    assertTrue(AbstractDJDocument._isNum("12.0"));
    assertTrue(AbstractDJDocument._isNum("12.3"));
    assertTrue(AbstractDJDocument._isNum("12.34"));
    
    assertTrue(AbstractDJDocument._isNum("1.0f"));
    assertTrue(AbstractDJDocument._isNum("12.0f"));
    assertTrue(AbstractDJDocument._isNum("12.3f"));
    assertTrue(AbstractDJDocument._isNum("12.34f"));
    assertTrue(AbstractDJDocument._isNum("1.0F"));
    assertTrue(AbstractDJDocument._isNum("12.0F"));
    assertTrue(AbstractDJDocument._isNum("12.3F"));
    assertTrue(AbstractDJDocument._isNum("12.34F"));
    
    assertTrue(AbstractDJDocument._isNum("1.0d"));
    assertTrue(AbstractDJDocument._isNum("12.0d"));
    assertTrue(AbstractDJDocument._isNum("12.3d"));
    assertTrue(AbstractDJDocument._isNum("12.34d"));
    assertTrue(AbstractDJDocument._isNum("1.0D"));
    assertTrue(AbstractDJDocument._isNum("12.0D"));
    assertTrue(AbstractDJDocument._isNum("12.3D"));
    assertTrue(AbstractDJDocument._isNum("12.34D"));
    
    assertTrue(AbstractDJDocument._isNum("1.0e2"));
    assertTrue(AbstractDJDocument._isNum("12.0e2"));
    assertTrue(AbstractDJDocument._isNum("12.3e2"));
    assertTrue(AbstractDJDocument._isNum("12.34e2"));
    
    assertTrue(AbstractDJDocument._isNum("1.0e2f"));
    assertTrue(AbstractDJDocument._isNum("12.0e2f"));
    assertTrue(AbstractDJDocument._isNum("12.3e2f"));
    assertTrue(AbstractDJDocument._isNum("12.34e2f"));
    assertTrue(AbstractDJDocument._isNum("1.0e2F"));
    assertTrue(AbstractDJDocument._isNum("12.0e2F"));
    assertTrue(AbstractDJDocument._isNum("12.3e2F"));
    assertTrue(AbstractDJDocument._isNum("12.34e2F"));
    
    assertTrue(AbstractDJDocument._isNum("1.0e2d"));
    assertTrue(AbstractDJDocument._isNum("12.0e2d"));
    assertTrue(AbstractDJDocument._isNum("12.3e2d"));
    assertTrue(AbstractDJDocument._isNum("12.34e2d"));
    assertTrue(AbstractDJDocument._isNum("1.0e2D"));
    assertTrue(AbstractDJDocument._isNum("12.0e2D"));
    assertTrue(AbstractDJDocument._isNum("12.3e2D"));
    assertTrue(AbstractDJDocument._isNum("12.34e2D"));
  }
}

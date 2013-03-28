package utils;


import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;



public class EasyMockHelper {
   
   List<IMocksControl> mockControls = new ArrayList<IMocksControl>(); 
   
   public <T> T createMock(Class<T> mockClass) {
      return createMock(null, mockClass);
   }

   public <T> T createMock(String name, Class<T> mockClass) {
      IMocksControl control = null;
      if (mockClass.isInterface()) {
         
         control = EasyMock.createControl();
      } else {
         
         control = org.easymock.classextension.EasyMock.createControl();
      }
      
      mockControls.add(control);
      if (name != null) {
      	return control.createMock(name, mockClass);
      } else {
      	return control.createMock(mockClass);
      }
   }
   
   
   public void replayAll() {
      for (IMocksControl control : mockControls) {
         control.replay();
      }
   }   
   
   public void resetAll() {
      for (IMocksControl control : mockControls) {
         control.reset();
      }
   }
   
   public void verifyAll() {
      for (IMocksControl control : mockControls) {
         control.verify();
      }
   }
   
}

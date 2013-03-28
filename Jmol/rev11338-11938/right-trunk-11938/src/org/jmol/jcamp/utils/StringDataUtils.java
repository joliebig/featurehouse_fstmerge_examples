

package org.jmol.jcamp.utils;

public class StringDataUtils {
  
  
  public static String jcampSubString(String str, int i, int j){
    if (str.length()<j) 
      return str;      
    return str.substring(i,j);    
  }
  
  
  public static String truncateEndBlanks(String str) {
    while (str.charAt(str.length()-1)==' ') 
       str=str.substring(0,str.length()-1);
    return str;
  }
  
  
  public static int compareStrings(String str1, String str2){
    if (str1==null) return -1;
    if (str2==null) return -1;

    if (str1.length()!=str2.length()) return -1;

    return str1.compareTo(str2);    
  }
  
  
  public static String reduceDataPrecision(String data) {
    
    if (data.length()>10) {
       if (data.indexOf('E')!=-1)
          data=data.substring(0,data.indexOf('E')-1)+"e"+data.substring(data.indexOf('E')+1);
       if (data.indexOf('e')==-1) data=data.substring(0,9);
            else data=String.valueOf(
                              Math.pow(10,Double.valueOf(data.substring(data.indexOf('e')+1)).doubleValue())*
                              Double.valueOf(data.substring(0,Math.min(9,data.indexOf('e')-1))).doubleValue()
                                              );
       }    
    return data;    
   }
}

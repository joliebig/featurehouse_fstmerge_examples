

package edu.rice.cs.drjava.platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;


public class WindowsRegistry {
  
  public static final int HKEY_CLASSES_ROOT = 0x80000000;

  
  public static final int HKEY_CURRENT_USER = 0x80000001;
  
  
  public static final int HKEY_LOCAL_MACHINE = 0x80000002;
  
  
  
  public static final int ERROR_SUCCESS = 0;
  
  
  public static final int ERROR_FILE_NOT_FOUND = 2;
  
  
  public static final int ERROR_ACCESS_DENIED = 5;
  
  
  public static final int ERROR_UNKNOWN = -1;
  
  
  public static final int NULL_NATIVE_HANDLE = 0;
  
  
  
  public static final int DELETE = 0x10000;
  
  
  public static final int KEY_QUERY_VALUE = 1;
  
  
  public static final int KEY_SET_VALUE = 2;
  
  
  public static final int KEY_CREATE_SUB_KEY = 4;
  
  
  public static final int KEY_ENUMERATE_SUB_KEYS = 8;
  
  
  public static final int KEY_READ = 0x20019;
  
  
  public static final int KEY_WRITE = 0x20006;
  
  
  public static final int KEY_ALL_ACCESS = 0xf003f;
  
  
  public static class RegistryException extends Exception {
    private int errorCode = ERROR_UNKNOWN;
    public RegistryException(String s) { super(s); }
    public RegistryException(String s, Throwable cause) { super(s, cause); }
    public RegistryException(int error) { super("Error code "+error); errorCode = error; }
    public RegistryException(String s, int error) { super(s); errorCode = error; }
    public int getErrorCode() { return errorCode; }
  }
  
  public static class RegistryKeyNotFoundException extends RegistryException {
    private int hive;
    private String subKey;
    public RegistryKeyNotFoundException(int hive, String subKey) {
      super("Registry key "+hiveToString(hive)+"\\"+subKey+" not found", ERROR_FILE_NOT_FOUND);
      this.hive = hive;
      this.subKey = subKey;
    }
    public int getHive() { return hive; }
    public String getSubKey() { return subKey; }
  }
  
  public static class RegistryAccessDeniedException extends RegistryException {
    private int hive;
    private String subKey;
    public RegistryAccessDeniedException(int hive, String subKey) {
      super("Registry key "+hiveToString(hive)+"\\"+subKey+" could not be accessed", ERROR_ACCESS_DENIED);
      this.hive = hive;
      this.subKey = subKey;
    }
    public int getHive() { return hive; }
    public String getSubKey() { return subKey; }
  }
  
  private static RegistryException newRegistryException(int error, int hive, String subKey) {
    if (error==ERROR_FILE_NOT_FOUND) return new RegistryKeyNotFoundException(hive, subKey);
    if (error==ERROR_ACCESS_DENIED) return new RegistryKeyNotFoundException(hive, subKey);
    return new RegistryException(error);
  }
  
  
  public static class CreateResult {
    public int handle;
    public boolean wasCreated;
    public CreateResult(int h, boolean c) { handle = h; boolean wasCreated = c; }
  }

  
  public static class QueryInfoResult {
    public int subkeyCount;
    public int valueCount;
    public int maxSubkeyLength;
    public int maxValueLength;
    public QueryInfoResult(int sc, int vc, int msl, int mvl) { subkeyCount = sc; valueCount = vc; maxSubkeyLength = msl; maxValueLength = mvl; }
  }
  
  
  
  
  private static final int NATIVE_HANDLE = 0;
  
  
  private static final int ERROR_CODE = 1;
  
  
  private static final int SUBKEYS_NUMBER = 0;
  
  
  private static final int VALUES_NUMBER = 2;
  
  
  private static final int MAX_KEY_LENGTH = 3;
  
  
  private static final int MAX_VALUE_NAME_LENGTH = 4;
  
  
  private static final int DISPOSITION = 2;
  
  
  private  static final int REG_CREATED_NEW_KEY = 1;
  
  
  private static final int REG_OPENED_EXISTING_KEY = 2;

  
  private static volatile boolean initialized = false;
  
  private static final Preferences userRoot = Preferences.userRoot();
  private static final Preferences systemRoot = Preferences.systemRoot();
  private static Class userClass = null;
  private static Class systemClass = null;
  private static Method windowsRegOpenKey = null;
  private static Method windowsRegCloseKey = null;
  private static Method windowsRegCreateKeyEx = null;
  private static Method windowsRegDeleteKey = null;
  private static Method windowsRegFlushKey = null;
  private static Method windowsRegQueryValueEx = null;
  private static Method windowsRegSetValueEx = null;
  private static Method windowsRegDeleteValue = null;
  private static Method windowsRegQueryInfoKey = null;
  private static Method windowsRegEnumKeyEx = null;
  private static Method windowsRegEnumValue = null;
  
  
  private static synchronized void initialize() throws NoSuchMethodException {
    if (initialized) return;
    initialized = true;
    
    userClass = userRoot.getClass();
    systemClass = systemRoot.getClass();
    windowsRegOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey", new Class[] { int.class, byte[].class, int.class });
    windowsRegOpenKey.setAccessible(true);
    
    windowsRegCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey", new Class[] { int.class });
    windowsRegCloseKey.setAccessible(true);
    
    windowsRegCreateKeyEx = userClass.getDeclaredMethod("WindowsRegCreateKeyEx", new Class[] { int.class, byte[].class });
    windowsRegCreateKeyEx.setAccessible(true);
    
    windowsRegDeleteKey = userClass.getDeclaredMethod("WindowsRegDeleteKey", new Class[] { int.class, byte[].class });
    windowsRegDeleteKey.setAccessible(true);
    
    windowsRegFlushKey = userClass.getDeclaredMethod("WindowsRegFlushKey", new Class[] { int.class });
    windowsRegFlushKey.setAccessible(true);
    
    windowsRegQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx", new Class[] { int.class, byte[].class });
    windowsRegQueryValueEx.setAccessible(true);
    
    windowsRegSetValueEx = userClass.getDeclaredMethod("WindowsRegSetValueEx", new Class[] { int.class, byte[].class, byte[].class });
    windowsRegSetValueEx.setAccessible(true);
    
    windowsRegDeleteValue = userClass.getDeclaredMethod("WindowsRegDeleteValue", new Class[] { int.class, byte[].class });
    windowsRegDeleteValue.setAccessible(true);
    
    windowsRegQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey", new Class[] { int.class });
    windowsRegQueryInfoKey.setAccessible(true);
    
    windowsRegEnumKeyEx = userClass.getDeclaredMethod("WindowsRegEnumKeyEx", new Class[] { int.class, int.class, int.class });
    windowsRegEnumKeyEx.setAccessible(true);
    
    windowsRegEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue", new Class[] { int.class, int.class, int.class });
    windowsRegEnumValue.setAccessible(true); 
  }
  
  
  public static int openKey(int hKey, String subKey, int securityMask) throws RegistryException {
    int[] retval = null;
    try {
      initialize();
      byte[] barr = stringToNullTerminated(subKey);
      retval = (int[])windowsRegOpenKey.invoke(systemRoot, new Object[] { hKey, barr, securityMask });
      if (retval.length!=2) throw new AssertionError("Invalid array length.");
      if (retval[ERROR_CODE]!=ERROR_SUCCESS) throw newRegistryException(retval[ERROR_CODE], hKey, subKey);
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in openKey", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in openKey", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in openKey", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in openKey", ite);
    }
    return retval[NATIVE_HANDLE];
  }
  
  
  public static void closeKey(int hKey) throws RegistryException {
    try {
      initialize();
      int retval = ((Integer)windowsRegCloseKey.invoke(systemRoot, new Object[] { hKey })).intValue();
      if (retval!=ERROR_SUCCESS) throw new RegistryException(retval);
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in closeKey", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in closeKey", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in closeKey", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in closeKey", ite);
    }
  }
  
  
  public static CreateResult createKey(int hKey, String subKey) throws RegistryException {
    
    
    
    int[] retval = null;
    try {
      initialize();
      byte[] barr = stringToNullTerminated(subKey);
      retval = (int[])windowsRegCreateKeyEx.invoke(systemRoot, new Object[] { hKey, barr });
      if (retval.length!=3) throw new AssertionError("Invalid array length.");
      if (retval[ERROR_CODE]!=ERROR_SUCCESS) throw newRegistryException(retval[ERROR_CODE], hKey, subKey);
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in createKey", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in createKey", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in createKey", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in createKey", ite);
    }
    return new CreateResult(retval[NATIVE_HANDLE], retval[DISPOSITION]==REG_CREATED_NEW_KEY);
  }
  
  
  public static void deleteKey(int hKey, String subKey) throws RegistryException {
    try {
      initialize();
      byte[] barr = stringToNullTerminated(subKey);
      int retval = ((Integer)windowsRegDeleteKey.invoke(systemRoot, new Object[] { hKey, barr })).intValue();
      if (retval!=ERROR_SUCCESS) throw newRegistryException(retval, hKey, subKey);
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in deleteKey", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in deleteKey", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in deleteKey", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in deleteKey", ite);
    }
  }
  
  
  public static void flushKey(int hKey) throws RegistryException {
    try {
      initialize();
      int retval = ((Integer)windowsRegFlushKey.invoke(systemRoot, new Object[] { hKey })).intValue();
      if (retval!=ERROR_SUCCESS) throw new RegistryException(retval);
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in flushKey", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in flushKey", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in flushKey", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in flushKey", ite);
    }
  }
  
  
  public static String queryValue(int hKey, String valueName) throws RegistryException {
    byte[] retval = null;
    try {
      initialize();
      byte[] barr = stringToNullTerminated(valueName);
      retval = (byte[])windowsRegQueryValueEx.invoke(systemRoot, new Object[] { hKey, barr });
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in queryValue", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in queryValue", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in queryValue", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in queryValue", ite);
    }
    return nullTerminatedToString(retval);
  }  
  
  
  public static void setValue(int hKey, String valueName, String value) throws RegistryException {
    try {
      initialize();
      byte[] barrName = stringToNullTerminated(valueName);
      byte[] barrValue = stringToNullTerminated(value);
      int retval = ((Integer)windowsRegSetValueEx.invoke(systemRoot, new Object[] { hKey, barrName, barrValue })).intValue();
      if (retval!=ERROR_SUCCESS) throw new RegistryException(retval);
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in setValue", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in setValue", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in setValue", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in setValue", ite);
    }
  }
  
  
  public static void deleteValue(int hKey, String valueName) throws RegistryException {
    try {
      initialize();
      byte[] barr = stringToNullTerminated(valueName);
      int retval = ((Integer)windowsRegDeleteValue.invoke(systemRoot, new Object[] { hKey, barr })).intValue();
      if (retval!=ERROR_SUCCESS) throw new RegistryException(retval);
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in deleteValue", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in deleteValue", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in deleteValue", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in deleteValue", ite);
    }
  }
  
  
  
  public static QueryInfoResult queryInfoKey(int hKey) throws RegistryException {
    
    
    
    
    
    int[] retval = null;
    try {
      initialize();
      retval = (int[])windowsRegQueryInfoKey.invoke(systemRoot, new Object[] { hKey });
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in queryInfoKey", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in queryInfoKey", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in queryInfoKey", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in queryInfoKey", ite);
    }
    return new QueryInfoResult(retval[SUBKEYS_NUMBER], retval[VALUES_NUMBER], retval[MAX_KEY_LENGTH], retval[MAX_VALUE_NAME_LENGTH]);
  }
  
  
  public static String enumKey(int hKey, int subKeyIndex, int maxKeyLength) throws RegistryException {
    byte[] retval = null;
    try {
      initialize();
      retval = (byte[])windowsRegEnumKeyEx.invoke(systemRoot, new Object[] { hKey, subKeyIndex, maxKeyLength });
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in enumKey", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in enumKey", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in enumKey", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in enumKey", ite);
    }
    return nullTerminatedToString(retval);
  }  
  
  
  public static String enumValue(int hKey, int valueIndex, int maxValueNameLength) throws RegistryException {
    byte[] retval = null;
    try {
      initialize();
      retval = (byte[])windowsRegEnumValue.invoke(systemRoot, new Object[] { hKey, valueIndex, maxValueNameLength });
    }
    catch(NoSuchMethodException nsme) {
      throw new RegistryException("Exception thrown in enumValue", nsme);
    }
    catch(IllegalArgumentException iae) {
      throw new RegistryException("Exception thrown in enumValue", iae);
    }
    catch(IllegalAccessException iae2) {
      throw new RegistryException("Exception thrown in enumValue", iae2);
    }
    catch(InvocationTargetException ite) {
      throw new RegistryException("Exception thrown in enumValue", ite);
    }
    return nullTerminatedToString(retval);
  }
  
  
  public static void setKey(int hKey, String subKey, String name, String value) throws RegistryException {
    int handle = createKey(hKey, subKey).handle;
    handle = openKey(hKey, subKey, KEY_ALL_ACCESS);
    setValue(handle, name, value);
    flushKey(handle);
    closeKey(handle);
  }
  
  
  public static String getKey(int hKey, String subKey, String name) throws RegistryException {
    int handle = openKey(hKey, subKey, KEY_QUERY_VALUE);
    String s = queryValue(handle, name);
    closeKey(handle);
    return s;
  }
  
  
  public static String toString(int hKey, String subKey) throws RegistryException {
    StringBuilder sb = new StringBuilder();
    toStringHelper(hKey, subKey, "", sb);
    return sb.toString();
  }

  private static void toStringHelper(int hKey, String subKey, String prefix, StringBuilder sb) throws RegistryException {
    int handle = openKey(hKey, subKey, KEY_ENUMERATE_SUB_KEYS|KEY_QUERY_VALUE);
    QueryInfoResult qi = queryInfoKey(handle);
    sb.append(prefix).append(subKey).append('\n');
    sb.append(prefix).append(qi.subkeyCount).append(" subkeys, ").append(qi.valueCount).append(" values\n");
    String s;
    for(int i=0; i<qi.valueCount; ++i) {
      s = enumValue(handle, i, qi.maxValueLength+1);
      sb.append(prefix).append(s).append(" = ");
      s = queryValue(handle, s);
      sb.append(s).append('\n');
    }
    for(int i=0; i<qi.subkeyCount; ++i) {
      s = enumKey(handle, i, qi.maxSubkeyLength+1);
      toStringHelper(handle, s, prefix+"   ", sb);
    }
    closeKey(handle);
  }
  
  
  public static void delKey(int hKey, String subKey) throws RegistryException {
    int handle = openKey(hKey, subKey, KEY_ALL_ACCESS);
    QueryInfoResult qi = queryInfoKey(handle);
    String s;
    for(int i=0; i<qi.valueCount; ++i) {
      s = enumValue(handle, i, qi.maxValueLength+1);
      if (s!=null) deleteValue(handle, s);
    }
    for(int i=0; i<qi.subkeyCount; ++i) {
      s = enumKey(handle, i, qi.maxSubkeyLength+1);
      if (s!=null) delKey(handle, s);
    }
    flushKey(handle);
    closeKey(handle);
    deleteKey(hKey, subKey);
  }
  
  
  public static byte[] stringToNullTerminated(String str) {
    byte[] result = new byte[str.length() + 1];
    for (int i = 0; i<str.length(); i++) {
      result[i] = (byte) str.charAt(i);
    }
    result[str.length()] = 0;
    return result;
  }
  
  
  public static String nullTerminatedToString(byte[] barr) {
    if (barr==null) return null;
    return new String(barr).substring(0, barr.length-1);
  }
  
  
  public static String hiveToString(int hive) {
    return (hive==HKEY_CLASSES_ROOT?"HKEY_CLASSES_ROOT":
              (hive==HKEY_CURRENT_USER?"HKEY_CURRENT_USER":
                 (hive==HKEY_LOCAL_MACHINE?"HKEY_LOCAL_MACHINE":"0x"+Integer.toHexString(hive))));
  }


































}

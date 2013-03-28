

package edu.rice.cs.plt.reflect;

import java.io.Serializable;


public enum JavaVersion {
  UNRECOGNIZED { public String versionString() { return "?"; } },
  JAVA_1_1 { public String versionString() { return "1.1"; } },
  JAVA_1_2 { public String versionString() { return "1.2"; } },
  JAVA_1_3 { public String versionString() { return "1.3"; } },
  JAVA_1_4 { public String versionString() { return "1.4"; } },
  JAVA_5 { public String versionString() { return "5"; } },
  JAVA_6 { public String versionString() { return "6"; } },
  JAVA_7 { public String versionString() { return "7"; } },
  FUTURE { public String versionString() { return ">7"; } };
  
  
  public static final JavaVersion CURRENT = parseClassVersion(System.getProperty("java.class.version", ""));
  
  
  public static final JavaVersion.FullVersion CURRENT_FULL = parseFullVersion(System.getProperty("java.version", ""),
                                                                              System.getProperty("java.runtime.name", ""),
                                                                              System.getProperty("java.vm.vendor", ""));
  
  
  public boolean supports(JavaVersion v) { return compareTo(v) >= 0; }

  
  public abstract String versionString();
  
  
  public String toString() { return "Java " + versionString(); }
  
  
  public FullVersion fullVersion() {
    return new FullVersion(this, 0, 0, ReleaseType.STABLE, null, VendorType.UNKNOWN, "");
  }
  
  
  public static JavaVersion parseClassVersion(String text) {
    int dot = text.indexOf('.');
    if (dot == -1) { return UNRECOGNIZED; }
    try {
      int major = Integer.parseInt(text.substring(0, dot));
      int minor = Integer.parseInt(text.substring(dot+1));
      
      switch (major) {
        case 45:
          if (minor >= 3) { return JAVA_1_1; }
          else { return UNRECOGNIZED; }
        case 46: return JAVA_1_2;
        case 47: return JAVA_1_3;
        case 48: return JAVA_1_4;
        case 49: return JAVA_5;
        case 50: return JAVA_6;
        case 51: return JAVA_7;
        default: return (major > 51) ? FUTURE : UNRECOGNIZED;
      }
    }
    catch (NumberFormatException e) { return UNRECOGNIZED; }
  }
  
  
  public static FullVersion parseFullVersion(String java_version, String java_runtime_name, String java_vm_vendor) {
    VendorType vendor = VendorType.UNKNOWN;
    String vendorString = null;
    if (java_runtime_name.toLowerCase().contains("mint")) {
      vendor = VendorType.MINT;
      vendorString = "Mint";
    }
    if (java_runtime_name.toLowerCase().contains("openjdk")) {
      vendor = VendorType.OPENJDK;
      vendorString = "OpenJDK";
    }
    else if (java_vm_vendor.toLowerCase().contains("apple")) {
      vendor = VendorType.APPLE;
      vendorString = "Apple";
    }
    else if (java_vm_vendor.toLowerCase().contains("sun")) {
      vendor = VendorType.SUN;
      vendorString = "Sun";
    }
    
    String number;
    String typeString;
    
    
    if ((!java_version.startsWith("1.")) && (java_version.replaceAll("[^\\.]","").length()==1)) java_version = "1."+java_version;
    int dash = java_version.indexOf('-');
    if (dash == -1) { number = java_version; typeString = null; }
    else { number = java_version.substring(0, dash); typeString = java_version.substring(dash+1); }
    
    int dot1 = number.indexOf('.');
    if (dot1 == -1) { return new FullVersion(UNRECOGNIZED, 0, 0,
                                             ReleaseType.STABLE, null,
                                             vendor, vendorString); }
    int dot2 = number.indexOf('.', dot1+1);
    if (dot2 == -1) { return new FullVersion(UNRECOGNIZED, 0, 0,
                                             ReleaseType.STABLE, null,
                                             vendor, vendorString); }
    int underscore = number.indexOf('_', dot2+1);
    if (underscore == -1) { underscore = number.indexOf('.', dot2+1); }
    if (underscore == -1) { underscore = number.length(); }
    try {
      int major = Integer.parseInt(number.substring(0, dot1));
      int feature = Integer.parseInt(number.substring(dot1+1, dot2));
      int maintenance = Integer.parseInt(number.substring(dot2+1, underscore));
      int update = (underscore >= number.length()) ? 0 : Integer.parseInt(number.substring(underscore+1));
      
      ReleaseType type;
      if (typeString == null) { type = ReleaseType.STABLE; }
      else if (typeString.startsWith("ea")) { type = ReleaseType.EARLY_ACCESS; }
      else if (typeString.startsWith("beta")) { type = ReleaseType.BETA; }
      else if (typeString.startsWith("rc")) { type = ReleaseType.RELEASE_CANDIDATE; }
      else { type = ReleaseType.UNRECOGNIZED; }
      
      JavaVersion version = UNRECOGNIZED;
      if (major == 1) {
        switch (feature) {
          case 1: version = JAVA_1_1; break;
          case 2: version = JAVA_1_2; break;
          case 3: version = JAVA_1_3; break;
          case 4: version = JAVA_1_4; break;
          case 5: version = JAVA_5; break;
          case 6: version = JAVA_6; break;
          case 7: version = JAVA_7; break;
          default: if (feature > 7) { version = FUTURE; } break;
        }
      }
      
      return new FullVersion(version, maintenance, update,
                             type, typeString,
                             vendor, vendorString);
    }
    catch (NumberFormatException e) { return new FullVersion(UNRECOGNIZED, 0, 0,
                                                             ReleaseType.STABLE, null,
                                                             vendor, vendorString); }
  }
  
  
  public static FullVersion parseFullVersion(String text) {
    return parseFullVersion(text, "", ""); 
  }
  
  
  public static class FullVersion implements Comparable<FullVersion>, Serializable {
    private JavaVersion _majorVersion;
    private int _maintenance;
    private int _update;
    private ReleaseType _type;
    private String _typeString;
    private VendorType _vendor;
    private String _vendorString;
    
    
    private FullVersion(JavaVersion majorVersion, int maintenance, int update, ReleaseType type,
                        String typeString, VendorType vendor, String vendorString) {
      _majorVersion = majorVersion;
      _maintenance = maintenance;
      _update = update;
      _type = type;
      _typeString = typeString;
      _vendor = vendor;
      _vendorString = vendorString;
    }
    
    
    public JavaVersion majorVersion() { return _majorVersion; }
    
    
    public FullVersion onlyMajorVersionAndVendor() {
      return new FullVersion(_majorVersion, 0, 0, ReleaseType.STABLE, null, _vendor, _vendorString);
    }
    
    
    public int maintenance() { return _maintenance; }
    
    
    public int update() { return _update; }    
    
    
    public ReleaseType release() { return _type; }    

    
    public VendorType vendor() { return _vendor; }
    
    
    public boolean supports(JavaVersion v) { return _majorVersion.supports(v); }
    
    
    public int compareTo(FullVersion v) {
      if ((_vendor==VendorType.MINT) && (v._vendor!=VendorType.MINT)) {
        
        return -1;
      }
      if ((v._vendor==VendorType.MINT) && (_vendor!=VendorType.MINT)) {
        
        return 1;
      }
      
      int result = _majorVersion.compareTo(v._majorVersion);
      if (result == 0) {
        result = _maintenance - v._maintenance;
        if (result == 0) {
          result = _update - v._update;
          if (result == 0) {
            result = _type.compareTo(v._type);
            if (result == 0) {
              result = _vendor.compareTo(v._vendor);
              if (result == 0 && !_type.equals(ReleaseType.STABLE)) {
                result = _typeString.compareTo(v._typeString);
              }
            }
          }
        }
      }
      return result;
    }
    
    public boolean equals(Object o) {
      if (this == o) { return true; }
      else if (!(o instanceof FullVersion)) { return false; }
      else {
        FullVersion v = (FullVersion) o;
        return _majorVersion.equals(v._majorVersion) &&
          _maintenance == v._maintenance &&
          _update == v._update &&
          _type.equals(v._type) &&
          _vendor.equals(v._vendor) &&
          (_type.equals(ReleaseType.STABLE) || _typeString.equals(v._typeString));
      }
    }
    
    public int hashCode() {
      int stringHash = _typeString == null ? 0 : _typeString.hashCode();
      return _majorVersion.hashCode() ^ (_maintenance << 1) ^ (_update << 2) ^ (_type.hashCode() << 3) 
        ^ (_vendor.hashCode() << 4) ^ stringHash;
    }
    
    private String stringSuffix() {
      StringBuilder result = new StringBuilder();
      result.append("." + _maintenance);
      if (_update != 0) { result.append("_" + _update); }
      if (!_type.equals(ReleaseType.STABLE)) { result.append('-').append(_typeString); }
      if ((!_vendor.equals(VendorType.SUN)) && 
          (!_vendor.equals(VendorType.APPLE)) &&
          (!_vendor.equals(VendorType.UNKNOWN))) {
        result.append('-').append(_vendorString);
      }
      return result.toString();
    }
    
    
    public String versionString() { return _majorVersion.versionString() + stringSuffix(); }

    public String toString() { return _majorVersion + stringSuffix(); }
    
  }
    
  private static enum ReleaseType { UNRECOGNIZED, EARLY_ACCESS, BETA, RELEASE_CANDIDATE, STABLE; }

  
  public static enum VendorType { UNKNOWN, MINT, OPENJDK, APPLE, SUN; }
}

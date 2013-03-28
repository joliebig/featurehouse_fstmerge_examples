using System;
using System.Collections.Specialized;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Runtime.Serialization;
namespace NewsComponents.Utils
{
 public class SerializationInfoReader {
  private readonly SerializationInfo moInfo;
  private readonly StringCollection keys;
  public StringCollection Keys {
   get {
    return keys;
   }
  }
  public SerializationInfo Info {
   get {
    return moInfo;
   }
  }
  public SerializationInfoReader(SerializationInfo info) {
   moInfo = info;
   keys = new StringCollection();
   if (moInfo!=null) {
    foreach (SerializationEntry entry in moInfo) {
     keys.Add(entry.Name);
    }
   }
  }
  public bool Contains(string key) {
   return keys.Contains(key);
  }
  public bool GetBoolean(string name, bool defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetBoolean(name);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public byte GetByte(string name, byte defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetByte(name);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public char GetChar(string name, char defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetChar(name);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public DateTime GetDateTime(string name, DateTime defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetDateTime(name);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public decimal GetDecimal(string name, decimal defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetDecimal(name);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public double GetDouble(string name, double defaultValue) {
   try {
    if (keys.Contains(name)) {
     string number = moInfo.GetString(name);
     switch (number) {
      case "NaN":
       return Double.NaN;
      default:
       return Convert.ToDouble(number,CultureInfo.InvariantCulture);
     }
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public short GetShort(string name, short defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetInt16(name);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public int GetInt(string name, int defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetInt32(name);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public long GetLong(string name, long defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetInt64(name);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public float GetSingle(string name, float defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetSingle(name);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public string GetString(string name, string defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetString(name);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public object GetValue(string name, Type type, object defaultValue) {
   try {
    if (keys.Contains(name)) {
     return moInfo.GetValue(name, type);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public Font GetFont(string name, Font defaultValue) {
   try {
    if (keys.Contains(name)) {
     FontConverter oFontConv = new FontConverter();
     string sFont = moInfo.GetString(name);
     return oFontConv.ConvertFromString(null, CultureInfo.InvariantCulture, sFont) as Font;
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public Image GetImage(string name, Image defaultValue) {
   try {
    if (keys.Contains(name)) {
     byte[] sImage = (byte[])moInfo.GetValue(name,typeof(byte[]));
     return ConvertBytesToImage(sImage);
    }
    else {
     return defaultValue;
    }
   }
   catch {
    return defaultValue;
   }
  }
  public void AddValue(string name, object value,Type type) {
   if (!keys.Contains(name)) {
    keys.Add(name);
    moInfo.AddValue(name,value,type);
   }
  }
  public void AddValue(string name, object value) {
   if (!keys.Contains(name)) {
    keys.Add(name);
    moInfo.AddValue(name,value);
   }
  }
  public void AddValue(string name, object value, Type type, object defaultValue) {
   if (value!=defaultValue) {
    this.AddValue(name,value,type);
   }
  }
  public void AddValue(string name, object value, object defaultValue) {
   if (value!=defaultValue) {
    this.AddValue(name,value);
   }
  }
  public Version VersionNumber() {
   return GetVersion(moInfo);
  }
  static public Version GetVersion(SerializationInfo info) {
   string assemblyName = info.AssemblyName;
   char[] separators = { ',', '=' };
   string[] nameParts = assemblyName.Split(separators);
   return new Version(nameParts[2]);
  }
  public static string ConvertFont(Font font) {
   FontConverter oFontConv = new FontConverter();
   return oFontConv.ConvertToString(null,CultureInfo.InvariantCulture,font);
  }
  public static Image ConvertBytesToImage(byte[] bytes) {
   if (bytes!=null) {
    MemoryStream stream = new MemoryStream(bytes);
    return Image.FromStream(stream);
   }
   return null;
  }
  public static byte[] ConvertImageToBytes(Image image) {
   if (image!=null) {
    MemoryStream stream = new MemoryStream();
    image.Save(stream,image.RawFormat);
    return stream.ToArray();
   }
   return null;
  }
 }
}

using System;
using System.ComponentModel;
using System.IO;
using System.Xml.Serialization;
using System.Windows.Forms;
namespace WorldWind.Configuration
{
 public class SettingsBase
 {
  private string m_fileName;
  [XmlIgnore]
  [Browsable(false)]
  public string FileName
  {
   get { return m_fileName; }
   set { m_fileName = value; }
  }
  private string m_formatVersion;
  [Browsable(false)]
  public string FormatVersion
  {
   get { return m_formatVersion; }
   set { m_formatVersion = value; }
  }
  public enum LocationType
  {
   User = 0,
   UserLocal,
   UserCommon,
   Application,
  }
  public static string DefaultLocation(LocationType locationType)
  {
   string directory;
   switch(locationType)
   {
    case LocationType.UserLocal:
     return Application.LocalUserAppDataPath;
    case LocationType.UserCommon:
     return Application.CommonAppDataPath;
    case LocationType.Application:
     return Application.StartupPath;
    default:
    case LocationType.User:
     directory = Utility.Log.DefaultSettingsDirectory();
     Directory.CreateDirectory(directory);
     return directory;
   }
  }
  public string DefaultName()
  {
   return String.Format("{0}.xml", this.ToString());
  }
  public SettingsBase()
  {
   m_formatVersion = Application.ProductVersion;
  }
  public virtual void Save(string fileName)
  {
   XmlSerializer ser = null;
   try
   {
    ser = new XmlSerializer(this.GetType());
    using(TextWriter tw = new StreamWriter(fileName))
    {
     ser.Serialize(tw, this);
    }
   }
   catch(Exception ex)
   {
    throw new System.Exception(String.Format("Saving settings class '{0}' to {1} failed", this.GetType().ToString(), fileName), ex);
   }
  }
  public virtual void Save()
  {
   try
   {
    Save(m_fileName);
   }
   catch(Exception caught)
   {
    Utility.Log.Write(caught);
   }
  }
  public static SettingsBase Load(SettingsBase defaultSettings, string fileName)
  {
   defaultSettings.m_fileName = fileName;
   if(!File.Exists(fileName))
   {
    return defaultSettings;
   }
   SettingsBase settings = defaultSettings;
   try
   {
    XmlSerializer ser = new XmlSerializer(defaultSettings.GetType());
    using(TextReader tr = new StreamReader(fileName))
    {
     settings = (SettingsBase)ser.Deserialize(tr);
     settings.m_fileName = fileName;
    }
   }
   catch(Exception ex)
   {
    throw new System.Exception(String.Format("Loading settings from file '{1}' to {0} failed",
     defaultSettings.GetType().ToString(), fileName), ex);
   }
   return settings;
  }
  public static SettingsBase LoadFromPath(SettingsBase defaultSettings, string path)
  {
   string fileName = Path.Combine(path, defaultSettings.DefaultName());
   return Load(defaultSettings, fileName);
  }
  public static SettingsBase Load(SettingsBase defaultSettings, LocationType locationType, string name)
  {
   string fileName = Path.Combine(DefaultLocation(locationType), name);
   return Load(defaultSettings, fileName);
  }
  public static SettingsBase Load(SettingsBase defaultSettings, LocationType locationType)
  {
   return Load(defaultSettings, locationType, defaultSettings.DefaultName());
  }
  public static SettingsBase Load(SettingsBase defaultSettings)
  {
   return Load(defaultSettings, LocationType.User);
  }
  public string SettingsFilePath
  {
   get
   {
    return m_fileName;
   }
  }
 }
}

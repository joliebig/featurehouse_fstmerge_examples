using System;
using System.Collections;
using System.IO;
using WorldWind;
namespace WorldWind.PluginEngine
{
 public class PluginInfo
 {
  Plugin m_plugin;
  string m_fullPath;
  string m_name;
  string m_description;
  string m_developer;
  string m_webSite;
  string m_references;
  public Plugin Plugin
  {
   get
   {
    return m_plugin;
   }
   set
   {
    m_plugin = value;
   }
  }
  public string FullPath
  {
   get
   {
    return m_fullPath;
   }
   set
   {
    m_fullPath = value;
   }
  }
  public string ID
  {
   get
   {
    if(m_fullPath!=null)
     return Path.GetFileNameWithoutExtension(m_fullPath);
    return m_name;
   }
  }
  public string Name
  {
   get
   {
    if(m_name==null)
     ReadMetaData();
    return m_name;
   }
   set
   {
    m_name=value;
   }
  }
  public string Description
  {
   get
   {
    if(m_description==null)
     ReadMetaData();
    return m_description;
   }
   set
   {
    m_description=value;
   }
  }
  public string Developer
  {
   get
   {
    if(m_developer==null)
     ReadMetaData();
    return m_developer;
   }
  }
  public string WebSite
  {
   get
   {
    if(m_webSite==null)
     ReadMetaData();
    return m_webSite;
   }
  }
  public string References
  {
   get
   {
    if(m_references==null)
     ReadMetaData();
    return m_references;
   }
  }
  public bool IsCurrentlyLoaded
  {
   get
   {
    if(m_plugin==null)
     return false;
    return m_plugin.IsLoaded;
   }
  }
  public bool IsLoadedAtStartup
  {
   get
   {
    foreach(string startupId in MainApplication.Settings.PluginsLoadedOnStartup)
     if(ID==startupId)
      return true;
    return false;
   }
   set
   {
    ArrayList startupPlugins = MainApplication.Settings.PluginsLoadedOnStartup;
    for(int index=0; index < startupPlugins.Count; index++)
    {
     string startupId = (string)startupPlugins[index];
     if(ID==startupId)
     {
      startupPlugins.RemoveAt(index);
      break;
     }
    }
    if(value)
     startupPlugins.Add(ID);
   }
  }
  private void ReadMetaData()
  {
   try
   {
    if(m_fullPath==null)
     return;
    if(m_name==null)
     m_name = "";
    if(m_description==null)
     m_description = "";
    if(m_developer==null)
     m_developer = "";
    if(m_webSite==null)
     m_webSite = "";
    if(m_references==null)
     m_references = "";
    using(TextReader tr = File.OpenText(m_fullPath))
    {
     while(true)
     {
      string line = tr.ReadLine();
      if(line==null)
       break;
      FindTagInLine(line, "NAME", ref m_name);
      FindTagInLine(line, "DESCRIPTION", ref m_description);
      FindTagInLine(line, "DEVELOPER", ref m_developer);
      FindTagInLine(line, "WEBSITE", ref m_webSite);
      FindTagInLine(line, "REFERENCES", ref m_references);
     }
    }
   }
   catch(IOException)
   {
   }
   finally
   {
    if(m_name.Length==0)
     m_name = Path.GetFileNameWithoutExtension(m_fullPath);
   }
  }
  static void FindTagInLine(string inputLine, string tag, ref string value)
  {
   if(value!=string.Empty)
    return;
   tag = " " + tag + ": ";
   int index = inputLine.IndexOf(tag);
   if(index<0)
    return;
   value = inputLine.Substring(index+tag.Length);
  }
 }
}

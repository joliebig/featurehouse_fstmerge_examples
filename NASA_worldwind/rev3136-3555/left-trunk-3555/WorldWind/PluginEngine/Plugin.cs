using System;
namespace WorldWind.PluginEngine
{
 public abstract class Plugin
 {
  protected MainApplication m_Application;
  protected string m_PluginDirectory;
  protected bool m_isLoaded;
  public virtual MainApplication Application
  {
   get
   {
    return m_Application;
   }
  }
  public virtual MainApplication ParentApplication
  {
   get
   {
    return m_Application;
   }
  }
  public virtual string PluginDirectory
  {
   get
   {
    return m_PluginDirectory;
   }
  }
  public virtual bool IsLoaded
  {
   get
   {
    return m_isLoaded;
   }
  }
  public virtual void Load()
  {
  }
  public virtual void Unload()
  {
  }
  public virtual void PluginLoad( MainApplication parent, string pluginDirectory )
  {
   if(m_isLoaded)
    return;
   m_Application = parent;
   m_PluginDirectory = pluginDirectory;
   Load();
   m_isLoaded = true;
  }
  public virtual void PluginUnload()
  {
   Unload();
   m_isLoaded = false;
  }
 }
}

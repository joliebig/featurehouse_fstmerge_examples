using WorldWind;
using System;
using System.IO;
using System.Text;
using System.Collections;
using System.Collections.Specialized;
using System.CodeDom;
using System.CodeDom.Compiler;
using Microsoft.VisualBasic;
using System.Reflection;
using System.Security.Permissions;
using System.Security;
using Utility;
namespace WorldWind.PluginEngine
{
 public class PluginCompiler
 {
  MainApplication worldWind;
  const string LogCategory = "PLUG";
  Hashtable codeDomProviders = new Hashtable();
  CompilerParameters cp = new CompilerParameters();
  ArrayList m_plugins = new ArrayList();
  StringCollection m_worldWindReferencesList = new StringCollection();
  string m_pluginRootDirectory;
  public string PluginRootDirectory
  {
   get
   {
    return m_pluginRootDirectory;
   }
   set
   {
    m_pluginRootDirectory = value;
    try
    {
     Directory.CreateDirectory(m_pluginRootDirectory);
    }
    catch
    {
    }
   }
  }
  public ArrayList Plugins
  {
   get
   {
    return m_plugins;
   }
  }
  public PluginCompiler( MainApplication worldWind, string pluginDirectory )
  {
   this.worldWind = worldWind;
   AddCodeProvider(new Microsoft.CSharp.CSharpCodeProvider() );
   AddCodeProvider(new Microsoft.VisualBasic.VBCodeProvider() );
   AddCodeProvider(new Microsoft.JScript.JScriptCodeProvider() );
   cp.GenerateExecutable = false;
   cp.GenerateInMemory = true;
   cp.IncludeDebugInformation = false;
   AssemblyName[] assemblyNames = Assembly.GetExecutingAssembly().GetReferencedAssemblies();
   foreach(AssemblyName assemblyName in assemblyNames)
    Assembly.Load(assemblyName);
   Assembly[] assemblies = AppDomain.CurrentDomain.GetAssemblies();
   foreach(Assembly assembly in assemblies)
   {
    try
    {
     if(assembly.Location.Length > 0)
      m_worldWindReferencesList.Add(assembly.Location);
    }
    catch(NotSupportedException)
    {
    }
   }
   PluginRootDirectory = pluginDirectory;
  }
  public void AddCodeProvider( CodeDomProvider cdp )
  {
   codeDomProviders.Add("."+cdp.FileExtension, cdp);
  }
  public void FindPlugins( Assembly assembly )
  {
   foreach( Type t in assembly.GetTypes() )
   {
    if(!t.IsClass)
     continue;
    if(!t.IsPublic)
     continue;
    if(t.BaseType!=typeof(Plugin))
     continue;
    try
    {
     PluginInfo pi = new PluginInfo();
     pi.Plugin = (Plugin) assembly.CreateInstance( t.ToString() );
     pi.Name = t.Name;
     pi.Description = "World Wind internally loaded plugin.";
     m_plugins.Add(pi);
    }
    catch
    {
    }
   }
  }
  public void FindPlugins()
  {
   if(!Directory.Exists(m_pluginRootDirectory))
    return;
   foreach(string directory in Directory.GetDirectories(m_pluginRootDirectory))
    AddPlugin(directory);
   AddPlugin(m_pluginRootDirectory);
  }
  void AddPlugin(string path)
  {
   foreach (string filename in Directory.GetFiles(path))
   {
    bool isAlreadyInList = false;
    foreach(PluginInfo info in m_plugins)
    {
     if(info.FullPath == filename)
     {
      isAlreadyInList = true;
      break;
     }
    }
    if(isAlreadyInList)
     continue;
    string extension = Path.GetExtension(filename).ToLower();
    if(HasCompiler(extension) || IsPreCompiled(extension))
    {
     PluginInfo plugin = new PluginInfo();
     plugin.FullPath = filename;
     m_plugins.Add(plugin);
    }
   }
  }
  public void LoadStartupPlugins()
  {
   foreach(PluginInfo pi in m_plugins)
   {
    if(pi.IsLoadedAtStartup)
    {
     try
     {
                        Log.Write(Log.Levels.Debug, LogCategory, "loading "+pi.Name+" ...");
                        worldWind.SplashScreen.SetText("Initializing plugin " + pi.Name);
      Load(pi);
     }
     catch(Exception caught)
     {
      string message = "Plugin " + pi.Name + " failed: " + caught.Message;
      Log.Write(Log.Levels.Error, LogCategory, message);
      Log.Write(caught);
      pi.IsLoadedAtStartup = false;
      worldWind.SplashScreen.SetError(message);
     }
    }
   }
  }
  public bool HasCompiler(string fileExtension)
  {
   CodeDomProvider cdp = (CodeDomProvider)codeDomProviders[fileExtension];
   return cdp != null;
  }
  static public bool IsPreCompiled(string fileExtension)
  {
   return fileExtension==".dll";
  }
  public void Load(PluginInfo pi)
  {
   if(pi.Plugin == null)
   {
    string extension = Path.GetExtension(pi.FullPath).ToLower();
    Assembly asm = null;
    if(extension==".dll")
    {
     asm = Assembly.LoadFile(pi.FullPath);
    }
    else
    {
     CodeDomProvider cdp = (CodeDomProvider)codeDomProviders[extension];
     if(cdp==null)
      return;
     asm = Compile(pi, cdp);
    }
    pi.Plugin = GetPluginInterface(asm);
   }
   string pluginPath = MainApplication.DirectoryPath;
   if( pi.FullPath != null && pi.FullPath.Length > 0)
    pluginPath = Path.GetDirectoryName(pi.FullPath);
   pi.Plugin.PluginLoad(worldWind, pluginPath);
  }
  public void Unload(PluginInfo pi)
  {
   if(!pi.IsCurrentlyLoaded)
    return;
   pi.Plugin.PluginUnload();
  }
  public void Uninstall(PluginInfo pi)
  {
   Unload(pi);
   File.Delete( pi.FullPath );
   m_plugins.Remove( pi );
  }
  public void Dispose()
  {
   foreach(PluginInfo pi in m_plugins)
   {
    try
    {
     Unload(pi);
    }
    catch(Exception caught)
    {
     Log.Write(Log.Levels.Error, "PLUG", "Plugin unload failed: " + caught.Message);
    }
   }
  }
  Assembly Compile( PluginInfo pi, CodeDomProvider cdp )
  {
   if(cdp is Microsoft.JScript.JScriptCodeProvider)
    cp.CompilerOptions = "";
   else
    cp.CompilerOptions = "/unsafe";
   cp.ReferencedAssemblies.Clear();
   foreach( string reference in m_worldWindReferencesList)
    cp.ReferencedAssemblies.Add(reference);
   if(cdp is Microsoft.VisualBasic.VBCodeProvider)
    cp.ReferencedAssemblies.Add("Microsoft.VisualBasic.dll");
   foreach( string reference in pi.References.Split(','))
    AddCompilerReference( pi.FullPath, reference.Trim() );
   CompilerResults cr = cdp.CompileAssemblyFromFile( cp, pi.FullPath );
   if(cr.Errors.HasErrors || cr.Errors.HasWarnings)
   {
    StringBuilder error = new StringBuilder();
    foreach (CompilerError err in cr.Errors)
    {
     string type = (err.IsWarning ? "Warning" : "Error");
     if(error.Length>0)
      error.Append(Environment.NewLine);
     error.AppendFormat("{0} {1}: Line {2} Column {3}: {4}", type, err.ErrorNumber, err.Line, err.Column, err.ErrorText );
    }
                Log.Write(Log.Levels.Error, LogCategory, error.ToString());
                if(cr.Errors.HasErrors)
        throw new Exception( error.ToString() );
   }
   return cr.CompiledAssembly;
  }
  void AddCompilerReference( string pluginDirectory, string assemblyName )
  {
   try
   {
    if(assemblyName.Length<=0)
     return;
    Assembly referencedAssembly = Assembly.Load(assemblyName);
    if(referencedAssembly == null)
    {
     string pluginReferencePath = Path.Combine( Path.GetDirectoryName(pluginDirectory),
      assemblyName );
     referencedAssembly = Assembly.LoadFile(pluginReferencePath);
     if(referencedAssembly == null)
      throw new ApplicationException("Search for required library '" + assemblyName + "' failed.");
    }
    cp.ReferencedAssemblies.Add( referencedAssembly.Location );
   }
   catch(Exception caught)
   {
    throw new ApplicationException("Failed to load '"+assemblyName+"': "+caught.Message);
   }
  }
  static Plugin GetPluginInterface(Assembly asm)
  {
   foreach( Type t in asm.GetTypes() )
   {
    if(!t.IsClass)
     continue;
    if(!t.IsPublic)
     continue;
    if(t.BaseType!=typeof(Plugin))
     continue;
    try
    {
     Plugin pluginInstance = (Plugin) asm.CreateInstance( t.ToString() );
     return pluginInstance;
    }
    catch(MissingMethodException)
    {
     throw;
    }
    catch
    {
    }
   }
   throw new ArgumentException( "Plugin does not derive from base class Plugin." );
  }
 }
}

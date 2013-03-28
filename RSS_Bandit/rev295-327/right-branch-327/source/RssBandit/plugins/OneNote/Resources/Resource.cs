using System;
using System.Resources;
using System.Reflection;
using System.IO;
using System.Diagnostics;
namespace BlogExtension.OneNote {
 internal sealed class Resource {
  private const string ResourceFileName = ".Resources.PluginsText";
  static Resource InternalResource = new Resource();
  public static Resource Manager {
   get { return InternalResource; }
  }
  ResourceManager rm = null;
  public Resource() {
   rm = new ResourceManager(this.GetType().Namespace + ResourceFileName, Assembly.GetExecutingAssembly());
  }
  public string this [ string key ] {
   get {
    return rm.GetString( key, System.Globalization.CultureInfo.CurrentUICulture );
   }
  }
  public string this [ string key, params object[] formatArgs ] {
   get {
    return String.Format( System.Globalization.CultureInfo.CurrentUICulture, this[key], formatArgs );
   }
  }
  public Stream GetStream( string name ){
   return Assembly.GetExecutingAssembly().GetManifestResourceStream(this.GetType().Namespace + "." + name);
  }
  public string FormatMessage( string key, params object[] formatArgs ) {
   return String.Format( System.Globalization.CultureInfo.CurrentUICulture, this[key], formatArgs );
  }
 }
}

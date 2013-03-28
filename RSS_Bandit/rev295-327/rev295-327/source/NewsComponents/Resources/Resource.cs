using System; 
using System.Globalization; 
using System.Resources; 
using System.Reflection; 
using System.IO; namespace  NewsComponents {
	
 internal sealed class  Resource {
		
  private  const string ResourceFileName = ".Resources.RssComponentsText"; 
  static  Resource InternalResource = new Resource();
 
  public static  Resource Manager {
   get { return InternalResource; }
  }
 
  ResourceManager rm = null;
 
  public  Resource() {
   rm = new ResourceManager(this.GetType().Namespace + ResourceFileName, Assembly.GetExecutingAssembly());
  }
 
  public  Stream GetStream( string name ){
   return Assembly.GetExecutingAssembly().GetManifestResourceStream(this.GetType().Namespace + "." + name);
  }

	}

}

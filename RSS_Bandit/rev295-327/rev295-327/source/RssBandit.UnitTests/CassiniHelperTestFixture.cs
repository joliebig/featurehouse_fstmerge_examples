using System; 
using System.IO; 
using Cassini; namespace  RssBandit.UnitTests {
	
 public class  CassiniHelperTestFixture  : BaseTestFixture {
		
  private  Server _webServer;
 
  private readonly  int _webServerPort = 8081;
 
  private readonly  string _webServerVDir = "/";
 
  private  string _webrootPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, WEBROOT_PATH);
 
  private  string _webBinPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, WEBROOT_PATH + @"\bin");
 
  private  string _webServerUrl;
 
  protected virtual  void SetUp()
  {
   Directory.CreateDirectory(_webBinPath);
   foreach(string file in Directory.GetFiles(AppDomain.CurrentDomain.BaseDirectory, "*.dll"))
   {
    string newFile = Path.Combine(_webBinPath,Path.GetFileName(file));
    if(File.Exists(newFile))
    {
     File.Delete(newFile);
    }
    File.Copy(file,newFile);
   }
   _webServer = new Server(_webServerPort, _webServerVDir, _webrootPath);
   _webServerUrl = String.Format("http://localhost:{0}{1}",_webServerPort,_webServerVDir);
   _webServer.Start();
   Console.WriteLine(String.Format("Web Server started on port {0} with VDir {1} in physical directory {2}",_webServerPort,_webServerVDir,_webrootPath));
  }
 
  protected virtual  void TearDown()
  {
   try
   {
    if (_webServer != null)
    {
     _webServer.Stop();
     _webServer = null;
    }
    Directory.Delete(_webBinPath,true);
   }
   catch{}
  }
 
  protected  void StartWebServer()
  {
   if (_webServer != null)
   {
    _webServer.Start();
   }
  }
 
  protected  void StopWebServer()
  {
   if (_webServer != null)
   {
    _webServer.Stop();
   }
  }

	}

}

using System; 
using System.Windows.Forms; 
using System.Threading; 
using System.Net; 
using System.Runtime.Remoting; 
using System.Runtime.Remoting.Lifetime; 
using System.Runtime.Remoting.Channels; 
using System.Runtime.Remoting.Channels.Tcp; namespace  RssBandit {
	
  public delegate  void  OtherInstanceCallback (string[] args);
	
  public class  InitialInstanceActivator {
		
   private static  int usedPort = IPEndPoint.MinPort;
 
   public static  int GetPort() {
    if (usedPort == IPEndPoint.MinPort) {
     int configPort = Win32.Registry.InstanceActivatorPort;
     if (configPort != 0) {
      usedPort = configPort;
     } else {
      Random rnd = new Random();
      usedPort = rnd.Next(49153, 65535);
      Win32.Registry.InstanceActivatorPort = usedPort;
     }
    }
    return usedPort;
   }
 
   public static  int Port {
    get { return GetPort(); }
   }
 
   public static  string GetChannelName() {
    return GetChannelName(RssBanditApplication.GetUserPath());
   }
 
   public static  string GetChannelName(string userApp) {
          string suffix = string.Empty;
    return userApp.ToLower().Replace(@"\", "_") + suffix;
   }
 
   public static  string ChannelName {
    get { return GetChannelName(); }
   }
 
   public static  string MutexName {
    get { return ChannelName; }
   }
 
   public static  bool Activate(Form mainForm) {
    return Activate(new ApplicationContext(mainForm), null, null);
   }
 
   public static  bool Activate(Form mainForm, OtherInstanceCallback callback, string[] args) {
    return Activate(new ApplicationContext(mainForm), callback, args);
   }
 
   private static  Mutex mutex;
 
   public static  bool Activate(ApplicationContext context, OtherInstanceCallback callback, string[] args) {
    bool createdNew = false;
    mutex = new Mutex(true, MutexName, out createdNew);
    if( !createdNew ) {
     string url = string.Format("tcp:");
     MainFormActivator activator = (MainFormActivator)RemotingServices.Connect(typeof(MainFormActivator), url);
     activator.OnOtherInstance(args);
     return true;
    }
    bool success = false; int maxRetry = 25;
    while (!success && maxRetry > 0) {
     try {
      ChannelServices.RegisterChannel(new TcpChannel(Port), false);
      success = true;
     } catch (System.Net.Sockets.SocketException sx) {
      maxRetry--;
      if (maxRetry > 0 && sx.ErrorCode == 10048) {
       usedPort++;
       Win32.Registry.InstanceActivatorPort = usedPort;
      } else {
     throw;
      }
     }
    }
    RemotingServices.Marshal(new MainFormActivator(context, callback), ChannelName);
    return false;
   }
 
   public class  MainFormActivator  : MarshalByRefObject {
			
    public  MainFormActivator(ApplicationContext context, OtherInstanceCallback callback) {
     this.context = context;
     this.callback = callback;
    }
 
    public override  object InitializeLifetimeService() {
     ILease lease = (ILease)base.InitializeLifetimeService();
     lease.InitialLeaseTime = TimeSpan.Zero;
     return(lease);
    }
 
    public  void OnOtherInstance(string[] args) {
              GuiInvoker.Invoke(context.MainForm,
                  delegate
                  {
                      if (this.callback != null)
                          this.callback(args);
                      context.MainForm.Activate();
                  });
    }
 
    readonly  ApplicationContext context;
 
    readonly  OtherInstanceCallback callback;

		}

	}

}

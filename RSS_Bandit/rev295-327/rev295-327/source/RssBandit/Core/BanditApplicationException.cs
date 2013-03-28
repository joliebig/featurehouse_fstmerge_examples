using System; 
using System.Runtime.Serialization; 
using System.Collections.Specialized; 
using System.Security.Permissions; 
using System.Security; 
using System.Threading; 
using System.Security.Principal; 
using Microsoft.ApplicationBlocks.ExceptionManagement; namespace  RssBandit {
	
 public enum  ApplicationExceptions  {
  Unknown,
  FeedlistOldFormat,
  FeedlistOnRead,
  FeedlistOnProcessContent,
  FeedlistNA,
 } 
 [Serializable] 
 public class  BanditApplicationException  : BaseApplicationException , ISerializable {
		
  internal readonly  ApplicationExceptions number;
 
  private  string osNameVersion;
 
  private  string frameworkVersion;
 
  private  DateTime createdDateTime = DateTime.Now;
 
  public  BanditApplicationException():base() {
   InitializeEnvironmentInformation();
  }
 
  public  BanditApplicationException(ApplicationExceptions number):base() {
   this.number = number;
   InitializeEnvironmentInformation();
  }
 
  public  BanditApplicationException(string errorText):base(errorText) {
   InitializeEnvironmentInformation();
  }
 
  public  BanditApplicationException(ApplicationExceptions number,string errorText):base(errorText) {
   this.number = number;
   InitializeEnvironmentInformation();
  }
 
  public  BanditApplicationException(ApplicationExceptions number, Exception inner):base(String.Empty, inner) {
   this.number = number;
   InitializeEnvironmentInformation();
  }
 
  public  BanditApplicationException(string errorText,Exception inner):base(errorText,inner) {
   InitializeEnvironmentInformation();
  }
 
  public  BanditApplicationException(ApplicationExceptions number,string errorText,Exception inner):base(errorText,inner) {
   this.number = number;
   InitializeEnvironmentInformation();
  }
 
  protected  BanditApplicationException(SerializationInfo info,StreamingContext context):base(info,context) {
   try {
    this.number = (ApplicationExceptions)info.GetValue("number", typeof(ApplicationExceptions));
    this.osNameVersion = info.GetString("osNameVersion");
    this.frameworkVersion = info.GetString("frameworkVersion");
   }
   catch {}
   finally {
    this.number = ApplicationExceptions.Unknown;
   }
  }
 
  [SecurityPermission(SecurityAction.Demand, SerializationFormatter = true)] 
  public override  void GetObjectData(SerializationInfo info ,StreamingContext context) {
   info.AddValue("number",this.number);
   info.AddValue("osNameVersion", osNameVersion, typeof(string));
   info.AddValue("frameworkVersion", frameworkVersion, typeof(string));
   base.GetObjectData(info,context);
  }
 
  public  ApplicationExceptions Number {
   get { return this.number;}
  }
 
  public  string OsNameVersion {
   get {
    return osNameVersion;
   }
  }
 
  public  string FrameworkVersion {
   get {
    return frameworkVersion;
   }
  }
 
  private  void InitializeEnvironmentInformation() {
   try {
    osNameVersion = Environment.OSVersion.ToString();
   } catch {
    osNameVersion = "n/a";
   }
   try {
    frameworkVersion = ".NET CLR " + System.Runtime.InteropServices.RuntimeEnvironment.GetSystemVersion();
   } catch {
    frameworkVersion = "n/a";
   }
  }

	}

}

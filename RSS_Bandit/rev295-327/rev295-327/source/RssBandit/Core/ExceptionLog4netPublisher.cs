using System; 
using Microsoft.ApplicationBlocks.ExceptionManagement; namespace  RssBandit {
	
 public class  ExceptionLog4NetPublisher  : IExceptionPublisher {
		
  public  ExceptionLog4NetPublisher() {
  }
 
  public  void Publish(Exception exception, System.Collections.Specialized.NameValueCollection additionalInfo, System.Collections.Specialized.NameValueCollection configSettings) {
   Common.Logging.Log.Error(exception.ToString() ,exception);
  }

	}

}

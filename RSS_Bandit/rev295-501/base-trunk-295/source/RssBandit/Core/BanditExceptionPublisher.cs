using System;
using System.Text;
using System.Collections;
using System.Collections.Specialized;
using System.Web;
using System.Net.Mail;
using System.IO;
using Microsoft.ApplicationBlocks.ExceptionManagement;
using NewsComponents.Utils;
namespace RssBandit
{
 public class BanditExceptionPublisher : IExceptionPublisher
 {
  private string m_LogName = Path.Combine(Path.GetTempPath(), "rssbandit.error.log");
  private string m_OpMail = String.Empty;
  public BanditExceptionPublisher() {}
  void IExceptionPublisher.Publish(Exception exception, NameValueCollection AdditionalInfo, NameValueCollection ConfigSettings)
  {
   if (ConfigSettings != null)
   {
    if (ConfigSettings["fileName"] != null &&
     ConfigSettings["fileName"].Length > 0)
    {
     m_LogName = Environment.ExpandEnvironmentVariables(ConfigSettings["fileName"]);
    }
    if (ConfigSettings["operatorMail"] !=null &&
     ConfigSettings["operatorMail"].Length > 0)
    {
     m_OpMail = ConfigSettings["operatorMail"];
    }
   }
   StringBuilder strInfo = new StringBuilder();
   if (AdditionalInfo != null)
   {
    strInfo.AppendFormat("{0}General Information{0}", Environment.NewLine);
    strInfo.AppendFormat("{0}{1}", Environment.NewLine, RssBanditApplication.Caption);
    strInfo.AppendFormat("{0}OS Version: {1}", Environment.NewLine, Environment.OSVersion.ToString());
    strInfo.AppendFormat("{0}OS-Culture: {1}", Environment.NewLine, System.Globalization.CultureInfo.InstalledUICulture.Name);
    strInfo.AppendFormat("{0}Framework Version: .NET CLR {1}", Environment.NewLine, System.Runtime.InteropServices.RuntimeEnvironment.GetSystemVersion());
    strInfo.AppendFormat("{0}Thread-Culture: {1}", Environment.NewLine, System.Threading.Thread.CurrentThread.CurrentCulture.Name);
    strInfo.AppendFormat("{0}UI-Culture: {1}", Environment.NewLine, System.Threading.Thread.CurrentThread.CurrentUICulture.Name);
    strInfo.AppendFormat("{0}Additonal Info:", Environment.NewLine);
    foreach (string i in AdditionalInfo)
    {
     strInfo.AppendFormat("{0}{1}: {2}", Environment.NewLine, i, AdditionalInfo.Get(i));
    }
   }
   if (exception != null) {
    strInfo.AppendFormat("{0}{0}Exception Information{0}{1}", Environment.NewLine, exception.ToString());
   } else {
    strInfo.AppendFormat("{0}{0}No Exception.{0}", Environment.NewLine);
   }
   using ( FileStream fs = FileHelper.OpenForWriteAppend(m_LogName) ) {
    StreamWriter sw = new StreamWriter(fs);
    sw.WriteLine(strInfo.ToString());
    sw.WriteLine("================= End Entry =================");
    sw.Flush();
   }
   if (m_OpMail.Length > 0)
   {
    string subject = "Exception Notification";
    string body = strInfo.ToString();
                SmtpClient mailer = new SmtpClient();
                mailer.Send("CustomSender@mycompany.com", m_OpMail, subject, body);
   }
  }
 }
}

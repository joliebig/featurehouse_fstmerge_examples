using System.Web;
using ThoughtWorks.CruiseControl.WebDashboard.IO;
namespace ThoughtWorks.CruiseControl.WebDashboard.MVC
{
 public interface IResponse
 {
  void Process(HttpResponse response);
        ConditionalGetFingerprint ServerFingerprint { get; set; }
 }
}

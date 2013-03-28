using ThoughtWorks.CruiseControl.WebDashboard.IO;
namespace ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise
{
 public interface ICruiseAction
 {
  IResponse Execute(ICruiseRequest cruiseRequest);
 }
}

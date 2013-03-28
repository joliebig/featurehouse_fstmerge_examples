namespace ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise
{
 public class SimpleErrorViewBuilder : IErrorViewBuilder
 {
  public IResponse BuildView(string errorMessage)
  {
   return new HtmlFragmentResponse(string.Format(
    @"<html><head><title>CruiseControl.NET</title></head><body><h1>An error has occurred in CruiseControl.NET</h1><p>{0}</p></body></html>",
    errorMessage));
  }
 }
}

using System;
namespace ThoughtWorks.CruiseControl.Remote
{
 public interface ICruiseManagerFactory
 {
        [Obsolete("")]
  ICruiseManager GetCruiseManager(string url);
        ICruiseServerClient GetCruiseServerClient(string url);
 }
}

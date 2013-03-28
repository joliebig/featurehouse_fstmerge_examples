namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public interface IPollable
 {
  void Poll();
  void OnPollStarting();
 }
}

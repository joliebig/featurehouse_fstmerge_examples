namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public interface IServerMonitor : IPollable
 {
  event MonitorServerPolledEventHandler Polled;
  event MonitorServerQueueChangedEventHandler QueueChanged;
 }
}

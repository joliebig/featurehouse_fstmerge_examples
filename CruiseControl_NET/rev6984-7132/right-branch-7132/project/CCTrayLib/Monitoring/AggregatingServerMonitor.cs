namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public class AggregatingServerMonitor : IServerMonitor
 {
  public event MonitorServerPolledEventHandler Polled;
  public event MonitorServerQueueChangedEventHandler QueueChanged;
  private readonly IServerMonitor[] monitors;
  public AggregatingServerMonitor(params IServerMonitor[] monitors)
  {
   this.monitors = monitors;
   foreach (IServerMonitor monitor in this.monitors)
   {
    monitor.Polled += new MonitorServerPolledEventHandler(Monitor_Polled);
    monitor.QueueChanged += new MonitorServerQueueChangedEventHandler(Monitor_QueueChanged);
   }
  }
  public void Poll()
  {
   foreach (IServerMonitor monitor in monitors)
   {
    monitor.Poll();
   }
  }
  public void OnPollStarting()
  {
   foreach (IServerMonitor monitor in monitors)
   {
    monitor.OnPollStarting();
   }
  }
  private void Monitor_Polled(object sender, MonitorServerPolledEventArgs args)
  {
   if (Polled != null) Polled(this, args);
  }
  private void Monitor_QueueChanged(object sender, MonitorServerQueueChangedEventArgs args)
  {
   if (QueueChanged != null) QueueChanged(this, args);
  }
 }
}

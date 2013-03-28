using System;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Triggers
{
 [ReflectorType("intervalTrigger")]
 public class IntervalTrigger : ITrigger
 {
  public const double DefaultIntervalSeconds = 60;
  private readonly DateTimeProvider dateTimeProvider;
  private string name;
  private double intervalSeconds = DefaultIntervalSeconds;
        private double initialIntervalSeconds = -1;
     private bool isInitialInterval = true;
        private DateTime nextBuildTime;
  public IntervalTrigger() : this(new DateTimeProvider()) { }
  public IntervalTrigger(DateTimeProvider dtProvider)
  {
   this.dateTimeProvider = dtProvider;
            IncrementNextBuildTime();
  }
  [ReflectorProperty("name", Required=false)]
  public string Name
  {
   get
   {
    if (name == null) name = GetType().Name;
    return name;
   }
   set { name = value; }
  }
        [ReflectorProperty("seconds", Required=false)]
        public double IntervalSeconds
        {
            get { return intervalSeconds; }
            set
            {
                intervalSeconds = value;
                IncrementNextBuildTime();
            }
        }
  [ReflectorProperty("initialSeconds", Required = false)]
  public double InitialIntervalSeconds
  {
   get
   {
                if (initialIntervalSeconds == -1)
                    return IntervalSeconds;
                else
                    return initialIntervalSeconds;
   }
   set
   {
    initialIntervalSeconds = value;
    IncrementNextBuildTime();
   }
  }
  [ReflectorProperty("buildCondition", Required=false)]
  public BuildCondition BuildCondition = BuildCondition.IfModificationExists;
  public virtual void IntegrationCompleted()
  {
            isInitialInterval = false;
   IncrementNextBuildTime();
  }
  protected DateTime IncrementNextBuildTime()
  {
      double delaySeconds;
            if (isInitialInterval)
    delaySeconds = InitialIntervalSeconds;
            else
                delaySeconds = IntervalSeconds;
            return nextBuildTime = dateTimeProvider.Now.AddSeconds(delaySeconds);
  }
  public DateTime NextBuild
  {
   get { return nextBuildTime;}
  }
  public virtual IntegrationRequest Fire()
  {
   BuildCondition buildCondition = ShouldRunIntegration();
   if (buildCondition == BuildCondition.NoBuild) return null;
   return new IntegrationRequest(buildCondition, Name, null);
  }
  private BuildCondition ShouldRunIntegration()
  {
   if (dateTimeProvider.Now < nextBuildTime)
    return BuildCondition.NoBuild;
   return BuildCondition;
  }
 }
}

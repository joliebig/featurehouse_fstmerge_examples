using System;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Triggers
{
    [ReflectorType("cronTrigger")]
    public class CronTrigger : ITrigger
    {
        private NCrontab.CrontabSchedule schedule;
        private DateTime nextBuild = DateTime.MinValue;
        private DateTimeProvider dtProvider;
        private bool triggered;
        public CronTrigger()
            : this(new DateTimeProvider())
        {
        }
        public CronTrigger(DateTimeProvider dtProvider)
        {
            this.dtProvider = dtProvider;
            this.BuildCondition = BuildCondition.IfModificationExists;
            this.StartDate = DateTime.Now;
            this.EndDate = DateTime.MaxValue;
        }
        public void IntegrationCompleted()
        {
            if (triggered)
            {
                SetNextIntegrationDateTime();
            }
            triggered = false;
        }
        public DateTime NextBuild
        {
            get
            {
                if (nextBuild == DateTime.MinValue)
                {
                    SetNextIntegrationDateTime();
                }
                return nextBuild;
            }
        }
        public IntegrationRequest Fire()
        {
            schedule = NCrontab.CrontabSchedule.Parse(CronExpression);
            DateTime now = dtProvider.Now;
            if (now > NextBuild)
            {
                triggered = true;
                return new IntegrationRequest(BuildCondition, Name, null);
            }
            return null;
        }
        [ReflectorProperty("cronExpression", Required = true)]
        public string CronExpression { get; set; }
        private string name;
        [ReflectorProperty("name", Required = false)]
        public string Name
        {
            get
            {
                if (name == null) name = GetType().Name;
                return name;
            }
            set { name = value; }
        }
        [ReflectorProperty("buildCondition", Required = false)]
        public BuildCondition BuildCondition { get; set; }
        public DateTime StartDate { get; set; }
        public DateTime EndDate { get; set; }
        private void SetNextIntegrationDateTime()
        {
            nextBuild = schedule.GetNextOccurrence(StartDate, EndDate);
        }
    }
}

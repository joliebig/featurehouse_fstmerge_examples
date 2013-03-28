namespace ThoughtWorks.CruiseControl.Core.Triggers
{
    using System;
    using System.Collections.Generic;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("rollUpTrigger")]
    public class RollUpTrigger
        : ITrigger
    {
        private readonly IClock clock;
        private DateTime nextAllowed;
        public RollUpTrigger()
            : this(new SystemClock())
        {
        }
        public RollUpTrigger(IClock clock)
        {
            this.clock = clock;
            this.nextAllowed = this.clock.Now.AddMinutes(-1);
        }
        public DateTime NextBuild
        {
            get { return this.nextAllowed; }
        }
        [ReflectorProperty("trigger", InstanceTypeKey = "type")]
        public ITrigger InnerTrigger { get; set; }
        [ReflectorProperty("time", typeof(TimeoutSerializerFactory))]
        public Timeout MinimumTime { get; set; }
        public void IntegrationCompleted()
        {
            this.InnerTrigger.IntegrationCompleted();
            this.nextAllowed = this.clock.Now.AddMilliseconds(this.MinimumTime.Millis);
        }
        public IntegrationRequest Fire()
        {
            IntegrationRequest request = null;
            if (this.clock.Now > this.nextAllowed)
            {
                request = this.InnerTrigger.Fire();
            }
            return request;
        }
    }
}

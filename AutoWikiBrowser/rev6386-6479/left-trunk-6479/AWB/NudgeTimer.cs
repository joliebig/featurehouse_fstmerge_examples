using System;
using System.ComponentModel;
namespace AutoWikiBrowser
{
    internal sealed partial class NudgeTimer : System.Windows.Forms.Timer
    {
        public new event TickEventHandler Tick;
        public delegate void TickEventHandler(object sender, NudgeTimerEventArgs eventArgs);
        public NudgeTimer(IContainer container)
            : base(container)
        {
            base.Tick += NudgeTimerTick;
        }
        public void StartMe()
        {
            Start();
        }
        public void Reset()
        {
            Interval = 120000;
        }
        private void NudgeTimerTick(object sender, EventArgs eventArgs)
        {
            NudgeTimerEventArgs myEventArgs = new NudgeTimerEventArgs();
            Tick(this, myEventArgs);
            if (!myEventArgs.Cancel)
            {
                switch (Interval)
                {
                    case 120000:
                        Interval = 240000;
                        break;
                    case 240000:
                        Interval = 360000;
                        break;
                    case 360000:
                        Interval = 600000;
                        break;
                }
            }
        }
        public override bool Enabled
        {
            get { return base.Enabled; }
            set
            {
                base.Enabled = value;
                Interval = 120000;
            }
        }
        internal sealed class NudgeTimerEventArgs : EventArgs
        {
            public bool Cancel { get; set; }
        }
    }
}

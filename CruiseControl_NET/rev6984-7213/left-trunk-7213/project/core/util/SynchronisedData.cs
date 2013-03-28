namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    using System.Threading;
    public delegate object LoadDataHandler();
    public class SynchronisedData
        : IDisposable
    {
        private ManualResetEvent manualEvent = new ManualResetEvent(false);
        public object Data { get; private set; }
        public bool WaitForLoad(int milliseconds)
        {
            var result = this.manualEvent.WaitOne(milliseconds, false);
            return result;
        }
        public void LoadData(LoadDataHandler handler)
        {
            try
            {
                this.Data = handler();
            }
            finally
            {
                this.manualEvent.Set();
            }
        }
        public void Dispose()
        {
            this.manualEvent.Set();
        }
    }
}

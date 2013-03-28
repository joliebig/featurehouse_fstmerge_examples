namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    using System.Threading;
    public delegate TData LoadDataHandler<TData>();
    public class SynchronisedData<TData>
        : IDisposable
    {
        private ManualResetEvent manualEvent = new ManualResetEvent(false);
        public TData Data { get; private set; }
        public bool WaitForLoad(int milliseconds)
        {
            var result = this.manualEvent.WaitOne(milliseconds, false);
            return result;
        }
        public void LoadData(LoadDataHandler<TData> handler)
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

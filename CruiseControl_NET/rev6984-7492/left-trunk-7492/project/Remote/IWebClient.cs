namespace ThoughtWorks.CruiseControl.Remote
{
    using System;
    using System.Collections.Specialized;
    using System.Net;
    public interface IWebClient
    {
        byte[] UploadValues(Uri address, string method, NameValueCollection data);
        void UploadValuesAsync(Uri address, string method, NameValueCollection data);
        void CancelAsync();
        event EventHandler<BinaryDataEventArgs> UploadValuesCompleted;
    }
}

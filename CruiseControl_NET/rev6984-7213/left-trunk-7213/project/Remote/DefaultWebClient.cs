namespace ThoughtWorks.CruiseControl.Remote
{
    using System;
    using System.Collections.Specialized;
    using System.Net;
    public class DefaultWebClient
        : IWebClient
    {
        private WebClient innerClient;
        public DefaultWebClient()
        {
            this.innerClient = new WebClient();
            this.innerClient.UploadValuesCompleted += (o, e) =>
            {
                if (this.UploadValuesCompleted != null)
                {
                    this.UploadValuesCompleted(this, new BinaryDataEventArgs(e.Result, e.Error, e.Cancelled, e.UserState));
                }
            };
        }
        public byte[] UploadValues(Uri address, string method, NameValueCollection data)
        {
            return this.innerClient.UploadValues(address, method, data);
        }
        public void UploadValuesAsync(Uri address, string method, NameValueCollection data)
        {
            this.innerClient.UploadValuesAsync(address, method, data);
        }
        public void CancelAsync()
        {
            this.innerClient.CancelAsync();
        }
        public event EventHandler<BinaryDataEventArgs> UploadValuesCompleted;
    }
}

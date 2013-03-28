using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Messages;
using System.Net;
using System.Collections.Specialized;
using System.Xml.Serialization;
using System.IO;
using System.Reflection;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class HttpConnection
        : IServerConnection
    {
        private readonly Uri serverAddress;
        private bool isBusy;
        private Dictionary<object, WebClient> asyncOperations = new Dictionary<object, WebClient>();
        private object lockObject = new object();
        public HttpConnection(string serverAddress)
            : this(new Uri(serverAddress))
        {
        }
        public HttpConnection(Uri serverAddress)
        {
            this.serverAddress = serverAddress;
        }
        public string Type
        {
            get { return "HTTP"; }
        }
        public string ServerName
        {
            get { return serverAddress.Host; }
        }
        public bool IsBusy
        {
            get { return isBusy || (asyncOperations.Count > 0); }
        }
        public Response SendMessage(string action, ServerRequest request)
        {
            Uri targetAddress = new Uri(serverAddress,
                string.Format("/server/{0}/RawXmlMessage.aspx", request.ServerName));
            WebClient client = new WebClient();
            NameValueCollection formData = new NameValueCollection();
            formData.Add("action", action);
            formData.Add("message", request.ToString());
            string response = Encoding.UTF8.GetString(client.UploadValues(targetAddress, "POST", formData));
            Response result = XmlConversionUtil.ProcessResponse(response);
            return result;
        }
        public virtual void SendMessageAsync(string action, ServerRequest request)
        {
            SendMessageAsync(action, request, null);
        }
        public virtual void SendMessageAsync(string action, ServerRequest request, object userState)
        {
            lock (lockObject)
            {
                if (userState == null)
                {
                    if (isBusy) throw new InvalidOperationException();
                    isBusy = true;
                }
                else if (asyncOperations.ContainsKey(userState))
                {
                    if (asyncOperations.ContainsKey(userState)) throw new ArgumentException("Duplicate userState", "userState");
                }
            }
            WebClient client = new WebClient();
            client.UploadValuesCompleted += delegate(object sender, UploadValuesCompletedEventArgs e)
            {
                if (SendMessageCompleted != null)
                {
                    if ((e.Error != null) && !e.Cancelled)
                    {
                        string response = Encoding.UTF8.GetString(e.Result);
                        Response result = XmlConversionUtil.ProcessResponse(response);
                        MessageReceivedEventArgs args = new MessageReceivedEventArgs(result, null, false, userState);
                        SendMessageCompleted(this, args);
                    }
                    else
                    {
                        MessageReceivedEventArgs args = new MessageReceivedEventArgs(null, e.Error, e.Cancelled, userState);
                        SendMessageCompleted(this, args);
                    }
                }
                CompleteAsyncCall(userState);
            };
            lock (lockObject)
            {
                asyncOperations.Add(userState ?? string.Empty, client);
            }
            try
            {
                Uri targetAddress = new Uri(serverAddress,
                    string.Format("/server/{0}/RawXmlMessage.aspx", request.ServerName));
                NameValueCollection formData = new NameValueCollection();
                formData.Add("action", action);
                formData.Add("message", request.ToString());
                client.UploadValuesAsync(targetAddress, "POST", formData);
            }
            catch (Exception error)
            {
                if (SendMessageCompleted != null)
                {
                    MessageReceivedEventArgs args = new MessageReceivedEventArgs(null, error, false, userState);
                    SendMessageCompleted(this, args);
                }
                CompleteAsyncCall(userState);
            }
        }
        public virtual void CancelAsync()
        {
            CancelAsync(null);
        }
        public void CancelAsync(object userState)
        {
            lock (lockObject)
            {
                if (asyncOperations.ContainsKey(userState ?? string.Empty))
                {
                    asyncOperations[userState ?? string.Empty].CancelAsync();
                }
            }
        }
        public event EventHandler<MessageReceivedEventArgs> SendMessageCompleted;
        private void CompleteAsyncCall(object userState)
        {
            lock (lockObject)
            {
                if (userState == null)
                {
                    isBusy = false;
                }
                if (asyncOperations.ContainsKey(userState ?? string.Empty)) asyncOperations.Remove(userState ?? string.Empty);
            }
        }
    }
}

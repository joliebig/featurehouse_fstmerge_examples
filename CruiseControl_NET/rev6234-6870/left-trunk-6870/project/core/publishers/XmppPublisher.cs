using System;
using System.Threading;
using agsXMPP;
using agsXMPP.protocol.client;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    [ReflectorType("xmpp")]
    public class XmppPublisher
        : ITask
    {
        private string userName;
        private string password;
        private string server = "gmail.com";
        private string connectServer = "talk.google.com";
        private int timeOutPeriod = 120;
        private int sendPeriod = 30;
        private string[] recipients;
        private XmppClientConnection client;
        private bool hasError;
        private DateTime? messagesSent;
        private IIntegrationResult result;
        [ReflectorProperty("loginName", Required = true)]
        public string LoginName
        {
            get { return userName; }
            set { userName = value; }
        }
        [ReflectorProperty("password", Required = true)]
        public string LoginPassword
        {
            get { return password; }
            set { password = value; }
        }
        [ReflectorProperty("server", Required = false)]
        public string Server
        {
            get { return server; }
            set { server = value; }
        }
        [ReflectorProperty("connectServer", Required = false)]
        public string ConnectServer
        {
            get { return connectServer; }
            set { connectServer = value; }
        }
        [ReflectorProperty("timeOutPeriod", Required = false)]
        public int TimeOutPeriod
        {
            get { return timeOutPeriod; }
            set { timeOutPeriod = value; }
        }
        [ReflectorProperty("sendPeriod", Required = false)]
        public int SendPeriod
        {
            get { return sendPeriod; }
            set { sendPeriod = value; }
        }
        [ReflectorArray("recipients", Required = true)]
        public string[] Recipients
        {
            get { return recipients; }
            set { recipients = value; }
        }
        public void Run(IIntegrationResult result)
        {
            this.result = result;
            try
            {
                hasError = false;
                messagesSent = null;
                client = new XmppClientConnection();
                client.Server = server;
                client.ConnectServer = connectServer;
                client.AutoRoster = false;
                client.Resource = null;
                client.OnLogin += new ObjectHandler(xmppClient_OnLogin);
                client.OnError += new ErrorHandler(xmppClient_OnError);
                client.OnAuthError += new XmppElementHandler(client_OnAuthError);
                Log.Debug("Connecting to XMPP server: " + server);
                client.Open(userName, password);
                bool continueWaiting = true;
                DateTime startWait = DateTime.Now;
                while (continueWaiting)
                {
                    Thread.Sleep(100);
                    if (hasError)
                    {
                        continueWaiting = false;
                    }
                    else if (client.Authenticated)
                    {
                        if (messagesSent.HasValue)
                        {
                            TimeSpan timeWaited = (DateTime.Now - messagesSent.Value);
                            continueWaiting = (timeWaited.TotalSeconds < sendPeriod);
                        }
                    }
                    if (continueWaiting)
                    {
                        TimeSpan timeWaited = (DateTime.Now - startWait);
                        if (timeWaited.TotalSeconds > timeOutPeriod)
                        {
                            continueWaiting = false;
                            Log.Warning("Time-out period has expired - send was not successful");
                        }
                    }
                }
                client.Close();
                client = null;
            }
            catch (Exception error)
            {
                Log.Error(error);
                result.ExceptionResult = error;
                result.Status = ThoughtWorks.CruiseControl.Remote.IntegrationStatus.Exception;
            }
        }
        private void xmppClient_OnError(object sender, Exception ex)
        {
            result.ExceptionResult = ex;
            result.Status = ThoughtWorks.CruiseControl.Remote.IntegrationStatus.Exception;
            hasError = true;
            Log.Error(ex);
        }
        void client_OnAuthError(object sender, agsXMPP.Xml.Dom.Element e)
        {
            result.ExceptionResult = new Exception(
                string.Format("XPPP Authentication Failed - Remote server response was {0}", e.InnerXml));
            result.Status = ThoughtWorks.CruiseControl.Remote.IntegrationStatus.Exception;
            hasError = true;
            Log.Error(result.ExceptionResult);
        }
        private void xmppClient_OnLogin(object sender)
        {
            string messageBody = string.Format("{0} has finished building - result is {1}", result.ProjectName, result.Status);
            foreach (string recipient in recipients)
            {
                Log.Debug("Sending XMPP message to  " + recipient);
                try
                {
                    Message message = new Message(recipient, MessageType.chat, messageBody);
                    client.Send(message);
                }
                catch (Exception error)
                {
                    Log.Error(error);
                }
            }
            messagesSent = DateTime.Now;
        }
    }
}

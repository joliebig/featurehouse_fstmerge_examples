namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.IO;
    using System.Net;
    using System.Text;
    using System.Xml;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("checkHttpStatus")]
    public class HttpStatusTask
        : TaskBase
    {
        private int[] successStatusCodes;
        public HttpStatusTask()
        {
            this.Retries = 3;
            this.successStatusCodes = new[] { (int)HttpStatusCode.OK };
        }
        [ReflectorProperty("successStatusCodes", Required = false)]
        public string SuccessStatusCodes
        {
            get
            {
                string result = string.Empty;
                if (this.successStatusCodes != null)
                {
                    foreach (int code in this.successStatusCodes)
                    {
                        if (result != string.Empty)
                        {
                            result = result + ",";
                        }
                        result = result + code.ToString();
                    }
                }
                return result;
            }
            set
            {
                string[] codes = value.Split(new[] { ',' }, StringSplitOptions.RemoveEmptyEntries);
                if (codes.Length == 0)
                {
                    this.successStatusCodes = null;
                    return;
                }
                this.successStatusCodes = new int[codes.Length];
                for (int i = 0; i < codes.Length; ++i)
                {
                    this.successStatusCodes[i] = Int32.Parse(codes[i].Trim());
                }
            }
        }
        [ReflectorProperty("httpRequest", Required = true)]
        public HttpRequestSettings RequestSettings { get; set; }
        [ReflectorProperty("retries", Required = false)]
        public int Retries { get; set; }
        [ReflectorProperty("includeContent", Required = false)]
        public bool IncludeContent { get; set; }
        [ReflectorProperty("taskTimeout", typeof(TimeoutSerializerFactory), Required = false)]
        public Timeout Timeout { get; set; }
        public bool HasTimeout
        {
            get { return this.Timeout != null; }
        }
        [ReflectorProperty("retryDelay", typeof(TimeoutSerializerFactory), Required = false)]
        public Timeout RetryDelay { get; set; }
        protected override bool Execute(IIntegrationResult result)
        {
            Log.Debug("HttpStatusTask is executing");
            bool taskTimedOut = false;
            string msg = string.Format("Checking http status of URI: '{0}'", this.RequestSettings.Uri.ToString());
            if (!String.IsNullOrEmpty(Description))
            {
                msg = Description + " (" + msg + ")";
            }
            Log.Debug(msg);
            result.BuildProgressInformation.SignalStartRunTask(msg);
            XmlTaskResult taskResult = new XmlTaskResult();
            List<HttpRequestStatus> resp = new List<HttpRequestStatus>();
            long startTimeStamp = -1;
            bool keepTrying;
            int attempt = 0;
            if (this.HasTimeout)
            {
                startTimeStamp = Stopwatch.GetTimestamp();
            }
            do
            {
                try
                {
                    if (this.HasTimeout)
                    {
                        long taskDurationTicks = Stopwatch.GetTimestamp() - startTimeStamp;
                        long taskDurationMilliSecs = (long)Math.Round(((double)taskDurationTicks / Stopwatch.Frequency) * 1000.0);
                        long msecsLeft = this.Timeout.Millis - taskDurationMilliSecs;
                        if (msecsLeft < 0)
                        {
                            taskTimedOut = true;
                            break;
                        }
                        if (msecsLeft < this.RequestSettings.Timeout.Millis)
                        {
                            this.RequestSettings.Timeout = new Timeout((int)msecsLeft, TimeUnits.MILLIS);
                        }
                        Log.Debug("Task timeout in T-{0:N1} seconds", msecsLeft / 1000.0);
                    }
                    HttpRequestStatus status = this.GetRequestStatus(this.RequestSettings);
                    if (status.Success)
                    {
                        Log.Debug("Checking returned status code ({0}) against success status codes: {1}", ((int)status.StatusCode).ToString(), this.SuccessStatusCodes);
                        for (int i = 0; i < this.successStatusCodes.Length; i++)
                        {
                            if ((int)status.StatusCode == this.successStatusCodes[i])
                            {
                                Log.Debug("Success, exiting...");
                                taskResult.Success = true;
                                break;
                            }
                        }
                    }
                    resp.Add(status);
                }
                catch (Exception ex)
                {
                    throw new BuilderException(this, String.Format("An exception occured in HttpStatusTask while checking status for: '{0}'", this.RequestSettings.Uri), ex);
                }
                keepTrying = !taskResult.Success && (this.Retries == -1 || (this.Retries - attempt++) > 0);
                if (keepTrying)
                {
                    var delay = this.RetryDelay ?? new Timeout(5000);
                    if (delay.Millis > 0)
                    {
                        delay.Normalize();
                        Log.Debug("Retrying in {0}...", delay.ToString());
                        System.Threading.Thread.Sleep(delay.Millis);
                    }
                    else
                    {
                        Log.Debug("Retrying...");
                    }
                }
            }
            while (keepTrying);
            if (taskTimedOut)
            {
                throw new BuilderException(this, String.Format("HttpStatusTask timed out while checking status for: '{0}'", this.RequestSettings.Uri));
            }
            Log.Debug("Writing output...");
            XmlWriter writer = taskResult.GetWriter();
            writer.WriteStartElement("httpStatus");
            writer.WriteAttributeString("uri", this.RequestSettings.Uri.ToString());
            writer.WriteAttributeString("success", XmlConvert.ToString(taskResult.Success));
            if (!String.IsNullOrEmpty(this.Description))
            {
                writer.WriteElementString("description", this.Description);
            }
            foreach (HttpRequestStatus status in resp)
            {
                status.WriteTo(writer, this.IncludeContent);
            }
            writer.WriteEndElement();
            writer.Close();
            result.AddTaskResult(taskResult);
            Log.Debug("HttpStatusTask finished, return value: {0}", taskResult.Success.ToString());
            return taskResult.Success;
        }
        protected HttpRequestStatus GetRequestStatus(HttpRequestSettings settings)
        {
            HttpRequestStatus ret = new HttpRequestStatus() { Settings = settings, Success = false };
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(settings.Uri);
            HttpWebResponse response = null;
            if (settings.HasHeaders)
            {
                foreach (HttpRequestHeader header in settings.Headers)
                {
                    request.Headers.Set(header.Name, header.Value);
                }
            }
            if (settings.HasTimeout || settings.HasOverrideTimeout)
            {
                request.Timeout = (settings.OverrideTimeout ?? settings.Timeout).Millis;
            }
            if (settings.HasReadWriteTimeout)
            {
                request.ReadWriteTimeout = settings.ReadWriteTimeout.Millis;
            }
            request.Method = settings.Method;
            if (settings.HasCredentials)
            {
                request.Credentials = settings.Credentials;
            }
            else
            {
                request.UseDefaultCredentials = settings.UseDefaultCredentials;
            }
            ret.RequestTime = DateTime.Now;
            long start = Stopwatch.GetTimestamp();
            Timeout timeout = new Timeout(request.Timeout);
            timeout.Normalize();
            Timeout readWriteTimeout = new Timeout(request.ReadWriteTimeout);
            readWriteTimeout.Normalize();
            Log.Debug("Requesting uri (timeout: {0}, read/write-timeout: {1})...", timeout.ToString(), readWriteTimeout.ToString());
            try
            {
                if (settings.HasBody || settings.HasSendFile)
                {
                    using (Stream requestStream = request.GetRequestStream())
                    {
                        if (settings.HasBody)
                        {
                            using (TextWriter writer = new StreamWriter(requestStream, new UTF8Encoding(false, true)))
                            {
                                writer.Write(settings.Body);
                            }
                        }
                        else
                        {
                            using (FileStream inputFile = File.OpenRead(settings.SendFile))
                            {
                                int read;
                                byte[] buff = new byte[16384];
                                do
                                {
                                    read = inputFile.Read(buff, 0, buff.Length);
                                    requestStream.Write(buff, 0, read);
                                }
                                while (read > 0);
                            }
                        }
                        requestStream.Close();
                    }
                }
                response = (HttpWebResponse)request.GetResponse();
            }
            catch (WebException wEx)
            {
                ret.Exception = wEx;
                if (wEx.Status == WebExceptionStatus.Timeout)
                {
                    ret.TimedOut = true;
                }
                if (wEx.Response is HttpWebResponse)
                {
                    response = (HttpWebResponse)wEx.Response;
                }
            }
            if (response != null)
            {
                ret.StatusCode = response.StatusCode;
                ret.StatusDescription = response.StatusDescription;
                ret.ContentEncoding = response.ContentEncoding;
                ret.CharacterSet = response.CharacterSet;
                ret.ContentType = response.ContentType;
                ret.Headers = response.Headers;
                if (this.IncludeContent)
                {
                    try
                    {
                        using (Stream responseStream = response.GetResponseStream())
                        {
                            try
                            {
                                ret.ResponseEncoding = Encoding.GetEncoding(response.CharacterSet);
                            }
                            catch (ArgumentException)
                            {
                                ret.ResponseEncoding = null;
                            }
                            if (ret.ResponseEncoding != null)
                            {
                                using (TextReader reader = new StreamReader(responseStream, ret.ResponseEncoding))
                                {
                                    ret.Content = reader.ReadToEnd();
                                }
                            }
                            else
                            {
                                using (MemoryStream memBuffer = new MemoryStream())
                                {
                                    int read;
                                    byte[] buff = new byte[512];
                                    do
                                    {
                                        read = responseStream.Read(buff, 0, buff.Length);
                                        memBuffer.Write(buff, 0, read);
                                    }
                                    while (read > 0);
                                    if (memBuffer.Length > 0)
                                    {
                                        ret.ContentIsBase64Encoded = true;
                                        ret.Content = Convert.ToBase64String(
                                            memBuffer.ToArray(),
                                            Base64FormattingOptions.InsertLineBreaks);
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        if (ret.Exception == null)
                        {
                            ret.Exception = ex;
                        }
                        ret.Content = null;
                    }
                }
                ret.Success = true;
            }
            ret.Duration = TimeSpan.FromSeconds((double)(Stopwatch.GetTimestamp() - start) / Stopwatch.Frequency);
            return ret;
        }
        protected class HttpRequestStatus
        {
            public HttpRequestSettings Settings { get; set; }
            public string Content { get; set; }
            public HttpStatusCode StatusCode { get; set; }
            public string StatusDescription { get; set; }
            public bool TimedOut { get; set; }
            public bool Success { get; set; }
            public bool ContentIsBase64Encoded { get; set; }
            public TimeSpan Duration { get; set; }
            public DateTime RequestTime { get; set; }
            public Encoding ResponseEncoding { get; set; }
            public Exception Exception { get; set; }
            public string ContentEncoding { get; set; }
            public string CharacterSet { get; set; }
            public string ContentType { get; set; }
            public WebHeaderCollection Headers { get; set; }
            public void WriteTo(XmlWriter writer, bool writeContent)
            {
                writer.WriteStartElement("httpRequest");
                writer.WriteAttributeString("requestTime", XmlConvert.ToString(this.RequestTime, XmlDateTimeSerializationMode.Local));
                writer.WriteAttributeString("duration", XmlConvert.ToString(this.Duration.TotalSeconds));
                writer.WriteAttributeString("success", XmlConvert.ToString(this.Success));
                if (!this.Success)
                {
                    string reason;
                    if (this.TimedOut)
                    {
                        reason = "timeout";
                    }
                    else if (this.Exception != null)
                    {
                        reason = "exception";
                    }
                    else
                    {
                        reason = this.StatusDescription;
                    }
                    writer.WriteAttributeString("reason", reason);
                }
                else
                {
                    writer.WriteAttributeString("statusCode", XmlConvert.ToString((int)this.StatusCode));
                }
                if (this.Headers != null)
                {
                    for (int i = 0; i < this.Headers.Count; i++)
                    {
                        writer.WriteStartElement("header");
                        writer.WriteAttributeString("name", this.Headers.GetKey(i));
                        writer.WriteString(this.Headers[i]);
                        writer.WriteEndElement();
                    }
                }
                if (writeContent && this.Content != null)
                {
                    writer.WriteStartElement("content");
                    writer.WriteAttributeString("type", this.ContentType);
                    writer.WriteAttributeString("encoding", this.ContentEncoding);
                    writer.WriteAttributeString("isBase64Encoded", XmlConvert.ToString(this.ContentIsBase64Encoded));
                    writer.WriteString(this.Content);
                    writer.WriteEndElement();
                }
                writer.WriteEndElement();
            }
        }
    }
}

namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Net;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("httpRequest")]
    public class HttpRequestSettings
    {
        public HttpRequestSettings()
        {
            this.Method = "GET";
        }
        [ReflectorProperty("method", Required = false)]
        public string Method { get; set; }
        public bool HasMethod
        {
            get
            {
                return !StringUtil.IsWhitespace(this.Method);
            }
        }
        [ReflectorArray("headers", Required = false)]
        public HttpRequestHeader[] Headers { get; set; }
        [ReflectorProperty("body", Required = false)]
        public string Body { get; set; }
        public bool HasBody
        {
            get
            {
                return !StringUtil.IsWhitespace(this.Body);
            }
        }
        public bool HasSendFile
        {
            get
            {
                return !StringUtil.IsWhitespace(this.SendFile);
            }
        }
        [ReflectorProperty("sendFile", Required = false)]
        public string SendFile { get; set; }
        [ReflectorProperty("uri", typeof(UriSerializerFactory), Required = true)]
        public Uri Uri { get; set; }
        [ReflectorProperty("timeout", typeof(TimeoutSerializerFactory), Required = false)]
        public Timeout Timeout { get; set; }
        public Timeout OverrideTimeout { get; set; }
        public bool HasTimeout
        {
            get { return this.Timeout != null; }
        }
        [ReflectorProperty("readWriteTimeout", typeof(TimeoutSerializerFactory), Required = false)]
        public Timeout ReadWriteTimeout { get; set; }
        public bool HasReadWriteTimeout
        {
            get { return this.ReadWriteTimeout != null; }
        }
        [ReflectorProperty("credentials", typeof(NetworkCredentialSerializerFactory), Required = false)]
        public NetworkCredential Credentials { get; set; }
        public bool HasCredentials
        {
            get { return this.Credentials != null; }
        }
        public bool HasHeaders
        {
            get { return (this.Headers != null) && (this.Headers.Length > 0); }
        }
        [ReflectorProperty("useDefaultCredentials", Required = false)]
        public bool UseDefaultCredentials { get; set; }
        public bool HasOverrideTimeout
        {
            get { return this.OverrideTimeout != null; }
        }
    }
}

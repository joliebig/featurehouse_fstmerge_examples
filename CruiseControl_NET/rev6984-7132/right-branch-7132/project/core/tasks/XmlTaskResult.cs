namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.IO;
    using System.Text;
    using System.Xml;
    public class XmlTaskResult : ITaskResult
    {
        public bool Success { get; set; }
        public string Data
        {
            get
            {
                if (this.CachedData != null)
                {
                    return this.CachedData;
                }
                if (this.Writer.WriteState != WriteState.Closed)
                {
                    this.Writer.Close();
                }
                this.CachedData = Encoding.UTF8.GetString(this.BackingStream.ToArray());
                this.BackingStream.Dispose();
                this.Writer = null;
                this.BackingStream = null;
                return this.CachedData;
            }
        }
        protected MemoryStream BackingStream { get; set; }
        protected XmlWriter Writer { get; set; }
        protected string CachedData { get; set; }
        public XmlWriter GetWriter()
        {
            if (this.CachedData != null)
            {
                throw new InvalidOperationException("Result already written.");
            }
            if (this.Writer == null)
            {
                this.BackingStream = new MemoryStream();
                var settings = new XmlWriterSettings() { Indent = true, CloseOutput = false, ConformanceLevel = ConformanceLevel.Fragment, OmitXmlDeclaration = true };
                this.Writer = XmlWriter.Create(this.BackingStream, settings);
            }
            return this.Writer;
        }
        public bool CheckIfSuccess()
        {
            return this.Success;
        }
    }
}

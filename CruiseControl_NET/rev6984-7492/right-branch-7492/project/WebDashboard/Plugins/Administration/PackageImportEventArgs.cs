using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Administration
{
    public class PackageImportEventArgs
        : EventArgs
    {
        private readonly TraceLevel level;
        private readonly string message;
        public PackageImportEventArgs(string message, TraceLevel level)
        {
            this.message = message;
            this.level = level;
        }
        public string Message
        {
            get { return message; }
        }
        public TraceLevel Level
        {
            get { return level; }
        }
    }
}

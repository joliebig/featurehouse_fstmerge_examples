using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Core.Config
{
    public interface IConfigurationErrorProcesser
    {
        void ProcessError(string message);
        void ProcessError(string message, params object[] args);
        void ProcessError(Exception error);
        void ProcessWarning(string message);
        void ProcessWarning(string message, params object[] args);
        bool ProcessUnhandledNode(XmlNode node);
    }
}

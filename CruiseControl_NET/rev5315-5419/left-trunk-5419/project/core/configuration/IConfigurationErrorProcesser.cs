using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Core.Config
{
    public interface IConfigurationErrorProcesser
    {
        void ProcessError(string message);
        void ProcessError(Exception error);
        void ProcessWarning(string message);
        bool ProcessUnhandledNode(XmlNode node);
    }
}

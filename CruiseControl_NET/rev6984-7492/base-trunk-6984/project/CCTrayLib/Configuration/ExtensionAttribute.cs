using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Configuration
{
    public class ExtensionAttribute
        : Attribute
    {
        private string displayName;
        public string DisplayName
        {
            get { return this.displayName; }
            set { this.displayName = value; }
        }
    }
}

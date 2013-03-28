using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.WebDashboard.Configuration
{
 [ReflectorType("stylesheet")]
    public class StylesheetConfiguration
    {
        private string location;
        private string name;
        private bool isDefault;
        [ReflectorProperty("location")]
        public string Location
        {
            get { return location; }
            set { location = value; }
        }
        [ReflectorProperty("name", Required = false)]
        public string Name
        {
            get { return name; }
            set { name = value; }
        }
        [ReflectorProperty("default", Required = false)]
        public bool IsDefault
        {
            get { return isDefault; }
            set { isDefault = value; }
        }
    }
}

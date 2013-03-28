using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote
{
    [Serializable]
    [XmlRoot("projectStatusSnapshot")]
    public class ProjectStatusSnapshot
        : ItemStatus
    {
        private DateTime timeOfSnapshot = DateTime.Now;
        [XmlElement("timeOfSnapshot")]
        public DateTime TimeOfSnapshot
        {
            get { return timeOfSnapshot; }
            set { timeOfSnapshot = value; }
        }
        public new ProjectStatusSnapshot Clone()
        {
            ProjectStatusSnapshot clone = new ProjectStatusSnapshot();
            CopyTo(clone);
            return clone;
        }
    }
}

using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Remote
{
    [Serializable]
    [XmlRoot("itemStatus")]
    public class ItemStatus
    {
        private readonly Guid uniqueId = Guid.NewGuid();
        private Guid identifier = Guid.NewGuid();
        private string name;
        private ItemBuildStatus status = ItemBuildStatus.Unknown;
        private DateTime? timeStarted;
        private DateTime? timeCompleted;
        private DateTime? timeOfEstimatedCompletion;
        private string description;
        private List<ItemStatus> childItems = new List<ItemStatus>();
        private ItemStatus parent;
        public ItemStatus()
        {
        }
        public ItemStatus(string name)
        {
            this.name = name;
        }
        [XmlAttribute("identifier")]
        public Guid Identifier
        {
            get { return identifier; }
        }
        [XmlAttribute("name")]
        public string Name
        {
            get { return name; }
            set { name = value; }
        }
        [XmlElement("description")]
        public string Description
        {
            get { return description; }
            set { description = value; }
        }
        [XmlElement("error")]
        public string Error { get; set; }
        [XmlAttribute("status")]
        public ItemBuildStatus Status
        {
            get { return status; }
            set { status = value; }
        }
        [XmlElement("timeStarted")]
        public DateTime? TimeStarted
        {
            get { return timeStarted; }
            set { timeStarted = value; }
        }
        [XmlElement("timeCompleted")]
        public DateTime? TimeCompleted
        {
            get { return timeCompleted; }
            set { timeCompleted = value; }
        }
        [XmlElement("timeOfEstimatedCompletion")]
        public DateTime? TimeOfEstimatedCompletion
        {
            get { return timeOfEstimatedCompletion; }
            set { timeOfEstimatedCompletion = value; }
        }
        [XmlArray("childItems")]
        [XmlArrayItem("childItem")]
        public List<ItemStatus> ChildItems
        {
            get { return childItems; }
        }
        [XmlIgnore()]
        public ItemStatus Parent
        {
            get { return parent; }
            set { parent = value; }
        }
        public void AddChild(ItemStatus child)
        {
            child.parent = this;
            childItems.Add(child);
        }
        public virtual ItemStatus Clone()
        {
            ItemStatus clone = new ItemStatus();
            CopyTo(clone);
            return clone;
        }
        public virtual void CopyTo(ItemStatus value)
        {
            value.identifier = this.identifier;
            value.description = this.description;
            value.name = this.name;
            value.status = this.status;
            value.Error = this.Error;
            value.timeCompleted = this.timeCompleted;
            value.timeOfEstimatedCompletion = this.timeOfEstimatedCompletion;
            value.timeStarted = this.timeStarted;
            foreach (ItemStatus item in this.childItems)
            {
                value.AddChild(item.Clone());
            };
        }
        public override int GetHashCode()
        {
            return identifier.GetHashCode();
        }
        public override bool Equals(object obj)
        {
            if (obj is ItemStatus)
            {
                bool areEqual = identifier.Equals((obj as ItemStatus).identifier);
                return areEqual;
            }
            else
            {
                return false;
            }
        }
        public override string ToString()
        {
            XmlSerializer serialiser = new XmlSerializer(this.GetType());
            StringBuilder builder = new StringBuilder();
            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Encoding = UTF8Encoding.UTF8;
            settings.Indent = false;
            settings.OmitXmlDeclaration = true;
            XmlWriter writer = XmlWriter.Create(builder, settings);
            serialiser.Serialize(writer, this);
            return builder.ToString();
        }
    }
}

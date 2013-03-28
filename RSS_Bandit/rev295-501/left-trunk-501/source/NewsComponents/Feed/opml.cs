using System.Xml;
using System.Xml.Serialization;
namespace NewsComponents.Feed
{
    public class opmlhead
    {
        private string titleField;
        public string title
        {
            get
            {
                return this.titleField;
            }
            set
            {
                this.titleField = value;
            }
        }
    }
    public class opmloutline
    {
        private opmloutline[] outlineField;
        private string titleField;
        private string textField;
        private string idField;
        private string xmlUrlField;
        private string htmlUrlField;
        private string syncXmlUrlField;
        private string folderIdField;
        private bool unseenField;
        private bool unseenFieldSpecified;
        private bool unreadField;
        private bool unreadFieldSpecified;
        private bool privateField;
        private bool privateFieldSpecified;
        private bool checkedByDefaultField;
        private bool checkedByDefaultFieldSpecified;
        private bool inStarterPackField;
        private bool inStarterPackFieldSpecified;
        private string starterPackOrderField;
        public opmloutline()
        {
            this.unseenField = true;
            this.unreadField = true;
            this.privateField = false;
        }
        [System.Xml.Serialization.XmlElementAttribute("outline")]
        public opmloutline[] outline
        {
            get
            {
                return this.outlineField;
            }
            set
            {
                this.outlineField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string text
        {
            get
            {
                return this.textField;
            }
            set
            {
                this.textField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string title
        {
            get
            {
                return this.titleField;
            }
            set
            {
                this.titleField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://newsgator.com/schema/opml")]
        public string id
        {
            get
            {
                return this.idField;
            }
            set
            {
                this.idField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string xmlUrl
        {
            get
            {
                return this.xmlUrlField;
            }
            set
            {
                this.xmlUrlField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string htmlUrl
        {
            get
            {
                return this.htmlUrlField;
            }
            set
            {
                this.htmlUrlField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string syncXmlUrl
        {
            get
            {
                return this.syncXmlUrlField;
            }
            set
            {
                this.syncXmlUrlField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://newsgator.com/schema/opml")]
        public string folderId
        {
            get
            {
                return this.folderIdField;
            }
            set
            {
                this.folderIdField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://newsgator.com/schema/opml")]
        [System.ComponentModel.DefaultValueAttribute(true)]
        public bool unseen
        {
            get
            {
                return this.unseenField;
            }
            set
            {
                this.unseenField = value;
            }
        }
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool unseenSpecified
        {
            get
            {
                return this.unseenFieldSpecified;
            }
            set
            {
                this.unseenFieldSpecified = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://newsgator.com/schema/opml")]
        [System.ComponentModel.DefaultValueAttribute(true)]
        public bool unread
        {
            get
            {
                return this.unreadField;
            }
            set
            {
                this.unreadField = value;
            }
        }
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool unreadSpecified
        {
            get
            {
                return this.unreadFieldSpecified;
            }
            set
            {
                this.unreadFieldSpecified = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://newsgator.com/schema/opml")]
        [System.ComponentModel.DefaultValueAttribute(false)]
        public bool @private
        {
            get
            {
                return this.privateField;
            }
            set
            {
                this.privateField = value;
            }
        }
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool privateSpecified
        {
            get
            {
                return this.privateFieldSpecified;
            }
            set
            {
                this.privateFieldSpecified = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://newsgator.com/schema/opml")]
        public bool checkedByDefault
        {
            get
            {
                return this.checkedByDefaultField;
            }
            set
            {
                this.checkedByDefaultField = value;
            }
        }
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool checkedByDefaultSpecified
        {
            get
            {
                return this.checkedByDefaultFieldSpecified;
            }
            set
            {
                this.checkedByDefaultFieldSpecified = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://newsgator.com/schema/opml")]
        public bool inStarterPack
        {
            get
            {
                return this.inStarterPackField;
            }
            set
            {
                this.inStarterPackField = value;
            }
        }
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool inStarterPackSpecified
        {
            get
            {
                return this.inStarterPackFieldSpecified;
            }
            set
            {
                this.inStarterPackFieldSpecified = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://newsgator.com/schema/opml", DataType = "integer")]
        public string starterPackOrder
        {
            get
            {
                return this.starterPackOrderField;
            }
            set
            {
                this.starterPackOrderField = value;
            }
        }
    }
    public partial class opmlbody
    {
        private opmloutline[] outlineField;
        [System.Xml.Serialization.XmlElementAttribute("outline")]
        public opmloutline[] outline
        {
            get
            {
                return this.outlineField;
            }
            set
            {
                this.outlineField = value;
            }
        }
    }
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true)]
    public class opml
    {
        [System.Xml.Serialization.XmlNamespaceDeclarations]
        public XmlSerializerNamespaces xmlns = new XmlSerializerNamespaces(new XmlQualifiedName[]{ new XmlQualifiedName("ng","http://newsgator.com/schema/opml") } ) ;
        private opmlhead headField;
        private opmloutline[] bodyField;
        public opmlhead head
        {
            get
            {
                return this.headField;
            }
            set
            {
                this.headField = value;
            }
        }
        [System.Xml.Serialization.XmlArrayItemAttribute("outline", IsNullable = false)]
        public opmloutline[] body
        {
            get
            {
                return this.bodyField;
            }
            set
            {
                this.bodyField = value;
            }
        }
    }
}

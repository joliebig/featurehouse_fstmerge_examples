using System.Xml;
using System.Xml.Schema;
namespace ThoughtWorks.CruiseControl.Core.Config
{
    public class XmlValidatingLoader
    {
        private readonly XmlReader innerReader;
        private XmlReaderSettings xmlReaderSettings;
        private bool valid;
        public XmlValidatingLoader(XmlReader innerReader)
        {
            this.innerReader = innerReader;
            if ( innerReader is XmlTextReader )
            {
                ((XmlTextReader)(innerReader)).EntityHandling = EntityHandling.ExpandEntities;
            }
            xmlReaderSettings = new XmlReaderSettings();
            xmlReaderSettings.ValidationType = ValidationType.None;
            xmlReaderSettings.ProhibitDtd = false;
            xmlReaderSettings.XmlResolver = new XmlUrlResolver();
            xmlReaderSettings.ConformanceLevel = ConformanceLevel.Auto;
            xmlReaderSettings.ValidationEventHandler += ValidationHandler;
        }
        public event ValidationEventHandler ValidationEventHandler
        {
            add { xmlReaderSettings.ValidationEventHandler += value; }
            remove { xmlReaderSettings.ValidationEventHandler -= value; }
        }
        public void AddSchema(XmlSchema schema)
        {
            xmlReaderSettings.Schemas.Add(schema);
            xmlReaderSettings.ValidationType = ValidationType.Schema;
        }
        public XmlDocument Load()
        {
            lock (this)
            {
                valid = true;
                using (XmlReader reader = XmlReader.Create(innerReader, xmlReaderSettings))
                {
                    try
                    {
                        XmlDocument doc = new XmlDocument();
                        doc.XmlResolver = new XmlUrlResolver();
                        doc.Load(reader);
                        return valid ? doc : null;
                    }
                    finally
                    {
                        valid = true;
                    }
                }
            }
        }
        private void ValidationHandler(object sender, ValidationEventArgs args)
        {
            valid = false;
        }
    }
}

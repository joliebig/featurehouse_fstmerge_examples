using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
using Exortech.NetReflector.Util;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class NameValuePairSerialiser
        : XmlMemberSerialiser
    {
        private bool isList = false;
        public NameValuePairSerialiser(ReflectorMember info, ReflectorPropertyAttribute attribute, bool isList)
            : base(info, attribute)
        {
            this.isList = isList;
        }
  public override object Read(XmlNode node, NetReflectorTypeTable table)
        {
            if (isList)
            {
                return ReadList(node);
            }
            else
            {
                var value = ReadValue(node as XmlElement);
                return value;
            }
        }
        public override void Write(XmlWriter writer, object target)
        {
            if (isList)
            {
                var list = target as NameValuePair[];
                if (list != null)
                {
                    writer.WriteStartElement(base.Attribute.Name);
                    foreach (var value in list)
                    {
                        WriteValue(writer, value, "value");
                    }
                    writer.WriteEndElement();
                }
            }
            else
            {
                var value = (target as NameValuePair);
                if (value != null)
                {
                    WriteValue(writer, value, base.Attribute.Name);
                }
            }
        }
        private object ReadList(XmlNode node)
        {
            var valueList = new List<NameValuePair>();
            if (node != null)
            {
                if (node.Attributes.Count > 0)
                {
                    throw new NetReflectorException(string.Concat("A name/value pair list cannot directly contain attributes.", Environment.NewLine, "XML: ", node.OuterXml));
                }
                var subNodes = node.SelectNodes("*");
                if (subNodes != null)
                {
                    foreach (XmlElement valueElement in subNodes)
                    {
                        if (valueElement.Name == "value")
                        {
                            var newValue = ReadValue(valueElement);
                            valueList.Add(newValue);
                        }
                        else
                        {
                            throw new NetReflectorException(string.Concat(valueElement.Name, " is not a valid sub-item.",
                                                                          Environment.NewLine, "XML: ", valueElement.OuterXml));
                        }
                    }
                }
            }
            return valueList.ToArray();
        }
        private NameValuePair ReadValue(XmlElement valueElement)
        {
            var fileSubNodes = valueElement.SelectNodes("*");
            if (fileSubNodes != null && fileSubNodes.Count > 0)
            {
                throw new NetReflectorException(string.Concat("value elements cannot contain any sub-items.", Environment.NewLine, "XML: ", valueElement.OuterXml));
            }
            var newValue = new NameValuePair();
            newValue.Value = valueElement.InnerText;
            newValue.Name = valueElement.GetAttribute("name"); ;
            return newValue;
        }
        private void WriteValue(XmlWriter writer, NameValuePair value, string elementName)
        {
            writer.WriteStartElement(elementName);
            if (!string.IsNullOrEmpty(value.Name)) writer.WriteAttributeString("name", value.Name);
            writer.WriteString(value.Value);
            writer.WriteEndElement();
        }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml;
using System.Xml.Linq;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor
{
    public static class XHelpers
    {
        public static bool HasAttribute(this XElement element, XName attr_name)
        {
            return element.Attribute( attr_name ) != null;
        }
        public static XElement NextSiblingElement(this XElement element)
        {
            return element.ElementsAfterSelf().FirstOrDefault();
        }
        public static string ErrorContext(this XObject obj)
        {
            string obj_info = "";
            if ( obj is XAttribute )
            {
                obj_info = String.Format( "Attribute '{0}'", ( ( XAttribute ) obj ).Name );
            }
            else if ( obj is XElement )
            {
                obj_info = String.Format( "Element '{0}'", ( ( XElement ) obj ).Name );
            }
            IXmlLineInfo line_info = obj;
            string line_and_pos = "line and position unknown";
            if ( line_info.HasLineInfo() )
            {
                line_and_pos = String.Format( "line {0}, pos {1}", line_info.LineNumber,
                                              line_info.LinePosition );
            }
            return String.Format( "File: {0} ({1}:{2})", obj.BaseUri, line_and_pos, obj_info );
        }
        public static string GetAttributeValue(this XElement element, XName attr_name)
        {
            XAttribute attr = element.Attribute( attr_name );
            return attr == null ? "" : attr.Value;
        }
        public static string GetTextValue(this IEnumerable< XNode > nodes)
        {
            return String.Concat( nodes.Select< XNode, String >( _ValueOf ).ToArray() );
        }
        private static string _ValueOf(XNode node)
        {
            switch ( node.NodeType )
            {
                case XmlNodeType.Element:
                    return ( ( XElement ) node ).Value;
                case XmlNodeType.Text:
                    return ( ( XText ) node ).Value;
                case XmlNodeType.Comment:
                case XmlNodeType.ProcessingInstruction:
                    return String.Empty;
                default:
                    throw new InvalidOperationException(
                        String.Format( "{0} Unhandled node type {1}",
                                       node.ErrorContext(),
                                       node.NodeType ) );
            }
        }
    }
    public static class XmlNs
    {
        public static readonly XNamespace PreProcessor = XNamespace.Get("urn:ccnet.config.builder");
    }
}

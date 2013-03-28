using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor
{
    internal static class Validation
    {
        public static void RequireAttributes(XElement element, params XName[] attr_names)
        {
            IEnumerable< XName > missing_attrs =
                attr_names.Where( attr_name => element.Attribute( attr_name ) == null );
            if ( missing_attrs.Any() )
            {
                string[] attr_list = attr_names.Select( n => n.ToString() ).ToArray();
                throw InvalidMarkupException.CreateException(
                    "{0} Element '{1}' does not have required attribute(s) '{2}'",
                    element.ErrorContext(), element.Name,
                    String.Join( ",", attr_list ) );
            }
        }
    }
}

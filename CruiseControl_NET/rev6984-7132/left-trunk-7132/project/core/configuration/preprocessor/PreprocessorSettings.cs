using System;
using System.Collections.Generic;
using System.Xml.Linq;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor
{
    public class PreprocessorSettings
    {
        public bool ExplicitDeclarationRequired;
        public IDictionary< string, string > InitialDefinitions;
        public bool NamesAreCaseSensitve;
        public bool UseOsEnvironment = true;
        public XNamespace Namespace = XmlNs.PreProcessor;
        public bool IgnoreWhitespace;
    }
}

using System.Collections.Generic;
using System.Xml.Linq;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor.ElementProcessors
{
    internal class IncludeProcessor : ElementProcessor
    {
        public IncludeProcessor(PreprocessorEnvironment env)
            : base(env._Settings.Namespace.GetName("include"), env)
        {
        }
        public override IEnumerable< XNode > Process(XNode node)
        {
            XElement element = _AssumeElement( node );
            Validation.RequireAttributes( element, AttrName.Href );
            string href =
                _ProcessText( ( string ) element.Attribute( AttrName.Href ) ).GetTextValue().Trim();
            XContainer doc = _Env.PushInclude( href );
            try
            {
                return _ProcessNodes( doc.Nodes() );
            }
            finally
            {
                _Env.PopInclude();
            }
        }
    }
}

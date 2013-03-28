using System.Collections.Generic;
using System.Xml.Linq;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor.ElementProcessors
{
    internal class DefineProcessor : ElementProcessor
    {
        public DefineProcessor(PreprocessorEnvironment env)
            : base(env._Settings.Namespace.GetName("define"), env)
        {
        }
        public override IEnumerable< XNode > Process(XNode node)
        {
            XElement element = _AssumeElement( node );
            if ( element.HasAttribute( AttrName.Name ) )
            {
                _Env.DefineNodesetSymbol( ( string ) element.Attribute( AttrName.Name ),
                                          element.Nodes() );
            }
            else if ( element.HasAttributes )
            {
                _DefineFromAttributes( element );
            }
            else
            {
                throw DefinitionException.CreateException(
                    "{0} <define> element has no attributes.",
                    node.ErrorContext() );
            }
            return new XNode[] {};
        }
    }
}

using System.Collections.Generic;
using System.Xml.Linq;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor.ElementProcessors
{
    internal abstract class ConditionalProcessor : ElementProcessor
    {
        protected ConditionalProcessor(XName target_element_name, PreprocessorEnvironment env)
            : base( target_element_name, env )
        {
        }
        protected IEnumerable< XNode > _ProcessConditional(XElement conditional_element,
                                                           bool condition)
        {
            return condition
                       ? _ProcessNodes( conditional_element.Nodes() )
                       : _ProcessNextElse( conditional_element );
        }
        private IEnumerable< XNode > _ProcessNextElse(XElement element)
        {
            XElement next_element = element.NextSiblingElement();
            if ( next_element != null && next_element.Name == _Env._Settings.Namespace.GetName("else" ) )
            {
                return _ProcessNodes( next_element.Nodes() );
            }
            return new XNode[] {};
        }
    }
}

using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor.ElementProcessors
{
    internal class ExpandSymbolProcessor : ElementProcessor
    {
        public ExpandSymbolProcessor(PreprocessorEnvironment env)
            : base( env._Settings.Namespace.GetName("_Unused_Name_"), env)
        {
        }
        public override IEnumerable< XNode > Process(XNode node)
        {
            XElement element = _AssumeElement( node );
            string symbol_name = element.Name.LocalName;
            if ( !_Env.IsDefined( symbol_name ) )
                throw InvalidMarkupException.CreateException( "[{0}] Undefined symbol '{1}'   ",
                                                              node.ErrorContext(),
                                                              element.Name );
            return _Env.Call( () =>
                                  {
                                      _DefineFromAttributes( element );
                                      _ProcessNodes(
                                          element.Elements( _Env._Settings.Namespace.GetName("define") ).Select
                                              ( n => ( XNode ) n ) );
                                      return _Env.EvalSymbol( symbol_name ).ToArray();
                                  } );
        }
    }
}

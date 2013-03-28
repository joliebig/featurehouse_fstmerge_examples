using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Xml.Linq;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor.ElementProcessors
{
    internal class ForProcessor : ElementProcessor
    {
        public ForProcessor(PreprocessorEnvironment env)
            : base(env._Settings.Namespace.GetName("for"), env)
        {
        }
        public override IEnumerable< XNode > Process(XNode node)
        {
            XElement element = _AssumeElement( node );
            Validation.RequireAttributes( element, AttrName.CounterName, AttrName.InitExpr,
                                          AttrName.TestExpr, AttrName.CountExpr );
            string counter_name =
                _ProcessText( element.GetAttributeValue( AttrName.CounterName ) ).GetTextValue();
            string init_expr =
                _ProcessText( element.GetAttributeValue( AttrName.InitExpr ) ).GetTextValue();
            string test_expr = element.GetAttributeValue( AttrName.TestExpr );
            string count_expr = element.GetAttributeValue( AttrName.CountExpr );
            var generated_nodes = new List< XNode >();
            string current_expr = init_expr;
            int count = _ExprAsInt( current_expr );
            bool run = true;
            while ( run )
            {
                int count1 = count;
                XNode[] nodes = _Env.Call( () =>
                                               {
                                                   _Env.DefineTextSymbol( counter_name,
                                                                          count1.ToString(
                                                                              NumberFormatInfo.
                                                                                  InvariantInfo ) );
                                                   if (
                                                       !_Env.EvalBool(
                                                           _ProcessText( test_expr ).GetTextValue() ) )
                                                   {
                                                       run = false;
                                                       return new XNode[] {};
                                                   }
                                                   count1 =
                                                       _ExprAsInt(
                                                           _ProcessText( count_expr ).GetTextValue() );
                                                   return _ProcessNodes( element.Nodes() ).ToArray();
                                               } );
                count = count1;
                generated_nodes.AddRange( nodes );
            }
            return generated_nodes;
        }
        private int _ExprAsInt(string expr)
        {
            int val;
            if ( !Int32.TryParse( _Env.EvalExprAsString( expr ), out val ) )
            {
                throw new InvalidCastException(
                    String.Format( "Expression '{0}' does not evaluate to an integer", expr ) );
            }
            return val;
        }
    }
}

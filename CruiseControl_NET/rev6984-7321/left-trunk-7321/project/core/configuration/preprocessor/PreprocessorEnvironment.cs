using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Xml;
using System.Xml.Linq;
using ThoughtWorks.CruiseControl.Core.Config.Preprocessor.ElementProcessors;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor
{
    public class PreprocessorEnvironment
    {
        private static readonly Regex _symbol_ref_matcher = new Regex( @"([$!])\((.+?)\)",
                                                                       RegexOptions.Compiled );
        private readonly Stack< Dictionary< string, SymbolicDef > > _define_stack =
            new Stack< Dictionary< string, SymbolicDef > >();
        private readonly Dictionary< SymbolicDef, int > _evaluated_symbols =
            new Dictionary< SymbolicDef, int >();
        private readonly Dictionary< Uri, bool > _fileset = new Dictionary< Uri, bool >();
        private readonly Stack< Uri > _include_stack = new Stack< Uri >();
        private readonly XmlUrlResolver _resolver;
        internal PreprocessorSettings _Settings { get; private set;}
        public PreprocessorEnvironment(PreprocessorSettings settings, Uri input_file_path,
                                      XmlUrlResolver resolver)
        {
            _Settings = settings;
            _DefaultNodeProcessor = new DefaultProcessor( this );
            _define_stack.Push( new Dictionary< string, SymbolicDef >() );
            _include_stack.Push( input_file_path );
            _fileset[ input_file_path ] = true;
            _resolver = resolver;
            if ( _Settings.InitialDefinitions != null )
            {
                foreach ( var pair in _Settings.InitialDefinitions )
                {
                    _DefineTextSymbol( pair.Key, pair.Value, false );
                }
            }
        }
        public Uri[] Fileset
        {
            get
            {
                var array = new Uri[_fileset.Keys.Count];
                _fileset.Keys.CopyTo( array, 0 );
                return array;
            }
        }
        internal DefaultProcessor _DefaultNodeProcessor { get; private set; }
        private string _CanonicalizeName(string name)
        {
            name = _Settings.NamesAreCaseSensitve ? name : name.ToLowerInvariant();
            if ( _HasJsFlag( name ) )
            {
                name = name.Substring( 0, name.Length - 3 );
            }
            return name;
        }
        private void _CheckAlreadyDefined(string name)
        {
            if ( _IsDefinedInCurrentFrame( name ) )
            {
                Utils.ThrowException( EvaluationException.CreateException,
                                      "Symbol '{0}' already defined", new object[] {name} );
            }
        }
        private void _DefineTextSymbol(string name, string value, bool is_explicit)
        {
            if ( string.IsNullOrEmpty( name ) )
            {
                throw DefinitionException.CreateException(
                    "An attempt was made to define a nameless variable (value = '{0}'",
                    new object[] {value} );
            }
            _CheckAlreadyDefined( name );
            var symbolic_def = new SymbolicDef()
                                   {
                                       Name = name,
                                       Value = new[] {new XText( value )},
                                       IsExplicitlyDefined = is_explicit
                                   };
            _DefineSymbolOnStack( symbolic_def );
        }
        protected static IEnumerable< XNode > _GetAsNodeSet(IEnumerable values)
        {
        	object value;
            return
                ( from value in values select new XText( value.ToString() ) ).Cast< XNode >()
                    .ToList();
        }
        protected static IEnumerable< XNode > _GetAsNodeSet(params string[] values)
        {
            return _GetAsNodeSet( ( IEnumerable ) values );
        }
        internal SymbolicDef GetSymbolDef(string name)
        {
            if ( !_IsDefined( name ) )
            {
                Utils.ThrowException( EvaluationException.CreateException,
                                      "Reference to unknown symbol '{0}'", new object[] {name} );
            }
            SymbolicDef symbolic_def = _InternalGetSymbolDef( _CanonicalizeName( name ) );
            if ( _Settings.ExplicitDeclarationRequired && !symbolic_def.IsExplicitlyDefined )
            {
                Utils.ThrowException( ExplicitDefinitionRequiredException.CreateException,
                                      "Symbol '{0}' must be explicitly declared before usage.",
                                      new object[] {name} );
            }
            return symbolic_def;
        }
        private static bool _HasJsFlag(string name)
        {
            return name.EndsWith( ":js" );
        }
        private SymbolicDef _InternalGetSymbolDef(string symbol_name)
        {
            symbol_name = _CanonicalizeName( symbol_name );
            foreach ( var dictionary in _define_stack.ToArray() )
            {
                SymbolicDef symbolic_def;
                if ( dictionary.TryGetValue( symbol_name, out symbolic_def ) )
                {
                    return symbolic_def;
                }
            }
            string environment_variable = Environment.GetEnvironmentVariable( symbol_name );
            if ( environment_variable != null )
            {
                var symbolic_def2 = new SymbolicDef()
                                        {
                                            Name = symbol_name,
                                            Value = _StringToNodeSet( environment_variable )
                                        };
                return symbolic_def2;
            }
            return null;
        }
        private static IEnumerable< XNode > _StringToNodeSet(string value)
        {
            return new[] {new XText( value )};
        }
        private bool _IsDefined(string symbol_name)
        {
            return ( _InternalGetSymbolDef( symbol_name ) != null );
        }
        private bool _IsDefinedInCurrentFrame(string name)
        {
            return _define_stack.Peek().ContainsKey( _CanonicalizeName( name.ToLowerInvariant() ) );
        }
        private static bool _IsJScriptExpression(string input)
        {
            return ( input.StartsWith( "{" ) && input.EndsWith( "}" ) );
        }
        private void _DefineSymbolOnStack(SymbolicDef symbolic_def)
        {
            _define_stack.Peek().Add( _CanonicalizeName( symbolic_def.Name ), symbolic_def );
        }
        public void AddToFileset(Uri url)
        {
            _fileset[ url ] = true;
        }
        public IEnumerable< XNode > DefineNodesetSymbol(string name, IEnumerable< XNode > value)
        {
            _CheckAlreadyDefined( name );
            var sym = new SymbolicDef() {Name = name, Value = value, IsExplicitlyDefined = true};
            _DefineSymbolOnStack( sym );
            return new XNode[] {};
        }
        public IEnumerable< XNode > DefineTextSymbol(string name, string value)
        {
            _DefineTextSymbol( name, value, true );
            return new XNode[] {};
        }
        public bool EvalBool(string expr)
        {
            bool flag;
            try
            {
                flag = Evaluator.EvalToType< bool >( expr );
            }
            catch ( Exception exception )
            {
                throw EvaluationException.CreateException( expr, exception );
            }
            return flag;
        }
        public IEnumerable< XNode > EvalSymbol(string name)
        {
            string symbol_name = _CanonicalizeName( name );
            SymbolicDef symbol_def = GetSymbolDef( symbol_name );
            IEnumerable< XNode > val = symbol_def.Value;
            if ( _evaluated_symbols.ContainsKey( symbol_def ) )
            {
                IEnumerable< string > names =
                    _evaluated_symbols.OrderBy( def => def.Value ).Select( def => def.Key.Name );
                string eval_chain = String.Join( "->", names.ToArray() ) + "->" + symbol_name;
                throw EvaluationException.CreateException(
                    "Cyclical definition detected definiton: {0}",
                    eval_chain );
            }
            _evaluated_symbols.Add( symbol_def, _evaluated_symbols.Count );
            try
            {
                val = val.SelectMany<XNode,XNode>( _DefaultNodeProcessor.Process ).ToArray();
                if ( _HasJsFlag( name ) )
                {
                    val = _StringToNodeSet( Evaluator.StringAsLiteral( val.GetTextValue().Trim() ) );
                }
                return val;
            }
            finally
            {
                _evaluated_symbols.Remove( symbol_def );
            }
        }
        protected IEnumerable< XNode > _Process(IEnumerable< XNode > nodes)
        {
            IEnumerable< XNode > processed_val =
                nodes.SelectMany< XNode, XNode >( _DefaultNodeProcessor.Process );
            return processed_val;
        }
        public IEnumerable< XNode > EvalExpr(string expr)
        {
            IEnumerable< XNode > iterator;
            try
            {
                object obj2 = Evaluator.EvalToObject( expr );
                if ( !( obj2 is string ) && ( obj2 is IEnumerable ) )
                {
                    return _GetAsNodeSet( ( IEnumerable ) obj2 );
                }
                iterator = _GetAsNodeSet( new[] {obj2.ToString()} );
            }
            catch ( EvaluationException )
            {
                throw;
            }
            catch ( Exception exception )
            {
                throw EvaluationException.CreateException( expr, exception );
            }
            return iterator;
        }
        public string EvalExprAsString(string expr)
        {
            string str;
            try
            {
                object obj2 = Evaluator.EvalToObject( expr );
                if ( !( obj2 is string ) && ( obj2 is IEnumerable ) )
                {
                    var builder = new StringBuilder();
                    foreach ( object obj3 in ( IEnumerable ) obj2 )
                    {
                        builder.Append( obj3 );
                    }
                    return builder.ToString();
                }
                str = obj2.ToString();
            }
            catch ( Exception exception )
            {
                throw EvaluationException.CreateException( expr, exception );
            }
            return str;
        }
        private static bool _IsPureText(IEnumerable< XNode > nodes)
        {
            return nodes.Count( node => node is XText ) == nodes.Count();
        }
        public IEnumerable< XNode > EvalTextSymbols(string input)
        {
            bool is_jscript = _IsJScriptExpression( input );
            if ( is_jscript )
            {
                input = input.Substring( 1, input.Length - 2 );
            }
            IEnumerable< XNode > node_set;
            MatchCollection reference_matches = _symbol_ref_matcher.Matches( input );
            var nodes = new List< XNode >();
            int idx = 0;
            foreach ( Match match in reference_matches )
            {
                if ( idx < match.Index )
                {
                    nodes.Add( new XText( input.Substring( idx, match.Index - idx ) ) );
                }
                string ref_name = match.Groups[ 2 ].Value;
                IEnumerable< XNode > match_val = EvalSymbol( ref_name );
                if ( _IsPureText( match_val ) )
                {
                    string match_val_text = match_val.GetTextValue().Trim();
                    if ( _IsJScriptExpression( match_val_text ) )
                    {
                        match_val =
                            EvalExpr( match_val_text.Substring( 1, match_val_text.Length - 2 ) );
                    }
                }
                nodes.AddRange( match_val );
                idx = match.Index + match.Length;
            }
            if ( idx < input.Length )
            {
                nodes.Add( new XText( input.Substring( idx ) ) );
            }
            node_set = nodes;
            return is_jscript ? EvalExpr( node_set.GetTextValue() ) : node_set;
        }
        public bool IsDefined(string symbol_name)
        {
            return _IsDefined( symbol_name );
        }
        public IEnumerable< XNode > PopCall()
        {
            _define_stack.Pop();
            return new XNode[] {};
        }
        public IEnumerable< XNode > PopInclude()
        {
            _include_stack.Pop();
            return new XNode[] {};
        }
        public T Call< T >(Func< T > func)
        {
            PushCall();
            try
            {
                return func();
            }
            finally
            {
                PopCall();
            }
        }
        public IEnumerable< XNode > PushCall()
        {
            _define_stack.Push( new Dictionary< string, SymbolicDef >() );
            return new XNode[] {};
        }
        public XContainer PushInclude(string href)
        {
            Uri base_url = _include_stack.Peek();
            Uri url = _resolver.ResolveUri( base_url, href );
            AddToFileset( url );
            using (
                XmlReader reader =
                    XmlReader.Create( ( Stream ) _resolver.GetEntity( url, null, typeof ( Stream ) ) )
                )
            {
                XDocument document = XDocument.Load( reader,
                                                     LoadOptions.SetLineInfo |
                                                     LoadOptions.SetBaseUri |
                                                     LoadOptions.PreserveWhitespace );
                _include_stack.Push( url );
                return document;
            }
        }
        public void ThrowInvalidMarkup(string message)
        {
            throw new InvalidMarkupException( message );
        }
    }
}

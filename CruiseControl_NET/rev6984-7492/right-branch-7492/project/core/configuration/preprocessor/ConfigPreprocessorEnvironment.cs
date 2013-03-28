using System;
using System.Collections.Generic;
using System.IO;
using System.Runtime.Serialization;
using System.Text;
using System.Text.RegularExpressions;
using System.Xml;
using System.Xml.XPath;
using ThoughtWorks.CruiseControl.Core.Util;
using ConstantDict = System.Collections.Generic.Dictionary<string,ThoughtWorks.CruiseControl.Core.Config.Preprocessor.Constant>;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor
{
    internal class ConfigPreprocessorEnvironment
    {
        private readonly Stack<ConstantDict> _env_stack = new Stack< ConstantDict >();
        private readonly Dictionary<string,bool> _fileset = new Dictionary< string, bool >( );
        private static readonly Regex _const_ref_matcher =
            new Regex(@"\$\(.+?\)", RegexOptions.Compiled);
        private readonly Stack<string> _eval_stack = new Stack<string>();
        private readonly Stack< Uri > _include_stack = new Stack< Uri >();
        private readonly PreprocessorUrlResolver _resolver;
        private readonly XmlDocument _utility_doc = new XmlDocument();
        public ConfigPreprocessorEnvironment( Uri input_file_path, PreprocessorUrlResolver resolver )
        {
            _env_stack.Push( new ConstantDict() );
            _include_stack.Push( input_file_path );
            _fileset[ input_file_path.LocalPath ] = true;
            _resolver = resolver;
        }
        public void AddToFileset (Uri url)
        {
            _fileset[url.LocalPath] = true;
        }
        public string[] Fileset
        {
            get
            {
                string[] docs = new string[_fileset.Keys.Count];
                _fileset.Keys.CopyTo( docs, 0 );
                return docs;
            }
        }
        public void define_text_constant(string name, string value)
        {
            _CheckAlreadyDefined(name);
            Constant const_def = new Constant();
            const_def.Name = name;
            const_def.Value = value;
            _SetConstant(const_def);
        }
        public void define_nodeset_constant(string name, XPathNodeIterator value)
        {
            _CheckAlreadyDefined(name);
            Constant const_def = new Constant();
            const_def.Name = name;
            const_def.Value = value;
            _SetConstant(const_def);
        }
        public object eval_text_constants(string value)
        {
            Dictionary< string, string > const_values= new Dictionary< string, string >( );
            value =
                _const_ref_matcher.Replace(
                    value, delegate (Match match)
                           {
                               string const_val;
                               if ( !const_values.TryGetValue( match.Value, out const_val) )
                               {
                                   string name = match.Value.Substring(2, match.Value.Length - 3);
                                   const_values[ match.Value] = const_val = eval_text_constant( name );
                               }
                               return const_val;
                           });
            return _utility_doc.CreateTextNode(value);
        }
        public object eval_constant(string name)
        {
            string lower_name = name.ToLowerInvariant();
            StringBuilder sb = new StringBuilder();
            if ( _eval_stack.Contains( lower_name ) )
            {
                string[] evals = _eval_stack.ToArray();
                Array.Reverse( evals );
                foreach ( string eval in evals )
                {
                    if ( sb.Length > 0 )
                        sb.Append( "->" );
                    sb.Append( eval );
                }
                sb.Append( "->" + lower_name );
                Utils.ThrowException(
                    EvaluationException.CreateException,
                    "Cycle detected while evaluating '{0}'.  Eval Stack: {1}",
                    name, sb );
            }
            _eval_stack.Push( lower_name );
            Constant const_def = _GetConstantDef( name );
            if ( const_def.Value is string )
            {
                string value = const_def.Value.ToString();
                Match match = _const_ref_matcher.Match(value);
                if ((!string.IsNullOrEmpty(match.Value)) && (match.Value == value))
                {
                    string nameConst = match.Value.Substring(2, match.Value.Length - 3);
                 return eval_constant(nameConst);
                }
                XmlDocument doc = new XmlDocument();
                using (
                    XmlWriter writer = doc.CreateNavigator().AppendChild() )
                {
                    writer.WriteElementString(
                        "root", string.Empty, const_def.Value.ToString() );
                }
                return
                    doc.DocumentElement.CreateNavigator().SelectChildren(
                        XPathNodeType.Text );
            }
            return const_def.Value;
        }
        public string eval_text_constant( string name )
        {
            string lower_name = name.ToLowerInvariant();
            _CheckForCycle( name );
            _eval_stack.Push( lower_name );
            Constant const_def = _GetConstantDef( name );
            if ( const_def.Value is string )
            {
                return const_def.Value as String;
            }
            else if ( const_def.Value is IXPathNavigable )
            {
                IXPathNavigable nav = ( IXPathNavigable ) const_def.Value;
                return nav.CreateNavigator().Value;
            }
            else
                Utils.ThrowAppException(
                    "Unexpected value type '{0}' while processing value for '{1}'",
                    const_def.Value.GetType().Name, name );
            return null;
        }
        private void _CheckForCycle (string name)
        {
        }
        public void unwind_eval_stack()
        {
            _eval_stack.Clear();
        }
        public void push_stack()
        {
            _env_stack.Push( new ConstantDict() );
        }
        public void pop_stack()
        {
            _env_stack.Pop();
        }
        public XPathNavigator push_include( string href )
        {
            Uri current_include = _include_stack.Peek();
            Uri new_include = _resolver.ResolveUri( current_include, href );
            AddToFileset( new_include );
            Log.Debug(string.Format("Beginning include level {0} for \"{1}\" included by \"{2}\", resolved to \"{3}\"",
                _include_stack.Count + 1, href, current_include, new_include));
            XPathDocument doc =
                new XPathDocument(
                    ( Stream )
                    _resolver.GetEntity(
                        new_include, null, typeof ( Stream ) ) );
            _include_stack.Push( new_include );
            return doc.CreateNavigator();
        }
        public void pop_include()
        {
            Uri thisInclude = _include_stack.Peek();
            int thisLevel = _include_stack.Count;
            _include_stack.Pop();
            Log.Debug(string.Format("Ending include level {0} for \"{1}\" included by \"{2}\"",
                thisLevel, thisInclude, _include_stack.Peek()));
        }
        private bool _IsDefined( string symbol_name )
        {
            return _InternalGetSymbolDef( symbol_name ) != null;
        }
        private void _SetConstant(Constant constant_def )
        {
            _env_stack.Peek().Add( constant_def.Name.ToLowerInvariant(), constant_def );
        }
        internal Constant _GetConstantDef( string name )
        {
            if ( !_IsDefined( name ) )
            {
                Utils.ThrowException(
                    EvaluationException.CreateException,
                    "Reference to unknown symbol {0}", name );
            }
            return _InternalGetSymbolDef( name.ToLowerInvariant() );
        }
        private Constant _InternalGetSymbolDef (string symbol_name)
        {
            symbol_name = symbol_name.ToLowerInvariant();
            foreach ( ConstantDict frame_defs in _env_stack.ToArray() )
            {
                Constant constant_def;
                if ( frame_defs.TryGetValue( symbol_name, out constant_def ))
                    return constant_def;
            }
            string env_var = Environment.GetEnvironmentVariable( symbol_name );
            if ( env_var != null )
            {
                Constant c = new Constant();
                c.Name = symbol_name;
                c.Value = env_var;
                return c;
            }
            return null;
        }
        private bool _IsDefinedInCurrentFrame (string name)
        {
            return _env_stack.Peek().ContainsKey( name.ToLowerInvariant() );
        }
        private void _CheckAlreadyDefined(string name)
        {
            if (_IsDefinedInCurrentFrame(name))
            {
                Utils.ThrowException(
                    EvaluationException.CreateException,
                    "Symbol '{0}' already defined", name );
            }
        }
    }
 [Serializable]
    public class EvaluationException : PreprocessorException
    {
  public EvaluationException() {}
  public EvaluationException(string msg) : base(msg) { }
  public EvaluationException(string message, Exception innerException) : base(message, innerException) {}
  protected EvaluationException(SerializationInfo info, StreamingContext context)
   : base(info, context) {}
        internal static Exception CreateException (string msg, params object[] args)
        {
            return new EvaluationException( String.Format( msg, args ) );
        }
    }
 [Serializable]
    public class DefinitionException : PreprocessorException
    {
  public DefinitionException() {}
  public DefinitionException(string msg) : base(msg) { }
  public DefinitionException(string message, Exception innerException) : base(message, innerException) {}
  protected DefinitionException(SerializationInfo info, StreamingContext context)
   : base(info, context) {}
        internal static Exception CreateException (string msg, params object[] args)
        {
            return new EvaluationException( String.Format( msg, args ) );
        }
    }
 [Serializable]
    public class PreprocessorException : ApplicationException
    {
  public PreprocessorException() {}
  public PreprocessorException(string msg) : base(msg) { }
  public PreprocessorException(string message, Exception innerException) : base(message, innerException) {}
  protected PreprocessorException(SerializationInfo info, StreamingContext context)
   : base(info, context) {}
    }
    internal delegate Exception ExceptionFactory( string msg, params object[] args);
}

using System;
using System.IO;
using System.Reflection;
using System.Xml;
using System.Xml.Xsl;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor
{
    internal class ConfigPreprocessor
    {
        private ConfigPreprocessorEnvironment _current_env;
        public ConfigPreprocessorEnvironment PreProcess(XmlReader input,
                                                         XmlWriter output,
            PreprocessorUrlResolver resolver,
            Uri input_uri
            )
        {
            XsltSettings xslt_settings = new XsltSettings(true, true);
            XslCompiledTransform xslt_preprocess = new XslCompiledTransform( false );
            using (XmlReader xslt_reader = XmlReader.Create(Utils.GetAssemblyResourceStream(
                    "ThoughtWorks.CruiseControl.Core.configuration.preprocessor.ConfigPreprocessor.xslt")))
            {
                xslt_preprocess.Load(
                    xslt_reader, xslt_settings, new XmlUrlResolver());
            }
            if ( resolver == null )
            {
                resolver = new PreprocessorUrlResolver();
            }
            XsltArgumentList xslt_args = new XsltArgumentList();
            Uri base_uri = input_uri ??
                           ( String.IsNullOrEmpty( input.BaseURI )
                                 ? new Uri(
                                       Path.Combine(
                                           Environment.CurrentDirectory,
                                           "nofile.xml" ) )
                                 : new Uri( input.BaseURI ) );
            _current_env = new ConfigPreprocessorEnvironment( base_uri, resolver );
            xslt_args.AddExtensionObject("environment", _current_env);
            try
            {
                xslt_preprocess.Transform( input, xslt_args, output, null );
            }
            catch( XsltException ex )
            {
                if ( ex.InnerException != null )
                {
                    FieldInfo remoteStackTraceString = typeof(Exception).GetField("_remoteStackTraceString",
                    BindingFlags.Instance | BindingFlags.NonPublic );
     if (remoteStackTraceString == null)
      remoteStackTraceString = typeof(Exception).GetField("remote_stack_trace",
       BindingFlags.Instance | BindingFlags.NonPublic);
                    remoteStackTraceString.SetValue( ex.InnerException,
                    ex.InnerException.StackTrace + Environment.NewLine );
                    throw ex.InnerException;
                }
                throw;
            }
            if ( SubfileLoaded != null )
            {
                foreach ( string path in _current_env.Fileset )
                {
                    SubfileLoaded( path );
                }
            }
            return _current_env;
        }
        public event ConfigurationSubfileLoadedHandler SubfileLoaded;
    }
}

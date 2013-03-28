using System;
using System.IO;
using System.Reflection;
using System.Text;
using System.Xml;
using System.Xml.Xsl;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor
{
    internal class Utils
    {
        internal static XmlWriter CreateWriter(string url)
        {
            var writer_settings = new XmlWriterSettings();
            writer_settings.Indent = true;
            writer_settings.Encoding = Encoding.UTF8;
            writer_settings.ConformanceLevel = ConformanceLevel.Auto;
            writer_settings.NewLineChars = "\n";
            writer_settings.NewLineHandling = NewLineHandling.Replace;
            return XmlWriter.Create( url, writer_settings );
        }
        internal static void ThrowAppException(string fmt, params object[] args)
        {
            throw new ApplicationException( String.Format( fmt, args ) );
        }
        internal static void ThrowException(ExceptionFactory factory, string fmt,
                                            params object[] args)
        {
            throw factory( String.Format( fmt, args ) );
        }
        internal static XmlDocument TransformToDocument(XmlReader input,
                                                        XslCompiledTransform transform,
                                                        XsltArgumentList args, XmlResolver resolver)
        {
            var doc = new XmlDocument();
            using ( XmlWriter output = doc.CreateNavigator().AppendChild() )
            {
                transform.Transform( input, args, output, resolver );
            }
            return doc;
        }
        public static Stream GetAssemblyResourceStream(Type type, string resource_name)
        {
            return type.Assembly.GetManifestResourceStream( type, resource_name );
        }
        public static Stream GetAssemblyResourceStream(string resource_name)
        {
            Stream stream = Assembly.GetCallingAssembly().GetManifestResourceStream( resource_name );
            if ( stream == null )
                throw new ApplicationException(
                    String.Format( "Could not find manifest resource stream: {0}", resource_name ) );
            return stream;
        }
        public static string ResolvePathToAssemblyLocation(string rel_path, Assembly assembly)
        {
            return
                Path.Combine(
                    Path.GetDirectoryName( assembly.Location ), rel_path );
        }
    }
}

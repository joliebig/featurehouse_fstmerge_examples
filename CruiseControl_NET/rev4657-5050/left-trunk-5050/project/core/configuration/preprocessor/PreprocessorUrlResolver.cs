using System;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor
{
    class PreprocessorUrlResolver : XmlUrlResolver
    {
        public event EventHandler< UrlResolvedArgs > UrlResolved;
        public PreprocessorUrlResolver( )
        {
        }
        public override Uri ResolveUri(Uri baseUri, string relativeUri)
        {
            Uri uri;
            if ( String.IsNullOrEmpty( relativeUri ) )
            {
                uri = baseUri;
            }
            else
            {
                string uriString = Uri.UnescapeDataString(relativeUri);
                Uri uri_rel = new Uri(uriString, UriKind.RelativeOrAbsolute);
                uri = new Uri(baseUri, uri_rel);
            }
            if ( UrlResolved != null )
                UrlResolved( this, new UrlResolvedArgs( uri ) );
            return uri;
        }
    }
    internal class UrlResolvedArgs : EventArgs
    {
        private readonly Uri _uri;
        public UrlResolvedArgs( Uri uri )
        {
            _uri = uri;
        }
        public Uri Uri
        {
            get { return _uri; }
        }
    }
}

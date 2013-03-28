using System;
using System.Collections.Generic;
using System.Xml;
using System.Xml.Serialization;
using WikiFunctions.API;
namespace WikiFunctions
{
    [Serializable]
    public class SiteInfo : IXmlSerializable
    {
        private readonly IApiEdit Editor;
        private string scriptPath;
        private readonly Dictionary<int, string> namespaces = new Dictionary<int, string>();
        private Dictionary<int, List<string> > namespaceAliases = new Dictionary<int, List<string> >();
        private readonly Dictionary<string, List<string> > magicWords = new Dictionary<string, List<string> >();
        internal SiteInfo()
        { }
        public SiteInfo(IApiEdit editor)
        {
            Editor = editor;
            ScriptPath = editor.URL;
            try
            {
                if (!LoadSiteInfo())
                    throw new WikiUrlException();
            }
            catch (WikiUrlException)
            {
                throw;
            }
            catch (Exception ex)
            {
                throw new WikiUrlException(ex);
            }
        }
        private static string Key(string scriptPath)
        {
            return "SiteInfo(" + scriptPath + ")@";
        }
        public static SiteInfo CreateOrLoad(IApiEdit editor)
        {
            SiteInfo si = (SiteInfo)ObjectCache.Global.Get<SiteInfo>(Key(editor.URL));
            if (si != null
                && Namespace.VerifyNamespaces(si.Namespaces))
            {
                return si;
            }
            si = new SiteInfo(editor);
            ObjectCache.Global[Key(editor.URL)] = si;
            return si;
        }
        private string ApiPath
        {
            get { return scriptPath + "api.php" + ((Editor.PHP5) ? "5" : ""); }
        }
        public static string NormalizeURL(string url)
        {
            return (!url.EndsWith("/")) ? url + "/" : url;
        }
        public bool LoadSiteInfo()
        {
            string output = Editor.HttpGet(ApiPath + "?action=query&meta=siteinfo&siprop=general|namespaces|namespacealiases|statistics|magicwords&format=xml");
            XmlDocument xd = new XmlDocument();
            xd.LoadXml(output);
            var api = xd["api"];
            if (api == null) return false;
            var query = api["query"];
            if (query == null) return false;
            var general = query["general"];
            if (general == null) return false;
            ArticleUrl = Host + general.GetAttribute("articlepath");
            Language = general.GetAttribute("lang");
            IsRightToLeft = general.HasAttribute("rtl");
            CapitalizeFirstLetter = general.GetAttribute("case") == "first-letter";
            if (query["namespaces"] == null || query["namespacealiases"] == null)
                return false;
            foreach (XmlNode xn in query["namespaces"].GetElementsByTagName("ns"))
            {
                int id = int.Parse(xn.Attributes["id"].Value);
                if (id != 0) namespaces[id] = xn.InnerText + ":";
            }
            if (!Namespace.VerifyNamespaces(namespaces))
                throw new Exception("Error loading namespaces from " + ApiPath);
            namespaceAliases = Variables.PrepareAliases(namespaces);
            foreach (XmlNode xn in query["namespacealiases"].GetElementsByTagName("ns"))
            {
                int id = int.Parse(xn.Attributes["id"].Value);
                if (id != 0) namespaceAliases[id].Add(xn.InnerText);
            }
            if (query["magicwords"] == null)
                return false;
            foreach (XmlNode xn in query["magicwords"].GetElementsByTagName("magicword"))
            {
                List<string> alias = new List<string>();
                foreach (XmlNode xin in xn["aliases"].GetElementsByTagName("alias"))
                {
                    alias.Add(xin.InnerText);
                }
                magicWords.Add(xn.Attributes["name"].Value, alias);
            }
            return true;
        }
        public string ScriptPath
        {
            get { return scriptPath; }
            set
            {
                scriptPath = NormalizeURL(value);
            }
        }
        public string Host
        {
            get
            {
                return "http://" + Tools.ServerName(ScriptPath);
            }
        }
        public Dictionary<int, string> Namespaces
        { get { return namespaces; } }
        public Dictionary<int, List<string> > NamespaceAliases
        { get { return namespaceAliases; } }
        public Dictionary<string, List<string> > MagicWords
        { get { return magicWords; } }
        public string ArticleUrl
        { get; private set; }
        public string Language
        { get; private set; }
        public bool IsRightToLeft
        { get; private set; }
        public bool CapitalizeFirstLetter
        { get; private set; }
        public Dictionary<string, string> GetMessages(params string[] names)
        {
            string output = Editor.HttpGet(ApiPath + "?format=xml&action=query&meta=allmessages&ammessages=" + string.Join("|", names));
            XmlDocument xd = new XmlDocument();
            xd.LoadXml(output);
            Dictionary<string, string> result = new Dictionary<string, string>(names.Length);
            foreach (XmlNode xn in xd.GetElementsByTagName("message"))
            {
                result[xn.Attributes["name"].Value] = xn.InnerText;
            }
            return result;
        }
        public void OpenPageInBrowser(string title)
        {
            string url = ArticleUrl.Replace("$1", Tools.WikiEncode(title));
            Tools.OpenURLInBrowser(url);
        }
        public void OpenPageInBrowserNoEncode(string title)
        {
            string url = ArticleUrl.Replace("$1", title);
            Tools.OpenURLInBrowser(url);
        }
        public System.Xml.Schema.XmlSchema GetSchema()
        {
            return null;
        }
        public void ReadXml(XmlReader reader)
        {
            throw new Exception("The method or operation is not implemented.");
        }
        public void WriteXml(XmlWriter writer)
        {
            writer.WriteAttributeString("url", scriptPath);
            writer.WriteAttributeString("php5", Editor.PHP5 ? "1" : "0");
            {
                writer.WriteStartElement("Namespaces");
                {
                    foreach (KeyValuePair<int, string> p in namespaces)
                    {
                        writer.WriteStartElement("Namespace");
                        writer.WriteAttributeString("id", p.Key.ToString());
                        writer.WriteValue(p.Value);
                        writer.WriteEndElement();
                    }
                }
            }
        }
    }
    public class WikiUrlException : Exception
    {
        public WikiUrlException()
            : base("Can't connect to given wiki site.")
        { }
        public WikiUrlException(Exception innerException)
            : base("Can't connect to given wiki site.", innerException)
        { }
    }
}

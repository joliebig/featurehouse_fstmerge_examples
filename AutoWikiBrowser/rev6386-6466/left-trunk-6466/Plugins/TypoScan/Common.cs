using System.Text.RegularExpressions;
using System.Xml;
using System.IO;
namespace WikiFunctions.Plugins.ListMaker.TypoScan
{
    static class Common
    {
        private const string Url = "http://toolserver.org/~awb/typoscan/index.php?action=";
        private static readonly Regex UrlRegex = new Regex(@"^https?://([-a-z0-9\.]+).*$", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        internal static string GetUrlFor(string action)
        {
            return Url + action + "&wiki=" + GetSite();
        }
        public static string CheckOperation(string xml)
        {
            using (XmlTextReader r = new XmlTextReader(new StringReader(xml)))
            {
                if (!r.ReadToFollowing("operation"))
                    return "xml";
                if (r.GetAttribute("status") == "success")
                    return null;
                string s = r.GetAttribute("error");
                return string.IsNullOrEmpty(s) ? r.ReadString() : s;
            }
        }
        internal static string GetSite()
        {
            return UrlRegex.Replace(Variables.URLLong, "$1");
        }
    }
}

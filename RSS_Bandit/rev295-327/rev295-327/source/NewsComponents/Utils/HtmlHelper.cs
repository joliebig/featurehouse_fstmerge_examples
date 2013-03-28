using System; 
using System.Collections.Generic; 
using System.Globalization; 
using System.IO; 
using System.Net; 
using System.Text; 
using System.Text.RegularExpressions; 
using System.Web; 
using System.Xml; 
using log4net; 
using NewsComponents.Collections; 
using NewsComponents.Net; 
using RssBandit.Common.Logging; 
using Sgml; namespace  NewsComponents.Utils {
	
    internal class  RelativeUrlExpander {
		
        internal  string baseUrl;
 
        internal  string ConvertToAbsoluteUrl(Match m)
        {
            string href = m.Groups[1].ToString();
            string test = (href.StartsWith("\"") || href.StartsWith("'") ? href.Substring(1) : href);
            if (test.StartsWith("http") || test.StartsWith("mailto:") || test.StartsWith("javascript:"))
            {
                return m.Groups[0].ToString();
            }
            return m.Groups[0].ToString().Replace(href, HtmlHelper.ConvertToAbsoluteUrl(href, baseUrl, true));
        }

	}
	
    public sealed class  HtmlHelper {
		
        private static readonly  Regex RegExFindHrefOrSrc =
            new Regex(
                @"(?:<[iI][mM][gG]\s+([^>]*\s*)?src\s*=\s*(?:""(?<1>[/\a-z0-9_][^""]*)""|'(?<1>[/\a-z0-9_][^']*)'|(?<1>[/\a-z0-9_]\S*))(\s[^>]*)?>)|(?:<[aA]\s+([^>]*\s*)?href\s*=\s*(?:""(?<1>[/\a-z0-9_][^""]*)""|'(?<1>[/\a-z0-9_][^']*)'|(?<1>[/\a-z0-9_]\S*))(\s[^>]*)?>)",
                RegexOptions.Singleline | RegexOptions.IgnoreCase | RegexOptions.Compiled);
 
        private static readonly  Regex RegExFindHref =
            new Regex(@"<a[\s]+[^>]*?href[\s]?=[\s""']+(.*?)[""']+.*?>([^<]+|.*?)?<\/a>",
                      RegexOptions.Singleline | RegexOptions.IgnoreCase | RegexOptions.Compiled);
 
        private static readonly  Regex RegExAnyTags = new Regex("</?[^<>]+>", RegexOptions.Compiled);
 
        private static readonly  Regex RegExAnyEntity =
            new Regex("(?:&#[0-9]+;?|&#x[a-f0-9]+;?|&[a-z0-9]+;?)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
 
        private static readonly  Regex RegExFindTitle =
            new Regex(@"<head\s*>.*<title\s*>(?<title>[^<]+)</title>.*</head>",
                      RegexOptions.Singleline | RegexOptions.IgnoreCase | RegexOptions.Compiled);
 
        private static readonly  Dictionary<string, string> _htmlEntities;
 
        private static readonly  ILog _log = Log.GetLogger(typeof (HtmlHelper));
 
        public static  string ConvertToAbsoluteUrl(string url, string baseUrl)
        {
            if (url == null || url.Length == 0)
                return null;
            return ConvertToAbsoluteUrl(url, baseUrl, false);
        }
 
        public static  string ConvertToAbsoluteUrl(string url, string baseUrl, bool onlyValid)
        {
            if (url == null || url.Length == 0)
                return null;
            Uri baseUri;
            Uri.TryCreate(baseUrl, UriKind.Absolute, out baseUri);
            return ConvertToAbsoluteUrl(url, baseUri, onlyValid);
        }
 
        public static  string ConvertToAbsoluteUrl(string url, Uri baseUri, bool onlyValid)
        {
            Uri uri = ConvertToAbsoluteUri(url, baseUri, onlyValid);
            if (uri != null)
                return uri.AbsoluteUri;
            if (!onlyValid)
                return url;
            return null;
        }
 
        public static  Uri ConvertToAbsoluteUri(string url, Uri baseUri, bool onlyValid)
        {
            if (url == null)
                return null;
            Uri result;
            if (Uri.TryCreate(baseUri, url, out result))
                return result;
            return null;
        }
 
        public static  string ConvertToAbsoluteUrlPath(string url)
        {
            if (url == null)
                return null;
            Uri uri;
            if (Uri.TryCreate(url, UriKind.Absolute, out uri))
            {
                uri = ConvertToAbsoluteUriPath(uri);
                return uri.AbsoluteUri;
            }
            else
            {
                if (url.IndexOf("/") >= 0)
                    return url.Substring(0, url.LastIndexOf("/") + 1);
                return url;
            }
        }
 
        public static  Uri ConvertToAbsoluteUriPath(Uri uri)
        {
            if (uri == null)
                return null;
            UriBuilder b = new UriBuilder(uri);
            b.Path = b.Path.Substring(0, b.Path.LastIndexOf("/") + 1);
            b.Query = null;
            b.Fragment = null;
            return b.Uri;
        }
 
        public static  List<string> RetrieveLinks(string html)
        {
            if (html == null || html.Length == 0)
                return GetList<string>().Empty;
            List<string> list = new List<string>();
            for (Match m = RegExFindHref.Match(html); m.Success; m = m.NextMatch())
            {
                string href = m.Groups[1].ToString();
                if (href.StartsWith("mailto:") || href.StartsWith("javascript:"))
                {
                    continue;
                }
                if (href.Length == 0)
                    continue;
                href = RelationCosmos.RelationCosmos.UrlTable.Add(href);
                if (!list.Contains(href))
                    list.Add(href);
            }
            if (list.Count == 0)
                list = GetList<string>().Empty;
            return list;
        }
 
        public static  string ExpandRelativeUrls(string html, string baseUrl)
        {
            if (html == null || html.Length == 0)
                return html;
            RelativeUrlExpander expander = new RelativeUrlExpander();
            expander.baseUrl = baseUrl;
            return RegExFindHrefOrSrc.Replace(html, new MatchEvaluator(expander.ConvertToAbsoluteUrl));
        }
 
        public static  string HtmlDecode(string s)
        {
            if (s == null)
            {
                return null;
            }
            return HtmlDecode(s, _htmlEntities);
        }
 
        private static  string HtmlDecode(string s, IDictionary<string, string> map)
        {
            Match m = RegExAnyEntity.Match(s);
            if (!m.Success)
                return s;
            int cpos = 0;
            StringBuilder b = new StringBuilder(s.Length);
            while (m.Success)
            {
                b.Append(s.Substring(cpos, m.Index - cpos));
                string entity = m.ToString();
                string decoded;
                if (map.TryGetValue(entity, out decoded))
                {
                    b.Append(decoded);
                }
                else if (entity[1] == '#')
                {
                    try
                    {
                        int etp;
                        int lCorr = 0;
                        if (entity[entity.Length - 1] == ';')
                            lCorr = 1;
                        if ((entity[2] == 'x') || (entity[2] == 'X'))
                        {
                            string ets = entity.Substring(3, entity.Length - 3 - lCorr);
                            etp = int.Parse(ets, NumberStyles.AllowHexSpecifier);
                        }
                        else
                        {
                            string ets = entity.Substring(2, entity.Length - 2 - lCorr);
                            etp = int.Parse(ets);
                        }
                        Encoding encoding = (etp <= 0xff) ? Encoding.Default : Encoding.Unicode;
                        decoded = encoding.GetString(BitConverter.GetBytes(etp));
                        if (decoded.Length > 0)
                        {
                            decoded = decoded.Substring(0, 1);
                        }
                        b.Append(decoded);
                    }
                    catch (Exception ex)
                    {
                        _log.Error("HtmlDecode() match Exception for HTML entity '" + entity + "'", ex);
                        b.Append(entity);
                    }
                }
                else
                {
                    b.Append(entity);
                }
                cpos = m.Index + m.Length;
                m = m.NextMatch();
            }
            b.Append(s.Substring(cpos, s.Length - cpos));
            return b.ToString();
        }
 
        public static  string StripAnyTags(string html)
        {
            if (html == null)
                return String.Empty;
            return RegExAnyTags.Replace(html, String.Empty);
        }
 
        public static  string FindTitle(string html, string defaultIfNoMatch)
        {
            if (html == null)
                return defaultIfNoMatch;
            Match m = RegExFindTitle.Match(html);
            if (m.Success)
            {
                string t = m.Groups["title"].Value;
                if (StringHelper.EmptyTrimOrNull(t))
                    return defaultIfNoMatch;
                return HtmlDecode(t);
            }
            return defaultIfNoMatch;
        }
 
        public static  string FindTitle(string url, string defaultIfNoMatch, IWebProxy proxy, ICredentials credentials)
        {
            HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(url);
            request.AllowAutoRedirect = true;
            request.Proxy = proxy;
            request.Credentials = credentials;
            request.Timeout = 4 * 1000 ;
            if(NewsHandler.SetCookies)
            {
                HttpCookieManager.SetCookies(request);
            }
            request.UserAgent = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1;)";
            string title = defaultIfNoMatch;
            Stream stream = null;
            try
            {
                stream = request.GetResponse().GetResponseStream();
                SgmlReader reader = new SgmlReader();
                reader.InputStream = new StreamReader(stream);
                while (reader.Read())
                {
                    if ((reader.NodeType == XmlNodeType.Element) && (reader.Name.ToLower().Equals("title")))
                    {
                        title = reader.ReadElementContentAsString();
                        reader.Close();
                        stream.Close();
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                _log.Debug("Error retrieving title from HTML page at " + url, e);
            }
            finally
            {
                if (stream != null)
                {
                    stream.Close();
                }
            }
            return title;
        }
 
        public static  string StripBadTags(string html)
        {
            if (html == null)
                return String.Empty;
            return html;
        }
 
        public static  string UrlEncode(string value)
        {
            if (value == null)
                return null;
            StringBuilder result = new StringBuilder();
            foreach (char c in value)
            {
                if (c < ' ' || c > 128 || c == '(' || c == ')'
                    || c == '/' || c == ' ' || c == '+' || c == ',' ||
                    c == ':' || c == '"' || c == '&')
                {
                    result.Append('%');
                    result.AppendFormat(CultureInfo.InvariantCulture, "{0:X2}", (int) c);
                }
                else
                    result.Append(c);
            }
            return result.ToString();
        }
 
        public static  string UrlDecode(string value)
        {
            if (value == null)
                return null;
            string result = value.Replace("+", "%2b");
            return HttpUtility.UrlDecode(result);
        }
 
        private  HtmlHelper()
        {
        }
 
        static  HtmlHelper()
        {
            _htmlEntities = new Dictionary<string, string>();
            _htmlEntities.Add("&Aacute;", Convert.ToString('\x00c1'));
            _htmlEntities.Add("&aacute;", Convert.ToString('\x00e1'));
            _htmlEntities.Add("&Acirc;", Convert.ToString('\x00c2'));
            _htmlEntities.Add("&acirc;", Convert.ToString('\x00e2'));
            _htmlEntities.Add("&acute;", Convert.ToString('\x00b4'));
            _htmlEntities.Add("&AElig;", Convert.ToString('\x00c6'));
            _htmlEntities.Add("&aelig;", Convert.ToString('\x00e6'));
            _htmlEntities.Add("&Agrave;", Convert.ToString('\x00c0'));
            _htmlEntities.Add("&agrave;", Convert.ToString('\x00e0'));
            _htmlEntities.Add("&alefsym;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Alpha;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&alpha;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&amp;", Convert.ToString('&'));
            _htmlEntities.Add("&and;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&ang;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&apos;", "'");
            _htmlEntities.Add("&Aring;", Convert.ToString('\x00c5'));
            _htmlEntities.Add("&aring;", Convert.ToString('\x00e5'));
            _htmlEntities.Add("&asymp;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Atilde;", Convert.ToString('\x00c3'));
            _htmlEntities.Add("&atilde;", Convert.ToString('\x00e3'));
            _htmlEntities.Add("&Auml;", Convert.ToString('\x00c4'));
            _htmlEntities.Add("&auml;", Convert.ToString('\x00e4'));
            _htmlEntities.Add("&bdquo;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Beta;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&beta;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&brvbar;", Convert.ToString('\x00a6'));
            _htmlEntities.Add("&bull;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&cap;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Ccedil;", Convert.ToString('\x00c7'));
            _htmlEntities.Add("&ccedil;", Convert.ToString('\x00e7'));
            _htmlEntities.Add("&cedil;", Convert.ToString('\x00b8'));
            _htmlEntities.Add("&cent;", Convert.ToString('\x00a2'));
            _htmlEntities.Add("&Chi;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&chi;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&circ;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&clubs;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&commat;", Convert.ToString('@'));
            _htmlEntities.Add("&cong;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&copy;", Convert.ToString('\x00a9'));
            _htmlEntities.Add("&crarr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&cup;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&curren;", Convert.ToString('\x00a4'));
            _htmlEntities.Add("&dagger;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Dagger;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&darr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&dArr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&deg;", Convert.ToString('\x00b0'));
            _htmlEntities.Add("&Delta;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&delta;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&diams;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&divide;", Convert.ToString('\x00f7'));
            _htmlEntities.Add("&dollar;", Convert.ToString('$'));
            _htmlEntities.Add("&Eacute;", Convert.ToString('\x00c9'));
            _htmlEntities.Add("&eacute;", Convert.ToString('\x00e9'));
            _htmlEntities.Add("&Ecirc;", Convert.ToString('\x00ca'));
            _htmlEntities.Add("&ecirc;", Convert.ToString('\x00ea'));
            _htmlEntities.Add("&Egrave;", Convert.ToString('\x00c8'));
            _htmlEntities.Add("&egrave;", Convert.ToString('\x00e8'));
            _htmlEntities.Add("&empty;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&emsp;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&ensp;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Epsilon;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&epsilon;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&equiv;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Eta;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&eta;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&ETH;", Convert.ToString('\x00d0'));
            _htmlEntities.Add("&eth;", Convert.ToString('\x00f0'));
            _htmlEntities.Add("&Euml;", Convert.ToString('\x00cb'));
            _htmlEntities.Add("&euml;", Convert.ToString('\x00eb'));
            _htmlEntities.Add("&euro;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&exist;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&fnof;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&forall;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&frac12;", Convert.ToString('\x00bd'));
            _htmlEntities.Add("&frac14;", Convert.ToString('\x00bc'));
            _htmlEntities.Add("&frac34;", Convert.ToString('\x00be'));
            _htmlEntities.Add("&frasl;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Gamma;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&gamma;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&ge;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&grave;", Convert.ToString('`'));
            _htmlEntities.Add("&gt;", Convert.ToString('>'));
            _htmlEntities.Add("&harr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&hArr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&hearts;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&hellip;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Iacute;", Convert.ToString('\x00cd'));
            _htmlEntities.Add("&iacute;", Convert.ToString('\x00ed'));
            _htmlEntities.Add("&Icirc;", Convert.ToString('\x00ce'));
            _htmlEntities.Add("&icirc;", Convert.ToString('\x00ee'));
            _htmlEntities.Add("&iexcl;", Convert.ToString('\x00a1'));
            _htmlEntities.Add("&Igrave;", Convert.ToString('\x00cc'));
            _htmlEntities.Add("&igrave;", Convert.ToString('\x00ec'));
            _htmlEntities.Add("&image;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&infin;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&int;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Iota;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&iota;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&iquest;", Convert.ToString('\x00bf'));
            _htmlEntities.Add("&isin;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Iuml;", Convert.ToString('\x00cf'));
            _htmlEntities.Add("&iuml;", Convert.ToString('\x00ef'));
            _htmlEntities.Add("&Kappa;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&kappa;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Lambda;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&lambda;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&lang;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&laquo;", Convert.ToString('\x00ab'));
            _htmlEntities.Add("&larr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&lArr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&lceil;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&ldquo;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&le;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&lfloor;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&lowast;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&loz;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&lrm;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&lsaquo;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&lsquo;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&lt;", Convert.ToString('<'));
            _htmlEntities.Add("&macr;", Convert.ToString('\x00af'));
            _htmlEntities.Add("&mdash;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&micro;", Convert.ToString('\x00b5'));
            _htmlEntities.Add("&middot;", Convert.ToString('\x00b7'));
            _htmlEntities.Add("&minus;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Mu;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&mu;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&nabla;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&nbsp;", Convert.ToString('\x00a0'));
            _htmlEntities.Add("&ndash;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&ne;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&ni;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&not;", Convert.ToString('\x00ac'));
            _htmlEntities.Add("&notin;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&nsub;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Ntilde;", Convert.ToString('\x00d1'));
            _htmlEntities.Add("&ntilde;", Convert.ToString('\x00f1'));
            _htmlEntities.Add("&Nu;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&nu;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&num;", Convert.ToString('#'));
            _htmlEntities.Add("&Oacute;", Convert.ToString('\x00d3'));
            _htmlEntities.Add("&oacute;", Convert.ToString('\x00f3'));
            _htmlEntities.Add("&Ocirc;", Convert.ToString('\x00d4'));
            _htmlEntities.Add("&ocirc;", Convert.ToString('\x00f4'));
            _htmlEntities.Add("&OElig;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&oelig;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Ograve;", Convert.ToString('\x00d2'));
            _htmlEntities.Add("&ograve;", Convert.ToString('\x00f2'));
            _htmlEntities.Add("&oline;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Omega;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&omega;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Omicron;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&omicron;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&oplus;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&or;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&ordf;", Convert.ToString('\x00aa'));
            _htmlEntities.Add("&ordm;", Convert.ToString('\x00ba'));
            _htmlEntities.Add("&Oslash;", Convert.ToString('\x00d8'));
            _htmlEntities.Add("&oslash;", Convert.ToString('\x00f8'));
            _htmlEntities.Add("&Otilde;", Convert.ToString('\x00d5'));
            _htmlEntities.Add("&otilde;", Convert.ToString('\x00f5'));
            _htmlEntities.Add("&otimes;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Ouml;", Convert.ToString('\x00d6'));
            _htmlEntities.Add("&ouml;", Convert.ToString('\x00f6'));
            _htmlEntities.Add("&para;", Convert.ToString('\x00b6'));
            _htmlEntities.Add("&part;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&percnt;", Convert.ToString('%'));
            _htmlEntities.Add("&permil;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&perp;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Phi;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&phi;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Pi;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&pi;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&piv;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&plusmn;", Convert.ToString('\x00b1'));
            _htmlEntities.Add("&pound;", Convert.ToString('\x00a3'));
            _htmlEntities.Add("&prime;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Prime;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&prod;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&prop;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Psi;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&psi;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&quot;", Convert.ToString('"'));
            _htmlEntities.Add("&radic;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&rang;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&raquo;", Convert.ToString('\x00bb'));
            _htmlEntities.Add("&rarr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&rArr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&rceil;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&rdquo;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&real;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&reg;", Convert.ToString('\x00ae'));
            _htmlEntities.Add("&rfloor;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Rho;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&rho;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&rlm;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&rsaquo;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&rsquo;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sbquo;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Scaron;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&scaron;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sdot;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sect;", Convert.ToString('\x00a7'));
            _htmlEntities.Add("&shy;", Convert.ToString('\x00ad'));
            _htmlEntities.Add("&Sigma;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sigma;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sigmaf;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sim;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&spades;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sub;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sube;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sum;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sup;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&sup1;", Convert.ToString('\x00b9'));
            _htmlEntities.Add("&sup2;", Convert.ToString('\x00b2'));
            _htmlEntities.Add("&sup3;", Convert.ToString('\x00b3'));
            _htmlEntities.Add("&supe;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&szlig;", Convert.ToString('\x00df'));
            _htmlEntities.Add("&Tau;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&tau;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&there4;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Theta;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&theta;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&thetasym;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&thinsp;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&THORN;", Convert.ToString('\x00de'));
            _htmlEntities.Add("&thorn;", Convert.ToString('\x00fe'));
            _htmlEntities.Add("&tilde;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&times;", Convert.ToString('\x00d7'));
            _htmlEntities.Add("&trade;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Uacute;", Convert.ToString('\x00da'));
            _htmlEntities.Add("&uacute;", Convert.ToString('\x00fa'));
            _htmlEntities.Add("&uarr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&uArr;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Ucirc;", Convert.ToString('\x00db'));
            _htmlEntities.Add("&ucirc;", Convert.ToString('\x00fb'));
            _htmlEntities.Add("&Ugrave;", Convert.ToString('\x00d9'));
            _htmlEntities.Add("&ugrave;", Convert.ToString('\x00f9'));
            _htmlEntities.Add("&uml;", Convert.ToString('\x00a8'));
            _htmlEntities.Add("&upsih;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Upsilon;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&upsilon;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Uuml;", Convert.ToString('\x00dc'));
            _htmlEntities.Add("&uuml;", Convert.ToString('\x00fc'));
            _htmlEntities.Add("&weierp;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Xi;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&xi;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&Yacute;", Convert.ToString('\x00dd'));
            _htmlEntities.Add("&yacute;", Convert.ToString('\x00fd'));
            _htmlEntities.Add("&yen;", Convert.ToString('\x00a5'));
            _htmlEntities.Add("&Yuml;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&yuml;", Convert.ToString('\x00ff'));
            _htmlEntities.Add("&Zeta;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&zeta;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&zwj;", Convert.ToString('\u0000'));
            _htmlEntities.Add("&zwnj;", Convert.ToString('\u0000'));
        }

	}

}

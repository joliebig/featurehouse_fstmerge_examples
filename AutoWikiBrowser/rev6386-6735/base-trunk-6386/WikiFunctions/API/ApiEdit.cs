using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.Reflection;
using System.Web;
using System.IO;
using System.Xml;
using System.Threading;
using System.Text.RegularExpressions;
namespace WikiFunctions.API
{
    public class ApiEdit : IApiEdit
    {
        private ApiEdit()
        {
            Cookies = new CookieContainer();
            User = new UserInfo();
            NewMessageThrows = true;
        }
        public ApiEdit(string url)
            : this(url, false)
        {
        }
        public ApiEdit(string url, bool usePHP5)
            : this()
        {
            if (string.IsNullOrEmpty(url)) throw new ArgumentException("Invalid URL specified", "url");
            if (!url.StartsWith("http://")) throw new NotSupportedException("Only editing via HTTP is currently supported");
            URL = url;
            PHP5 = usePHP5;
            ApiURL = URL + "api.php" + (PHP5 ? "5" : "");
            Maxlag = 5;
            IWebProxy proxy;
            if (ProxyCache.TryGetValue(url, out proxy))
            {
                ProxySettings = proxy;
            }
            else
            {
                ProxySettings = WebRequest.GetSystemWebProxy();
                if (ProxySettings.IsBypassed(new Uri(url)))
                {
                    ProxySettings = null;
                }
                ProxyCache.Add(url, ProxySettings);
            }
        }
        public IApiEdit Clone()
        {
            return new ApiEdit()
                       {
                           URL = URL,
                           ApiURL = ApiURL,
                           PHP5 = PHP5,
                           Maxlag = Maxlag,
                           Cookies = Cookies,
                           ProxySettings = ProxySettings,
                           User = User
                       };
        }
        public string URL { get; private set; }
        public string ApiURL { get; private set; }
        private string Server
        { get { return "http://" + new Uri(URL).Host; } }
        public bool PHP5 { get; private set; }
        public int Maxlag { get; set; }
        public bool NewMessageThrows
        { get; set; }
        public string Action { get; private set; }
        public PageInfo Page
        { get; private set; }
        public string HtmlHeaders
        { get; private set; }
        public CookieContainer Cookies { get; private set; }
        public void Reset()
        {
            Action = null;
            Page = new PageInfo();
            Aborting = false;
            Request = null;
        }
        public void Abort()
        {
            Aborting = true;
            Request.Abort();
            Thread.Sleep(1);
            Aborting = false;
        }
        private void AdjustCookies()
        {
            string host = new Uri(URL).Host;
            var newCookies = new CookieContainer();
            var urls = new[] { URL, "http://fnord." + host };
            foreach (string u in urls)
            {
                foreach (Cookie c in Cookies.GetCookies(new Uri(u)))
                {
                    c.Domain = host;
                    newCookies.Add(c);
                }
            }
            Cookies = newCookies;
        }
        protected static string BuildQuery(string[,] request)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= request.GetUpperBound(0); i++)
            {
                string s = request[i, 0];
                if (string.IsNullOrEmpty(s)) continue;
                sb.Append('&');
                sb.Append(s);
                s = request[i, 1];
                if (s != null)
                {
                    sb.Append('=');
                    sb.Append(HttpUtility.UrlEncode(s));
                }
            }
            return sb.ToString();
        }
        protected static string Titles(params string[] titles)
        {
            for (int i = 0; i < titles.Length; i++) titles[i] = Tools.WikiEncode(titles[i]);
            if (titles.Length > 0) return "&titles=" + string.Join("|", titles);
            return "";
        }
        protected static string NamedTitles(string paramName, params string[] titles)
        {
            for (int i = 0; i < titles.Length; i++) titles[i] = Tools.WikiEncode(titles[i]);
            if (titles.Length > 0) return "&" + paramName + "=" + string.Join("|", titles);
            return "";
        }
        protected string AppendOptions(string url, ActionOptions options)
        {
            if ((options & ActionOptions.CheckMaxlag) > 0 && Maxlag > 0)
                url += "&maxlag=" + Maxlag;
            if ((options & ActionOptions.RequireLogin) > 0)
                url += "&assert=user";
            if ((options & ActionOptions.CheckNewMessages) > 0)
                url += "&meta=userinfo&uiprop=hasmsg";
            return url;
        }
        protected string BuildUrl(string[,] request, ActionOptions options)
        {
            string url = ApiURL + "?format=xml" + BuildQuery(request);
            return AppendOptions(url, options);
        }
        protected string BuildUrl(string[,] request)
        {
            return BuildUrl(request, ActionOptions.None);
        }
        private static readonly Dictionary<string, IWebProxy> ProxyCache = new Dictionary<string, IWebProxy>();
        private IWebProxy ProxySettings;
        private static readonly string UserAgent = string.Format("WikiFunctions ApiEdit/{0} ({1}; .NET CLR {2})",
                                                                 Assembly.GetExecutingAssembly().GetName().Version,
                                                                 Environment.OSVersion.VersionString,
                                                                 Environment.Version);
        protected HttpWebRequest CreateRequest(string url)
        {
            if (Globals.UnitTestMode) throw new Exception("You shouldn't access Wikipedia from unit tests");
            ServicePointManager.Expect100Continue = false;
            HttpWebRequest res = (HttpWebRequest)WebRequest.Create(url);
            res.ServicePoint.Expect100Continue = false;
            res.Expect = "";
            if (ProxySettings != null) res.Proxy = ProxySettings;
            res.UserAgent = UserAgent;
            res.AutomaticDecompression = DecompressionMethods.Deflate | DecompressionMethods.GZip;
            if (url.StartsWith(URL)) res.CookieContainer = Cookies;
            return res;
        }
        private bool Aborting;
        private HttpWebRequest Request;
        protected string GetResponseString(HttpWebRequest req)
        {
            Request = req;
            try
            {
                using (WebResponse resp = req.GetResponse())
                {
                    using (StreamReader sr = new StreamReader(resp.GetResponseStream()))
                    {
                        return sr.ReadToEnd();
                    }
                }
            }
            catch (WebException ex)
            {
                var resp = (HttpWebResponse)ex.Response;
                if (resp == null) throw;
                switch (resp.StatusCode)
                {
                    case HttpStatusCode.NotFound :
                        return "";
                }
                if (ex.Status == WebExceptionStatus.RequestCanceled)
                    throw new AbortedException(this);
                else throw;
            }
            finally
            {
                Request = null;
            }
        }
        protected string HttpPost(string[,] get, string[,] post, ActionOptions options)
        {
            string url = BuildUrl(get, options);
            string query = BuildQuery(post);
            byte[] postData = Encoding.UTF8.GetBytes(query);
            HttpWebRequest req = CreateRequest(url);
            req.Method = "POST";
            req.ContentType = "application/x-www-form-urlencoded";
            req.ContentLength = postData.Length;
            using (Stream rs = req.GetRequestStream())
            {
                rs.Write(postData, 0, postData.Length);
            }
            return GetResponseString(req);
        }
        protected string HttpPost(string[,] get, string[,] post)
        {
            return HttpPost(get, post, ActionOptions.None);
        }
        protected string HttpGet(string[,] request, ActionOptions options)
        {
            string url = BuildUrl(request, options);
            return HttpGet(url);
        }
        protected string HttpGet(string[,] request)
        {
            return HttpGet(request, ActionOptions.None);
        }
        public string HttpGet(string url)
        {
            return GetResponseString(CreateRequest(url));
        }
        public void Login(string username, string password)
        {
            if (string.IsNullOrEmpty(username)) throw new ArgumentException("Username required", "username");
            Reset();
            User = new UserInfo();
            string result = HttpPost(new[,] { { "action", "login" } },
                                     new[,]
                                     {
                                        { "lgname", username },
                                        { "lgpassword", password }
                                     }
                                );
            XmlReader xr = XmlReader.Create(new StringReader(result));
            xr.ReadToFollowing("login");
            if (xr.GetAttribute("result").Equals("NeedToken", StringComparison.InvariantCultureIgnoreCase))
            {
                string token = xr.GetAttribute("token");
                result = HttpPost(new[,] { { "action", "login" } },
                                  new[,]
                                      {
                                          {"lgname", username},
                                          {"lgpassword", password},
                                          {"lgtoken", token}
                                      }
                    );
                xr = XmlReader.Create(new StringReader(result));
                xr.ReadToFollowing("login");
            }
            string status = xr.GetAttribute("result");
            if (!status.Equals("Success", StringComparison.InvariantCultureIgnoreCase))
            {
                throw new LoginException(this, status);
            }
            CheckForErrors(result, "login");
            AdjustCookies();
            RefreshUserInfo();
        }
        public void Logout()
        {
            Reset();
            User = new UserInfo();
            string result = HttpGet(new[,] { { "action", "logout" } });
            CheckForErrors(result, "logout");
        }
        public void Watch(string title)
        {
            if (string.IsNullOrEmpty(title)) throw new ArgumentException("Page name required", "title");
            Reset();
            string result = HttpGet(new[,]
                {
                    {"action", "watch"},
                    {"title", title}
                });
            CheckForErrors(result, "watch");
        }
        public void Unwatch(string title)
        {
            if (string.IsNullOrEmpty(title)) throw new ArgumentException("Page name required", "title");
            Reset();
            string result = HttpGet(new[,]
                                        {
                                            {"action", "watch"},
                                            {"title", title},
                                            {"unwatch", null}
                                        });
            CheckForErrors(result, "watch");
        }
        public UserInfo User { get; private set; }
        public void RefreshUserInfo()
        {
            Reset();
            User = new UserInfo();
            string result = HttpPost(new[,] { { "action", "query" } },
                         new[,] {
                            { "meta", "userinfo" },
                            { "uiprop", "blockinfo|hasmsg|groups|rights" }
                         });
            var xml = CheckForErrors(result, "userinfo");
            User = new UserInfo(xml);
        }
        public string Open(string title)
        {
            if (string.IsNullOrEmpty(title)) throw new ArgumentException("Page name required", "title");
            if (!User.IsLoggedIn) throw new LoggedOffException(this);
            Reset();
            string result = HttpGet(new[,] {
                { "action", "query" },
                { "prop", "info|revisions" },
                { "intoken","edit" },
                { "titles", title },
                { "inprop", "protection|watched" },
                { "rvprop", "content|timestamp" }
            },
            ActionOptions.All);
            CheckForErrors(result, "query");
            try
            {
                Page = new PageInfo(result);
                Action = "edit";
            }
            catch (Exception ex)
            {
                throw new BrokenXmlException(this, ex);
            }
            return Page.Text;
        }
        public SaveInfo Save(string pageText, string summary, bool minor, WatchOptions watch)
        {
            if (string.IsNullOrEmpty(pageText) && !Page.Exists) throw new ArgumentException("Can't save empty pages", "pageText");
            if (Action != "edit") throw new ApiException(this, "This page is not opened properly for editing");
            if (string.IsNullOrEmpty(Page.EditToken)) throw new ApiException(this, "Edit token is needed to edit pages");
            pageText = Tools.ConvertFromLocalLineEndings(pageText);
            string result = HttpPost(
                new[,]
                {
                    { "action", "edit" },
                    { "title", Page.Title },
                    { minor ? "minor" : null, null },
                    { WatchOptionsToParam(watch), null },
                    { User.IsBot ? "bot" : null, null }
                },
                new[,]
                {
                    { "md5", MD5(pageText) },
                    { "summary", summary },
                    { "basetimestamp", Page.Timestamp },
                    { "text", pageText },
                    { "starttimestamp", Page.TokenTimestamp },
                    { "token", Page.EditToken }
                },
                ActionOptions.All);
            var xml = CheckForErrors(result, "edit");
            Reset();
            return new SaveInfo(xml);
        }
        public void Delete(string title, string reason)
        {
            Delete(title, reason, false);
        }
        public void Delete(string title, string reason, bool watch)
        {
            if (string.IsNullOrEmpty(title)) throw new ArgumentException("Page name required", "title");
            if (string.IsNullOrEmpty(reason)) throw new ArgumentException("Deletion reason required", "reason");
            Reset();
            Action = "delete";
            string result = HttpGet(
                new[,]
                    {
                        { "action", "query" },
                        { "prop", "info" },
                        { "intoken", "delete" },
                        { "titles", title },
                        { watch ? "watch" : null, null }
                    },
                    ActionOptions.All);
            CheckForErrors(result);
            try
            {
                XmlReader xr = XmlReader.Create(new StringReader(result));
                if (!xr.ReadToFollowing("page")) throw new Exception("Cannot find <page> element");
                Page.EditToken = xr.GetAttribute("deletetoken");
            }
            catch (Exception ex)
            {
                throw new BrokenXmlException(this, ex);
            }
            if (Aborting) throw new AbortedException(this);
            result = HttpPost(
                new[,]
                {
                    { "action", "delete" }
                },
                new[,]
                {
                    { "title", title },
                    { "token", Page.EditToken },
                    { "reason", reason }
                },
                ActionOptions.All);
            CheckForErrors(result);
            Reset();
        }
        public void Protect(string title, string reason, TimeSpan expiry, string edit, string move)
        {
            Protect(title, reason, expiry.ToString(), edit, move, false, false);
        }
        public void Protect(string title, string reason, string expiry, string edit, string move)
        {
            Protect(title, reason, expiry, edit, move, false, false);
        }
        public void Protect(string title, string reason, TimeSpan expiry, string edit, string move, bool cascade, bool watch)
        {
            Protect(title, reason, expiry.ToString(), edit, move, cascade, watch);
        }
        public void Protect(string title, string reason, string expiry, string edit, string move, bool cascade, bool watch)
        {
            if (string.IsNullOrEmpty(title)) throw new ArgumentException("Page name required", "title");
            if (string.IsNullOrEmpty(reason)) throw new ArgumentException("Deletion reason required", "reason");
            Reset();
            Action = "protect";
            string result = HttpGet(
                new[,]
                    {
                        { "action", "query" },
                        { "prop", "info" },
                        { "intoken", "protect" },
                        { "titles", title },
                    },
                    ActionOptions.All);
            CheckForErrors(result);
            try
            {
                XmlReader xr = XmlReader.Create(new StringReader(result));
                if (!xr.ReadToFollowing("page")) throw new Exception("Cannot find <page> element");
                Page.EditToken = xr.GetAttribute("protecttoken");
            }
            catch (Exception ex)
            {
                throw new BrokenXmlException(this, ex);
            }
            if (Aborting) throw new AbortedException(this);
            result = HttpPost(
                new[,]
                    {
                        {"action", "protect"}
                    },
                new[,]
                    {
                        { "title", title },
                        { "token", Page.EditToken },
                        { "reason", reason },
                        { "protections", "edit=" + edit + "|move=" + move },
                        { string.IsNullOrEmpty(expiry) ? "" : "expiry", string.IsNullOrEmpty(expiry) ? "" : expiry + "|" + expiry },
                        { cascade ? "cascade" : null, null },
                        { watch ? "watch" : null, null }
                    },
                    ActionOptions.All);
            CheckForErrors(result);
            Reset();
        }
        public void Move(string title, string newTitle, string reason)
        {
            Move(title, newTitle, reason, true, false, false);
        }
        public void Move(string title, string newTitle, string reason, bool moveTalk, bool noRedirect)
        {
            Move(title, newTitle, reason, moveTalk, noRedirect, false);
        }
        public void Move(string title, string newTitle, string reason, bool moveTalk, bool noRedirect, bool watch)
        {
            if (string.IsNullOrEmpty(title)) throw new ArgumentException("Page title required", "title");
            if (string.IsNullOrEmpty(newTitle)) throw new ArgumentException("Target page title required", "newTitle");
            if (string.IsNullOrEmpty(reason)) throw new ArgumentException("Page rename reason required", "reason");
            if (title == newTitle) throw new ArgumentException("Page cannot be moved to the same title");
            Reset();
            Action = "move";
            string result = HttpGet(
                new[,]
                    {
                        { "action", "query" },
                        { "prop", "info" },
                        { "intoken", "move" },
                        { "titles", title + "|" + newTitle },
                    },
                    ActionOptions.All);
            CheckForErrors(result);
            bool invalid;
            try
            {
                XmlReader xr = XmlReader.Create(new StringReader(result));
                if (!xr.ReadToFollowing("page")) throw new Exception("Cannot find <page> element");
                invalid = xr.MoveToAttribute("invalid");
                Page.EditToken = xr.GetAttribute("movetoken");
            }
            catch (Exception ex)
            {
                throw new BrokenXmlException(this, ex);
            }
            if (Aborting) throw new AbortedException(this);
            if (invalid) throw new ApiException(this, "invalidnewtitle", new ArgumentException("Target page invalid", "newTitle"));
            result = HttpPost(
                new[,]
                    {
                        { "action", "move" }
                    },
                new[,]
                    {
                        { "from", title },
                        { "to", newTitle },
                        { "token", Page.EditToken },
                        { "reason", reason },
                        { "protections", "" },
                        { moveTalk ? "movetalk" : null, null },
                        { noRedirect ? "noredirect" : null, null },
                        { watch ? "watch" : null, null }
                    },
                ActionOptions.All);
            CheckForErrors(result);
            Reset();
        }
        public string QueryApi(string queryParamters)
        {
            if (string.IsNullOrEmpty(queryParamters)) throw new ArgumentException("queryParamters cannot be null/empty", "queryParamters");
            string result = HttpGet(ApiURL + "?action=query&format=xml&" + queryParamters);
            CheckForErrors(result, "query");
            return result;
        }
        private string ExpandRelativeUrls(string html)
        {
            return html.Replace(" href=\"/", " href=\"" + Server + "/")
                .Replace(" src=\"/", " src=\"" + Server + "/");
        }
        private static readonly Regex ExtractCssAndJs = new Regex(@"("
            + @"<!--\[if .*?-->"
            + @"|<style\b.*?>.*?</style>"
            + @"|<link rel=""stylesheet"".*?/\s?>"
            + ")",
            RegexOptions.Singleline | RegexOptions.Compiled);
        private void EnsureHtmlHeadersLoaded()
        {
            if (!string.IsNullOrEmpty(HtmlHeaders)) return;
            string html = HttpGet(URL + "index.php" + (PHP5 ? "5" : ""));
            html = Tools.StringBetween(html, "<head>", "</head>");
            StringBuilder extracted = new StringBuilder(2048);
            foreach (Match m in ExtractCssAndJs.Matches(html))
            {
                extracted.Append(m.Value);
                extracted.Append("\n");
            }
            HtmlHeaders = ExpandRelativeUrls(extracted.ToString());
        }
        public string Preview(string title, string text)
        {
            EnsureHtmlHeadersLoaded();
            string result = HttpPost(
                new[,]
                {
                    { "action", "parse" },
                    { "prop", "text" }
                },
                new[,]
                {
                    { "title", title },
                    { "text", text }
                });
            CheckForErrors(result, "parse");
            try
            {
                XmlReader xr = XmlReader.Create(new StringReader(result));
                if (!xr.ReadToFollowing("text")) throw new Exception("Cannot find <text> element");
                return ExpandRelativeUrls(xr.ReadString());
            }
            catch (Exception ex)
            {
                throw new BrokenXmlException(this, ex);
            }
        }
        public string ExpandTemplates(string title, string text)
        {
            string result = HttpPost(
                new[,]
                {
                    { "action", "expandtemplates" }
                },
                new[,]
                {
                    { "title", title },
                    { "text", text }
                });
            CheckForErrors(result, "expandtemplates");
            try
            {
                XmlReader xr = XmlReader.Create(new StringReader(result));
                if (!xr.ReadToFollowing("expandtemplates")) throw new Exception("Cannot find <expandtemplates> element");
                return xr.ReadString();
            }
            catch (Exception ex)
            {
                throw new BrokenXmlException(this, ex);
            }
        }
        private XmlDocument CheckForErrors(string xml)
        {
            return CheckForErrors(xml, null);
        }
        private static readonly Regex MaxLag = new Regex(@": (\d+) seconds lagged", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private XmlDocument CheckForErrors(string xml, string action)
        {
            if (string.IsNullOrEmpty(xml)) throw new ApiBlankException(this);
            var doc = new XmlDocument();
            doc.Load(new StringReader(xml));
            bool prevMessages = User.HasMessages;
            User.Update(doc);
            if (action != "login"
                && action != "userinfo"
                && NewMessageThrows
                && User.HasMessages
                && !prevMessages)
                throw new NewMessagesException(this);
            var errors = doc.GetElementsByTagName("error");
            if (errors.Count > 0)
            {
                var error = errors[0];
                string errorCode = error.Attributes["code"].Value;
                string errorMessage = error.Attributes["info"].Value;
                switch (errorCode.ToLower())
                {
                    case "maxlag":
                        int maxlag;
                        int.TryParse(MaxLag.Match(xml).Groups[1].Value, out maxlag);
                        throw new MaxlagException(this, maxlag, 10);
                    case "wrnotloggedin":
                        throw new LoggedOffException(this);
                    default:
                        if (errorCode.Contains("disabled"))
                            throw new FeatureDisabledException(this, errorCode, errorMessage);
                        throw new ApiErrorException(this, errorCode, errorMessage);
                }
            }
            if (string.IsNullOrEmpty(action)) return doc;
            var api = doc["api"];
            if (api == null) return doc;
            if (api.GetElementsByTagName("interwiki").Count > 0)
                throw new InterwikiException(this);
            var actionElement = api[action];
            if (actionElement == null) return doc;
            if (actionElement.HasAttribute("assert"))
            {
                string what = actionElement.GetAttribute("assert");
                if (what == "user")
                    throw new LoggedOffException(this);
                throw new AssertionFailedException(this, what);
            }
            if (actionElement.HasAttribute("spamblacklist"))
            {
                throw new SpamlistException(this, actionElement.GetAttribute("spamblacklist"));
            }
            if (actionElement.GetElementsByTagName("captcha").Count > 0)
            {
                throw new CaptchaException(this);
            }
            string result = actionElement.GetAttribute("result");
            if (!string.IsNullOrEmpty(result) && result != "Success")
                throw new OperationFailedException(this, action, result);
            return doc;
        }
        protected static string BoolToParam(bool value)
        {
            return value ? "1" : "0";
        }
        protected static string WatchOptionsToParam(WatchOptions watch)
        {
            switch (watch)
            {
                case WatchOptions.UsePreferences:
                    return "watchlist=preferences";
                case WatchOptions.Watch:
                    return "watchlist=watch&watch";
                case WatchOptions.Unwatch:
                    return "watchlist=unwatch&unwatch";
                default:
                    return "watchlist=nochange";
            }
        }
        private static readonly System.Security.Cryptography.MD5 MD5Summer = System.Security.Cryptography.MD5.Create();
        protected static string MD5(string input)
        {
            return MD5(Encoding.UTF8.GetBytes(input));
        }
        protected static string MD5(byte[] input)
        {
            byte[] hash = MD5Summer.ComputeHash(input);
            StringBuilder sb = new StringBuilder(20);
            for (int i = 0; i < hash.Length; i++)
            {
                sb.Append(hash[i].ToString("x2"));
            }
            return sb.ToString();
        }
    }
    public enum WatchOptions
    {
        NoChange,
        UsePreferences,
        Watch,
        Unwatch
    }
    [Flags]
    public enum ActionOptions
    {
        None = 0,
        CheckMaxlag = 1,
        RequireLogin = 2,
        CheckNewMessages = 4,
        All = CheckMaxlag | RequireLogin | CheckNewMessages
    };
}

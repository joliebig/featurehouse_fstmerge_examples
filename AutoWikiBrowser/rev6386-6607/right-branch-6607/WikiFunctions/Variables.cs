using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Text.RegularExpressions;
using WikiFunctions.Lists.Providers;
using WikiFunctions.Plugin;
using WikiFunctions.Background;
using System.Net;
using System.Threading;
namespace WikiFunctions
{
    public enum ProjectEnum { wikipedia, wiktionary, wikisource, wikiquote, wikiversity, wikibooks, wikinews, species, commons, meta, mediawiki, wikia, custom }
    public static partial class Variables
    {
        static Variables()
        {
            CanonicalNamespaces[-2] = "Media:";
            CanonicalNamespaces[-1] = "Special:";
            CanonicalNamespaces[1] = "Talk:";
            CanonicalNamespaces[2] = "User:";
            CanonicalNamespaces[3] = "User talk:";
            CanonicalNamespaces[4] = "Project:";
            CanonicalNamespaces[5] = "Project talk:";
            CanonicalNamespaces[6] = "File:";
            CanonicalNamespaces[7] = "File talk:";
            CanonicalNamespaces[8] = "MediaWiki:";
            CanonicalNamespaces[9] = "MediaWiki talk:";
            CanonicalNamespaces[10] = "Template:";
            CanonicalNamespaces[11] = "Template talk:";
            CanonicalNamespaces[12] = "Help:";
            CanonicalNamespaces[13] = "Help talk:";
            CanonicalNamespaces[14] = "Category:";
            CanonicalNamespaces[15] = "Category talk:";
            CanonicalNamespaceAliases = PrepareAliases(CanonicalNamespaces);
            CanonicalNamespaceAliases[6].Add("Image:");
            CanonicalNamespaceAliases[7].Add("Image talk:");
            if (!Globals.UnitTestMode)
                SetProject("en", ProjectEnum.wikipedia);
            else
            {
                SetToEnglish();
                RegenerateRegexes();
            }
            PHP5 = false;
            TypoSummaryTag = "typos fixed: ";
            AWBDefaultSummaryTag();
        }
        public static string Revision
        {
            get
            {
                return (!m_Revision.Contains("$")) ? m_Revision.Replace("/", "-") : "?";
            }
        }
        public static string RetfPath;
        public static IAutoWikiBrowser MainForm
        { get; set; }
        public static Profiler Profiler = new Profiler();
        public static readonly Dictionary<int, string> CanonicalNamespaces = new Dictionary<int, string>(20);
        public static readonly Dictionary<int, List<string> > CanonicalNamespaceAliases;
        public static Dictionary<int, string> Namespaces = new Dictionary<int, string>(40);
        public static Dictionary<int, List<string> > NamespaceAliases;
        public static readonly Dictionary<int, string> NamespacesCaseInsensitive = new Dictionary<int, string>(24);
        public static Dictionary<string, List<string> > MagicWords = new Dictionary<string, List<string> >();
        public static string URLLong
        { get { return URL + URLEnd; } }
        public static string URLIndex
        { get { return URLLong + IndexPHP; } }
        public static string URLApi
        { get { return URLLong + ApiPHP; } }
        public static bool RTL
        { get; set; }
        public static string[] MonthNames;
        public static readonly string[] ENLangMonthNames = new[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        private static string URLEnd = "/w/";
        public static string URL = "http://en.wikipedia.org";
        public static string Host { get { return new Uri(URL).Host; } }
        public static string ScriptPath
        {
            get { return URLEnd.Substring(0, URLEnd.LastIndexOf('/')); }
        }
        public static ProjectEnum Project { get; private set; }
        public static string LangCode { get; internal set; }
        public static bool IsWikimediaProject
        { get { return Project <= ProjectEnum.species; } }
        public static bool IsWikipediaEN
        { get { return (Project == ProjectEnum.wikipedia && LangCode == "en"); } }
        public static bool IsWikimediaMonolingualProject
        {
            get
            {
                return (Project == ProjectEnum.commons || Project == ProjectEnum.meta
                  || Project == ProjectEnum.species || Project == ProjectEnum.mediawiki);
            }
        }
        public static bool IsCustomProject
        { get { return Project == ProjectEnum.custom; } }
        public static bool IsWikia
        { get { return Project == ProjectEnum.wikia; } }
        public static string CustomProject
        { get; private set; }
        public static string IndexPHP { get; private set; }
        public static string ApiPHP { get; private set; }
        private static bool usePHP5;
        public static bool PHP5
        {
            get { return usePHP5; }
            set
            {
                usePHP5 = value;
                IndexPHP = value ? "index.php5" : "index.php";
                ApiPHP = value ? "api.php5" : "api.php";
            }
        }
        public static string TypoSummaryTag
        { get; private set; }
        private static string mSummaryTag = " using ";
        public static string SummaryTag
        { get { return mSummaryTag + WPAWB; } }
        public static string WPAWB
        { get; private set; }
        internal static Dictionary<int, List<string> > PrepareAliases(Dictionary<int, string> namespaces)
        {
            Dictionary<int, List<string> > ret = new Dictionary<int, List<string> >(namespaces.Count);
            foreach (int n in namespaces.Keys)
            {
                ret[n] = new List<string>();
            }
            return ret;
        }
        private static void AWBDefaultSummaryTag()
        {
            mSummaryTag = " using ";
            WPAWB = "[[Project:AWB|AWB]]";
        }
        public static readonly List<string> UnderscoredTitles = new List<string>();
        private static readonly List<BackgroundRequest> DelayedRequests = new List<BackgroundRequest>();
        static void CancelBackgroundRequests()
        {
            lock (DelayedRequests)
            {
                foreach (BackgroundRequest b in DelayedRequests) b.Abort();
                DelayedRequests.Clear();
            }
        }
        public static void WaitForDelayedRequests()
        {
            do
            {
                lock(DelayedRequests)
                {
                    if (DelayedRequests.Count == 0) return;
                }
                Thread.Sleep(100);
            } while (true);
        }
        internal static void LoadUnderscores(params string[] cats)
        {
            BackgroundRequest r = new BackgroundRequest(UnderscoresLoaded) {HasUI = false};
            DelayedRequests.Add(r);
            r.GetList(new CategoryListProvider(), cats);
        }
        private static void UnderscoresLoaded(BackgroundRequest req)
        {
            lock (DelayedRequests)
            {
                DelayedRequests.Remove(req);
                UnderscoredTitles.Clear();
                foreach (Article a in (List<Article>)req.Result)
                {
                    UnderscoredTitles.Add(a.Name);
                }
            }
        }
        static IWebProxy SystemProxy;
        public static HttpWebRequest PrepareWebRequest(string url, string userAgent)
        {
            HttpWebRequest r = (HttpWebRequest)WebRequest.Create(url);
            if (SystemProxy != null) r.Proxy = SystemProxy;
            r.UserAgent = string.IsNullOrEmpty(userAgent) ? Tools.DefaultUserAgentString : userAgent;
            r.AutomaticDecompression = DecompressionMethods.Deflate | DecompressionMethods.GZip;
            r.Proxy.Credentials = CredentialCache.DefaultCredentials;
            return r;
        }
        public static HttpWebRequest PrepareWebRequest(string url)
        { return PrepareWebRequest(url, ""); }
        public static void RefreshProxy()
        {
            SystemProxy = WebRequest.GetSystemWebProxy();
            if (SystemProxy.IsBypassed(new Uri(URL)))
            {
                SystemProxy = null;
            }
        }
        public static string AWBVersionString(string version)
        {
            return "*" + WPAWB + " version " + version + Environment.NewLine;
        }
        public static string Stub;
        public static string SectStub;
        public static Regex SectStubRegex;
        public static void SetProject(string langCode, ProjectEnum projectName)
        {
            SetProject(langCode, projectName, "");
        }
        static readonly string[] AttackSites = new[]
            {
                "encyclopediadramatica",
                "conservapedia.com",
                "traditio.",
                "volgota.com",
                "wikireality.ru"
            };
        public static void SetProject(string langCode, ProjectEnum projectName, string customProject)
        {
            Namespaces.Clear();
            CancelBackgroundRequests();
            UnderscoredTitles.Clear();
            foreach(var s in AttackSites)
                if (customProject.Contains(s))
                {
                    MessageBox.Show("This software does not work on attack sites.");
                    Application.ExitThread();
                }
            Project = projectName;
            LangCode = langCode;
            CustomProject = customProject;
            RefreshProxy();
            URLEnd = "/w/";
            AWBDefaultSummaryTag();
            Stub = "[Ss]tub";
            MonthNames = ENLangMonthNames;
            SectStub = @"\{\{[Ss]ect";
            SectStubRegex = new Regex(SectStub, RegexOptions.Compiled);
            if (IsCustomProject)
            {
                LangCode = "en";
                int x = customProject.IndexOf('/');
                if (x > 0)
                {
                    URLEnd = customProject.Substring(x, customProject.Length - x);
                    customProject = customProject.Substring(0, x);
                }
                URL = "http://" + CustomProject;
            }
            else
                URL = "http://" + LangCode + "." + Project + ".org";
            switch (projectName)
            {
                case ProjectEnum.wikipedia:
                case ProjectEnum.wikinews:
                case ProjectEnum.wikisource:
                case ProjectEnum.wikibooks:
                case ProjectEnum.wikiquote:
                case ProjectEnum.wiktionary:
                case ProjectEnum.wikiversity:
                    switch (langCode)
                    {
                        case "en":
                            if (projectName == ProjectEnum.wikipedia)
                                SetToEnglish();
                            break;
                        case "ar":
                            mSummaryTag = " ";
                            WPAWB = "Ø¨Ø§Ø³ØªØ®Ø¯Ø§Ù [[ÙÙÙÙØ¨ÙØ¯ÙØ§:Ø£ÙØ¨|Ø§ÙØ£ÙØªÙÙÙÙÙ Ø¨Ø±Ø§ÙØ²Ø±]]";
                            TypoSummaryTag = ".Ø§ÙØ£Ø®Ø·Ø§Ø¡ Ø§ÙÙØµØ­Ø­Ø©: ";
                            break;
                        case "bg":
                            mSummaryTag = " ÑÐµÐ´Ð°ÐºÑÐ¸ÑÐ°Ð½Ð¾ Ñ ";
                            WPAWB = "AWB";
                            break;
                        case "ca":
                            mSummaryTag = " ";
                            WPAWB = "[[ViquipÃ¨dia:AutoWikiBrowser|AWB]]";
                            break;
                        case "da":
                            mSummaryTag = " ved brug af ";
                            WPAWB = "[[en:Wikipedia:AutoWikiBrowser|AWB]]";
                            break;
                        case "de":
                            mSummaryTag = " mit ";
                            TypoSummaryTag = ", Schreibweise:";
                            break;
      case "el":
       mSummaryTag = " Î¼Îµ ÏÎ· ÏÏÎ®ÏÎ· ";
                            WPAWB = "[[ÎÎ¹ÎºÎ¹ÏÎ±Î¯Î´ÎµÎ¹Î±:AutoWikiBrowser|AWB]]";
       break;
                        case "eo":
                            mSummaryTag = " ";
                            WPAWB = "[[Vikipedio:AutoWikiBrowser|AWB]]";
                            break;
                        case "hu":
                            mSummaryTag = " ";
                            WPAWB = "[[WikipÃ©dia:AutoWikiBrowser|AWB]]";
                            break;
                        case "ku":
                            mSummaryTag = " ";
                            WPAWB = "[[WÃ®kÃ®pediya:AutoWikiBrowser|AWB]]";
                            break;
                        case "nl":
                            mSummaryTag = " met ";
                            break;
                        case "pl":
                            SectStub = @"\{\{[Ss]ek";
                            SectStubRegex = new Regex(SectStub, RegexOptions.Compiled);
                            break;
                        case "pt":
                            mSummaryTag = " utilizando ";
                            break;
                        case "ru":
                            mSummaryTag = " Ñ Ð¿Ð¾Ð¼Ð¾ÑÑÑ ";
                            Stub = "(?:[Ss]tub|[ÐÐ·]Ð°Ð³Ð¾ÑÐ¾Ð²ÐºÐ°)";
                            break;
                        case "sk":
                            mSummaryTag = " ";
                            WPAWB = "[[WikipÃ©dia:AutoWikiBrowser|AWB]]";
                            break;
                        case "sl":
                            mSummaryTag = " ";
                            WPAWB = "[[Wikipedija:AutoWikiBrowser|AWB]]";
                            Stub = "(?:[Ss]tub|[Å Å¡]krbina)";
                            break;
                        case "uk":
                            Stub = "(?:[Ss]tub|[ÐÐ´]Ð¾ÑÐ¾Ð±Ð¸ÑÐ¸)";
                            mSummaryTag = " Ð·Ð° Ð´Ð¾Ð¿Ð¾Ð¼Ð¾Ð³Ð¾Ñ ";
                            WPAWB = "[[ÐÑÐºÑÐ¿ÐµÐ´ÑÑ:AutoWikiBrowser|AWB]]";
                            break;
                        default:
                            break;
                    }
                    break;
                case ProjectEnum.commons:
                    URL = "http://commons.wikimedia.org";
                    LangCode = "en";
                    break;
                case ProjectEnum.meta:
                    URL = "http://meta.wikimedia.org";
                    LangCode = "en";
                    break;
                case ProjectEnum.mediawiki:
                    URL = "http://www.mediawiki.org";
                    LangCode = "en";
                    break;
                case ProjectEnum.species:
                    URL = "http://species.wikimedia.org";
                    LangCode = "en";
                    break;
                case ProjectEnum.wikia:
                    URL = "http://" + customProject + ".wikia.com";
                    URLEnd = "/";
                    break;
                case ProjectEnum.custom:
                    URLEnd = "";
                    break;
            }
            RefreshProxy();
            if (MainForm != null && MainForm.TheSession != null)
            {
                if (!MainForm.TheSession.UpdateProject())
                {
                    LangCode = "en";
                    Project = ProjectEnum.wikipedia;
                    SetToEnglish();
                }
            }
            RegenerateRegexes();
            RetfPath = Namespaces[Namespace.Project] + "AutoWikiBrowser/Typos";
            foreach (string s in Namespaces.Values)
            {
                System.Diagnostics.Trace.Assert(s.EndsWith(":"), "Internal error: namespace does not end with ':'.",
                                                "Please contact a developer.");
            }
            System.Diagnostics.Trace.Assert(!Namespaces.ContainsKey(0), "Internal error: key exists for namespace 0.",
                                            "Please contact a developer.");
        }
        private static void RegenerateRegexes()
        {
            NamespacesCaseInsensitive.Clear();
            foreach (int ns in Namespaces.Keys)
            {
                NamespacesCaseInsensitive.Add(ns, "(?i:"
                    + WikiRegexes.GenerateNamespaceRegex(ns) + @")\s*:");
            }
            WikiRegexes.MakeLangSpecificRegexes();
        }
        private static void SetToEnglish()
        {
            foreach (var i in CanonicalNamespaces.Keys)
                Namespaces[i] = CanonicalNamespaces[i];
            Namespaces[4] = "Wikipedia:";
            Namespaces[5] = "Wikipedia talk:";
            Namespaces[100] = "Portal:";
            Namespaces[101] = "Portal talk:";
            Namespaces[108] = "Book:";
            Namespaces[109] = "Book talk:";
            mSummaryTag = " using ";
            NamespaceAliases = CanonicalNamespaceAliases;
            MonthNames = ENLangMonthNames;
            SectStub = @"\{\{[Ss]ect";
            SectStubRegex = new Regex(SectStub, RegexOptions.Compiled);
            Stub = "[Ss]tub";
            LangCode = "en";
            RTL = false;
        }
        public static string NonPrettifiedURL(string title)
        {
            return URLIndex + "?title=" + Tools.WikiEncode(title);
        }
        public static string GetArticleHistoryURL(string title)
        {
            return (NonPrettifiedURL(title) + "&action=history");
        }
        public static string GetEditURL(string title)
        {
            return (NonPrettifiedURL(title) + "&action=edit");
        }
        public static string GetAddToWatchlistURL(string title)
        {
            return (NonPrettifiedURL(title) + "&action=watch");
        }
        public static string GetRemoveFromWatchlistURL(string title)
        {
            return (NonPrettifiedURL(title) + "&action=unwatch");
        }
        public static string GetUserTalkURL(string username)
        {
            return URLIndex + "?title=User_talk:" + Tools.WikiEncode(username) + "&action=purge";
        }
        public static string GetPlainTextURL(string title)
        {
            return NonPrettifiedURL(title) + "&action=raw";
        }
    }
    public enum WikiStatusResult
    {
        Error,
        NotLoggedIn,
        NotRegistered,
        OldVersion,
        NoRights,
        Registered,
        PendingUpdate
    }
}

using System;
using System.Web;
using System.Text.RegularExpressions;
using System.Collections.Generic;
using System.Xml;
using WikiFunctions.Controls.Lists;
namespace WikiFunctions.Lists.Providers
{
    public class CategoryListProvider : CategoryProviderBase
    {
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            List<Article> list = new List<Article>();
            foreach (string page in PrepareCategories(searchCriteria))
            {
                list.AddRange(GetListing(page, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "Category"; } }
    }
    public class CategoryRecursiveListProvider : CategoryProviderBase
    {
        public const int MaxDepth = 30;
        int depth = MaxDepth;
        public int Depth
        {
            get { return depth; }
            set { depth = Math.Min(value, MaxDepth); }
        }
        public CategoryRecursiveListProvider()
            : this(MaxDepth)
        { }
        public CategoryRecursiveListProvider(int depth)
        {
            Depth = depth;
            Limit = 200000;
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            List<Article> list = new List<Article>();
            lock (Visited)
            {
                Visited.Clear();
                foreach (string page in PrepareCategories(searchCriteria))
                {
                    list.AddRange(RecurseCategory(page, list.Count, Depth));
                }
                Visited.Clear();
            }
            return list;
        }
        public override string DisplayText
        { get { return "Category (recursive)"; } }
    }
    public class CategoryRecursiveOneLevelListProvider : CategoryRecursiveListProvider
    {
        public CategoryRecursiveOneLevelListProvider()
            : base(1)
        { }
        public override string DisplayText
        {
            get { return "Category (recurse 1 level)"; }
        }
    }
    public class CategoryRecursiveUserDefinedLevelListProvider : CategoryRecursiveListProvider
    {
        public CategoryRecursiveUserDefinedLevelListProvider()
            : base(0)
        { }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            int userDepth = Tools.GetNumberFromUser(false);
            if (userDepth < 0)
                return new List<Article>();
            Depth = userDepth;
            return base.MakeList(searchCriteria);
        }
        public override string DisplayText
        {
            get { return "Category (recurse user defined level)"; }
        }
    }
    public class CategoriesOnPageListProvider : ApiListProviderBase
    {
        protected string clshow;
        static readonly List<string> pe = new List<string>(new[] { "cl" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "categories" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            searchCriteria = Tools.FirstToUpperAndRemoveHashOnArray(searchCriteria);
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "prop=categories&cllimit=max&titles="
                             + HttpUtility.UrlEncode(page) + "&clshow=" + clshow;
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "Categories on page"; } }
        public override string UserInputTextBoxText
        { get { return "Pages"; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
    }
    public class CategoriesOnPageNoHiddenListProvider : CategoriesOnPageListProvider
    {
        public CategoriesOnPageNoHiddenListProvider()
        {
            clshow = "!hidden";
        }
        public override string DisplayText
        { get { return base.DisplayText + " (no hidden cats)"; } }
    }
    public class CategoriesOnPageOnlyHiddenListProvider : CategoriesOnPageListProvider
    {
        public CategoriesOnPageOnlyHiddenListProvider()
        {
            clshow = "hidden";
        }
        public override string DisplayText
        { get { return base.DisplayText + " (only hidden cats)"; } }
    }
    public class WhatLinksHereListProvider : ApiListProviderBase, ISpecialPageProvider
    {
        public WhatLinksHereListProvider()
        { }
        public WhatLinksHereListProvider(int limit)
        {
            Limit = limit;
        }
        static readonly List<string> pe = new List<string>(new[] { "bl" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "backlinks" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        protected bool IncludeWhatLinksToRedirects;
        protected string Blfilterredir;
        public List<Article> MakeList(int Namespace, params string[] searchCriteria)
        {
            return MakeList(Namespace.ToString(), searchCriteria);
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList(Namespace.Article, searchCriteria);
        }
        protected List<Article> MakeList(string Namespace, params string[] searchCriteria)
        {
            searchCriteria = Tools.FirstToUpperAndRemoveHashOnArray(searchCriteria);
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "list=backlinks&bltitle="
                             + HttpUtility.UrlEncode(page) + "&bllimit=max&blnamespace=" + Namespace;
                if (IncludeWhatLinksToRedirects)
                    url += "&blredirect";
                if (!string.IsNullOrEmpty(Blfilterredir))
                    url += "&blfilterredir=" + Blfilterredir;
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "What links here"; } }
        public override string UserInputTextBoxText
        { get { return "What links to:"; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
        public bool PagesNeeded
        {
            get { return false; }
        }
        public bool NamespacesEnabled
        {
            get { return true; }
        }
    }
    public class WhatLinksHereAllNSListProvider : WhatLinksHereListProvider
    {
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList("", searchCriteria);
        }
        public override string DisplayText
        { get { return "What links here (all NS)"; } }
    }
    public class WhatLinksHereAndToRedirectsAllNSListProvider : WhatLinksHereAllNSListProvider
    {
        public WhatLinksHereAndToRedirectsAllNSListProvider(int limit)
            : this()
        {
            Limit = limit;
        }
        public WhatLinksHereAndToRedirectsAllNSListProvider()
        {
            IncludeWhatLinksToRedirects = true;
        }
        public override string DisplayText
        { get { return base.DisplayText + " (and to redirects)"; } }
    }
    public class WhatLinksHereAndToRedirectsListProvider : WhatLinksHereListProvider
    {
        public WhatLinksHereAndToRedirectsListProvider(int limit)
            : this()
        {
            Limit = limit;
        }
        public WhatLinksHereAndToRedirectsListProvider()
        {
            IncludeWhatLinksToRedirects = true;
        }
        public override string DisplayText
        { get { return base.DisplayText + " (and to redirects)"; } }
    }
    public class WhatLinksHereAndPageRedirectsExcludingTheRedirectsListProvider : WhatLinksHereListProvider
    {
        public WhatLinksHereAndPageRedirectsExcludingTheRedirectsListProvider(int limit)
            : this()
        {
            Limit = limit;
        }
        public WhatLinksHereAndPageRedirectsExcludingTheRedirectsListProvider()
        {
            Blfilterredir = "nonredirects";
            IncludeWhatLinksToRedirects = true;
        }
        public override string DisplayText
        { get { return base.DisplayText + " directly"; } }
        protected override bool EvaluateXmlElement(XmlTextReader xml)
        {
            return !xml.MoveToAttribute("redirect");
        }
    }
    public class WhatLinksHereExcludingPageRedirectsListProvider : WhatLinksHereListProvider
    {
        public WhatLinksHereExcludingPageRedirectsListProvider(int limit)
            : this()
        {
            Limit = limit;
        }
        public WhatLinksHereExcludingPageRedirectsListProvider()
        {
            Blfilterredir = "nonredirects";
        }
        public override string DisplayText
        { get { return base.DisplayText + " (no redirects)"; } }
    }
    public class RedirectsListProvider : WhatLinksHereListProvider
    {
        public RedirectsListProvider()
        {
            Blfilterredir = "redirects";
        }
        public override string DisplayText
        { get { return "What redirects here"; } }
        public override string UserInputTextBoxText
        { get { return "Redirects to"; } }
    }
    public class WhatTranscludesPageListProvider : ApiListProviderBase, ISpecialPageProvider
    {
        static readonly List<string> pe = new List<string>(new[] { "ei" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "embeddedin" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList(Namespace.Article, searchCriteria);
        }
        public virtual List<Article> MakeList(int Namespace, params string[] searchCriteria)
        {
            return MakeList(Namespace.ToString(), searchCriteria);
        }
        protected List<Article> MakeList(string Namespace, params string[] searchCriteria)
        {
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "list=embeddedin&eititle="
                             + HttpUtility.UrlEncode(page) + "&eilimit=max&einamespace=" + Namespace;
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "What transcludes page"; } }
        public override string UserInputTextBoxText
        { get { return "What embeds:"; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected()
        { }
        public virtual bool PagesNeeded
        { get { return true; } }
        public bool NamespacesEnabled
        { get { return true; } }
    }
    public class WhatTranscludesPageAllNSListProvider : WhatTranscludesPageListProvider
    {
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList("", searchCriteria);
        }
        public override string DisplayText
        { get { return "What transcludes page (all NS)"; } }
    }
    public class LinksOnPageListProvider : ApiListProviderBase
    {
        static readonly List<string> pe = new List<string>(new[] { "pl" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "links" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            searchCriteria = Tools.FirstToUpperAndRemoveHashOnArray(searchCriteria);
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "prop=links&titles="
                             + HttpUtility.UrlEncode(page) + "&pllimit=max";
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "Links on page"; } }
        public override string UserInputTextBoxText
        { get { return "Links on:"; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
    }
    public class LinksOnPageOnlyRedListProvider : ApiListProviderBase
    {
        public LinksOnPageOnlyRedListProvider()
        {
            Limit = 5000;
        }
        static readonly List<string> pe = new List<string>(new[] { "page" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "pages" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            searchCriteria = Tools.FirstToUpperAndRemoveHashOnArray(searchCriteria);
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "generator=links&titles="
                             + HttpUtility.UrlEncode(page) + "&gpllimit=max";
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        protected override bool EvaluateXmlElement(XmlTextReader xml)
        {
            return xml.MoveToAttribute("missing");
        }
        public override string DisplayText
        { get { return "Links on page (only redlinks)"; } }
        public override string UserInputTextBoxText
        { get { return "Links on"; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
    }
    public class LinksOnPageOnlyBlueListProvider : LinksOnPageOnlyRedListProvider
    {
        protected override bool EvaluateXmlElement(XmlTextReader xml)
        {
            return !base.EvaluateXmlElement(xml);
        }
        public override string DisplayText
        { get { return "Links on page (only bluelinks)"; } }
    }
    public class ImagesOnPageListProvider : ApiListProviderBase
    {
        static readonly List<string> pe = new List<string>(new[] { "im" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "images" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            searchCriteria = Tools.FirstToUpperAndRemoveHashOnArray(searchCriteria);
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "prop=images&titles="
                             + HttpUtility.UrlEncode(page) + "&imlimit=max";
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "Images on page"; } }
        public override string UserInputTextBoxText
        { get { return "Images on:"; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
    }
    public class TransclusionsOnPageListProvider : ApiListProviderBase
    {
        static readonly List<string> pe = new List<string>(new[] { "tl" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "templates" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            searchCriteria = Tools.FirstToUpperAndRemoveHashOnArray(searchCriteria);
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "prop=templates&titles="
                             + HttpUtility.UrlEncode(page) + "&tllimit=max";
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "Transclusions on page"; } }
        public override string UserInputTextBoxText
        { get { return "Transclusions on:"; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
    }
    public class UserContribsListProvider : ApiListProviderBase, ISpecialPageProvider
    {
        static readonly List<string> pe = new List<string>(new[] { "item" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "usercontribs" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        protected string uclimit = "max";
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList("", searchCriteria);
        }
        public List<Article> MakeList(string @namespace, string[] searchCriteria)
        {
            searchCriteria = Tools.FirstToUpperAndRemoveHashOnArray(searchCriteria);
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "list=usercontribs&ucuser=" +
                             Tools.WikiEncode(
                                 Regex.Replace(page, Variables.NamespacesCaseInsensitive[Namespace.Category], ""))
                             + "&uclimit=" + uclimit
                             + "&ucnamespace=" + @namespace;
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "User contribs"; } }
        public override string UserInputTextBoxText
        { get { return Variables.Namespaces[Namespace.User]; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
        public override bool RunOnSeparateThread
        { get { return true; } }
        public List<Article> MakeList(int @namespace, params string[] searchCriteria)
        {
            return MakeList(@namespace.ToString(), searchCriteria);
        }
        public bool PagesNeeded
        {
            get { return true; }
        }
        public bool NamespacesEnabled
        {
            get { return true; }
        }
    }
    public class UserContribUserDefinedNumberListProvider : UserContribsListProvider
    {
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            Limit = Tools.GetNumberFromUser(true);
            if (Limit < 500)
                uclimit = Limit.ToString();
            return base.MakeList(searchCriteria);
        }
        public override string DisplayText
        { get { return "User contribs (user defined number)"; } }
    }
    public class ImageFileLinksListProvider : ApiListProviderBase
    {
        static readonly List<string> pe = new List<string>(new[] { "iu" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "imageusage" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            searchCriteria = Tools.FirstToUpperAndRemoveHashOnArray(searchCriteria);
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string image = Regex.Replace(page, "^" + Variables.Namespaces[Namespace.File],
                                             "", RegexOptions.IgnoreCase);
                image = HttpUtility.UrlEncode(image);
                string url = "list=imageusage&iutitle=Image:"
                             + image + "&iulimit=max";
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "Image file links"; } }
        public override string UserInputTextBoxText
        { get { return Variables.Namespaces[Namespace.File]; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
    }
    public class WikiSearchListProvider : ApiListProviderBase
    {
        protected string Srwhat = "text";
        static readonly List<string> pe = new List<string>(new[] { "p" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "search" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public WikiSearchListProvider()
        {
            Limit = 1000;
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "list=search&srwhat=" + Srwhat + "&srsearch=all:'"
                             + HttpUtility.UrlEncode(page) + "'&srlimit=max";
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "Wiki search (text)"; } }
        public override string UserInputTextBoxText
        { get { return "Wiki search:"; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
    }
    public class WikiTitleSearchListProvider : WikiSearchListProvider
    {
        public WikiTitleSearchListProvider()
        {
            Srwhat = "title";
        }
        public override string DisplayText
        { get { return "Wiki search (title)"; } }
    }
    public class MyWatchlistListProvider : ApiListProviderBase
    {
        static readonly List<string> pe = new List<string>(new[] { "wr" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "watchlistraw" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return ApiMakeList("list=watchlistraw&wrlimit=max", 0);
        }
        public override string DisplayText
        { get { return "My watchlist"; } }
        public override string UserInputTextBoxText
        { get { return ""; } }
        public override bool UserInputTextBoxEnabled
        { get { return false; } }
        public override void Selected() { }
    }
    public class DatabaseScannerListProvider : IListProvider
    {
        private readonly ListMaker LMaker;
        public DatabaseScannerListProvider(ListMaker lm)
        {
            LMaker = lm;
        }
        public List<Article> MakeList(params string[] searchCriteria)
        {
            new DBScanner.DatabaseScanner(LMaker).Show();
            return null;
        }
        public string DisplayText
        { get { return "Database dump"; } }
        public string UserInputTextBoxText
        { get { return ""; } }
        public bool UserInputTextBoxEnabled
        { get { return false; } }
        public void Selected() { }
        public bool RunOnSeparateThread
        { get { return false; } }
    }
    public class RandomPagesSpecialPageProvider : ApiListProviderBase, ISpecialPageProvider
    {
        protected string Extra;
        public RandomPagesSpecialPageProvider()
        {
            Limit = 100;
        }
        static readonly List<string> pe = new List<string>(new[] { "page" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "random" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList(Namespace.Article, searchCriteria);
        }
        public List<Article> MakeList(int Namespace, string[] searchCriteria)
        {
            List<Article> list = new List<Article>();
            string url = "list=random&rnnamespace=" + Namespace +
                         "&rnlimit=max" + Extra;
            list.AddRange(ApiMakeList(url, list.Count));
            return list;
        }
        public override string DisplayText
        { get { return "Random pages"; } }
        public override string UserInputTextBoxText
        { get { return ""; } }
        public override bool UserInputTextBoxEnabled
        { get { return false; } }
        public override void Selected() { }
        public bool PagesNeeded
        { get { return false; } }
        public bool NamespacesEnabled
        { get { return true; } }
    }
    public class RandomRedirectsSpecialPageProvider : RandomPagesSpecialPageProvider
    {
        public RandomRedirectsSpecialPageProvider()
        {
            Extra = "&rnredirect";
        }
        public override string DisplayText
        { get { return "Random redirects"; } }
    }
    public class AllPagesSpecialPageProvider : ApiListProviderBase, ISpecialPageProvider
    {
        static readonly List<string> pe = new List<string>(new[] { "p" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "allpages" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        protected string From = "apfrom", Extra;
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList(Namespace.Article, searchCriteria);
        }
        public virtual List<Article> MakeList(int Namespace, params string[] searchCriteria)
        {
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "list=allpages&" + From + "=" +
                             HttpUtility.UrlEncode(page) + "&apnamespace=" + Namespace + "&aplimit=max" + Extra;
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string UserInputTextBoxText
        { get { return "All Pages"; } }
        public virtual bool PagesNeeded
        { get { return false; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
        public override string DisplayText
        { get { return UserInputTextBoxText; } }
        public virtual bool NamespacesEnabled
        { get { return true; } }
    }
    public class AllCategoriesSpecialPageProvider : AllPagesSpecialPageProvider
    {
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList(Namespace.Category, searchCriteria);
        }
        public override string DisplayText
        { get { return "All Categories"; } }
        public override string UserInputTextBoxText
        { get { return "Start Cat.:"; } }
        public override bool NamespacesEnabled
        { get { return false; } }
    }
    public class AllFilesSpecialPageProvider : AllPagesSpecialPageProvider
    {
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList(Namespace.File, searchCriteria);
        }
        public override string DisplayText
        { get { return "All Files"; } }
        public override string UserInputTextBoxText
        { get { return "Start File:"; } }
        public override bool NamespacesEnabled
        { get { return false; } }
    }
    public class AllRedirectsSpecialPageProvider : AllPagesSpecialPageProvider
    {
        public AllRedirectsSpecialPageProvider()
        {
            Extra = "&apfilterredir=redirects";
        }
        public override string DisplayText
        { get { return "All Redirects"; } }
        public override string UserInputTextBoxText
        { get { return "Start Redirect:"; } }
    }
    public class ProtectedPagesSpecialPageProvider : AllPagesSpecialPageProvider
    {
        private readonly ProtectionLevel Protlevel = new ProtectionLevel();
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList(Namespace.Article, searchCriteria);
        }
        public override List<Article> MakeList(int Namespace, params string[] searchCriteria)
        {
            Protlevel.ShowDialog();
            Extra = "&apprtype=" + Protlevel.Type + "&apprlevel=" + Protlevel.Level;
            return base.MakeList(Namespace, searchCriteria);
        }
        public override string DisplayText
        { get { return "Protected Pages"; } }
        public override string UserInputTextBoxText
        { get { return "Pages"; } }
    }
    public class PagesWithoutLanguageLinksSpecialPageProvider : AllPagesSpecialPageProvider
    {
        public PagesWithoutLanguageLinksSpecialPageProvider()
        {
            Extra = "&apfilterlanglinks=withoutlanglinks";
        }
        public override string DisplayText
        { get { return "Pages without Language Links"; } }
        public override string UserInputTextBoxText
        { get { return "Pages"; } }
    }
    public class PrefixIndexSpecialPageProvider : AllPagesSpecialPageProvider
    {
        public PrefixIndexSpecialPageProvider()
        {
            From = "apprefix";
        }
        public override string DisplayText
        { get { return "All Pages with prefix (Prefixindex)"; } }
        public override bool PagesNeeded
        { get { return true; } }
    }
    public class RecentChangesSpecialPageProvider : ApiListProviderBase, ISpecialPageProvider
    {
        static readonly List<string> pe = new List<string>(new[] { "rc" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "recentchanges" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList(Namespace.Article, searchCriteria);
        }
        public List<Article> MakeList(int Namespace, params string[] searchCriteria)
        {
            List<Article> list = new List<Article>();
            foreach (string page in searchCriteria)
            {
                string url = "list=recentchanges&rctitles=" + HttpUtility.UrlEncode(page) + "&rcnamespace=" + Namespace + "&rclimit=max";
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "Recent Changes"; } }
        public bool PagesNeeded
        { get { return false; } }
        public override string UserInputTextBoxText
        { get { return DisplayText; } }
        public override bool UserInputTextBoxEnabled
        { get { return false; } }
        public override void Selected() { }
        public bool NamespacesEnabled
        { get { return true; } }
    }
    public class NewPagesListProvider : ApiListProviderBase, ISpecialPageProvider
    {
        public NewPagesListProvider()
        {
            Limit = 100;
        }
        static readonly List<string> pe = new List<string>(new[] { "rc" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "recentchanges" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList(Namespace.Article, searchCriteria);
        }
        public List<Article> MakeList(int Namespace, params string[] searchCriteria)
        {
            List<Article> list = new List<Article>();
            string url = "list=recentchanges"
                         + "&rclimit=100&rctype=new&rcshow=!redirect&rcnamespace=" + Namespace;
            list.AddRange(ApiMakeList(url, list.Count));
            return list;
        }
        public override string DisplayText
        { get { return "New pages"; } }
        public override string UserInputTextBoxText
        { get { return ""; } }
        public override bool UserInputTextBoxEnabled
        { get { return false; } }
        public override void Selected() { }
        public bool PagesNeeded
        { get { return false; } }
        public bool NamespacesEnabled
        { get { return true; } }
    }
    public class LinkSearchSpecialPageProvider : ApiListProviderBase, ISpecialPageProvider
    {
        static readonly List<string> pe = new List<string>(new[] { "eu" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "exturlusage" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return MakeList(Namespace.Article, searchCriteria);
        }
        public List<Article> MakeList(int Namespace, params string[] searchCriteria)
        {
            List<Article> list = new List<Article>();
            foreach (string searchUrl in searchCriteria)
            {
                int index = searchUrl.IndexOf("://");
                string protocol, urlEnd;
                if (index > -1)
                {
                    protocol = searchUrl.Substring(0, index);
                    urlEnd = searchUrl.Substring(index + 3);
                }
                else
                {
                    protocol = "";
                    urlEnd = searchUrl;
                }
                string url = "list=exturlusage&euquery=" +
                             HttpUtility.UrlEncode(urlEnd) + "&eunamespace=" + Namespace +
                               "&euprotocol=" + protocol + "&eulimit=max";
                list.AddRange(ApiMakeList(url, list.Count));
            }
            return list;
        }
        public override string DisplayText
        { get { return "Link search"; } }
        public override string UserInputTextBoxText
        { get { return "URL:"; } }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public override void Selected() { }
        public bool PagesNeeded
        { get { return true; } }
        public bool NamespacesEnabled
        { get { return true; } }
    }
    public class DisambiguationPagesSpecialPageProvider : WhatTranscludesPageListProvider
    {
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            return base.MakeList(Namespace.Article, new[] { "Template:Disambiguation" });
        }
        public override List<Article> MakeList(int @namespace, params string[] searchCriteria)
        {
            return base.MakeList(@namespace, new[] { "Template:Disambiguation" });
        }
        public override string DisplayText
        { get { return "Disambiguation Pages"; } }
        public override string UserInputTextBoxText
        { get { return ""; } }
        public override bool UserInputTextBoxEnabled
        { get { return false; } }
        public override bool PagesNeeded
        { get { return false; } }
    }
    public class GalleryNewFilesSpecialPageProvider : ApiListProviderBase, ISpecialPageProvider
    {
        static readonly List<string> pe = new List<string>(new[] { "item" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        static readonly List<string> ac = new List<string>(new[] { "logevents" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public GalleryNewFilesSpecialPageProvider()
        {
            Limit = 1000;
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            List<Article> list = new List<Article>();
            list.AddRange(ApiMakeList("list=logevents&letype=upload&lelimit=max", list.Count));
            return list;
        }
        public List<Article> MakeList(int @namespace, string[] searchCriteria)
        {
            return MakeList("");
        }
        public override string DisplayText
        { get { return "New files"; } }
        public override string UserInputTextBoxText
        { get { return ""; } }
        public override bool UserInputTextBoxEnabled
        { get { return false; } }
        public override void Selected() { }
        public bool PagesNeeded
        { get { return false; } }
        public bool NamespacesEnabled
        { get { return false; } }
    }
}

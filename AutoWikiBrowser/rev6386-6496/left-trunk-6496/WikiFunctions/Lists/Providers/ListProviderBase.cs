using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using System.Web;
using System.Xml;
using System.IO;
using WikiFunctions.API;
namespace WikiFunctions.Lists.Providers
{
    public abstract class ApiListProviderBase : IListProvider
    {
        protected ApiListProviderBase()
        {
            Limit = 25000;
        }
        protected abstract ICollection<string> PageElements { get; }
        protected abstract ICollection<string> Actions { get; }
        public int Limit { get; set; }
        public List<Article> ApiMakeList(string url, int haveSoFar)
        {
            List<Article> list = new List<Article>();
            string postfix = "";
            string newUrl = url;
            ApiEdit editor = Variables.MainForm.TheSession.Editor.SynchronousEditor;
            while (list.Count + haveSoFar < Limit)
            {
                string text = editor.QueryApi(newUrl + postfix);
                XmlTextReader xml = new XmlTextReader(new StringReader(text));
                xml.MoveToContent();
                postfix = "";
                while (xml.Read())
                {
                    if (xml.Name == "query-continue")
                    {
                        XmlReader r = xml.ReadSubtree();
                        r.Read();
                        while (r.Read())
                        {
                            if (!r.IsStartElement()) continue;
                            if (!r.MoveToFirstAttribute())
                                throw new FormatException("Malformed element '" + r.Name + "' in <query-continue>");
                            postfix += "&" + r.Name + "=" + HttpUtility.UrlEncode(r.Value);
                        }
                    }
                    else if (PageElements.Contains(xml.Name) && xml.IsStartElement())
                    {
                        if (!EvaluateXmlElement(xml))
                            continue;
                        int ns;
                        int.TryParse(xml.GetAttribute("ns"), out ns);
                        string name = xml.GetAttribute("title");
                        if (string.IsNullOrEmpty(name))
                        {
                            System.Windows.Forms.MessageBox.Show(xml.ReadInnerXml());
                            break;
                        }
                        list.Add(ns >= 0 ? new Article(name, ns) : new Article(name));
                    }
                }
                if (string.IsNullOrEmpty(postfix)) break;
            }
            return list;
        }
        protected virtual bool EvaluateXmlElement(XmlTextReader xml)
        {
            return true;
        }
        public abstract List<Article> MakeList(params string[] searchCriteria);
        public abstract string DisplayText { get; }
        public abstract string UserInputTextBoxText { get; }
        public abstract bool UserInputTextBoxEnabled { get; }
        public abstract void Selected();
        public virtual bool RunOnSeparateThread
        { get { return true; } }
    }
    public abstract class CategoryProviderBase : ApiListProviderBase
    {
        readonly List<string> pe = new List<string>(new [] { "cm" });
        protected override ICollection<string> PageElements
        {
            get { return pe; }
        }
        readonly List<string> ac = new List<string>(new[] { "categorymembers" });
        protected override ICollection<string> Actions
        {
            get { return ac; }
        }
        public override string UserInputTextBoxText
        {
            get { return Variables.Namespaces[Namespace.Category]; }
        }
        public override void Selected() { }
        public override bool UserInputTextBoxEnabled
        { get { return true; } }
        public List<Article> GetListing(string category, int haveSoFar)
        {
            string title = HttpUtility.UrlEncode(category);
            string url = "?action=query&list=categorymembers&cmtitle=Category:" + title + "&cmcategory=" + title
                         + "&cmlimit=max";
            return ApiMakeList(url, 0);
        }
        public List<Article> GetListing(string category)
        {
            return GetListing(category, 0);
        }
        protected readonly List<string> Visited = new List<string>();
        public List<Article> RecurseCategory(string category, int haveSoFar, int depth)
        {
            if (haveSoFar > Limit || depth < 0) return new List<Article>();
            category = Tools.TurnFirstToUpper(Tools.WikiDecode(category));
            if (!Visited.Contains(category))
                Visited.Add(category);
            else
                return new List<Article>();
            List<Article> list = GetListing(category, haveSoFar);
            List<Article> fromSubcats = null;
            if (depth > 0 && haveSoFar + list.Count < Limit)
            {
                foreach (Article pg in list)
                {
                    if (haveSoFar + list.Count > Limit) break;
                    if (pg.NameSpaceKey == Namespace.Category && !Visited.Contains(pg.Name))
                    {
                        if (fromSubcats == null) fromSubcats = new List<Article>();
                        fromSubcats.AddRange(RecurseCategory(pg.NamespacelessName, haveSoFar + list.Count, depth - 1));
                    }
                }
            }
            if (fromSubcats != null && fromSubcats.Count > 0) list.AddRange(fromSubcats);
            return list;
        }
        public static IEnumerable<string> PrepareCategories(IEnumerable<string> source)
        {
            List<string> cats = new List<string>();
            foreach (string cat in source)
            {
                cats.Add(Regex.Replace(Tools.RemoveHashFromPageTitle(Tools.WikiDecode(cat)).Trim(),
                                       "^" + Variables.NamespacesCaseInsensitive[Namespace.Category], "").Trim());
            }
            return cats;
        }
    }
    [Serializable]
    public class ListProviderException : Exception
    {
        public ListProviderException(string message)
            : base(message)
        { }
    }
}

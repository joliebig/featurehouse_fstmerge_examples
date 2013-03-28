using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using WikiFunctions.Background;
using WikiFunctions.Controls;
namespace WikiFunctions.Parse
{
    public interface ITyposProvider
    {
        Dictionary<string, string> GetTypos();
    }
    public class TyposDownloader : ITyposProvider
    {
        private static readonly Regex TypoRegex = new Regex("<(?:Typo)?\\s+(?:word=\"(.*?)\"\\s+)?find=\"(.*?)\"\\s+replace=\"(.*?)\"\\s*/?>", RegexOptions.Compiled);
        public static string Url
        {
            get
            {
                string typolistUrl = Variables.RetfPath;
                if (!typolistUrl.StartsWith("http:"))
                    typolistUrl = Variables.GetPlainTextURL(typolistUrl);
                return typolistUrl;
            }
        }
        public Dictionary<string, string> GetTypos()
        {
            Dictionary<string, string> typoStrings = new Dictionary<string, string>();
            try
            {
                string text = "";
                try
                {
                    text = Tools.GetHTML(Url, Encoding.UTF8);
                }
                catch
                {
                    if (string.IsNullOrEmpty(text))
                    {
                        if (
                            MessageBox.Show(
                                "No list of typos was found. Would you like to use the list of typos from the English Wikipedia?\r\nOnly choose 'Yes' if this is an English wiki.",
                                "Load from English Wikipedia?", MessageBoxButtons.YesNo) != DialogResult.Yes)
                        {
                            return typoStrings;
                        }
                        try
                        {
                            text =
                                Tools.GetHTML(
                                    "http://en.wikipedia.org/w/index.php?title=Wikipedia:AutoWikiBrowser/Typos&action=raw",
                                    Encoding.UTF8);
                        }
                        catch (Exception ex)
                        {
                            MessageBox.Show("There was a problem loading the list of typos: " + ex.Message);
                        }
                    }
                }
                if (string.IsNullOrEmpty(text))
                    return typoStrings;
                foreach (Match m in TypoRegex.Matches(text))
                {
                    try
                    {
                        typoStrings.Add(m.Groups[2].Value, m.Groups[3].Value);
                    }
                    catch (ArgumentException)
                    {
                        RegExTypoFix.TypoError("Duplicate typo rule '" + m.Groups[2].Value + "' found.");
                        return new Dictionary<string, string>();
                    }
                }
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
                return new Dictionary<string, string>();
            }
            return typoStrings;
        }
    }
    class TypoGroup
    {
        private static readonly RegexOptions GroupedRegexOptionsForPlatform;
        static TypoGroup()
        {
            GroupedRegexOptionsForPlatform = (IntPtr.Size * 8) == 64 ? RegexOptions.None : RegexOptions.Compiled;
        }
        public TypoGroup(int groupSize, string match, string dontMatch, string prefix, string postfix)
        {
            GroupSize = groupSize;
            if (!string.IsNullOrEmpty(match)) Allow = new Regex(match, RegexOptions.Compiled);
            if (!string.IsNullOrEmpty(dontMatch)) Disallow = new Regex(dontMatch, RegexOptions.Compiled);
            Prefix = prefix;
            Postfix = postfix;
        }
        private readonly int GroupSize;
        private readonly Regex Allow, Disallow;
        private readonly string Prefix, Postfix;
        private List<Regex> Groups;
        public readonly List<KeyValuePair<Regex, string> > Typos = new List<KeyValuePair<Regex, string> >(20);
        public readonly List<TypoStat> Statistics = new List<TypoStat>();
        public bool IsSuitableTypo(string typo)
        {
            if (Allow != null && !Allow.IsMatch(typo)) return false;
            if (Disallow != null && Disallow.IsMatch(typo)) return false;
            return true;
        }
        public void Add(string typo, string replacement)
        {
            if (!IsSuitableTypo(typo))
            {
                throw new ArgumentException("Typo \"" + typo + "\" is not suitable for this group.");
            }
            Regex r;
            try
            {
                r = new Regex(typo, RegexOptions.Compiled);
            }
            catch (Exception ex)
            {
                RegExTypoFix.TypoError("Error in typo '" + typo + "': " + ex.Message);
                throw new TypoException();
            }
            Typos.Add(new KeyValuePair<Regex, string>(r, replacement));
        }
        public void MakeGroups()
        {
            if (GroupSize <= 1) return;
            Groups = new List<Regex>(5);
            for (int n = 0; n < (Typos.Count - 1) / GroupSize + 1; n++)
            {
                string s = "";
                for (int i = 0; i < Math.Min(GroupSize, Typos.Count - n * GroupSize); i++)
                {
                    string typo = Typos[n * GroupSize + i].Key.ToString();
                    if (Allow != null) typo = Allow.Match(typo).Groups[1].Value;
                    s += (s.Length == 0 ? "" : "|") + typo;
                }
                if (s.Length > 0)
                {
                    Groups.Add(new Regex(Prefix + "(" + s + ")" + Postfix, GroupedRegexOptionsForPlatform));
                }
            }
        }
        private void FixTypo(ref string articleText, ref string summary, KeyValuePair<Regex, string> typo, string articleTitle)
        {
            if (typo.Key.IsMatch(articleTitle))
                return;
            MatchCollection matches = typo.Key.Matches(articleText);
            if (matches.Count > 0)
            {
                TypoStat stats = new TypoStat(typo) { Total = matches.Count };
                articleText = typo.Key.Replace(articleText, typo.Value);
                int count = 0;
                foreach (Match m in matches)
                {
                    string res = typo.Key.Replace(m.Value, typo.Value);
                    if (res != m.Value)
                    {
                        count++;
                        if (1 == count)
                            summary += (summary.Length > 0 ? ", " : "") + m.Value + FindandReplace.Arrow + res;
                    }
                }
                if (count > 1)
                    summary += " (" + count + ")";
                stats.SelfMatches = stats.Total - count;
                Statistics.Add(stats);
            }
        }
        [Obsolete]
        public void FixTypos(ref string articleText, ref string summary, string articleTitle)
        {
            FixTypos(ref articleText, ref summary, articleTitle, articleText);
        }
        public void FixTypos(ref string articleText, ref string summary, string articleTitle, string originalArticleText)
        {
            Statistics.Clear();
            if (Groups != null)
                for (int i = 0; i < Groups.Count; i++)
                {
                    if (Groups[i].IsMatch(articleText))
                    {
                        for (int j = 0; j < Math.Min(GroupSize, Typos.Count - i * GroupSize); j++)
                        {
                            if (!Regex.IsMatch(originalArticleText, @"\[\[[^\[\]\r\n\|]*?" + Typos[i * GroupSize + j].Key + @"[^\[\]\r\n\|]*?(?:\]\]|\|)"))
                                FixTypo(ref articleText, ref summary, Typos[i * GroupSize + j], articleTitle);
                        }
                    }
                }
            else
            {
                foreach (KeyValuePair<Regex, string> typo in Typos)
                {
                    FixTypo(ref articleText, ref summary, typo, articleTitle);
                }
            }
        }
    }
    public class RegExTypoFix
    {
        private readonly BackgroundRequest TypoThread;
        public event BackgroundRequestComplete Complete;
        private readonly ITyposProvider Source;
        public RegExTypoFix()
            : this(true, new TyposDownloader())
        {
        }
        public RegExTypoFix(ITyposProvider provider)
            : this(true, provider)
        {
        }
        public RegExTypoFix(bool loadThreaded)
            : this(loadThreaded, new TyposDownloader())
        {
        }
        public RegExTypoFix(bool loadThreaded, ITyposProvider provider)
        {
            Source = provider;
            if (!loadThreaded)
            {
                MakeRegexes();
                return;
            }
            TypoThread = new BackgroundRequest(Complete);
            TypoThread.Execute(MakeRegexes);
        }
        public static string CreateRule(string find, string replace, string name)
        {
            return "<Typo word=\"" + name + "\" find=\"" + find + "\" replace=\"" + replace + "\" />";
        }
        public static string CreateRule(string find, string replace)
        {
            return CreateRule(find, replace, "<enter a name>");
        }
        public int TypoCount { get; private set; }
        public bool TyposLoaded { get; private set; }
        private static readonly Regex IgnoreRegex = new Regex("133t|-ology|\\(sic\\)|\\[sic\\]|\\[''sic''\\]|\\{\\{sic\\}\\}|spellfixno", RegexOptions.Compiled);
        static readonly Regex RemoveTail = new Regex(@"(\s|\n|\r|\*|#|:|ââââM?\d*ââââ)*$", RegexOptions.Compiled);
        private readonly List<TypoGroup> Groups = new List<TypoGroup>();
        internal static void TypoError(string error)
        {
            MessageBox.Show(error + "\r\n\r\nPlease visit the typo page at " + Variables.RetfPath +
                " and fix this error, then click 'File â Refresh status/typos' menu item to reload typos.",
                "RegexTypoFix error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
        private void MakeRegexes()
        {
            try
            {
                Groups.Clear();
                TypoCount = 0;
                Groups.Add(new TypoGroup(20, @"^\\b(.*)\\b$", @"[^\\]\\\d", @"\b", @"\b"));
                Groups.Add(new TypoGroup(5, null, @"[^\\]\\\d", "", ""));
                Groups.Add(new TypoGroup(1, null, null, "", ""));
                Dictionary<string, string> typoStrings = Source.GetTypos();
                TyposLoaded = typoStrings.Count > 0;
                if (TyposLoaded)
                {
                    foreach (KeyValuePair<string, string> rule in typoStrings)
                    {
                        foreach (TypoGroup grp in Groups)
                        {
                            if (grp.IsSuitableTypo(rule.Key))
                            {
                                grp.Add(rule.Key, rule.Value);
                                TypoCount++;
                                break;
                            }
                        }
                    }
                    foreach (TypoGroup grp in Groups)
                        grp.MakeGroups();
                }
            }
            catch (TypoException)
            {
                Groups.Clear();
                TypoCount = 0;
                TyposLoaded = false;
            }
            catch (Exception ex)
            {
                TyposLoaded = false;
                ErrorHandler.Handle(ex);
            }
            finally
            {
                if (Complete != null) Complete(TypoThread);
            }
        }
        public string PerformTypoFixes(string articleText, out bool noChange, out string summary, string articleTitle)
        {
            string originalArticleText = articleText;
            summary = "";
            if ((TypoCount == 0) || IgnoreRegex.IsMatch(articleText))
            {
                noChange = true;
                return articleText;
            }
            HideText removeText = new HideText(true, false, true);
            articleText = removeText.HideMore(articleText, true);
            Match m = RemoveTail.Match(articleText);
            string tail = m.Value;
            if (!string.IsNullOrEmpty(tail)) articleText = articleText.Remove(m.Index);
            string originalText = articleText;
            string strSummary = "";
            foreach (TypoGroup grp in Groups)
            {
                grp.FixTypos(ref articleText, ref strSummary, articleTitle, originalArticleText);
            }
            noChange = (originalText == articleText);
            summary = Variables.TypoSummaryTag + strSummary.Trim();
            return removeText.AddBackMore(articleText + tail);
        }
        public bool DetectTypo(string articleText, string articleTitle)
        {
            bool noChange;
            string summary;
            PerformTypoFixes(articleText, out noChange, out summary, articleTitle);
            return !noChange;
        }
        public List<TypoStat> GetStatistics()
        {
            List<TypoStat> res = new List<TypoStat>();
            foreach (TypoGroup g in Groups)
            {
                res.AddRange(g.Statistics);
            }
            return res;
        }
        public List<KeyValuePair<Regex, string> > GetTypos()
        {
            List<KeyValuePair<Regex, string> > lst = new List<KeyValuePair<Regex, string> >();
            foreach (TypoGroup grp in Groups)
            {
                lst.AddRange(grp.Typos);
            }
            return lst;
        }
    }
    public class TypoStat
    {
        public readonly string Find, Replace;
        public int Total, SelfMatches, FalsePositives;
        public TypoStatsListViewItem ListViewItem;
        public TypoStat(KeyValuePair<Regex, string> typo)
            : this(typo.Key.ToString(), typo.Value)
        { }
        public TypoStat(string find, string replace)
        {
            Find = find;
            Replace = replace;
        }
        public override string ToString()
        {
            return Find + " â " + Replace;
        }
        public override int GetHashCode()
        {
            return Find.GetHashCode() + Replace.GetHashCode();
        }
        public override bool Equals(object obj)
        {
            TypoStat item = (obj as TypoStat);
            if (item == null)
                return false;
            return ((item.Find == Find) && (item.Replace == Replace));
        }
    }
    [Serializable]
    public class TypoException : ApplicationException
    {
        public TypoException() { }
        public TypoException(string message)
            : base(message) { }
        public TypoException(string message, Exception inner)
            : base(message, inner) { }
        protected TypoException(System.Runtime.Serialization.SerializationInfo info, System.Runtime.Serialization.StreamingContext context)
            : base(info, context) { }
    }
}

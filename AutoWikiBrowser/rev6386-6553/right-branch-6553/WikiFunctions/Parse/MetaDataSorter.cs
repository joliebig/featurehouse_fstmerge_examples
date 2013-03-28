using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using WikiFunctions.TalkPages;
namespace WikiFunctions.Parse
{
    public enum InterWikiOrderEnum
    {
        LocalLanguageAlpha,
        LocalLanguageFirstWord,
        Alphabetical,
        AlphabeticalEnFirst
    }
    public class MetaDataSorter
    {
        public List<string> PossibleInterwikis;
        public bool SortInterwikis
        { get; set; }
        public bool AddCatKey
        { get; set; }
        public MetaDataSorter()
        {
            SortInterwikis = true;
            if (!LoadInterWikiFromCache())
            {
                LoadInterWikiFromNetwork();
                SaveInterWikiToCache();
            }
            if (InterwikiLocalAlpha == null)
                throw new NullReferenceException("InterwikiLocalAlpha is null");
            InterWikiOrder = InterWikiOrderEnum.LocalLanguageAlpha;
        }
        private readonly Regex InterLangRegex = new Regex(@"<!--\s*(other languages?|language links?|inter ?(language|wiki)? ?links|inter ?wiki ?language ?links|inter ?wikis?|The below are interlanguage links\.?|interwiki links to this article in other languages, below)\s*-->", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private readonly Regex CatCommentRegex = new Regex("<!-- ?cat(egories)? ?-->", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private List<string> InterwikiLocalAlpha;
        private List<string> InterwikiLocalFirst;
        private List<string> InterwikiAlpha;
        private List<string> InterwikiAlphaEnFirst;
        private InterWikiComparer Comparer;
        private InterWikiOrderEnum Order = InterWikiOrderEnum.LocalLanguageAlpha;
        public InterWikiOrderEnum InterWikiOrder
        {
            set
            {
                Order = value;
                List<string> seq;
                switch (Order)
                {
                    case InterWikiOrderEnum.Alphabetical:
                        seq = InterwikiAlpha;
                        break;
                    case InterWikiOrderEnum.AlphabeticalEnFirst:
                        seq = InterwikiAlphaEnFirst;
                        break;
                    case InterWikiOrderEnum.LocalLanguageAlpha:
                        seq = InterwikiLocalAlpha;
                        break;
                    case InterWikiOrderEnum.LocalLanguageFirstWord:
                        seq = InterwikiLocalFirst;
                        break;
                    default:
                        throw new ArgumentOutOfRangeException("MetaDataSorter.InterWikiOrder",
                                                              (Exception)null);
                }
                PossibleInterwikis = SiteMatrix.GetProjectLanguages(Variables.Project);
                Comparer = new InterWikiComparer(new List<string>(seq), PossibleInterwikis);
            }
            get
            {
                return Order;
            }
        }
        private bool Loaded = true;
        private List<string> Load(string what)
        {
            var result = (List<string>)ObjectCache.Global.Get<List<string> >(Key(what));
            if (result == null)
            {
                Loaded = false;
                return new List<string>();
            }
            return result;
        }
        private void SaveInterWikiToCache()
        {
            ObjectCache.Global.Set(Key("InterwikiLocalAlpha"), InterwikiLocalAlpha);
            ObjectCache.Global.Set(Key("InterwikiLocalFirst"), InterwikiLocalFirst);
            ObjectCache.Global.Set(Key("InterwikiAlpha"), InterwikiAlpha);
            ObjectCache.Global.Set(Key("InterwikiAlphaEnFirst"), InterwikiAlphaEnFirst);
        }
        private static string Key(string what)
        {
            return "MetaDataSorter::" + what;
        }
        private bool LoadInterWikiFromCache()
        {
            InterwikiLocalAlpha = Load("InterwikiLocalAlpha");
            InterwikiLocalFirst = Load("InterwikiLocalFirst");
            InterwikiAlpha = Load("InterwikiAlpha");
            InterwikiAlphaEnFirst = Load("InterwikiAlphaEnFirst");
            return Loaded;
        }
        private void LoadInterWikiFromNetwork()
        {
            string text = !Globals.UnitTestMode
                ? Tools.GetHTML("http://en.wikipedia.org/w/index.php?title=Wikipedia:AutoWikiBrowser/IW&action=raw")
                : @"<!--InterwikiLocalAlphaBegins-->
ru, sq, en
<!--InterwikiLocalAlphaEnds-->
<!--InterwikiLocalFirstBegins-->
en, sq, ru
<!--InterwikiLocalFirstEnds-->";
            string interwikiLocalAlphaRaw =
                RemExtra(Tools.StringBetween(text, "<!--InterwikiLocalAlphaBegins-->", "<!--InterwikiLocalAlphaEnds-->"));
            string interwikiLocalFirstRaw =
                RemExtra(Tools.StringBetween(text, "<!--InterwikiLocalFirstBegins-->", "<!--InterwikiLocalFirstEnds-->"));
            InterwikiLocalAlpha = new List<string>();
            foreach (string s in interwikiLocalAlphaRaw.Split(new[] { "," }, StringSplitOptions.RemoveEmptyEntries)
                    )
            {
                InterwikiLocalAlpha.Add(s.Trim().ToLower());
            }
            InterwikiLocalFirst = new List<string>();
            foreach (string s in interwikiLocalFirstRaw.Split(new[] { "," }, StringSplitOptions.RemoveEmptyEntries)
                    )
            {
                InterwikiLocalFirst.Add(s.Trim().ToLower());
            }
            InterwikiAlpha = new List<string>(InterwikiLocalFirst);
            InterwikiAlpha.Sort(StringComparer.Create(new System.Globalization.CultureInfo("en-US", true), true));
            InterwikiAlphaEnFirst = new List<string>(InterwikiAlpha);
            InterwikiAlphaEnFirst.Remove("en");
            InterwikiAlphaEnFirst.Insert(0, "en");
        }
        private static string RemExtra(string input)
        {
            return input.Replace("\r\n", "").Replace(">", "").Replace("\n", "");
        }
        private static string Newline(string s)
        {
            return Newline(s, 1);
        }
        private static string Newline(string s, int n)
        {
            if (s.Length == 0)
                return s;
            for (int i = 0; i < n; i++)
                s = "\r\n" + s;
            return s;
        }
        internal string Sort(string articleText, string articleTitle)
        {
            return Sort(articleText, articleTitle, true);
        }
        internal string Sort(string articleText, string articleTitle, bool fixOptionalWhitespace)
        {
            if (Namespace.Determine(articleTitle) == 10)
                return articleText;
            string strSave = articleText;
            try
            {
                articleText = Regex.Replace(articleText, "<!-- ?\\[\\[en:.*?\\]\\] ?-->", "");
                string personData = Newline(RemovePersonData(ref articleText));
                string disambig = Newline(RemoveDisambig(ref articleText));
                string categories = Newline(RemoveCats(ref articleText, articleTitle));
                string interwikis = Newline(Interwikis(ref articleText));
                if(Variables.LangCode == "en")
                    articleText = MoveMaintenanceTags(articleText);
                articleText = MoveDablinks(articleText);
                if (Variables.LangCode == "en")
                {
                    articleText = MovePortalTemplates(articleText);
                    articleText = MoveTemplateToReferencesSection(articleText, WikiRegexes.Ibid);
                    articleText = MoveExternalLinks(articleText);
                    articleText = MoveSeeAlso(articleText);
                }
                string strStub = Newline(RemoveStubs(ref articleText), Variables.LangCode == "ru" ? 1 : 2);
                    articleText = Parsers.RemoveWhiteSpace(articleText, fixOptionalWhitespace) + "\r\n";
                articleText += disambig;
                switch (Variables.LangCode)
                {
                    case "de":
                    case "sl":
                        articleText += strStub + categories + personData;
                        if (Variables.LangCode == "de" && personData.Length > 0 && interwikis.Length > 0)
                            articleText += "\r\n";
                        break;
                    case "pl":
                    case "ru":
                    case "simple":
                        articleText += personData + strStub + categories;
                        break;
                    default:
                        articleText += personData + categories + strStub;
                        break;
                }
                return (articleText + interwikis).TrimEnd();
            }
            catch (Exception ex)
            {
                if (!ex.Message.Contains("DEFAULTSORT")) ErrorHandler.Handle(ex);
                return strSave;
            }
        }
        public string RemoveCats(ref string articleText, string articleTitle)
        {
            List<string> categoryList = new List<string>();
            string originalArticleText = articleText;
            Regex r = new Regex(@"<!-- [^<>]*?\[\[" + Variables.NamespacesCaseInsensitive[Namespace.Category]
                                + @".*?(\]\]|\|.*?\]\]).*?-->|\[\["
                                + Variables.NamespacesCaseInsensitive[Namespace.Category]
                                + @".*?(\]\]|\|.*?\]\])( {0,4}ââââ[0-9]{1,4}ââââ|\s*<!--.*?-->(?=\r\n\[\[))?", RegexOptions.Singleline);
            MatchCollection matches = r.Matches(articleText);
            foreach (Match m in matches)
            {
                if (!Regex.IsMatch(m.Value, "\\[\\[Category:(Pages|Categories|Articles) for deletion\\]\\]"))
                    categoryList.Add(m.Value);
            }
            articleText = Tools.RemoveMatches(articleText, matches);
            if(!CommentsNotChanged(originalArticleText, articleText))
            {
                articleText = originalArticleText;
                return "";
            }
            if (AddCatKey)
                categoryList = CatKeyer(categoryList, articleTitle);
            if (CatCommentRegex.IsMatch(articleText))
            {
                string catComment = CatCommentRegex.Match(articleText).Value;
                articleText = articleText.Replace(catComment, "");
                categoryList.Insert(0, catComment);
            }
            MatchCollection mc = WikiRegexes.Defaultsort.Matches(articleText);
            if (mc.Count > 1) throw new ArgumentException("Page contains multiple {{DEFAULTSORTS}} tags. Metadata sorting cancelled");
            string defaultSort = "";
            if (mc.Count > 0 && WikiRegexes.Defaultsort.Matches(WikiRegexes.Comments.Replace(articleText, "")).Count == mc.Count)
                defaultSort = mc[0].Value;
            if (!string.IsNullOrEmpty(defaultSort))
                articleText = articleText.Replace(defaultSort, "");
            if (!string.IsNullOrEmpty(defaultSort) && defaultSort.ToUpper().Contains("DEFAULTSORT"))
            {
                defaultSort = TalkPageHeaders.FormatDefaultSort(defaultSort);
            }
            if (!string.IsNullOrEmpty(defaultSort)) defaultSort += "\r\n";
            return defaultSort + ListToString(categoryList);
        }
        private bool CommentsNotChanged(string originalArticleText, string articleText)
        {
            if(WikiRegexes.Comments.Matches(originalArticleText).Count != WikiRegexes.Comments.Matches(articleText).Count)
                return true;
            string before = "", after = "";
            foreach(Match m in WikiRegexes.Comments.Matches(originalArticleText))
            {
                before += m.Value;
            }
            foreach(Match m in WikiRegexes.Comments.Matches(articleText))
            {
                after += m.Value;
            }
            return(before.Length.Equals(after.Length));
        }
        public static string RemovePersonData(ref string articleText)
        {
            string strPersonData = (Variables.LangCode == "de")
                ? Parsers.GetTemplate(articleText, "[Pp]ersonendaten")
                : Parsers.GetTemplate(articleText, "[Pp]ersondata");
            if (!string.IsNullOrEmpty(strPersonData))
                articleText = articleText.Replace(strPersonData, "");
            if (articleText.Contains(WikiRegexes.PersonDataCommentEN) && Variables.LangCode == "en")
            {
                articleText = articleText.Replace(WikiRegexes.PersonDataCommentEN, "");
                strPersonData = WikiRegexes.PersonDataCommentEN + strPersonData;
            }
            return strPersonData;
        }
        public static string RemoveStubs(ref string articleText)
        {
            if (Variables.LangCode == "ru") return "";
            List<string> stubList = new List<string>();
            MatchCollection matches = WikiRegexes.PossiblyCommentedStub.Matches(articleText);
            if (matches.Count == 0) return "";
            string x;
            StringBuilder sb = new StringBuilder(articleText);
            for (int i = matches.Count - 1; i >= 0; i--)
            {
                Match m = matches[i];
                x = m.Value;
                if (!Regex.IsMatch(x, Variables.SectStub) && !x.Contains("|"))
                {
                    stubList.Add(x);
                    sb.Remove(m.Index, x.Length);
                }
            }
            articleText = sb.ToString();
            stubList.Reverse();
            return (stubList.Count != 0) ? ListToString(stubList) : "";
        }
        public static string RemoveDisambig(ref string articleText)
        {
            if (Variables.LangCode != "en")
                return "";
            string strDisambig = "";
            if (WikiRegexes.Disambigs.IsMatch(WikiRegexes.Comments.Replace(articleText, "")))
            {
                strDisambig = WikiRegexes.Disambigs.Match(articleText).Value;
                articleText = articleText.Replace(strDisambig, "");
            }
            return strDisambig;
        }
        public static string MoveDablinks(string articleText)
        {
            string zerothSection = WikiRegexes.ZerothSection.Match(articleText).Value;
            if (Variables.LangCode != "en" || !WikiRegexes.Dablinks.IsMatch(WikiRegexes.Comments.Replace(zerothSection, "")))
                return articleText;
            string restOfArticle = articleText.Replace(zerothSection, "");
            string strDablinks = "";
            foreach (Match m in WikiRegexes.Dablinks.Matches(zerothSection))
            {
                strDablinks = strDablinks + m.Value + "\r\n";
                zerothSection = zerothSection.Replace(m.Value, "");
            }
            articleText = strDablinks + zerothSection + restOfArticle;
            return articleText.Replace(strDablinks + "\r\n", strDablinks);
        }
        public static string MoveMaintenanceTags(string articleText)
        {
            bool doMove = false;
            int lastIndex = -1;
            foreach(Match m in WikiRegexes.MaintenanceTemplates.Matches(articleText))
            {
                lastIndex = m.Index;
            }
            if (lastIndex < 0)
                return articleText;
            string articleTextToCheck = articleText.Substring(0, lastIndex);
            foreach(Match m in WikiRegexes.NestedTemplates.Matches(articleTextToCheck))
            {
                if (Tools.GetTemplateName(m.Value).ToLower().Contains("infobox"))
                {
                    doMove = true;
                    break;
                }
                articleTextToCheck = articleTextToCheck.Replace(m.Value, "");
            }
            if(articleTextToCheck.Trim().Length > 0)
                doMove = true;
            if(!doMove)
                return articleText;
            string strMaintTags = "";
            foreach (Match m in WikiRegexes.MaintenanceTemplates.Matches(articleText))
            {
                if(!m.Value.Contains("section"))
                {
                    strMaintTags = strMaintTags + m.Value + "\r\n";
                    articleText = articleText.Replace(m.Value, "");
                }
            }
            articleText = strMaintTags + articleText;
            return strMaintTags.Length > 0 ? articleText.Replace(strMaintTags + "\r\n", strMaintTags) : articleText;
        }
        private static readonly Regex SeeAlso = new Regex(@"(\s*(==+)\s*see\s+also\s*\2)", RegexOptions.IgnoreCase | RegexOptions.Singleline);
        private static readonly Regex SeeAlsoSection = new Regex(@"(^== *[Ss]ee also *==.*?)(?=^==[^=][^\r\n]*?[^=]==(\r\n?|\n)$)", RegexOptions.Multiline | RegexOptions.Singleline);
        private static readonly Regex SeeAlsoToEnd = new Regex(@"(\s*(==+)\s*see\s+also\s*\2 *).*", RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static string MovePortalTemplates(string articleText)
        {
            if (WikiRegexes.PortalTemplate.Matches(articleText).Count >= 1 && SeeAlso.Matches(articleText).Count == 1)
            {
                foreach (Match m in WikiRegexes.PortalTemplate.Matches(articleText))
                {
                    string portalTemplateFound = m.Value;
                    string seeAlsoSectionString = SeeAlsoSection.Match(articleText).Value;
                    if (seeAlsoSectionString.Length == 0)
                        seeAlsoSectionString = SeeAlsoToEnd.Match(articleText).Value;
                    if (!seeAlsoSectionString.Contains(portalTemplateFound.Trim()))
                    {
                        articleText = Regex.Replace(articleText, Regex.Escape(portalTemplateFound) + @"\s*\r\n", "");
                        articleText = SeeAlso.Replace(articleText, "$0" + "\r\n" + portalTemplateFound);
                    }
                }
            }
            return (articleText);
        }
        private static readonly Regex ReferencesSectionRegex = new Regex(@"^== *[Rr]eferences *==\s*", RegexOptions.Multiline);
        private static readonly Regex NotesSectionRegex = new Regex(@"^== *[Nn]otes *==\s*", RegexOptions.Multiline);
        private static readonly Regex FootnotesSectionRegex = new Regex(@"^== *[Ff]ootnotes *==\s*", RegexOptions.Multiline);
        public static string MoveTemplateToReferencesSection(string articleText, Regex TemplateRegex, bool onlyfromzerothsection)
        {
            if(TemplateRegex.Matches(articleText).Count != 1)
                return articleText;
            if(onlyfromzerothsection)
            {
                string zerothSection = WikiRegexes.ZerothSection.Match(articleText).Value;
                if (TemplateRegex.Matches(zerothSection).Count != 1)
                    return articleText;
            }
            int templatePosition = TemplateRegex.Match(articleText).Index;
            int referencesSectionPosition = ReferencesSectionRegex.Match(articleText).Index;
            if (referencesSectionPosition > 0 && templatePosition < referencesSectionPosition)
                return MoveTemplateToSection(articleText, TemplateRegex, 1);
            int notesSectionPosition = NotesSectionRegex.Match(articleText).Index;
            if (notesSectionPosition > 0 && templatePosition < notesSectionPosition)
                return MoveTemplateToSection(articleText, TemplateRegex, 2);
            int footnotesSectionPosition = FootnotesSectionRegex.Match(articleText).Index;
            if (footnotesSectionPosition > 0 && templatePosition < footnotesSectionPosition)
                return MoveTemplateToSection(articleText, TemplateRegex, 3);
            return articleText;
        }
        public static string MoveTemplateToReferencesSection(string articleText, Regex TemplateRegex)
        {
            return MoveTemplateToReferencesSection(articleText, TemplateRegex, false);
        }
        private static string MoveTemplateToSection(string articleText, Regex TemplateRegex, int section)
        {
            string extractedTemplate = TemplateRegex.Match(articleText).Value;
            articleText = articleText.Replace(extractedTemplate, "");
            switch (section)
            {
                case 1:
                    return ReferencesSectionRegex.Replace(articleText, "$0" + extractedTemplate + "\r\n");
                case 2:
                    return NotesSectionRegex.Replace(articleText, "$0" + extractedTemplate + "\r\n");
                case 3:
                    return FootnotesSectionRegex.Replace(articleText, "$0" + extractedTemplate + "\r\n");
                default:
                    return articleText;
            }
        }
        private static readonly Regex ExternalLinksSection = new Regex(@"(^== *[Ee]xternal +[Ll]inks? *==.*?)(?=^==+[^=][^\r\n]*?[^=]==+(\r\n?|\n)$)", RegexOptions.Multiline | RegexOptions.Singleline);
        private static readonly Regex ReferencesSection = new Regex(@"(^== *[Rr]eferences *==.*?)(?=^==[^=][^\r\n]*?[^=]==(\r\n?|\n)$)", RegexOptions.Multiline | RegexOptions.Singleline);
        private static readonly Regex ReferencesToEnd = new Regex(@"^== *[Rr]eferences *==\s*" + WikiRegexes.ReferencesTemplates + @"\s*(?={{DEFAULTSORT\:|\[\[Category\:)", RegexOptions.Multiline);
        public static string MoveExternalLinks(string articleText)
        {
            string articleTextAtStart = articleText;
            string externalLinks = ExternalLinksSection.Match(articleText).Groups[1].Value;
            string references = ReferencesSection.Match(articleText).Groups[1].Value;
            if (references.Length == 0)
                references = ReferencesToEnd.Match(articleText).Value;
            if (articleText.IndexOf(externalLinks) < articleText.IndexOf(references) && references.Length > 0 && externalLinks.Length > 0)
            {
                articleText = articleText.Replace(externalLinks, "");
                articleText = articleText.Replace(references, references + externalLinks);
            }
            if(!Parsers.HasRefAfterReflist(articleText))
                return articleText;
            else return articleTextAtStart;
        }
        public static string MoveSeeAlso(string articleText)
        {
            string references = ReferencesSection.Match(articleText).Groups[1].Value;
            string seealso = SeeAlsoSection.Match(articleText).Groups[1].Value;
            if (articleText.IndexOf(seealso) > articleText.IndexOf(references) && references.Length > 0 && seealso.Length > 0)
            {
                articleText = articleText.Replace(seealso, "");
                articleText = articleText.Replace(references, seealso + references);
            }
            return articleText;
        }
        private static List<string> RemoveLinkFGAs(ref string articleText)
        {
            List<string> linkFGAList = new List<string>();
            MatchCollection matches;
            switch(Variables.LangCode)
            {
                case "ar" :
                    matches =WikiRegexes.LinkFGAsArabic.Matches(articleText);
                    break;
               case "ca" :
                    matches =WikiRegexes.LinkFGAsCatalan.Matches(articleText);
                    break;
               case "es" :
                    matches =WikiRegexes.LinkFGAsSpanish.Matches(articleText);
                    break;
                case "fr" :
                    matches =WikiRegexes.LinkFGAsFrench.Matches(articleText);
                    break;
                case "it" :
                    matches =WikiRegexes.LinkFGAsItalian.Matches(articleText);
                    break;
                    default :
                        matches = WikiRegexes.LinkFGAs.Matches(articleText);
                    break;
            }
            if (matches.Count == 0)
                return linkFGAList;
            foreach (Match m in matches)
            {
                string FGAlink = m.Value;
                linkFGAList.Add(FGAlink);
                articleText = articleText.Replace(FGAlink, "");
            }
            linkFGAList.Reverse();
            return linkFGAList;
        }
        public string Interwikis(ref string articleText)
        {
            string interWikiComment = "";
            if (InterLangRegex.IsMatch(articleText))
            {
                interWikiComment = InterLangRegex.Match(articleText).Value;
                articleText = articleText.Replace(interWikiComment, "");
            }
            HideText hider = new HideText(false, true, false);
            articleText = hider.Hide(articleText);
            string interWikis = ListToString(RemoveLinkFGAs(ref articleText));
            if(interWikiComment.Length > 0)
                interWikis += interWikiComment + "\r\n";
            interWikis += ListToString(RemoveInterWikis(ref articleText));
            articleText = hider.AddBack(articleText);
            return interWikis;
        }
        private List<string> RemoveInterWikis(ref string articleText)
        {
            List<string> interWikiList = new List<string>();
            MatchCollection matches = WikiRegexes.PossibleInterwikis.Matches(articleText);
            if (matches.Count == 0) return interWikiList;
            List<Match> goodMatches = new List<Match>(matches.Count);
            foreach (Match m in matches)
            {
                string site = m.Groups[1].Value.Trim().ToLower();
                if (!PossibleInterwikis.Contains(site)) continue;
                goodMatches.Add(m);
                interWikiList.Add("[[" + site + ":" + m.Groups[2].Value.Trim() + "]]");
            }
            articleText = Tools.RemoveMatches(articleText, goodMatches);
            if (SortInterwikis)
                interWikiList.Sort(Comparer);
            return interWikiList;
        }
        public static string IWMatchEval(Match match)
        {
            string[] textArray = new[] { "[[", match.Groups["site"].ToString().ToLower(), ":", match.Groups["text"].ToString(), "]]" };
            return string.Concat(textArray);
        }
        private static string ListToString(ICollection<string> items)
        {
            if (items.Count == 0)
                return "";
            List<string> uniqueItems = new List<string>();
            foreach (string s in items)
            {
                if (!uniqueItems.Contains(s))
                    uniqueItems.Add(s);
            }
            StringBuilder list = new StringBuilder();
            foreach (string s in uniqueItems)
            {
                list.AppendLine(s);
            }
            return list.ToString();
        }
        private static List<string> CatKeyer(IEnumerable<string> list, string name)
        {
            name = Tools.MakeHumanCatKey(name);
            List<string> newCats = new List<string>();
            foreach (string s in list)
            {
                string z = s;
                if (!z.Contains("|"))
                    z = z.Replace("]]", "|" + name + "]]");
                newCats.Add(z);
            }
            return newCats;
        }
    }
}

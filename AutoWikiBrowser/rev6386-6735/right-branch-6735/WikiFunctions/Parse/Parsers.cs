using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using WikiFunctions.Lists.Providers;
namespace WikiFunctions.Parse
{
    public class Parsers
    {
        public Parsers()
        {
        }
        public Parsers(int stubWordCount, bool addHumanKey)
        {
            StubMaxWordCount = stubWordCount;
            Sorter.AddCatKey = addHumanKey;
        }
        static Parsers()
        {
            RegexUnicode.Add(new Regex("&(ndash|mdash|minus|times|lt|gt|nbsp|thinsp|shy|lrm|rlm|[Pp]rime|ensp|emsp|#x2011|#820[13]|#8239);", RegexOptions.Compiled), "&amp;$1;");
            RegexUnicode.Add(new Regex("&#(705|803|596|620|699|700|8652|9408|9848|12288|160|61|x27|39);", RegexOptions.Compiled), "&amp;#$1;");
            RegexUnicode.Add(new Regex("&#(x109[0-9A-Z]{2});", RegexOptions.Compiled), "&amp;#$1;");
            RegexUnicode.Add(new Regex("&#((?:277|119|84|x1D|x100)[A-Z0-9a-z]{2,3});", RegexOptions.Compiled), "&amp;#$1;");
            RegexUnicode.Add(new Regex("&#(x12[A-Za-z0-9]{3});", RegexOptions.Compiled), "&amp;#$1;");
            RegexUnicode.Add(new Regex("&#(0?13|126|x5[BD]|x7[bcd]|0?9[13]|0?12[345]|0?0?3[92]);", RegexOptions.Compiled | RegexOptions.IgnoreCase), "&amp;#$1;");
            RegexTagger.Add(new Regex(@"\{\{\s*(?:template:)?\s*(?:wikify(?:-date)?|wfy|wiki)(\s*\|\s*section)?\s*\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Wikify$1|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(Clean( ?up)?|CU|Tidy)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Cleanup|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(Orphan)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Orphan|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(Uncategori[sz]ed|Uncat|Classify|Category needed|Catneeded|categori[zs]e|nocats?)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Uncategorized|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(Unreferenced(sect)?|add references|cite[ -]sources?|cleanup-sources?|needs? references|no sources|no references?|not referenced|references|unref|unsourced)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Unreferenced|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(Trivia2?|Too ?much ?trivia|Trivia section|Cleanup-trivia)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Trivia|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(deadend|DEP)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Deadend|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(copyedit|g(rammar )?check|copy-edit|cleanup-copyedit|cleanup-english)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{copyedit|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(sources|refimprove|not verified)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Refimprove|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(Uncategori[zs]edstub|uncatstub)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Uncategorizedstub|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(Expand)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Expand|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(?:\s*[Tt]emplate:)?(\s*(?:[Cc]n|[Ff]act|[Pp]roveit|[Cc]iteneeded|[Uu]ncited|[Cc]itation needed)\s*(?:\|[^{}]+(?<!\|\s*date\s*=[^{}]+))?)\}\}", RegexOptions.Compiled), "{{$1|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(COI|Conflict of interest|Selfpromotion)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{COI|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?(Intro( |-)?missing|Nointro(duction)?|Lead missing|No ?lead|Missingintro|Opening|No-intro|Leadsection|No lead section)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Intro missing|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexTagger.Add(new Regex(@"\{\{(template:)?([Pp]rimary ?[Ss]ources?|[Rr]eliable ?sources)\}\}", RegexOptions.Compiled), "{{Primary sources|" + WikiRegexes.DateYearMonthParameter + @"}}");
            RegexConversion.Add(new Regex(@"\{\{(?:Template:)?(Dab|Disamb|Disambiguation)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Disambig}}");
            RegexConversion.Add(new Regex(@"\{\{(?:Template:)?(Bio-dab|Hndisambig)", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{Hndis");
            RegexConversion.Add(new Regex(@"\{\{(?:Template:)?(Prettytable|Prettytable100)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled), "{{subst:Prettytable}}");
            RegexConversion.Add(new Regex(@"\{\{(?:[Tt]emplate:)?((?:BASE)?PAGENAMEE?\}\}|[Ll]ived\||[Bb]io-cats\|)", RegexOptions.Compiled), "{{subst:$1");
            RegexConversion.Add(new Regex(@"({{\s*(?:[Aa]rticle|[Mm]ultiple) ?issues\s*(?:\|[^{}]*|\|)\s*[Dd]o-attempt\s*=\s*)[^{}\|]+\|\s*att\s*=\s*([^{}\|]+)(?=\||}})", RegexOptions.Compiled), "$1$2");
            RegexConversion.Add(new Regex(@"({{\s*(?:[Aa]rticle|[Mm]ultiple) ?issues\s*(?:\|[^{}]*|\|)\s*[Cc]opyedit\s*)for\s*=\s*[^{}\|]+\|\s*date(\s*=[^{}\|]+)(?=\||}})", RegexOptions.Compiled), "$1$2");
            for (int a = 0; a < 5; a++)
            {
                RegexConversion.Add(new Regex(@"({{\s*(?:[Aa]rticle|[Mm]ultiple) ?issues\s*(?:\|[^{}]*|\|)\s*)(?![Ee]xpert)" + WikiRegexes.ArticleIssuesTemplatesString + @"\s*(\||}})", RegexOptions.Compiled), "$1$2={{subst:CURRENTMONTHNAME}} {{subst:CURRENTYEAR}}$3");
                RegexConversion.Add(new Regex(@"(?<={{\s*(?:[Aa]rticle|[Mm]ultiple) ?issues\s*(?:\|[^{}]*?)?(?:{{subst:CURRENTMONTHNAME}} {{subst:CURRENTYEAR}}[^{}]*?){0,4}\|[^{}\|]{3,}?)\b(?i)date(?<!.*out of.*)", RegexOptions.Compiled), "");
            }
            RegexConversion.Add(new Regex(@"\{\{(?:[Aa]rticle|[Mm]ultiple) ?issues\s*\|\s*([^\|{}=]{3,}?)\s*(=\s*\w{3,10}\s+20\d\d)\s*\}\}", RegexOptions.Compiled), "{{$1|date$2}}");
            RegexConversion.Add(new Regex(@"\{\{(?:[Aa]rticle|[Mm]ultiple) ?issues(?:\s*\|\s*(?:section|article)\s*=\s*[Yy])?\s*\}\}", RegexOptions.Compiled), "");
            RegexConversion.Add(new Regex(@"\{\{[Cc]ommons\|\s*[Cc]ategory:\s*([^{}]+?)\s*\}\}", RegexOptions.Compiled), @"{{Commons category|$1}}");
            RegexConversion.Add(new Regex(@"(?<={{[Cc]ommons cat(?:egory)?\|\s*)([^{}\|]+?)\s*\|\s*\1\s*}}", RegexOptions.Compiled), @"$1}}");
            RegexConversion.Add(new Regex(@"(?!{{[Cc]ite wikisource)(\{\{\s*(?:[Cc]it[ae]|[Aa]rticle ?issues)[^{}]*)\|\s*(\}\}|\|)", RegexOptions.Compiled), "$1$2");
            RegexConversion.Add(new Regex(@"({{\s*(?:[Aa]rticle|[Mm]ultiple) ?issues[^{}]*\|\s*)(\w+)\s*=\s*([^\|}{]+?)\s*\|((?:[^{}]*?\|)?\s*)\2(\s*=\s*)\3(\s*(\||\}\}))", RegexOptions.Compiled), "$1$4$2$5$3$6");
            RegexConversion.Add(new Regex(@"(\{\{\s*(?:[Aa]rticle|[Mm]ultiple) ?issues[^{}]*\|\s*)(\w+)(\s*=\s*[^\|}{]+(?:\|[^{}]+?)?)\|\s*\2\s*=\s*(\||\}\})", RegexOptions.Compiled), "$1$2$3$4");
            RegexConversion.Add(new Regex(@"(\{\{\s*(?:[Aa]rticle|[Mm]ultiple) ?issues[^{}]*\|\s*)(\w+)\s*=\s*\|\s*((?:[^{}]+?\|)?\s*\2\s*=\s*[^\|}{\s])", RegexOptions.Compiled), "$1$3");
            RegexConversion.Add(new Regex(@"(?<={{\s*)(?:[Aa]rticle) ?(?=issues.*}})", RegexOptions.Compiled), "multiple ");
            RegexConversion.Add(new Regex(@"{{\s*(?:[Cc]n|[Ff]act|[Pp]roveit|[Cc]iteneeded|[Uu]ncited)(?=\s*[\|}])", RegexOptions.Compiled), @"{{Citation needed");
            RegexConversion.Add(new Regex(@"({{\s*[Cc]itation needed\s*\|)\s*(?:[Dd]ate:)?([A-Z][a-z]+ 20\d\d)\s*\|\s*(date\s*=\s*\2\s*}})", RegexOptions.Compiled | RegexOptions.IgnoreCase), @"$1$3");
            SmallTagRegexes.Add(WikiRegexes.SupSub);
            SmallTagRegexes.Add(WikiRegexes.Images);
            SmallTagRegexes.Add(WikiRegexes.Refs);
            SmallTagRegexes.Add(WikiRegexes.Small);
        }
        private static readonly Dictionary<Regex, string> RegexUnicode = new Dictionary<Regex, string>();
        private static readonly Dictionary<Regex, string> RegexConversion = new Dictionary<Regex, string>();
        private static readonly Dictionary<Regex, string> RegexTagger = new Dictionary<Regex, string>();
        private readonly HideText Hider = new HideText();
        private readonly HideText HiderHideExtLinksImages = new HideText(true, true, true);
        public static int StubMaxWordCount = 500;
        public bool SortInterwikis
        {
            get { return Sorter.SortInterwikis; }
            set { Sorter.SortInterwikis = value; }
        }
        public InterWikiOrderEnum InterWikiOrder
        {
            set { Sorter.InterWikiOrder = value; }
            get { return Sorter.InterWikiOrder; }
        }
        private MetaDataSorter metaDataSorter;
        public MetaDataSorter Sorter
        {
            get { return metaDataSorter ?? (metaDataSorter = new MetaDataSorter()); }
        }
        public string HideText(string articleText)
        {
            return Hider.Hide(articleText);
        }
        public string AddBackText(string articleText)
        {
            return Hider.AddBack(articleText);
        }
        public string HideTextImages(string articleText)
        {
            return HiderHideExtLinksImages.Hide(articleText);
        }
        public string AddBackTextImages(string articleText)
        {
            return HiderHideExtLinksImages.AddBack(articleText);
        }
        public string HideMoreText(string articleText, bool hideOnlyTargetOfWikilink)
        {
            return HiderHideExtLinksImages.HideMore(articleText, hideOnlyTargetOfWikilink);
        }
        public string HideMoreText(string articleText)
        {
            return HiderHideExtLinksImages.HideMore(articleText);
        }
        public string AddBackMoreText(string articleText)
        {
            return HiderHideExtLinksImages.AddBackMore(articleText);
        }
        public string SortMetaData(string articleText, string articleTitle)
        {
            return SortMetaData(articleText, articleTitle, true);
        }
        public string SortMetaData(string articleText, string articleTitle, bool fixOptionalWhitespace)
        {
            if (Namespace.IsMainSpace(articleTitle) && NoIncludeIncludeOnlyProgrammingElement(articleText))
                return articleText;
            return (Variables.Project <= ProjectEnum.species) ? Sorter.Sort(articleText, articleTitle, fixOptionalWhitespace) : articleText;
        }
        private static readonly Regex ApostropheInDecades = new Regex(@"(?<=(?:the |later? |early |mid-|[12]\d\d0'?s and )(?:\[?\[?[12]\d\d0\]?\]?))['â]s(?=\]\])?", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadings0 = new Regex("(== ?)(see also:?|related topics:?|related articles:?|internal links:?|also see:?)( ?==)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadings1 = new Regex("(== ?)(external link[s]?|external site[s]?|outside link[s]?|web ?link[s]?|exterior link[s]?):?( ?==)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadings3 = new Regex("(== ?)(referen[sc]e:?)(s? ?==)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadings4 = new Regex("(== ?)(source:?)(s? ?==)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadings5 = new Regex("(== ?)(further readings?:?)( ?==)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadings6 = new Regex("(== ?)(Early|Personal|Adult|Later) Life( ?==)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadings7 = new Regex("(== ?)(Current|Past|Prior) Members( ?==)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadings8 = new Regex(@"^(=+ ?)'''(.*?)'''( ?=+)\s*?(\r)?$", RegexOptions.Multiline | RegexOptions.Compiled);
        private static readonly Regex RegexHeadings9 = new Regex("(== ?)track listing( ?==)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadings10 = new Regex("(== ?)Life and Career( ?==)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadingsCareer = new Regex("(== ?)([a-zA-Z]+) Career( ?==)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexRemoveLinksInHeadings = new Regex(@"^ *((={1,4})[^\[\]\{\}\|=\r\n]*)\[\[(?:[^\[\]\{\}\|=\r\n]+\|)?([^\[\]\{\}\|\r\n]+)\]\]([^\{\}=\r\n]*\2)", RegexOptions.Compiled | RegexOptions.Multiline);
        private static readonly Regex RegexBadHeader = new Regex("^(={1,4} ?(about|description|overview|definition|profile|(?:general )?information|background|intro(?:duction)?|summary|bio(?:graphy)?) ?={1,4})", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RegexHeadingWhitespaceBefore = new Regex(@"^ *(==+)(\s*.+?\s*)\1 +(\r|\n)", RegexOptions.IgnoreCase | RegexOptions.Multiline | RegexOptions.Compiled);
        private static readonly Regex RegexHeadingWhitespaceAfter = new Regex(@"^ +(==+)(\s*.+?\s*)\1 *(\r|\n)", RegexOptions.IgnoreCase | RegexOptions.Multiline | RegexOptions.Compiled);
        private static readonly Regex RegexHeadingUpOneLevel = new Regex(@"^=(==+[^=].*?[^=]==+)=(\r\n?|\n)$", RegexOptions.Multiline | RegexOptions.Compiled);
        private static readonly Regex ReferencesExternalLinksSeeAlso = new Regex(@"== *([Rr]eferences|[Ee]xternal +[Ll]inks?|[Ss]ee +[Aa]lso) *==\s", RegexOptions.Compiled);
        private static readonly Regex RegexHeadingColonAtEnd = new Regex(@"^(=+)(.+?)\:(\s*\1(?:\r\n?|\n))$", RegexOptions.Multiline | RegexOptions.Compiled);
        private static readonly Regex RegexHeadingWithBold = new Regex(@"(?<====+.*?)'''(.*?)'''(?=.*?===+)", RegexOptions.Compiled);
        public static string FixHeadings(string articleText, string articleTitle, out bool noChange)
        {
            string newText = FixHeadings(articleText, articleTitle);
            noChange = (newText == articleText);
            return newText.Trim();
        }
        private const int MinCleanupTagsToCombine = 3;
        public string ArticleIssues(string articleText, string articleTitle)
        {
            if (Variables.LangCode != "en")
                return articleText;
            if (WikiRegexes.ArticleIssues.IsMatch(articleText))
            {
                string aiat = WikiRegexes.ArticleIssues.Match(articleText).Value;
                if (Tools.GetTemplateParameterValue(aiat, "unreferenced").Length > 0 && articleText.Contains(@"[[Category:Living people"))
                    articleText = articleText.Replace(aiat, Tools.RenameTemplateParameter(aiat, "unreferenced", "BLPunreferenced"));
                else if (Tools.GetTemplateParameterValue(aiat, "unref").Length > 0 && articleText.Contains(@"[[Category:Living people"))
                    articleText = articleText.Replace(aiat, Tools.RenameTemplateParameter(aiat, "unref", "BLPunreferenced"));
                articleText = MetaDataSorter.MoveMaintenanceTags(articleText);
            }
            foreach (Match m in WikiRegexes.ArticleIssuesInTitleCase.Matches(articleText))
            {
                string firstPart = m.Groups[1].Value;
                string parameterFirstChar = m.Groups[2].Value.ToLower();
                string lastPart = m.Groups[3].Value;
                articleText = articleText.Replace(m.Value, firstPart + parameterFirstChar + lastPart);
            }
            if (!WikiRegexes.ArticleIssuesRegexExpert.IsMatch(articleText))
                articleText = WikiRegexes.ArticleIssuesRegexWithDate.Replace(articleText, "$1$2");
            string newTags = "";
            string zerothSection = WikiRegexes.ZerothSection.Match(articleText).Value;
            string restOfArticle = articleText.Remove(0, zerothSection.Length);
            int tagsToAdd = WikiRegexes.ArticleIssuesTemplates.Matches(zerothSection).Count;
            if (!WikiRegexes.ArticleIssues.IsMatch(zerothSection) && WikiRegexes.ArticleIssuesTemplates.Matches(zerothSection).Count < MinCleanupTagsToCombine)
                return (articleText);
            if ((WikiRegexes.ArticleIssuesTemplateNameRegex.Matches(WikiRegexes.ArticleIssues.Match(zerothSection).Value).Count + tagsToAdd) < MinCleanupTagsToCombine || tagsToAdd == 0)
                return (articleText);
            foreach (Match m in WikiRegexes.ArticleIssuesTemplates.Matches(zerothSection))
            {
                string singleTag = m.Groups[1].Value;
                string tagValue = m.Groups[2].Value;
                if (!WikiRegexes.CoiOrPovBlp.IsMatch(singleTag))
                    singleTag = singleTag.ToLower();
                if (singleTag == "expert" && tagValue.Trim().Length == 0)
                    continue;
                if ((tagValue.Contains("=") && Regex.IsMatch(tagValue, @"(?i)date")) || tagValue.Length == 0)
                    newTags += @"|" + singleTag + @" " + tagValue;
                else
                    continue;
                newTags = newTags.Trim();
                zerothSection = zerothSection.Replace(m.Value, "");
            }
            string ai = WikiRegexes.ArticleIssues.Match(zerothSection).Value;
            if (ai.Length > 0)
                zerothSection = zerothSection.Replace(ai, ai.Substring(0, ai.Length - 2) + newTags + @"}}");
            else
                zerothSection = @"{{Article issues" + newTags + "}}\r\n" + zerothSection;
            return (zerothSection + restOfArticle);
        }
        private static readonly Regex PortalBox = Tools.NestedTemplateRegex(new[] { "portal box", "portalbox" });
        public static string MergePortals(string articleText)
        {
            if (!Variables.LangCode.Equals("en"))
                return articleText;
            int firstPortal = WikiRegexes.PortalTemplate.Match(articleText).Index;
            string originalArticleText = articleText;
            List<string> Portals = new List<string>();
            foreach (Match m in WikiRegexes.PortalTemplate.Matches(articleText))
            {
                string thePortalCall = m.Value, thePortalName = Tools.GetTemplateArgument(m.Value, 1);
                if (!Portals.Contains(thePortalName) && Tools.GetTemplateArgumentCount(thePortalCall) == 1)
                {
                    Portals.Add(thePortalName);
                    articleText = Regex.Replace(articleText, Regex.Escape(thePortalCall) + @"\s*(?:\r\n)?", "");
                }
            }
            if (Portals.Count == 0)
                return articleText;
            string PortalsToAdd = "";
            foreach (string portal in Portals)
                PortalsToAdd += ("|" + portal.Trim());
            Match pb = PortalBox.Match(articleText);
            if (pb.Success)
                return articleText.Replace(pb.Value, pb.Value.Substring(0, pb.Length - 2) + PortalsToAdd + @"}}");
            if (Portals.Count < 2)
                return originalArticleText;
            if(WikiRegexes.SeeAlso.IsMatch(articleText))
                return WikiRegexes.SeeAlso.Replace(articleText, "$0" + Tools.Newline(@"{{Portal box" + PortalsToAdd + @"}}"));
            if(Summary.ModifiedSection(originalArticleText, articleText).Length > 0)
                return articleText.Insert(firstPortal, @"{{Portal box" + PortalsToAdd + @"}}" + "\r\n");
            return originalArticleText;
        }
        public static string Dablinks(string articleText)
        {
            if (Variables.LangCode != "en")
                return articleText;
            string oldArticleText = "";
            string zerothSection = WikiRegexes.ZerothSection.Match(articleText).Value;
            string restOfArticle = articleText.Remove(0, zerothSection.Length);
            articleText = zerothSection;
            articleText = Tools.RenameTemplate(articleText, "otheruses4", "about");
            foreach (Match m in Tools.NestedTemplateRegex("about").Matches(articleText))
            {
                if (m.Groups[3].Value.TrimStart("| ".ToCharArray()).ToLower().StartsWith("about"))
                    articleText = articleText.Replace(m.Value, m.Groups[1].Value + m.Groups[2].Value + Regex.Replace(m.Groups[3].Value, @"^\|\s*[Aa]bout\s*", "|"));
            }
            oldArticleText = "";
            while (oldArticleText != articleText)
            {
                oldArticleText = articleText;
                bool doneAboutMerge = false;
                foreach (Match m in Tools.NestedTemplateRegex("about").Matches(articleText))
                {
                    string firstarg = Tools.GetTemplateArgument(m.Value, 1);
                    foreach (Match m2 in Tools.NestedTemplateRegex("about").Matches(articleText))
                    {
                        if (m2.Value == m.Value)
                            continue;
                        if (Tools.GetTemplateArgument(m2.Value, 1).Equals(firstarg))
                        {
                            if (Tools.GetTemplateArgument(m.Value, 2).Length > 0 && Tools.GetTemplateArgument(m2.Value, 2).Length > 0)
                            {
                                articleText = articleText.Replace(m.Value, m.Value.TrimEnd('}') + @"|" + Tools.GetTemplateArgument(m2.Value, 2) + @"|" + Tools.GetTemplateArgument(m2.Value, 3) + @"}}");
                                doneAboutMerge = true;
                            }
                            if (Tools.GetTemplateArgument(m.Value, 2).Length == 0 && Tools.GetTemplateArgument(m2.Value, 2).Length == 0)
                            {
                                articleText = articleText.Replace(m.Value, m.Value.TrimEnd('}') + @"|and|" + Tools.GetTemplateArgument(m2.Value, 3) + @"}}");
                                doneAboutMerge = true;
                            }
                        }
                        else if (Tools.GetTemplateArgument(m2.Value, 1).Length == 0)
                        {
                            if (Tools.GetTemplateArgument(m.Value, 2).Length > 0 && Tools.GetTemplateArgument(m2.Value, 2).Length > 0)
                            {
                                articleText = articleText.Replace(m.Value, m.Value.TrimEnd('}') + @"|" + Tools.GetTemplateArgument(m2.Value, 2) + @"|" + Tools.GetTemplateArgument(m2.Value, 3) + @"}}");
                                doneAboutMerge = true;
                            }
                        }
                        if (doneAboutMerge)
                        {
                            articleText = articleText.Replace(m2.Value, "");
                            break;
                        }
                    }
                    if (doneAboutMerge)
                        break;
                }
            }
            if (Tools.NestedTemplateRegex("for").Matches(articleText).Count > 1 && Tools.NestedTemplateRegex("about").Matches(articleText).Count == 0)
            {
                foreach (Match m in Tools.NestedTemplateRegex("for").Matches(articleText))
                {
                    if (Tools.GetTemplateArgument(m.Value, 3).Length == 0)
                    {
                        articleText = articleText.Replace(m.Value, Tools.RenameTemplate(m.Value, "about|"));
                        break;
                    }
                }
            }
            if (Tools.NestedTemplateRegex("about").Matches(articleText).Count == 1 &&
               Tools.GetTemplateArgument(Tools.NestedTemplateRegex("about").Match(articleText).Value, 2).Length > 0)
            {
                foreach (Match m in Tools.NestedTemplateRegex("for").Matches(articleText))
                {
                    string About = Tools.NestedTemplateRegex("about").Match(articleText).Value;
                    string extra = "";
                    if (Tools.GetTemplateArgument(Tools.NestedTemplateRegex("about").Match(articleText).Value, 3).Length == 0
                       && Tools.GetTemplateArgument(Tools.NestedTemplateRegex("about").Match(articleText).Value, 4).Length == 0)
                        extra = @"|";
                    if (Tools.GetTemplateArgument(m.Value, 3).Length == 0)
                        articleText = articleText.Replace(About, About.TrimEnd('}') + extra + m.Groups[3].Value);
                    else
                        articleText = articleText.Replace(About, About.TrimEnd('}') + extra + m.Groups[3].Value.Insert(m.Groups[3].Value.LastIndexOf('|') + 1, "and|"));
                    articleText = articleText.Replace(m.Value, "");
                }
            }
            foreach (Match m in Tools.NestedTemplateRegex("about").Matches(articleText))
            {
                string aboutcall = m.Value;
                for (int a = 1; a <= Tools.GetTemplateArgumentCount(m.Value); a++)
                {
                    string arg = Tools.GetTemplateArgument(aboutcall, a);
                    if (arg.Length > 0 && Namespace.Determine(arg) != Namespace.Mainspace)
                        aboutcall = aboutcall.Replace(arg, @":" + arg);
                }
                if (!m.Value.Equals(aboutcall))
                    articleText = articleText.Replace(m.Value, aboutcall);
            }
            oldArticleText = "";
            while (oldArticleText != articleText)
            {
                oldArticleText = articleText;
                bool doneDistinguishMerge = false;
                foreach (Match m in Tools.NestedTemplateRegex("distinguish").Matches(articleText))
                {
                    foreach (Match m2 in Tools.NestedTemplateRegex("distinguish").Matches(articleText))
                    {
                        if (m2.Value.Equals(m.Value))
                            continue;
                        articleText = articleText.Replace(m.Value, m.Value.TrimEnd('}') + m2.Groups[3].Value);
                        doneDistinguishMerge = true;
                        articleText = articleText.Replace(m2.Value, "");
                        break;
                    }
                    if (doneDistinguishMerge)
                        break;
                }
            }
            return (articleText + restOfArticle);
        }
        public static string FixHeadings(string articleText, string articleTitle)
        {
            articleText = Regex.Replace(articleText, "^={1,4} ?" + Regex.Escape(articleTitle) + " ?={1,4}", "", RegexOptions.IgnoreCase);
            articleText = RegexBadHeader.Replace(articleText, "");
            if (RegexRemoveLinksInHeadings.Matches(articleText).Count < 6
                && !(Regex.IsMatch(articleTitle, WikiRegexes.Months) || articleTitle.StartsWith(@"List of") || WikiRegexes.GregorianYear.IsMatch(articleTitle)))
            {
                while (RegexRemoveLinksInHeadings.IsMatch(articleText))
                {
                    articleText = RegexRemoveLinksInHeadings.Replace(articleText, "$1$3$4");
                }
            }
            if (!Regex.IsMatch(articleText, "= ?See also ?="))
                articleText = RegexHeadings0.Replace(articleText, "$1See also$3");
            articleText = RegexHeadings1.Replace(articleText, "$1External links$3");
            Match refsHeader = RegexHeadings3.Match(articleText);
            string refsheader1 = refsHeader.Groups[1].Value;
            string refsheader2 = refsHeader.Groups[2].Value;
            string refsheader3 = refsHeader.Groups[3].Value;
            if (refsheader2.Length > 0)
                articleText = articleText.Replace(refsheader1 + refsheader2 + refsheader3,
                                                  refsheader1 + "Reference" + refsheader3.ToLower());
            Match sourcesHeader = RegexHeadings4.Match(articleText);
            string sourcesheader1 = sourcesHeader.Groups[1].Value;
            string sourcesheader2 = sourcesHeader.Groups[2].Value;
            string sourcesheader3 = sourcesHeader.Groups[3].Value;
            if (sourcesheader2.Length > 0)
                articleText = articleText.Replace(sourcesheader1 + sourcesheader2 + sourcesheader3,
                                                  sourcesheader1 + "Source" + sourcesheader3.ToLower());
            articleText = RegexHeadings5.Replace(articleText, "$1Further reading$3");
            articleText = RegexHeadings6.Replace(articleText, "$1$2 life$3");
            articleText = RegexHeadings7.Replace(articleText, "$1$2 members$3");
            articleText = RegexHeadings8.Replace(articleText, "$1$2$3$4");
            articleText = RegexHeadings9.Replace(articleText, "$1Track listing$2");
            articleText = RegexHeadings10.Replace(articleText, "$1Life and career$2");
            articleText = RegexHeadingsCareer.Replace(articleText, "$1$2 career$3");
            articleText = RegexHeadingWhitespaceBefore.Replace(articleText, "$1$2$1$3");
            articleText = RegexHeadingWhitespaceAfter.Replace(articleText, "$1$2$1$3");
            articleText = RegexHeadingColonAtEnd.Replace(articleText, "$1$2$3");
            string articleTextLocal = articleText;
            articleTextLocal = ReferencesExternalLinksSeeAlso.Replace(articleTextLocal, "");
            string originalarticleText = "";
            while (!originalarticleText.Equals(articleText))
            {
                originalarticleText = articleText;
                if (!WikiRegexes.HeadingLevelTwo.IsMatch(articleTextLocal) && Namespace.IsMainSpace(articleTitle))
                {
                    int upone = 0;
                    foreach (Match m in RegexHeadingUpOneLevel.Matches(articleText))
                    {
                        if (m.Index > upone)
                            upone = m.Index;
                    }
                    if (!ReferencesExternalLinksSeeAlso.IsMatch(articleText) || (upone < ReferencesExternalLinksSeeAlso.Match(articleText).Index))
                        articleText = RegexHeadingUpOneLevel.Replace(articleText, "$1$2");
                }
                articleTextLocal = ReferencesExternalLinksSeeAlso.Replace(articleText, "");
            }
            articleText = RegexHeadingWithBold.Replace(articleText, "$1");
            return articleText;
        }
        private static readonly Regex CommaDates = new Regex(WikiRegexes.Months + @" ?, *([1-3]?\d) ?, ?((?:200|19\d)\d)\b");
        private static readonly Regex NoCommaAmericanDates = new Regex(@"\b(" + WikiRegexes.MonthsNoGroup + @" *[1-3]?\d(?:â[1-3]?\d)?)\b *,?([12]\d{3})\b");
        private static readonly Regex NoSpaceAmericanDates = new Regex(@"\b" + WikiRegexes.Months + @"([1-3]?\d(?:â[1-3]?\d)?), *([12]\d{3})\b");
        private static readonly Regex IncorrectCommaInternationalDates = new Regex(@"\b((?:[1-3]?\d) +" + WikiRegexes.MonthsNoGroup + @") *, *(1\d{3}|20\d{2})\b", RegexOptions.Compiled);
        private static readonly Regex SameMonthInternationalDateRange = new Regex(@"\b([1-3]?\d) *- *([1-3]?\d +" + WikiRegexes.MonthsNoGroup + @")\b", RegexOptions.Compiled);
        private static readonly Regex SameMonthAmericanDateRange = new Regex(@"(" + WikiRegexes.MonthsNoGroup + @" *)([1-3]?\d) *- *([1-3]?\d)\b", RegexOptions.Compiled);
        private static readonly Regex LongFormatInternationalDateRange = new Regex(@"\b([1-3]?\d) +" + WikiRegexes.Months + @" *(?:-|â|&nbsp;) *([1-3]?\d) +\2,? *([12]\d{3})\b", RegexOptions.Compiled);
        private static readonly Regex LongFormatAmericanDateRange = new Regex(WikiRegexes.Months + @" +([1-3]?\d) +" + @" *(?:-|â|&nbsp;) *\1 +([1-3]?\d) *,? *([12]\d{3})\b", RegexOptions.Compiled);
        private static readonly Regex FullYearRange = new Regex(@"(?:[\(,=;\|]|\b(?:from|between|and|reigned|f?or)) *([12]\d{3}) *- *([12]\d{3}) *(?=\)|[,;\|]|and\b|\s*$)", RegexOptions.Compiled | RegexOptions.Multiline);
        private static readonly Regex SpacedFullYearRange = new Regex(@"([12]\d{3})(?: +â *| *â +)([12]\d{3})", RegexOptions.Compiled);
        private static readonly Regex YearRangeShortenedCentury = new Regex(@"(?:[\(,=;]|\b(?:from|between|and|reigned)) *([12]\d{3}) *- *(\d{2}) *(?=\)|[,;]|and\b|\s*$)", RegexOptions.Compiled | RegexOptions.Multiline);
        private static readonly Regex YearRangeToPresent = new Regex(@"([12]\d{3}) *- *([Pp]resent)", RegexOptions.Compiled);
        public string FixDates(string articleText)
        {
            if (Variables.LangCode != "en")
                return articleText;
            articleText = HideTextImages(articleText);
            articleText = CommaDates.Replace(articleText, @"$1 $2, $3");
            articleText = IncorrectCommaInternationalDates.Replace(articleText, @"$1 $2");
            articleText = SameMonthInternationalDateRange.Replace(articleText, @"$1â$2");
            foreach (Match m in SameMonthAmericanDateRange.Matches(articleText))
            {
                int day1 = Convert.ToInt32(m.Groups[2].Value);
                int day2 = Convert.ToInt32(m.Groups[3].Value);
                if (day2 > day1)
                    articleText = articleText.Replace(m.Value, Regex.Replace(m.Value, @" *- *", @"â"));
            }
            articleText = LongFormatInternationalDateRange.Replace(articleText, @"$1â$3 $2 $4");
            articleText = LongFormatAmericanDateRange.Replace(articleText, @"$1 $2â$3, $4");
            articleText = NoCommaAmericanDates.Replace(articleText, @"$1, $2");
            articleText = NoSpaceAmericanDates.Replace(articleText, @"$1 $2, $3");
            articleText = SpacedFullYearRange.Replace(articleText, @"$1â$2");
            articleText = AddBackTextImages(articleText);
            articleText = HideMoreText(articleText);
            articleText = YearRangeToPresent.Replace(articleText, @"$1â$2");
            foreach (Match m in FullYearRange.Matches(articleText))
            {
                int year1 = Convert.ToInt32(m.Groups[1].Value);
                int year2 = Convert.ToInt32(m.Groups[2].Value);
                if (year2 > year1 && year2 - year1 <= 300)
                    articleText = articleText.Replace(m.Value, Regex.Replace(m.Value, @" *- *", @"â"));
            }
            foreach (Match m in YearRangeShortenedCentury.Matches(articleText))
            {
                int year1 = Convert.ToInt32(m.Groups[1].Value);
                int year2 = Convert.ToInt32(m.Groups[1].Value.Substring(0, 2) + m.Groups[2].Value);
                if (year2 > year1 && year2 - year1 <= 99)
                    articleText = articleText.Replace(m.Value, Regex.Replace(m.Value, @" *- *", @"â"));
            }
            articleText = FixDatesRaw(articleText);
            articleText = SyntaxRemoveBr.Replace(articleText, "\r\n");
            articleText = SyntaxRemoveParagraphs.Replace(articleText, "\r\n\r\n");
            return AddBackMoreText(articleText);
        }
        private static readonly Regex DiedDateRegex =
            new Regex(
                @"('''[^']+'''\s*\()d\.(\s+\[*(?:" + WikiRegexes.MonthsNoGroup + @"\s+0?([1-3]?[0-9])|0?([1-3]?[0-9])\s*" +
                WikiRegexes.MonthsNoGroup + @")?\]*\s*\[*[1-2]?\d{3}\]*\)\s*)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex DOBRegex =
            new Regex(
                @"('''[^']+'''\s*\()b\.(\s+\[*(?:" + WikiRegexes.MonthsNoGroup + @"\s+0?([1-3]?\d)|0?([1-3]?\d)\s*" +
                WikiRegexes.MonthsNoGroup + @")?\]*\s*\[*[1-2]?\d{3}\]*\)\s*)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex BornDeathRegex =
            new Regex(
                @"('''[^']+'''\s*\()(?:[Bb]orn|b\.)\s+(\[*(?:" + WikiRegexes.MonthsNoGroup +
                @"\s+0?(?:[1-3]?\d)|0?(?:[1-3]?\d)\s*" + WikiRegexes.MonthsNoGroup +
                @")?\]*,?\s*\[*[1-2]?\d{3}\]*)\s*(.|&.dash;)\s*(?:[Dd]ied|d\.)\s+(\[*(?:" + WikiRegexes.MonthsNoGroup +
                @"\s+0?(?:[1-3]?\d)|0?(?:[1-3]?\d)\s*" + WikiRegexes.MonthsNoGroup + @")\]*,?\s*\[*[1-2]?\d{3}\]*\)\s*)",
                RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex UnlinkedFloruit = new Regex(@"\(\s*(?:[Ff]l)\.*\s*(\d\d)", RegexOptions.Compiled);
        public static string FixLivingThingsRelatedDates(string articleText)
        {
            if (UnlinkedFloruit.IsMatch(WikiRegexes.ZerothSection.Match(articleText).Value))
                articleText = UnlinkedFloruit.Replace(articleText, @"([[floruit|fl.]] $1", 1);
            articleText = DiedDateRegex.Replace(articleText, "$1died$2");
            articleText = DOBRegex.Replace(articleText, "$1born$2");
            return BornDeathRegex.Replace(articleText, "$1$2 â $4");
        }
        public static string FixDatesRaw(string articleText)
        {
            return ApostropheInDecades.Replace(articleText, "s");
        }
        public static string FixFootnotes(string articleText)
        {
            articleText = WikiRegexes.SuperscriptedPunctuationBetweenRefs.Replace(articleText, "$1<ref");
            articleText = WikiRegexes.WhiteSpaceFactTag.Replace(articleText, "{{$1}}");
            string oldArticleText = "";
            while (oldArticleText != articleText)
            {
                oldArticleText = articleText;
                articleText = WikiRegexes.Match0A.Replace(articleText, "$1$2$4$3");
                articleText = WikiRegexes.Match0B.Replace(articleText, "$1$2$4$3");
                articleText = WikiRegexes.Match0D.Replace(articleText, "$1$2$3");
                articleText = WikiRegexes.Match1A.Replace(articleText, "$1$2$6$3");
                articleText = WikiRegexes.Match1B.Replace(articleText, "$1$2$6$3");
                articleText = WikiRegexes.Match1D.Replace(articleText, "$1$2$3");
            }
            return articleText;
        }
        private const string OutofOrderRefs = @"(<ref\s+name\s*=\s*(?:""|')?([^<>""=]+?)(?:""|')?\s*(?:\/\s*|>[^<>]+</ref)>)(\s*{{\s*rp\|[^{}]+}})?(\s*)(<ref\s+name\s*=\s*(?:""|')?([^<>""=]+?)(?:""|')?\s*(?:\/\s*|>[^<>]+</ref)>)(\s*{{\s*rp\|[^{}]+}})?";
        private static readonly Regex OutofOrderRefs1 = new Regex(@"(<ref>[^<>]+</ref>)(\s*)(<ref\s+name\s*=\s*(?:""|')?([^<>""=]+?)(?:""|')?\s*(?:\/\s*|>[^<>]+</ref)>)(\s*{{\s*[Rr]p\|[^{}]+}})?", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        private static readonly Regex OutofOrderRefs2 = new Regex(OutofOrderRefs, RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        private static readonly Regex OutofOrderRefs3 = new Regex(@"(?<=\s*(?:\/\s*|>[^<>]+</ref)>\s*(?:{{\s*rp\|[^{}]+}})?)" + OutofOrderRefs, RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static string ReorderReferences(string articleText)
        {
            int referencestags = WikiRegexes.ReferencesTemplate.Match(articleText).Index;
            if (referencestags <= 0)
                referencestags = articleText.Length;
            for (int i = 0; i < 5; i++)
            {
                string articleTextBefore = articleText;
                foreach (Match m in OutofOrderRefs1.Matches(articleText))
                {
                    string ref1 = m.Groups[1].Value;
                    int ref1Index = Regex.Match(articleText, @"(?si)<ref\s+name\s*=\s*(?:""|')?" + Regex.Escape(m.Groups[4].Value) + @"(?:""|')?\s*(?:\/\s*|>.+?</ref)>").Index;
                    int ref2Index = articleText.IndexOf(ref1);
                    if (ref1Index < ref2Index && ref2Index > 0 && ref1Index > 0 && m.Groups[3].Index < referencestags)
                    {
                        string whitespace = m.Groups[2].Value;
                        string rptemplate = m.Groups[5].Value;
                        string ref2 = m.Groups[3].Value;
                        articleText = articleText.Replace(ref1 + whitespace + ref2 + rptemplate, ref2 + rptemplate + whitespace + ref1);
                    }
                }
                articleText = ReorderRefs(articleText, OutofOrderRefs2, referencestags);
                articleText = ReorderRefs(articleText, OutofOrderRefs3, referencestags);
                if (articleTextBefore == articleText)
                    break;
            }
            return articleText;
        }
        private const string RefsPunctuation = @"([,\.:;])";
        private static readonly Regex RefsAfterPunctuationR = new Regex(RefsPunctuation + @" *" + WikiRegexes.Refs, RegexOptions.Singleline | RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RefsBeforePunctuationR = new Regex(WikiRegexes.Refs + @" *" + RefsPunctuation, RegexOptions.Singleline | RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex RefsAfterDupePunctuation = new Regex(@"([^,\.:;])([,\.:;])\2 *" + WikiRegexes.Refs, RegexOptions.Singleline | RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static string RefsAfterPunctuation(string articleText)
        {
            if (!Variables.LangCode.Equals("en"))
                return articleText;
            int RAfter = RefsAfterPunctuationR.Matches(articleText).Count;
            int RBefore = RefsBeforePunctuationR.Matches(articleText).Count;
            if (RAfter > RBefore && (RBefore == 0 || RAfter / RBefore > 3))
            {
                string articleTextlocal = "";
                while (!articleTextlocal.Equals(articleText))
                {
                    articleTextlocal = articleText;
                    articleText = RefsBeforePunctuationR.Replace(articleText, "$2$1");
                }
            }
            return RefsAfterDupePunctuation.Replace(articleText, "$1$2$3");
        }
        private static string ReorderRefs(string articleText, Regex outofOrderRegex, int referencestagindex)
        {
            foreach (Match m in outofOrderRegex.Matches(articleText))
            {
                int ref1Index = Regex.Match(articleText, @"(?si)<ref\s+name\s*=\s*(?:""|')?" + Regex.Escape(m.Groups[2].Value) + @"(?:""|')?\s*(?:\/\s*|>.+?</ref)>").Index;
                int ref2Index = Regex.Match(articleText, @"(?si)<ref\s+name\s*=\s*(?:""|')?" + Regex.Escape(m.Groups[6].Value) + @"(?:""|')?\s*(?:\/\s*|>.+?</ref)>").Index;
                if (ref1Index > ref2Index && ref1Index > 0 && m.Groups[5].Index < referencestagindex)
                {
                    string ref1 = m.Groups[1].Value;
                    string ref2 = m.Groups[5].Value;
                    string whitespace = m.Groups[4].Value;
                    string rptemplate1 = m.Groups[3].Value;
                    string rptemplate2 = m.Groups[7].Value;
                    articleText = articleText.Replace(ref1 + rptemplate1 + whitespace + ref2 + rptemplate2, ref2 + rptemplate2 + whitespace + ref1 + rptemplate1);
                }
            }
            return articleText;
        }
        private static readonly Regex LongNamedReferences = new Regex(@"(<\s*ref\s+name\s*=\s*(?:""|')?([^<>=\r\n]+?)(?:""|')?\s*>\s*([^<>]{30,}?)\s*<\s*/\s*ref>)", RegexOptions.Compiled);
        public static string DuplicateNamedReferences(string articleText)
        {
            Dictionary<string, string> NamedRefs = new Dictionary<string, string>();
            foreach (Match m in WikiRegexes.NamedReferences.Matches(articleText))
            {
                string refName = m.Groups[2].Value;
                string namedRefValue = m.Groups[3].Value;
                string name2 = "";
                if (!NamedRefs.ContainsKey(namedRefValue))
                    NamedRefs.Add(namedRefValue, refName);
                else
                {
                    NamedRefs.TryGetValue(namedRefValue, out name2);
                    if (name2.Equals(refName) && namedRefValue.Length >= 25)
                    {
                        int reflistIndex = WikiRegexes.ReferencesTemplate.Match(articleText).Index;
                        if (reflistIndex > 0 && m.Index > reflistIndex)
                            continue;
                        if (m.Index > articleText.Length)
                            continue;
                        string texttomatch = articleText.Substring(0, m.Index);
                        string textaftermatch = articleText.Substring(m.Index);
                        articleText = texttomatch + textaftermatch.Replace(m.Value, @"<ref name=""" + refName + @"""/>");
                    }
                }
                foreach (Match m3 in Regex.Matches(articleText, @"<\s*ref\s*>\s*" + Regex.Escape(namedRefValue) + @"\s*<\s*/\s*ref>"))
                    articleText = articleText.Replace(m3.Value, @"<ref name=""" + refName + @"""/>");
            }
            return articleText;
        }
        private const string RefName = @"(?si)<\s*ref\s+name\s*=\s*(?:""|')?";
        private static readonly Regex UnnamedRef = new Regex(@"<\s*ref\s*>\s*([^<>]+)\s*<\s*/\s*ref>", RegexOptions.Singleline | RegexOptions.Compiled);
        public static bool HasNamedReferences(string articleText)
        {
            return WikiRegexes.NamedReferences.IsMatch(WikiRegexes.Comments.Replace(articleText, ""));
        }
        private struct Ref
        {
            public string Text;
            public string InnerText;
        }
        public static string DuplicateUnnamedReferences(string articleText)
        {
            if (Variables.LangCode == "en" && !HasNamedReferences(articleText))
                return articleText;
            var refs = new Dictionary<int, List<Ref> >();
            bool haveRefsToFix = false;
            foreach (Match m in UnnamedRef.Matches(articleText))
            {
                var str = m.Value;
                if (WikiRegexes.IbidOpCitation.IsMatch(str)) continue;
                string inner = m.Groups[1].Value.Trim();
                int hash = inner.GetHashCode();
                List<Ref> list;
                if (refs.TryGetValue(hash, out list))
                {
                    haveRefsToFix = true;
                }
                else
                {
                    list = new List<Ref>();
                    refs[hash] = list;
                }
                list.Add(new Ref() { Text = str, InnerText = inner });
            }
            if (!haveRefsToFix)
                return articleText;
            StringBuilder result = new StringBuilder(articleText);
            foreach (var list in refs.Values)
            {
                if (list.Count < 2) continue;
                string friendlyName = DeriveReferenceName(articleText, list[0].InnerText);
                if (friendlyName.Length <= 3 || Regex.IsMatch(result.ToString(), RefName + Regex.Escape(friendlyName) + @"""\s*/?\s*>"))
                    continue;
                for (int i = 0; i < list.Count; i++)
                {
                    StringBuilder newValue = new StringBuilder();
                    newValue.Append(@"<ref name=""");
                    newValue.Append(friendlyName);
                    newValue.Append('"');
                    if (i == 0)
                    {
                        newValue.Append('>');
                        newValue.Append(list[0].InnerText);
                        newValue.Append("</ref>");
                        Tools.ReplaceOnce(result, list[0].Text, newValue.ToString());
                    }
                    else
                    {
                        newValue.Append("/>");
                        result.Replace(list[i].Text, newValue.ToString());
                    }
                }
            }
            return result.ToString();
        }
        private static readonly Regex PageRef = new Regex(@"\s*(?:(?:[Pp]ages?|[Pp][pg]?[:\.]?)|^)\s*[XVI\d]", RegexOptions.Compiled);
        public static string SameRefDifferentName(string articleText)
        {
            articleText = SameNamedRefShortText(articleText);
            Dictionary<string, string> NamedRefs = new Dictionary<string, string>();
            foreach (Match m in WikiRegexes.NamedReferences.Matches(articleText))
            {
                string refname = m.Groups[2].Value;
                string refvalue = m.Groups[3].Value;
                string existingname = "";
                if (!NamedRefs.ContainsKey(refvalue))
                {
                    NamedRefs.Add(refvalue, refname);
                    continue;
                }
                NamedRefs.TryGetValue(refvalue, out existingname);
                if (existingname.Length > 0 && !existingname.Equals(refname) && !WikiRegexes.IbidOpCitation.IsMatch(refvalue))
                {
                    string newRefName = refname;
                    string oldRefName = existingname;
                    if ((existingname.Length > refname.Length && !existingname.Contains("autogenerated")
                         && !existingname.Contains("ReferenceA")) || (refname.Contains("autogenerated")
                                                                     || refname.Contains("ReferenceA")))
                    {
                        newRefName = existingname;
                        oldRefName = refname;
                    }
                    Regex a = new Regex(@"<\s*ref\s+name\s*=\s*(?:""|')?" + Regex.Escape(oldRefName) + @"(?:""|')?\s*(?=/\s*>|>\s*" + Regex.Escape(refvalue) + @"\s*</ref>)");
                    articleText = a.Replace(articleText, @"<ref name=""" + newRefName + @"""");
                }
            }
            return DuplicateNamedReferences(articleText);
        }
        private static string SameNamedRefShortText(string articleText)
        {
            foreach (Match m in LongNamedReferences.Matches(articleText))
            {
                string refname = m.Groups[2].Value;
                string refvalue = m.Groups[3].Value;
                Regex shortNamedReferences = new Regex(@"(<\s*ref\s+name\s*=\s*(?:""|')?(" + Regex.Escape(refname) + @")(?:""|')?\s*>\s*([^<>]{1,9}?|\[?[Ss]ee above\]?)\s*<\s*/\s*ref>)");
                foreach (Match m2 in shortNamedReferences.Matches(articleText))
                {
                    if (refvalue.Length > 30 && !PageRef.IsMatch(m2.Groups[3].Value))
                        articleText = articleText.Replace(m2.Value, @"<ref name=""" + refname + @"""/>");
                }
            }
            return articleText;
        }
        private static string ExtractReferenceNameComponents(string reference, Regex referenceNameMask, int components)
        {
            string referenceName = "";
            if (referenceNameMask.Matches(reference).Count > 0)
            {
                Match m = referenceNameMask.Match(reference);
                referenceName = m.Groups[1].Value;
                if (components > 1)
                    referenceName += " " + m.Groups[2].Value;
                if (components > 2)
                    referenceName += " " + m.Groups[3].Value;
            }
            return CleanDerivedReferenceName(referenceName);
        }
        private const string CharsToTrim = @".;: {}[]|`?\/$ââ-_â=+,";
        private static readonly Regex CommentOrFloorNumber = new Regex(@"(\<\!--.*?--\>|" + "\u0000" + @"{3,}\d+" + "\u0000" + "{3,})", RegexOptions.Compiled);
        private static readonly Regex SequenceOfQuotesInDerivedName = new Regex(@"(''+|[âââ""\[\]\(\)\<\>" + "\u0000\u0000" + "])", RegexOptions.Compiled);
        private static readonly Regex WhitespaceInDerivedName = new Regex(@"(\s{2,}|&nbsp;|\t|\n)", RegexOptions.Compiled);
        private static readonly Regex DateRetrievedOrAccessed = new Regex(@"(?im)(\s*(date\s+)?(retrieved|accessed)\b|^\d+$)", RegexOptions.Compiled);
        private static string CleanDerivedReferenceName(string derivedName)
        {
            derivedName = WikiRegexes.PipedWikiLink.Replace(derivedName, "$2");
            derivedName = CommentOrFloorNumber.Replace(derivedName, "");
            derivedName = derivedName.Trim(CharsToTrim.ToCharArray());
            derivedName = SequenceOfQuotesInDerivedName.Replace(derivedName, "");
            derivedName = WhitespaceInDerivedName.Replace(derivedName, " ");
            derivedName = derivedName.Replace(@"&ndash;", "â");
            return DateRetrievedOrAccessed.IsMatch(derivedName) ? "" : derivedName;
        }
        private const string NameMask = @"(?-i)\s*(?:sir)?\s*((?:[A-Z]+\.?){0,3}\s*[A-Z][\w-']{2,}[,\.]?\s*(?:\s+\w\.?|\b(?:[A-Z]+\.?){0,3})?(?:\s+[A-Z][\w-']{2,}){0,3}(?:\s+\w(?:\.?|\b)){0,2})\s*(?:[,\.'&;:\[\(â`]|et\s+al)(?i)[^{}<>\n]*?";
        private const string YearMask = @"(\([12]\d{3}\)|\b[12]\d{3}[,\.\)])";
        private const string PageMask = @"('*(?:p+g?|pages?)'*\.?'*(?:&nbsp;)?\s*(?:\d{1,3}|(?-i)[XVICM]+(?i))\.?(?:\s*[-/&\.,]\s*(?:\d{1,3}|(?-i)[XVICM]+(?i)))?\b)";
        private static readonly Regex CitationCiteBook = new Regex(@"{{[Cc]it[ae]((?>[^\{\}]+|\{(?<DEPTH>)|\}(?<-DEPTH>))*(?(DEPTH)(?!))}})", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateLastParameter = new Regex(@"(?<=\s*last\s*=\s*)([^{}\|<>]+?)(?=\s*(?:\||}}))", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateAuthorParameter = new Regex(@"(?<=\s*author(?:link)?\s*=\s*)([^{}\|<>]+?)(?=\s*(?:\||}}))", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateYearParameter = new Regex(@"(?<=\s*year\s*=\s*)([^{}\|<>]+?)(?=\s*(?:\||}}))", RegexOptions.Compiled);
        private static readonly Regex CiteTemplatePagesParameter = new Regex(@"(?<=\s*pages?\s*=\s*)([^{}\|<>]+?)(?=\s*(?:\||}}))", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateDateParameter = new Regex(@"(?<=\s*date\s*=\s*)(\d{4})(?=\s*(?:\||}}))", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateTitleParameter = new Regex(@"(?<=\s*title\s*=\s*)([^{}\|<>]+?)(?=\s*(?:\||}}))", RegexOptions.Compiled);
        private static readonly Regex CiteTemplatePublisherParameter = new Regex(@"(?<=\s*publisher\s*=\s*)([^{}\|<>]+?)(?=\s*(?:\||}}))", RegexOptions.Compiled);
        private static readonly Regex UrlShortDescription = new Regex(@"\s*[^{}<>\n]*?\s*\[*(?:http://www\.|http://|www\.)[^\[\]<>""\s]+?\s+([^{}<>\[\]]{4,35}?)\s*(?:\]|<!--|\u0000\u0000\u0000\u0000)", RegexOptions.Compiled);
        private static readonly Regex UrlDomain = new Regex(@"\s*\w*?[^{}<>]{0,4}?\s*(?:\[?|\{\{\s*cit[^{}<>]*\|\s*url\s*=\s*)\s*(?:http://www\.|http://|www\.)([^\[\]<>""\s\/:]+)", RegexOptions.Compiled);
        private static readonly Regex HarvnbTemplate = new Regex(@"\s*{{[Hh]arv(?:(?:col)?(?:nb|txt))?\s*\|\s*([^{}\|]+?)\s*\|(?:[^{}]*?\|)?\s*(\d{4})\s*(?:\|\s*(?:pp?\s*=\s*)?([^{}\|]+?)\s*)?}}\s*", RegexOptions.Compiled);
        private static readonly Regex WholeShortReference = new Regex(@"\s*([^<>{}]{4,35})\s*", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateUrl = new Regex(@"\s*\{\{\s*cit[^{}<>]*\|\s*url\s*=\s*([^\/<>{}\|]{4,35})", RegexOptions.Compiled);
        private static readonly Regex NameYearPage = new Regex(NameMask + YearMask + @"[^{}<>\n]*?" + PageMask + @"\s*", RegexOptions.Compiled);
        private static readonly Regex NamePage = new Regex(NameMask + PageMask + @"\s*", RegexOptions.Compiled);
        private static readonly Regex NameYear = new Regex(NameMask + YearMask + @"\s*", RegexOptions.Compiled);
        public static string DeriveReferenceName(string articleText, string reference)
        {
            string derivedReferenceName = "";
            string citationTemplate = CitationCiteBook.Match(reference).Value;
            if (citationTemplate.Length > 10)
            {
                string last = CiteTemplateLastParameter.Match(reference).Value.Trim();
                if (last.Length < 1)
                {
                    last = CiteTemplateAuthorParameter.Match(reference).Value.Trim();
                }
                if (last.Length > 1)
                {
                    derivedReferenceName = last;
                    string year = CiteTemplateYearParameter.Match(reference).Value.Trim();
                    string pages = CiteTemplatePagesParameter.Match(reference).Value.Trim();
                    if (year.Length > 3)
                        derivedReferenceName += " " + year;
                    else
                    {
                        string date = CiteTemplateDateParameter.Match(reference).Value.Trim();
                        if (date.Length > 3)
                            derivedReferenceName += " " + date;
                    }
                    if (pages.Length > 0)
                        derivedReferenceName += " " + pages;
                    derivedReferenceName = CleanDerivedReferenceName(derivedReferenceName);
                }
                else
                {
                    string title = CiteTemplateTitleParameter.Match(reference).Value.Trim();
                    if (title.Length > 3 && title.Length < 35)
                        derivedReferenceName = title;
                    derivedReferenceName = CleanDerivedReferenceName(derivedReferenceName);
                    if (derivedReferenceName.Length < 4)
                    {
                        title = CiteTemplatePublisherParameter.Match(reference).Value.Trim();
                        if (title.Length > 3 && title.Length < 35)
                            derivedReferenceName = title;
                        derivedReferenceName = CleanDerivedReferenceName(derivedReferenceName);
                    }
                }
            }
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            derivedReferenceName = ExtractReferenceNameComponents(reference, UrlShortDescription, 1);
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            derivedReferenceName = ExtractReferenceNameComponents(reference, UrlDomain, 1);
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            derivedReferenceName = ExtractReferenceNameComponents(reference, HarvnbTemplate, 3);
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            if (reference.Length < 35)
                derivedReferenceName = ExtractReferenceNameComponents(reference, WholeShortReference, 1);
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            derivedReferenceName = ExtractReferenceNameComponents(reference, CiteTemplateUrl, 1);
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            derivedReferenceName = ExtractReferenceNameComponents(reference, NameYearPage, 3);
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            derivedReferenceName = ExtractReferenceNameComponents(reference, NamePage, 2);
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            derivedReferenceName = ExtractReferenceNameComponents(reference, NameYear, 2);
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            derivedReferenceName = @"ReferenceA";
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            derivedReferenceName = @"ReferenceB";
            if (ReferenceNameValid(articleText, derivedReferenceName))
                return derivedReferenceName;
            derivedReferenceName = @"ReferenceC";
            return ReferenceNameValid(articleText, derivedReferenceName) ? derivedReferenceName : "";
        }
        private static bool ReferenceNameValid(string articleText, string derivedReferenceName)
        {
            return !Regex.IsMatch(articleText, RefName + Regex.Escape(derivedReferenceName) + @"(?:""|')?\s*/?\s*>") && derivedReferenceName.Length >= 3;
        }
        public string CiteTemplateDates(string articleText, out bool noChange)
        {
            string newText = CiteTemplateDates(articleText);
            noChange = (newText == articleText);
            return newText;
        }
        private static readonly Regex CiteWeb = Tools.NestedTemplateRegex(new[] { "cite web", "citeweb" });
        private static readonly Regex CitationPopulatedParameter = new Regex(@"\|\s*([a-z_0-9-]+)\s*=\s*([^\|}]{3,}?)\s*");
        public static Dictionary<int, int> BadCiteParameters(string articleText)
        {
            Regex citeWebParameters = new Regex(@"\b(first\d?|last\d?|author|authorlink\d?|coauthors|title|url|archiveurl|work|publisher|location|pages?|language|trans_title|format|doi|date|month|year|archivedate|accessdate|quote|ref|separator|postscript|at)\b");
            Dictionary<int, int> found = new Dictionary<int, int>();
            foreach (Match m in CiteWeb.Matches(articleText))
            {
                foreach (Match m2 in CitationPopulatedParameter.Matches(m.Value))
                {
                    if (!citeWebParameters.IsMatch(m2.Groups[1].Value) && m2.Groups[2].Value.Trim().Length > 2)
                        found.Add(m.Index + m2.Groups[1].Index, m2.Groups[1].Length);
                }
            }
            foreach (Match m in WikiRegexes.CiteTemplate.Matches(articleText))
            {
                if (Regex.IsMatch(Tools.GetTemplateName(m.Value), @"[Cc]ite\s*(?:web|book|news|journal|paper|press release|hansard|encyclopedia)"))
                {
                    string pipecleaned = Tools.PipeCleanedTemplate(m.Value, false);
                    if (Regex.Matches(pipecleaned, @"=").Count > 0)
                    {
                        int noequals = Regex.Match(pipecleaned, @"\|[^=]+?\|").Index;
                        if (noequals > 0)
                            found.Add(m.Index + noequals, Regex.Match(pipecleaned, @"\|[^=]+?\|").Value.Length);
                    }
                }
            }
            return found;
        }
        public static Dictionary<int, int> DeadLinks(string articleText)
        {
            articleText = Tools.ReplaceWithSpaces(articleText, WikiRegexes.Comments);
            Dictionary<int, int> found = new Dictionary<int, int>();
            foreach (Match m in WikiRegexes.DeadLink.Matches(articleText))
            {
                found.Add(m.Index, m.Length);
            }
            return found;
        }
        private const string SiCitStart = @"(?si)(\{\{\s*cit[^{}]*\|\s*";
        private const string CitAccessdate = SiCitStart + @"(?:access|archive)date\s*=\s*";
        private const string CitDate = SiCitStart + @"(?:archive|air)?date2?\s*=\s*";
        private const string CitYMonthD = SiCitStart + @"(?:archive|air|access)?date2?\s*=\s*\d{4})[-/\s]";
        private const string dTemEnd = @"?[-/\s]([0-3]?\d\s*(?:\||}}))";
        private static readonly Regex AccessOrArchiveDate = new Regex(@"\b(access|archive)date\s*=", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static readonly RegexReplacement[] CiteTemplateIncorrectISOAccessdates = new[] {
            new RegexReplacement(CitAccessdate + @")(1[0-2])[/_\-\.]?(1[3-9])[/_\-\.]?(?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-$2-$3"),
            new RegexReplacement(CitAccessdate + @")(1[0-2])[/_\-\.]?([2-3]\d)[/_\-\.]?(?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-$2-$3"),
            new RegexReplacement(CitAccessdate + @")(1[0-2])[/_\-\.]?\2[/_\-\.]?(?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$3-$2-$2"),
            new RegexReplacement(CitAccessdate + @")(1[3-9])[/_\-\.]?(1[0-2])[/_\-\.]?(?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-$3-$2"),
            new RegexReplacement(CitAccessdate + @")(1[3-9])[/_\-\.]?0?([1-9])[/_\-\.]?(?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-0$3-$2"),
            new RegexReplacement(CitAccessdate + @")(20[01]\d)0?([01]\d)[/_\-\.]([0-3]\d\s*(?:\||}}))", "$1$2-$3-$4"),
            new RegexReplacement(CitAccessdate + @")(20[01]\d)[/_\-\.]([01]\d)0?([0-3]\d\s*(?:\||}}))", "$1$2-$3-$4"),
            new RegexReplacement(CitAccessdate + @")(20[01]\d)[/_\-\.]?([01]\d)[/_\-\.]?([1-9]\s*(?:\||}}))", "$1$2-$3-0$4"),
            new RegexReplacement(CitAccessdate + @")(20[01]\d)[/_\-\.]?([1-9])[/_\-\.]?([0-3]\d\s*(?:\||}}))", "$1$2-0$3-$4"),
            new RegexReplacement(CitAccessdate + @")(20[01]\d)[/_\-\.]?([1-9])[/_\-\.]0?([1-9]\s*(?:\||}}))", "$1$2-0$3-0$4"),
            new RegexReplacement(CitAccessdate + @")(20[01]\d)[/_\-\.]0?([1-9])[/_\-\.]([1-9]\s*(?:\||}}))", "$1$2-0$3-0$4"),
            new RegexReplacement(CitAccessdate + @")(20[01]\d)[/_\.]?([01]\d)[/_\.]?([0-3]\d\s*(?:\||}}))", "$1$2-$3-$4"),
            new RegexReplacement(CitAccessdate + @")([2-3]\d)[/_\-\.]?(1[0-2])[/_\-\.]?(?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-$3-$2"),
            new RegexReplacement(CitAccessdate + @")([2-3]\d)[/_\-\.]0?([1-9])[/_\-\.](?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-0$3-$2"),
            new RegexReplacement(CitAccessdate + @")0?([1-9])[/_\-\.]?(1[3-9]|[2-3]\d)[/_\-\.]?(?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-0$2-$3"),
            new RegexReplacement(CitAccessdate + @")0?([1-9])[/_\-\.]?0?\2[/_\-\.]?(?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$3-0$2-0$2"),
        };
        private static readonly Regex CiteTemplateArchiveAirDate = new Regex(@"{{\s*cit[^{}]*\|\s*(?:archive|air)?date2?\s*=", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        private static readonly RegexReplacement[] CiteTemplateIncorrectISODates = new[] {
            new RegexReplacement(CitDate + @"\[?\[?)(20\d\d|19[7-9]\d)[/_]?([0-1]\d)[/_]?([0-3]\d\s*(?:\||}}))", "$1$2-$3-$4"),
            new RegexReplacement(CitDate + @"\[?\[?)(1[0-2])[/_\-\.]?([2-3]\d)[/_\-\.]?(19[7-9]\d)(?=\s*(?:\||}}))", "$1$4-$2-$3"),
            new RegexReplacement(CitDate + @"\[?\[?)0?([1-9])[/_\-\.]?([2-3]\d)[/_\-\.]?(19[7-9]\d)(?=\s*(?:\||}}))", "$1$4-0$2-$3"),
            new RegexReplacement(CitDate + @"\[?\[?)([2-3]\d)[/_\-\.]?0?([1-9])[/_\-\.]?(19[7-9]\d)(?=\s*(?:\||}}))", "$1$4-0$3-$2"),
            new RegexReplacement(CitDate + @"\[?\[?)([2-3]\d)[/_\-\.]?(1[0-2])[/_\-\.]?(19[7-9]\d)(?=\s*(?:\||}}))", "$1$4-$3-$2"),
            new RegexReplacement(CitDate + @"\[?\[?)(1[0-2])[/_\-\.]([2-3]\d)[/_\-\.](?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-$2-$3"),
            new RegexReplacement(CitDate + @"\[?\[?)0?([1-9])[/_\-\.]([2-3]\d)[/_\-\.](?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-0$2-$3"),
            new RegexReplacement(CitDate + @"\[?\[?)([2-3]\d)[/_\-\.]0?([1-9])[/_\-\.](?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-0$3-$2"),
            new RegexReplacement(CitDate + @"\[?\[?)([2-3]\d)[/_\-\.](1[0-2])[/_\-\.]?(?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-$3-$2"),
            new RegexReplacement(CitDate + @"\[?\[?)(1[0-2])[/_\-\.]?(1[3-9])[/_\-\.]?(19[7-9]\d)(?=\s*(?:\||}}))", "$1$4-$2-$3"),
            new RegexReplacement(CitDate + @"\[?\[?)0?([1-9])[/_\-\.](1[3-9])[/_\-\.](19[7-9]\d)(?=\s*(?:\||}}))", "$1$4-0$2-$3"),
            new RegexReplacement(CitDate + @"\[?\[?)(1[3-9])[/_\-\.]?0?([1-9])[/_\-\.]?(19[7-9]\d)(?=\s*(?:\||}}))", "$1$4-0$3-$2"),
            new RegexReplacement(CitDate + @"\[?\[?)(1[3-9])[/_\-\.]?(1[0-2])[/_\-\.]?(19[7-9]\d)(?=\s*(?:\||}}))", "$1$4-$3-$2"),
            new RegexReplacement(CitDate + @"\[?\[?)(1[0-2])[/_\-\.](1[3-9])[/_\-\.](?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-$2-$3"),
            new RegexReplacement(CitDate + @"\[?\[?)([1-9])[/_\-\.](1[3-9])[/_\-\.](?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-0$2-$3"),
            new RegexReplacement(CitDate + @"\[?\[?)(1[3-9])[/_\-\.]([1-9])[/_\-\.](?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-0$3-$2"),
            new RegexReplacement(CitDate + @"\[?\[?)(1[3-9])[/_\-\.](1[0-2])[/_\-\.](?:20)?([01]\d)(?=\s*(?:\||}}))", "${1}20$4-$3-$2"),
            new RegexReplacement(CitDate + @")0?([1-9])[/_\-\.]0?\2[/_\-\.](20\d\d|19[7-9]\d)(?=\s*(?:\||}}))", "$1$3-0$2-0$2"),
            new RegexReplacement(CitDate + @")0?([1-9])[/_\-\.]0?\2[/_\-\.]([01]\d)(?=\s*(?:\||}}))", "${1}20$3-0$2-0$2"),
            new RegexReplacement(CitDate + @")(1[0-2])[/_\-\.]\2[/_\-\.]?(20\d\d|19[7-9]\d)(?=\s*(?:\||}}))", "$1$3-$2-$2"),
            new RegexReplacement(CitDate + @")(1[0-2])[/_\-\.]\2[/_\-\.]([01]\d)(?=\s*(?:\||}}))", "${1}20$3-$2-$2"),
            new RegexReplacement(CitDate + @")((?:\[\[)?20\d\d|19[7-9]\d)[/_\-\.]([1-9])[/_\-\.]0?([1-9](?:\]\])?\s*(?:\||}}))", "$1$2-0$3-0$4"),
            new RegexReplacement(CitDate + @")((?:\[\[)?20\d\d|19[7-9]\d)[/_\-\.]0?([1-9])[/_\-\.]([1-9](?:\]\])?\s*(?:\||}}))", "$1$2-0$3-0$4"),
            new RegexReplacement(CitDate + @")((?:\[\[)?20\d\d|19[7-9]\d)[/_\-\.]?([0-1]\d)[/_\-\.]?([1-9](?:\]\])?\s*(?:\||}}))", "$1$2-$3-0$4"),
            new RegexReplacement(CitDate + @")((?:\[\[)?20\d\d|19[7-9]\d)[/_\-\.]?([1-9])[/_\-\.]?([0-3]\d(?:\]\])?\s*(?:\||}}))", "$1$2-0$3-$4"),
            new RegexReplacement(CitDate + @")((?:\[\[)?20\d\d|19[7-9]\d)([0-1]\d)[/_\-\.]([0-3]\d(?:\]\])?\s*(?:\||}}))", "$1$2-$3-$4"),
            new RegexReplacement(CitDate + @")((?:\[\[)?20\d\d|19[7-9]\d)[/_\-\.]([0-1]\d)0?([0-3]\d(?:\]\])?\s*(?:\||}}))", "$1$2-$3-$4"),
        };
        private static readonly RegexReplacement[] CiteTemplateAbbreviatedMonths = new[] {
            new RegexReplacement(CitYMonthD + @"Jan(?:uary|\.)" + dTemEnd, "$1-01-$2"),
            new RegexReplacement(CitYMonthD + @"Feb(?:r?uary|\.)" + dTemEnd, "$1-02-$2"),
            new RegexReplacement(CitYMonthD + @"Mar(?:ch|\.)" + dTemEnd, "$1-03-$2"),
            new RegexReplacement(CitYMonthD + @"Apr(?:il|\.)" + dTemEnd, "$1-04-$2"),
            new RegexReplacement(CitYMonthD + @"May\." + dTemEnd, "$1-05-$2"),
            new RegexReplacement(CitYMonthD + @"Jun(?:e|\.)" + dTemEnd, "$1-06-$2"),
            new RegexReplacement(CitYMonthD + @"Jul(?:y|\.)" + dTemEnd, "$1-07-$2"),
            new RegexReplacement(CitYMonthD + @"Aug(?:ust|\.)" + dTemEnd, "$1-08-$2"),
            new RegexReplacement(CitYMonthD + @"Sep(?:tember|\.)" + dTemEnd, "$1-09-$2"),
            new RegexReplacement(CitYMonthD + @"Oct(?:ober|\.)" + dTemEnd, "$1-10-$2"),
            new RegexReplacement(CitYMonthD + @"Nov(?:ember|\.)" + dTemEnd, "$1-11-$2"),
            new RegexReplacement(CitYMonthD + @"Dec(?:ember|\.)" + dTemEnd, "$1-12-$2"),
        };
        private static readonly Regex CiteTemplateDateYYYYDDMMFormat = new Regex(SiCitStart + @"(?:archive|air|access)?date2?\s*=\s*(?:\[\[)?20\d\d)-([2-3]\d|1[3-9])-(0[1-9]|1[0-2])(\]\])?", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateTimeInDateParameter = new Regex(@"(\{\{\s*cite[^\{\}]*\|\s*(?:archive|air|access)?date2?\s*=\s*(?:(?:20\d\d|19[7-9]\d)-[01]?\d-[0-3]?\d|[0-3]?\d\s*\w+,?\s*(?:20\d\d|19[7-9]\d)|\w+\s*[0-3]?\d,?\s*(?:20\d\d|19[7-9]\d)))(\s*[,-:]?\s+[0-2]?\d[:\.]?[0-5]\d(?:\:?[0-5]\d)?\s*[^\|\}]*)", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static string CiteTemplateDates(string articleText)
        {
            if (!Variables.IsWikipediaEN)
                return articleText;
            if (Tools.NestedTemplateRegex("cite podcast").IsMatch(articleText))
                return articleText;
            string articleTextlocal = "";
            while (articleTextlocal != articleText)
            {
                articleTextlocal = articleText;
                if (AccessOrArchiveDate.IsMatch(articleText) && !AmbiguousCiteTemplateDates(articleText))
                    foreach (RegexReplacement rr in CiteTemplateIncorrectISOAccessdates)
                        articleText = rr.Regex.Replace(articleText, rr.Replacement);
                if (CiteTemplateArchiveAirDate.IsMatch(articleText) && !AmbiguousCiteTemplateDates(articleText))
                {
                    foreach (RegexReplacement rr in CiteTemplateIncorrectISODates)
                        articleText = rr.Regex.Replace(articleText, rr.Replacement);
                    foreach (RegexReplacement rr in CiteTemplateAbbreviatedMonths)
                        articleText = rr.Regex.Replace(articleText, rr.Replacement);
                }
                articleText = CiteTemplateDateYYYYDDMMFormat.Replace(articleText, "$1-$3-$2$4");
                articleText = CiteTemplateTimeInDateParameter.Replace(articleText, "$1<!--$2-->");
            }
            return articleText;
        }
        private static readonly Regex PossibleAmbiguousCiteDate = new Regex(@"(?<={{\s*[Cc]it[ae][^{}]+?\|\s*(?:access|archive|air)?date2?\s*=\s*)(0?[1-9]|1[0-2])[/_\-\.](0?[1-9]|1[0-2])[/_\-\.](20\d\d|19[7-9]\d|[01]\d)\b");
        public static bool AmbiguousCiteTemplateDates(string articleText)
        {
            return AmbigCiteTemplateDates(articleText).Count > 0;
        }
        public static bool HasSeeAlsoAfterNotesReferencesOrExternalLinks(string articleText)
        {
            int seeAlso = WikiRegexes.SeeAlso.Match(articleText).Index;
            if(seeAlso <= 0)
                return false;
            int externalLinks = WikiRegexes.ExternalLinksHeaderRegex.Match(articleText).Index;
            if(externalLinks > 0 && seeAlso > externalLinks)
                return true;
            int references = WikiRegexes.ReferencesRegex.Match(articleText).Index;
            if(references > 0 && seeAlso > references)
                return true;
            int notes = WikiRegexes.NotesHeading.Match(articleText).Index;
            if(notes > 0 && seeAlso > notes)
                return true;
            return false;
        }
        private static readonly Regex MathSourceCodeNowikiPreTagStart = new Regex(@"<\s*(?:math|(?:source|ref)\b[^>]*|code|nowiki|pre|small)\s*>", RegexOptions.Compiled);
        public static Dictionary<int, int> UnclosedTags(string articleText)
        {
            Dictionary<int, int> back = new Dictionary<int, int>();
            articleText = Tools.ReplaceWithSpaces(articleText, WikiRegexes.UnformattedText);
            articleText = Tools.ReplaceWithSpaces(articleText, WikiRegexes.Code);
            articleText = Tools.ReplaceWithSpaces(articleText, WikiRegexes.Source);
            articleText = Tools.ReplaceWithSpaces(articleText, WikiRegexes.Small);
            articleText = Tools.ReplaceWithSpaces(articleText, WikiRegexes.Refs);
            foreach (Match m in MathSourceCodeNowikiPreTagStart.Matches(articleText))
            {
                back.Add(m.Index, m.Length);
            }
            return back;
        }
        public static Dictionary<int, int> AmbigCiteTemplateDates(string articleText)
        {
            Dictionary<int, int> ambigDates = new Dictionary<int, int>();
            foreach (Match m in PossibleAmbiguousCiteDate.Matches(articleText))
            {
                if (m.Groups[1].Value != m.Groups[2].Value)
                    ambigDates.Add(m.Index, m.Length);
            }
            return ambigDates;
        }
        [Obsolete("namespace key no longer needed as input")]
        public string Mdashes(string articleText, string articleTitle, int nameSpaceKey)
        {
            return Mdashes(articleText, articleTitle);
        }
        private static readonly Regex PageRangeIncorrectMdash = new Regex(@"(pages\s*=\s*|pp\.?\s*)((?:&nbsp;)?\d+\s*)(?:-|â|&mdash;|&#8212;)(\s*\d+)", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static readonly Regex UnitTimeRangeIncorrectMdash = new Regex(@"(?<!-)(\b[1-9]?\d+\s*)(?:-|â|&mdash;|&#8212;)(\s*[1-9]?\d+)(\s+|&nbsp;)((?:years|months|weeks|days|hours|minutes|seconds|[km]g|kb|[ckm]?m|[Gk]?Hz|miles|mi\.|%|feet|foot|ft|met(?:er|re)s)\b|in\))", RegexOptions.Compiled);
        private static readonly Regex DollarAmountIncorrectMdash = new Regex(@"(\$[1-9]?\d{1,3}\s*)(?:-|â|&mdash;|&#8212;)(\s*\$?[1-9]?\d{1,3})", RegexOptions.Compiled);
        private static readonly Regex AMPMIncorrectMdash = new Regex(@"([01]?\d:[0-5]\d\s*([AP]M)\s*)(?:-|â|&mdash;|&#8212;)(\s*[01]?\d:[0-5]\d\s*([AP]M))", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static readonly Regex AgeIncorrectMdash = new Regex(@"([Aa]ge[sd])\s([1-9]?\d\s*)(?:-|â|&mdash;|&#8212;)(\s*[1-9]?\d)", RegexOptions.Compiled);
        private static readonly Regex SentenceClauseIncorrectMdash = new Regex(@"(?!xn--)(\w{2})\s*--\s*(?=\w)", RegexOptions.Compiled);
        private static readonly Regex SuperscriptMinus = new Regex(@"(?<=<sup>)(?:-|â|â)(?=\d+</sup>)", RegexOptions.Compiled);
        public string Mdashes(string articleText, string articleTitle)
        {
            articleText = HideMoreText(articleText);
            foreach (Match m in PageRangeIncorrectMdash.Matches(articleText))
            {
                string pagespart = m.Groups[1].Value;
                if (pagespart.Contains(@"Pp"))
                    pagespart = pagespart.ToLower();
                articleText = articleText.Replace(m.Value, pagespart + m.Groups[2].Value + @"â" + m.Groups[3].Value);
            }
            articleText = PageRangeIncorrectMdash.Replace(articleText, @"$1$2â$3");
            articleText = UnitTimeRangeIncorrectMdash.Replace(articleText, @"$1â$2$3$4");
            articleText = DollarAmountIncorrectMdash.Replace(articleText, @"$1â$2");
            articleText = AMPMIncorrectMdash.Replace(articleText, @"$1â$3");
            articleText = AgeIncorrectMdash.Replace(articleText, @"$1 $2â$3");
            if (articleTitle.Contains(@"â") || articleTitle.Contains(@"â"))
                articleText = Regex.Replace(articleText, Regex.Escape(articleTitle.Replace(@"â", @"-").Replace(@"â", @"-")), articleTitle);
            if (Namespace.Determine(articleTitle) == Namespace.Mainspace)
                articleText = SentenceClauseIncorrectMdash.Replace(articleText, @"$1â");
            articleText = SuperscriptMinus.Replace(articleText, "â");
            return AddBackMoreText(articleText);
        }
        private static string ReflistMatchEvaluator(Match m)
        {
            if (DivStart.Matches(m.Value).Count != DivEnd.Matches(m.Value).Count)
                return m.Value;
            if (m.Value.Contains("references-2column"))
                return "{{reflist|2}}";
            return "{{reflist}}";
        }
        private static readonly Regex ReferenceListTags = new Regex(@"(<(span|div)( class=""(references-small|small|references-2column)|)?"">[\r\n\s]*){1,2}[\r\n\s]*<references[\s]?/>([\r\n\s]*</(span|div)>){1,2}", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static readonly Regex DivStart = new Regex(@"<div\b.*?>", RegexOptions.Compiled);
        private static readonly Regex DivEnd = new Regex(@"< ?/ ?div\b.*?>", RegexOptions.Compiled);
        public static string FixReferenceListTags(string articleText)
        {
            return ReferenceListTags.Replace(articleText, new MatchEvaluator(ReflistMatchEvaluator));
        }
        private static readonly Regex EmptyReferences = new Regex(@"(<ref\s+name\s*=\s*(?:""|')?[^<>=\r\n]+?(?:""|')?)\s*>\s*<\s*/\s*ref\s*>", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static string SimplifyReferenceTags(string articleText)
        {
            return EmptyReferences.Replace(articleText, @"$1 />");
        }
        private static readonly Regex LinksHeading = new Regex(@"(?sim)(==+\s*)Links(\s*==+\s*(?:^(?:\*|\d\.?)?\s*\[?\s*http://))", RegexOptions.Compiled);
        private static readonly Regex ReferencesHeadingLevel2 = new Regex(@"(?i)==\s*'*\s*References?\s*'*\s*==", RegexOptions.Compiled);
        private static readonly Regex ReferencesHeadingLevelLower = new Regex(@"(?i)(==+\s*'*\s*References?\s*'*\s*==+)", RegexOptions.Compiled);
        private static readonly Regex ExternalLinksHeading = new Regex(@"(?im)(^\s*=+\s*(?:External\s+link|Source|Web\s*link)s?\s*=)", RegexOptions.Compiled);
        private static readonly Regex ExternalLinksToReferences = new Regex(@"(?sim)(^\s*=+\s*(?:External\s+link|Source|Web\s*link)s?\s*=+.*?)(\r\n==+References==+\r\n{{Reflist}}<!--added above External links/Sources by script-assisted edit-->)", RegexOptions.Compiled);
        private static readonly Regex Category = new Regex(@"(?im)(^\s*\[\[\s*Category\s*:)", RegexOptions.Compiled);
        private static readonly Regex CategoryToReferences = new Regex(@"(?sim)((?:^\{\{(?![Tt]racklist\b)[^{}]+?\}\}\s*)*)(^\s*\[\[\s*Category\s*:.*?)(\r\n==+References==+\r\n{{Reflist}}<!--added above categories/infobox footers by script-assisted edit-->)", RegexOptions.Compiled);
        private static readonly Regex ReflistByScript = new Regex(@"(\{\{Reflist\}\})<!--added[^<>]+by script-assisted edit-->", RegexOptions.Compiled);
        private static readonly Regex ReferencesMissingSlash = new Regex(@"<references>", RegexOptions.Compiled);
        public static string AddMissingReflist(string articleText)
        {
            if (!IsMissingReferencesDisplay(articleText))
                return articleText;
            if (ReferencesMissingSlash.IsMatch(articleText))
                return ReferencesMissingSlash.Replace(articleText, @"<references/>");
            articleText = LinksHeading.Replace(articleText, "$1External links$2");
            if (ReferencesHeadingLevel2.IsMatch(articleText))
                articleText = ReferencesHeadingLevelLower.Replace(articleText, "$1\r\n{{Reflist}}<!--added under references heading by script-assisted edit-->");
            else
            {
                if (ExternalLinksHeading.IsMatch(articleText))
                {
                    articleText += "\r\n==References==\r\n{{Reflist}}<!--added above External links/Sources by script-assisted edit-->";
                    articleText = ExternalLinksToReferences.Replace(articleText, "$2\r\n$1");
                }
                else
                {
                    if (Category.IsMatch(articleText))
                    {
                        articleText += "\r\n==References==\r\n{{Reflist}}<!--added above categories/infobox footers by script-assisted edit-->";
                        articleText = CategoryToReferences.Replace(articleText, "$3\r\n$1$2");
                    }
                }
            }
            return ReflistByScript.Replace(articleText, "$1");
        }
        private static readonly RegexReplacement[] RefWhitespace = new[] {
            new RegexReplacement(new Regex(@"<\s*(?:\s+ref\s*|\s*ref\s+)>", RegexOptions.Compiled | RegexOptions.Singleline), "<ref>"),
            new RegexReplacement(new Regex(@"<(?:\s*/(?:\s+ref\s*|\s*ref\s+)|\s+/\s*ref\s*)>", RegexOptions.Compiled | RegexOptions.Singleline), "</ref>"),
            new RegexReplacement(new Regex(@"(</ref>|<ref\s*name\s*=[^{}<>]+?\s*\/\s*>) +(?=<ref(?:\s*name\s*=[^{}<>]+?\s*\/?\s*)?>)", RegexOptions.Compiled), "$1"),
            new RegexReplacement(new Regex(@"(</ref>|<ref\s*name\s*=[^{}<>]+?\s*\/\s*>)(\w)", RegexOptions.Compiled), "$1 $2"),
            new RegexReplacement(new Regex(@"([,\.:;]) +(?=<ref(?:\s*name\s*=[^{}<>]+?\s*\/?\s*)?>)", RegexOptions.Compiled), "$1"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*""[^<>=""\/]+?"")\s*/\s*(?:ref|/)\s*>", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), "$1/>"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*""[^<>=""\/]+?"")["">]\s*(/?)>", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), "$1$2>"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*)[ââââ]*([^<>=""\/]+?)[ââââ]+(\s*/?>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), @"$1""$2""$3"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*)[ââââ]+([^<>=""\/]+?)[ââââ]*(\s*/?>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), @"$1""$2""$3"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*)''+([^<>=""\/]+?)'+(\s*/?>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), @"$1""$2""$3"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*)'+([^<>=""\/]+?)''+(\s*/?>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), @"$1""$2""$3"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*)([^<>=""'\/]+?\s+[^<>=""'\/\s]+?)(\s*/?>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), @"$1""$2""$3"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*)([^<>=""'\/]*?[^\x00-\xff]+?[^<>=""'\/]*?)(\s*/?>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), @"$1""$2""$3"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*)['`â]*([^<>=""\/]+?)""(\s*/?>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), @"$1""$2""$3"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*)""([^<>=""\/]+?)['`â]*(\s*/?>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), @"$1""$2""$3"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*)[\+\-]?(\s*""[^<>=""\/]+?""\s*/?>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), @"$1=$2"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+)(""[^<>=""\/]+?""\s*/?>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), "$1name=$2"),
            new RegexReplacement(new Regex(@"(<\s*ref\s+n)(me\s*=)", RegexOptions.Compiled | RegexOptions.IgnoreCase), "$1a$2"),
            new RegexReplacement(new Regex(@"(<\s*ref(?:\s+name\s*=[^<>]*?)?\s*>[^<>""]+?)<\s*ref\s*/\s*>", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), "$1</ref>"),
            new RegexReplacement(new Regex(@"(<\s*ref(?:\s+name\s*=[^<>]*?)?\s*>[^<>""]+?)<\s*/\s*red\s*>", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase), "$1</ref>"),
            new RegexReplacement(new Regex(@"(<\s*\/?\s*)(?:R[Ee][Ff]|r[Ee]F)(\s*(?:>|name\s*=))"), "$1ref$2"),
            new RegexReplacement(new Regex(@" +</ref>"), "</ref>"),
            new RegexReplacement(new Regex(@"(<ref[^<>\{\}\/]*>) +"), "$1"),
            new RegexReplacement(new Regex(@"<ref>\s*</ref>"), ""),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*=\s*""[^<>=""\/]+?)""([^<>=""\/]{2,}?)(?<!\s+)(?=\s*/?>)", RegexOptions.Compiled | RegexOptions.IgnoreCase), @"$1$2"""),
            new RegexReplacement(new Regex(@"(<\s*ref\s+name\s*)-", RegexOptions.Compiled), "$1="),
            new RegexReplacement(new Regex(@"<\s*ref\s+NAME(\s*=)", RegexOptions.Compiled), "<ref name$1"),
        };
        public static string FixReferenceTags(string articleText)
        {
            foreach (RegexReplacement rr in RefWhitespace)
                articleText = rr.Regex.Replace(articleText, rr.Replacement);
            return articleText;
        }
        private static readonly Regex OfBetweenMonthAndYear = new Regex(@"\b" + WikiRegexes.Months + @"\s+of\s+(20\d\d|1[89]\d\d)\b(?<!\b[Tt]he\s{1,5}\w{3,15}\s{1,5}of\s{1,5}(20\d\d|1[89]\d\d))", RegexOptions.Compiled);
        private static readonly Regex OrdinalsInDatesAm = new Regex(@"(?<!\b[1-3]\d +)\b" + WikiRegexes.Months + @"\s+([0-3]?\d)(?:st|nd|rd|th)\b(?<!\b[Tt]he\s+\w{3,10}\s+(?:[0-3]?\d)(?:st|nd|rd|th)\b)(?:(\s*(?:to|and|.|&.dash;)\s*[0-3]?\d)(?:st|nd|rd|th)\b)?", RegexOptions.Compiled);
        private static readonly Regex OrdinalsInDatesInt = new Regex(@"(?:\b([0-3]?\d)(?:st|nd|rd|th)(\s*(?:to|and|.|&.dash;)\s*))?\b([0-3]?\d)(?:st|nd|rd|th)\s+" + WikiRegexes.Months + @"\b(?<!\b[Tt]he\s+(?:[0-3]?\d)(?:st|nd|rd|th)\s+\w{3,10})", RegexOptions.Compiled);
        private static readonly Regex DateLeadingZerosAm = new Regex(@"(?<!\b[0-3]?\d\s*)\b" + WikiRegexes.Months + @"\s+0([1-9])" + @"\b", RegexOptions.Compiled);
        private static readonly Regex DateLeadingZerosInt = new Regex(@"\b" + @"0([1-9])\s+" + WikiRegexes.Months + @"\b", RegexOptions.Compiled);
        private static readonly Regex MonthsRegex = new Regex(@"\b" + WikiRegexes.MonthsNoGroup + @"\b", RegexOptions.Compiled);
        private static readonly Regex DayOfMonth = new Regex(@"(?<![Tt]he +)\b([1-9]|[12][0-9]|3[01])(?:st|nd|rd|th) +of +" + WikiRegexes.Months, RegexOptions.Compiled);
        public string FixDateOrdinalsAndOf(string articleText, string articleTitle)
        {
            if (Variables.LangCode != "en")
                return articleText;
            articleText = HideMoreText(articleText);
            articleText = OfBetweenMonthAndYear.Replace(articleText, "$1 $2");
            if (!MonthsRegex.IsMatch(articleTitle))
            {
                articleText = OrdinalsInDatesAm.Replace(articleText, "$1 $2$3");
                articleText = OrdinalsInDatesInt.Replace(articleText, "$1$2$3 $4");
                articleText = DayOfMonth.Replace(articleText, "$1 $2");
            }
            articleText = DateLeadingZerosAm.Replace(articleText, "$1 $2");
            articleText = DateLeadingZerosInt.Replace(articleText, "$1 $2");
            articleText = NoCommaAmericanDates.Replace(articleText, @"$1, $2");
            return AddBackMoreText(articleText);
        }
        private static readonly Regex BrTwoNewlines = new Regex("<br ?/?>\r\n\r\n", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static readonly Regex ThreeOrMoreNewlines = new Regex("\r\n(\r\n)+", RegexOptions.Compiled);
        private static readonly Regex TwoNewlinesInBlankSection = new Regex("== ? ?\r\n\r\n==", RegexOptions.Compiled);
        private static readonly Regex NewlinesBelowExternalLinks = new Regex(@"==External links==[\r\n\s]*\*", RegexOptions.Compiled);
        private static readonly Regex NewlinesBeforeUrl = new Regex(@"\r\n\r\n(\* ?\[?http)", RegexOptions.Compiled);
        private static readonly Regex HorizontalRule = new Regex("----+$", RegexOptions.Compiled);
        private static readonly Regex MultipleTabs = new Regex("  +", RegexOptions.Compiled);
        private static readonly Regex SpaceThenNewline = new Regex(" \r\n", RegexOptions.Compiled);
        private static readonly Regex WikiListWithMultipleSpaces = new Regex(@"^([\*#]+) +", RegexOptions.Compiled | RegexOptions.Multiline);
        private static readonly Regex WikiList = new Regex(@"^([\*#]+)", RegexOptions.Compiled | RegexOptions.Multiline);
        private static readonly Regex SpacedHeadings = new Regex("^(={1,4}) ?(.*?) ?(={1,4})$", RegexOptions.Compiled | RegexOptions.Multiline);
        private static readonly Regex SpacedDashes = new Regex(" (â|â|&#15[01];|&[nm]dash;|&#821[12];|&#x201[34];) ", RegexOptions.Compiled);
        public static string RemoveWhiteSpace(string articleText)
        {
            return RemoveWhiteSpace(articleText, true);
        }
        public static string RemoveWhiteSpace(string articleText, bool fixOptionalWhitespace)
        {
            articleText = BrTwoNewlines.Replace(articleText.Trim(), "\r\n\r\n");
            articleText = ThreeOrMoreNewlines.Replace(articleText, "\r\n\r\n");
            if (fixOptionalWhitespace)
                articleText = TwoNewlinesInBlankSection.Replace(articleText, "==\r\n==");
            articleText = NewlinesBelowExternalLinks.Replace(articleText, "==External links==\r\n*");
            articleText = NewlinesBeforeUrl.Replace(articleText, "\r\n$1");
            articleText = HorizontalRule.Replace(articleText.Trim(), "");
            if (articleText.Contains("\r\n|\r\n\r\n"))
                articleText = articleText.Replace("\r\n|\r\n\r\n", "\r\n|\r\n");
            if (articleText.Contains("\r\n\r\n|"))
                articleText = articleText.Replace("\r\n\r\n|", "\r\n|");
            return articleText.Trim();
        }
        public static string RemoveAllWhiteSpace(string articleText)
        {
            articleText = articleText.Replace("\t", " ");
            articleText = RemoveWhiteSpace(articleText);
            articleText = articleText.Replace("\r\n\r\n*", "\r\n*");
            articleText = MultipleTabs.Replace(articleText, " ");
            articleText = SpaceThenNewline.Replace(articleText, "\r\n");
            articleText = articleText.Replace("==\r\n\r\n", "==\r\n");
            articleText = NewlinesBelowExternalLinks.Replace(articleText, "==External links==\r\n*");
            articleText = WikiListWithMultipleSpaces.Replace(articleText, "$1");
            articleText = WikiList.Replace(articleText, "$1 ");
            articleText = SpacedHeadings.Replace(articleText, "$1$2$3");
            articleText = SpacedDashes.Replace(articleText, "$1");
            return articleText.Trim();
        }
        public static string FixSyntax(string articleText, out bool noChange)
        {
            string newText = FixSyntax(articleText);
            noChange = (newText.Equals(articleText));
            return newText;
        }
        private static readonly Regex DoubleBracketAtStartOfExternalLink = new Regex(@"\[(\[https?:/(?>[^\[\]]+|\[(?<DEPTH>)|\](?<-DEPTH>))*(?(DEPTH)(?!))\])", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex DoubleBracketAtEndOfExternalLink = new Regex(@"(\[https?:/(?>[^\[\]]+|\[(?<DEPTH>)|\](?<-DEPTH>))*(?(DEPTH)(?!))\])\](?!\])", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex DoubleBracketAtEndOfExternalLinkWithinImage = new Regex(@"(\[https?:/(?>[^\[\]]+|\[(?<DEPTH>)|\](?<-DEPTH>))*(?(DEPTH)(?!)))\](?=\]{3})", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private const string TemEnd = @"(\s*(?:\||\}\}))";
        private const string CitUrl = @"(\{\{\s*cit[ae][^{}]*?\|\s*url\s*=\s*)";
        private static readonly Regex BracketsAtBeginCiteTemplateURL = new Regex(CitUrl + @"\[+\s*((?:(?:ht|f)tp://)?[^\[\]<>""\s]+?\s*)\]?" + TemEnd, RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex BracketsAtEndCiteTemplateURL = new Regex(CitUrl + @"\[?\s*((?:(?:ht|f)tp://)?[^\[\]<>""\s]+?\s*)\]+" + TemEnd, RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexWikilinkMissingClosingBracket = new Regex(@"\[\[([^][]*?)\](?=[^\]]*?(?:$|\[|\n))", RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexWikilinkMissingOpeningBracket = new Regex(@"(?<=(?:^|\]|\n)[^\[]*?)\[([^][]*?)\]\](?!\])", RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexExternalLinkToImageURL = new Regex("\\[?\\[image:(http:\\/\\/.*?)\\]\\]?", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexSimpleWikilinkStartsWithSpaces = new Regex("\\[\\[ (.*)?\\]\\]", RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexSimpleWikilinkEndsWithSpaces = new Regex("\\[\\[([A-Za-z]*) \\]\\]", RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexSectionLinkUnnecessaryUnderscore = new Regex("\\[\\[(.*)?_#(.*)\\]\\]", RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexTemplate = new Regex(@"(\{\{\s*)[Tt]emplate\s*:(.*?\}\})", RegexOptions.Singleline | RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexListRowBrTag = new Regex(@"^([#\*:;]+.*?) *<[/\\]?br ?[/\\]?> *\r\n", RegexOptions.Multiline | RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexMultipleSpacesInWikilink = new Regex(@"(\[\[[^\[\]]+?) {2,}([^\[\]]+\]\])", RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexItalic = new Regex("< *i *>(.*?)< */ *i *>", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexBold = new Regex("< *b *>(.*?)< */ *b *>", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex SyntaxRemoveParagraphs = new Regex(@"(?<!^[!\|].*)</? ?[Pp]>", RegexOptions.Multiline | RegexOptions.Compiled);
        private static readonly Regex SyntaxRemoveBr = new Regex(@"(?<!^[!\|].*)(<br[\s/]*> *){2,}", RegexOptions.IgnoreCase | RegexOptions.Multiline | RegexOptions.Compiled);
        private static readonly Regex MultipleHttpInLink = new Regex(@"([\s\[>=])((?:ht|f)tp:?/+)(\2)+", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex PipedExternalLink = new Regex(@"(\[\w+://[^\]\[<>\""\s]*?\s*)(?: +\||\|([ ']))(?=[^\[\]\|]*\])", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex MissingColonInHttpLink = new Regex(@"([\s\[>=](?:ht|f))tp(?://?:?|:(?::+//)?)(\w+)", RegexOptions.Compiled);
        private static readonly Regex SingleTripleSlashInHttpLink = new Regex(@"([\s\[>=](?:ht|f))tp:(?:/|///)(\w+)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex CellpaddingTypo = new Regex(@"({\s*\|\s*class\s*=\s*""wikitable[^}]*?)cel(?:lpa|pad?)ding\b", RegexOptions.IgnoreCase | RegexOptions.Singleline);
        private static readonly Regex RemoveNoPropertyFontTags = new Regex(@"<font>([^<>]+)</font>", RegexOptions.IgnoreCase);
        private static readonly Regex RefTemplateIncorrectBracesAtEnd = new Regex(@"(?<=<ref(?:\s*name\s*=[^{}<>/]+?\s*)?>\s*)({{\s*[Cc]it[ae][^{}]+?)(?:}\]?|\)\))?(?=\s*</ref>)", RegexOptions.Compiled);
        private static readonly Regex RefExternalLinkUsingBraces = new Regex(@"(?<=<ref(?:\s*name\s*=[^{}<>]+?\s*)?>\s*){{(\s*https?://[^{}\s\r\n]+)(\s+[^{}]+\s*)?}}(\s*</ref>)", RegexOptions.Compiled);
        private static readonly Regex TemplateIncorrectBracesAtStart = new Regex(@"(?:{\[|\[{)([^{}\[\]]+}})", RegexOptions.Compiled);
        private static readonly Regex CitationTemplateSingleBraceAtStart = new Regex(@"(?<=[^{])({\s*[Cc]it[ae])", RegexOptions.Compiled);
        private static readonly Regex ReferenceTemplateQuadBracesAtEnd = new Regex(@"(?<=<ref(?:\s*name\s*=[^{}<>]+?\s*)?>\s*{{[^{}]+)}}(}}\s*</ref>)", RegexOptions.Compiled);
        private static readonly Regex CitationTemplateIncorrectBraceAtStart = new Regex(@"(?<=<ref(?:\s*name\s*=[^{}<>]+?\s*)?>){\[([Cc]it[ae])", RegexOptions.Compiled);
        private static readonly Regex CitationTemplateIncorrectBracesAtEnd = new Regex(@"(<ref(?:\s*name\s*=[^{}<>]+?\s*)?>\s*{{[Cc]it[ae][^{}]+?)(?:}\]|\]}|{})(?=\s*</ref>)", RegexOptions.Compiled);
        private static readonly Regex RefExternalLinkMissingStartBracket = new Regex(@"(?<=<ref(?:\s*name\s*=[^{}<>]+?\s*)?>[^{}\[\]<>]*?){?(https?://[^{}\[\]<>]+\][^{}\[\]<>]*</ref>)", RegexOptions.Compiled);
        private static readonly Regex RefExternalLinkMissingEndBracket = new Regex(@"(?<=<ref(?:\s*name\s*=[^{}<>]+?\s*)?>[^{}\[\]<>]*?\[\s*https?://[^{}\[\]<>]+)(</ref>)", RegexOptions.Compiled);
        private static readonly Regex RefCitationMissingOpeningBraces = new Regex(@"(?<=<\s*ref(?:\s+name\s*=[^<>]*?)?\s*>\s*)\(?\(?(?=[Cc]it[ae][^{}]+}}\s*</ref>)", RegexOptions.Compiled);
        private static readonly Regex BracesWithinDefaultsort = new Regex(@"(?<={{DEFAULTSORT[^{}\[\]]+)[\]\[]+}}", RegexOptions.Compiled);
        private static readonly Regex WordingIntoBareExternalLinks = new Regex(@"(?<=<ref(?:\s*name\s*=[^{}<>]+?\s*)?>\s*)([^<>{}\[\]\r\n]{3,70}?)[\.,::]?\s*\[\s*((?:[Hh]ttp|[Hh]ttps|[Ff]tp|[Mm]ailto)://[^\ \n\r<>]+)\s*\](?=\s*</ref>)", RegexOptions.Compiled);
        private static readonly Regex ExternalLinkWordSpacingBefore = new Regex(@"(\w)(?=\[(?:https?|ftp|mailto|irc|gopher|telnet|nntp|worldwind|news|svn)://.*?\])", RegexOptions.Compiled);
        private static readonly Regex ExternalLinkWordSpacingAfter = new Regex(@"(?<=\[(?:https?|ftp|mailto|irc|gopher|telnet|nntp|worldwind|news|svn)://[^\]\[<>]*?\])(\w)", RegexOptions.Compiled);
        private static readonly Regex WikilinkEndsBr = new Regex(@"<br[\s/]*>\]\]$", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static readonly Regex SquareBracketsInExternalLinks = new Regex(@"(\[https?://(?>[^\[\]<>]+|\[(?<DEPTH>)|\](?<-DEPTH>))*(?(DEPTH)(?!))\])", RegexOptions.Compiled);
        private static readonly Regex IncorrectBr = new Regex(@"< *br\. *>|<\\ *br *>|< *br *\\ *>", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexHorizontalRule = new Regex("^<hr>|^----+", RegexOptions.Compiled | RegexOptions.Multiline);
        private static readonly Regex SyntaxRegexHeadingWithHorizontalRule = new Regex("(^==?[^=]*==?)\r\n(\r\n)?----+", RegexOptions.Compiled | RegexOptions.Multiline);
        private static readonly Regex SyntaxRegexHTTPNumber = new Regex(@"HTTP/\d\.", RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexISBN = new Regex("ISBN: ?([0-9])", RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexExternalLinkOnWholeLine = new Regex(@"^\[(\s*http.*?)\]$", RegexOptions.Compiled | RegexOptions.Singleline);
        private static readonly Regex SyntaxRegexClosingBracket = new Regex(@"([^]])\]([^]]|$)", RegexOptions.Compiled);
        private static readonly Regex SyntaxRegexImageWithHTTP = new Regex("\\[\\[[Ii]mage:[^]]*http", RegexOptions.Compiled);
        private static readonly Regex DoublePipeInWikiLink = new Regex(@"(?<=\[\[[^\[\[\r\n\|{}]+)\|\|(?=[^\[\[\r\n\|{}]+\]\])", RegexOptions.Compiled);
        public static string FixSyntax(string articleText)
        {
            articleText = articleText.Replace(@"<small/>", @"</small>");
            articleText = SyntaxRegexItalic.Replace(articleText, "''$1''");
            articleText = SyntaxRegexBold.Replace(articleText, "'''$1'''");
            articleText = SyntaxRegexHorizontalRule.Replace(articleText, "----");
            articleText = SyntaxRegexHeadingWithHorizontalRule.Replace(articleText, "$1");
            articleText = DoublePipeInWikiLink.Replace(articleText, "|");
            articleText = SyntaxRegexTemplate.Replace(articleText, "$1$2");
            articleText = SyntaxRegexListRowBrTag.Replace(articleText, "$1\r\n");
            articleText = DoubleBracketAtStartOfExternalLink.Replace(articleText, "$1");
            articleText = DoubleBracketAtEndOfExternalLink.Replace(articleText, "$1");
            articleText = DoubleBracketAtEndOfExternalLinkWithinImage.Replace(articleText, "$1");
            articleText = BracketsAtBeginCiteTemplateURL.Replace(articleText, "$1$2$3");
            articleText = BracketsAtEndCiteTemplateURL.Replace(articleText, "$1$2$3");
            articleText = MultipleHttpInLink.Replace(articleText, "$1$2");
            articleText = PipedExternalLink.Replace(articleText, "$1 $2");
            articleText = SyntaxRegexExternalLinkToImageURL.Replace(articleText, "[$1]");
            articleText = SyntaxRegexSimpleWikilinkStartsWithSpaces.Replace(articleText, "[[$1]]");
            articleText = SyntaxRegexSimpleWikilinkEndsWithSpaces.Replace(articleText, "[[$1]]");
            articleText = SyntaxRegexSectionLinkUnnecessaryUnderscore.Replace(articleText, "[[$1#$2]]");
            articleText = SyntaxRegexMultipleSpacesInWikilink.Replace(articleText, @"$1 $2");
            if (!SyntaxRegexHTTPNumber.IsMatch(articleText))
            {
                articleText = MissingColonInHttpLink.Replace(articleText, "$1tp://$2");
                articleText = SingleTripleSlashInHttpLink.Replace(articleText, "$1tp://$2");
            }
            articleText = SyntaxRegexISBN.Replace(articleText, "ISBN $1");
            articleText = CellpaddingTypo.Replace(articleText, "$1cellpadding");
            articleText = RemoveNoPropertyFontTags.Replace(articleText, "$1");
            articleText = RefTemplateIncorrectBracesAtEnd.Replace(articleText, @"$1}}");
            articleText = RefExternalLinkUsingBraces.Replace(articleText, @"[$1$2]$3");
            articleText = TemplateIncorrectBracesAtStart.Replace(articleText, @"{{$1");
            articleText = CitationTemplateSingleBraceAtStart.Replace(articleText, @"{$1");
            articleText = ReferenceTemplateQuadBracesAtEnd.Replace(articleText, @"$1");
            articleText = CitationTemplateIncorrectBraceAtStart.Replace(articleText, @"{{$1");
            articleText = CitationTemplateIncorrectBracesAtEnd.Replace(articleText, @"$1}}");
            articleText = RefExternalLinkMissingStartBracket.Replace(articleText, @"[$1");
            articleText = RefExternalLinkMissingEndBracket.Replace(articleText, @"]$1");
            articleText = RefCitationMissingOpeningBraces.Replace(articleText, @"{{");
            articleText = BracesWithinDefaultsort.Replace(articleText, @"}}");
            foreach (Match m in SquareBracketsInExternalLinks.Matches(articleText))
            {
                string externalLink = SyntaxRegexExternalLinkOnWholeLine.Replace(m.Value, "$1");
                if (externalLink.Contains("]"))
                {
                    externalLink = SyntaxRegexClosingBracket.Replace(externalLink, @"$1&#93;$2");
                    articleText = articleText.Replace(m.Value, @"[" + externalLink + @"]");
                }
            }
            if (!SyntaxRegexImageWithHTTP.IsMatch(articleText))
            {
                articleText = SyntaxRegexWikilinkMissingClosingBracket.Replace(articleText, "[[$1]]");
                articleText = SyntaxRegexWikilinkMissingOpeningBracket.Replace(articleText, "[[$1]]");
            }
            articleText = FixUnbalancedBrackets(articleText);
            articleText = IncorrectBr.Replace(articleText, "<br />");
            articleText = FixSmallTags(articleText);
            articleText = WordingIntoBareExternalLinks.Replace(articleText, @"[$2 $1]");
            articleText = ExternalLinkWordSpacingBefore.Replace(articleText, "$1 ");
            articleText = ExternalLinkWordSpacingAfter.Replace(articleText, " $1");
            foreach (Match m in WikiRegexes.FileNamespaceLink.Matches(articleText))
            {
                if (WikilinkEndsBr.IsMatch(m.Value))
                    articleText = articleText.Replace(m.Value, WikilinkEndsBr.Replace(m.Value, @"]]"));
            }
            return articleText.Trim();
        }
        private static List<Regex> SmallTagRegexes = new List<Regex>();
        private static string FixSmallTags(string articleText)
        {
            if (!WikiRegexes.Small.IsMatch(articleText) || UnclosedTags(articleText).Count > 0)
                return articleText;
            foreach (Regex rx in SmallTagRegexes)
            {
                foreach (Match m in rx.Matches(articleText))
                {
                    Match s = WikiRegexes.Small.Match(m.Value);
                    if (s.Success)
                    {
                        if (s.Index > 0)
                            articleText = articleText.Replace(m.Value, WikiRegexes.Small.Replace(m.Value, "$1"));
                        else
                            articleText = articleText.Replace(m.Value, m.Value.Substring(0, 7) + WikiRegexes.Small.Replace(m.Value.Substring(7), "$1"));
                    }
                }
                if (!WikiRegexes.Small.IsMatch(articleText))
                    return articleText;
            }
            return articleText;
        }
        private static readonly Regex CurlyBraceInsteadOfPipeInWikiLink = new Regex(@"(?<=\[\[[^\[\]{}<>\r\n\|]{1,50})}(?=[^\[\]{}<>\r\n\|]{1,50}\]\])", RegexOptions.Compiled);
        private static readonly Regex CurlyBraceInsteadOfBracketClosing = new Regex(@"(?<=\([^{}<>\(\)]+[^{}<>\(\)\|])}(?=[^{}])", RegexOptions.Compiled);
        private static readonly Regex CurlyBraceInsteadOfSquareBracket = new Regex(@"(?<=\[[^{}<>\[\]]+[^{}<>\(\)\|])}(?=[^{}])", RegexOptions.Compiled);
        private static readonly Regex CurlyBraceInsteadOfBracketOpening = new Regex(@"(?<=[^{}<>]){(?=[^{}<>\(\)\|][^{}<>\(\)]+\)[^{}\(\)])", RegexOptions.Compiled);
        private static readonly Regex ExtraBracketOnWikilinkOpening = new Regex(@"(?<=[^\[\]{}<>])(?:{\[\[?|\[\[\[)(?=[^\[\]{}<>]+\]\])", RegexOptions.Compiled);
        private static readonly Regex ExtraBracketOnWikilinkOpening2 = new Regex(@"(?<=\[\[){(?=[^{}\[\]<>]+\]\])", RegexOptions.Compiled);
        private static readonly Regex ExternalLinkMissingClosing = new Regex(@"(?<=^ *\* *\[ *https?://[^<>{}\[\]\r\n\s]+[^\[\]\r\n]*)(\s$)", RegexOptions.Compiled | RegexOptions.Multiline);
        private static readonly Regex ExternalLinkMissingOpening = new Regex(@"(?<=^ *\*) *(?=https?://[^<>{}\[\]\r\n\s]+[^\[\]\r\n]*\]\s$)", RegexOptions.Compiled | RegexOptions.Multiline);
        private static readonly Regex TemplateIncorrectClosingBraces = new Regex(@"(?<={{[^{}<>]{1,400}[^{}<>\|])(?:\]}|}\]?)(?=[^{}])", RegexOptions.Compiled);
        private static readonly Regex TemplateMissingOpeningBrace = new Regex(@"(?<=[^{}<>\|]){(?=[^{}<>]{1,400}}})", RegexOptions.Compiled);
        private static readonly Regex QuadrupleCurlyBrackets = new Regex(@"(?<=^{{[^{}\r\n]+}})}}(\s)$", RegexOptions.Multiline | RegexOptions.Compiled);
        private static string FixUnbalancedBrackets(string articleText)
        {
            int bracketLength = 0;
            string articleTextTemp = articleText;
            int unbalancedBracket = UnbalancedBrackets(articleText, ref bracketLength);
            if (unbalancedBracket > -1)
            {
                int firstUnbalancedBracket = unbalancedBracket;
                char bracket = articleTextTemp[unbalancedBracket];
                if (bracketLength == 1 && unbalancedBracket > 2
                    && articleTextTemp[unbalancedBracket] == ']'
                    && articleTextTemp[unbalancedBracket - 1] == ']'
                    && articleTextTemp[unbalancedBracket - 2] == ']'
                   )
                {
                    articleTextTemp = articleTextTemp.Remove(unbalancedBracket, 1);
                }
                else if (bracketLength == 1)
                {
                    switch (bracket)
                    {
                        case '}':
                            articleTextTemp = CurlyBraceInsteadOfPipeInWikiLink.Replace(articleTextTemp, "|");
                            articleTextTemp = CurlyBraceInsteadOfBracketClosing.Replace(articleTextTemp, ")");
                            articleTextTemp = CurlyBraceInsteadOfSquareBracket.Replace(articleTextTemp, "]");
                            break;
                        case '{':
                            articleTextTemp = CurlyBraceInsteadOfBracketOpening.Replace(articleTextTemp, "(");
                            articleTextTemp = ExtraBracketOnWikilinkOpening2.Replace(articleTextTemp, "");
                            break;
                        case '(':
                            if (articleTextTemp.Length > (unbalancedBracket + 1)
                                && articleText[unbalancedBracket + 1] == '('
                               )
                            {
                                articleTextTemp = articleTextTemp.Remove(unbalancedBracket, 1);
                            }
                            break;
                        case '[':
                            articleTextTemp = ExternalLinkMissingClosing.Replace(articleTextTemp, "]$1");
                            break;
                        case ']':
                            articleTextTemp = ExternalLinkMissingOpening.Replace(articleTextTemp, " [");
                            break;
                        case '>':
                            articleTextTemp = articleTextTemp.Replace(@"<ref>>", @"<ref>");
                            break;
                        default:
                            articleTextTemp = articleTextTemp.Replace("ï¼", ")");
                            articleTextTemp = articleTextTemp.Replace("ï¼", "(");
                            break;
                    }
                    articleTextTemp = ExtraBracketOnWikilinkOpening.Replace(articleTextTemp, "[[");
                }
                if (bracketLength == 2)
                {
                    articleTextTemp = TemplateIncorrectClosingBraces.Replace(articleTextTemp, "}}");
                    articleTextTemp = TemplateMissingOpeningBrace.Replace(articleTextTemp, "{{");
                    if (articleTextTemp.Substring(unbalancedBracket, Math.Min(4, articleTextTemp.Length - unbalancedBracket)) == "[[[["
                        || articleTextTemp.Substring(Math.Max(0, unbalancedBracket - 2), Math.Min(4, articleTextTemp.Length - unbalancedBracket)) == "]]]]")
                    {
                        articleTextTemp = articleTextTemp.Remove(unbalancedBracket, 2);
                    }
                    articleTextTemp = articleTextTemp.Replace("{{" + Variables.Namespaces[Namespace.Category], "[[" + Variables.Namespaces[Namespace.Category]);
                    articleTextTemp = QuadrupleCurlyBrackets.Replace(articleTextTemp, "$1");
                    string defaultsort = WikiRegexes.Defaultsort.Match(articleTextTemp).Value;
                    if (!string.IsNullOrEmpty(defaultsort) && !defaultsort.Contains("}}"))
                        articleTextTemp = articleTextTemp.Replace(defaultsort.TrimEnd(), defaultsort.TrimEnd() + "}}");
                }
                unbalancedBracket = UnbalancedBrackets(articleTextTemp, ref bracketLength);
                if (unbalancedBracket < 0 || Math.Abs(unbalancedBracket - firstUnbalancedBracket) > 300)
                    articleText = articleTextTemp;
            }
            return articleText;
        }
        private static List<string> DateFields = new List<string>(new[] { "date", "accessdate", "archivedate", "airdate" });
        public static string PredominantDates(string articleText)
        {
            if (Variables.LangCode != "en")
                return articleText;
            DateLocale predominantLocale = DeterminePredominantDateLocale(articleText, true, true);
            if (predominantLocale.Equals(DateLocale.Undetermined))
                return articleText;
            foreach (Match m in WikiRegexes.CiteTemplate.Matches(articleText))
            {
                string newValue = m.Value;
                foreach (string field in DateFields)
                {
                    string fvalue = Tools.GetTemplateParameterValue(newValue, field);
                    if (WikiRegexes.ISODates.IsMatch(fvalue) || WikiRegexes.AmericanDates.IsMatch(fvalue) || WikiRegexes.InternationalDates.IsMatch(fvalue))
                        newValue = Tools.UpdateTemplateParameterValue(newValue, field, Tools.ISOToENDate(fvalue, predominantLocale));
                }
                if (!m.Value.Equals(newValue))
                    articleText = articleText.Replace(m.Value, newValue);
            }
            return articleText;
        }
        private static readonly Regex AccessdateTypo = new Regex(@"(\{\{\s*cit[^{}]*?\|\s*)ac(?:(?:ess?s?|cc?es|cess[es]|ccess)date|cessda[ry]e|c?essdat|cess(?:daste|ate)|cdessdate|cess?data|cesdsate|cessdaet|cessdatge|cesseddate|cessedon|cess-date)(\s*=\s*)", RegexOptions.IgnoreCase);
        private static readonly Regex PublisherTypo = new Regex(@"(?<={{\s*[Cc]it[ae][^{}\|]*?\|(?:[^{}]+\|)?\s*)(?:p(?:u[ns]|oub)lisher|publihser|pub(?:lication)?|pubslisher|puablisher|publicher|ublisher|publsiher|pusliher|pblisher|pubi?lsher|publishet|puiblisher|puplisher|publiisher|publiser|pulisher|publishser|pulbisher|publisber|publoisher|publishier|pubhlisher|publiaher|publicser|publicsher|publidsherr|publiher|publihsher|publilsher|publiosher|publisaher|publischer|publiseher|publisehr|publiserh|publisger|publishe?|publishey|publlisher|publusher|pubsliher)(\s*=)", RegexOptions.Compiled);
        private static readonly Regex AccessdateSynonyms = new Regex(@"(?<={{\s*[Cc]it[ae][^{}]*?\|\s*)(?:\s*date\s*)?(?:retrieved(?:\s+on)?|(?:last|date) *ac+essed|access\s+date)(?=\s*=\s*)", RegexOptions.Compiled);
        private static readonly Regex UppercaseCiteFields = new Regex(@"(\{\{(?:[Cc]ite\s*(?:web|book|news|journal|paper|press release|hansard|encyclopedia)|[Cc]itation)\b\s*[^{}]*\|\s*)(\w*?[A-Z]+\w*)(?<!(?:IS[BS]N|DOI|PMID))(\s*=\s*[^{}\|]{3,})", RegexOptions.Compiled);
        private static readonly Regex CiteUrl = new Regex(@"\|\s*url\s*=\s*([^\[\]<>""\s]+)", RegexOptions.Compiled);
        private static readonly Regex CiteUrlDomain = new Regex(@"\|\s*url\s*=\s*(?:ht|f)tps?://(?:www\.)?([^\[\]<>""\s/:]+)", RegexOptions.Compiled);
        private static readonly Regex CiteFormatFieldTypo = new Regex(@"(\{\{\s*[Cc]it[^{}]*?\|\s*)(?i)(?:fprmat)(\s*=\s*)", RegexOptions.Compiled);
        private static readonly Regex WorkInItalics = new Regex(@"(\|\s*work\s*=\s*)''([^'{}\|]+)''(?=\s*(?:\||}}))", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateFormatHTML = new Regex(@"\|\s*format\s*=\s*(?:HTML?|\[\[HTML?\]\]|html?)\s*(?=\||}})", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateFormatnull = new Regex(@"\|\s*format\s*=\s*(?=\||}})", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateLangEnglish = new Regex(@"\|\s*language\s*=\s*(?:[Ee]nglish)\s*(?=\||}})", RegexOptions.Compiled);
        private static readonly Regex CiteTemplatePagesPP = new Regex(@"(?<=\|\s*pages?\s*=\s*)p(?:p|gs?)?(?:\.|\b)(?:&nbsp;|\s*)(?=[^{}\|]+(?:\||}}))", RegexOptions.Compiled);
        private static readonly Regex CiteTemplateHTMLURL = new Regex(@"\|\s*url\s*=\s*[^<>{}\s\|]+?\.(?:HTML?|html?)\s*(?:\||}})", RegexOptions.Compiled);
        private static readonly Regex CiteTemplatesJournalVolume = new Regex(@"(?<=\|\s*volume\s*=\s*)vol(?:umes?|\.)?(?:&nbsp;|:)?", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static readonly Regex CiteTemplatesJournalVolumeAndIssue = new Regex(@"(?<=\|\s*volume\s*=\s*[0-9VXMILC]+?)(?:[;,]?\s*(?:no[\.:;]?|(?:numbers?|issue|iss)\s*[:;]?))", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static readonly Regex CiteTemplatesJournalIssue = new Regex(@"(?<=\|\s*issue\s*=\s*)(?:issues?|(?:nos?|iss)(?:[\.,;:]|\b)|numbers?[\.,;:]?)(?:&nbsp;)?", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static readonly Regex CiteTemplatesPageRange = new Regex(@"(?<=\|\s*pages?\s*=[^\|{}=/\\]*?)\b(\d+)\s*[-â]\s*(\d+)", RegexOptions.Compiled);
        private static readonly Regex CiteTemplatesPageRangeName = new Regex(@"(\|\s*)page(\s*=\s*\d+\s*â\s*\d)", RegexOptions.Compiled);
        private static readonly Regex AccessDateYear = new Regex(@"(?<=\|\s*accessdate\s*=\s*(?:[1-3]?\d\s+" + WikiRegexes.MonthsNoGroup + @"|\s*" + WikiRegexes.MonthsNoGroup + @"\s+[1-3]?\d))(\s*)\|\s*accessyear\s*=\s*(20[01]\d)\s*(\||}})", RegexOptions.Compiled);
        private static readonly Regex AccessDayMonthDay = new Regex(@"\|\s*access(?:daymonth|month(?:day)?|year)\s*=\s*(?=\||}})", RegexOptions.Compiled);
        private static readonly Regex DateLeadingZero = new Regex(@"(?<=\|\s*(?:access|archive)?date\s*=\s*)(?:0([1-9]\s+" + WikiRegexes.MonthsNoGroup + @")|(\s*" + WikiRegexes.MonthsNoGroup + @"\s)+0([1-9],?))(\s+20[01]\d)?(\s*\||}})", RegexOptions.Compiled);
        private static readonly Regex YearInDate = new Regex(@"(\|\s*)date(\s*=\s*[12]\d{3}\s*)(?=\||}})", RegexOptions.Compiled);
        private static readonly Regex DupeFields = new Regex(@"((\|\s*([a-z\d]+)\s*=\s*([^\{\}\|]*?))\s*(?:\|.*?)?)\|\s*\3\s*=\s*([^\{\}\|]*?)\s*(\||}})", RegexOptions.Singleline | RegexOptions.Compiled);
        public static string FixCitationTemplates(string articleText)
        {
            if (Variables.LangCode != "en")
                return articleText;
            articleText = AccessdateTypo.Replace(articleText, "$1accessdate$2");
            articleText = PublisherTypo.Replace(articleText, @"publisher$1");
            articleText = AccessdateSynonyms.Replace(articleText, "accessdate");
            int matchCount;
            int urlMatches;
            do
            {
                var matches = UppercaseCiteFields.Matches(articleText);
                matchCount = matches.Count;
                urlMatches = 0;
                foreach (Match m in matches)
                {
                    bool urlmatch = CiteUrl.Match(m.Value).Value.Contains(m.Groups[2].Value);
                    if (!urlmatch)
                        articleText = articleText.Replace(m.Value,
                                                          m.Groups[1].Value + m.Groups[2].Value.ToLower() +
                                                          m.Groups[3].Value);
                    else
                        urlMatches++;
                }
            } while (matchCount > 0 && matchCount != urlMatches);
            articleText = CiteFormatFieldTypo.Replace(articleText, "$1format$2");
            foreach (Match m in WikiRegexes.CiteTemplate.Matches(articleText))
            {
                string newValue = m.Value;
                string templatename = Tools.GetTemplateName(newValue);
                newValue = CiteTemplateFormatHTML.Replace(newValue, "");
                if (Variables.LangCode == "en")
                    newValue = CiteTemplateLangEnglish.Replace(newValue, "");
                if (CiteTemplateHTMLURL.IsMatch(newValue))
                    newValue = CiteTemplateFormatnull.Replace(newValue, "");
                newValue = WorkInItalics.Replace(newValue, "$1$2");
                if (Tools.GetTemplateParameterValue(newValue, "nopp").Length == 0 &&
                    !templatename.Equals("cite journal", StringComparison.OrdinalIgnoreCase))
                    newValue = CiteTemplatePagesPP.Replace(newValue, "");
                if (!Regex.IsMatch(newValue, @"{{\s*[Cc]ite (?:video|podcast)\b"))
                    newValue = YearInDate.Replace(newValue, "$1year$2");
                string TheYear = Tools.GetTemplateParameterValue(newValue, "year");
                if (WikiRegexes.ISODates.IsMatch(TheYear) || WikiRegexes.InternationalDates.IsMatch(TheYear)
                   || WikiRegexes.AmericanDates.IsMatch(TheYear))
                    newValue = Tools.RenameTemplateParameter(newValue, "year", "date");
                if (DupeFields.IsMatch(newValue))
                {
                    string URL = CiteUrl.Match(newValue).Value;
                    string newvaluetemp = newValue;
                    Match m2 = DupeFields.Match(newValue);
                    string val1 = m2.Groups[4].Value.Trim();
                    string val2 = m2.Groups[5].Value.Trim();
                    string firstfieldandvalue = m2.Groups[2].Value;
                    if (val2.Length == 0 || (val1.Length > 0 && val1.Contains(val2))
                        && !URL.Contains(firstfieldandvalue))
                        newvaluetemp = DupeFields.Replace(newValue, @"$1$6", 1);
                    else if (val1.Length == 0 || (val2.Length > 0 && val2.Contains(val1))
                             && !URL.Contains(firstfieldandvalue))
                        newvaluetemp = newValue.Remove(m2.Groups[2].Index, m2.Groups[2].Length);
                    if (newvaluetemp != newValue && (URL.Length == 0 || newvaluetemp.Contains(URL)))
                        newValue = newvaluetemp;
                }
                string year = Tools.GetTemplateParameterValue(newValue, "year");
                if (Regex.IsMatch(year, @"^[12]\d{3}$") && Tools.GetTemplateParameterValue(newValue, "date").Contains(year))
                    newValue = Tools.RemoveTemplateParameter(newValue, "year");
                if (templatename.Equals("cite journal", StringComparison.OrdinalIgnoreCase))
                {
                    newValue = CiteTemplatesJournalVolume.Replace(newValue, "");
                    newValue = CiteTemplatesJournalIssue.Replace(newValue, "");
                    if (Tools.GetTemplateParameterValue(newValue, "issue").Length == 0)
                        newValue = CiteTemplatesJournalVolumeAndIssue.Replace(newValue, @"| issue = ");
                }
                if (Regex.IsMatch(templatename, @"[Cc]ite ?web") && newValue.Contains("http:"))
                    newValue = Tools.RenameTemplate(newValue, templatename, "cite book");
                newValue = DateLeadingZero.Replace(newValue, @"$1$2$3$4$5");
                if (Regex.IsMatch(templatename, @"[Cc]ite(?: ?web| book| news)"))
                {
                    newValue = AccessDayMonthDay.Replace(newValue, "");
                    newValue = AccessDateYear.Replace(newValue, @" $2$1$3");
                }
                string accessyear = Tools.GetTemplateParameterValue(newValue, "accessyear");
                if (accessyear.Length > 0 && Tools.GetTemplateParameterValue(newValue, "accessdate").Contains(accessyear))
                    newValue = Tools.RemoveTemplateParameter(newValue, "accessyear");
                newValue = NoCommaAmericanDates.Replace(newValue, @"$1, $2");
                string page = Tools.GetTemplateParameterValue(newValue, "page");
                if (page.Length == 0)
                    page = Tools.GetTemplateParameterValue(newValue, "pages");
                bool pagerangesokay = true;
                Dictionary<int, int> PageRanges = new Dictionary<int, int>();
                if (page.Length > 2 && !page.Contains(" to "))
                {
                    foreach (Match pagerange in CiteTemplatesPageRange.Matches(newValue))
                    {
                        string page1 = pagerange.Groups[1].Value;
                        string page2 = pagerange.Groups[2].Value;
                        if (page1.Length > page2.Length)
                            page2 = page1.Substring(0, page1.Length - page2.Length) + page2;
                        if (Convert.ToInt32(page1) < Convert.ToInt32(page2) &&
                            Convert.ToInt32(page2) - Convert.ToInt32(page1) < 999)
                            pagerangesokay = true;
                        else
                            pagerangesokay = false;
                        foreach (KeyValuePair<int, int> kvp in PageRanges)
                        {
                            if ((Convert.ToInt32(page1) >= kvp.Key && Convert.ToInt32(page1) <= kvp.Value) || (Convert.ToInt32(page2) >= kvp.Key && Convert.ToInt32(page2) <= kvp.Value))
                            {
                                pagerangesokay = false;
                                break;
                            }
                        }
                        if (!pagerangesokay)
                            break;
                        PageRanges.Add(Convert.ToInt32(page1), Convert.ToInt32(page2));
                    }
                    if (pagerangesokay)
                        newValue = CiteTemplatesPageRange.Replace(newValue, @"$1â$2");
                }
                newValue = CiteTemplatesPageRangeName.Replace(newValue, @"$1pages$2");
                if (OrdinalsInDatesInt.IsMatch(newValue))
                {
                    newValue = Tools.UpdateTemplateParameterValue(newValue, "date", OrdinalsInDatesInt.Replace(Tools.GetTemplateParameterValue(newValue, "date"), "$1$2$3 $4"));
                    newValue = Tools.UpdateTemplateParameterValue(newValue, "accessdate", OrdinalsInDatesInt.Replace(Tools.GetTemplateParameterValue(newValue, "accessdate"), "$1$2$3 $4"));
                }
                if (OrdinalsInDatesAm.IsMatch(newValue))
                {
                    newValue = Tools.UpdateTemplateParameterValue(newValue, "date", OrdinalsInDatesAm.Replace(Tools.GetTemplateParameterValue(newValue, "date"), "$1 $2$3"));
                    newValue = Tools.UpdateTemplateParameterValue(newValue, "accessdate", OrdinalsInDatesAm.Replace(Tools.GetTemplateParameterValue(newValue, "accessdate"), "$1 $2$3"));
                }
                articleText = articleText.Replace(m.Value, newValue);
            }
            return articleText;
        }
        private static readonly Regex CiteWebOrNews = new Regex(@"[Cc]ite( ?web| news)", RegexOptions.Compiled);
        private static readonly Regex PressPublishers = new Regex(@"(Associated Press|United Press International)", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private static readonly List<string> WorkParameterAndAliases = new List<string>(new[] { "work", "newspaper", "journal", "periodical", "magazine" });
        public static string CitationPublisherToWork(string citation)
        {
            if (!CiteWebOrNews.IsMatch(Tools.GetTemplateName(citation)))
                return citation;
            string publisher = Tools.GetTemplateParameterValue(citation, "publisher");
            if (publisher.Length == 0 | PressPublishers.IsMatch(publisher))
                return citation;
            List<string> workandaliases = Tools.GetTemplateParametersValues(citation, WorkParameterAndAliases);
            if (string.Join("", workandaliases.ToArray()).Length == 0)
            {
                citation = Tools.RenameTemplateParameter(citation, "publisher", "work");
                citation = WorkInItalics.Replace(citation, "$1$2");
            }
            return citation;
        }
        public enum DateLocale { International, American, ISO, Undetermined };
        public static DateLocale DeterminePredominantDateLocale(string articleText)
        {
            return DeterminePredominantDateLocale(articleText, false, false);
        }
        public static DateLocale DeterminePredominantDateLocale(string articleText, bool considerISO)
        {
            return DeterminePredominantDateLocale(articleText, considerISO, false);
        }
        public static DateLocale DeterminePredominantDateLocale(string articleText, bool considerISO, bool explicitonly)
        {
            string DatesT = WikiRegexes.UseDatesTemplate.Match(articleText).Groups[2].Value.ToLower();
            DatesT = DatesT.Replace("iso", "ymd");
            DatesT = Regex.Match(DatesT, @"(ymd|dmy|mdy)").Value;
            if (Variables.LangCode == "en" && DatesT.Length > 0)
                switch (DatesT)
                {
                    case "dmy":
                        return DateLocale.International;
                    case "mdy":
                        return DateLocale.American;
                    case "ymd":
                        return DateLocale.ISO;
                }
            if (explicitonly)
                return DateLocale.Undetermined;
            int Americans = WikiRegexes.MonthDay.Matches(articleText).Count;
            int Internationals = WikiRegexes.DayMonth.Matches(articleText).Count;
            if (considerISO)
            {
                int ISOs = WikiRegexes.ISODates.Matches(articleText).Count;
                if (ISOs > Americans && ISOs > Internationals)
                    return DateLocale.ISO;
            }
            if (Americans == Internationals)
                return DateLocale.Undetermined;
            if (Americans == 0 && Internationals > 0 || (Internationals / Americans >= 2 && Internationals > 4))
                return DateLocale.International;
            if (Internationals == 0 && Americans > 0 || (Americans / Internationals >= 2 && Americans > 4))
                return DateLocale.American;
            return DateLocale.Undetermined;
        }
        public static string CanonicalizeTitle(string title)
        {
            if (!Tools.IsValidTitle(title) || title.Contains(":/"))
                return title;
            Variables.WaitForDelayedRequests();
            string s = CanonicalizeTitleRaw(title);
            if (Variables.UnderscoredTitles.Contains(Tools.TurnFirstToUpper(s)))
            {
                return HttpUtility.UrlDecode(title.Replace("+", "%2B"))
                    .Trim(new[] { '_' });
            }
            return s;
        }
        public static string CanonicalizeTitleAggressively(string title)
        {
            title = Tools.RemoveHashFromPageTitle(title);
            title = Tools.WikiDecode(title).Trim();
            title = Tools.TurnFirstToUpper(title);
            if (title.StartsWith(":"))
                title = title.Remove(0, 1).Trim();
            var pos = title.IndexOf(':');
            if (pos <= 0)
                return title;
            string titlePart = title.Substring(0, pos + 1);
            foreach (var regex in WikiRegexes.NamespacesCaseInsensitive)
            {
                var m = regex.Value.Match(titlePart);
                if (!m.Success || m.Index != 0)
                    continue;
                title = Variables.Namespaces[regex.Key] + Tools.TurnFirstToUpper(title.Substring(pos + 1).Trim());
                break;
            }
            return title;
        }
        private static readonly Regex SingleCurlyBrackets = new Regex(@"{((?>[^\{\}]+|\{(?<DEPTH>)|\}(?<-DEPTH>))*(?(DEPTH)(?!))})", RegexOptions.Compiled);
        private static readonly Regex DoubleSquareBrackets = new Regex(@"\[\[((?>[^\[\]]+|\[(?<DEPTH>)|\](?<-DEPTH>))*(?(DEPTH)(?!))\]\])", RegexOptions.Compiled);
        private static readonly Regex SingleSquareBrackets = new Regex(@"\[((?>[^\[\]]+|\[(?<DEPTH>)|\](?<-DEPTH>))*(?(DEPTH)(?!))\])", RegexOptions.Compiled);
        private static readonly Regex SingleRoundBrackets = new Regex(@"\(((?>[^\(\)]+|\((?<DEPTH>)|\)(?<-DEPTH>))*(?(DEPTH)(?!))\))", RegexOptions.Compiled);
        private static readonly Regex Tags = new Regex(@"\<((?>[^\<\>]+|\<(?<DEPTH>)|\>(?<-DEPTH>))*(?(DEPTH)(?!))\>)", RegexOptions.Compiled);
        private static readonly Regex HideNestedBrackets = new Regex(@"[^\[\]{}<>]\[[^\[\]{}<>]*?&#93;", RegexOptions.Compiled);
        private static readonly Regex AmountComparison = new Regex(@"[<>]\s*\d", RegexOptions.Compiled);
        public static int UnbalancedBrackets(string articleText, ref int bracketLength)
        {
            articleText = HideNestedBrackets.Replace(articleText, " ");
            articleText = Tools.ReplaceWithSpaces(articleText, WikiRegexes.MathPreSourceCodeComments);
            bracketLength = 2;
            int unbalancedfound = UnbalancedBrackets(articleText, "{{", "}}", WikiRegexes.NestedTemplates);
            if (unbalancedfound > -1)
                return unbalancedfound;
            unbalancedfound = UnbalancedBrackets(articleText, "[[", "]]", DoubleSquareBrackets);
            if (unbalancedfound > -1)
                return unbalancedfound;
            bracketLength = 1;
            unbalancedfound = UnbalancedBrackets(articleText, "{", "}", SingleCurlyBrackets);
            if (unbalancedfound > -1)
                return unbalancedfound;
            unbalancedfound = UnbalancedBrackets(articleText, "[", "]", SingleSquareBrackets);
            if (unbalancedfound > -1)
                return unbalancedfound;
            unbalancedfound = UnbalancedBrackets(articleText, "(", ")", SingleRoundBrackets);
            if (unbalancedfound > -1)
                return unbalancedfound;
            unbalancedfound = UnbalancedBrackets(articleText, "<", ">", Tags);
            if (unbalancedfound > -1)
                return unbalancedfound;
            return -1;
        }
        private static int UnbalancedBrackets(string articleText, string openingBrackets, string closingBrackets, Regex bracketsRegex)
        {
            if (openingBrackets == "[")
                articleText = Tools.ReplaceWithSpaces(articleText, DoubleSquareBrackets);
            if (openingBrackets == "{")
                articleText = Tools.ReplaceWithSpaces(articleText, WikiRegexes.NestedTemplates);
            articleText = Tools.ReplaceWithSpaces(articleText, bracketsRegex);
            int open = Regex.Matches(articleText, Regex.Escape(openingBrackets)).Count;
            int closed = Regex.Matches(articleText, Regex.Escape(closingBrackets)).Count;
            if (openingBrackets == "<" && AmountComparison.IsMatch(articleText))
                return -1;
            if (open == 0 && closed >= 1)
                return articleText.IndexOf(closingBrackets);
            if (open >= 1 && closed == 0)
                return articleText.IndexOf(openingBrackets);
            return -1;
        }
        private static bool IsHex(byte b)
        {
            return ((b >= '0' && b <= '9') || (b >= 'A' && b <= 'F'));
        }
        private static byte DecodeHex(byte a, byte b)
        {
            return byte.Parse(new string(new[] { (char)a, (char)b }), System.Globalization.NumberStyles.HexNumber);
        }
        private static readonly Regex LinkWhitespace1 = new Regex(@" \[\[ ([^\]]{1,30})\]\]", RegexOptions.Compiled);
        private static readonly Regex LinkWhitespace2 = new Regex(@"(?<=\w)\[\[ ([^\]]{1,30})\]\]", RegexOptions.Compiled);
        private static readonly Regex LinkWhitespace3 = new Regex(@"\[\[([^\]]{1,30}?) {2,10}([^\]]{1,30})\]\]", RegexOptions.Compiled);
        private static readonly Regex LinkWhitespace4 = new Regex(@"\[\[([^\]\|]{1,30}) \]\] ", RegexOptions.Compiled);
        private static readonly Regex LinkWhitespace5 = new Regex(@"\[\[([^\]]{1,30}) \]\](?=\w)", RegexOptions.Compiled);
        private static readonly Regex DateLinkWhitespace1 = new Regex(@"\b(\[\[\d\d? (?:January|February|March|April|May|June|July|August|September|October|November|December)\]\]),? {0,2}(\[\[\d{1,4}\]\])\b", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private static readonly Regex DateLinkWhitespace2 = new Regex(@"\b(\[\[(?:January|February|March|April|May|June|July|August|September|October|November|December) \d\d?\]\]),? {0,2}(\[\[\d{1,4}\]\])\b", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static string FixLinkWhitespace(string articleText, string articleTitle)
        {
            articleText = LinkWhitespace1.Replace(articleText, " [[$1]]");
            articleText = LinkWhitespace2.Replace(articleText, " [[$1]]");
            articleText = LinkWhitespace3.Replace(articleText, "[[$1 $2]]");
            articleText = LinkWhitespace4.Replace(articleText, "[[$1]] ");
            articleText = LinkWhitespace5.Replace(articleText, "[[$1]] ");
            articleText = DateLinkWhitespace1.Replace(articleText, "$1 $2");
            articleText = DateLinkWhitespace2.Replace(articleText, "$1 $2");
            if (articleTitle.Length > 0)
            {
                Regex sectionLinkWhitespace = new Regex(@"(\[\[" + Regex.Escape(articleTitle) + @"\#)\s+([^\[\]]+\]\])");
                return sectionLinkWhitespace.Replace(articleText, "$1$2");
            }
            return articleText;
        }
        [Obsolete]
        public static string FixLinkWhitespace(string articleText)
        {
            return FixLinkWhitespace(articleText, "");
        }
        public static string FixLinks(string articleText, string articleTitle, out bool noChange)
        {
            string articleTextAtStart = articleText;
            string escTitle = Regex.Escape(articleTitle);
            if (Regex.IsMatch(articleText, @"{{[Ii]nfobox (?:[Ss]ingle|[Aa]lbum)"))
                articleText = FixLinksInfoBoxSingleAlbum(articleText, articleTitle);
            if (!WikiRegexes.ImageMap.IsMatch(articleText)
                && !WikiRegexes.Noinclude.IsMatch(articleText)
                && !WikiRegexes.Includeonly.IsMatch(articleText))
            {
                articleText = Regex.Replace(articleText, @"\[\[\s*(" + Tools.CaseInsensitive(escTitle)
                                            + @")\s*\]\]", "$1");
                articleText = Regex.Replace(articleText, @"\[\[\s*" + Tools.CaseInsensitive(escTitle)
                                            + @"\s*\|\s*([^\]]+)\s*\]\]", "$1");
            }
            StringBuilder sb = new StringBuilder(articleText, (articleText.Length * 11) / 10);
            foreach (Match m in WikiRegexes.WikiLink.Matches(articleText))
            {
                string theTarget = m.Groups[1].Value;
                if (theTarget.Length > 0)
                {
                    string y = m.Value;
                    if(!theTarget.Contains("%27%27"))
                        y = m.Value.Replace(theTarget, CanonicalizeTitle(theTarget));
                    if (y != m.Value)
                        sb = sb.Replace(m.Value, y);
                }
            }
            noChange = (sb.ToString() == articleTextAtStart);
            return sb.ToString();
        }
        private static string FixLinksInfoBoxSingleAlbum(string articleText, string articleTitle)
        {
            string escTitle = Regex.Escape(articleTitle);
            string lowerTitle = Tools.TurnFirstToLower(escTitle);
            const string infoBoxSingleAlbum = @"(?s)(?<={{[Ii]nfobox (?:[Ss]ingle|[Aa]lbum).*?\|\s*[Tt]his (?:[Ss]ingle|[Aa]lbum)\s*=[^{}]*?)\[\[\s*";
            articleText = Regex.Replace(articleText, infoBoxSingleAlbum + escTitle + @"\s*\]\](?=[^{}\|]*(?:\||}}))", @"'''" + articleTitle + @"'''");
            articleText = Regex.Replace(articleText, infoBoxSingleAlbum + lowerTitle + @"\s*\]\](?=[^{}\|]*(?:\||}}))", @"'''" + lowerTitle + @"'''");
            articleText = Regex.Replace(articleText, infoBoxSingleAlbum + escTitle + @"\s*\|\s*([^\]]+)\s*\]\](?=[^{}\|]*(?:\||}}))", @"'''" + "$1" + @"'''");
            articleText = Regex.Replace(articleText, infoBoxSingleAlbum + lowerTitle + @"\s*\|\s*([^\]]+)\s*\]\](?=[^{}\|]*(?:\||}}))", @"'''" + "$1" + @"'''");
            return articleText;
        }
        public static string CanonicalizeTitleRaw(string title)
        {
            return CanonicalizeTitleRaw(title, true);
        }
        public static string CanonicalizeTitleRaw(string title, bool trim)
        {
            title = HttpUtility.UrlDecode(title.Replace("+", "%2B").Replace('_', ' '));
            return trim ? title.Trim() : title;
        }
        public static string SimplifyLinks(string articleText)
        {
            foreach (Match m in WikiRegexes.PipedWikiLink.Matches(articleText))
            {
                string n = m.Value;
                string a = m.Groups[1].Value.Trim();
                string b = (Namespace.Determine(a) != Namespace.Category)
                    ? m.Groups[2].Value.Trim()
                    : m.Groups[2].Value.TrimEnd(new[] { ' ' });
                if (b.Length == 0)
                    continue;
                if (a == b || Tools.TurnFirstToLower(a) == b)
                {
                    articleText = articleText.Replace(n, "[[" + b + "]]");
                }
                else if (Tools.TurnFirstToLower(b).StartsWith(Tools.TurnFirstToLower(a), StringComparison.Ordinal))
                {
                    bool doBreak = false;
                    foreach (char ch in b.Remove(0, a.Length))
                    {
                        if (!char.IsLower(ch))
                        {
                            doBreak = true;
                            break;
                        }
                    }
                    if (doBreak)
                        continue;
                    articleText = articleText.Replace(n, "[[" + b.Substring(0, a.Length) + "]]" + b.Substring(a.Length));
                }
                else
                {
                    string newlink = "[[" + a + "|" + b + "]]";
                    if (newlink != n)
                        articleText = articleText.Replace(n, newlink);
                }
            }
            return articleText;
        }
        public static string StickyLinks(string articleText)
        {
            foreach (Match m in WikiRegexes.PipedWikiLink.Matches(articleText))
            {
                string a = m.Groups[1].Value;
                string b = m.Groups[2].Value;
                if (b.Trim().Length == 0 || a.Contains(","))
                    continue;
                if (Tools.TurnFirstToLower(a).StartsWith(Tools.TurnFirstToLower(b), StringComparison.Ordinal))
                {
                    bool hasSpace = false;
                    if (a.Length > b.Length)
                        hasSpace = a[b.Length] == ' ';
                    string search = @"\[\[" + Regex.Escape(a) + @"\|" + Regex.Escape(b) +
                        @"\]\]" + (hasSpace ? "[ ]+" : "") + Regex.Escape(a.Remove(0,
                                                                                   b.Length + (hasSpace ? 1 : 0))) + @"\b";
                    a = a.Remove(0, 1).Insert(0, b[0] + "");
                    articleText = Regex.Replace(articleText, search, "[[" + a + @"]]");
                }
            }
            return articleText;
        }
        private static readonly Regex RegexMainArticle = new Regex(@"^:?'{0,5}Main article:\s?'{0,5}\[\[([^\|\[\]]*?)(\|([^\[\]]*?))?\]\]\.?'{0,5}\.?\s*?(?=($|[\r\n]))", RegexOptions.IgnoreCase | RegexOptions.Compiled | RegexOptions.Multiline);
        public static string FixMainArticle(string articleText)
        {
            return RegexMainArticle.Replace(articleText,
                                            m => m.Groups[2].Value.Length == 0
                                            ? "{{main|" + m.Groups[1].Value + "}}"
                                            : "{{main|" + m.Groups[1].Value + "|l1=" + m.Groups[3].Value + "}}");
        }
        public static string FixEmptyLinksAndTemplates(string articleText)
        {
            foreach (Match link in WikiRegexes.EmptyLink.Matches(articleText))
            {
                string trim = link.Groups[2].Value.Trim();
                if (string.IsNullOrEmpty(trim) || trim == "|" + Variables.NamespacesCaseInsensitive[Namespace.Image] ||
                    trim == "|" + Variables.NamespacesCaseInsensitive[Namespace.Category] || trim == "|")
                    articleText = articleText.Replace("[[" + link.Groups[1].Value + link.Groups[2].Value + "]]", "");
            }
            articleText = WikiRegexes.EmptyTemplate.Replace(articleText, "");
            return articleText;
        }
        public static string BulletExternalLinks(string articleText, out bool noChange)
        {
            string newText = BulletExternalLinks(articleText);
            noChange = (newText == articleText);
            return newText;
        }
        private static readonly HideText BulletExternalHider = new HideText(false, true, false);
        private static readonly Regex ExternalLinksSection = new Regex(@"=\s*(?:external)?\s*links\s*=", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.RightToLeft);
        private static readonly Regex NewlinesBeforeHTTP = new Regex("(\r\n|\n)?(\r\n|\n)(\\[?http)", RegexOptions.Compiled);
        public static string BulletExternalLinks(string articleText)
        {
            Match m = ExternalLinksSection.Match(articleText);
            if (!m.Success)
                return articleText;
            int intStart = m.Index;
            string articleTextSubstring = articleText.Substring(intStart);
            articleText = articleText.Substring(0, intStart);
            articleTextSubstring = BulletExternalHider.HideMore(articleTextSubstring);
            articleTextSubstring = NewlinesBeforeHTTP.Replace(articleTextSubstring, "$2* $3");
            return articleText + BulletExternalHider.AddBackMore(articleTextSubstring);
        }
        private static readonly Regex WordWhitespaceEndofline = new Regex(@"(\w+)\s+$", RegexOptions.Compiled);
        public static string FixCategories(string articleText)
        {
            string cat = "[[" + Variables.Namespaces[Namespace.Category];
            articleText = Regex.Replace(articleText, @"(?<=" + Regex.Escape(cat) + @"[^\r\n\[\]{}<>]+\]\])\]+", "");
            articleText = Regex.Replace(articleText, @"\[+(?=" + Regex.Escape(cat) + @"[^\r\n\[\]{}<>]+\]\])", "");
            foreach (Match m in WikiRegexes.LooseCategory.Matches(articleText))
            {
                if (!Tools.IsValidTitle(m.Groups[1].Value))
                    continue;
                string x = cat + Tools.TurnFirstToUpper(CanonicalizeTitleRaw(m.Groups[1].Value, false).Trim()) +
                    WordWhitespaceEndofline.Replace(Tools.RemoveDiacritics(m.Groups[2].Value), "$1") + "]]";
                if (x != m.Value)
                    articleText = articleText.Replace(m.Value, x);
            }
            return articleText;
        }
        [Obsolete]
        public static string FixCategories(string articleText, bool isMainSpace)
        {
            return FixCategories(articleText);
        }
        public static bool NoIncludeIncludeOnlyProgrammingElement(string articleText)
        {
            return WikiRegexes.Noinclude.IsMatch(articleText) || WikiRegexes.Includeonly.IsMatch(articleText) || Regex.IsMatch(articleText, @"{{{\d}}}");
        }
        public static string FixImages(string articleText)
        {
            foreach (Match m in WikiRegexes.LooseImage.Matches(articleText))
            {
                string imageName = m.Groups[2].Value;
                string x = "[[" + Namespace.Normalize(m.Groups[1].Value, 6) + (imageName.Contains("%27%27") ? imageName : CanonicalizeTitle(imageName).Trim()) + m.Groups[3].Value.Trim() + "]]";
                articleText = articleText.Replace(m.Value, x);
            }
            return articleText;
        }
        private static readonly Regex Temperature = new Regex(@"(?:&deg;|&ordm;|Âº|Â°)(?:&nbsp;)?\s*([CcFf])(?![A-Za-z])", RegexOptions.Compiled);
        public static string FixTemperatures(string articleText)
        {
            foreach (Match m in Temperature.Matches(articleText))
                articleText = articleText.Replace(m.ToString(), "Â°" + m.Groups[1].Value.ToUpper());
            return articleText;
        }
        public string FixNonBreakingSpaces(string articleText)
        {
            articleText = HideMoreText(articleText);
            articleText = WikiRegexes.UnitsWithoutNonBreakingSpaces.Replace(articleText, "$1&nbsp;$2");
            articleText = WikiRegexes.ImperialUnitsInBracketsWithoutNonBreakingSpaces.Replace(articleText, "$1&nbsp;$2");
            articleText = WikiRegexes.MetresFeetConversionNonBreakingSpaces.Replace(articleText, @"$1&nbsp;m");
            articleText = Regex.Replace(articleText, @"(\b[Pp]?p\.) *(?=[\dIVXCL])", @"$1&nbsp;");
            return AddBackMoreText(articleText);
        }
        private static string ExtractTemplate(string articleText, Match m)
        {
            Regex theTemplate = new Regex(Regex.Escape(m.Groups[1].Value) + @"(?>[^\{\}]+|\{(?<DEPTH>)|\}(?<-DEPTH>))*(?(DEPTH)(?!))}}");
            foreach (Match n in theTemplate.Matches(articleText))
            {
                if (n.Index == m.Index)
                    return theTemplate.Match(articleText).Value;
            }
            return "";
        }
        public static string GetTemplate(string articleText, string template)
        {
            Regex search = new Regex(@"(\{\{\s*" + Tools.CaseInsensitive(template) + @"\s*)(?:\||\}|<)", RegexOptions.Singleline);
            string articleTextCleaned = WikiRegexes.UnformattedText.Replace(articleText, "");
            if (search.IsMatch(articleTextCleaned))
            {
                Match m = search.Match(articleText);
                return m.Success ? ExtractTemplate(articleText, m) : "";
            }
            return "";
        }
        public static List<Match> GetTemplates(string articleText, string template)
        {
            return GetTemplates(articleText, Tools.NestedTemplateRegex(template));
        }
        public static List<Match> GetTemplates(string articleText)
        {
            return GetTemplates(articleText, WikiRegexes.NestedTemplates);
        }
        private static List<Match> GetTemplates(string articleText, Regex search)
        {
            List<Match> templateMatches = new List<Match>();
            string articleTextAtStart = articleText;
            articleText = Tools.ReplaceWithSpaces(articleText, WikiRegexes.UnformattedText);
            foreach (Match m in search.Matches(articleText))
            {
                foreach (Match m2 in search.Matches(articleTextAtStart))
                {
                    if (m2.Index.Equals(m.Index))
                    {
                        templateMatches.Add(m2);
                        break;
                    }
                }
            }
            return templateMatches;
        }
        public static string GetTemplateName(string call)
        {
            return WikiRegexes.TemplateCall.Match(call).Groups[1].Value;
        }
        public static string GetTemplateName(string setting, bool fromSetting)
        {
            if (fromSetting)
            {
                setting = setting.Trim();
                if (string.IsNullOrEmpty(setting))
                    return "";
                string gtn = GetTemplateName(setting).Trim();
                return string.IsNullOrEmpty(gtn) ? setting : gtn;
            }
            return GetTemplateName(setting);
        }
        public static string RemoveEmptyComments(string articleText)
        {
            return WikiRegexes.EmptyComments.Replace(articleText, "");
        }
        public string FixUnicode(string articleText)
        {
            articleText = articleText.Replace('\x2029', ' ');
            return articleText.Replace('\x2028', ' ');
        }
        public string Unicodify(string articleText, out bool noChange)
        {
            string newText = Unicodify(articleText);
            noChange = (newText == articleText);
            return newText;
        }
        private static readonly Regex NDash = new Regex("&#150;|&#8211;|&#x2013;", RegexOptions.Compiled);
        private static readonly Regex MDash = new Regex("&#151;|&#8212;|&#x2014;", RegexOptions.Compiled);
        public string Unicodify(string articleText)
        {
            if (Regex.IsMatch(articleText, "<[Mm]ath>"))
                return articleText;
            articleText = NDash.Replace(articleText, "&ndash;");
            articleText = MDash.Replace(articleText, "&mdash;");
            articleText = articleText.Replace(" &amp; ", " & ");
            articleText = articleText.Replace("&amp;", "&amp;amp;");
            articleText = articleText.Replace("&#153;", "â¢");
            articleText = articleText.Replace("&#149;", "â¢");
            foreach (KeyValuePair<Regex, string> k in RegexUnicode)
            {
                articleText = k.Key.Replace(articleText, k.Value);
            }
            try
            {
                articleText = HttpUtility.HtmlDecode(articleText);
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
            }
            return articleText;
        }
        private static string BoldedSelfLinks(string articleTitle, string articleText)
        {
            string escTitle = Regex.Escape(articleTitle);
            Regex r1 = new Regex(@"'''\[\[\s*" + escTitle + @"\s*\]\]'''");
            Regex r2 = new Regex(@"'''\[\[\s*" + Tools.TurnFirstToLower(escTitle) + @"\s*\]\]'''");
            if (!WikiRegexes.Noinclude.IsMatch(articleText) && !WikiRegexes.Includeonly.IsMatch(articleText))
            {
                articleText = r1.Replace(articleText, @"'''" + articleTitle + @"'''");
                articleText = r2.Replace(articleText, @"'''" + Tools.TurnFirstToLower(articleTitle) + @"'''");
            }
            return articleText;
        }
        private static readonly Regex BracketedAtEndOfLine = new Regex(@" \(.*?\)$", RegexOptions.Compiled);
        private static readonly Regex BoldTitleAlready3 = new Regex(@"^\s*({{[^\{\}]+}}\s*)*'''('')?\s*\w", RegexOptions.Compiled);
        public string BoldTitle(string articleText, string articleTitle, out bool noChange)
        {
            HideText Hider2 = new HideText();
            HideText Hider3 = new HideText(true, true, true);
            articleText = BoldedSelfLinks(articleTitle, articleText);
            noChange = true;
            string escTitle = Regex.Escape(articleTitle);
            string escTitleNoBrackets = Regex.Escape(BracketedAtEndOfLine.Replace(articleTitle, ""));
            string articleTextAtStart = articleText;
            string zerothSection = WikiRegexes.ZerothSection.Match(articleText).Value;
            string restOfArticle = articleText.Remove(0, zerothSection.Length);
            string zerothSectionHidden = Hider2.HideMore(zerothSection, false, false);
            string zerothSectionHiddenOriginal = zerothSectionHidden;
            Regex r1 = new Regex(@"\[\[\s*" + escTitle + @"\s*\]\]");
            Regex r2 = new Regex(@"\[\[\s*" + Tools.TurnFirstToLower(escTitle) + @"\s*\]\]");
            if (!Regex.IsMatch(zerothSection, "'''" + escTitle + "'''") && !WikiRegexes.Noinclude.IsMatch(articleText) && !WikiRegexes.Includeonly.IsMatch(articleText))
                zerothSectionHidden = r1.Replace(zerothSectionHidden, "'''" + articleTitle + @"'''");
            if (zerothSectionHiddenOriginal == zerothSectionHidden && !Regex.IsMatch(zerothSection, @"'''" + Tools.TurnFirstToLower(escTitle) + @"'''"))
                zerothSectionHidden = r2.Replace(zerothSectionHidden, "'''" + Tools.TurnFirstToLower(articleTitle) + @"'''");
            zerothSection = Hider2.AddBackMore(zerothSectionHidden);
            if (zerothSectionHiddenOriginal != zerothSectionHidden)
            {
                noChange = false;
                return (zerothSection + restOfArticle);
            }
            if (WikiRegexes.Dates2.IsMatch(articleTitle) || WikiRegexes.Dates.IsMatch(articleTitle))
                return articleTextAtStart;
            Regex boldTitleAlready1 = new Regex(@"'''\s*(" + escTitle + "|" + Tools.TurnFirstToLower(escTitle) + @")\s*'''");
            Regex boldTitleAlready2 = new Regex(@"'''\s*(" + escTitleNoBrackets + "|" + Tools.TurnFirstToLower(escTitleNoBrackets) + @")\s*'''");
            if (boldTitleAlready1.IsMatch(articleText) || boldTitleAlready2.IsMatch(articleText)
                || BoldTitleAlready3.IsMatch(articleText))
                return articleTextAtStart;
            string articleTextHidden = Hider3.HideMore(articleText);
            int fivepc = articleTextHidden.Length / 20;
            if (articleTextHidden.Substring(0, fivepc).Contains("'''"))
            {
                return articleTextAtStart;
            }
            Regex regexBoldNoBrackets = new Regex(@"([^\[]|^)(" + escTitleNoBrackets + "|" + Tools.TurnFirstToLower(escTitleNoBrackets) + ")([ ,.:;])");
            if (regexBoldNoBrackets.IsMatch(articleTextHidden))
                articleTextHidden = regexBoldNoBrackets.Replace(articleTextHidden, "$1'''$2'''$3", 1);
            articleText = Hider3.AddBackMore(articleTextHidden);
            if (AddedBoldIsValid(articleText, escTitleNoBrackets))
            {
                noChange = false;
                return articleText;
            }
            return articleTextAtStart;
        }
        private static readonly Regex RegexFirstBold = new Regex(@"^(.*?)'''", RegexOptions.Singleline | RegexOptions.Compiled);
        private bool AddedBoldIsValid(string articleText, string escapedTitle)
        {
            HideText Hider2 = new HideText(true, true, true);
            string articletextoriginal = articleText;
            Regex regexBoldAdded = new Regex(@"^(.*?)'''" + escapedTitle, RegexOptions.Singleline);
            int boldAddedPos = regexBoldAdded.Match(articleText).Length - Regex.Unescape(escapedTitle).Length;
            int firstBoldPos = RegexFirstBold.Match(articleText).Length;
            articleText = Hider2.HideMore(articleText);
            bool inFirst5Percent = articleText.Substring(0, articleText.Length / 20).Contains("'''");
            if (inFirst5Percent && boldAddedPos <= firstBoldPos)
                return true;
            Regex boldAfterInfobox = new Regex(WikiRegexes.InfoBox + @"\s*'''" + escapedTitle);
            return boldAfterInfobox.IsMatch(articletextoriginal);
        }
        public static string ReplaceImage(string oldImage, string newImage, string articleText, out bool noChange)
        {
            string newText = ReplaceImage(oldImage, newImage, articleText);
            noChange = (newText == articleText);
            return newText;
        }
        public static string ReplaceImage(string oldImage, string newImage, string articleText)
        {
            articleText = FixImages(articleText);
            oldImage = Tools.WikiDecode(Regex.Replace(oldImage, "^" + Variables.Namespaces[Namespace.File], "", RegexOptions.IgnoreCase));
            newImage = Tools.WikiDecode(Regex.Replace(newImage, "^" + Variables.Namespaces[Namespace.File], "", RegexOptions.IgnoreCase));
            oldImage = Regex.Escape(oldImage).Replace("\\ ", "[ _]");
            oldImage = "((?i:" + WikiRegexes.GenerateNamespaceRegex(Namespace.File, Namespace.Media)
                + @"))\s*:\s*" + Tools.CaseInsensitive(oldImage);
            newImage = "$1:" + newImage;
            return Regex.Replace(articleText, oldImage, newImage);
        }
        public static string RemoveImage(string image, string articleText, bool commentOut, string comment)
        {
            image = Tools.WikiDecode(Regex.Replace(image, "^"
                                                   + Variables.NamespacesCaseInsensitive[Namespace.File], "", RegexOptions.IgnoreCase));
            image = Tools.CaseInsensitive(HttpUtility.UrlDecode(Regex.Escape(image).Replace("\\ ", "[ _]")));
            articleText = FixImages(articleText);
            Regex r = new Regex(@"\[\[\s*:?\s*(?i:"
                                + WikiRegexes.GenerateNamespaceRegex(Namespace.File, Namespace.Media)
                                + @")\s*:\s*" + image + @".*\]\]", RegexOptions.Singleline);
            MatchCollection n = r.Matches(articleText);
            if (n.Count > 0)
            {
                foreach (Match m in n)
                {
                    string match = m.Value;
                    int i = 0;
                    int j = 0;
                    foreach (char c in match)
                    {
                        if (c == '[')
                            j++;
                        else if (c == ']')
                            j--;
                        i++;
                        if (j == 0)
                        {
                            if (match.Length > i)
                                match = match.Remove(i);
                            Regex t = new Regex(Regex.Escape(match));
                            articleText = commentOut
                                ? t.Replace(articleText, "<!-- " + comment + " " + match + " -->", 1, m.Index)
                                : t.Replace(articleText, "", 1);
                            break;
                        }
                    }
                }
            }
            else
            {
                r = new Regex("(" + Variables.NamespacesCaseInsensitive[Namespace.File] + ")?" + image);
                n = r.Matches(articleText);
                foreach (Match m in n)
                {
                    Regex t = new Regex(Regex.Escape(m.Value));
                    articleText = commentOut
                        ? t.Replace(articleText, "<!-- " + comment + " $0 -->", 1, m.Index)
                        : t.Replace(articleText, "", 1, m.Index);
                }
            }
            return articleText;
        }
        public static string RemoveImage(string image, string articleText, bool commentOut, string comment, out bool noChange)
        {
            string newText = RemoveImage(image, articleText, commentOut, comment);
            noChange = (newText == articleText);
            return newText;
        }
        public string AddCategory(string newCategory, string articleText, string articleTitle, out bool noChange)
        {
            string newText = AddCategory(newCategory, articleText, articleTitle);
            noChange = (newText == articleText);
            return newText;
        }
        public string AddCategory(string newCategory, string articleText, string articleTitle)
        {
            string oldText = articleText;
            articleText = FixCategories(articleText);
            if (Regex.IsMatch(articleText, @"\[\["
                              + Variables.NamespacesCaseInsensitive[Namespace.Category]
                              + Regex.Escape(newCategory) + @"[\|\]]"))
            {
                return oldText;
            }
            string cat = Tools.Newline("[[" + Variables.Namespaces[Namespace.Category] + newCategory + "]]");
            cat = Tools.ApplyKeyWords(articleTitle, cat);
            if (Namespace.Determine(articleTitle) == Namespace.Template)
                articleText += "<noinclude>" + cat + Tools.Newline("</noinclude>");
            else
                articleText += cat;
            return SortMetaData(articleText, articleTitle, false);
        }
        public static string ReCategoriser(string oldCategory, string newCategory, string articleText, out bool noChange)
        {
            return ReCategoriser(oldCategory, newCategory, articleText, out noChange, false);
        }
        public static string ReCategoriser(string oldCategory, string newCategory, string articleText, out bool noChange, bool removeSortKey)
        {
            oldCategory = Regex.Replace(oldCategory, "^"
                                        + Variables.NamespacesCaseInsensitive[Namespace.Category], "", RegexOptions.IgnoreCase);
            newCategory = Regex.Replace(newCategory, "^"
                                        + Variables.NamespacesCaseInsensitive[Namespace.Category], "", RegexOptions.IgnoreCase);
            articleText = FixCategories(articleText);
            string testText = articleText;
            if (Regex.IsMatch(articleText, "\\[\\["
                              + Variables.NamespacesCaseInsensitive[Namespace.Category]
                              + Tools.CaseInsensitive(Regex.Escape(newCategory)) + @"\s*(\||\]\])"))
            {
                bool tmp;
                articleText = RemoveCategory(oldCategory, articleText, out tmp);
            }
            else
            {
                oldCategory = Regex.Escape(oldCategory);
                oldCategory = Tools.CaseInsensitive(oldCategory);
                oldCategory = Variables.Namespaces[Namespace.Category] + oldCategory + @"\s*(\|[^\|\[\]]+\]\]|\]\])";
                if (!removeSortKey)
                    newCategory = Variables.Namespaces[Namespace.Category] + newCategory + "$1";
                else
                    newCategory = Variables.Namespaces[Namespace.Category] + newCategory + @"]]";
                articleText = Regex.Replace(articleText, oldCategory, newCategory);
            }
            noChange = (testText == articleText);
            return articleText;
        }
        public static string RemoveCategory(string strOldCat, string articleText, out bool noChange)
        {
            articleText = FixCategories(articleText);
            string testText = articleText;
            articleText = RemoveCategory(strOldCat, articleText);
            noChange = (testText == articleText);
            return articleText;
        }
        public static string RemoveCategory(string strOldCat, string articleText)
        {
            strOldCat = Tools.CaseInsensitive(Regex.Escape(strOldCat));
            if (!articleText.Contains("<includeonly>"))
                articleText = Regex.Replace(articleText, "\\[\\["
                                            + Variables.NamespacesCaseInsensitive[Namespace.Category] + " ?"
                                            + strOldCat + "( ?\\]\\]| ?\\|[^\\|]*?\\]\\])\r\n", "");
            articleText = Regex.Replace(articleText, "\\[\\["
                                        + Variables.NamespacesCaseInsensitive[Namespace.Category] + " ?"
                                        + strOldCat + "( ?\\]\\]| ?\\|[^\\|]*?\\]\\])", "");
            return articleText;
        }
        public static bool CategoryMatch(string articleText, string categoryName)
        {
            Regex anyCategory = new Regex(@"\[\[\s*" + Variables.NamespacesCaseInsensitive[Namespace.Category] +
                                          @"\s*" + Regex.Escape(categoryName) + @"\s*(?:|\|([^\|\]]*))\s*\]\]", RegexOptions.IgnoreCase);
            return anyCategory.IsMatch(articleText);
        }
        public static string ChangeToDefaultSort(string articleText, string articleTitle, out bool noChange)
        {
            return ChangeToDefaultSort(articleText, articleTitle, out noChange, false);
        }
        public static string GetCategorySort(string articleText)
        {
            if (WikiRegexes.Defaultsort.Matches(articleText).Count == 1)
                return "";
            int matches;
            const string dummy = @"@@@@";
            string sort = GetCategorySort(articleText, dummy, out matches);
            return sort == dummy ? "" : sort;
        }
        public static string GetCategorySort(string articleText, string articleTitle, out int matches)
        {
            string sort = "";
            bool allsame = true;
            matches = 0;
            foreach (Match m in WikiRegexes.Category.Matches(articleText))
            {
                string explicitKey = m.Groups[2].Value;
                if (explicitKey.Length == 0)
                    explicitKey = articleTitle;
                if (string.IsNullOrEmpty(sort))
                    sort = explicitKey;
                if (sort != explicitKey && !String.IsNullOrEmpty(explicitKey))
                {
                    allsame = false;
                    break;
                }
                matches++;
            }
            if (allsame && matches > 0)
                return sort;
            return "";
        }
        public static string ChangeToDefaultSort(string articleText, string articleTitle, out bool noChange, bool restrictDefaultsortChanges)
        {
            string testText = articleText;
            noChange = true;
            int matches;
            if (!Namespace.IsMainSpace(articleTitle))
                articleTitle = Tools.RemoveNamespaceString(articleTitle);
            string sort = GetCategorySort(articleText, articleTitle, out matches);
            MatchCollection ds = WikiRegexes.Defaultsort.Matches(articleText);
            if (ds.Count > 1 || (ds.Count == 1 && !ds[0].Value.ToUpper().Contains("DEFAULTSORT")))
            {
                bool allsame2 = false;
                string lastvalue = "";
                foreach (Match m in WikiRegexes.Defaultsort.Matches(articleText))
                {
                    if (lastvalue.Length == 0)
                    {
                        lastvalue = m.Value;
                        allsame2 = true;
                    }
                    else
                        allsame2 = (m.Value == lastvalue);
                }
                if (allsame2)
                    articleText = WikiRegexes.Defaultsort.Replace(articleText, "", ds.Count - 1);
                else
                    return articleText;
            }
            articleText = TalkPages.TalkPageHeaders.FormatDefaultSort(articleText);
            ds = WikiRegexes.Defaultsort.Matches(articleText);
            articleText = FixCategories(articleText);
            if (ds.Count == 0 && !restrictDefaultsortChanges)
            {
                if (sort.Length > 4 && matches > 1 && !sort.StartsWith(" "))
                {
                    articleText = WikiRegexes.Category.Replace(articleText, "[["
                                                               + Variables.Namespaces[Namespace.Category] + "$1]]");
                    if (sort != articleTitle && Tools.FixupDefaultSort(sort) != articleTitle)
                        articleText += Tools.Newline("{{DEFAULTSORT:") + Tools.FixupDefaultSort(sort) + "}}";
                }
                articleText = DefaultsortTitlesWithDiacritics(articleText, articleTitle, matches, IsArticleAboutAPerson(articleText, articleTitle, true));
            }
            else if (ds.Count == 1)
            {
                string s = Tools.FixupDefaultSort(ds[0].Groups[1].Value).Trim();
                if (s != ds[0].Groups[1].Value && s.Length > 0 && !restrictDefaultsortChanges)
                    articleText = articleText.Replace(ds[0].Value, "{{DEFAULTSORT:" + s + "}}");
                ds = WikiRegexes.Defaultsort.Matches(articleText);
                string defaultsortKey = ds[0].Groups["key"].Value;
                articleText = ExplicitCategorySortkeys(articleText, defaultsortKey);
            }
            noChange = (testText == articleText);
            return articleText;
        }
        private static string ExplicitCategorySortkeys(string articleText, string defaultsortKey)
        {
            foreach (Match m in WikiRegexes.Category.Matches(articleText))
            {
                string explicitKey = m.Groups[2].Value;
                if (explicitKey.Length == 0)
                    continue;
                if (string.Compare(explicitKey, defaultsortKey, StringComparison.OrdinalIgnoreCase) == 0
                    || defaultsortKey.StartsWith(explicitKey))
                {
                    articleText = articleText.Replace(m.Value,
                                                      "[[" + Variables.Namespaces[Namespace.Category] + m.Groups[1].Value + "]]");
                }
            }
            return (articleText);
        }
        private static string DefaultsortTitlesWithDiacritics(string articleText, string articleTitle, int matches, bool articleAboutAPerson)
        {
            if (((Tools.FixupDefaultSort(articleTitle) != articleTitle && !articleAboutAPerson) ||
                 (Tools.MakeHumanCatKey(articleTitle) != articleTitle && articleAboutAPerson))
                && matches > 0 && !WikiRegexes.Defaultsort.IsMatch(articleText))
            {
                string sortkey = articleAboutAPerson
                    ? Tools.MakeHumanCatKey(articleTitle)
                    : Tools.FixupDefaultSort(articleTitle);
                articleText += Tools.Newline("{{DEFAULTSORT:") + sortkey + "}}";
                return (ExplicitCategorySortkeys(articleText, sortkey));
            }
            return articleText;
        }
        private static readonly Regex InUniverse = new Regex(@"{{[Ii]n-universe", RegexOptions.Compiled);
        private static readonly Regex CategoryCharacters = new Regex(@"\[\[Category:[^\[\]]*?[Cc]haracters", RegexOptions.Compiled);
        private static readonly Regex SeeAlsoOrMain = new Regex(@"{{(?:[Ss]ee\salso|[Mm]ain)\b", RegexOptions.Compiled);
        private static readonly Regex InfoboxFraternity = new Regex(@"{{\s*[Ii]nfobox[\s_]+[Ff]raternity", RegexOptions.Compiled);
        private static readonly Regex BoldedLink = new Regex(@"'''.*?\[\[[^\[\]]+\]\].*?'''", RegexOptions.Compiled);
        private static readonly Regex RefImprove = new Regex(@"{{\s*[Rr]efimproveBLP\b", RegexOptions.Compiled);
        [Obsolete]
        public static bool IsArticleAboutAPerson(string articleText)
        {
            return IsArticleAboutAPerson(articleText, "", false);
        }
        public static bool IsArticleAboutAPerson(string articleText, string articleTitle)
        {
            return IsArticleAboutAPerson(articleText, articleTitle, false);
        }
        public static bool IsArticleAboutAPerson(string articleText, string articleTitle, bool parseTalkPage)
        {
            if (Variables.LangCode != "en"
                || articleText.Contains(@"[[Category:Multiple people]]")
                || articleText.Contains(@"[[Category:Married couples")
                || articleText.Contains(@"[[Category:Fictional")
                || Regex.IsMatch(articleText, @"\[\[Category:\d{4} animal")
                || articleText.Contains(@"[[fictional character")
                || InUniverse.IsMatch(articleText)
                || articleText.Contains(@"[[Category:Presidencies")
                || articleText.Contains(@"[[Category:Military careers")
                || CategoryCharacters.IsMatch(articleText))
                return false;
            if (
                Tools.GetTemplateParameterValue(
                    Tools.NestedTemplateRegex(new[]
                                                  {
                                                      "Infobox musical artist", "Infobox musical artist 2",
                                                      "Infobox Musical Artist", "Infobox singer", "Infobox Musician",
                                                      "Infobox musician", "Music artist", "Infobox Musical Artist 2",
                                                      "Infobox Musicial Artist 2", "Infobox Composer", "Infobox composer",
                                                      "Infobox Musical artist", "Infobox Band"
                                                  }).Match(articleText).Value,
                    "Background").Contains("group_or_band"))
            {
                return false;
            }
            string zerothSection = WikiRegexes.ZerothSection.Match(articleText).Value;
            if (SeeAlsoOrMain.IsMatch(zerothSection))
                return false;
            if (InfoboxFraternity.IsMatch(articleText))
                return false;
            int dateBirthAndAgeCount = WikiRegexes.DateBirthAndAge.Matches(articleText).Count;
            int dateDeathAndAgeCount = WikiRegexes.DeathDate.Matches(articleText).Count;
            if (dateBirthAndAgeCount > 1 || dateDeathAndAgeCount > 1)
                return false;
            if (WikiRegexes.Persondata.Matches(articleText).Count == 1
                || articleText.Contains(@"-bio-stub}}")
                || articleText.Contains(@"[[Category:Living people"))
                return true;
            if (BoldedLink.IsMatch(WikiRegexes.Template.Replace(zerothSection, "")))
                return false;
            if (dateBirthAndAgeCount == 1 || dateDeathAndAgeCount == 1)
                return true;
            return WikiRegexes.DeathsOrLivingCategory.IsMatch(articleText)
                || WikiRegexes.LivingPeopleRegex2.IsMatch(articleText)
                || WikiRegexes.BirthsCategory.IsMatch(articleText)
                || WikiRegexes.BLPSources.IsMatch(articleText)
                || RefImprove.IsMatch(articleText)
                || (!string.IsNullOrEmpty(articleTitle) && parseTalkPage &&
                    TryGetArticleText(Variables.Namespaces[Namespace.Talk] + articleTitle).Contains(@"{{WPBiography"));
        }
        private static string TryGetArticleText(string title)
        {
            try
            {
                return Variables.MainForm.TheSession.Editor.SynchronousEditor.Clone().Open(title, false);
            }
            catch
            {
                return "";
            }
        }
        [Obsolete]
        private static string DefaultsortTitlesWithDiacritics(string articleText, string articleTitle, int matches)
        {
            return DefaultsortTitlesWithDiacritics(articleText, articleTitle, matches, false);
        }
        public static string LivingPeople(string articleText, out bool noChange)
        {
            string newText = LivingPeople(articleText);
            noChange = (newText == articleText);
            return newText;
        }
        private static readonly Regex ThreeOrMoreDigits = new Regex(@"\d{3,}", RegexOptions.Compiled);
        private static readonly Regex BirthsSortKey = new Regex(@"\|.*?\]\]", RegexOptions.Compiled);
        public static string LivingPeople(string articleText)
        {
            if (WikiRegexes.DeathsOrLivingCategory.IsMatch(articleText) || WikiRegexes.LivingPeopleRegex2.IsMatch(articleText) ||
                BornDeathRegex.IsMatch(articleText) || DiedDateRegex.IsMatch(articleText))
                return articleText;
            Match m = WikiRegexes.BirthsCategory.Match(articleText);
            if (!m.Success)
                return articleText;
            string birthCat = m.Value;
            int birthYear = 0;
            string byear = m.Groups[1].Value;
            if (ThreeOrMoreDigits.IsMatch(byear))
                birthYear = int.Parse(byear);
            if (birthYear < (DateTime.Now.Year - 121))
                return articleText;
            string catKey = birthCat.Contains("|") ? BirthsSortKey.Match(birthCat).Value : "]]";
            return articleText + "[[Category:Living people" + catKey;
        }
        private static readonly Regex PersonYearOfBirth = new Regex(@"(?<='''.{0,100}?)\( *[Bb]orn[^\)\.;]{1,150}?(?<!.*(?:[Dd]ied|&[nm]dash;|â).*)([12]?\d{3}(?: BC)?)\b[^\)]{0,200}", RegexOptions.Compiled);
        private static readonly Regex PersonYearOfDeath = new Regex(@"(?<='''.{0,100}?)\([^\(\)]*?[Dd]ied[^\)\.;]+?([12]?\d{3}(?: BC)?)\b", RegexOptions.Compiled);
        private static readonly Regex PersonYearOfBirthAndDeath = new Regex(@"^.{0,100}?'''\s*\([^\)\r\n]*?(?<![Dd]ied)\b([12]?\d{3})\b[^\)\r\n]*?(-|â|â|&[nm]dash;)[^\)\r\n]*?([12]?\d{3})\b[^\)]{0,200}", RegexOptions.Singleline | RegexOptions.Compiled);
        private static readonly Regex UncertainWordings = new Regex(@"(?:\b(about|before|after|either|prior to|around|late|[Cc]irca|between|[Bb]irth based on age as of date|\d{3,4}(?:\]\])?/(?:\[\[)?\d{1,4}|or +(?:\[\[)?\d{3,})\b|\d{3} *\?|\bca?(?:'')?\.|\bca\b|\b(bef|abt)\.|~)", RegexOptions.Compiled);
        private static readonly Regex ReignedRuledUnsure = new Regex(@"(?:\?|[Rr](?:uled|eign(?:ed)?\b)|\br\.|(chr|fl(?:\]\])?)\.|\b(?:[Ff]lo(?:urished|ruit)|active)\b)", RegexOptions.Compiled);
        [Obsolete]
        public string FixPeopleCategories(string articleText, out bool noChange)
        {
            return FixPeopleCategories(articleText, "", false, out noChange);
        }
        [Obsolete]
        public string FixPeopleCategories(string articleText, string articleTitle, out bool noChange)
        {
            string newText = FixPeopleCategories(articleText, articleTitle);
            noChange = (newText == articleText);
            return newText;
        }
        public string FixPeopleCategories(string articleText, string articleTitle, bool parseTalkPage, out bool noChange)
        {
            string newText = FixPeopleCategories(articleText, articleTitle, parseTalkPage);
            noChange = (newText == articleText);
            return newText;
        }
        private static readonly Regex LongWikilink = new Regex(@"\[\[[^\[\]\|]{11,}(?:\|[^\[\]]+)?\]\]", RegexOptions.Compiled);
        private static readonly Regex YearPossiblyWithBC = new Regex(@"\d{3,4}(?![\ds])(?: BC)?", RegexOptions.Compiled);
        private static readonly Regex ThreeOrFourDigitNumber = new Regex(@"\d{3,4}", RegexOptions.Compiled);
        private static readonly Regex DiedOrBaptised = new Regex(@"(^.*?)((?:&[nm]dash;|â|â|;|[Dd](?:ied|\.)|baptised).*)", RegexOptions.Compiled);
        private static readonly Regex NotCircaTemplate = new Regex(@"{{(?![Cc]irca)[^{]*?}}", RegexOptions.Compiled);
        [Obsolete]
        public static string FixPeopleCategories(string articleText)
        {
            return FixPeopleCategories(articleText, "");
        }
        public static string FixPeopleCategories(string articleText, string articleTitle)
        {
            return FixPeopleCategories(articleText, articleTitle, false);
        }
        public static string FixPeopleCategories(string articleText, string articleTitle, bool parseTalkPage)
        {
            if (WikiRegexes.Refs.Matches(articleText).Count > 20 || (articleText.Length > 15000 && !WikiRegexes.BirthsCategory.IsMatch(articleText)
                                                                     && !WikiRegexes.DeathsOrLivingCategory.IsMatch(articleText)))
                return YearOfBirthMissingCategory(articleText);
            string articleTextBefore = articleText;
            string zerothSection = WikiRegexes.ZerothSection.Match(articleText).Value;
            zerothSection = WikiRegexes.Refs.Replace(zerothSection, " ");
            zerothSection = LongWikilink.Replace(zerothSection, " ");
            string yearstring, yearFromInfoBox = "";
            string sort = GetCategorySort(articleText);
            bool alreadyUncertain = false;
            string fromInfoBox = GetInfoBoxFieldValue(zerothSection, @"(?:(?:[Yy]ear|[Dd]ate)[Oo]f[Bb]irth|[Bb]orn|birth_?date)");
            if (fromInfoBox.Length > 0 && !UncertainWordings.IsMatch(fromInfoBox))
                yearFromInfoBox = YearPossiblyWithBC.Match(fromInfoBox).Value;
            if (!WikiRegexes.BirthsCategory.IsMatch(articleText) && (PersonYearOfBirth.Matches(zerothSection).Count == 1
                                                                     || WikiRegexes.DateBirthAndAge.IsMatch(zerothSection) || WikiRegexes.DeathDateAndAge.IsMatch(zerothSection)
                                                                     || ThreeOrFourDigitNumber.IsMatch(yearFromInfoBox)))
            {
                yearstring = WikiRegexes.DateBirthAndAge.Match(articleText).Groups[1].Value;
                if (String.IsNullOrEmpty(yearstring))
                    yearstring = WikiRegexes.DeathDateAndAge.Match(articleText).Groups[2].Value;
                if (ThreeOrFourDigitNumber.IsMatch(yearFromInfoBox))
                    yearstring = yearFromInfoBox;
                if (String.IsNullOrEmpty(yearstring))
                {
                    Match m = PersonYearOfBirth.Match(zerothSection);
                    string birthpart = DiedOrBaptised.Replace(m.Value, "$1");
                    if (WikiRegexes.CircaTemplate.IsMatch(birthpart))
                        alreadyUncertain = true;
                    birthpart = WikiRegexes.TemplateMultiline.Replace(birthpart, " ");
                    if (!(m.Index > PersonYearOfDeath.Match(zerothSection).Index) || !PersonYearOfDeath.IsMatch(zerothSection))
                    {
                        if (UncertainWordings.IsMatch(birthpart) || alreadyUncertain)
                        {
                            if (!CategoryMatch(articleText, YearOfBirthMissingLivingPeople) && !CategoryMatch(articleText, YearOfBirthUncertain))
                                articleText += Tools.Newline(@"[[Category:") + YearOfBirthUncertain + CatEnd(sort);
                        }
                        else
                            if (!birthpart.Contains(@"?") && Regex.IsMatch(birthpart, @"\d{3,4}"))
                                yearstring = m.Groups[1].Value;
                    }
                }
                if (!string.IsNullOrEmpty(yearstring) && yearstring.Length > 2
                    && (!Regex.IsMatch(yearstring, @"\d{4}") || Convert.ToInt32(yearstring) <= DateTime.Now.Year)
                    && !(articleText.Contains(@"[[Category:Living people") && Convert.ToInt32(yearstring) < (DateTime.Now.Year - 121)))
                    articleText += Tools.Newline(@"[[Category:") + yearstring + " births" + CatEnd(sort);
            }
            yearFromInfoBox = "";
            fromInfoBox = GetInfoBoxFieldValue(articleText, @"(?:(?:[Yy]ear|[Dd]ate)[Oo]f[Dd]eath|[Dd]ied|death_?date)");
            if (fromInfoBox.Length > 0 && !UncertainWordings.IsMatch(fromInfoBox))
                yearFromInfoBox = YearPossiblyWithBC.Match(fromInfoBox).Value;
            if (!WikiRegexes.DeathsOrLivingCategory.IsMatch(articleText) && (PersonYearOfDeath.IsMatch(zerothSection) || WikiRegexes.DeathDate.IsMatch(zerothSection)
                                                                             || ThreeOrFourDigitNumber.IsMatch(yearFromInfoBox)))
            {
                yearstring = WikiRegexes.DeathDate.Match(articleText).Groups[1].Value;
                if (ThreeOrFourDigitNumber.IsMatch(yearFromInfoBox))
                    yearstring = yearFromInfoBox;
                if (string.IsNullOrEmpty(yearstring))
                {
                    Match m = PersonYearOfDeath.Match(zerothSection);
                    if (m.Index >= PersonYearOfBirth.Match(zerothSection).Index || !PersonYearOfBirth.IsMatch(zerothSection))
                    {
                        if (!UncertainWordings.IsMatch(m.Value) && !m.Value.Contains(@"?"))
                            yearstring = m.Groups[1].Value;
                    }
                }
                if (!string.IsNullOrEmpty(yearstring) && yearstring.Length > 2
                    && (!Regex.IsMatch(yearstring, @"^\d{4}$") || Convert.ToInt32(yearstring) <= DateTime.Now.Year))
                    articleText += Tools.Newline(@"[[Category:") + yearstring + " deaths" + CatEnd(sort);
            }
            zerothSection = NotCircaTemplate.Replace(zerothSection, " ");
            if (PersonYearOfBirthAndDeath.IsMatch(zerothSection) && (!WikiRegexes.BirthsCategory.IsMatch(articleText) || !WikiRegexes.DeathsOrLivingCategory.IsMatch(articleText)))
            {
                Match m = PersonYearOfBirthAndDeath.Match(zerothSection);
                string birthyear = m.Groups[1].Value;
                int birthyearint = int.Parse(birthyear);
                string deathyear = m.Groups[3].Value;
                int deathyearint = int.Parse(deathyear);
                if (birthyearint <= deathyearint && (deathyearint - birthyearint) <= 125)
                {
                    string birthpart = zerothSection.Substring(m.Index, m.Groups[2].Index - m.Index),
                    deathpart = zerothSection.Substring(m.Groups[2].Index, (m.Value.Length + m.Index) - m.Groups[2].Index);
                    if (!WikiRegexes.BirthsCategory.IsMatch(articleText))
                    {
                        if (!UncertainWordings.IsMatch(birthpart) && !ReignedRuledUnsure.IsMatch(m.Value) && !Regex.IsMatch(birthpart, @"(?:[Dd](?:ied|\.)|baptised)"))
                            articleText += Tools.Newline(@"[[Category:") + birthyear + @" births" + CatEnd(sort);
                        else
                            if (UncertainWordings.IsMatch(birthpart) && !CategoryMatch(articleText, YearOfBirthMissingLivingPeople) && !CategoryMatch(articleText, YearOfBirthUncertain))
                                articleText += Tools.Newline(@"[[Category:Year of birth uncertain") + CatEnd(sort);
                    }
                    if (!UncertainWordings.IsMatch(deathpart) && !ReignedRuledUnsure.IsMatch(m.Value) && !Regex.IsMatch(deathpart, @"[Bb](?:orn|\.)") && !Regex.IsMatch(birthpart, @"[Dd](?:ied|\.)")
                        && (!WikiRegexes.DeathsOrLivingCategory.IsMatch(articleText) || CategoryMatch(articleText, YearofDeathMissing)))
                        articleText += Tools.Newline(@"[[Category:") + deathyear + @" deaths" + CatEnd(sort);
                }
            }
            if (articleText != articleTextBefore && !IsArticleAboutAPerson(articleTextBefore, articleTitle, parseTalkPage))
                return YearOfBirthMissingCategory(articleTextBefore);
            return YearOfBirthMissingCategory(articleText);
        }
        private static string CatEnd(string sort)
        {
            return ((sort.Length > 3) ? "|" + sort : "") + "]]";
        }
        private const string YearOfBirthMissingLivingPeople = "Year of birth missing (living people)",
        YearOfBirthMissing = "Year of birth missing",
        YearOfBirthUncertain = "Year of birth uncertain",
        YearofDeathMissing = "Year of death missing";
        private static readonly Regex Cat4YearBirths = new Regex(@"\[\[Category:\d{4} births(?:\s*\|[^\[\]]+)? *\]\]", RegexOptions.Compiled);
        private static readonly Regex Cat4YearDeaths = new Regex(@"\[\[Category:\d{4} deaths(?:\s*\|[^\[\]]+)? *\]\]", RegexOptions.Compiled);
        private static string YearOfBirthMissingCategory(string articleText)
        {
            if (Variables.LangCode != "en")
                return articleText;
            if (CategoryMatch(articleText, YearOfBirthMissingLivingPeople) && Cat4YearBirths.IsMatch(articleText))
                articleText = RemoveCategory(YearOfBirthMissingLivingPeople, articleText);
            else if (CategoryMatch(articleText, YearOfBirthMissing))
            {
                if (Cat4YearBirths.IsMatch(articleText))
                    articleText = RemoveCategory(YearOfBirthMissing, articleText);
            }
            if (CategoryMatch(articleText, YearOfBirthMissing) && CategoryMatch(articleText, YearOfBirthUncertain))
                articleText = RemoveCategory(YearOfBirthMissing, articleText);
            if (CategoryMatch(articleText, YearofDeathMissing) && Cat4YearDeaths.IsMatch(articleText))
                articleText = RemoveCategory(YearofDeathMissing, articleText);
            return articleText;
        }
        private static readonly Regex InfoboxValue = new Regex(@"\s*\|[^{}\|=]+?\s*=\s*.*", RegexOptions.Compiled);
        public static string GetInfoBoxFieldValue(string articleText, string fieldRegex)
        {
            string infoBox = WikiRegexes.InfoBox.Match(articleText).Value;
            string fieldValue;
            infoBox = WikiRegexes.Comments.Replace(infoBox, "");
            infoBox = WikiRegexes.Refs.Replace(infoBox, "");
            try
            {
                fieldValue = Regex.Match(infoBox, @"^\s*\|?\s*" + fieldRegex + @"\s*=\s*(.*)", RegexOptions.Multiline).Groups[1].Value.Trim();
            }
            catch
            {
                return "";
            }
            if (fieldValue.Length > 0)
            {
                if (InfoboxValue.IsMatch(fieldValue))
                {
                    return "";
                }
                return fieldValue;
            }
            return "";
        }
        public static string InterwikiConversions(string articleText, out bool noChange)
        {
            string newText = InterwikiConversions(articleText);
            noChange = (newText == articleText);
            return newText;
        }
        public static string InterwikiConversions(string articleText)
        {
            if (articleText.Contains("[[zh-tw:"))
                articleText = articleText.Replace("[[zh-tw:", "[[zh:");
            if (articleText.Contains("[[nb:"))
                articleText = articleText.Replace("[[nb:", "[[no:");
            if (articleText.Contains("[[dk:"))
                articleText = articleText.Replace("[[dk:", "[[da:");
            return articleText;
        }
        public static string Conversions(string articleText, out bool noChange)
        {
            string newText = Conversions(articleText);
            noChange = (newText == articleText);
            return newText;
        }
        public static string Conversions(string articleText)
        {
            if (articleText.Contains("{{msg:"))
                articleText = articleText.Replace("{{msg:", "{{");
            foreach (KeyValuePair<Regex, string> k in RegexConversion)
            {
                articleText = k.Key.Replace(articleText, k.Value);
            }
            articleText = Tools.RenameTemplate(articleText, @"2otheruses", "Two other uses");
            if (WikiRegexes.Refs.IsMatch(articleText))
                articleText = Tools.RenameTemplate(articleText, @"nofootnotes", "morefootnotes");
            if (Variables.IsWikipediaEN && WikiRegexes.Unreferenced.IsMatch(articleText) && articleText.Contains(@"[[Category:Living people"))
                articleText = Tools.RenameTemplate(articleText, WikiRegexes.Unreferenced.Match(articleText).Groups[1].Value, "BLP unsourced");
            articleText = MergePortals(articleText);
            return Dablinks(articleText);
        }
        private static readonly Regex TemplateParameter2 = new Regex(" \\{\\{\\{2\\|\\}\\}\\}", RegexOptions.Compiled);
        public static string SubstUserTemplates(string talkPageText, string talkPageTitle, Regex userTalkTemplatesRegex)
        {
            if (userTalkTemplatesRegex == null)
                return talkPageText;
            talkPageText = talkPageText.Replace("{{{subst", "REPLACE_THIS_TEXT");
            //Dictionary<Regex, string> regexes = new Dictionary<Regex, string>() { { userTalkTemplatesRegex, "{{subst:$2}}" } };
            talkPageText = Tools.ExpandTemplate(talkPageText, talkPageTitle, regexes, true);
            talkPageText = TemplateParameter2.Replace(talkPageText, "");
            return talkPageText.Replace("REPLACE_THIS_TEXT", "{{{subst");
        }
        public string Tagger(string articleText, string articleTitle, bool restrictOrphanTagging, out bool noChange, ref string summary)
        {
            string newText = Tagger(articleText, articleTitle, restrictOrphanTagging, ref summary);
            newText = TagUpdater(newText);
            noChange = (newText == articleText);
            return newText;
        }
        private static readonly CategoriesOnPageNoHiddenListProvider CategoryProv = new CategoriesOnPageNoHiddenListProvider();
        private readonly List<string> tagsRemoved = new List<string>();
        private readonly List<string> tagsAdded = new List<string>();
        public string Tagger(string articleText, string articleTitle, bool restrictOrphanTagging, ref string summary)
        {
            if (!Namespace.IsMainSpace(articleTitle) || Tools.IsRedirect(articleText))
                return articleText;
            tagsRemoved.Clear();
            tagsAdded.Clear();
            string commentsStripped = WikiRegexes.Comments.Replace(articleText, "");
            Sorter.Interwikis(ref commentsStripped);
            string crapStripped = WikiRegexes.BulletedText.Replace(commentsStripped, "");
            int words = (Tools.WordCount(commentsStripped) + Tools.WordCount(crapStripped)) / 2;
            if ((words > StubMaxWordCount) && WikiRegexes.Stub.IsMatch(commentsStripped))
            {
                articleText = WikiRegexes.Stub.Replace(articleText, StubChecker).Trim();
                tagsRemoved.Add("stub");
            }
            if (Variables.LangCode == "en" && WikiRegexes.Stub.IsMatch(commentsStripped) &&
                WikiRegexes.Expand.IsMatch(commentsStripped))
            {
                articleText = WikiRegexes.Expand.Replace(articleText, "");
                tagsRemoved.Add("expand");
            }
            articleText = TagOrphans(articleText, articleTitle, restrictOrphanTagging);
            articleText = TagRefsIbid(articleText);
            articleText = TagEmptySection(articleText);
            foreach (Match m in WikiRegexes.Template.Matches(articleText))
            {
                if (!(WikiRegexes.Stub.IsMatch(m.Value)
                      || WikiRegexes.Uncat.IsMatch(m.Value)
                      || WikiRegexes.DeadEnd.IsMatch(m.Value)
                      || WikiRegexes.Wikify.IsMatch(m.Value)
                      || WikiRegexes.Orphan.IsMatch(m.Value)
                      || WikiRegexes.ReferenceList.IsMatch(m.Value)
                      || WikiRegexes.NewUnReviewedArticle.IsMatch(m.Value)
                      || m.Value.Contains("subst")))
                {
                    summary = PrepareTaggerEditSummary();
                    return articleText;
                }
            }
            double length = articleText.Length + 1,
            linkCount = Tools.LinkCount(commentsStripped);
            int totalCategories;
            {
                totalCategories = CategoryProv.MakeList(new[] { articleTitle }).Count
                    - Tools.NestedTemplateRegex("stub").Matches(commentsStripped).Count;
            }
            if (commentsStripped.Length <= 300 && !WikiRegexes.Stub.IsMatch(commentsStripped))
            {
                articleText += Tools.Newline("{{stub}}", 3);
                tagsAdded.Add("stub");
                commentsStripped = WikiRegexes.Comments.Replace(articleText, "");
            }
            if (words > 6 && totalCategories == 0
                && !WikiRegexes.Uncat.IsMatch(articleText) && Variables.LangCode != "nl")
            {
                if (WikiRegexes.Stub.IsMatch(commentsStripped))
                {
                    articleText += Tools.Newline("{{Uncategorized stub|", 2) + WikiRegexes.DateYearMonthParameter + @"}}";
                    tagsAdded.Add("[[CAT:UNCATSTUBS|uncategorised]]");
                }
                else
                {
                    articleText += Tools.Newline("{{Uncategorized|", 2) + WikiRegexes.DateYearMonthParameter + @"}}";
                    tagsAdded.Add("[[CAT:UNCAT|uncategorised]]");
                }
            }
            else if (totalCategories > 0 && WikiRegexes.Uncat.IsMatch(articleText))
            {
                articleText = WikiRegexes.Uncat.Replace(articleText, "");
                tagsRemoved.Add("uncategorised");
            }
            if (linkCount == 0 && !WikiRegexes.DeadEnd.IsMatch(articleText) && Variables.LangCode != "sv"
                && !Regex.IsMatch(WikiRegexes.ArticleIssues.Match(articleText).Value.ToLower(), @"\bdead ?end\b"))
            {
                articleText = "{{dead end|" + WikiRegexes.DateYearMonthParameter + "}}\r\n\r\n" + articleText;
                tagsAdded.Add("[[:Category:Dead-end pages|deadend]]");
            }
            else if ((linkCount - totalCategories) > 0 && WikiRegexes.DeadEnd.IsMatch(articleText))
            {
                articleText = WikiRegexes.DeadEnd.Replace(articleText, "");
                tagsRemoved.Add("deadend");
            }
            if (linkCount < 3 && ((linkCount / length) < 0.0025) && !WikiRegexes.Wikify.IsMatch(articleText)
                && !WikiRegexes.ArticleIssues.Match(articleText).Value.ToLower().Contains("wikify"))
            {
                articleText = "{{Wikify|" + WikiRegexes.DateYearMonthParameter + "}}\r\n\r\n" + articleText;
                tagsAdded.Add("[[WP:WFY|wikify]]");
            }
            else if (linkCount > 3 && ((linkCount / length) > 0.0025) &&
                     WikiRegexes.Wikify.IsMatch(articleText))
            {
                articleText = WikiRegexes.Wikify.Replace(articleText, "");
                tagsRemoved.Add("wikify");
            }
            summary = PrepareTaggerEditSummary();
            return articleText;
        }
        private static readonly WhatLinksHereAndPageRedirectsExcludingTheRedirectsListProvider WlhProv = new WhatLinksHereAndPageRedirectsExcludingTheRedirectsListProvider(MinIncomingLinksToBeConsideredAnOrphan);
        private const int MinIncomingLinksToBeConsideredAnOrphan = 3;
        private static readonly Regex Rq = Tools.NestedTemplateRegex("Rq");
        private string TagOrphans(string articleText, string articleTitle, bool restrictOrphanTagging)
        {
            bool orphaned, orphaned2;
            int incomingLinks = 0;
            {
                try
                {
                    incomingLinks = WlhProv.MakeList(Namespace.Article, articleTitle).Count;
                    orphaned = (incomingLinks < MinIncomingLinksToBeConsideredAnOrphan);
                    orphaned2 = restrictOrphanTagging
                        ? (incomingLinks == 0)
                        : orphaned;
                }
                catch (Exception ex)
                {
                    orphaned = orphaned2 = false;
                    ErrorHandler.CurrentPage = articleTitle;
                    ErrorHandler.Handle(ex);
                }
            }
            if (Variables.LangCode == "ru" && incomingLinks == 0 && Rq.Matches(articleText).Count == 1)
            {
                string rqText = Rq.Match(articleText).Value;
                if (!rqText.Contains("linkless"))
                    return articleText.Replace(rqText, rqText.Replace(@"}}", @"|linkless}}"));
            }
            if (orphaned2 && !WikiRegexes.Orphan.IsMatch(articleText) && Tools.GetTemplateParameterValue(WikiRegexes.ArticleIssues.Match(articleText).Value, "orphan").Length == 0
                && !WikiRegexes.Disambigs.IsMatch(articleText))
            {
                articleText = "{{orphan|" + WikiRegexes.DateYearMonthParameter + "}}\r\n\r\n" + articleText;
                tagsAdded.Add("[[CAT:O|orphan]]");
            }
            else if (!orphaned && WikiRegexes.Orphan.IsMatch(articleText))
            {
                articleText = WikiRegexes.Orphan.Replace(articleText, "");
                tagsRemoved.Add("orphan");
            }
            return articleText;
        }
        private static readonly Regex IbidOpCitRef = new Regex(@"<\s*ref\b[^<>]*>\s*(ibid\.?|op\.?\s*cit\.?|loc\.?\s*cit\.?)\b", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        private string TagRefsIbid(string articleText)
        {
            if (Variables.LangCode == "en" && IbidOpCitRef.IsMatch(articleText) && !WikiRegexes.Ibid.IsMatch(articleText))
            {
                tagsAdded.Add("Ibid");
                return @"{{Ibid|" + WikiRegexes.DateYearMonthParameter + @"}}" + articleText;
            }
            return articleText;
        }
        private string TagEmptySection(string articleText)
        {
            if (Variables.LangCode != "en")
                return articleText;
            string originalarticleText = "";
            int tagsadded = 0;
            while (!originalarticleText.Equals(articleText))
            {
                originalarticleText = articleText;
                int lastpos = -1;
                foreach (Match m in WikiRegexes.HeadingLevelTwo.Matches(articleText))
                {
                    if (lastpos > -1 && articleText.Substring(lastpos, (m.Index - lastpos)).Trim().Length == 0)
                    {
                        articleText = articleText.Insert(m.Index, @"{{Empty section|date={{subst:CURRENTMONTHNAME}} {{subst:CURRENTYEAR}}}}" + "\r\n");
                        tagsadded++;
                        break;
                    }
                    lastpos = m.Index + m.Length;
                }
            }
            if (tagsadded > 0)
                tagsAdded.Add("Empty section (" + tagsadded + ")");
            return articleText;
        }
        private string PrepareTaggerEditSummary()
        {
            string summary = "";
            if (tagsRemoved.Count > 0)
            {
                summary = "removed " + Tools.ListToStringCommaSeparator(tagsRemoved) + " tag" +
                    (tagsRemoved.Count == 1 ? "" : "s");
            }
            if (tagsAdded.Count > 0)
            {
                if (!string.IsNullOrEmpty(summary))
                    summary += ", ";
                summary += "added " + Tools.ListToStringCommaSeparator(tagsAdded) + " tag" +
                    (tagsAdded.Count == 1 ? "" : "s");
            }
            return summary;
        }
        public static string TagUpdater(string articleText)
        {
            foreach (KeyValuePair<Regex, string> k in RegexTagger)
            {
                articleText = k.Key.Replace(articleText, k.Value);
            }
            return articleText;
        }
        private static readonly Regex CommonPunctuation = new Regex(@"[""',\.;:`!\(\)\[\]\?\-â/]", RegexOptions.Compiled);
        public static string RedirectTagger(string articleText, string articleTitle)
        {
            if (!Tools.IsRedirect(articleText) || !Variables.IsWikipediaEN || WikiRegexes.Template.IsMatch(articleText))
                return articleText;
            string redirecttarget = Tools.RedirectTarget(articleText);
            if (Tools.TurnFirstToUpperNoProjectCheck(redirecttarget).Equals(Tools.TurnFirstToUpperNoProjectCheck(articleTitle)))
                return articleText;
   if(!Namespace.IsMainSpace(redirecttarget) && !Tools.NestedTemplateRegex(new[] { @"R to other namespace,R to other namespaces"}).IsMatch(articleText))
    return (articleText + Tools.Newline(@"{{R to other namespace}}"));
            if (!Tools.NestedTemplateRegex(WikiRegexes.RFromModificationList).IsMatch(articleText)
                && !CommonPunctuation.Replace(redirecttarget, "").Equals(redirecttarget) && CommonPunctuation.Replace(redirecttarget, "").Equals(CommonPunctuation.Replace(articleTitle, "")))
                return (articleText + Tools.Newline(WikiRegexes.RFromModificationString));
            if (redirecttarget != Tools.RemoveDiacritics(redirecttarget) && Tools.RemoveDiacritics(redirecttarget) == articleTitle
                && !Tools.NestedTemplateRegex(WikiRegexes.RFromTitleWithoutDiacriticsList).IsMatch(articleText))
                return (articleText + Tools.Newline(WikiRegexes.RFromTitleWithoutDiacriticsString));
            if (redirecttarget.ToLower().Equals(articleTitle.ToLower())
               && !Tools.NestedTemplateRegex(WikiRegexes.RFromOtherCapitaliastionList).IsMatch(articleText))
                return (articleText + Tools.Newline(WikiRegexes.RFromOtherCapitaliastionString));
            return articleText;
        }
        private static string StubChecker(Match m)
        {
            return Variables.SectStubRegex.IsMatch(m.Value) ? m.Value : "";
        }
        private static readonly Regex BotsAllow = new Regex(@"{{\s*(?:[Nn]obots|[Bb]ots)\s*\|\s*allow\s*=(.*?)}}", RegexOptions.Singleline | RegexOptions.Compiled);
        public static bool CheckNoBots(string articleText, string user)
        {
            Match bot = BotsAllow.Match(articleText);
            if (bot.Success)
            {
                return
                    (Regex.IsMatch(bot.Groups[1].Value,
                                   @"(?:^|,)\s*(?:" + user.Normalize() + @"|awb)\s*(?:,|$)", RegexOptions.IgnoreCase));
            }
            return
                !Regex.IsMatch(articleText,
                               @"\{\{(nobots|bots\|(allow=none|deny=(?!none).*(" + user.Normalize() +
                               @"|awb|all)|optout=all))\}\}", RegexOptions.IgnoreCase);
        }
        private static readonly Regex DuplicatePipedLinks = new Regex(@"\[\[([^\]\|]+)\|([^\]]*)\]\](.*[.\n]*)\[\[\1\|\2\]\]", RegexOptions.Compiled);
        private static readonly Regex DuplicateUnpipedLinks = new Regex(@"\[\[([^\]]+)\]\](.*[.\n]*)\[\[\1\]\]", RegexOptions.Compiled);
        public static string RemoveDuplicateWikiLinks(string articleText)
        {
            articleText = DuplicatePipedLinks.Replace(articleText, "[[$1|$2]]$3$2");
            return DuplicateUnpipedLinks.Replace(articleText, "[[$1]]$2$1");
        }
        static readonly Regex ExtToIn = new Regex(@"(?<![*#:;]{2})\[http://([a-z0-9\-]{3})\.(?:(wikt)ionary|wiki(n)ews|wiki(b)ooks|wiki(q)uote|wiki(s)ource|wiki(v)ersity|(w)ikipedia)\.(?:com|net|org)/wiki/([^][{|}\s""]*) +([^\n\]]+)\]", RegexOptions.Compiled);
        static readonly Regex ExtToIn2 = new Regex(@"(?<![*#:;]{2})\[http://(?:(m)eta|(commons)|(incubator)|(quality))\.wikimedia\.(?:com|net|org)/wiki/([^][{|}\s""]*) +([^\n\]]+)\]", RegexOptions.Compiled);
        static readonly Regex ExtToIn3 = new Regex(@"(?<![*#:;]{2})\[http://([a-z0-9\-]+)\.wikia\.(?:com|net|org)/wiki/([^][{|}\s""]+) +([^\n\]]+)\]", RegexOptions.Compiled);
        public static string ExternalURLToInternalLink(string articleText)
        {
            articleText = ExtToIn.Replace(articleText, "[[$2$3$4$5$6$7:$1:$8|$9]]");
            articleText = ExtToIn2.Replace(articleText, "[[$1$2$3$4:$5|$6]]");
            return ExtToIn3.Replace(articleText, "[[wikia:$1:$2|$3]]");
        }
        public static bool HasStubTemplate(string articleText)
        {
            return WikiRegexes.Stub.IsMatch(articleText);
        }
        public static bool IsStub(string articleText)
        {
            return (HasStubTemplate(articleText) || articleText.Length < StubMaxWordCount);
        }
        public static bool HasInfobox(string articleText)
        {
            if (Variables.LangCode != "en")
                return false;
            articleText = WikiRegexes.Nowiki.Replace(articleText, "");
            articleText = WikiRegexes.Comments.Replace(articleText, "");
            return WikiRegexes.InfoBox.IsMatch(articleText);
        }
        public static bool IsInUse(string articleText)
        {
            return (Variables.LangCode != "en")
                ? false
                : WikiRegexes.InUse.IsMatch(WikiRegexes.UnformattedText.Replace(articleText, ""));
        }
        public static bool HasSicTag(string articleText)
        {
            return WikiRegexes.SicTag.IsMatch(articleText);
        }
        public static bool HasDeadLinks(string articleText)
        {
            articleText = WikiRegexes.Comments.Replace(articleText, "");
            return WikiRegexes.DeadLink.IsMatch(articleText);
        }
        public static bool HasMorefootnotesAndManyReferences(string articleText)
        {
            return (WikiRegexes.MoreNoFootnotes.IsMatch(WikiRegexes.Comments.Replace(articleText, "")) && WikiRegexes.Refs.Matches(articleText).Count > 4);
        }
        public static bool IsMissingReferencesDisplay(string articleText)
        {
            if (Variables.LangCode != "en")
                return false;
            return !WikiRegexes.ReferencesTemplate.IsMatch(articleText) && Regex.IsMatch(articleText, WikiRegexes.ReferenceEndGR);
        }
        public static bool HasRefAfterReflist(string articleText)
        {
            articleText = WikiRegexes.Comments.Replace(articleText, "");
            return (WikiRegexes.RefAfterReflist.IsMatch(articleText) &&
                    WikiRegexes.ReferencesTemplate.Matches(articleText).Count == 1);
        }
        public static bool HasBareReferences(string articleText)
        {
            int referencesIndex = WikiRegexes.ReferencesRegex.Match(articleText).Index;
            if (referencesIndex < 2)
                return false;
            int externalLinksIndex =
                WikiRegexes.ExternalLinksHeaderRegex.Match(articleText).Index;
            string refsArea = externalLinksIndex > referencesIndex
                ? articleText.Substring(referencesIndex, (externalLinksIndex - referencesIndex))
                : articleText.Substring(referencesIndex);
            return (WikiRegexes.BareExternalLink.IsMatch(refsArea));
        }
    }
}

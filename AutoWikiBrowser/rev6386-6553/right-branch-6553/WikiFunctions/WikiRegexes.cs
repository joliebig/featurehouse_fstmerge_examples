using System.Text;
using System.Text.RegularExpressions;
using System.Collections.Generic;
namespace WikiFunctions
{
    public static class WikiRegexes
    {
        public static void MakeLangSpecificRegexes()
        {
            NamespacesCaseInsensitive = new Dictionary<int,Regex>();
            foreach (var p in Variables.NamespacesCaseInsensitive)
            {
                NamespacesCaseInsensitive[p.Key] = new Regex(p.Value, RegexOptions.Compiled);
            }
            TemplateStart = @"\{\{\s*(:?" + Variables.NamespacesCaseInsensitive[Namespace.Template] + ")?";
            Category = new Regex(@"\[\[\s*" + Variables.NamespacesCaseInsensitive[Namespace.Category] +
                                 @"\s*(.*?)\s*(?:|\|([^\|\]]*))\s*\]\]", RegexOptions.Compiled);
            Images =
                new Regex(
                    @"\[\[\s*" + Variables.NamespacesCaseInsensitive[Namespace.File] +
                    @"[ \%\!""$&'\(\)\*,\-.\/0-9:;=\?@A-Z\\\^_`a-z~\x80-\xFF\+]+\.[a-zA-Z]{3,4}\b(?:\s*(?:\]\]|\|))?|<[Gg]allery\b([^>]*?)>[\s\S]*?</ ?[Gg]allery>|{{\s*[Gg]allery\s*(?:\|(?>[^\{\}]+|\{(?<DEPTH>)|\}(?<-DEPTH>))*(?(DEPTH)(?!)))?}}|(?<=\|\s*[a-zA-Z\d_ ]+\s*=)[^\|{}]+?\.[a-zA-Z]{3,4}(?=\s*(?:\||}}))",
                    RegexOptions.Compiled | RegexOptions.Singleline);
            FileNamespaceLink = new Regex(@"\[\[\s*" + Variables.NamespacesCaseInsensitive[Namespace.File] +
                                          @"((?>[^\[\]]+|\[\[(?<DEPTH>)|\]\](?<-DEPTH>))*(?(DEPTH)(?!)))\]\]", RegexOptions.Compiled);
            Stub = new Regex(@"{{.*?" + Variables.Stub + @"}}", RegexOptions.Compiled);
            PossiblyCommentedStub =
                new Regex(
                    @"(<!-- ?\{\{[^}]*?" + Variables.Stub + @"\b\}\}.*?-->|\{\{[^}]*?" + Variables.Stub + @"\}\})",
                    RegexOptions.Compiled);
            TemplateCall = new Regex(TemplateStart + @"\s*([^\]\|]*)\s*(.*)}}",
                                     RegexOptions.Compiled | RegexOptions.Singleline);
            LooseCategory =
                new Regex(@"\[\[[\s_]*" + Variables.NamespacesCaseInsensitive[Namespace.Category]
                    + @"[\s_]*([^\|]*?)(|\|.*?)\]\]",
                    RegexOptions.Compiled);
            LooseImage = new Regex(@"\[\[\s*?(" + Variables.NamespacesCaseInsensitive[Namespace.File]
                + @")\s*([^\|\]]+)(.*?)\]\]",
                RegexOptions.Compiled);
            Months = "(" + string.Join("|", Variables.MonthNames) + ")";
            MonthsNoGroup = "(?:" + string.Join("|", Variables.MonthNames) + ")";
            Dates = new Regex("^(0?[1-9]|[12][0-9]|3[01]) " + Months + "$", RegexOptions.Compiled);
            Dates2 = new Regex("^" + Months + " (0?[1-9]|[12][0-9]|3[01])$", RegexOptions.Compiled);
            InternationalDates = new Regex(@"\b([1-9]|[12][0-9]|3[01]) +" + Months + @" +([12]\d{3})\b", RegexOptions.Compiled);
            AmericanDates = new Regex(Months + @" +([1-9]|[12][0-9]|3[01]),? +([12]\d{3})\b", RegexOptions.Compiled);
            DayMonth = new Regex(@"\b([1-9]|[12][0-9]|3[01]) +" + Months + @"\b", RegexOptions.Compiled);
            MonthDay = new Regex(Months + @" +([1-9]|[12][0-9]|3[01])\b", RegexOptions.Compiled);
            DayMonthRangeSpan = new Regex(@"\b((?:[1-9]|[12][0-9]|3[01])(?:â|&ndash;|{{ndash}}|\/)(?:[1-9]|[12][0-9]|3[01])) " + Months + @"\b", RegexOptions.Compiled);
            MonthDayRangeSpan = new Regex(Months + @" ((?:[1-9]|[12][0-9]|3[01])(?:â|&ndash;|{{ndash}}|\/)(?:[1-9]|[12][0-9]|3[01]))\b", RegexOptions.Compiled);
            List<string> magic;
            string s = Variables.MagicWords.TryGetValue("redirect", out magic)
                           ? string.Join("|", magic.ToArray()).Replace("#", "")
                           : "REDIRECT";
            Redirect = new Regex(@"#(?:" + s + @")\s*:?\s*\[\[\s*:?\s*([^\|\[\]]*?)\s*(\|.*?)?\]\]", RegexOptions.IgnoreCase);
            switch (Variables.LangCode)
            {
                case "ru":
                    s = "([Dd]isambiguation|[Dd]isambig|[ÐÐ½]ÐµÐ¾Ð´Ð½Ð¾Ð·Ð½Ð°ÑÐ½Ð¾ÑÑÑ|[Ðm]Ð½Ð¾Ð³Ð¾Ð·Ð½Ð°ÑÐ½Ð¾ÑÑÑ)";
                    break;
                default:
                    s = "([Dd]isamb(?:ig(?:uation)?)?|[Dd]ab|[Mm]athdab|[Ss]urname|(?:[Nn]umber|[Rr]oad?|[Hh]ospital|[Gg]eo|[Hh]n|[Ss]chool)dis|SIA|[Ll]etter-disambig|[Ss]hipindex|[Mm]ountainindex|[[Aa]irport disambig|[Cc]allsigndis|[Dd]isambig-cleanup)";
                    break;
            }
            Disambigs = new Regex(TemplateStart + s + @"\s*(?:\|[^{}]*?)?}}", RegexOptions.Compiled);
            if (Variables.MagicWords.TryGetValue("defaultsort", out magic))
                s = "(?i:" + string.Join("|", magic.ToArray()).Replace(":", "") + ")";
            else
                s = (Variables.LangCode == "en")
                    ? "(?:(?i:defaultsort(key|CATEGORYSORT)?))"
                    : "(?i:defaultsort)";
            Defaultsort = new Regex(TemplateStart + s + @"\s*[:|](?<key>(?>[^\{\}]+|\{(?<DEPTH>)|\}(?<-DEPTH>))*(?(DEPTH)(?!))|[^\}\r\n]*?)(?:}}|\r|\n)",
                                    RegexOptions.Compiled | RegexOptions.ExplicitCapture);
            int pos = Tools.FirstDifference(Variables.URL, Variables.URLLong);
            s = Regex.Escape(Variables.URLLong.Substring(0, pos));
            s += "(?:" + Regex.Escape(Variables.URLLong.Substring(pos)) + @"index\.php(?:\?title=|/)|"
                 + Regex.Escape(Variables.URL.Substring(pos)) + "/wiki/" + ")";
            ExtractTitle = new Regex("^" + s + "([^?&]*)$", RegexOptions.Compiled);
            string cat = Variables.NamespacesCaseInsensitive[Namespace.Category],
                img = Variables.NamespacesCaseInsensitive[Namespace.Image];
            EmptyLink = new Regex("\\[\\[(:?" + cat + "|" + img + "|)(|" + img + "|" + cat + "|.*?)\\]\\]", RegexOptions.IgnoreCase | RegexOptions.Compiled);
            EmptyTemplate = new Regex(@"{{(" + Variables.NamespacesCaseInsensitive[Namespace.Template] + @")?[|\s]*}}", RegexOptions.IgnoreCase | RegexOptions.Compiled);
            string orphantemplate = "", uncattemplate = "";
            switch(Variables.LangCode)
            {
                case "sv":
                    orphantemplate = @"FÃ¶rÃ¤ldralÃ¶s";
                    uncattemplate = "[Oo]kategoriserad";
                    Wikify = new Regex(@"{{\s*Ickewiki(?:\s*\|\s*(date={{subst:CURRENTMONTHNAME}} {{subst:CURRENTYEAR}}|.*?))?}}", RegexOptions.Compiled | RegexOptions.IgnoreCase);
                    DateYearMonthParameter = @"datum={{subst:CURRENTYEAR}}-{{subst:CURRENTMONTH}}";
                    break;
                default:
                    orphantemplate = "Orphan";
                    uncattemplate = @"([Uu]ncat|[Cc]lassify|[Cc]at[Nn]eeded|[Uu]ncategori[sz]ed|[Cc]ategori[sz]e|[Cc]ategories needed|[Cc]ategory ?needed|[Cc]ategory requested|[Cc]ategories requested|[Nn]ocats?|[Uu]ncat-date|[Uu]ncategorized-date|[Nn]eeds cats?|[Cc]ats? needed|[Uu]ncategori[sz]edstub)";
                    Wikify = new Regex(@"({{\s*Wikify(?:\s*\|\s*(date={{subst:CURRENTMONTHNAME}} {{subst:CURRENTYEAR}}|.*?))?}}|(?<={{\s*(?:Article|Multiple)\s*issues\b[^{}]*?)\|\s*wikify\s*=[^{}\|]+)", RegexOptions.Compiled | RegexOptions.IgnoreCase);
                    DateYearMonthParameter = @"date={{subst:CURRENTMONTHNAME}} {{subst:CURRENTYEAR}}";
                    break;
            }
            Orphan = new Regex(@"{{\s*" + orphantemplate + @"(?:\s*\|\s*(" + DateYearMonthParameter + @"|.*?))?}}", RegexOptions.Compiled | RegexOptions.IgnoreCase);
            Uncat = new Regex(@"{{\s*" + uncattemplate + @"((\s*\|[^{}]+)?\s*|\s*\|((?>[^\{\}]+|\{\{(?<DEPTH>)|\}\}(?<-DEPTH>))*(?(DEPTH)(?!))))\}\}", RegexOptions.Compiled);
        }
        public static string Months;
        public static string MonthsNoGroup;
        public static string GenerateNamespaceRegex(params int[] namespaces)
        {
            StringBuilder sb = new StringBuilder(100 * namespaces.Length);
            foreach (int ns in namespaces)
            {
                if (ns == Namespace.Article) continue;
                if (sb.Length > 0) sb.Append('|');
                string nsName = Variables.Namespaces[ns];
                sb.Append(Tools.StripNamespaceColon(nsName));
                string canNS;
                if (Variables.CanonicalNamespaces.TryGetValue(ns, out canNS)
                    && canNS != nsName)
                {
                    sb.Append('|');
                    sb.Append(Tools.StripNamespaceColon(canNS));
                }
                List<string> nsAlias;
                if (Variables.NamespaceAliases.TryGetValue(ns, out nsAlias))
                    foreach (string s in nsAlias)
                    {
                        sb.Append('|');
                        sb.Append(Tools.StripNamespaceColon(s));
                    }
            }
            sb.Replace(" ", "[ _]");
            return sb.ToString();
        }
        public static Dictionary<int, Regex> NamespacesCaseInsensitive;
        public static string TemplateStart;
        public static readonly Regex SimpleWikiLink = new Regex(@"\[\[((?>[^\[\]\n]+|\[\[(?<DEPTH>)|\]\](?<-DEPTH>))*(?(DEPTH)(?!)))\]\]", RegexOptions.Compiled);
        public static readonly Regex WikiLinksOnly = new Regex(@"\[\[[^[\]\n]+(?<!\[\[[A-Z]?[a-z-]{2,}:[^[\]\n]+)\]\]", RegexOptions.Compiled);
        public static readonly Regex WikiLinksOnlyPossiblePipe = new Regex(@"\[\[([^[\]\|\n]+)(?<!\[\[[A-Z]?[a-z-]{2,}:[^[\]\n]+)(?:\|([^[\]\|\n]+))?\]\]", RegexOptions.Compiled);
        public static readonly Regex WikiLinksOnlyPlusWord = new Regex(@"\[\[[^\[\]\n]+\]\](\w+)", RegexOptions.Compiled);
        public static readonly Regex WikiLink = new Regex(@"\[\[(.*?)(?:\]\]|\|)", RegexOptions.Compiled);
        public static readonly Regex PipedWikiLink = new Regex(@"\[\[([^[\]\n]*?)\|([^[\]\n]*?)\]\]", RegexOptions.Compiled);
        public static readonly Regex UnPipedWikiLink = new Regex(@"\[\[([^\|\n]*?)\]\]", RegexOptions.Compiled);
        public static readonly Regex AnchorEncodedLink = new Regex(@"#.*(_|\.[0-9A-Z]{2}).*", RegexOptions.Compiled);
        public static Regex Defaultsort;
        public static readonly Regex Heading = new Regex(@"^(=+)(.*?)(=+)", RegexOptions.Compiled | RegexOptions.Multiline);
        public static readonly Regex Headings = new Regex(@"^={1,6}(.*?)={1,6}\s*$", RegexOptions.Multiline | RegexOptions.Compiled);
        public static readonly Regex HeadingLevelTwo = new Regex(@"^==([^=](?:.*?[^=])?)==\s*$", RegexOptions.Multiline);
        public static readonly Regex HeadingLevelThree = new Regex(@"^===([^=](?:.*?[^=])?)===\s*$", RegexOptions.Multiline);
        public static readonly Regex SectionLevelTwo = new Regex(@"^==[^=][^\r\n]*?[^=]==.*?(?=^==[^=][^\r\n]*?[^=]==(\r\n?|\n)$)", RegexOptions.Multiline | RegexOptions.Singleline);
        public static readonly Regex ZerothSection = new Regex("(^.+?(?===+)|^.+$)", RegexOptions.Singleline);
        public static readonly Regex ArticleToFirstLevelTwoHeading = new Regex(@"^.*?(?=[^=]==[^=][^\r\n]*?[^=]==(\r\n?|\n))", RegexOptions.Singleline);
        public static readonly Regex IndentedText = new Regex(@"^:.*", RegexOptions.Compiled | RegexOptions.Multiline);
        public static readonly Regex BulletedText = new Regex(@"^[\*#: ]+.*?$", RegexOptions.Multiline | RegexOptions.Compiled);
        public static readonly Regex Template = new Regex(@"{{[^{\n]*?}}", RegexOptions.Compiled);
        public static readonly Regex TemplateEnd = new Regex(@" *(\r\n)*}}$", RegexOptions.Compiled);
        public static readonly Regex TemplateMultiline = new Regex(@"{{[^{]*?}}", RegexOptions.Compiled);
        public static readonly Regex NestedTemplates = new Regex(@"{{(?>[^\{\}]+|\{(?<DEPTH>)|\}(?<-DEPTH>))*(?(DEPTH)(?!))}}");
        public static readonly Regex TemplateName = new Regex(@"{{\s*([^\|{}]+?)(?:\s*<!--.*?-->\s*)?\s*(?:\||}})");
        public static readonly Regex ExternalLinks = new Regex(@"(https?|ftp|mailto|irc|gopher|telnet|nntp|worldwind|news|svn)://(?:[\w\._\-~!/\*""'\(\):;@&=+$,\\\?%#\[\]]+?(?=}})|[\w\._\-~!/\*""'\(\):;@&=+$,\\\?%#\[\]]*)|\[(https?|ftp|mailto|irc|gopher|telnet|nntp|worldwind|news|svn)://.*?\]", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        public static readonly Regex PossibleInterwikis = new Regex(@"\[\[\s*([-a-z]{2,12})\s*:\s*([^\]]*?)\s*\]\]", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static readonly Regex UnformattedText = new Regex(@"<nowiki>.*?</\s*nowiki>|<pre\b.*?>.*?</\s*pre>|<math\b.*?>.*?</\s*math>|<!--.*?-->|<timeline\b.*?>.*?</\s*timeline>", RegexOptions.Singleline | RegexOptions.Compiled);
        public static readonly Regex Blockquote = new Regex(@"<\s*blockquote\s*>(.*?)<\s*/\s*blockquote\s*>", RegexOptions.IgnoreCase | RegexOptions.Singleline | RegexOptions.Compiled);
        public static readonly Regex Poem = new Regex(@"<\s*poem\s*>(.*?)<\s*/\s*poem\s*>", RegexOptions.IgnoreCase | RegexOptions.Singleline | RegexOptions.Compiled);
        public static readonly Regex ImageMap = new Regex(@"<\s*imagemap\b[^<>]*>(.*?)<\s*/\s*imagemap\s*>", RegexOptions.IgnoreCase | RegexOptions.Singleline | RegexOptions.Compiled);
        public static readonly Regex Noinclude = new Regex(@"<\s*noinclude\s*>(.*?)<\s*/\s*noinclude\s*>", RegexOptions.IgnoreCase | RegexOptions.Singleline | RegexOptions.Compiled);
        public static readonly Regex Includeonly = new Regex(@"(?:<\s*includeonly\s*>.*?<\s*/\s*includeonly\s*>|<\s*onlyinclude\s*>.*?<\s*/\s*onlyinclude\s*>)", RegexOptions.IgnoreCase | RegexOptions.Singleline | RegexOptions.Compiled);
        public static Regex Redirect;
        public const string RFromModificationString = @"{{R from modification}}";
        public static readonly List<string> RFromModificationList = new List<string>(@"R from alternative punctuation,R mod,R from modifcation,R from modification,R from alternate punctuation".Split(','));
        public const string RFromTitleWithoutDiacriticsString = @"{{R from title without diacritics}}";
        public static readonly List<string> RFromTitleWithoutDiacriticsList = new List<string>(@"R from name without diacritics,R from original name without diacritics,R from title without diacritics,R to accents,R to diacritics,R to title with diacritics,R to unicode,R to unicode name,R without diacritics,RDiacr,Redirects from title without diacritics".Split(','));
        public const string RFromOtherCapitaliastionString = @"{{R from other capitalisation}}";
        public static readonly List<string> RFromOtherCapitaliastionList = new List<string>(@"R cap,R for alternate capitalisation,R for alternate capitalization,R for alternative capitalisation,R for alternative capitalization,R from Capitalisation,R from Capitalization,R from alt cap,R from alt case,R from alternate capitalisation,R from alternate capitalization,R from alternative capitalisation,R from alternative capitalization,R from another capitalisation,R from another capitalization,R from cap,R from capitalisation,R from capitalization,R from caps,R from different capitalization ,R from lowercase,R from miscapitalization,R from other Capitalization,R from other capitalisation,R from other capitalization,R from other caps,R to Capitalization,R to alternate capitalisation,Redirect from capitalisation".Split(','));
        public static readonly Regex RegexWord = new Regex(@"\w+", RegexOptions.Compiled);
        public static readonly Regex Newline = new Regex("\\n", RegexOptions.Compiled);
        public static readonly Regex RegexWordApostrophes = new Regex(@"\w+(?:'\w+)?", RegexOptions.Compiled);
        public static readonly Regex Source = new Regex(@"<\s*source(?:\s.*?|)>(.*?)<\s*/\s*source\s*>", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static readonly Regex Code = new Regex(@"<\s*code\s*>(.*?)<\s*/\s*code\s*>", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static readonly Regex MathPreSourceCodeComments = new Regex(@"<pre>.*?</pre>|<!--.*?-->|<math>.*?</math>|" + Code + @"|" + Source, RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static Regex Dates;
        public static Regex Dates2;
        public static Regex Category;
        public static Regex Images;
        public static Regex FileNamespaceLink;
        public static Regex Disambigs;
        public static Regex Stub;
        public static Regex PossiblyCommentedStub;
        public static Regex LooseCategory;
        public static Regex LooseImage;
        public static readonly Regex UntemplatedQuotes = new Regex(@"(?<=[^\w])[""Â«Â»âââââââ¹âºâââââ`ââââ].{1,500}?[""Â«Â»âââââââ¹âºâââââ`ââââ](?=[^\w])", RegexOptions.Compiled | RegexOptions.Singleline);
        public static readonly Regex UnitsWithoutNonBreakingSpaces = new Regex(@"\b(\d?\.?\d+)\s*((?:[cmknuÂµ])(?:[mgWN])|m?mol|cd|mi|lb[fs]?|b?hp|mph|inch(?:es)?|ft|[kGM]Hz|gram(?:me)?s?)\b(?<!(\d?\.?\d+)mm)", RegexOptions.Compiled);
        public static readonly Regex MetresFeetConversionNonBreakingSpaces = new Regex(@"(\d+(?:\.\d+)?) ?m(?= \(\d+(?:\.\d+)?&nbsp;ft\.?\))");
        public static readonly Regex ImperialUnitsInBracketsWithoutNonBreakingSpaces = new Regex(@"(\(\d+(?:\.\d+)?(?:\s*(?:-|â|&mdash;)\s*\d+(?:\.\d+)?)?)\s*((?:in|feet|foot|oz)\))", RegexOptions.Compiled);
        public static readonly Regex SicTag = new Regex(@"({{\s*(?:[Ss]ic|[Tt]ypo)(?:\||}})|([\(\[{]\s*[Ss]ic!?\s*[\)\]}]))", RegexOptions.Compiled);
        public static readonly Regex UseDatesTemplate = new Regex(@"{{\s*[Uu]se (dmy|mdy|ymd) dates\s*}}", RegexOptions.Compiled);
        public static Regex AmericanDates;
        public static Regex InternationalDates;
        public static Regex MonthDay;
        public static Regex DayMonth;
        public static Regex MonthDayRangeSpan;
        public static Regex DayMonthRangeSpan;
        public static readonly Regex ISODates = new Regex(@"\b(20\d\d|1[6-9]\d\d)-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\b", RegexOptions.Compiled);
        public static readonly Regex TalkHeaderTemplate = new Regex(@"\{\{\s*(?:template *:)?\s*(talk[ _]?(page)?(header)?)\s*(?:\|[^{}]*)?\}\}\s*",
           RegexOptions.Compiled | RegexOptions.IgnoreCase);
        public static readonly Regex SkipTOCTemplateRegex = new Regex(
            @"\{\{\s*(template *:)?\s*(skiptotoctalk|Skiptotoc|Skip to talk)\s*\}\}\s*",
            RegexOptions.Compiled | RegexOptions.ExplicitCapture | RegexOptions.IgnoreCase);
        public static readonly Regex MoreNoFootnotes = Tools.NestedTemplateRegex(new List<string>(@"no footnotes,nofootnotes,more footnotes,morefootnotes".Split(',')));
        public static readonly Regex BLPSources = new Regex(@"{{\s*([Bb](LP|lp) ?(sources|[Uu]n(sourced|ref(?:erenced)?))|[Uu]n(sourced|referenced) ?[Bb](LP|lp))\b", RegexOptions.Compiled);
        public const string ReferencesTemplates = @"(\{\{\s*(?:ref(?:-?li(?:st|nk)|erence)|[Ll]istaref)(?>[^\{\}]+|\{(?<DEPTH>)|\}(?<-DEPTH>))*(?(DEPTH)(?!))\}\}|<\s*references\s*/>|\{\{refs|<\s*references\s*>.*</\s*references\s*>)";
        public const string ReferenceEndGR = @"(?:</ref>|{{GR\|\d}})";
        public static readonly Regex ReferencesTemplate = new Regex(ReferencesTemplates, RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static readonly Regex RefAfterReflist = new Regex(ReferencesTemplates + ".*?" + ReferenceEndGR, RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static readonly Regex BareExternalLink = new Regex(@"^ *\*? *(?:[Hh]ttp|[Hh]ttps|[Ff]tp|[Mm]ailto)://[^\ \n\r<>]+\s+$", RegexOptions.Multiline);
        public static readonly Regex BareRefExternalLink = new Regex(@"<\s*ref\b[^<>]*>\s*\[*\s*((?:https?|ftp|mailto)://[^\ \n\r<>]+?)\s*\]*[\.,:;\s]*<\s*/\s*ref\s*>", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static readonly Regex BareRefExternalLinkBotGenTitle = new Regex(@"<\s*ref\b[^<>]*>\s*\[*\s*((?:https?|ftp|mailto)://[^\ \n\r<>]+?)\s*(?:\s+[^<>{}]+?<!--\s*Bot generated title\s*-->)?\]*[\.,:;\s]*<\s*/\s*ref\s*>", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static readonly Regex CiteTemplate = new Regex(@"{{\s*([Cc]it[ae][^{}\|]*?)\s*(\|(?>[^\{\}]+|\{(?<DEPTH>)|\}(?<-DEPTH>))*(?(DEPTH)(?!)))?}}", RegexOptions.Compiled);
        public static readonly Regex Persondata = Tools.NestedTemplateRegex(@"Persondata");
        public const string PersonDataCommentEN = @"<!-- Metadata: see [[Wikipedia:Persondata]] -->
";
        public static readonly Regex DeathsOrLivingCategory = new Regex(@"\[\[\s*Category *:[ _]?(\d{1,2}\w{0,2}[- _]century(?: BC)?[ _]deaths|[0-9s]{3,5}(?: BC)?[ _]deaths|Disappeared[ _]people|Living[ _]people|Year[ _]of[ _]death[ _](?:missing|unknown)|Possibly[ _]living[ _]people)", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static readonly Regex LivingPeopleRegex2 = new Regex(@"\{\{\s*(Template:)?(Recent ?death|Recentlydeceased)\}\}", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static readonly Regex BirthsCategory = new Regex(@"\[\[ ?Category ?:[ _]?(?:(\d{3,4})(?:s| BC)?|\d{1,2}\w{0,2}[- _]century)[ _]births(\|.*?)?\]\]", RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static readonly Regex DateBirthAndAge = new Regex(@"{{\s*[Bb](?:(?:da|irth date(?: and age)?)\s*(?:\|\s*[md]f\s*=\s*y(?:es)?\s*)?\|\s*|irth-date\s*\|[^{}\|]*?)(?:year *= *)?\b([12]\d{3})\s*(?:\||}})");
        public static readonly Regex DeathDate = new Regex(@"{{\s*[Dd]eath(?: date(?: and age)?\s*(?:\|\s*[md]f\s*=\s*y(?:es)?\s*)?\|\s*|-date\s*\|[^{}\|]*?)(?:year *= *)?\b([12]\d{3})\s*(?:\||}})");
        public static readonly Regex DeathDateAndAge = new Regex(@"{{\s*[Dd](?:eath[ -]date and age|da)\s*\|(?:[^{}]*?\|)?\s*([12]\d{3})\s*\|[^{}]+?\|\s*([12]\d{3})\s*\|");
        public static readonly Regex LinkFGAs = new Regex(@"{{\s*[Ll]ink (?:[FG]A|FL)\|.*?}}", RegexOptions.Compiled | RegexOptions.RightToLeft);
        public static readonly Regex LinkFGAsFrench = new Regex(@"{{\s*[Ll]ien (?:BA|[PA]dQ)\|.*?}}", RegexOptions.Compiled | RegexOptions.RightToLeft);
        public static readonly Regex LinkFGAsItalian = new Regex(@"{{\s*[Ll]ink (FA|AdQ)\|.*?}}", RegexOptions.Compiled | RegexOptions.RightToLeft);
        public static readonly Regex LinkFGAsSpanish = new Regex(@"{{\s*([Ll]ink FA|[Dd]estacado)\|.*?}}", RegexOptions.Compiled | RegexOptions.RightToLeft);
        public static readonly Regex LinkFGAsCatalan = new Regex(@"{{\s*([Ll]ink FA|[Ee]nllaÃ§ AD)\|.*?}}", RegexOptions.Compiled | RegexOptions.RightToLeft);
        public static readonly Regex LinkFGAsArabic = new Regex(@"{{\s*ÙØµÙØ© ÙÙØ§ÙØ© ÙØ®ØªØ§Ø±Ø©\s*\|.*?}}", RegexOptions.Compiled | RegexOptions.Singleline);
        public static readonly Regex DeadEnd = new Regex(@"({{\s*([Dd]ead ?end|[Ii]nternal ?links|[Nn]uevointernallinks|[Dd]ep)(\|(?:[^{}]+|date={{subst:CURRENTMONTHNAME}} {{subst:CURRENTYEAR}}))?}}|(?<={{\s*(?:[Aa]rticle|[Mm]ultiple)\s*issues\b[^{}]*?)\|\s*dead ?end\s*=[^{}\|]+)", RegexOptions.Compiled);
        public static Regex Wikify;
        public static readonly Regex DeadLink = Tools.NestedTemplateRegex(new List<string>("dead link,deadlink,broken link,brokenlink,link broken,linkbroken,404,dl,dl-s,cleanup-link".Split(',')));
        public static readonly Regex Expand = new Regex(@"({{\s*(?:Expand-?article|Expand|Develop|Elaborate|Expansion)(?:\s*\|\s*(?:date={{subst:CURRENTMONTHNAME}} {{subst:CURRENTYEAR}}|.*?))?}}|(?<={{(?:Article|Multiple)\s*issues\b[^{}]*?)\|\s*expand\s*=[^{}\|]+)", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        public static Regex Orphan;
        public static Regex Uncat;
        public static readonly Regex ArticleIssues = Tools.NestedTemplateRegex(new List<string>(@"article issues,articleissues,multipleissues,multiple issues".Split(',')));
        public static readonly Regex NewUnReviewedArticle = Tools.NestedTemplateRegex("new unreviewed article");
        public const string ArticleIssuesTemplatesString = @"(3O|[Aa]dvert|[Aa]utobiography|[Bb]iased|[Bb]lpdispute|BLPrefimprove|BLPsources|BLPunref(?:erenced)?|BLPunsourced|[Cc]itations missing|[Cc]itationstyle|[Cc]itecheck|[Cc]leanup|COI|[Cc]oi|[Cc]olloquial|[Cc]onfusing|[Cc]ontext|[Cc]ontradict|[Cc]opyedit|[Cc]riticisms|[Cc]rystal|[Dd]ead ?end|[Dd]isputed|[Dd]o-attempt|[Ee]ssay(?:\-like)?|[Ee]xamplefarm|[Ee]xpand|[Ee]xpert|[Ee]xternal links|[Ff]ancruft|[Ff]ansite|[Ff]iction|[Gg]ameguide|[Gg]lobalize|[Gg]rammar|[Hh]istinfo|[Hh]oax|[Hh]owto|[Ii]nappropriate person|[Ii]n-universe|[Ii]ncomplete|[Ii]ntro(?: length|\-too(?:long|short))|[Ii]ntromissing|[Ii]ntrorewrite|[Jj]argon|[Ll]aundry(?:lists)?|[Ll]ike ?resume|[Ll]ong|[Nn]ewsrelease|[Nn]otab(?:le|ility)|[Oo]nesource|[Oo]riginal[ -][Rr]esearch|[Oo]rphan|[Oo]ut of date|[Pp]eacock|[Pp]lot|N?POV|n?pov|[Pp]rimarysources|[Pp]rose(?:line)?|[Qq]uotefarm|[Rr]ecent(?:ism)?|[Rr]efimprove(?:BLP)?|[Rr]estructure|[Rr]eview|[Rr]ewrite|[Rr]oughtranslation|[Ss]ections|[Ss]elf-published|[Ss]pam|[Ss]tory|[Ss]ynthesis|[Tt]echnical|[Ii]nappropriate tone|[Tt]one|[Tt]oo(?:short|long)|[Tt]ravelguide|[Tt]rivia|[Uu]nbalanced|[Uu]nencyclopedic|[Uu]nref(?:erenced(?:BLP)?|BLP)?|[Uu]pdate|[Ww]easel|[Ww]ikify)";
        public static readonly Regex ArticleIssuesTemplateNameRegex = new Regex(ArticleIssuesTemplatesString, RegexOptions.Compiled);
        public static readonly Regex CoiOrPovBlp = new Regex("(COI|OR|POV|BLP)", RegexOptions.Compiled);
        public static string DateYearMonthParameter;
        public static readonly Regex ArticleIssuesTemplates = new Regex(@"{{" + ArticleIssuesTemplatesString + @"\s*(?:\|\s*([^{}\|]*?(?:{{subst:CURRENTMONTHNAME}} {{subst:CURRENTYEAR}}[^{}\|]*?)?))?\s*}}");
        public static readonly Regex ReferenceList = new Regex(@"{{\s*(reflist|references-small|references-2column)}}", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.RightToLeft);
        public static readonly Regex InfoBox = new Regex(@"{{\s*([Ii]nfobox[\s_][^{}\|]+?|[^{}\|]+?[Ii]nfobox)\s*\|(?>[^\{\}]+|\{(?<DEPTH>)|\}(?<-DEPTH>))*(?(DEPTH)(?!))}}");
        public static readonly Regex CircaTemplate = Tools.NestedTemplateRegex(@"Circa");
        public static readonly Regex NamedReferences = new Regex(@"(<\s*ref\s+name\s*=\s*(?:""|')?([^<>=\r\n/]+?)(?:""|')?\s*>\s*(.+?)\s*<\s*/\s*ref\s*>)", RegexOptions.Compiled | RegexOptions.Singleline | RegexOptions.IgnoreCase);
        public static readonly Regex Dablinks = new Regex(@"{{\s*(?:[Aa]bout|[Ff]or[2-3]?|[Dd]ablink|[Dd]istinguish2?|[Oo]therpeople[1-4]|[Oo]ther ?persons|[Oo]ther ?places[23]?|[Oo]ther ?ships|[Oo]theruses-number|[Oo]ther ?use(?:s[2-5]|s)?|2otheruses|[Rr]edirect-acronym|[Rr]edirect[2-4]?|[Aa]mbiguous link|[Dd]isambig-acronym)\s*(?:\|[^{}]*(?:{{[^{}]*}}[^{}]*)?)?}}", RegexOptions.Compiled);
        public static readonly Regex MaintenanceTemplates = Tools.NestedTemplateRegex(new List<string>(@"orphan,BLPunsourced,cleanup".Split(',')));
        public static readonly Regex Unreferenced = new Regex(@"{{\s*([Uu]nreferenced( stub)?|[Uu]nsourced|[Uu]nverified|[Uu]nref|[Rr]eferences|[Uu]ncited-article|[Cc]itesources|[Nn][Rr]|[Nn]o references|[Uu]nrefarticle|[Nn]o ?refs?|[Nn]oreferences|[Cc]leanup-cite|[Rr]eferences needed)\s*(?:\|.*?)?}}", RegexOptions.Singleline);
        public static readonly Regex PortalTemplate = new Regex(@"{{\s*[Pp]ortal(?:par)?(?:\|[^{}]+)?}}", RegexOptions.RightToLeft);
        public static readonly Regex Comments = new Regex(@"<!--.*?-->", RegexOptions.Compiled | RegexOptions.Singleline);
        public static readonly Regex Pstyles = new Regex(@"<p style\s*=\s*[^<>]+>.*?<\s*/p>", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static readonly Regex EmptyComments = new Regex(@"<!--[^\S\r\n]*-->", RegexOptions.Compiled);
        public static readonly Regex Refs = new Regex(@"(<ref\s+(?:name|group)\s*=\s*[^<>]*?/\s*>|<ref\b[^>/]*?>.*?<\s*/\s*ref\s*>)", RegexOptions.Singleline | RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static readonly Regex Cites = new Regex(@"<cite[^>]*?>[^<]*<\s*/cite\s*>", RegexOptions.Compiled | RegexOptions.Singleline);
        public static readonly Regex Nowiki = new Regex("<nowiki>.*?</nowiki>", RegexOptions.Singleline | RegexOptions.IgnoreCase | RegexOptions.Compiled);
        public static Regex TemplateCall;
        public static readonly Regex NoGeneralFixes = new Regex("<!--No general fixes:.*?-->", RegexOptions.Singleline | RegexOptions.Compiled);
        public static readonly Regex NoRETF = new Regex("<!--No RETF:.*?-->", RegexOptions.Singleline | RegexOptions.Compiled);
        public static Regex ExtractTitle;
        public static Regex EmptyLink;
        public static Regex EmptyTemplate;
        public static readonly Regex BoldItalics = new Regex(@"'''''(.+?)'''''");
        public static readonly Regex Italics = new Regex(@"''(.+?)''");
        public static readonly Regex Bold = new Regex(@"'''(.+?)'''");
        public static readonly Regex StarRows = new Regex(@"^ *(\*)(.*)", RegexOptions.Multiline);
        public static readonly Regex ReferencesRegex = new Regex(@"== *References *==", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.RightToLeft);
        public static readonly Regex ExternalLinksHeaderRegex = new Regex(@"== *External +links? *==", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.RightToLeft);
        public static readonly Regex ArticleIssuesInTitleCase = new Regex(@"({{\s*(?:[Aa]rticle|[Mm]ultiple) ?issues\|\s*(?:[^{}]+?\|\s*)?)([A-Z])([a-z]+(?: [a-z]+)?\s*=)", RegexOptions.Compiled);
        public static readonly Regex ArticleIssuesRegexExpert = new Regex(@"{{\s*(?:[Aa]rticle|[Mm]ultiple) ?issues[^{}]+?expert", RegexOptions.Compiled);
        public static readonly Regex ArticleIssuesRegexWithDate = new Regex(@"({{\s*(?:[Aa]rticle|[Mm]ultiple) ?issues\s*(?:\|[^{}]*?)?)\|\s*date\s*=[^{}\|]{0,20}?(\||}})", RegexOptions.Compiled);
        public static readonly Regex GregorianYear = new Regex(@"\b[12]\d{3}\b", RegexOptions.Compiled);
        public static readonly Regex SuperscriptedPunctuationBetweenRefs = new Regex("(</ref>|<ref[^>]*?/>)<sup>[ ]*[,;-]?[ ]*</sup><ref", RegexOptions.Compiled);
        private const string FactTag = "{{[ ]*(fact|fact[ ]*[\\|][^}]*|facts|citequote|citation needed|cn|verification needed|verify source|verify credibility|who|failed verification|nonspecific|dubious|or|lopsided|GR[ ]*[\\|][ ]*[^ ]+|[c]?r[e]?f[ ]*[\\|][^}]*|ref[ _]label[ ]*[\\|][^}]*|ref[ _]num[ ]*[\\|][^}]*)[ ]*}}";
        public static readonly Regex WhiteSpaceFactTag = new Regex("[\\n\\r\\f\\t ]+?" + FactTag, RegexOptions.Compiled);
        private const string LacksPunctuation = "([^\\.,;:!\\?\"'â])";
        private const string QuestionOrExclam = "([!\\?])";
        private const string AnyPunctuation = "([\\.,;:!\\?])";
        private const string MajorPunctuation = "([,;:!\\?])";
        private const string Period = "([\\.])";
        private const string Quote = "([\"'â]*)";
        private const string Space = "[ ]*";
        private const string RefTag =
            "(<ref>([^<]|<[^/]|</[^r]|</r[^e]|</re[^f]|</ref[^>])*?</ref>" +
            "|<ref[^>]*?[^/]>([^<]|<[^/]|</[^r]|</r[^e]|</re[^f]" + "|</ref[^>])*?</ref>|<ref[^>]*?/>)";
        public static readonly Regex Match0A = new Regex(LacksPunctuation + Quote + FactTag + Space + AnyPunctuation, RegexOptions.Compiled);
        public static readonly Regex Match0B = new Regex(QuestionOrExclam + Quote + FactTag + Space + MajorPunctuation, RegexOptions.Compiled);
        public static readonly Regex Match0D = new Regex(QuestionOrExclam + Quote + FactTag + Space + Period, RegexOptions.Compiled);
        public static readonly Regex Match1A = new Regex(LacksPunctuation + Quote + RefTag + Space + AnyPunctuation, RegexOptions.Compiled);
        public static readonly Regex Match1B = new Regex(QuestionOrExclam + Quote + RefTag + Space + MajorPunctuation, RegexOptions.Compiled);
        public static readonly Regex Match1D = new Regex(QuestionOrExclam + Quote + RefTag + Space + Period, RegexOptions.Compiled);
        public static readonly Regex IbidOpCitation = new Regex(@"\b(ibid|op.{1,4}cit)\b", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static readonly Regex Ibid = Tools.NestedTemplateRegex(@"Ibid");
        public static readonly Regex InUse = Tools.NestedTemplateRegex(@"Inuse");
        public static readonly Regex WhiteSpace = new Regex(@"\s+", RegexOptions.Compiled);
        public static readonly Regex UrlValidator =
            new Regex(
                @"^(http|https|ftp)\://[a-zA-Z0-9\-\.]+\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\-\._\?\,\'/\\\+&amp;%\$#\=~])*[^\.\,\)\(\s]$",
                RegexOptions.Compiled);
    }
}

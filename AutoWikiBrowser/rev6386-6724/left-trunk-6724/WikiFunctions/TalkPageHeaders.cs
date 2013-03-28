using System.Text.RegularExpressions;
using WikiFunctions.Logging;
namespace WikiFunctions.TalkPages
{
    public enum DEFAULTSORT
    {
        NoChange,
        MoveToTop,
        MoveToBottom
    }
    internal class Processor
    {
        public string DefaultSortKey;
        public bool FoundDefaultSort;
        public bool FoundSkipToTalk;
        public bool FoundTalkHeader;
        public string DefaultSortMatchEvaluator(Match match)
        {
            FoundDefaultSort = true;
            if (match.Groups["key"].Captures.Count > 0)
                DefaultSortKey = match.Groups["key"].Captures[0].Value.Trim();
            return "";
        }
        public string SkipTOCMatchEvaluator(Match match)
        {
            FoundSkipToTalk = true;
            return "";
        }
        public string TalkHeaderMatchEvaluator(Match match)
        {
            FoundTalkHeader = true;
            return "";
        }
    }
    public static class TalkPageHeaders
    {
        public static bool ContainsDefaultSortKeywordOrTemplate(string articleText)
        {
            return WikiRegexes.Defaultsort.IsMatch(articleText);
        }
        public static bool ProcessTalkPage(ref string articleText, ref string summary, DEFAULTSORT moveDefaultsort)
        {
            Processor pr = new Processor();
            articleText = WikiRegexes.SkipTOCTemplateRegex.Replace(articleText, new MatchEvaluator(pr.SkipTOCMatchEvaluator), 1);
            articleText = MoveTalkHeader(articleText, ref summary);
            if (pr.FoundSkipToTalk)
                WriteHeaderTemplate("Skip to talk", ref articleText, ref summary);
            if (moveDefaultsort != DEFAULTSORT.NoChange)
            {
                articleText = WikiRegexes.Defaultsort.Replace(articleText,
                    new MatchEvaluator(pr.DefaultSortMatchEvaluator), 1);
                if (pr.FoundDefaultSort)
                {
                    if (string.IsNullOrEmpty(pr.DefaultSortKey))
                    {
                        AppendToSummary(ref summary, "DEFAULTSORT has no key; removed");
                    }
                    else
                    {
                        articleText = SetDefaultSort(pr.DefaultSortKey, moveDefaultsort, articleText, ref summary);
                    }
                }
            }
            articleText = AddMissingFirstCommentHeader(articleText, ref summary);
            return pr.FoundTalkHeader || pr.FoundSkipToTalk || pr.FoundDefaultSort;
        }
        public static string FormatDefaultSort(string articleText)
        {
            return WikiRegexes.Defaultsort.Replace(articleText, "{{DEFAULTSORT:${key}}}");
        }
        private static void AppendToSummary(ref string summary, string newText)
        {
            if (!string.IsNullOrEmpty(summary))
                summary += ", ";
            summary += newText;
        }
        private static string SetDefaultSort(string key, DEFAULTSORT location, string articleText, ref string summary)
        {
            switch (location)
            {
                case DEFAULTSORT.MoveToTop:
                    return "{{DEFAULTSORT:" + key + "}}\r\n" + articleText;
                case DEFAULTSORT.MoveToBottom:
                    return articleText + "\r\n{{DEFAULTSORT:" + key + "}}";
            }
            return "";
        }
        private static void WriteHeaderTemplate(string name, ref string articleText, ref string summary)
        {
            articleText = "{{" + name + "}}\r\n" + articleText;
            AppendToSummary(ref summary, "{{" + name + "}} given top billing");
        }
        private static string MoveTalkHeader(string articleText, ref string summary)
        {
            Match m = WikiRegexes.TalkHeaderTemplate.Match(articleText);
            if(m.Success && m.Index > 0)
            {
                if(articleText.Contains("\r\n" + m.Value))
                    articleText = articleText.Replace(m.Value, "");
                else
                    articleText = articleText.Replace(m.Value, "\r\n");
                articleText = m.Value.TrimEnd() + "\r\n" + articleText.TrimStart();
                articleText = articleText.Replace(m.Groups[1].Value, "Talk header");
                AppendToSummary(ref summary, "{{Talk header}} given top billing");
            }
            return articleText;
        }
        private static readonly Regex FirstComment = new Regex(@"^ {0,4}[:\*\w'""](?<!_)", RegexOptions.Compiled | RegexOptions.Multiline);
        private static string AddMissingFirstCommentHeader(string articleText, ref string summary)
        {
            string articleTextTemplatesSpaced = Tools.ReplaceWithSpaces(articleText, WikiRegexes.NestedTemplates.Matches(articleText));
            if(FirstComment.IsMatch(articleTextTemplatesSpaced))
            {
                int firstCommentIndex = FirstComment.Match(articleTextTemplatesSpaced).Index;
                int firstLevelTwoHeading = WikiRegexes.HeadingLevelTwo.IsMatch(articleText) ? WikiRegexes.HeadingLevelTwo.Match(articleText).Index : 99999999;
                if (firstCommentIndex < firstLevelTwoHeading)
                {
                    string articletexttofirstcomment = articleText.Substring(0, firstCommentIndex);
                    if(WikiRegexes.HeadingLevelThree.IsMatch(articletexttofirstcomment))
                    {
                        AppendToSummary(ref summary, "Corrected comments section header");
                        articleText = WikiRegexes.HeadingLevelThree.Replace(articleText, @"==$1==", 1);
                    }
                    else
                    {
                        AppendToSummary(ref summary, "Added missing comments section header");
                        articleText = articleText.Insert(firstCommentIndex, "\r\n==Untitled==\r\n");
                    }
                }
            }
            return articleText;
        }
    }
}

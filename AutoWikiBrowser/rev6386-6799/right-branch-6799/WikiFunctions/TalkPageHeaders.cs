using System.Text.RegularExpressions;
using System.Collections.Generic;
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
        public static bool ProcessTalkPage(ref string articleText, DEFAULTSORT moveDefaultsort)
        {
            Processor pr = new Processor();
            articleText = WikiRegexes.SkipTOCTemplateRegex.Replace(articleText, new MatchEvaluator(pr.SkipTOCMatchEvaluator), 1);
            articleText = MoveTalkHeader(articleText);
            if (pr.FoundSkipToTalk)
                WriteHeaderTemplate("Skip to talk", ref articleText);
            if (moveDefaultsort != DEFAULTSORT.NoChange)
            {
                articleText = WikiRegexes.Defaultsort.Replace(articleText,
                    new MatchEvaluator(pr.DefaultSortMatchEvaluator), 1);
                if (pr.FoundDefaultSort)
                {
                    if (!string.IsNullOrEmpty(pr.DefaultSortKey))
                    {
                        articleText = SetDefaultSort(pr.DefaultSortKey, moveDefaultsort, articleText);
                    }
                }
            }
            articleText = AddMissingFirstCommentHeader(articleText);
            return pr.FoundTalkHeader || pr.FoundSkipToTalk || pr.FoundDefaultSort;
        }
        public static string FormatDefaultSort(string articleText)
        {
            return WikiRegexes.Defaultsort.Replace(articleText, "{{DEFAULTSORT:${key}}}");
        }
        private static string SetDefaultSort(string key, DEFAULTSORT location, string articleText)
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
        private static void WriteHeaderTemplate(string name, ref string articleText)
        {
            articleText = "{{" + name + "}}\r\n" + articleText;
        }
        private static string MoveTalkHeader(string articleText)
        {
            Match m = WikiRegexes.TalkHeaderTemplate.Match(articleText);
            if(m.Success && m.Index > 0)
            {
                articleText = articleText.Replace(m.Value, articleText.Contains("\r\n" + m.Value) ? "" : "\r\n");
                articleText = m.Value.TrimEnd() + "\r\n" + articleText.TrimStart();
                articleText = articleText.Replace(m.Groups[1].Value, "Talk header");
            }
            return articleText;
        }
        private static readonly Regex FirstComment = new Regex(@"^ {0,4}[:\*\w'""](?<!_)", RegexOptions.Compiled | RegexOptions.Multiline);
        private static string AddMissingFirstCommentHeader(string articleText)
        {
            string articleTextTemplatesSpaced = Tools.ReplaceWithSpaces(articleText, WikiRegexes.NestedTemplates.Matches(articleText));
            if(FirstComment.IsMatch(articleTextTemplatesSpaced))
            {
                int firstCommentIndex = FirstComment.Match(articleTextTemplatesSpaced).Index;
                int firstLevelTwoHeading = WikiRegexes.HeadingLevelTwo.IsMatch(articleText) ? WikiRegexes.HeadingLevelTwo.Match(articleText).Index : 99999999;
                if (firstCommentIndex < firstLevelTwoHeading)
                {
                    string articletexttofirstcomment = articleText.Substring(0, firstCommentIndex);
                    articleText = WikiRegexes.HeadingLevelThree.IsMatch(articletexttofirstcomment) ? WikiRegexes.HeadingLevelThree.Replace(articleText, @"==$1==", 1) : articleText.Insert(firstCommentIndex, "\r\n==Untitled==\r\n");
                }
            }
            return articleText;
        }
        private static List<string> BannerShellRedirects = new List<string>(new [] { "WikiProject Banners", "WikiProjectBanners", "WPBS", "WPB", "Wpb", "Wpbs"});
        public static string WikiProjectBannerShell(string articletext)
        {
            foreach(string redirect in BannerShellRedirects)
                articletext = Tools.RenameTemplate(articletext, redirect, "WikiProjectBannerShell");
            return articletext;
        }
    }
}

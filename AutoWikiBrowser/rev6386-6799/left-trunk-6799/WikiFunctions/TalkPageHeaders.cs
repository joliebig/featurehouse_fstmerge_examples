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
        articleText = WikiProjectBannerShell(articleText);
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
        private static List<string> Nos = new List<string>(new [] {"blp", "activepol", "collapsed"});
        private static readonly Regex BLPRegex = Tools.NestedTemplateRegex(new [] { "blp", "BLP", "Blpinfo" });
        public static string WikiProjectBannerShell(string articletext)
        {
            if(!WikiRegexes.WikiProjectBannerShellTemplate.IsMatch(articletext))
                return articletext;
            foreach(string redirect in BannerShellRedirects)
                articletext = Tools.RenameTemplate(articletext, redirect, "WikiProjectBannerShell");
            foreach (Match m in WikiRegexes.WikiProjectBannerShellTemplate.Matches(articletext))
            {
                string newValue = Tools.RemoveDuplicateTemplateParameters(m.Value);
                foreach(string theNo in Nos)
                {
                    if(Tools.GetTemplateParameterValue(newValue, theNo).Equals("no"))
                        newValue = Tools.RemoveTemplateParameter(newValue, theNo);
                }
                Match blpm = BLPRegex.Match(articletext);
                if(blpm.Success)
                {
                    newValue = Tools.SetTemplateParameterValue(newValue, "blp", "yes");
                    articletext = articletext.Replace(blpm.Value, "");
                }
                string arg1 = Tools.GetTemplateParameterValue(newValue, "1");
                Match m2 = Tools.NestedTemplateRegex("WPBiography").Match(arg1);
                if(m2.Success)
                {
                    string WPBiographyCall = m2.Value;
                    string livingParam = Tools.GetTemplateParameterValue(WPBiographyCall, "living");
                    if(livingParam.Equals("yes"))
                        newValue = Tools.SetTemplateParameterValue(newValue, "blp", "yes");
                    else if (livingParam.Equals("no"))
                    {
                        if(Tools.GetTemplateParameterValue(newValue, "blp").Equals("yes"))
                            newValue = Tools.RemoveTemplateParameter(newValue, "blp");
                    }
                    if(Tools.GetTemplateParameterValue(WPBiographyCall, "activepol").Equals("yes"))
                        newValue = Tools.SetTemplateParameterValue(newValue, "activepol", "yes");
                    if(Tools.GetTemplateParameterValue(WPBiographyCall, "blpo").Equals("yes"))
                        newValue = Tools.SetTemplateParameterValue(newValue, "blpo", "yes");
                }
                if(arg1.Length == 0)
                {
                    int argCount = Tools.GetTemplateArgumentCount(newValue);
                    for(int arg = 1; arg <= argCount; arg++)
                    {
                        string argValue = Tools.GetTemplateArgument(newValue, arg);
                        if(argValue.StartsWith(@"{{"))
                        {
                            newValue = newValue.Insert(Tools.GetTemplateArgumentIndex(newValue, arg), "1=");
                            break;
                        }
                    }
                }
                if(!newValue.Equals(m.Value))
                    articletext = articletext.Replace(m.Value, newValue);
            }
            return articletext;
        }
    }
}

using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using WikiFunctions.Parse;
namespace WikiFunctions
{
    public static class Summary
    {
        public const int MaxLength = 255;
        public static string ModifiedSection(string originalText, string articleText)
        {
            var sectionsBefore = Tools.SplitToSections(originalText);
            if (sectionsBefore.Length == 0)
                return "";
            var sectionsAfter = Tools.SplitToSections(articleText);
            if (sectionsAfter.Length != sectionsBefore.Length)
                return "";
            int sectionsChanged = 0, sectionChangeNumber = 0;
            for (int i = 0; i < sectionsAfter.Length; i++)
            {
                if (sectionsBefore[i] != sectionsAfter[i])
                {
                    sectionsChanged++;
                    sectionChangeNumber = i;
                }
                if (sectionsChanged > 1)
                    return "";
            }
            return WikiRegexes.Headings.Match(sectionsAfter[sectionChangeNumber]).Groups[1].Value.Trim();
        }
        public static string Trim(string summary)
        {
            int maxAvailableSummaryLength = ((MaxLength - 5) - (Variables.SummaryTag.Length + 1));
            if (Encoding.UTF8.GetByteCount(summary) >= maxAvailableSummaryLength && summary.EndsWith(@"]]"))
                summary = Regex.Replace(summary, @"\s*\[\[[^\[\]\r\n]+?\]\]$", "...");
            return (Encoding.UTF8.GetByteCount(summary) > maxAvailableSummaryLength)
                       ? LimitByteLength(summary, maxAvailableSummaryLength)
                       : summary;
        }
        public static bool IsCorrect(string s)
        {
            if (Encoding.UTF8.GetByteCount(s) > MaxLength)
                return false;
            bool res = true;
            int pos = s.IndexOf("[[");
            while (pos >= 0)
            {
                s = s.Remove(0, pos);
                pos = res ? s.IndexOf("]]") : s.IndexOf("[[");
                res = !res;
            }
            return res;
        }
        private static string LimitByteLength(string input, int maxLength)
        {
            for (int i = input.Length - 1; i >= 0; i--)
            {
                if (Encoding.UTF8.GetByteCount(input.Substring(0, i + 1)) <= maxLength)
                {
                    return input.Substring(0, i + 1);
                }
            }
            return string.Empty;
        }
    }
}

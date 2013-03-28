using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using System.Collections;
namespace WikiFunctions.Parse
{
    public sealed class HideText
    {
        public HideText() { }
        public HideText(bool hideExternalLinks, bool leaveMetaHeadings, bool hideImages)
        {
            HideExternalLinks = hideExternalLinks;
            LeaveMetaHeadings = leaveMetaHeadings;
            HideImages = hideImages;
        }
        private readonly bool LeaveMetaHeadings, HideImages, HideExternalLinks;
        private static readonly Regex NoWikiIgnoreRegex = new Regex("<!-- ?(cat(egories)?|\\{\\{.*?stub\\}\\}.*?|other languages?|language links?|inter ?(language|wiki)? ?links|inter ?wiki ?language ?links|inter ?wikis?|The below are interlanguage links\\.?) ?-->", RegexOptions.Compiled | RegexOptions.IgnoreCase);
        private readonly List<HideObject> HiddenTokens = new List<HideObject>();
        private void Replace(IEnumerable matches, ref string articleText)
        {
            StringBuilder sb = new StringBuilder((int)(articleText.Length * 1.1));
            int pos = 0;
            foreach (Match m in matches)
            {
                sb.Append(articleText, pos, m.Index - pos);
                string s = "ââââ" + HiddenTokens.Count + "ââââ";
                sb.Append(s);
                pos = m.Index + m.Value.Length;
                HiddenTokens.Add(new HideObject(s, m.Value));
            }
            sb.Append(articleText, pos, articleText.Length - pos);
            articleText = sb.ToString();
        }
        public string Hide(string articleText)
        {
            HiddenTokens.Clear();
            Replace(WikiRegexes.Source.Matches(articleText), ref articleText);
            var matches = new List<Match>();
            foreach (Match m in WikiRegexes.UnformattedText.Matches(articleText))
            {
                if (LeaveMetaHeadings && NoWikiIgnoreRegex.IsMatch(m.Value))
                    continue;
                matches.Add(m);
            }
            Replace(matches, ref articleText);
            if (HideExternalLinks)
            {
                Replace(WikiRegexes.ExternalLinks.Matches(articleText), ref articleText);
                List<Match> matches2 = new List<Match>();
                foreach (Match m in WikiRegexes.PossibleInterwikis.Matches(articleText))
                {
                    if (SiteMatrix.Languages.Contains(m.Groups[1].Value.ToLower()))
                        matches2.Add(m);
                }
                Replace(matches2, ref articleText);
            }
            if (HideImages)
            {
                Replace(WikiRegexes.Images.Matches(articleText), ref articleText);
            }
            return articleText;
        }
        private static readonly Regex HiddenRegex = new Regex("ââââ(\\d*)ââââ", RegexOptions.Compiled);
        public string AddBack(string articleText)
        {
            MatchCollection mc;
            while ((mc = HiddenRegex.Matches(articleText)).Count > 0)
            {
                StringBuilder sb = new StringBuilder(articleText.Length * 2);
                int pos = 0;
                foreach (Match m in mc)
                {
                    sb.Append(articleText, pos, m.Index - pos);
                    sb.Append(HiddenTokens[int.Parse(m.Groups[1].Value)].Text);
                    pos = m.Index + m.Value.Length;
                }
                sb.Append(articleText, pos, articleText.Length - pos);
                articleText = sb.ToString();
            }
            HiddenTokens.Clear();
            return articleText;
        }
        private readonly List<HideObject> HiddenUnformattedText = new List<HideObject>();
        public string HideUnformatted(string articleText)
        {
            HiddenUnformattedText.Clear();
            int i = 0;
            foreach (Match m in WikiRegexes.UnformattedText.Matches(articleText))
            {
                string s = "ââââ" + i + "ââââ";
                articleText = articleText.Replace(m.Value, s);
                HiddenUnformattedText.Add(new HideObject(s, m.Value));
                i++;
            }
            return articleText;
        }
        public string AddBackUnformatted(string articleText)
        {
            HiddenUnformattedText.Reverse();
            foreach (HideObject k in HiddenUnformattedText)
                articleText = articleText.Replace(k.Code, k.Text);
            HiddenUnformattedText.Clear();
            return articleText;
        }
        private readonly List<HideObject> MoreHide = new List<HideObject>(32);
        private void ReplaceMore(ICollection matches, ref string articleText)
        {
            StringBuilder sb = new StringBuilder((int)(articleText.Length * 1.1));
            int pos = 0;
            foreach (Match m in matches)
            {
                sb.Append(articleText, pos, m.Index - pos);
                string s = "ââââM" + MoreHide.Count + "ââââ";
                sb.Append(s);
                pos = m.Index + m.Value.Length;
                MoreHide.Add(new HideObject(s, m.Value));
            }
            sb.Append(articleText, pos, articleText.Length - pos);
            articleText = sb.ToString();
        }
        public string HideMore(string articleText)
        {
            return HideMore(articleText, false, true);
        }
        public string HideMore(string articleText, bool hideOnlyTargetOfWikilink)
        {
            return HideMore(articleText, hideOnlyTargetOfWikilink, true);
        }
        public string HideMore(string articleText, bool hideOnlyTargetOfWikilink, bool hideWikiLinks)
        {
            MoreHide.Clear();
            ReplaceMore(WikiRegexes.NestedTemplates.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.Blockquote.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.Source.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.Code.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.Noinclude.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.Includeonly.Matches(articleText), ref articleText);
            if (HideExternalLinks) ReplaceMore(WikiRegexes.ExternalLinks.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.Headings.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.UnformattedText.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.IndentedText.Matches(articleText), ref articleText);
            if(hideWikiLinks)
                ReplaceMore(WikiRegexes.WikiLinksOnlyPlusWord.Matches(articleText), ref articleText);
            if (!hideOnlyTargetOfWikilink && hideWikiLinks)
            {
                ReplaceMore(WikiRegexes.WikiLinksOnly.Matches(articleText), ref articleText);
                ReplaceMore(WikiRegexes.SimpleWikiLink.Matches(articleText), ref articleText);
            }
            ReplaceMore(WikiRegexes.Cites.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.Refs.Matches(articleText), ref articleText);
            if(hideWikiLinks)
                ReplaceMore(WikiRegexes.WikiLink.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.Images.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.UntemplatedQuotes.Matches(articleText), ref articleText);
            ReplaceMore(WikiRegexes.Pstyles.Matches(articleText), ref articleText);
            return articleText;
        }
        private static readonly Regex HiddenMoreRegex = new Regex("ââââM(\\d*)ââââ", RegexOptions.Compiled);
        public string AddBackMore(string articleText)
        {
            MatchCollection mc;
            while ((mc = HiddenMoreRegex.Matches(articleText)).Count > 0)
            {
                StringBuilder sb = new StringBuilder(articleText.Length * 2);
                int pos = 0;
                foreach (Match m in mc)
                {
                    sb.Append(articleText, pos, m.Index - pos);
                    sb.Append(MoreHide[int.Parse(m.Groups[1].Value)].Text);
                    pos = m.Index + m.Value.Length;
                }
                sb.Append(articleText, pos, articleText.Length - pos);
                articleText = sb.ToString();
            }
            MoreHide.Clear();
            return articleText;
        }
    }
    struct HideObject
    {
        public HideObject(string code, string text)
        {
            Code = code;
            Text = text;
        }
        public readonly string Code, Text;
        public override string ToString()
        {
            return Code + " --> " + Text;
        }
    }
}

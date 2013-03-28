using System;
using System.Collections.Specialized;
using System.Collections.Generic;
using System.Globalization;
using System.Text;
using System.Net;
using System.Web;
using System.IO;
using System.Text.RegularExpressions;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using WikiFunctions.Parse;
namespace WikiFunctions
{
    public static class Tools
    {
        static Tools()
        {
            DefaultUserAgentString = string.Format("WikiFunctions/{0} ({1}; .NET CLR {2})",
                                                   VersionString,
                                                   Environment.OSVersion.VersionString,
                                                   Environment.Version);
        }
        public delegate void SetProgress(int percent);
        public static Version Version
        { get { return System.Reflection.Assembly.GetExecutingAssembly().GetName().Version; } }
        public static string VersionString
        { get { return Version.ToString(); } }
        public static string DefaultUserAgentString
        { get; private set; }
        public static void About()
        {
            new Controls.AboutBox().Show();
        }
        public static bool IsRedirect(string articletext)
        {
            return (RedirectTarget(articletext).Length > 0);
        }
        public static string RedirectTarget(string articleText)
        {
            Match m = WikiRegexes.Redirect.Match(WikiRegexes.UnformattedText.Replace(FirstChars(articleText, 512), ""));
            return WikiDecode(m.Groups[1].Value).Trim();
        }
        public static bool IsWikimediaProject(ProjectEnum p)
        {
            return (p != ProjectEnum.custom && p != ProjectEnum.wikia);
        }
        private readonly static char[] InvalidChars = new[] { '[', ']', '{', '}', '|', '<', '>', '#' };
        public static bool IsValidTitle(string articleTitle)
        {
            articleTitle = WikiDecode(articleTitle).Trim();
            if (articleTitle.Length == 0) return false;
            if (articleTitle.IndexOfAny(InvalidChars) >= 0)
                return false;
            articleTitle = Parsers.CanonicalizeTitleAggressively(articleTitle);
            var a = new Article(articleTitle);
            var name = a.NamespacelessName;
            return name.Length > 0 && !name.StartsWith(":");
        }
        public static string RemoveInvalidChars(string articleTitle)
        {
            int pos;
            while ((pos = articleTitle.IndexOfAny(InvalidChars)) >= 0)
                articleTitle = articleTitle.Remove(pos, 1);
            return articleTitle;
        }
        public static string StripNamespaceColon(string ns)
        {
            return ns.TrimEnd(':');
        }
        public static int RegexMatchCount(string regex, string input)
        {
            return RegexMatchCount(new Regex(regex), input);
        }
        public static int RegexMatchCount(string regex, string input, RegexOptions opts)
        {
            return RegexMatchCount(new Regex(regex, opts), input);
        }
        public static int RegexMatchCount(Regex regex, string input)
        {
            return regex.Matches(input).Count;
        }
        private static readonly Regex McName = new Regex(@"^Mc([A-Z])", RegexOptions.Compiled);
        public static string MakeHumanCatKey(string name)
        {
            name = RemoveNamespaceString(Regex.Replace(RemoveDiacritics(name), @"\(.*?\)$", "").Replace("'", "").Trim()).Trim();
            string origName = name;
            if (!name.Contains(" ") || Variables.LangCode == "uk")
                return FixupDefaultSort(origName);
            string suffix = "";
            int pos = name.IndexOf(',');
            if (pos >= 0 && Variables.LangCode != "ru")
            {
                suffix = name.Substring(pos + 1).Trim();
                name = name.Substring(0, pos).Trim();
            }
            if (Regex.IsMatch(origName, @"(\b(Abd[au]ll?ah?|Ahmed|Mustaq|Merza|Kandah[a-z]*|Mohabet|Nasrat|Nazargul|Yasi[mn]|Husayn|Akram|M[ou]hamm?[ae]d\w*|Abd[eu]l|Razzaq|Adil|Anwar|Fahed|Habi[bdr]|Hafiz|Jawad|Hassan|Ibr[ao]him|Khal[ei]d|Karam|Majid|Mustafa|Rash[ie]d|Yusef|[Bb]in|Nasir|Aziz|Rahim|Kareem|Abu|Aminullah|Fahd|Fawaz|Ahmad|Rahman|Hasan|Nassar|A(?:zz|s)am|Jam[ai]l|Tariqe?|Yussef|Said|Wass?im|Wazir|Tarek|Umran|Mahmoud|Malik|Shoaib|Hizani|Abib|Raza|Salim|Iqbal|Saleh|Hajj|Brahim|Zahir|Wasm|Yo?usef|Yunis|Zakim|Shah|Yasser|Samil|Akh[dk]ar|Haji|Uthman|Khadr|Asiri|Rajab|Shakouri|Ishmurat|Anazi|Nahdi|Zaheed|Ramzi|Rasul|Muktar|Muhassen|Radhi|Rafat|Kadir|Zaman|Karim|Awal|Mahmud|Mohammon|Husein|Airat|Alawi|Ullah|Sayaf|Henali|Ismael|Salih|Mahnut|Faha|Hammad|Hozaifa|Ravil|Jehan|Abdah|Djamel|Sabir|Ruhani|Hisham|Rehman|Mesut|Mehdi|Lakhdar|Mourad|Fazal[a-z]*|Mukit|Jalil|Rustam|Jumm?a|Omar Ali)\b|(?:[bdfmtnrz]ullah|alludin|[hm]atulla|r[ao]llah|harudin|millah)\b|\b(?:Abd[aeu][lr]|Nazur| Al[- ][A-Z]| al-[A-Z]))"))
                return FixupDefaultSort(origName);
            int intLast = name.LastIndexOf(" ") + 1;
            string lastName = name.Substring(intLast).Trim();
            name = name.Remove(intLast).Trim();
            if (IsRomanNumber(lastName) || Regex.IsMatch(lastName, @"^[SJsj]n?r\.$"))
            {
                if (name.Contains(" "))
                {
                    suffix += lastName;
                    intLast = name.LastIndexOf(" ") + 1;
                    lastName = name.Substring(intLast);
                    name = name.Remove(intLast).Trim();
                }
                else
                {
                    return FixupDefaultSort(origName);
                }
            }
            lastName = McName.Replace(lastName, "Mac$1");
            lastName = TurnFirstToUpper(lastName.ToLower());
            name = (lastName + ", " + (name.Length > 0 ? name + ", " : "") + suffix).Trim(" ,".ToCharArray());
            return FixupDefaultSort(name);
        }
        public static string RemoveNamespaceString(string title)
        {
            return RemoveNamespaceString(new Article(title));
        }
        public static string RemoveNamespaceString(Article a)
        {
            return a.NamespacelessName;
        }
        public static string GetNamespaceString(string title)
        {
            return GetNamespaceString(new Article(title));
        }
        public static string GetNamespaceString(Article a)
        {
            int ns = a.NameSpaceKey;
            return (ns == 0) ? "" : Variables.Namespaces[ns].Replace(":", "");
        }
        public static string BasePageName(string title)
        {
            title = RemoveNamespaceString(title);
            int i = title.IndexOf('/');
            return (i < 0) ? title : title.Substring(0, i);
        }
        public static string SubPageName(string title)
        {
            title = RemoveNamespaceString(title);
            int i = title.LastIndexOf('/');
            return (i < 0) ? title : title.Substring(i + 1);
        }
        public static bool IsRomanNumber(string s)
        {
            if (string.IsNullOrEmpty(s) || s.Length > 5) return false;
            foreach (char c in s)
            {
                if (c != 'I' && c != 'V' && c != 'X') return false;
            }
            return true;
        }
        public static string GetHTML(string url)
        {
            return GetHTML(url, Encoding.UTF8);
        }
        public static string GetHTML(string url, Encoding enc)
        {
            if (Globals.UnitTestMode) throw new Exception("You shouldn't access Wikipedia from unit tests");
            HttpWebRequest rq = Variables.PrepareWebRequest(url);
            HttpWebResponse response = (HttpWebResponse)rq.GetResponse();
            Stream stream = response.GetResponseStream();
            StreamReader sr = new StreamReader(stream, enc);
            string text = sr.ReadToEnd();
            sr.Close();
            stream.Close();
            response.Close();
            return text;
        }
        [DllImport("user32.dll")]
        private static extern void FlashWindow(IntPtr hwnd, bool bInvert);
        public static void FlashWindow(Control window)
        {
            try
            {
                FlashWindow(window.Handle, true);
            }
            catch { }
        }
        public static bool CaseInsensitiveStringCompare(string one, string two)
        {
            return (string.Compare(one, two, true) == 0);
        }
        public static string CaseInsensitive(string input)
        {
            if (!string.IsNullOrEmpty(input) && char.IsLetter(input[0]) &&
                (char.ToUpper(input[0]) != char.ToLower(input[0])))
            {
                input = input.Trim();
                return "[" + char.ToUpper(input[0]) + char.ToLower(input[0]) + "]" + input.Remove(0, 1);
            }
            return input;
        }
        public static string AllCaseInsensitive(string input)
        {
            if (string.IsNullOrEmpty(input))
                return input;
            input = input.Trim();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i <= input.Length - 1; i++)
            {
                if (char.IsLetter(input[i]))
                    builder.Append("[" + char.ToUpper(input[i]) + char.ToLower(input[i]) + "]");
                else builder.Append(input[i]);
            }
            return builder.ToString();
        }
        public static string ApplyKeyWords(string title, string text)
        {
            if (!string.IsNullOrEmpty(text) && !string.IsNullOrEmpty(title) && text.Contains("%%"))
            {
                string titleEncoded = WikiEncode(title);
                text = text.Replace("%%title%%", title);
                text = text.Replace("%%titlee%%", titleEncoded);
                text = text.Replace("%%fullpagename%%", title);
                text = text.Replace("%%fullpagenamee%%", titleEncoded);
                text = text.Replace("%%key%%", MakeHumanCatKey(title));
                string titleNoNamespace = RemoveNamespaceString(title);
                string basePageName = BasePageName(title);
                string subPageName = SubPageName(title);
                string theNamespace = GetNamespaceString(title);
                text = text.Replace("%%pagename%%", titleNoNamespace);
                text = text.Replace("%%pagenamee%%", WikiEncode(titleNoNamespace));
                text = text.Replace("%%basepagename%%", basePageName);
                text = text.Replace("%%basepagenamee%%", WikiEncode(basePageName));
                text = text.Replace("%%namespace%%", theNamespace);
                text = text.Replace("%%namespacee%%", WikiEncode(theNamespace));
                text = text.Replace("%%subpagename%%", subPageName);
                text = text.Replace("%%subpagenamee%%", WikiEncode(subPageName));
                text = text.Replace("%%server%%", Variables.URL);
                text = text.Replace("%%scriptpath%%", Variables.ScriptPath);
                text = text.Replace("%%servername%%", ServerName(Variables.URL));
            }
            return text;
        }
        public static string TurnFirstToUpper(string input)
        {
            if (Variables.Project == ProjectEnum.wiktionary || string.IsNullOrEmpty(input))
                return input;
            return TurnFirstToUpperNoProjectCheck(input);
        }
        public static string TurnFirstToUpperNoProjectCheck(string input)
        {
            return (string.IsNullOrEmpty(input)) ? "" : (char.ToUpper(input[0]) + input.Remove(0, 1));
        }
        public static string TurnFirstToLower(string input)
        {
            return (string.IsNullOrEmpty(input)) ? "" : (char.ToLower(input[0]) + input.Remove(0, 1));
        }
        private static readonly CultureInfo EnglishCulture = new CultureInfo("en-GB");
        public static string TitleCaseEN(string text)
        {
            if (text.ToUpper().Equals(text))
                text = text.ToLower();
            TextInfo info = EnglishCulture.TextInfo;
            return (info.ToTitleCase(text.Trim()));
        }
        public static string Newline(string s)
        {
            return Newline(s, 1);
        }
        public static string Newline(string s, int n)
        {
            if (s.Length == 0)
                return s;
            StringBuilder sb = new StringBuilder(s);
            for (int i = 0; i < n; i++)
                sb.Insert(0,"\r\n");
            return sb.ToString();
        }
        private static readonly Regex RegexWordCountTable = new Regex(@"\{\|.*?\|\}", RegexOptions.Compiled | RegexOptions.Singleline);
        public static int WordCount(string text)
        {
            text = RegexWordCountTable.Replace(text, "");
            text = WikiRegexes.TemplateMultiline.Replace(text, " ");
            text = WikiRegexes.Comments.Replace(text, "");
            int words = 0;
            int i = 0;
            while (i < text.Length)
            {
                if (!char.IsLetterOrDigit(text[i]))
                {
                    do
                        i++;
                    while (i < text.Length && !char.IsLetterOrDigit(text[i]));
                }
                else
                {
                    words++;
                    do
                        i++;
                    while (i < text.Length && char.IsLetterOrDigit(text[i]));
                }
            }
            return words;
        }
        public static int InterwikiCount(string text)
        {
            int count = 0;
            foreach (Match m in WikiRegexes.PossibleInterwikis.Matches(text))
            {
                if (SiteMatrix.Languages.Contains(m.Groups[1].Value.ToLower())) count++;
            }
            return count;
        }
        public static int LinkCount(string text)
        { return WikiRegexes.WikiLinksOnly.Matches(text).Count; }
        public static string RemoveSyntax(string text)
        {
            if (string.IsNullOrEmpty(text))
                return text;
            if (text[0] == '#' || text[0] == '*')
                text = text.Substring(1);
            text = text.Replace("_", " ").Trim();
            text = text.Trim('[', ']');
            text = text.Replace(@"&amp;", @"&");
            text = text.Replace(@"&quot;", @"""");
            text = text.Replace(@"ï¿½", "");
            return text.TrimStart(':');
        }
        public static string GetMetaContentValue(string pagesource, string metaname)
        {
            if (pagesource.Length == 0 || metaname.Length == 0)
                return "";
            Regex metaContent = new Regex(@"< *meta +name *= *""" + Regex.Escape(metaname) + @""" +content *= *""([^""<>]+?)"" */? *>", RegexOptions.IgnoreCase);
            return metaContent.Match(pagesource).Groups[1].Value.Trim();
        }
        public static string[] SplitToSections(string articleText)
        {
            string[] lines = articleText.Split(new[] { "\r\n" }, StringSplitOptions.None);
            List<string> sections = new List<string>();
            StringBuilder section = new StringBuilder();
            foreach (string s in lines)
            {
                if (WikiRegexes.Heading.IsMatch(s))
                {
                    if (section.Length > 0)
                    {
                        sections.Add(section.ToString());
                        section.Length = 0;
                    }
                }
                section.Append(s);
                section.Append("\r\n");
            }
            if (section.Length > 0) sections.Add(section.ToString());
            return sections.ToArray();
        }
        public static string RemoveMatches(string str, MatchCollection matches)
        {
            if (matches.Count == 0) return str;
            StringBuilder sb = new StringBuilder(str);
            for (int i = matches.Count - 1; i >= 0; i--)
            {
                sb.Remove(matches[i].Index, matches[i].Value.Length);
            }
            return sb.ToString();
        }
        public static string RemoveMatches(string str, IList<Match> matches)
        {
            if (matches.Count == 0) return str;
            StringBuilder sb = new StringBuilder(str);
            for (int i = matches.Count - 1; i >= 0; i--)
            {
                sb.Remove(matches[i].Index, matches[i].Value.Length);
            }
            return sb.ToString();
        }
        public static readonly KeyValuePair<string, string>[] Diacritics =
        {
            new KeyValuePair<string, string>("Ã", "A"),
            new KeyValuePair<string, string>("Ã¡", "a"),
            new KeyValuePair<string, string>("Ä", "C"),
            new KeyValuePair<string, string>("Ä", "c"),
            new KeyValuePair<string, string>("Ã", "E"),
            new KeyValuePair<string, string>("Ã©", "e"),
            new KeyValuePair<string, string>("Ã", "I"),
            new KeyValuePair<string, string>("Ã­", "i"),
            new KeyValuePair<string, string>("Ä¹", "L"),
            new KeyValuePair<string, string>("Äº", "l"),
            new KeyValuePair<string, string>("Å", "N"),
            new KeyValuePair<string, string>("Å", "n"),
            new KeyValuePair<string, string>("Ã", "O"),
            new KeyValuePair<string, string>("Ã³", "o"),
            new KeyValuePair<string, string>("Å", "R"),
            new KeyValuePair<string, string>("Å", "r"),
            new KeyValuePair<string, string>("Å", "S"),
            new KeyValuePair<string, string>("Å", "s"),
            new KeyValuePair<string, string>("Ã", "U"),
            new KeyValuePair<string, string>("Ãº", "u"),
            new KeyValuePair<string, string>("Ã", "Y"),
            new KeyValuePair<string, string>("Ã½", "y"),
            new KeyValuePair<string, string>("Å¹", "Z"),
            new KeyValuePair<string, string>("Åº", "z"),
            new KeyValuePair<string, string>("Ã", "A"),
            new KeyValuePair<string, string>("Ã ", "a"),
            new KeyValuePair<string, string>("Ã", "E"),
            new KeyValuePair<string, string>("Ã¨", "e"),
            new KeyValuePair<string, string>("Ã", "I"),
            new KeyValuePair<string, string>("Ã¬", "i"),
            new KeyValuePair<string, string>("Ã", "O"),
            new KeyValuePair<string, string>("Ã²", "o"),
            new KeyValuePair<string, string>("Ã", "U"),
            new KeyValuePair<string, string>("Ã¹", "u"),
            new KeyValuePair<string, string>("Ã", "A"),
            new KeyValuePair<string, string>("Ã¢", "a"),
            new KeyValuePair<string, string>("Ä", "C"),
            new KeyValuePair<string, string>("Ä", "c"),
            new KeyValuePair<string, string>("Ã", "E"),
            new KeyValuePair<string, string>("Ãª", "e"),
            new KeyValuePair<string, string>("Ä", "G"),
            new KeyValuePair<string, string>("Ä", "g"),
            new KeyValuePair<string, string>("Ä¤", "H"),
            new KeyValuePair<string, string>("Ä¥", "h"),
            new KeyValuePair<string, string>("Ã", "I"),
            new KeyValuePair<string, string>("Ã®", "i"),
            new KeyValuePair<string, string>("Ä´", "J"),
            new KeyValuePair<string, string>("Äµ", "j"),
            new KeyValuePair<string, string>("Ã", "O"),
            new KeyValuePair<string, string>("Ã´", "o"),
            new KeyValuePair<string, string>("Å", "S"),
            new KeyValuePair<string, string>("Å", "s"),
            new KeyValuePair<string, string>("Ã", "U"),
            new KeyValuePair<string, string>("Ã»", "u"),
            new KeyValuePair<string, string>("Å´", "W"),
            new KeyValuePair<string, string>("Åµ", "w"),
            new KeyValuePair<string, string>("Å¶", "Y"),
            new KeyValuePair<string, string>("Å·", "y"),
            new KeyValuePair<string, string>("Ã", "A"),
            new KeyValuePair<string, string>("Ã¤", "a"),
            new KeyValuePair<string, string>("Ã", "E"),
            new KeyValuePair<string, string>("Ã«", "e"),
            new KeyValuePair<string, string>("Ã", "I"),
            new KeyValuePair<string, string>("Ã¯", "i"),
            new KeyValuePair<string, string>("Ã", "O"),
            new KeyValuePair<string, string>("Ã¶", "o"),
            new KeyValuePair<string, string>("Ã", "U"),
            new KeyValuePair<string, string>("Ã¼", "u"),
            new KeyValuePair<string, string>("Å¸", "Y"),
            new KeyValuePair<string, string>("Ã¿", "y"),
            new KeyValuePair<string, string>("Ã", "ss"),
            new KeyValuePair<string, string>("Ã", "A"),
            new KeyValuePair<string, string>("Ã£", "a"),
            new KeyValuePair<string, string>("áº¼", "E"),
            new KeyValuePair<string, string>("áº½", "e"),
            new KeyValuePair<string, string>("Ä¨", "I"),
            new KeyValuePair<string, string>("Ä©", "i"),
            new KeyValuePair<string, string>("Ã", "N"),
            new KeyValuePair<string, string>("Ã±", "n"),
            new KeyValuePair<string, string>("Ã", "O"),
            new KeyValuePair<string, string>("Ãµ", "o"),
            new KeyValuePair<string, string>("Å¨", "U"),
            new KeyValuePair<string, string>("Å©", "u"),
            new KeyValuePair<string, string>("á»¸", "Y"),
            new KeyValuePair<string, string>("á»¹", "y"),
            new KeyValuePair<string, string>("Ã", "C"),
            new KeyValuePair<string, string>("Ã§", "c"),
            new KeyValuePair<string, string>("Ä¢", "G"),
            new KeyValuePair<string, string>("Ä£", "g"),
            new KeyValuePair<string, string>("Ä¶", "K"),
            new KeyValuePair<string, string>("Ä·", "k"),
            new KeyValuePair<string, string>("Ä»", "L"),
            new KeyValuePair<string, string>("Ä¼", "l"),
            new KeyValuePair<string, string>("Å", "N"),
            new KeyValuePair<string, string>("Å", "n"),
            new KeyValuePair<string, string>("Å", "R"),
            new KeyValuePair<string, string>("Å", "r"),
            new KeyValuePair<string, string>("Å", "S"),
            new KeyValuePair<string, string>("Å", "s"),
            new KeyValuePair<string, string>("Å¢", "T"),
            new KeyValuePair<string, string>("Å£", "t"),
            new KeyValuePair<string, string>("Ä", "D"),
            new KeyValuePair<string, string>("Ä", "d"),
            new KeyValuePair<string, string>("Å®", "U"),
            new KeyValuePair<string, string>("Å¯", "u"),
            new KeyValuePair<string, string>("Ç", "A"),
            new KeyValuePair<string, string>("Ç", "a"),
            new KeyValuePair<string, string>("Ä", "C"),
            new KeyValuePair<string, string>("Ä", "c"),
            new KeyValuePair<string, string>("Ä", "D"),
            new KeyValuePair<string, string>("Ä", "d"),
            new KeyValuePair<string, string>("Ä", "E"),
            new KeyValuePair<string, string>("Ä", "e"),
            new KeyValuePair<string, string>("Ç", "I"),
            new KeyValuePair<string, string>("Ç", "i"),
            new KeyValuePair<string, string>("Ä½", "L"),
            new KeyValuePair<string, string>("Ä¾", "l"),
            new KeyValuePair<string, string>("Å", "N"),
            new KeyValuePair<string, string>("Å", "N"),
            new KeyValuePair<string, string>("Ç", "O"),
            new KeyValuePair<string, string>("Ç", "o"),
            new KeyValuePair<string, string>("Å", "R"),
            new KeyValuePair<string, string>("Å", "r"),
            new KeyValuePair<string, string>("Å ", "S"),
            new KeyValuePair<string, string>("Å¡", "s"),
            new KeyValuePair<string, string>("Å¤", "T"),
            new KeyValuePair<string, string>("Å¥", "t"),
            new KeyValuePair<string, string>("Ç", "U"),
            new KeyValuePair<string, string>("Ç", "u"),
            new KeyValuePair<string, string>("Å½", "Z"),
            new KeyValuePair<string, string>("Å¾", "z"),
            new KeyValuePair<string, string>("Ä", "A"),
            new KeyValuePair<string, string>("Ä", "a"),
            new KeyValuePair<string, string>("Ä", "E"),
            new KeyValuePair<string, string>("Ä", "e"),
            new KeyValuePair<string, string>("Äª", "I"),
            new KeyValuePair<string, string>("Ä«", "i"),
            new KeyValuePair<string, string>("Å", "O"),
            new KeyValuePair<string, string>("Å", "o"),
            new KeyValuePair<string, string>("Åª", "U"),
            new KeyValuePair<string, string>("Å«", "u"),
            new KeyValuePair<string, string>("È²", "Y"),
            new KeyValuePair<string, string>("È³", "y"),
            new KeyValuePair<string, string>("Ç¢", "E"),
            new KeyValuePair<string, string>("Ç£", "e"),
            new KeyValuePair<string, string>("Ç", "u"),
            new KeyValuePair<string, string>("Ç", "u"),
            new KeyValuePair<string, string>("Ç", "u"),
            new KeyValuePair<string, string>("Ç", "u"),
            new KeyValuePair<string, string>("Ä", "A"),
            new KeyValuePair<string, string>("Ä", "a"),
            new KeyValuePair<string, string>("Ä", "E"),
            new KeyValuePair<string, string>("Ä", "e"),
            new KeyValuePair<string, string>("Ä", "G"),
            new KeyValuePair<string, string>("Ä", "g"),
            new KeyValuePair<string, string>("Ä¬", "I"),
            new KeyValuePair<string, string>("Ä­", "i"),
            new KeyValuePair<string, string>("Å", "O"),
            new KeyValuePair<string, string>("Å", "o"),
            new KeyValuePair<string, string>("Å¬", "U"),
            new KeyValuePair<string, string>("Å­", "u"),
            new KeyValuePair<string, string>("Ä", "C"),
            new KeyValuePair<string, string>("Ä", "c"),
            new KeyValuePair<string, string>("Ä", "E"),
            new KeyValuePair<string, string>("Ä", "e"),
            new KeyValuePair<string, string>("Ä ", "G"),
            new KeyValuePair<string, string>("Ä¡", "g"),
            new KeyValuePair<string, string>("Ä°", "I"),
            new KeyValuePair<string, string>("Ä±", "i"),
            new KeyValuePair<string, string>("Å»", "Z"),
            new KeyValuePair<string, string>("Å¼", "z"),
            new KeyValuePair<string, string>("Ä", "A"),
            new KeyValuePair<string, string>("Ä", "a"),
            new KeyValuePair<string, string>("Ä", "E"),
            new KeyValuePair<string, string>("Ä", "e"),
            new KeyValuePair<string, string>("Ä®", "I"),
            new KeyValuePair<string, string>("Ä¯", "i"),
            new KeyValuePair<string, string>("Çª", "O"),
            new KeyValuePair<string, string>("Ç«", "o"),
            new KeyValuePair<string, string>("Å²", "U"),
            new KeyValuePair<string, string>("Å³", "u"),
            new KeyValuePair<string, string>("á¸", "D"),
            new KeyValuePair<string, string>("á¸", "d"),
            new KeyValuePair<string, string>("á¸¤", "H"),
            new KeyValuePair<string, string>("á¸¥", "h"),
            new KeyValuePair<string, string>("á¸¶", "L"),
            new KeyValuePair<string, string>("á¸·", "l"),
            new KeyValuePair<string, string>("á¸¸", "L"),
            new KeyValuePair<string, string>("á¸¹", "l"),
            new KeyValuePair<string, string>("á¹", "M"),
            new KeyValuePair<string, string>("á¹", "m"),
            new KeyValuePair<string, string>("á¹", "N"),
            new KeyValuePair<string, string>("á¹", "n"),
            new KeyValuePair<string, string>("á¹", "R"),
            new KeyValuePair<string, string>("á¹", "r"),
            new KeyValuePair<string, string>("á¹", "R"),
            new KeyValuePair<string, string>("á¹", "r"),
            new KeyValuePair<string, string>("á¹¢", "S"),
            new KeyValuePair<string, string>("á¹£", "s"),
            new KeyValuePair<string, string>("á¹¬", "T"),
            new KeyValuePair<string, string>("á¹­", "t"),
            new KeyValuePair<string, string>("Å", "L"),
            new KeyValuePair<string, string>("Å", "l"),
            new KeyValuePair<string, string>("Å", "O"),
            new KeyValuePair<string, string>("Å", "o"),
            new KeyValuePair<string, string>("Å°", "U"),
            new KeyValuePair<string, string>("Å±", "u"),
            new KeyValuePair<string, string>("Ä¿", "L"),
            new KeyValuePair<string, string>("Å", "l"),
            new KeyValuePair<string, string>("Ä¦", "H"),
            new KeyValuePair<string, string>("Ä§", "h"),
            new KeyValuePair<string, string>("Ã", "D"),
            new KeyValuePair<string, string>("Ã°", "d"),
            new KeyValuePair<string, string>("Ã", "TH"),
            new KeyValuePair<string, string>("Ã¾", "th"),
            new KeyValuePair<string, string>("Å", "O"),
            new KeyValuePair<string, string>("Å", "o"),
            new KeyValuePair<string, string>("Ã", "AE"),
            new KeyValuePair<string, string>("Ã¦", "ae"),
            new KeyValuePair<string, string>("Ã", "O"),
            new KeyValuePair<string, string>("Ã¸", "o"),
            new KeyValuePair<string, string>("Ã", "A"),
            new KeyValuePair<string, string>("Ã¥", "a"),
            new KeyValuePair<string, string>("Æ", "E"),
            new KeyValuePair<string, string>("É", "e"),
            new KeyValuePair<string, string>("Ð", "Ð"),
            new KeyValuePair<string, string>("Ñ", "Ðµ"),
            new KeyValuePair<string, string>("Ð±", "b"),
            new KeyValuePair<string, string>("Ð»", "l"),
            new KeyValuePair<string, string>("á»", "o"),
            new KeyValuePair<string, string>("á»", "o"),
            new KeyValuePair<string, string>("áº§", "a"),
            new KeyValuePair<string, string>("Æ¡", "o"),
            new KeyValuePair<string, string>("Æ ", "O"),
            new KeyValuePair<string, string>("Æ°", "u"),
            new KeyValuePair<string, string>("Æ¯", "U"),
            new KeyValuePair<string, string>("á»", "e"),
            new KeyValuePair<string, string>("á»©", "u"),
            new KeyValuePair<string, string>("á¹", "n"),
            new KeyValuePair<string, string>("áº", "y"),
            new KeyValuePair<string, string>("á¸»", "l"),
            new KeyValuePair<string, string>("á¹", "r"),
            new KeyValuePair<string, string>("á¹", "n"),
            new KeyValuePair<string, string>("Æ", "b"),
            new KeyValuePair<string, string>("Æ", "d"),
            new KeyValuePair<string, string>("Æ", "f"),
            new KeyValuePair<string, string>("Æ", "k"),
            new KeyValuePair<string, string>("Æ", "l"),
            new KeyValuePair<string, string>("Æ", "n"),
            new KeyValuePair<string, string>("Æ¡", "o"),
            new KeyValuePair<string, string>("Æ¥", "p"),
            new KeyValuePair<string, string>("Æ«", "t"),
            new KeyValuePair<string, string>("Æ­", "t"),
            new KeyValuePair<string, string>("Æ°", "u"),
            new KeyValuePair<string, string>("Æ´", "y"),
            new KeyValuePair<string, string>("Æ¶", "z"),
            new KeyValuePair<string, string>("Ç", "a"),
            new KeyValuePair<string, string>("Ç", "o"),
            new KeyValuePair<string, string>("Ç", "u"),
            new KeyValuePair<string, string>("Ç", "u"),
            new KeyValuePair<string, string>("Ç", "u"),
            new KeyValuePair<string, string>("Ç", "u"),
            new KeyValuePair<string, string>("Ç", "u"),
            new KeyValuePair<string, string>("Ç", "a"),
            new KeyValuePair<string, string>("Ç¡", "a"),
            new KeyValuePair<string, string>("Ç¥", "g"),
            new KeyValuePair<string, string>("Ç§", "g"),
            new KeyValuePair<string, string>("Ç©", "k"),
            new KeyValuePair<string, string>("Ç«", "o"),
            new KeyValuePair<string, string>("á»£", "o"),
            new KeyValuePair<string, string>("á»", "o"),
            new KeyValuePair<string, string>("Ç­", "o"),
            new KeyValuePair<string, string>("Ç°", "j"),
            new KeyValuePair<string, string>("Çµ", "g"),
            new KeyValuePair<string, string>("Ç¹", "n"),
            new KeyValuePair<string, string>("Ç»", "a"),
            new KeyValuePair<string, string>("Ç¿", "o"),
            new KeyValuePair<string, string>("È", "a"),
            new KeyValuePair<string, string>("È", "a"),
            new KeyValuePair<string, string>("È", "e"),
            new KeyValuePair<string, string>("È", "e"),
            new KeyValuePair<string, string>("È", "i"),
            new KeyValuePair<string, string>("È", "i"),
            new KeyValuePair<string, string>("È", "o"),
            new KeyValuePair<string, string>("È", "o"),
            new KeyValuePair<string, string>("È", "r"),
            new KeyValuePair<string, string>("È", "r"),
            new KeyValuePair<string, string>("È", "u"),
            new KeyValuePair<string, string>("È", "u"),
            new KeyValuePair<string, string>("È", "s"),
            new KeyValuePair<string, string>("È", "t"),
            new KeyValuePair<string, string>("È", "h"),
            new KeyValuePair<string, string>("È¡", "d"),
            new KeyValuePair<string, string>("È¥", "z"),
            new KeyValuePair<string, string>("È§", "a"),
            new KeyValuePair<string, string>("È©", "e"),
            new KeyValuePair<string, string>("È«", "o"),
            new KeyValuePair<string, string>("È­", "o"),
            new KeyValuePair<string, string>("È¯", "o"),
            new KeyValuePair<string, string>("È±", "o"),
            new KeyValuePair<string, string>("È´", "l"),
            new KeyValuePair<string, string>("Èµ", "n"),
            new KeyValuePair<string, string>("È¶", "t"),
            new KeyValuePair<string, string>("È¼", "c"),
            new KeyValuePair<string, string>("È¿", "s"),
            new KeyValuePair<string, string>("É", "z"),
            new KeyValuePair<string, string>("Æ", "D"),
            new KeyValuePair<string, string>("Æ", "D"),
            new KeyValuePair<string, string>("Æ", "D"),
            new KeyValuePair<string, string>("Æ", "E"),
            new KeyValuePair<string, string>("Æ", "E"),
            new KeyValuePair<string, string>("Æ", "F"),
            new KeyValuePair<string, string>("Æ", "G"),
            new KeyValuePair<string, string>("Æ", "I"),
            new KeyValuePair<string, string>("Æ", "K"),
            new KeyValuePair<string, string>("Æ", "M"),
            new KeyValuePair<string, string>("Æ", "N"),
            new KeyValuePair<string, string>("Æ", "O"),
            new KeyValuePair<string, string>("Æ¤", "P"),
            new KeyValuePair<string, string>("Æ¬", "T"),
            new KeyValuePair<string, string>("Æ®", "T"),
            new KeyValuePair<string, string>("Æ²", "V"),
            new KeyValuePair<string, string>("Æ³", "Y"),
            new KeyValuePair<string, string>("Æµ", "Z"),
            new KeyValuePair<string, string>("Ç", "D"),
            new KeyValuePair<string, string>("Ç", "L"),
            new KeyValuePair<string, string>("Ç", "N"),
            new KeyValuePair<string, string>("Ç", "U"),
            new KeyValuePair<string, string>("Ç", "U"),
            new KeyValuePair<string, string>("Ç", "U"),
            new KeyValuePair<string, string>("Ç", "U"),
            new KeyValuePair<string, string>("Ç", "e"),
            new KeyValuePair<string, string>("Ç", "A"),
            new KeyValuePair<string, string>("Ç ", "A"),
            new KeyValuePair<string, string>("Ç¤", "G"),
            new KeyValuePair<string, string>("Ç¦", "G"),
            new KeyValuePair<string, string>("Ç¨", "K"),
            new KeyValuePair<string, string>("Ç¬", "O"),
            new KeyValuePair<string, string>("Ç²", "D"),
            new KeyValuePair<string, string>("Ç´", "G"),
            new KeyValuePair<string, string>("Ç¸", "N"),
            new KeyValuePair<string, string>("Çº", "A"),
            new KeyValuePair<string, string>("Ç¾", "O"),
            new KeyValuePair<string, string>("È", "A"),
            new KeyValuePair<string, string>("È", "A"),
            new KeyValuePair<string, string>("È", "E"),
            new KeyValuePair<string, string>("È", "E"),
            new KeyValuePair<string, string>("È", "I"),
            new KeyValuePair<string, string>("È", "I"),
            new KeyValuePair<string, string>("È", "O"),
            new KeyValuePair<string, string>("È", "O"),
            new KeyValuePair<string, string>("È", "R"),
            new KeyValuePair<string, string>("È", "R"),
            new KeyValuePair<string, string>("È", "U"),
            new KeyValuePair<string, string>("È", "U"),
            new KeyValuePair<string, string>("È", "S"),
            new KeyValuePair<string, string>("È", "T"),
            new KeyValuePair<string, string>("È", "H"),
            new KeyValuePair<string, string>("È ", "N"),
            new KeyValuePair<string, string>("È¤", "Z"),
            new KeyValuePair<string, string>("È¦", "A"),
            new KeyValuePair<string, string>("È¨", "E"),
            new KeyValuePair<string, string>("Èª", "O"),
            new KeyValuePair<string, string>("È¬", "O"),
            new KeyValuePair<string, string>("È®", "O"),
            new KeyValuePair<string, string>("È°", "O"),
            new KeyValuePair<string, string>("Èº", "A"),
            new KeyValuePair<string, string>("È»", "C"),
            new KeyValuePair<string, string>("È½", "L"),
            new KeyValuePair<string, string>("È¾", "T"),
            new KeyValuePair<string, string>("â± ", "L"),
            new KeyValuePair<string, string>("â±¡", "l"),
            new KeyValuePair<string, string>("â±¢", "L"),
            new KeyValuePair<string, string>("â±£", "P"),
            new KeyValuePair<string, string>("â±¤", "R"),
            new KeyValuePair<string, string>("â±¥", "a"),
            new KeyValuePair<string, string>("â±¦", "t"),
            new KeyValuePair<string, string>("â±§", "H"),
            new KeyValuePair<string, string>("â±¨", "h"),
            new KeyValuePair<string, string>("â±©", "K"),
            new KeyValuePair<string, string>("â±ª", "k"),
            new KeyValuePair<string, string>("â±«", "Z"),
            new KeyValuePair<string, string>("â±¬", "z"),
            new KeyValuePair<string, string>("â±´", "v"),
            new KeyValuePair<string, string>("á»", "o"),
            new KeyValuePair<string, string>("áº¯", "a"),
            new KeyValuePair<string, string>("áº¡", "a"),
            new KeyValuePair<string, string>("áº£", "a"),
            new KeyValuePair<string, string>("áº±", "a"),
            new KeyValuePair<string, string>("áº©", "a"),
            new KeyValuePair<string, string>("áº­", "a"),
            new KeyValuePair<string, string>("áº¿", "e"),
            new KeyValuePair<string, string>("á»", "e"),
            new KeyValuePair<string, string>("á»", "e"),
            new KeyValuePair<string, string>("á»", "e"),
            new KeyValuePair<string, string>("á»", "i"),
            new KeyValuePair<string, string>("á»", "i"),
            new KeyValuePair<string, string>("á»", "o"),
            new KeyValuePair<string, string>("Ã¸", "o"),
            new KeyValuePair<string, string>("á»", "o"),
            new KeyValuePair<string, string>("á»", "o"),
            new KeyValuePair<string, string>("á»¥", "u"),
            new KeyValuePair<string, string>("á»§", "u"),
            new KeyValuePair<string, string>("á»¯", "u"),
            new KeyValuePair<string, string>("á»³", "y"),
        };
        public static string RemoveDiacritics(string s)
        {
            foreach (KeyValuePair<string, string> p in Diacritics)
            {
                s = s.Replace(p.Key, p.Value);
            }
            return s;
        }
        public static bool HasDiacritics(string s)
        {
            return s != RemoveDiacritics(s);
        }
        private static readonly Regex BadDsChars = new Regex("[\"]");
        public static string FixupDefaultSort(string s)
        {
            s = BadDsChars.Replace(RemoveDiacritics(s), "");
            foreach (Match m in WikiRegexes.RegexWordApostrophes.Matches(s))
            {
                s = s.Remove(m.Index, m.Length);
                s = s.Insert(m.Index, TurnFirstToUpper(m.Value.ToLower()));
            }
            return s;
        }
        public static void WriteTextFileAbsolutePath(string message, string file, bool append)
        {
            using (StreamWriter writer = new StreamWriter(file, append, Encoding.UTF8))
            {
                writer.Write(message);
                writer.Close();
            }
        }
        public static void WriteTextFileAbsolutePath(StringBuilder message, string file, bool append)
        {
            WriteTextFileAbsolutePath(message.ToString(), file, append);
        }
        public static void WriteTextFile(string message, string file, bool append)
        {
            if (file.Contains(":"))
                WriteTextFileAbsolutePath(message, file, append);
            else
                WriteTextFileAbsolutePath(message, Application.StartupPath + "\\" + file, append);
        }
        public static void WriteTextFile(StringBuilder message, string file, bool append)
        {
            WriteTextFileAbsolutePath(message.ToString(), Application.StartupPath + "\\" + file, append);
        }
        public static string HTMLListToWiki(string text, string bullet)
        {
            text = text.Replace("\r\n\r\n", "\r\n");
            text = Regex.Replace(text, "<br ?/?>", "", RegexOptions.IgnoreCase);
            text = Regex.Replace(text, "</?(ol|ul|li)>", "", RegexOptions.IgnoreCase);
            text = Regex.Replace(text, "^</?(ol|ul|li)>\r\n", "", RegexOptions.Multiline | RegexOptions.IgnoreCase);
            text = Regex.Replace(text, @"^(\:|\*|#|\(? ?[0-9]{1,3}\b ?\)|[0-9]{1,3}\b\.?)", "", RegexOptions.Multiline);
            return Regex.Replace(text, @"^(.*)$(?<!^\s+$)", bullet + "$1", RegexOptions.Multiline);
        }
        private static readonly System.Media.SoundPlayer Sound = new System.Media.SoundPlayer();
        public static void Beep()
        {
            Sound.Stream = Properties.Resources.beep1;
            Sound.Play();
        }
        public static bool WriteDebugEnabled;
        public static void WriteDebug(string @object, string text)
        {
            if (!WriteDebugEnabled)
                return;
            try
            {
                WriteTextFile(string.Format(
                    @"object: {0}
Time: {1}
Message: {2}
", @object, DateTime.Now.ToLongTimeString(), text), "Log.txt", true);
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
            }
        }
        public static bool IsIP(string s)
        {
            IPAddress dummy;
            return IPAddress.TryParse(s, out dummy);
        }
        public static string StringBetween(string source, string start, string end)
        {
            int startPos = source.IndexOf(start) + start.Length;
            int endPos = source.IndexOf(end);
            if (startPos >= 0 && endPos >= 0 && startPos <= endPos)
                return source.Substring(startPos, endPos - startPos);
            return "";
        }
        public static string ReplacePartOfString(string source, int position, int length, string replace)
        {
            return source.Substring(0, position) + replace + source.Substring(position + length);
        }
        public static bool ReplaceOnce(StringBuilder text, string oldValue, string newValue)
        {
            int index = text.ToString().IndexOf(oldValue);
            if (index < 0)
                return false;
            text.Replace(oldValue, newValue, index, oldValue.Length);
            return true;
        }
        public static bool ReplaceOnce(ref string text, string oldValue, string newValue)
        {
            int index = text.IndexOf(oldValue);
            if (index < 0)
                return false;
            text = text.Remove(index, oldValue.Length);
            text = text.Insert(index, newValue);
            return true;
        }
        public static string FirstChars(string str, int count)
        {
            return (str.Length <= count) ? str : str.Substring(0, count);
        }
        public static string ConvertToLocalLineEndings(string input)
        {
            return input.Replace("\n", Environment.NewLine);
        }
        public static string ConvertFromLocalLineEndings(string input)
        {
            return input.Replace(Environment.NewLine, "\n");
        }
        public static void OpenArticleInBrowser(string title)
        {
            OpenURLInBrowser(Variables.NonPrettifiedURL(title));
        }
        public static void OpenURLInBrowser(string url)
        {
            try
            {
                System.Diagnostics.Process.Start(url);
            }
            catch { }
        }
        public static void OpenENArticleInBrowser(string title, bool userspace)
        {
            if (userspace)
                OpenURLInBrowser("http://en.wikipedia.org/wiki/User:" + WikiEncode(title));
            else
                OpenURLInBrowser("http://en.wikipedia.org/wiki/" + WikiEncode(title));
        }
        public static string GetENLinkWithSimpleSkinAndLocalLanguage(string article)
        {
            return "http://en.wikipedia.org/w/index.php?title=" + WikiEncode(article) + "&useskin=simple&uselang=" +
                Variables.LangCode;
        }
        public static void OpenArticleHistoryInBrowser(string title)
        {
            OpenURLInBrowser(Variables.GetArticleHistoryURL(title));
        }
        public static void OpenUserTalkInBrowser(string username)
        {
            OpenURLInBrowser(Variables.GetUserTalkURL(username));
        }
        public static void OpenArticleLogInBrowser(string page)
        {
            OpenURLInBrowser(Variables.URLLong +
                             "index.php?title=Special:Log&type=&user=&page=" + page + "&year=&month=-1&tagfilter=&hide_patrol_log=1");
        }
        public static void EditArticleInBrowser(string title)
        {
            OpenURLInBrowser(Variables.GetEditURL(title));
        }
        public static string WikiEncode(string title)
        {
            return HttpUtility.UrlEncode(title.Replace(' ', '_')).Replace("%2f", "/").Replace("%3a", ":");
        }
        public static string WikiDecode(string title)
        {
            return HttpUtility.UrlDecode(title.Replace("+", "%2B")).Replace('_', ' ');
        }
        public static string RemoveHashFromPageTitle(string title)
        {
            return !title.Contains("#") ? title : (title.Substring(0, title.IndexOf('#')));
        }
        public static string ServerName(string url)
        {
            return new Uri(url).Host;
        }
        private static readonly Regex ExpandTemplatesRegex = new Regex(@"<expandtemplates[^\>]*>(.*?)</expandtemplates>", RegexOptions.Compiled | RegexOptions.IgnoreCase | RegexOptions.Singleline);
        public static string ExpandTemplate(string articleText, string articleTitle, Dictionary<Regex, string> regexes, bool includeComment)
        {
            foreach (KeyValuePair<Regex, string> p in regexes)
            {
                foreach (Match m in p.Key.Matches(articleText))
                {
                    string call = m.Value;
                    string expandUri = Variables.URLApi + "?action=expandtemplates&format=xml&title=" + WikiEncode(articleTitle) + "&text=" + HttpUtility.UrlEncode(call);
                    string result;
                    try
                    {
                        string respStr = GetHTML(expandUri);
                        Match m1 = ExpandTemplatesRegex.Match(respStr);
                        if (!m.Success) continue;
                        result = HttpUtility.HtmlDecode(m1.Groups[1].Value);
                    }
                    catch
                    {
                        continue;
                    }
                    bool skipArticle;
                    result = new Parsers().Unicodify(result, out skipArticle);
                    if (includeComment)
                        result = result + "<!-- " + call + " -->";
                    articleText = articleText.Replace(call, result);
                }
            }
            return articleText;
        }
        public static string GetTitleFromURL(string link)
        {
            link = WikiRegexes.ExtractTitle.Match(link).Groups[1].Value;
            return string.IsNullOrEmpty(link) ? null : WikiDecode(link);
        }
        public static string[] FirstToUpperAndRemoveHashOnArray(string[] input)
        {
            if (input == null)
                return null;
            for (int i = 0; i < input.Length; i++)
            {
                input[i] = TurnFirstToUpper(RemoveHashFromPageTitle(input[i].Trim('[', ']', ' ', '\t')));
            }
            return input;
        }
        public static void Copy(ListBox box)
        {
            try
            {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < box.SelectedItems.Count; i++)
                {
                    builder.AppendLine(box.SelectedItems[i].ToString());
                }
                Clipboard.SetDataObject(builder.ToString().Trim(), true);
            }
            catch { }
        }
        public static void Copy(ListView view)
        {
            try
            {
                StringBuilder builder = new StringBuilder();
                foreach (ListViewItem a in view.SelectedItems)
                {
                    builder.AppendLine(a.Text);
                }
                Clipboard.SetDataObject(builder.ToString().Trim(), true);
            }
            catch { }
        }
        private const char ReturnLine = '\r', NewLine = '\n';
        private static readonly char[] Separators = new[] { ReturnLine, NewLine };
        public static string[] SplitLines(string source)
        {
            List<string> res = new List<string>();
            int pos = 0;
            int sourceLength = source.Length;
            while (pos < sourceLength)
            {
                int eol = source.IndexOfAny(Separators, pos);
                string s;
                if (eol < 0)
                {
                    s = source.Substring(pos);
                    pos = sourceLength;
                }
                else
                {
                    s = source.Substring(pos, eol - pos);
                    char ch = source[eol];
                    eol++;
                    if (ch == ReturnLine && eol < sourceLength)
                    {
                        if (source[eol] == NewLine) eol++;
                    }
                    pos = eol;
                }
                res.Add(s);
            }
            return res.ToArray();
        }
        public static string Join(string separator, params object[] list)
        {
            StringBuilder sb = new StringBuilder();
            foreach (object o in list)
            {
                if (sb.Length > 0) sb.Append(separator);
                sb.Append(o.ToString());
            }
            return sb.ToString();
        }
        public static int FirstDifference(string a, string b)
        {
            for (int i = 0; i < Math.Min(a.Length, b.Length); i++)
            {
                if (a[i] != b[i]) return i;
            }
            return Math.Min(a.Length, b.Length);
        }
        public static string ConvertToTalk(string a)
        {
            return ConvertToTalk(new Article(a));
        }
        public static string ConvertToTalk(Article a)
        {
            if (Namespace.IsSpecial(a.NameSpaceKey) || Namespace.IsTalk(a.NameSpaceKey))
                return a.Name;
            if (a.NameSpaceKey == Namespace.Article)
                return (Variables.Namespaces[Namespace.Talk] + a.Name);
            return Variables.Namespaces[a.NameSpaceKey + 1] + a.NamespacelessName;
        }
        public static List<Article> ConvertToTalk(List<Article> list)
        {
            List<Article> newList = new List<Article>(list.Count);
            foreach (Article a in list)
            {
                string s = ConvertToTalk(a);
                newList.Add(a.Equals(s) ? a : new Article(s));
            }
            return newList;
        }
        public static string ConvertFromTalk(string a)
        {
            return ConvertFromTalk(new Article(a));
        }
        public static string ConvertFromTalk(Article a)
        {
            if (Namespace.IsTalk(a.NameSpaceKey))
            {
                if (a.NameSpaceKey == Namespace.Talk)
                    return a.NamespacelessName;
                return Variables.Namespaces[a.NameSpaceKey - 1] + a.NamespacelessName;
            }
            return a.Name;
        }
        public static List<Article> ConvertFromTalk(List<Article> list)
        {
            List<Article> newList = new List<Article>(list.Count);
            foreach (Article a in list)
            {
                string s = ConvertFromTalk(a);
                newList.Add(a.Equals(s) ? a : new Article(s));
            }
            return newList;
        }
        public static List<Article> FilterSomeArticles(List<Article> unfilteredArticles)
        {
            List<Article> items = new List<Article>();
            foreach (Article a in unfilteredArticles)
            {
                if (a.NameSpaceKey >= Namespace.Article && a.NameSpaceKey != Namespace.MediaWiki &&
                    a.NameSpaceKey != Namespace.MediaWikiTalk && !a.Name.StartsWith("Commons:"))
                {
                    if (!items.Contains(a))
                        items.Add(a);
                }
            }
            return items;
        }
        public static string PostData(NameValueCollection postvars, string url)
        {
            if (Globals.UnitTestMode) throw new Exception("You shouldn't access Wikipedia from unit tests");
            HttpWebRequest rq = Variables.PrepareWebRequest(url);
            rq.Method = "POST";
            rq.ContentType = "application/x-www-form-urlencoded";
            Stream requestStream = rq.GetRequestStream();
            byte[] data = Encoding.UTF8.GetBytes(BuildPostDataString(postvars));
            requestStream.Write(data, 0, data.Length);
            requestStream.Close();
            HttpWebResponse rs = (HttpWebResponse)rq.GetResponse();
            if (rs.StatusCode == HttpStatusCode.OK)
                return new StreamReader(rs.GetResponseStream()).ReadToEnd();
            throw new WebException(rs.StatusDescription, WebExceptionStatus.UnknownError);
        }
        public static string BuildPostDataString(NameValueCollection postvars)
        {
            StringBuilder ret = new StringBuilder();
            for (int i = 0; i < postvars.Keys.Count; i++)
            {
                if (i > 0)
                    ret.Append("&");
                ret.Append(postvars.Keys[i] + "=" + HttpUtility.UrlEncode(postvars[postvars.Keys[i]]));
            }
            return ret.ToString();
        }
        public static void CopyToClipboard(string text)
        {
            try
            {
                Clipboard.Clear();
                System.Threading.Thread.Sleep(50);
                Clipboard.SetText(text);
            }
            catch { }
        }
        public static void CopyToClipboard(object data, bool copy)
        {
            try
            {
                Clipboard.Clear();
                System.Threading.Thread.Sleep(50);
                Clipboard.SetDataObject(data, copy);
            }
            catch { }
        }
        public static string VBInputBox(string prompt, string title, string defaultResponse, int xPos, int yPos)
        {
            return Microsoft.VisualBasic.Interaction.InputBox(prompt, title, defaultResponse, xPos, yPos);
        }
        public static void MessageBox(string message)
        {
            System.Windows.Forms.MessageBox.Show(message);
        }
        public static int GetNumberFromUser(bool edits, int max)
        {
            using (Controls.LevelNumber num = new Controls.LevelNumber(edits, max))
            {
                if (num.ShowDialog() != DialogResult.OK) return -1;
                return num.Levels;
            }
        }
        public static string ReplaceWithSpaces(string input, MatchCollection matches)
        {
            return ReplaceWith(input, matches, ' ');
        }
        public static string ReplaceWith(string input, MatchCollection matches, char rwith)
        {
            StringBuilder sb = new StringBuilder(input.Length);
            foreach (Match m in matches)
            {
                sb.Append(input, sb.Length, m.Index - sb.Length);
                sb.Append(rwith, m.Length);
            }
            sb.Append(input, sb.Length, input.Length - sb.Length);
            return sb.ToString();
        }
        public static string ReplaceWithSpaces(string input, Regex regex)
        {
            return ReplaceWithSpaces(input, regex.Matches(input));
        }
        public static string ReplaceWith(string input, Regex regex, char rwith)
        {
            return ReplaceWith(input, regex.Matches(input), rwith);
        }
        private static readonly System.Globalization.CultureInfo English = new System.Globalization.CultureInfo("en-GB");
        public static string ISOToENDate(string ISODate, Parsers.DateLocale locale)
        {
            if (Variables.LangCode != "en")
                return ISODate;
            DateTime dt;
            try
            {
                dt = Convert.ToDateTime(ISODate);
            }
            catch
            {
                return ISODate;
            }
            switch (locale)
            {
                case Parsers.DateLocale.American:
                    return dt.ToString("MMMM d, yyyy", English);
                case Parsers.DateLocale.International:
                    return dt.ToString("d MMMM yyyy", English);
                default:
                    return ISODate;
            }
        }
        public static string AppendParameterToTemplate(string template, string parameter, string value)
        {
            if (!template.StartsWith(@"{{"))
                return template;
            const string mask = "@";
            string separator = " ";
            string templatecopy = template;
            templatecopy = @"{{" + ReplaceWithSpaces(templatecopy.Substring(2), WikiRegexes.NestedTemplates);
            templatecopy = ReplaceWithSpaces(templatecopy, WikiRegexes.SimpleWikiLink);
            templatecopy = ReplaceWithSpaces(templatecopy, WikiRegexes.UnformattedText);
            templatecopy = templatecopy.Replace(mask, "");
            int bars = (templatecopy.Length - templatecopy.Replace(@"|", "").Length);
            templatecopy = templatecopy.Replace("\r\n", mask);
            int newlines = (templatecopy.Length - templatecopy.Replace(mask, "").Length);
            if (newlines > 2 && newlines >= (bars - 2))
                separator = "\r\n";
            return WikiRegexes.TemplateEnd.Replace(template, separator + @"| " + parameter + "=" + value + @"$1}}");
        }
        public static string GetTemplateParameterValue(string template, string parameter)
        {
            Regex param = new Regex(@"\|\s*" + Regex.Escape(parameter) + @"\s*=(.*?)(?=\||}}$)", RegexOptions.Singleline);
            string pipecleanedtemplate = PipeCleanedTemplate(template);
            Match m = param.Match(pipecleanedtemplate);
            if (m.Success)
            {
                Group paramValue = param.Match(pipecleanedtemplate).Groups[1];
                return template.Substring(paramValue.Index, paramValue.Length).Trim();
            }
            return "";
        }
        public static List<string> GetTemplateParametersValues(string template, List<string> parameters)
        {
            List<string> returnedvalues = new List<string>();
            foreach (string param in parameters)
            {
                returnedvalues.Add(GetTemplateParameterValue(template, param));
            }
            return returnedvalues;
        }
        public static string GetTemplateArgument(string template, int argument)
        {
            Regex arg = new Regex(@"\|\s*(.*?)\s*(?=\||}}$)", RegexOptions.Singleline);
            string pipecleanedtemplate = PipeCleanedTemplate(template);
            int count = 1;
            foreach (Match m in arg.Matches(pipecleanedtemplate))
            {
                if (count.Equals(argument))
                    return template.Substring(m.Groups[1].Index, m.Groups[1].Length);
                count++;
            }
            return "";
        }
        public static int GetTemplateArgumentCount(string template)
        {
            Regex arg = new Regex(@"\|\s*(.*?)\s*(?=\||}}$)", RegexOptions.Singleline);
            string pipecleanedtemplate = PipeCleanedTemplate(template);
            return arg.Matches(pipecleanedtemplate).Count;
        }
        public static string RenameTemplateParameter(string template, string oldparameter, string newparameter)
        {
            Regex param = new Regex(@"(\|\s*(?:<!--.*?-->)?)" + Regex.Escape(oldparameter) + @"(\s*(?:<!--.*?-->\s*)?=)", RegexOptions.Compiled);
            return (param.Replace(template, "$1" + newparameter + "$2"));
        }
        public static string RenameTemplateParameter(string template, List<string> oldparameters, string newparameter)
        {
            foreach (string oldparameter in oldparameters)
                template = RenameTemplateParameter(template, oldparameter, newparameter);
            return template;
        }
        public static string RemoveTemplateParameter(string articletext, string templatename, string parameter)
        {
            Regex oldtemplate = NestedTemplateRegex(templatename);
            foreach (Match m in oldtemplate.Matches(articletext))
            {
                string template = m.Value;
                articletext = articletext.Replace(template, RemoveTemplateParameter(template, parameter));
            }
            return articletext;
        }
        public static string RemoveTemplateParameter(string template, string parameter)
        {
            Regex param = new Regex(@"\|\s*" + Regex.Escape(parameter) + @"\s*=(.*?)(?=\||}}$)", RegexOptions.Singleline);
            string pipecleanedtemplate = PipeCleanedTemplate(template);
            Match m = param.Match(pipecleanedtemplate);
            if (m.Success)
            {
                int start = m.Index;
                int valuelength = param.Match(pipecleanedtemplate).Length;
                return (template.Substring(0, start) + template.Substring(start + valuelength));
            }
            return template;
        }
        public static string UpdateTemplateParameterValue(string template, string parameter, string newvalue)
        {
            Regex param = new Regex(@"\|[\s~]*" + Regex.Escape(parameter) + @"[\s~]*= *\s*?(.*?)\s*(?=(?:\||}}$))", RegexOptions.Singleline);
            string pipecleanedtemplate = PipeCleanedTemplate(template, true);
            Match m = param.Match(pipecleanedtemplate);
            if (m.Success)
            {
                int start = m.Groups[1].Index, valuelength = m.Groups[1].Length;
                return (template.Substring(0, start) + newvalue + template.Substring(start + valuelength));
            }
            return template;
        }
        public static string PipeCleanedTemplate(string template, bool commentsastilde)
        {
            const char rwith = '#';
            if (template.Length < 5)
                return template;
            string restoftemplate = template.Substring(3);
            restoftemplate = ReplaceWith(restoftemplate, WikiRegexes.NestedTemplates, rwith);
            restoftemplate = ReplaceWith(restoftemplate, WikiRegexes.SimpleWikiLink, rwith);
            restoftemplate = commentsastilde
                                 ? ReplaceWith(restoftemplate, WikiRegexes.UnformattedText, '~')
                                 : ReplaceWithSpaces(restoftemplate, WikiRegexes.UnformattedText);
            return (template.Substring(0, 3) + restoftemplate);
        }
        private static string PipeCleanedTemplate(string template)
        {
            return PipeCleanedTemplate(template, false);
        }
        public static string SetTemplateParameterValue(string template, string parameter, string newvalue)
        {
            string updatedtemplate = UpdateTemplateParameterValue(template, parameter, newvalue);
            if (updatedtemplate.Equals(template))
                updatedtemplate = AppendParameterToTemplate(template, parameter, newvalue);
            return updatedtemplate;
        }
        public static string GetTemplateName(string template)
        {
            return WikiRegexes.TemplateName.Match(template).Groups[1].Value;
        }
        public static string RenameTemplate(string articletext, string templatename, string newtemplatename)
        {
            return NestedTemplateRegex(templatename).Replace(articletext, "$1" + newtemplatename + "$3");
        }
        public static string RenameTemplate(string template, string newtemplatename)
        {
            return NestedTemplateRegex(GetTemplateName(template)).Replace(template, "$1" + newtemplatename + "$3");
        }
        public static string RenameTemplate(string articletext, string templatename, string newtemplatename, int count)
        {
            return NestedTemplateRegex(templatename).Replace(articletext, "$1" + newtemplatename + "$3", count);
        }
        private const string NestedTemplateRegexStart = @"({{\s*)(";
        private const string NestedTemplateRegexEnd = @"(\s*(?:<!--[^>]*?-->\s*|ââââM?\d+ââââ\s*)?(\|((?>[^\{\}]+|\{(?<DEPTH>)|\}(?<-DEPTH>))*(?(DEPTH)(?!))))?\}\})";
        public static Regex NestedTemplateRegex(string templatename)
        {
            if (templatename.Length == 0)
                return null;
            return NestedTemplateRegex(new [] { templatename });
        }
        public static Regex NestedTemplateRegex(ICollection<string> templatenames)
        {
            if (templatenames.Count == 0)
                return null;
            StringBuilder theRegex = new StringBuilder(NestedTemplateRegexStart);
            foreach (string templatename in templatenames)
            {
                string templatename2 = Regex.Escape(templatename.Replace('_', ' ')).Replace(@"\ ", @"[_ ]");
                theRegex.Append(CaseInsensitive(templatename2) + "|");
            }
            theRegex[theRegex.Length - 1] = ')';
            theRegex.Append(NestedTemplateRegexEnd);
            return new Regex(theRegex.ToString(), RegexOptions.Compiled);
        }
        public static bool IsSubnodeOf(TreeNode refnode, TreeNode testnode)
        {
            for (TreeNode t = testnode; t != null; t = t.Parent)
            {
                if (ReferenceEquals(refnode, t))
                    return true;
            }
            return false;
        }
        public static string ListToStringCommaSeparator(List<string> items)
        {
            return string.Join(", ", items.ToArray());
        }
        public static string UnescapeXML(string s)
        {
            if (string.IsNullOrEmpty(s)) return s;
            string returnString = s;
            returnString = returnString.Replace("&apos;", "'");
            returnString = returnString.Replace("&quot;", "\"");
            returnString = returnString.Replace("&gt;", ">");
            returnString = returnString.Replace("&lt;", "<");
            returnString = returnString.Replace("&amp;", "&");
            return returnString;
        }
    }
}

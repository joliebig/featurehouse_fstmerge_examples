using System;
using System.Text.RegularExpressions;
using System.Collections.Generic;
using System.Collections.ObjectModel;
namespace WikiFunctions
{
    public static class Namespace
    {
        public const int Media = -2;
        public const int Special = -1;
        public const int Article = 0;
        public const int Talk = 1;
        public const int User = 2;
        public const int UserTalk = 3;
        public const int Project = 4;
        public const int ProjectTalk = 5;
        public const int File = 6;
        public const int FileTalk = 7;
        public const int MediaWiki = 8;
        public const int MediaWikiTalk = 9;
        public const int Template = 10;
        public const int TemplateTalk = 11;
        public const int Help = 12;
        public const int HelpTalk = 13;
        public const int Category = 14;
        public const int CategoryTalk = 15;
        public const int FirstCustom = 100;
        public const int FirstCustomTalk = 101;
        public static readonly int Mainspace = Article;
        public static readonly int Image = File;
        public static readonly int ImageTalk = FileTalk;
        public static ReadOnlyCollection<int> StandardNamespaces
        { get; private set; }
        static Namespace()
        {
            var ns = new List<int>();
            ns.Add(Media);
            ns.Add(Special);
            for (int i = Talk; i <= CategoryTalk; i++)
                ns.Add(i);
            StandardNamespaces = new ReadOnlyCollection<int>(ns);
        }
        public static int Determine(string articleTitle)
        {
            articleTitle = Tools.TurnFirstToUpper(Regex.Replace(articleTitle, @"\s*:\s*", ":"));
            articleTitle = Tools.WikiDecode(articleTitle);
            if (!articleTitle.Contains(":"))
                return 0;
            foreach (KeyValuePair<int, string> k in Variables.Namespaces)
            {
                if (articleTitle.StartsWith(k.Value))
                    return articleTitle.Length >= k.Value.Length ? k.Key : 0;
            }
            foreach (KeyValuePair<int, List<string> > k in Variables.NamespaceAliases)
            {
                foreach (string s in k.Value)
                {
                    if (articleTitle.StartsWith(s))
                        return articleTitle.Length >= s.Length ? k.Key : 0;
                }
            }
            foreach (KeyValuePair<int, string> k in Variables.CanonicalNamespaces)
            {
                if (articleTitle.StartsWith(k.Value))
                    return articleTitle.Length >= k.Value.Length ? k.Key : 0;
            }
            return 0;
        }
        public static bool IsMainSpace(string articleTitle)
        {
            return Determine(articleTitle) == 0;
        }
        public static bool IsMainSpace(int ns)
        {
            return ns == Article;
        }
        public static bool IsImportant(string articleTitle)
        {
            return IsImportant(Determine(articleTitle));
        }
        public static bool IsImportant(int key)
        {
            return (key == Article || key == File
                || key == Template || key == Category);
        }
        public static bool IsTalk(string articleTitle)
        {
            return IsTalk(Determine(articleTitle));
        }
        public static bool IsTalk(int key)
        {
            return (key % 2 == 1);
        }
        public static bool IsUserSpace(string articleTitle)
        {
            return IsUserTalk(articleTitle) || IsUserPage(articleTitle);
        }
        public static bool IsUserTalk(string articleTitle)
        {
            return articleTitle.StartsWith(Variables.Namespaces[UserTalk]);
        }
        public static bool IsUserPage(string articleTitle)
        {
            return Determine(articleTitle) == User;
        }
        public static bool IsSpecial(int ns)
        {
            return ns < 0;
        }
        private static readonly Regex NormalizeColon = new Regex(@"\s*:$", RegexOptions.Compiled);
        public static string Normalize(string ns, int nsId)
        {
            ns = Tools.WikiDecode(NormalizeColon.Replace(ns, ":"));
            if (Variables.Namespaces[nsId].Equals(ns, StringComparison.InvariantCultureIgnoreCase))
                return Variables.Namespaces[nsId];
            foreach (string s in Variables.NamespaceAliases[nsId])
            {
                if (s.Equals(ns, StringComparison.InvariantCultureIgnoreCase))
                    return s;
            }
            return ns;
        }
        public static bool VerifyNamespaces(Dictionary<int, string> namespaces)
        {
            foreach (var ns in StandardNamespaces)
            {
                if (!namespaces.ContainsKey(ns)) return false;
            }
            if (namespaces.ContainsKey(Mainspace)) return false;
            foreach (var s in namespaces.Values)
            {
                if (s.Length < 2 || !s.EndsWith(":"))
                    return false;
            }
            return true;
        }
    }
}

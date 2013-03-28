using System;
using System.IO;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public static class PathUtils
    {
        private static string defaultProgramDataFolder = Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData),
            Path.Combine("CruiseControl.NET", "Server"));
        public static bool Match(string pattern, string str, bool isCaseSensitive)
        {
            if (pattern.Equals("*.*"))
            {
                pattern = "*";
            }
            char[] patArr = pattern.ToCharArray();
            char[] strArr = str.ToCharArray();
            int patIdxStart = 0;
            int patIdxEnd = patArr.Length - 1;
            int strIdxStart = 0;
            int strIdxEnd = strArr.Length - 1;
            char ch;
            bool containsStar = false;
            for (int i = 0; i < patArr.Length; i++)
            {
                if (patArr[i] == '*')
                {
                    containsStar = true;
                    break;
                }
            }
            if (!containsStar)
            {
                if (patIdxEnd != strIdxEnd)
                {
                    return false;
                }
                for (int i = 0; i <= patIdxEnd; i++)
                {
                    ch = patArr[i];
                    if (ch != '?')
                    {
                        if (isCaseSensitive && ch != strArr[i])
                        {
                            return false;
                        }
                        if (!isCaseSensitive && Char.ToUpper(ch) != Char.ToUpper(strArr[i]))
                        {
                            return false;
                        }
                    }
                }
                return true;
            }
            if (patIdxEnd == 0)
            {
                return true;
            }
            while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd)
            {
                if (ch != '?')
                {
                    if (isCaseSensitive && ch != strArr[strIdxStart])
                    {
                        return false;
                    }
                    if (!isCaseSensitive && Char.ToUpper(ch) != Char.ToUpper(strArr[strIdxStart]))
                    {
                        return false;
                    }
                }
                patIdxStart++;
                strIdxStart++;
            }
            if (strIdxStart > strIdxEnd)
            {
                for (int i = patIdxStart; i <= patIdxEnd; i++)
                {
                    if (patArr[i] != '*')
                    {
                        return false;
                    }
                }
                return true;
            }
            while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd)
            {
                if (ch != '?')
                {
                    if (isCaseSensitive && ch != strArr[strIdxEnd])
                    {
                        return false;
                    }
                    if (!isCaseSensitive && Char.ToUpper(ch) != Char.ToUpper(strArr[strIdxEnd]))
                    {
                        return false;
                    }
                }
                patIdxEnd--;
                strIdxEnd--;
            }
            if (strIdxStart > strIdxEnd)
            {
                for (int i = patIdxStart; i <= patIdxEnd; i++)
                {
                    if (patArr[i] != '*')
                    {
                        return false;
                    }
                }
                return true;
            }
            while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd)
            {
                int patIdxTmp = -1;
                for (int i = patIdxStart + 1; i <= patIdxEnd; i++)
                {
                    if (patArr[i] == '*')
                    {
                        patIdxTmp = i;
                        break;
                    }
                }
                if (patIdxTmp == patIdxStart + 1)
                {
                    patIdxStart++;
                    continue;
                }
                int patLength = (patIdxTmp - patIdxStart - 1);
                int strLength = (strIdxEnd - strIdxStart + 1);
                int foundIdx = -1;
                for (int i = 0; i <= strLength - patLength; i++)
                {
                    bool mismatch = false;
                    for (int j = 0; j < patLength; j++)
                    {
                        ch = patArr[patIdxStart + j + 1];
                        if (ch != '?')
                        {
                            if (isCaseSensitive && ch != strArr[strIdxStart + i + j])
                            {
                                mismatch = true;
                                break;
                            }
                            if (!isCaseSensitive &&
                                Char.ToUpper(ch) != Char.ToUpper(strArr[strIdxStart + i + j]))
                            {
                                mismatch = true;
                                break;
                            }
                        }
                    }
                    if (!mismatch)
                    {
                        foundIdx = strIdxStart + i;
                        break;
                    }
                }
                if (foundIdx == -1)
                {
                    return false;
                }
                patIdxStart = patIdxTmp;
                strIdxStart = foundIdx + patLength;
            }
            for (int i = patIdxStart; i <= patIdxEnd; i++)
            {
                if (patArr[i] != '*')
                {
                    return false;
                }
            }
            return true;
        }
        public static bool MatchPath(string pattern, string str, bool isCaseSensitive)
        {
            if (string.IsNullOrEmpty(str))
                return false;
            string[] patDirs = SplitPath(pattern);
            if (IsSeperator(Char.Parse(str.Substring(0, 1))))
            {
                if (!IsSeperator(Char.Parse(pattern.Substring(0, 1))))
                {
                    if (patDirs.Length == 0 || !patDirs[0].Equals("**"))
                    {
                        return false;
                    }
                }
            }
            string[] strDirs = SplitPath(str);
            int patIdxStart = 0;
            int patIdxEnd = patDirs.Length - 1;
            int strIdxStart = 0;
            int strIdxEnd = strDirs.Length - 1;
            while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd)
            {
                string patDir = patDirs[patIdxStart];
                if (patDir.Equals("**"))
                {
                    break;
                }
                if (!Match(patDir, strDirs[strIdxStart], isCaseSensitive))
                {
                    return false;
                }
                patIdxStart++;
                strIdxStart++;
            }
            if (strIdxStart > strIdxEnd)
            {
                for (int i = patIdxStart; i <= patIdxEnd; i++)
                {
                    if (!patDirs[i].Equals("**"))
                    {
                        return false;
                    }
                }
                return true;
            }
            else
            {
                if (patIdxStart > patIdxEnd)
                {
                    return false;
                }
            }
            while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd)
            {
                string patDir = patDirs[patIdxEnd];
                if (patDir.Equals("**"))
                {
                    break;
                }
                if (!Match(patDir, strDirs[strIdxEnd], isCaseSensitive))
                {
                    return false;
                }
                patIdxEnd--;
                strIdxEnd--;
            }
            if (strIdxStart > strIdxEnd)
            {
                for (int i = patIdxStart; i <= patIdxEnd; i++)
                {
                    if (!patDirs[i].Equals("**"))
                    {
                        return false;
                    }
                }
                return true;
            }
            while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd)
            {
                int patIdxTmp = -1;
                for (int i = patIdxStart + 1; i <= patIdxEnd; i++)
                {
                    if (patDirs[i].Equals("**"))
                    {
                        patIdxTmp = i;
                        break;
                    }
                }
                if (patIdxTmp == patIdxStart + 1)
                {
                    patIdxStart++;
                    continue;
                }
                int patLength = (patIdxTmp - patIdxStart - 1);
                int strLength = (strIdxEnd - strIdxStart + 1);
                int foundIdx = -1;
                for (int i = 0; i <= strLength - patLength; i++)
                {
                    bool mismatch = false;
                    for (int j = 0; j < patLength; j++)
                    {
                        string subPat = patDirs[patIdxStart + j + 1];
                        string subStr = strDirs[strIdxStart + i + j];
                        if (!Match(subPat, subStr, isCaseSensitive))
                        {
                            mismatch = true;
                            break;
                        }
                    }
                    if (!mismatch)
                    {
                        foundIdx = strIdxStart + i;
                        break;
                    }
                }
                if (foundIdx == -1)
                {
                    return false;
                }
                patIdxStart = patIdxTmp;
                strIdxStart = foundIdx + patLength;
            }
            for (int i = patIdxStart; i <= patIdxEnd; i++)
            {
                if (!patDirs[i].Equals("**"))
                {
                    return false;
                }
            }
            return true;
        }
        public static string[] SplitPath(string target)
        {
            int start = 0;
            int len = target.Length;
            int count = 0;
            char[] path = target.ToCharArray();
            for (int pos = 0; pos < len; pos++)
            {
                if (IsSeperator(path[pos]))
                {
                    if (pos != start)
                    {
                        count++;
                    }
                    start = pos + 1;
                }
            }
            if (len != start)
            {
                count++;
            }
            string[] l = new string[count];
            count = 0;
            start = 0;
            for (int pos = 0; pos < len; pos++)
            {
                if (IsSeperator(path[pos]))
                {
                    if (pos != start)
                    {
                        string tok = target.Substring(start, pos - start);
                        l[count++] = tok;
                    }
                    start = pos + 1;
                }
            }
            if (len != start)
            {
                string tok = target.Substring(start);
                l[count] = tok;
            }
            return l;
        }
        private static bool IsSeperator(char c)
        {
            char sep = Path.DirectorySeparatorChar;
            return (c == '\\' || c == '/' || c == sep);
        }
    }
}

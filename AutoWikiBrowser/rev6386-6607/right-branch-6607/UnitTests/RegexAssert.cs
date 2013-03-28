using System;
using System.Text.RegularExpressions;
using NUnit.Framework;
namespace UnitTests
{
    public static class RegexAssert
    {
        public static void IsMatch(string pattern, string input)
        {
            IsMatch(pattern, RegexOptions.None, input, "");
        }
        public static void IsMatch(string pattern, string input, string message)
        {
            IsMatch(pattern, RegexOptions.None, input, message);
        }
        public static void IsMatch(string pattern, RegexOptions options, string input)
        {
            IsMatch(pattern, options, input, "");
        }
        public static void IsMatch(string pattern, RegexOptions options, string input, string message)
        {
            IsMatch(new Regex(pattern, options), input, message);
        }
        public static void IsMatch(Regex regex, string input)
        {
            IsMatch(regex, input, "");
        }
        public static void IsMatch(Regex regex, string input, string message)
        {
            if (!regex.IsMatch(input)) throw new AssertionException(string.Format(
                 "The string <{0}> does not match the given regex <{1}>{2}", input, regex,
                 (message.Length == 0 ? "" : ": " + message)));
        }
        public static void NoMatch(string pattern, string input)
        {
            NoMatch(pattern, RegexOptions.None, input, "");
        }
        public static void NoMatch(string pattern, string input, string message)
        {
            NoMatch(pattern, RegexOptions.None, input, message);
        }
        public static void NoMatch(string pattern, RegexOptions options, string input)
        {
            NoMatch(pattern, options, input, "");
        }
        public static void NoMatch(string pattern, RegexOptions options, string input, string message)
        {
            NoMatch(new Regex(pattern, options), input, message);
        }
        public static void NoMatch(Regex regex, string input)
        {
            NoMatch(regex, input, "");
        }
        public static void NoMatch(Regex regex, string input, string message)
        {
            new AssertionHelper().Expect(!regex.IsMatch(input), "The string matches the given regex"
                + (message.Length == 0 ? "" : ": " + message));
        }
        public static void Matches(string expected, string pattern, string input)
        {
            Matches(expected, pattern, RegexOptions.None, input, "");
        }
        public static void Matches(string expected, string pattern, string input, string message)
        {
            Matches(expected, pattern, RegexOptions.None, input, message);
        }
        public static void Matches(string expected, string pattern, RegexOptions options, string input, string message)
        {
            Matches(expected, new Regex(pattern, options), input, message);
        }
        public static void Matches(string expected, Regex regex, string input)
        {
            Matches(expected, regex, input, "");
        }
        public static void Matches(string expected, Regex regex, string input, string message)
        {
            string m = regex.Match(input).Value;
            if (m != expected) throw new AssertionException(string.Format("  Expected match: <{0}>, actual match: <{1}>{2}",
                expected, m, (message.Length == 0 ? "" : ": " + message)));
        }
        public static void NotMatches(string expected, string pattern, string input)
        {
            NotMatches(expected, pattern, RegexOptions.None, input, "");
        }
        public static void NotMatches(string expected, string pattern, string input, string message)
        {
            NotMatches(expected, pattern, RegexOptions.None, input, message);
        }
        public static void NotMatches(string expected, string pattern, RegexOptions options, string input, string message)
        {
            NotMatches(expected, new Regex(pattern, options), input, message);
        }
        public static void NotMatches(string expected, Regex regex, string input)
        {
            NotMatches(expected, regex, input, "");
        }
        public static void NotMatches(string expected, Regex regex, string input, string message)
        {
            string m = regex.Match(input).Value;
            if (m == expected) throw new AssertionException(string.Format("  Regex unexpectedly matched <{0}>{1}",
                expected, (message.Length == 0 ? "" : ": " + message)));
        }
        public static void Matches(Regex regex, string input, params string[] expected)
        {
            Matches("", regex, input, expected);
        }
        public static void Matches(string message, Regex regex, string input, params string[] expected)
        {
            if (expected.Length == 0)
                throw new ArgumentException("Expected is empty", "expected");
            CollectionAssert.AreEqual(expected, MatchesToStrings(regex.Matches(input)), message);
        }
        private static string[] MatchesToStrings(MatchCollection matches)
        {
            string[] strings = new string[matches.Count];
            for (int i = 0; i < strings.Length; i++)
            {
                strings[i] = matches[i].Value;
            }
            return strings;
        }
    }
}

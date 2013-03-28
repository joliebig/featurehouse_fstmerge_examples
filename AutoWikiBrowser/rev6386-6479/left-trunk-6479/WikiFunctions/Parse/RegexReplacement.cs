using System.Text.RegularExpressions;
namespace WikiFunctions.Parse
{
    struct RegexReplacement
    {
        public RegexReplacement(Regex regex, string replacement)
        {
            Regex = regex;
            Replacement = replacement;
        }
        public RegexReplacement(string pattern, RegexOptions options, string replacement)
            : this(new Regex(pattern, options), replacement)
        {
        }
        public RegexReplacement(string pattern, string replacement)
            : this(pattern, RegexOptions.Compiled, replacement)
        {
        }
        public readonly Regex Regex;
        public readonly string Replacement;
    }
}

using System.Text.RegularExpressions;
namespace WikiFunctions
{
    public class RegexArticleComparer : IArticleComparer
    {
        public RegexArticleComparer(Regex comparator)
        {
            Comparator = comparator;
        }
        public bool Matches(Article article)
        {
            return Comparator.IsMatch(article.ArticleText);
        }
        private readonly Regex Comparator;
    }
    public class DynamicRegexArticleComparer : IArticleComparer
    {
        public DynamicRegexArticleComparer(string comparator, RegexOptions options)
        {
            Comparator = comparator;
            Options = (options & ~RegexOptions.Compiled);
            new Regex(Tools.ApplyKeyWords("a", comparator), options);
        }
        public bool Matches(Article article)
        {
            return Regex.IsMatch(article.ArticleText, Tools.ApplyKeyWords(article.Name, Comparator), Options);
        }
        private readonly string Comparator;
        private readonly RegexOptions Options;
    }
}

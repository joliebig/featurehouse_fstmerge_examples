using System;
namespace WikiFunctions
{
    public class CaseSensitiveArticleComparer : IArticleComparer
    {
        public CaseSensitiveArticleComparer(string comparator)
        {
            Comparator = comparator;
        }
        public bool Matches(Article article)
        {
            return article.ArticleText.Contains(Comparator);
        }
        private readonly string Comparator;
    }
    public class CaseInsensitiveArticleComparer : IArticleComparer
    {
        public CaseInsensitiveArticleComparer(string comparator)
        {
            Comparator = comparator;
        }
        public bool Matches(Article article)
        {
            return article.ArticleText.IndexOf(Comparator, StringComparison.CurrentCultureIgnoreCase) >= 0;
        }
        private readonly string Comparator;
    }
    public class CaseSensitiveArticleComparerWithKeywords : IArticleComparer
    {
        public CaseSensitiveArticleComparerWithKeywords(string comparator)
        {
            Comparator = comparator;
        }
        public bool Matches(Article article)
        {
            return article.ArticleText.Contains(Tools.ApplyKeyWords(article.Name, Comparator));
        }
        private readonly string Comparator;
    }
    public class CaseInsensitiveArticleComparerWithKeywords : IArticleComparer
    {
        public CaseInsensitiveArticleComparerWithKeywords(string comparator)
        {
            Comparator = comparator;
        }
        public bool Matches(Article article)
        {
            return article.ArticleText.IndexOf(Tools.ApplyKeyWords(article.Name, Comparator), StringComparison.CurrentCultureIgnoreCase) >= 0;
        }
        private readonly string Comparator;
    }
}

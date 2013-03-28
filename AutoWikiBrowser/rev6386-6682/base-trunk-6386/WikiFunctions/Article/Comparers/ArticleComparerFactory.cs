using System;
using System.Text.RegularExpressions;
using System.Windows.Forms;
namespace WikiFunctions
{
    public static class ArticleComparerFactory
    {
        public static IArticleComparer Create(string comparator, bool isCaseSensitive, bool isRegex, bool isSingleLine, bool isMultiLine)
        {
            if (comparator == null)
                throw new ArgumentNullException("comparator");
            if (isRegex)
            {
                try
                {
                    RegexOptions opts = RegexOptions.None;
                    if (!isCaseSensitive)
                        opts |= RegexOptions.IgnoreCase;
                    if (isSingleLine)
                        opts |= RegexOptions.Singleline;
                    if (isMultiLine)
                        opts |= RegexOptions.Multiline;
                    return comparator.Contains("%%")
                               ? (IArticleComparer)new DynamicRegexArticleComparer(comparator, opts)
                               : new RegexArticleComparer(new Regex(comparator, opts | RegexOptions.Compiled));
                }
                catch (ArgumentException ex)
                {
                    MessageBox.Show(ex.Message, "Bad Regex");
                    throw;
                }
            }
            if (comparator.Contains("%%"))
                return isCaseSensitive
                    ? (IArticleComparer)new CaseSensitiveArticleComparerWithKeywords(comparator)
                    : new CaseInsensitiveArticleComparerWithKeywords(comparator);
            return isCaseSensitive
                       ? (IArticleComparer)new CaseSensitiveArticleComparer(comparator)
                       : new CaseInsensitiveArticleComparer(comparator);
        }
    }
    public interface IArticleComparer
    {
        bool Matches(Article article);
    }
}

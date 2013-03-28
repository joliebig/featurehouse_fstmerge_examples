using System;
using System.Collections.Generic;
using System.Text.RegularExpressions;
using WikiFunctions.Parse;
namespace WikiFunctions.DBScanner
{
    public abstract class Scan
    {
        public virtual bool Check(ArticleInfo article)
        {
            return true;
        }
    }
    public class IsNotRedirect : Scan
    {
        public override bool Check(ArticleInfo article)
        {
            return (!Tools.IsRedirect(article.Text));
        }
    }
    public class TextContainsRegex : Scan
    {
        public TextContainsRegex(params Regex[] containsR)
        {
            Contains = containsR;
        }
        private readonly Regex[] Contains;
        public override bool Check(ArticleInfo article)
        {
            foreach (Regex r in Contains)
            {
                if (!r.IsMatch(article.Text))
                    return false;
            }
            return true;
        }
    }
    public class TextDoesNotContainRegex : TextContainsRegex
    {
        public TextDoesNotContainRegex(params Regex[] notContainsR)
            : base(notContainsR)
        { }
        public override bool Check(ArticleInfo article)
        {
            return !base.Check(article);
        }
    }
    public class TitleContains : Scan
    {
        public TitleContains(Regex containsR)
        {
            Contains = containsR;
        }
        private readonly Regex Contains;
        public override bool Check(ArticleInfo article)
        {
            return (Contains.IsMatch(article.Title));
        }
    }
    public class TitleDoesNotContain : TitleContains
    {
        public TitleDoesNotContain(Regex notContainsR)
            : base(notContainsR)
        { }
        public override bool Check(ArticleInfo article)
        {
            return !base.Check(article);
        }
    }
    public enum MoreLessThan { LessThan, MoreThan, EqualTo }
    public class CountCharacters : Scan
    {
        public CountCharacters(MoreLessThan value, int characters)
        {
            M = value;
            Test = characters;
        }
        private readonly MoreLessThan M;
        private readonly int Test;
        private int Actual;
        public override bool Check(ArticleInfo article)
        {
            Actual = article.Text.Length;
            if (M == MoreLessThan.MoreThan)
                return (Actual > Test);
            if (M == MoreLessThan.LessThan)
                return (Actual < Test);
            return (Actual == Test);
        }
    }
    public class CountLinks : Scan
    {
        public CountLinks(MoreLessThan value, int links)
        {
            M = value;
            Test = links;
        }
        private readonly MoreLessThan M;
        private readonly int Test;
        private int Actual;
        public override bool Check(ArticleInfo article)
        {
            Actual = WikiRegexes.SimpleWikiLink.Matches(article.Text).Count;
            if (M == MoreLessThan.MoreThan)
                return (Actual > Test);
            if (M == MoreLessThan.LessThan)
                return (Actual < Test);
            return (Actual == Test);
        }
    }
    public class CountWords : Scan
    {
        public CountWords(MoreLessThan value, int words)
        {
            M = value;
            Test = words;
        }
        private readonly MoreLessThan M;
        private readonly int Test;
        private int Actual;
        public override bool Check(ArticleInfo article)
        {
            Actual = Tools.WordCount(article.Text);
            if (M == MoreLessThan.MoreThan)
                return (Actual > Test);
            if (M == MoreLessThan.LessThan)
                return (Actual < Test);
            return (Actual == Test);
        }
    }
    public class CheckNamespace : Scan
    {
        public CheckNamespace(List<int> nameSpaces)
        {
            Namespaces = nameSpaces;
        }
        private readonly List<int> Namespaces;
        public override bool Check(ArticleInfo article)
        {
            return (Namespaces.Contains(Namespace.Determine(article.Title)));
        }
    }
    public class HasBadLinks : Scan
    {
        public override bool Check(ArticleInfo article)
        {
            foreach (Match m in WikiRegexes.SimpleWikiLink.Matches(article.Text))
            {
                string y = System.Web.HttpUtility.UrlDecode(m.Value.Replace("+", "%2B"));
                if (m.Value != y)
                    return false;
            }
            return true;
        }
    }
    public class HasNoBoldTitle : Scan
    {
        private readonly Parsers P = new Parsers();
        private bool Skip;
        public override bool Check(ArticleInfo article)
        {
            P.BoldTitle(article.Text, article.Title, out Skip);
            return !Skip;
        }
    }
    public class CiteTemplateDates : Scan
    {
        private readonly Parsers P = new Parsers();
        private bool Skip;
        public override bool Check(ArticleInfo article)
        {
            P.CiteTemplateDates(article.Text, out Skip);
            return !Skip;
        }
    }
    public class PeopleCategories : Scan
    {
        private readonly Parsers P = new Parsers();
        private bool Skip;
        public override bool Check(ArticleInfo article)
        {
            P.FixPeopleCategories(article.Text, article.Title, false, out Skip);
            return !Skip;
        }
    }
    public class UnbalancedBrackets : Scan
    {
        public override bool Check(ArticleInfo article)
        {
            int bracketLength = 0;
            return (Parsers.UnbalancedBrackets(article.Text, ref bracketLength) != -1);
        }
    }
    public class HasHTMLEntities : Scan
    {
        private readonly Parsers P = new Parsers();
        private bool Skip;
        public override bool Check(ArticleInfo article)
        {
            P.Unicodify(article.Text, out Skip);
            return !Skip;
        }
    }
    public class HasSimpleLinks : Scan
    {
        public override bool Check(ArticleInfo article)
        {
            foreach (Match m in WikiRegexes.PipedWikiLink.Matches(article.Text))
            {
                string a = m.Groups[1].Value;
                string b = m.Groups[2].Value;
                if (a == b || Tools.TurnFirstToLower(a) == b)
                    return true;
                if (a + "s" == b || Tools.TurnFirstToLower(a) + "s" == b)
                    return true;
            }
            return false;
        }
    }
    public class HasSectionError : Scan
    {
        private bool Skip = true;
        public override bool Check(ArticleInfo article)
        {
            Parsers.FixHeadings(article.Text, article.Title, out Skip);
            return !Skip;
        }
    }
    public class HasUnbulletedLinks : Scan
    {
        private readonly Regex BulletRegex = new Regex(@"External [Ll]inks? ? ?={1,4} ? ?(
){0,3}\[?http", RegexOptions.Compiled);
        public override bool Check(ArticleInfo article)
        {
            return (BulletRegex.IsMatch(article.Text));
        }
    }
    public class LivingPerson : Scan
    {
        private bool Skip = true;
        public override bool Check(ArticleInfo article)
        {
            Parsers.LivingPeople(article.Text, out Skip);
            return !Skip;
        }
    }
    public class MissingDefaultsort : Scan
    {
        private bool Skip = true;
        public override bool Check(ArticleInfo article)
        {
            Parsers.ChangeToDefaultSort(article.Text, article.Title, out Skip);
            return !Skip;
        }
    }
    public class Typo : Scan
    {
        private readonly RegExTypoFix Retf = new RegExTypoFix(false);
        public override bool Check(ArticleInfo article)
        {
            return Retf.DetectTypo(article.Text, article.Title);
        }
    }
    public class UnCategorised : Scan
    {
        public override bool Check(ArticleInfo article)
        {
            if (WikiRegexes.Category.IsMatch(article.Text))
                return false;
            foreach (Match m in WikiRegexes.Template.Matches(article.Text))
            {
                if (!m.Value.Contains("stub"))
                    return false;
            }
            return true;
        }
    }
    public class DateRange : Scan
    {
        private readonly DateTime From_, To;
        public DateRange(DateTime from_, DateTime to)
        {
            From_ = from_;
            To = to;
        }
        public override bool Check(ArticleInfo article)
        {
            DateTime timestamp;
            if (DateTime.TryParse(article.Timestamp, out timestamp))
                return ((DateTime.Compare(timestamp, From_) >= 0) && (DateTime.Compare(timestamp, To) <= 0));
            return false;
        }
    }
    public class Restriction : Scan
    {
        private const string EditRest = "edit=", MoveRest = "move=";
        private readonly string Edit, Move;
        public Restriction(string editLevel, string moveLevel)
        {
            Edit = editLevel;
            Move = moveLevel;
        }
        public override bool Check(ArticleInfo article)
        {
            bool restrictionStringEmpty = (string.IsNullOrEmpty(article.Restrictions));
            bool noEditRestriction = string.IsNullOrEmpty(Edit);
            bool noMoveRestriction = string.IsNullOrEmpty(Move);
            if (restrictionStringEmpty)
            {
                return (noEditRestriction && noMoveRestriction);
            }
            if (!noEditRestriction && !article.Restrictions.Contains(EditRest + Edit))
                return false;
            return noMoveRestriction || article.Restrictions.Contains(MoveRest + Move);
        }
    }
}

namespace WikiFunctions.Plugin
{
    public interface IModule
    {
        string ProcessArticle(string articleText, string articleTitle, int @namespace, out string summary,
            out bool skip);
    }
    public interface ISkipOptions
    {
        bool SkipNoUnicode
        { get; }
        bool SkipNoTag
        { get; }
        bool SkipNoHeaderError
        { get; }
        bool SkipNoBoldTitle
        { get; }
        bool SkipNoBulletedLink
        { get; }
        bool SkipNoBadLink
        { get; }
        bool SkipNoDefaultSortAdded
        { get; }
        bool SkipNoUserTalkTemplatesSubstd
        { get; }
        bool SkipNoCiteTemplateDatesFixed
        { get; }
        bool SkipNoPeopleCategoriesFixed
        { get; }
    }
}

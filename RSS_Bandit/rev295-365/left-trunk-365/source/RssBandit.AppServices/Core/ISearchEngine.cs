namespace RssBandit.WebSearch
{
 public interface ISearchEngine
 {
  string Title { get; }
  string SearchLink { get; }
  string Description { get; }
  string ImageName { get; }
  bool IsActive { get; }
  bool ReturnRssResult { get; }
  bool MergeRssResult { get; }
 }
}

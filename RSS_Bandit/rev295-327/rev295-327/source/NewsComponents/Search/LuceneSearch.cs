using System; 
using System.Collections; 
using System.Collections.Generic; 
using System.Collections.Specialized; 
using System.Globalization; 
using System.IO; 
using System.Runtime.InteropServices; 
using System.Security; 
using System.Text; 
using Lucene.Net.Analysis; 
using Lucene.Net.Analysis.Snowball; 
using Lucene.Net.Analysis.Standard; 
using Lucene.Net.Documents; 
using Lucene.Net.Index; 
using Lucene.Net.QueryParsers; 
using Lucene.Net.Search; 
using NewsComponents.Collections; 
using NewsComponents.Feed; 
using NewsComponents.Resources; 
using NewsComponents.Utils; 
using RssBandit.Common.Logging; namespace  NewsComponents.Search {
	
 public class  LuceneSearch {
		
  public  const string HelpLink = "http://www.rssbandit.org/docs/search_query_syntax.htm"; 
  public  event LuceneIndexingProgressEventHandler IndexingProgress; 
  public  event EventHandler IndexingFinished; 
  public static  string DefaultLanguage = "en";
 
  private readonly  NewsFeedProperty indexRelevantPropertyChanges =
   NewsFeedProperty.FeedLink |
   NewsFeedProperty.FeedUrl |
   NewsFeedProperty.FeedTitle |
   NewsFeedProperty.FeedCategory |
   NewsFeedProperty.FeedDescription |
   NewsFeedProperty.FeedType |
   NewsFeedProperty.FeedItemsDeleteUndelete |
   NewsFeedProperty.FeedAdded |
   NewsFeedProperty.FeedRemoved;
 
  private readonly  NewsHandler newsHandler; 
  private readonly  LuceneIndexModifier indexModifier;
 
  private readonly  LuceneSettings settings;
 
  private  bool startIndexAll;
 
  private static readonly  log4net.ILog _log = RssBandit.Common.Logging.Log.GetLogger(typeof(LuceneSearch));
 
  public  LuceneSearch(INewsComponentsConfiguration configuration, NewsHandler newsHandler)
  {
   this.newsHandler = newsHandler;
   if (this.newsHandler == null)
    throw new ArgumentNullException("newsHandler");
   if (configuration.SearchIndexBehavior != SearchIndexBehavior.NoIndexing)
   {
    this.settings = new LuceneSettings(configuration);
    startIndexAll = (this.settings.IsRAMBasedSearch ||
     !IndexReader.IndexExists(this.settings.GetIndexDirectory()));
    this.indexModifier = new LuceneIndexModifier(this.settings);
   }
  } 
  internal  LuceneSettings Settings {
   get { return this.settings; }
  }
 
  public  Result ExecuteSearch(SearchCriteriaCollection criteria, NewsFeed[] scope, string cultureName)
  {
   if (!UseIndex)
    return null;
   Query q = BuildLuceneQuery(criteria, scope, LuceneSearch.GetAnalyzer(cultureName));
   if (q == null)
    return new Result(0, 0, GetArrayList.Empty, GetArrayList.Empty);
   IndexSearcher searcher = new IndexSearcher(this.settings.GetIndexDirectory());
   Hits hits = null;
   while (hits == null)
   {
    try {
     System.DateTime start = System.DateTime.Now;
     hits = searcher.Search(q, Sort.RELEVANCE);
     TimeSpan timeRequired = TimeSpan.FromTicks(System.DateTime.Now.Ticks - start.Ticks);
     _log.Info(String.Format("Found {0} document(s) that matched query '{1}' (time required: {2})", hits.Length(), q,timeRequired));
    } catch (BooleanQuery.TooManyClauses) {
     BooleanQuery.SetMaxClauseCount(BooleanQuery.GetMaxClauseCount()*2);
     _log.Info(String.Format("Search failed with error 'BooleanQuery.TooManyClauses'. Retry with BooleanQuery.MaxClauseCount == {0}", BooleanQuery.GetMaxClauseCount()));
    }
   }
   ArrayList items = new ArrayList(hits.Length());
   HybridDictionary matchedFeeds = new HybridDictionary();
   for (int i = 0; i < hits.Length(); i++) {
    Document doc = hits.Doc(i);
    NewsFeed f = null;
    string feedLink = doc.Get(LuceneSearch.Keyword.FeedLink);
    if (matchedFeeds.Contains(feedLink))
     f = (NewsFeed) matchedFeeds[feedLink];
                if (f == null && newsHandler.FeedsTable.ContainsKey(feedLink))
     f = newsHandler.FeedsTable[feedLink];
    if (f == null) continue;
    SearchHitNewsItem item = new SearchHitNewsItem(f,
     doc.Get(LuceneSearch.Keyword.ItemTitle),
     doc.Get(LuceneSearch.Keyword.ItemLink),
     doc.Get(LuceneSearch.IndexDocument.ItemSummary),
     doc.Get(LuceneSearch.Keyword.ItemAuthor),
     new DateTime(DateTools.StringToTime(doc.Get(LuceneSearch.Keyword.ItemDate))),
     LuceneNewsItemSearch.NewsItemIDFromUID(doc.Get(LuceneSearch.IndexDocument.ItemID)));
    items.Add(item);
    if (!matchedFeeds.Contains(feedLink))
     matchedFeeds.Add(feedLink, f);
   }
   return new Result(items.Count, matchedFeeds.Count, items, new ArrayList(matchedFeeds.Values));
  } 
  public  bool ValidateSearchCriteria(SearchCriteriaCollection criteria, string cultureName, out Exception validationException) {
   validationException = null;
   if (criteria == null || criteria.Count == 0) {
    validationException = new SearchException(ComponentsText.ExceptionSearchNoSearchCriteria);
    return false;
   }
   SearchCriteriaString criteriaProperty = null;
   foreach (ISearchCriteria sc in criteria)
   {
    criteriaProperty = sc as SearchCriteriaString;
    if (criteriaProperty != null) {
     if (StringExpressionKind.RegularExpression == criteriaProperty.WhatKind) {
      validationException = new SearchException(ComponentsText.ExceptionLuceneSearchKindNotSupported(criteriaProperty.WhatKind.ToString()));
      break;
     } else if (StringExpressionKind.XPathExpression == criteriaProperty.WhatKind) {
      validationException = new SearchException(ComponentsText.ExceptionLuceneSearchKindNotSupported(criteriaProperty.WhatKind.ToString()));
      return false;
     }
    }
   }
   try {
    if (null == BuildLuceneQuery(criteria, null, LuceneSearch.GetAnalyzer(cultureName))) {
     validationException = new SearchException(ComponentsText.ExceptionSearchQueryBuilder);
     return false;
    }
   } catch (Exception ex) {
    validationException = new SearchException(ComponentsText.ExceptionSearchQueryBuilderFatal(ex.Message), ex);
    return false;
   }
   return true;
  }
 
  private static  Query BuildLuceneQuery(SearchCriteriaCollection criteria, NewsFeed[] scope, Analyzer analyzer)
  {
   BooleanQuery masterQuery = null;
   BooleanQuery bTerms = new BooleanQuery();
   BooleanQuery bRanges = new BooleanQuery();
   for (int i=0; criteria != null && i< criteria.Count; i++)
   {
    ISearchCriteria sc = criteria[i];
    if (sc is SearchCriteriaString) {
     SearchCriteriaString c = (SearchCriteriaString) sc;
     if (string.IsNullOrEmpty(c.What))
      continue;
     if (c.Where == SearchStringElement.Undefined) {
      AddBooleanClauseShould(bTerms, QueryFromStringExpression(c, LuceneSearch.IndexDocument.ItemContent, analyzer));
     } else {
      if((c.Where & SearchStringElement.Title) > 0){
       AddBooleanClauseShould(bTerms, QueryFromStringExpression(c, LuceneSearch.Keyword.ItemTitle, analyzer));
      }
      if((c.Where & SearchStringElement.Link) > 0){
       AddBooleanClauseShould(bTerms, QueryFromStringExpression(c, LuceneSearch.Keyword.ItemLink, analyzer));
      }
      if((c.Where & SearchStringElement.Content) > 0){
       AddBooleanClauseShould(bTerms, QueryFromStringExpression(c, LuceneSearch.IndexDocument.ItemContent, analyzer));
      }
      if((c.Where & SearchStringElement.Subject) > 0){
       AddBooleanClauseShould(bTerms, QueryFromStringExpression(c, LuceneSearch.Keyword.ItemTopic, analyzer));
      }
      if((c.Where & SearchStringElement.Author) > 0){
       AddBooleanClauseShould(bTerms, QueryFromStringExpression(c, LuceneSearch.Keyword.ItemAuthor, analyzer));
      }
     }
    }
    else if (sc is SearchCriteriaAge) {
     SearchCriteriaAge c = (SearchCriteriaAge) sc;
     Term left = null, right = null;
     string pastDate = "19900101",
            pastDateTime = "199001010001";
     string futureDate = DateTimeExt.DateAsInteger(DateTime.Now.AddYears(20)).ToString(),
            futureDateTime = DateTimeExt.DateAsInteger(DateTime.Now.AddYears(20)) + "0001";
     if (c.WhatRelativeToToday.CompareTo(TimeSpan.Zero) == 0) {
      switch(c.WhatKind){
       case DateExpressionKind.Equal:
        AddBooleanClauseMust(bRanges, new PrefixQuery(new Term(LuceneSearch.Keyword.ItemDate, c.WhatAsIntDateOnly.ToString())));
        break;
       case DateExpressionKind.OlderThan:
        left = new Term(LuceneSearch.Keyword.ItemDate, pastDate);
        right = new Term(LuceneSearch.Keyword.ItemDate, DateTimeExt.DateAsInteger(c.What).ToString());
        AddBooleanClauseMust(bRanges, new RangeQuery(left, right, true));
        break;
       case DateExpressionKind.NewerThan:
        left = new Term(LuceneSearch.Keyword.ItemDate, DateTimeExt.DateAsInteger(c.What).ToString());
        right = new Term(LuceneSearch.Keyword.ItemDate, futureDate);
        AddBooleanClauseMust(bRanges, new RangeQuery(left, right, true));
        break;
       default:
        break;
      }
     } else {
      DateTime dt = DateTime.Now.ToUniversalTime().Subtract(c.WhatRelativeToToday);
      switch(c.WhatKind){
       case DateExpressionKind.OlderThan:
        left = new Term(LuceneSearch.Keyword.ItemDate, pastDateTime);
        right = new Term(LuceneSearch.Keyword.ItemDate, DateTools.TimeToString(dt.Ticks, DateTools.Resolution.MINUTE));
        AddBooleanClauseMust(bRanges, new RangeQuery(left, right, true));
        break;
       case DateExpressionKind.NewerThan:
        left = new Term(LuceneSearch.Keyword.ItemDate, DateTools.TimeToString(dt.Ticks, DateTools.Resolution.MINUTE));
        right = new Term(LuceneSearch.Keyword.ItemDate, futureDateTime);
        AddBooleanClauseMust(bRanges, new RangeQuery(left, right, true));
        break;
       default:
        break;
      }
     }
    }
    else if (sc is SearchCriteriaDateRange) {
     SearchCriteriaDateRange c = (SearchCriteriaDateRange) sc;
     Term left = new Term(LuceneSearch.Keyword.ItemDate, DateTimeExt.DateAsInteger(c.Bottom).ToString());
     Term right = new Term(LuceneSearch.Keyword.ItemDate, DateTimeExt.DateAsInteger(c.Top).ToString());
     AddBooleanClauseMust(bRanges, new RangeQuery(left, right, true));
    }
   }
   if (bTerms.GetClauses().Length > 0) {
    masterQuery = new BooleanQuery();
    AddBooleanClauseMust(masterQuery, bTerms);
   }
   if (bRanges.GetClauses().Length > 0) {
    if (masterQuery != null)
     AddBooleanClauseMust(masterQuery, bRanges);
    else
     masterQuery = bRanges;
   }
   if (scope != null && scope.Length > 0 && masterQuery != null)
   {
    StringBuilder scopeQuery = new StringBuilder("(");
    for (int i = 0; i < scope.Length; i++) {
     scopeQuery.Append(scope[i].id);
     scopeQuery.Append(" ");
    }
    scopeQuery[scopeQuery.Length-1] = ')';
    AddBooleanClauseMust(masterQuery,
                         QueryFromStringExpression(scopeQuery.ToString(), LuceneSearch.IndexDocument.FeedID, analyzer));
   }
   return masterQuery;
  }
 
  private static  Query QueryFromStringExpression(SearchCriteriaString c, string field, Analyzer a) {
   if (c.WhatKind == StringExpressionKind.RegularExpression) {
    throw new NotSupportedException(ComponentsText.ExceptionLuceneSearchKindNotSupported(c.WhatKind.ToString()));
   } else {
    return QueryFromStringExpression(c.What, field, a);
   }
  }
 
  private static  Query QueryFromStringExpression(string expression, string field, Analyzer a) {
   return new QueryParser(field, a).Parse(expression);
  }
 
  private static  void AddBooleanClauseMust(BooleanQuery bq, Query q) {
   bq.Add(q, BooleanClause.Occur.MUST);
  }
 
  private static  void AddBooleanClauseShould(BooleanQuery bq, Query q) {
   bq.Add(q, BooleanClause.Occur.SHOULD);
  }
 
  public  bool IsIndexRelevantChange(NewsFeedProperty changedProperty) {
   return (indexRelevantPropertyChanges & changedProperty) != NewsFeedProperty.None;
  }
 
  public  void CheckIndex() {
   CheckIndex(false);
  }
 
  public  void CheckIndex(bool force)
  {
   if (!UseIndex) return;
   bool restartIndexing = false;
   bool fileBasedIndex = this.settings.IndexPath != null &&
                         Directory.Exists(this.settings.IndexPath);
   string indexingStateFile = this.RestartIndexingStateFile;
   DateTime indexModifiedAt = DateTime.MinValue;
   if (fileBasedIndex)
   {
    indexModifiedAt = Directory.GetLastWriteTimeUtc(this.settings.IndexPath);
    bool indexStateFileExists = (indexingStateFile != null && File.Exists(indexingStateFile));
    if ((force || startIndexAll) && indexStateFileExists)
     FileHelper.Delete(indexingStateFile);
    restartIndexing = (! startIndexAll && indexStateFileExists);
   } else {
    startIndexAll = true;
   }
   if (force || restartIndexing || startIndexAll) {
    IDictionary restartInfo = null;
    DictionaryEntry lastIndexed = new DictionaryEntry();
    if (restartIndexing) {
     restartInfo = ReadIndexingRestartStateFileContent(indexingStateFile, out lastIndexed);
    }
    if (restartInfo == null)
     restartInfo = new HybridDictionary();
    if (startIndexAll)
     this.indexModifier.CreateIndex();
    LuceneIndexer indexer = CreateIndexer();
    indexer.IndexingFinished += OnIndexingFinished;
    indexer.IndexingProgress += OnIndexingProgress;
    indexer.IndexAll(restartInfo, lastIndexed);
   }
   else if (fileBasedIndex)
   {
    DateTime lastIndexModification = this.settings.LastIndexOptimization;
    int compareResult = indexModifiedAt.CompareTo(lastIndexModification);
    if (compareResult > 0) {
     this.indexModifier.FinishedIndexOperation += OnIndexModifierFinishedIndexOperation;
     this.IndexOptimize();
    } else if (compareResult != 0) {
     this.settings.LastIndexOptimization = Directory.GetLastWriteTimeUtc(this.settings.IndexPath);
    }
   }
  }
 
  private  void OnIndexModifierFinishedIndexOperation(object sender, FinishedIndexOperationEventArgs e) {
   if (e.Operation.Action == IndexOperation.OptimizeIndex) {
    this.indexModifier.FinishedIndexOperation -= OnIndexModifierFinishedIndexOperation;
    this.settings.LastIndexOptimization = Directory.GetLastWriteTimeUtc(this.settings.IndexPath);
   }
  }
 
  public  void IndexAdd(IList<NewsItem> newsItems) {
   if (!UseIndex || newsItems == null) return;
   try {
    LuceneIndexer indexer = CreateIndexer();
    indexer.IndexNewsItems(newsItems);
   } catch (Exception ex) {
    Log.Error("Failure while add item(s) to search index.", ex);
   }
  }
 
  public  void IndexAdd(NewsItem item) {
   if (!UseIndex || item == null) return;
   this.IndexAdd(new NewsItem[]{item});
  }
 
  public  void IndexRemove(NewsItem[] newsItems) {
   if (!UseIndex || newsItems == null) return;
   try {
    LuceneIndexer indexer = CreateIndexer();
    indexer.RemoveNewsItems(newsItems);
   } catch (Exception ex) {
    Log.Error("Failure while remove item(s) from search index.", ex);
   }
  }
 
  public  void IndexRemove(NewsItem newsItem) {
   if (!UseIndex || newsItem == null) return;
   this.IndexRemove(new NewsItem[] {newsItem});
  }
 
  public  void IndexRemove(string feedID) {
   if (!UseIndex || string.IsNullOrEmpty(feedID)) return;
   try {
    LuceneIndexer indexer = CreateIndexer();
    indexer.RemoveFeed(feedID);
   } catch (Exception ex) {
    Log.Error("Failure while remove item(s) from search index.", ex);
   }
  }
 
  public  void IndexRemoveAll() {
   if (!UseIndex) return;
   try {
    this.indexModifier.ResetIndex();
   } catch (Exception ex) {
    Log.Error("Failure while reset the whole index.", ex);
   }
  }
 
  public  void ReIndex(NewsFeed feed) {
   if (!UseIndex || feed == null) return;
   try {
    LuceneIndexer indexer = CreateIndexer();
    indexer.RemoveNewsItems(feed.id);
    indexer.IndexNewsItems(newsHandler.GetCachedItemsForFeed(feed.link));
   } catch (Exception ex) {
    Log.Error("Failure while ReIndex item(s) in search index.", ex);
   }
  } 
  public  void IndexOptimize() {
   if (!UseIndex) return;
   try {
    this.indexModifier.Optimize();
   } catch (Exception ex) {
    Log.Error("Failure while optimizing search index.", ex);
   }
  }
 
  public  void StopIndexer() {
   if (!UseIndex) return;
   this.indexModifier.StopIndexer();
  }
 
  internal static  Analyzer GetAnalyzer(NewsItem item) {
   if (item == null)
    return new StandardAnalyzer();
   return GetAnalyzer(item.Language);
  }
 
  internal static  Analyzer GetAnalyzer(string culture) {
   culture = NormalizeCulture(culture);
   switch (culture) {
    case "en": return new SnowballAnalyzer("English");
    case "de": return new SnowballAnalyzer("German");
    case "es": return new SnowballAnalyzer("Spanish");
    case "fr": return new SnowballAnalyzer("French");
    case "it": return new SnowballAnalyzer("Italian");
    case "nl-nl": return new SnowballAnalyzer("Dutch");
    case "no": return new SnowballAnalyzer("Norwegian");
    case "pt": return new SnowballAnalyzer("Portuguese");
    case "ru": return new SnowballAnalyzer("Russian");
    case "sv": return new SnowballAnalyzer("Swedish");
    default: return new StandardAnalyzer();
   }
  }
 
  internal static  string NormalizeCulture(string culture) {
   if (string.IsNullOrEmpty(culture))
    return DefaultLanguage;
   culture = culture.ToLower(CultureInfo.InvariantCulture);
   if (culture == "en" || culture.StartsWith("en-")) return "en";
   if (culture == "de" || culture.StartsWith("de-")) return "de";
   if (culture == "da" || culture.StartsWith("da-")) return "da";
   if (culture == "es" || culture.StartsWith("es-")) return "es";
   if (culture == "fi" || culture.StartsWith("fi-")) return "fi";
   if (culture == "fr" || culture.StartsWith("fr-")) return "fr";
   if (culture == "it" || culture.StartsWith("it-")) return "it";
   if (culture == "ja" || culture.StartsWith("ja-")) return "ja";
   if (culture == "ko" || culture.StartsWith("ko-")) return "ko";
   if (culture == "nl-nl") return culture;
   if (culture == "no" || culture.StartsWith("nb-") || culture.StartsWith("nn-")) return "no";
   if (culture == "pt" || culture.StartsWith("pt-")) return "pt";
   if (culture == "ru" || culture.StartsWith("ru-")) return "ru";
   if (culture == "sv" || culture.StartsWith("sv-")) return "sv";
   if (culture == "zh" || culture.StartsWith("zh-")) return "zh";
   return DefaultLanguage;
  }
 
  private  LuceneIndexer CreateIndexer() {
   return new LuceneIndexer(this.newsHandler, this.indexModifier);
  }
 
  private  bool UseIndex {
   get {
    return this.settings != null && (
           this.settings.SearchIndexBehavior != SearchIndexBehavior.NoIndexing ||
     this.indexModifier != null);
   }
  }
 
  private  bool RaiseIndexingProgress(LuceneIndexingProgressCancelEventArgs e) {
   bool toReturn = false;
   if (IndexingProgress != null) {
    IndexingProgress(this, e);
    return e.Cancel;
   }
   return toReturn;
  }
 
  private  void RaiseIndexingFinished() {
   if (IndexingFinished != null)
    try { IndexingFinished(this, EventArgs.Empty); } catch {}
  }
 
  private static  IDictionary ReadIndexingRestartStateFileContent(string indexStateFile, out DictionaryEntry lastIndexed) {
   HybridDictionary toReturn = new HybridDictionary();
   lastIndexed = new DictionaryEntry();
   if (File.Exists(indexStateFile))
   {
    using(StreamReader reader = File.OpenText(indexStateFile))
    {
     while (reader.Peek() >= 0)
     {
      DictionaryEntry de = ReadIndexingState(reader);
      if (!string.IsNullOrEmpty((string)de.Key) &&
       !string.IsNullOrEmpty((string)de.Value))
      {
       lastIndexed = de;
       if (! toReturn.Contains(de.Key))
        toReturn.Add(de.Key, de.Value);
      }
     }
    }
   }
   return toReturn;
  }
 
  private static  DictionaryEntry ReadIndexingState(TextReader reader) {
   string line = reader.ReadLine();
   if (!StringHelper.EmptyTrimOrNull(line))
   {
    string[] fields = line.Split(new char[]{'\t'});
    if (fields.Length > 1) {
     return new DictionaryEntry(fields[1], fields[0]);
    }
   }
   return new DictionaryEntry();
  }
 
  private static  void WriteIndexingState(TextWriter writer, LuceneIndexingProgressCancelEventArgs e) {
   writer.Write(e.YetIndexedFeedID);
   writer.Write("\t");
   writer.WriteLine(e.YetIndexedFeedUrl);
   writer.Flush();
  }
 
  private  string RestartIndexingStateFile {
   get {
    if (this.settings.IndexPath != null)
     return Path.Combine(this.settings.IndexPath, "index.state");
    return null;
   }
  }
 
  private  void OnIndexingFinished(object sender, EventArgs e) {
   try {
    if (File.Exists(this.RestartIndexingStateFile))
     FileHelper.Delete(this.RestartIndexingStateFile);
   } catch (Exception ex) {
    Log.Error("Cannot delete '" + this.RestartIndexingStateFile + "': " + ex.Message, ex);
   }
   RaiseIndexingFinished();
  }
 
  private  void OnIndexingProgress(object sender, LuceneIndexingProgressCancelEventArgs e) {
   if (File.Exists(this.RestartIndexingStateFile)) {
    try {
     using (StreamWriter writer = File.AppendText(this.RestartIndexingStateFile) ) {
      WriteIndexingState(writer, e);
     }
    } catch (Exception ex) {
     Log.Error("Cannot append to '" + this.RestartIndexingStateFile + "': " + ex.Message, ex);
    }
   } else {
    try {
     using (StreamWriter writer = File.CreateText(this.RestartIndexingStateFile) ) {
      WriteIndexingState(writer, e);
     }
    } catch (Exception ex) {
     Log.Error("Cannot create to '" + this.RestartIndexingStateFile + "': " + ex.Message, ex);
    }
   }
   e.Cancel = this.RaiseIndexingProgress(e);
  }
 
  public sealed class  Keyword {
			
   public  const string ItemAuthor = "author"; 
   public  const string ItemTitle = "title"; 
   public  const string ItemLink = "link"; 
   public  const string ItemDate = "date"; 
   public  const string ItemTopic = "topic"; 
   public  const string FeedLink = "feedlink"; 
   public  const string FeedUrl = "feedurl"; 
   public  const string FeedTitle = "feedtitle"; 
   public  const string FeedDescription = "feeddescription"; 
   public  const string FeedType = "feedtype";
		}
		
  internal sealed class  IndexDocument {
			
   internal  const string ItemID = "iid"; 
   internal  const string FeedID = "fid"; 
   internal  const string ItemContent = "content"; 
   internal  const string ItemSummary = "summery";
		}
		
  public class  Result {
			
   public readonly  int ItemMatchCount;
 
   public readonly  int FeedMatchCount;
 
   public  IList ItemsMatched;
 
   public  IList FeedsMatched;
 
   public  Result(int itemMatches, int feedMatches, IList itemsMatched, IList feedsMatched) {
    this.ItemMatchCount = itemMatches;
    this.FeedMatchCount = feedMatches;
    this.ItemsMatched = itemsMatched;
    this.FeedsMatched = feedsMatched;
   }

		}
		
  [ComVisible(false),Serializable] 
  public class  SearchException  : ApplicationException {
			
   public  SearchException() :
    this(ComponentsText.ExceptionSearchFatal, null) {
   }
 
   public  SearchException(string message) :
    this(message, null) {
   }
 
   public  SearchException(string message, Exception innerException) :
    base(message, innerException) {
    base.HelpLink = LuceneSearch.HelpLink;
   }

		}
		
           
     
           
           
    <<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442488353/fstmerge_var1_6179817179539605596
Result ExecuteSearch(SearchCriteriaCollection criteria, NewsFeed[] scope, IList<NewsHandler> newsHandlers, string cultureName)
  {
   if (!UseIndex)
    return null;
   Query q = BuildLuceneQuery(criteria, scope, LuceneSearch.GetAnalyzer(cultureName));
   if (q == null)
    return new Result(0, 0, GetArrayList.Empty, GetArrayList.Empty);
   IndexSearcher searcher = new IndexSearcher(this.settings.GetIndexDirectory());
   Hits hits = null;
   while (hits == null)
   {
    try {
     System.DateTime start = System.DateTime.Now;
     hits = searcher.Search(q, Sort.RELEVANCE);
     TimeSpan timeRequired = TimeSpan.FromTicks(System.DateTime.Now.Ticks - start.Ticks);
     _log.Info(String.Format("Found {0} document(s) that matched query '{1}' (time required: {2})", hits.Length(), q,timeRequired));
    } catch (BooleanQuery.TooManyClauses) {
     BooleanQuery.SetMaxClauseCount(BooleanQuery.GetMaxClauseCount()*2);
     _log.Info(String.Format("Search failed with error 'BooleanQuery.TooManyClauses'. Retry with BooleanQuery.MaxClauseCount == {0}", BooleanQuery.GetMaxClauseCount()));
    }
   }
   ArrayList items = new ArrayList(hits.Length());
   HybridDictionary matchedFeeds = new HybridDictionary();
   for (int i = 0; i < hits.Length(); i++) {
    Document doc = hits.Doc(i);
    INewsFeed f = null;
    string feedLink = doc.Get(LuceneSearch.Keyword.FeedLink);
    if (matchedFeeds.Contains(feedLink))
     f = (NewsFeed) matchedFeeds[feedLink];
                if (f == null){
                    foreach (NewsHandler h in newsHandlers)
                    {
                        if (h.FeedsTable.ContainsKey(feedLink))
                        {
                            f = h.FeedsTable[feedLink];
                            break;
                        }
                    }
                }
    if (f == null) continue;
    SearchHitNewsItem item = new SearchHitNewsItem(f,
     doc.Get(LuceneSearch.Keyword.ItemTitle),
     doc.Get(LuceneSearch.Keyword.ItemLink),
     doc.Get(LuceneSearch.IndexDocument.ItemSummary),
     doc.Get(LuceneSearch.Keyword.ItemAuthor),
     new DateTime(DateTools.StringToTime(doc.Get(LuceneSearch.Keyword.ItemDate))),
     LuceneNewsItemSearch.NewsItemIDFromUID(doc.Get(LuceneSearch.IndexDocument.ItemID)));
    items.Add(item);
    if (!matchedFeeds.Contains(feedLink))
     matchedFeeds.Add(feedLink, f);
   }
   return new Result(items.Count, matchedFeeds.Count, items, new ArrayList(matchedFeeds.Values));
  }
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/binary/fstmerge_tmp1307442488353/fstmerge_var2_7480778991521704481
 
  public  void ReIndex(INewsFeed feed, IList<NewsItem> items) {
   if (!UseIndex || feed == null) return;
   try {
    LuceneIndexer indexer = CreateIndexer();
    indexer.RemoveNewsItems(feed.id);
    indexer.IndexNewsItems(items);
   } catch (Exception ex) {
    Log.Error("Failure while ReIndex item(s) in search index.", ex);
   }
  }
	}

}

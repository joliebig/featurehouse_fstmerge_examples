using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.IO;
using System.Text;
using System.Net;
using System.Globalization;
using System.Text.RegularExpressions;
using NewsComponents.Utils;
using NewsComponents.Feed;
using NewsComponents.RelationCosmos;
namespace NewsComponents.News{
 public sealed class NntpParser{
  private static readonly log4net.ILog _log = RssBandit.Common.Logging.Log.GetLogger(typeof(NntpParser));
  private static string GoogleGroupsUrl = "http://www.google.com/groups?selm=";
  public static void PostCommentViaNntp(NewsItem item2post, NewsItem inReply2item, ICredentials credentials){
   PostCommentViaNntp(item2post, inReply2item.Feed, credentials);
  }
  public static void PostCommentViaNntp(NewsItem item2post, NewsFeed postTarget, ICredentials credentials){
   string comment = item2post.ToString(NewsItemSerializationFormat.NntpMessage);
   Encoding enc = Encoding.UTF8, unicode = Encoding.Unicode;
   byte[] encBytes = Encoding.Convert(unicode, enc, unicode.GetBytes(comment));
   NntpWebRequest request = (NntpWebRequest) WebRequest.Create(postTarget.link);
   request.Method = "POST";
   if (credentials != null)
    request.Credentials = credentials;
   Stream myWriter = null;
   try{
    myWriter = request.GetRequestStream();
    myWriter.Write(encBytes, 0, encBytes.Length);
    request.GetResponse();
   } catch(Exception e){
    throw new WebException(e.Message, e);
   }finally{
    if(myWriter != null){
     myWriter.Close();
    }
   }
  }
  public static StringCollection GetNewsgroupList(Stream newsgroupListStream){
   StringCollection col = new StringCollection();
   string newsgroup;
   int len;
   TextReader reader = new StreamReader(newsgroupListStream);
   reader.ReadLine();
   while ( ((newsgroup = reader.ReadLine()) != null) &&
    (newsgroup.StartsWith(".")!= true) && (newsgroup.Length > 0)){
    len = newsgroup.IndexOf(" ");
    len = (len == -1 ? newsgroup.Length : len);
    col.Add(newsgroup.Substring(0, len));
   }
   reader.Close();
   return col;
  }
  public static FeedInfo GetItemsForNewsGroup(NewsFeed f, Stream newsgroupListStream, bool cachedStream) {
   int readItems = 0;
            List<NewsItem> items = new List<NewsItem>();
   NewsItem item;
   string currentLine, title, author, parentId, headerName, headerValue, id;
   StringBuilder content = new StringBuilder();
   DateTime pubDate;
   int colonPos;
   TextReader reader = new StreamReader(newsgroupListStream);
   FeedInfo fi = new FeedInfo(f.id, f.cacheurl, items, f.title, f.link, String.Empty);
   try{
    while(reader.ReadLine()!= null){
     title = author = parentId = id = null;
     pubDate = DateTime.Now;
     content.Remove(0, content.Length);
     while(((currentLine = reader.ReadLine()) != null) &&
      (currentLine.Trim().Length > 0)){
      colonPos = currentLine.IndexOf(":");
      if(colonPos > 0){
       headerName = currentLine.Substring(0, colonPos).ToLower();
       headerValue = currentLine.Substring(colonPos + 2);
       switch(headerName){
        case "subject" :
         title = HeaderDecode(headerValue);
         break;
        case "from_":
         author = HeaderDecode(headerValue);
         break;
        case "references":
         int spaceIndex = headerValue.LastIndexOf(" ");
         spaceIndex = ((spaceIndex != - 1) && (spaceIndex + 1 < headerValue.Length) ? spaceIndex : -1);
         parentId = (spaceIndex == -1 ? headerValue : headerValue.Substring(spaceIndex + 1));
         break;
        case "date":
         pubDate = DateTimeExt.Parse(headerValue);
         break;
        case "message-id":
         id = headerValue;
         break;
        default:
         break;
       }
      }
     }
     while(((currentLine = reader.ReadLine()) != null) &&
      (currentLine.Equals(".")!= true)){
      content.Append(currentLine.Replace("<", "&lt;").Replace("]]>", "]]&gt;"));
      content.Append("<br>");
     }
     if (id != null) {
      item = new NewsItem(f, title, CreateGoogleUrlFromID(id), content.ToString(), author, pubDate, id, parentId);
      item.FeedDetails = fi;
      item.CommentStyle = SupportedCommentStyle.NNTP;
                        item.Enclosures = NewsComponents.Collections.GetList<Enclosure>.Empty;
      items.Add(item);
      NewsHandler.ReceivingNewsChannelServices.ProcessItem(item);
     } else {
      _log.Warn("No message-id header found for item." );
     }
    }
    if (!cachedStream) {
     f.lastretrieved = new DateTime(DateTime.Now.Ticks);
     f.lastretrievedSpecified = true;
    }
    if((items.Count== 0) || (readItems == items.Count)){
     f.containsNewMessages = false;
    }else{
     f.containsNewMessages = true;
    }
    NewsHandler.ReceivingNewsChannelServices.ProcessItem(fi);
                NewsHandler.RelationCosmosAddRange(items);
                fi.itemsList.AddRange(items);
   }
   catch(Exception e)
   {
    System.Diagnostics.Trace.WriteLine(e);
   } finally {
    reader.Close();
   }
   return fi;
  }
  private static Regex BQHeaderEncodingRegex = new Regex(@"=\?([^?]+)\?([^?]+)\?([^?]+)\?=" , RegexOptions.Compiled);
  public static string HeaderDecode( string line ) {
   Match m = BQHeaderEncodingRegex.Match(line);
   if (m.Success) {
    StringBuilder ms = new StringBuilder(line);
    while ( m.Success ) {
     string oStr = m.Groups[0].ToString();
     string encoding = m.Groups[1].ToString();
     string method = m.Groups[2].ToString();
     string content = m.Groups[3].ToString();
     if (method == "b" || method== "B")
      content = Base64Decode(encoding, content);
     else if (method == "q" || method== "Q")
      content = QDecode(encoding, content);
     ms.Replace(oStr, content);
     m = m.NextMatch();
    }
    return ms.ToString();
   }
   return line;
  }
  public static string QDecode(string encoding, string text){
   StringBuilder decoded = new StringBuilder();
   Encoding decoder = Encoding.GetEncoding(encoding);
   for(int i =0; i < text.Length; i++){
    if(text[i] == '='){
     string current = String.Empty + text[i + 1] + text[i + 2];
     byte theByte = Byte.Parse(current, NumberStyles.HexNumber);
     byte[] bytes = new byte[]{theByte};
     decoded.Append(decoder.GetString(bytes));
     i+=2;
    }else if(text[i] == '_'){
     byte theByte = Byte.Parse("20", NumberStyles.HexNumber);
     byte[] bytes = new byte[]{theByte};
     decoded.Append(decoder.GetString(bytes));
    }else{
     decoded.Append(text[i]);
    }
   }
   return decoded.ToString();
  }
  public static string Base64Decode(string encoding, string text){
   Encoding decoder = Encoding.GetEncoding(encoding);
   byte[] textAsByteArray = Convert.FromBase64String(text);
   return decoder.GetString(textAsByteArray);
  }
  public static ArrayList GetEncodedWordIndexes(string str){
   ArrayList list = new ArrayList();
   int begin = -1;
   for(int i =0; i < str.Length; i++){
    if((i != 0) && (str[i] == '?') && ((i + 1) != str.Length)){
     if((begin == -1) && (str[i-1]== '=')){
      begin = i -1;
     }else if(str[i+1]== '='){
      i++;
      list.Add(new int[]{begin, i});
      begin = -1;
     }
    }
   }
   return list;
  }
  internal static string CreateGoogleUrlFromID(string id) {
   return GoogleGroupsUrl + id.Substring(1, id.Length - 2).Replace("#","%23");
  }
 }
}

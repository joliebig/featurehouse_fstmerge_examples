using System.Collections.Generic;
using System.IO;
using System.Xml;
using WikiFunctions.Plugin;
namespace WikiFunctions.Plugins.ListMaker.BingSearch
{
    public class BingSearchListMakerPlugin : IListMakerPlugin
    {
        private const string AppId = "56218EEF0B712AA9E56CEBA0FFE1B79E55E92FFA";
        private const string BaseUrl = "http://api.search.live.net/xml.aspx?Appid=" + AppId +
                                       "&query={0}(site:{1})&sources=web&web.count={2}&web.offset={3}";
        private const int TotalResults = 500,
            NoOfResultsPerRequest = 50;
        public List<Article> MakeList(params string[] searchCriteria)
        {
            List<Article> articles = new List<Article>();
            foreach (string s in searchCriteria)
            {
                int start = 0;
                do
                {
                    string url = string.Format(BaseUrl, s, Variables.URL, NoOfResultsPerRequest, start);
                    using (XmlTextReader reader = new XmlTextReader(new StringReader(Tools.GetHTML(url))))
                    {
                        while (reader.Read())
                        {
                            if (reader.Name.Equals("Error"))
                            {
                                reader.ReadToFollowing("Code");
                                if (string.Compare(reader.ReadString(), "2002", true) == 0)
                                    Tools.MessageBox("Query limit for Bing Exceeded. Please try again later");
                                return articles;
                            }
                            if (reader.Name.Equals("web:Total"))
                            {
                                if (int.Parse(reader.ReadString()) > TotalResults)
                                    start += NoOfResultsPerRequest;
                            }
                            if (reader.Name.Equals("web:Url"))
                            {
                                string title = Tools.GetTitleFromURL(reader.ReadString());
                                if (!string.IsNullOrEmpty(title))
                                    articles.Add(new Article(title));
                            }
                        }
                    }
                } while (articles.Count < TotalResults);
            }
            return articles;
        }
        public string DisplayText
        {
            get { return "Bing Search"; }
        }
        public string UserInputTextBoxText
        {
            get { return "Bing Search:"; }
        }
        public bool UserInputTextBoxEnabled
        { get { return true; } }
        public void Selected() { }
        public bool RunOnSeparateThread
        { get { return true; } }
        public string Name
        {
            get { return "Bing Search Plugin"; }
        }
    }
}

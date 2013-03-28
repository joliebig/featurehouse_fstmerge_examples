using System;
using System.Collections.Generic;
using System.Xml;
using System.IO;
using WikiFunctions.Plugin;
using System.Windows.Forms;
namespace WikiFunctions.Plugins.ListMaker.TypoScan
{
    public class TypoScanListMakerPlugin : IListMakerPlugin
    {
        protected int Count = 100;
        public virtual string Name
        {
            get { return "TypoScan ListMaker Plugin"; }
        }
        public List<Article> MakeList(params string[] searchCriteria)
        {
            List<Article> articles = new List<Article>();
            using (
                XmlTextReader reader =
                    new XmlTextReader(new StringReader(Tools.GetHTML(Common.GetUrlFor("displayarticles") + "&count=" + Count))))
            {
                while (reader.Read())
                {
                    if (reader.Name.Equals("site"))
                    {
                        reader.MoveToAttribute("address");
                        string site = reader.Value;
                        if (site != Common.GetSite())
                        {
                            MessageBox.Show("Wrong Site");
                            return articles;
                        }
                    }
                    else if (reader.Name.Equals("article"))
                    {
                        reader.MoveToAttribute("id");
                        int id = int.Parse(reader.Value);
                        string title = reader.ReadString();
                        articles.Add(new Article(title));
                        if (!TypoScanBasePlugin.PageList.ContainsKey(title))
                            TypoScanBasePlugin.PageList.Add(title, id);
                    }
                }
            }
            TypoScanBasePlugin.CheckoutTime = DateTime.Now;
            return articles;
        }
        public virtual string DisplayText
        { get { return "TypoScan"; } }
        public string UserInputTextBoxText
        { get { return ""; } }
        public bool UserInputTextBoxEnabled
        { get { return false; } }
        public void Selected()
        { }
        public bool RunOnSeparateThread
        { get { return true; } }
    }
    public class TypoScanListMakerPlugin500 : TypoScanListMakerPlugin
    {
        public TypoScanListMakerPlugin500()
        {
            Count = 500;
        }
        public override string DisplayText
        { get { return base.DisplayText + " (500 pages)"; } }
        public override string Name
        { get { return base.Name + " 500"; } }
    }
}

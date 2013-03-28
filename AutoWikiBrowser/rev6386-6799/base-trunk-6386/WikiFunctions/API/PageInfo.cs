using System.Xml;
using System.IO;
using System;
namespace WikiFunctions.API
{
    public sealed class PageInfo
    {
        internal PageInfo()
        {
        }
        internal PageInfo(string xml)
        {
            XmlReader xr = XmlReader.Create(new StringReader(xml));
            if (!xr.ReadToFollowing("page")) throw new Exception("Cannot find <page> element");
            Exists = (xr.GetAttribute("missing") == null);
            IsWatched = (xr.GetAttribute("watched") != null);
            EditToken = xr.GetAttribute("edittoken");
            TokenTimestamp = xr.GetAttribute("starttimestamp");
            long revId;
            RevisionID = long.TryParse(xr.GetAttribute("lastrevid"), out revId) ? revId : -1;
            Title = xr.GetAttribute("title");
            NamespaceID = int.Parse(xr.GetAttribute("ns"));
            if (xr.ReadToDescendant("protection") && !xr.IsEmptyElement)
            {
                XmlDocument doc = new XmlDocument();
                doc.LoadXml(xr.ReadOuterXml());
                foreach (XmlNode xn in doc.GetElementsByTagName("pr"))
                {
                    switch (xn.Attributes["type"].Value)
                    {
                        case "edit":
                            EditProtection = xn.Attributes["level"].Value;
                            break;
                        case "move":
                            MoveProtection = xn.Attributes["level"].Value;
                            break;
                    }
                }
            }
            else
                xr.ReadToFollowing("revisions");
            xr.ReadToDescendant("rev");
            Timestamp = xr.GetAttribute("timestamp");
            Text = Tools.ConvertToLocalLineEndings(xr.ReadString());
        }
        public string Title
        { get; private set; }
        public string Text
        { get; private set; }
        public bool Exists
        { get; private set; }
        public long RevisionID
        { get; private set; }
        public int NamespaceID
        { get; private set; }
        public string Timestamp
        { get; private set; }
        public string EditToken
        { get; internal set; }
        public string TokenTimestamp
        { get; private set; }
        public string EditProtection
        { get; private set; }
        public string MoveProtection
        { get; private set; }
        public bool IsWatched
        { get; set; }
    }
}

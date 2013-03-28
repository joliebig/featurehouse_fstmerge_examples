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
            if (!xr.ReadToFollowing("page"))
                throw new Exception("Cannot find <page> element");
            string normalisedFrom = null, redirectFrom = null;
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(xml);
            var redirects = doc.GetElementsByTagName("r");
            if (redirects.Count >= 1)
            {
                if (redirects.Count > 1 &&
                    redirects[0].Attributes["from"].Value == redirects[redirects.Count - 1].Attributes["to"].Value)
                {
                    TitleChangedStatus = PageTitleStatus.RedirectLoop;
                }
                else
                {
                    TitleChangedStatus = redirects.Count == 1
                                             ? PageTitleStatus.Redirected
                                             : PageTitleStatus.MultipleRedirects;
                }
                redirectFrom = redirects[0].Attributes["from"].Value;
            }
            else
            {
                TitleChangedStatus = PageTitleStatus.NoChange;
            }
            var normalised = doc.GetElementsByTagName("n");
            if (normalised.Count > 0)
            {
                normalisedFrom = normalised[0].Attributes["from"].Value;
                if (TitleChangedStatus == PageTitleStatus.NoChange)
                    TitleChangedStatus = PageTitleStatus.Normalised;
                else
                    TitleChangedStatus |= PageTitleStatus.Normalised;
            }
            if (!string.IsNullOrEmpty(normalisedFrom))
            {
                OriginalTitle = normalisedFrom;
            }
            else if (!string.IsNullOrEmpty(redirectFrom))
            {
                OriginalTitle = redirectFrom;
            }
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
            xr.ReadToFollowing("revisions");
            xr.ReadToDescendant("rev");
            Timestamp = xr.GetAttribute("timestamp");
            Text = Tools.ConvertToLocalLineEndings(xr.ReadString());
        }
        public string Title
        { get; private set; }
        public string OriginalTitle
        { get; private set; }
        public PageTitleStatus TitleChangedStatus
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
        public static bool WasRedirected(PageInfo page)
        {
            if (page.TitleChangedStatus == PageTitleStatus.NoChange)
                return false;
            PageTitleStatus pts = page.TitleChangedStatus;
            return ((pts & PageTitleStatus.Redirected) == PageTitleStatus.Redirected ||
                    (pts & PageTitleStatus.RedirectLoop) == PageTitleStatus.RedirectLoop ||
                    (pts & PageTitleStatus.MultipleRedirects) == PageTitleStatus.MultipleRedirects);
        }
    }
    [Flags]
    public enum PageTitleStatus
    {
        NoChange = 0,
        RedirectLoop = 1,
        MultipleRedirects = 2,
        Redirected = 4,
        Normalised = 8,
    }
}

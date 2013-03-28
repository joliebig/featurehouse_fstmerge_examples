using System.Xml;
using System.IO;
namespace WikiFunctions.API
{
    public sealed class SaveInfo
    {
        public string Title
        { get; private set; }
        public int PageId
        { get; private set; }
        public int OldId
        { get; private set; }
        public int NewId
        { get; private set; }
        public bool NoChange
        { get; private set; }
        public bool IsNewPage
        { get; private set; }
        public XmlDocument ResponseXml
        { get; private set; }
        internal SaveInfo(XmlDocument doc)
        {
            ResponseXml = doc;
            try
            {
                var edit = doc["api"]["edit"];
                if (edit != null)
                {
                    NoChange = edit.HasAttribute("nochange");
                    IsNewPage = edit.HasAttribute("new");
                    Title = edit.GetAttribute("title");
                    PageId = int.Parse(edit.GetAttribute("pageid"));
                    int rev;
                    int.TryParse(edit.GetAttribute("newrevid"), out rev);
                    NewId = rev;
                    int.TryParse(edit.GetAttribute("oldrevid"), out rev);
                    OldId = rev;
                }
            }
            catch
            { }
        }
    }
}

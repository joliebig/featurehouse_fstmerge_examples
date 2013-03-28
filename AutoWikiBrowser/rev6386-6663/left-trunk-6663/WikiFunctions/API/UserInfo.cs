using System.Collections.Generic;
using System.Xml;
namespace WikiFunctions.API
{
    public sealed class UserInfo
    {
        public string Name
        { get; private set; }
        public int Id
        { get; private set; }
        public bool IsLoggedIn
        { get { return Id != 0; } }
        public bool IsSysop
        { get { return IsInGroup("sysop"); } }
        public bool IsBot
        { get { return IsInGroup("bot") || HasRight("bot"); } }
        public bool IsBlocked
        { get; private set; }
        public bool HasMessages
        { get; internal set; }
        public bool IsInGroup(string group)
        {
            return string.IsNullOrEmpty(group) || Groups.Contains(group);
        }
        public bool HasRight(string right)
        {
            return string.IsNullOrEmpty(right) || Rights.Contains(right);
        }
        public bool CanEditPage(PageInfo page)
        {
            return (IsInGroup(page.EditProtection) || HasRight(page.EditProtection))
                && !(page.NamespaceID == Namespace.MediaWiki && !HasRight("editinterface"));
        }
        public bool CanMovePage(PageInfo page)
        {
            return page.NamespaceID != Namespace.MediaWiki
                && (IsInGroup(page.MoveProtection) || HasRight(page.MoveProtection));
        }
        internal UserInfo(XmlDocument xml)
        {
            var users = xml.GetElementsByTagName("userinfo");
            if (users.Count == 0) throw new BrokenXmlException(null, "XML with <userinfo> element expected");
            var user = users[0];
            Name = user.Attributes["name"].Value;
            Id = int.Parse(user.Attributes["id"].Value);
            var groups = user["groups"];
            if (groups != null)
            {
                foreach(XmlNode g in groups.GetElementsByTagName("g"))
                {
                    Groups.Add(g.InnerText);
                }
            }
            var rights = user["rights"];
            if (rights != null)
            {
                foreach (XmlNode r in rights.GetElementsByTagName("r"))
                {
                    Rights.Add(r.InnerText);
                }
            }
            Update(xml);
        }
        internal void Update(XmlDocument xml)
        {
            var users = xml.GetElementsByTagName("userinfo");
            if (users.Count == 0) return;
            HasMessages = users[0].Attributes["messages"] != null;
            IsBlocked = users[0].Attributes["blockedby"] != null;
        }
        internal UserInfo()
        {
        }
        private readonly List<string> Groups = new List<string>();
        private readonly List<string> Rights = new List<string>();
    }
}

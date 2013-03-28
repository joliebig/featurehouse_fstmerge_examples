namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System;
    using System.Net;
    using System.Xml;
    using Exortech.NetReflector;
    using Exortech.NetReflector.Util;
    public class NetworkCredentialsSerializer
        : XmlMemberSerialiser
    {
        public NetworkCredentialsSerializer(ReflectorMember member, ReflectorPropertyAttribute attribute)
            : base(member, attribute)
        {
        }
        public override object Read(XmlNode node, NetReflectorTypeTable table)
        {
            NetworkCredential ret = null;
            if (node is XmlElement)
            {
                XmlElement elem = (XmlElement)node;
                if (!elem.HasAttribute("userName") || String.IsNullOrEmpty(elem.GetAttribute("userName").Trim()))
                {
                    Log.Warning("No 'userName' specified!");
                    return ret;
                }
                if (!elem.HasAttribute("password"))
                {
                    Log.Warning("No 'password' specified!");
                    return ret;
                }
                if (elem.HasAttribute("domain") && !String.IsNullOrEmpty(elem.GetAttribute("domain").Trim()))
                {
                    ret = new NetworkCredential(elem.GetAttribute("userName").Trim(), elem.GetAttribute("password").Trim(), elem.GetAttribute("domain").Trim());
                }
                else
                {
                   ret = new NetworkCredential(elem.GetAttribute("userName").Trim(), elem.GetAttribute("password").Trim());
                }
            }
            return ret;
        }
        public override void Write(XmlWriter writer, object target)
        {
            NetworkCredential credentials = target as NetworkCredential;
            if (credentials != null)
            {
                writer.WriteStartElement("networkCredentials");
                writer.WriteAttributeString("userName", credentials.UserName);
                writer.WriteAttributeString("password", credentials.Password);
                writer.WriteAttributeString("domain", credentials.Domain);
                writer.WriteEndElement();
            }
        }
    }
}

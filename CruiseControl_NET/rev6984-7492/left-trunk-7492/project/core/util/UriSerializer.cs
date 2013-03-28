namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System;
    using System.Xml;
    using Exortech.NetReflector;
    using Exortech.NetReflector.Util;
    public class UriSerializer
        : XmlMemberSerialiser
    {
        public UriSerializer(ReflectorMember member, ReflectorPropertyAttribute attribute)
            : base(member, attribute)
        {
        }
        public override object Read(XmlNode node, NetReflectorTypeTable table)
        {
            if (node == null)
            {
                if (this.Attribute.Required)
                {
                    throw new NetReflectorItemRequiredException(Attribute.Name + " is required");
                }
                else
                {
                    return null;
                }
            }
            Uri ret;
            if (node is XmlAttribute)
            {
                ret = new Uri(node.Value);
            }
            else
            {
                ret = new Uri(node.InnerText);
            }
            return ret;
        }
        public override void Write(XmlWriter writer, object target)
        {
            if (!(target is Uri)) target = ReflectorMember.GetValue(target);
            var uri = target as Uri;
            if (uri != null)
            {
                writer.WriteElementString("uri", uri.ToString());
            }
        }
    }
}

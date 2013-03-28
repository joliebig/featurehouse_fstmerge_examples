namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System;
    using System.Xml;
    using Exortech.NetReflector;
    using Exortech.NetReflector.Util;
    public class PrivateStringSerialiser
        : XmlMemberSerialiser
    {
        public PrivateStringSerialiser(ReflectorMember member, ReflectorPropertyAttribute attribute)
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
            PrivateString value;
            if (node is XmlAttribute)
            {
                value = node.Value;
            }
            else
            {
                value = node.InnerText;
            }
            return value;
        }
        public override void Write(XmlWriter writer, object target)
        {
            if (!(target is PrivateString)) target = this.ReflectorMember.GetValue(target);
            var value = target as PrivateString;
            if (value != null)
            {
                writer.WriteElementString(this.Attribute.Name, value.ToString());
            }
        }
    }
}

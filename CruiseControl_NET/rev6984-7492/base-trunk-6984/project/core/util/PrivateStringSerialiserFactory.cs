namespace ThoughtWorks.CruiseControl.Core.Util
{
    using Exortech.NetReflector;
    using Exortech.NetReflector.Util;
    public class PrivateStringSerialiserFactory
        : ISerialiserFactory
    {
        public IXmlMemberSerialiser Create(ReflectorMember memberInfo, ReflectorPropertyAttribute attribute)
        {
            return new PrivateStringSerialiser(memberInfo, attribute);
        }
    }
}

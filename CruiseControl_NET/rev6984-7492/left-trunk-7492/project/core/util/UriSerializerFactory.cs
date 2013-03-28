namespace ThoughtWorks.CruiseControl.Core.Util
{
    using Exortech.NetReflector;
    using Exortech.NetReflector.Util;
    public class UriSerializerFactory
        : ISerialiserFactory
    {
        public IXmlMemberSerialiser Create(ReflectorMember memberInfo, ReflectorPropertyAttribute attribute)
        {
            return new UriSerializer(memberInfo, attribute);
        }
    }
}

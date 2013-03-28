namespace ThoughtWorks.CruiseControl.Core.Util
{
    using Exortech.NetReflector;
    using Exortech.NetReflector.Util;
    public class NetworkCredentialSerializerFactory
        : ISerialiserFactory
    {
        public IXmlMemberSerialiser Create(ReflectorMember memberInfo, ReflectorPropertyAttribute attribute)
        {
            return new NetworkCredentialsSerializer(memberInfo, attribute);
        }
    }
}

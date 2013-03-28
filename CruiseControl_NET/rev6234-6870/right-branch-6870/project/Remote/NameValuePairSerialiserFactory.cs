using Exortech.NetReflector;
using Exortech.NetReflector.Util;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class NameValuePairSerialiserFactory
        : ISerialiserFactory
    {
        public IXmlMemberSerialiser Create(ReflectorMember memberInfo, ReflectorPropertyAttribute attribute)
        {
            return new NameValuePairSerialiser(memberInfo, attribute, false);
        }
    }
}

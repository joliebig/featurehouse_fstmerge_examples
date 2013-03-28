using Exortech.NetReflector;
using Exortech.NetReflector.Util;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class NameValuePairListSerialiserFactory
        : ISerialiserFactory
    {
        public IXmlMemberSerialiser Create(ReflectorMember memberInfo, ReflectorPropertyAttribute attribute)
        {
            return new NameValuePairSerialiser(memberInfo, attribute, true);
        }
    }
}

using Exortech.NetReflector;
using Exortech.NetReflector.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public class MergeFileSerialiserFactory
        : ISerialiserFactory
    {
        public IXmlMemberSerialiser Create(ReflectorMember memberInfo, ReflectorPropertyAttribute attribute)
        {
            return new MergeFileSerialiser(memberInfo, attribute);
        }
    }
}

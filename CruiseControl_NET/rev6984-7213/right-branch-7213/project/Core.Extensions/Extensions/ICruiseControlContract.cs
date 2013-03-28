using System.ServiceModel;
using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Core.Extensions
{
    [ServiceContract(Namespace = "http://ccnet.thoughtworks.com/1/5/extensions")]
    public interface ICruiseControlContract
    {
        [OperationContract]
        Response ProcessMessage(string action, ServerRequest message);
    }
}

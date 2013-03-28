using ThoughtWorks.CruiseControl.Remote.Messages;
namespace ThoughtWorks.CruiseControl.Remote
{
    public interface IMessageProcessor
    {
        string ProcessMessage(string action, string message);
        Response ProcessMessage(string action, ServerRequest message);
    }
}

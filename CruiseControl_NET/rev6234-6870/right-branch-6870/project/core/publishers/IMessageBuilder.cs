using ThoughtWorks.CruiseControl.Core.Tasks;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
 public interface IMessageBuilder
 {
        System.Collections.IList xslFiles { get; set; }
        string BuildMessage(IIntegrationResult result, TaskContext context);
 }
}

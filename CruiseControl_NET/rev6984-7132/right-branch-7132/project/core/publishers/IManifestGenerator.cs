using System.Xml;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    public interface IManifestGenerator
    {
        XmlDocument Generate(IIntegrationResult result, string[] packagedFiles);
    }
}

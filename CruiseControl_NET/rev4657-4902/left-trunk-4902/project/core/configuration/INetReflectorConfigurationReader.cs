using System.Xml;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Config
{
    public interface INetReflectorConfigurationReader
    {
        IConfiguration Read(XmlDocument document);
        event InvalidNodeEventHandler InvalidNodeEventHandler;
    }
}

namespace ThoughtWorks.CruiseControl.Core
{
    using System.Xml;
    using ThoughtWorks.CruiseControl.Core.Config;
    public interface INetReflectorConfigurationReader
    {
        IConfiguration Read(XmlDocument document, IConfigurationErrorProcesser errorProcesser);
        object ParseElement(XmlNode node);
    }
}

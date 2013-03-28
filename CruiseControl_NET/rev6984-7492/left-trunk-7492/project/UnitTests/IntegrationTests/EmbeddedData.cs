namespace ThoughtWorks.CruiseControl.UnitTests.IntegrationTests
{
    using System.Xml;
    public static class EmbeddedData
    {
        public static XmlDocument LoadEmbeddedXml(string documentName)
        {
            var document = new XmlDocument();
            var actualName = "ThoughtWorks.CruiseControl.UnitTests.IntegrationTests.Data." + documentName;
            using (var stream = typeof(DynamicValuesTests).Assembly.GetManifestResourceStream(actualName))
            {
                document.Load(stream);
            }
            return document;
        }
    }
}

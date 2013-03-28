namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    using System.Collections.Generic;
    using ICSharpCode.SharpZipLib.Zip;
    public interface IPackageItem
    {
        IEnumerable<string> Package(IIntegrationResult result, ZipOutputStream zipStream);
    }
}

using System.IO;
namespace ThoughtWorks.CruiseControl.Remote
{
    public interface IFileTransfer
    {
        void Download(Stream destination);
    }
}

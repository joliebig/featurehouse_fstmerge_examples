using System.IO;
using System.Xml.Serialization;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("modificationWriter")]
    public class ModificationWriterTask
        : TaskBase
    {
        private readonly IFileSystem fileSystem;
        public ModificationWriterTask()
            : this(new SystemIoFileSystem())
        { }
        public ModificationWriterTask(IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;
        }
        protected override bool Execute(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Writing Modifications");
            XmlSerializer serializer = new XmlSerializer(typeof(Modification[]));
            StringWriter writer = new Utf8StringWriter();
            serializer.Serialize(writer, result.Modifications);
            string filename = ModificationFile(result);
            fileSystem.EnsureFolderExists(filename);
            fileSystem.Save(filename, writer.ToString());
            return true;
        }
        private string ModificationFile(IIntegrationResult result)
        {
         if (!AppendTimeStamp)
          return Path.Combine(result.BaseFromArtifactsDirectory(OutputPath), Filename);
         FileInfo fi = new FileInfo(Filename);
         string dummy = Filename.Remove(Filename.Length - fi.Extension.Length, fi.Extension.Length);
         string newFileName = string.Format("{0}_{1}{2}", dummy, result.StartTime.ToString("yyyyMMddHHmmssfff"),
                                            fi.Extension);
         return Path.Combine(result.BaseFromArtifactsDirectory(OutputPath), newFileName);
        }
        [ReflectorProperty("filename", Required = false)]
        public string Filename = "modifications.xml";
        [ReflectorProperty("path", Required = false)]
        public string OutputPath;
        [ReflectorProperty("appendTimeStamp", Required = false)]
        public bool AppendTimeStamp;
    }
}

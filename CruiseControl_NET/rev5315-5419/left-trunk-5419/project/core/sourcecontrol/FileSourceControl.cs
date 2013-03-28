using System;
using System.Collections;
using System.IO;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    [ReflectorType("filesystem")]
    public class FileSourceControl : ISourceControl
    {
        private readonly IFileSystem fileSystem;
        public FileSourceControl()
            : this(new SystemIoFileSystem())
        { }
        public FileSourceControl(IFileSystem fileSystem)
        {
            this.fileSystem = fileSystem;
        }
        [ReflectorProperty("repositoryRoot")]
        public string RepositoryRoot;
        [ReflectorProperty("ignoreMissingRoot", Required = false)]
        public bool IgnoreMissingRoot;
        [ReflectorProperty("autoGetSource", Required = false)]
        public bool AutoGetSource = false;
        public Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
        {
            DirectoryInfo root = new DirectoryInfo(from_.BaseFromWorkingDirectory(RepositoryRoot));
            ArrayList modifications = GetMods(root, from_.StartTime);
            return (Modification[])modifications.ToArray(typeof(Modification));
        }
        private ArrayList GetMods(DirectoryInfo dir, DateTime from_)
        {
            ArrayList mods = new ArrayList();
            try
            {
                foreach (FileInfo file in dir.GetFiles())
                {
                    if (IsLocalFileChanged(file, from_))
                    {
                        mods.Add(CreateModification(file));
                    }
                }
                foreach (DirectoryInfo sub in dir.GetDirectories())
                {
                    mods.AddRange(GetMods(sub, from_));
                }
            }
            catch (DirectoryNotFoundException exc)
            {
                if (!IgnoreMissingRoot)
                {
                    throw exc;
                }
            }
            return mods;
        }
        private Modification CreateModification(FileInfo info)
        {
            Modification modification = new Modification();
            modification.FileName = info.Name;
            modification.FolderName = info.DirectoryName;
            if (info.CreationTime > info.LastWriteTime)
            {
                modification.ModifiedTime = info.CreationTime;
            }
            else
            {
                modification.ModifiedTime = info.LastWriteTime;
            }
            return modification;
        }
        private bool IsLocalFileChanged(FileInfo reposFile, DateTime date)
        {
            bool result = false;
            if (reposFile.LastWriteTime > date) result = true;
            if (reposFile.CreationTime > date) result = true;
            return result;
        }
        public void LabelSourceControl(IIntegrationResult result)
        { }
        public void GetSource(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask("Getting source from_ FileSourceControl");
            if (AutoGetSource)
                fileSystem.Copy(RepositoryRoot, result.WorkingDirectory);
        }
        public void Initialize(IProject project)
        { }
        public void Purge(IProject project)
        { }
    }
}

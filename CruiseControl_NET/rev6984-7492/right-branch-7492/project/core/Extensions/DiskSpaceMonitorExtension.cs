using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote.Events;
namespace ThoughtWorks.CruiseControl.Core.Extensions
{
    public class DiskSpaceMonitorExtension
        : ICruiseServerExtension
    {
        private Dictionary<string, long> driveSpaces = new Dictionary<string, long>();
        public virtual void Initialise(ICruiseServer server, ExtensionConfiguration extensionConfig)
        {
            if (server == null)
            {
                throw new ArgumentNullException("server");
            }
            else
            {
                server.IntegrationStarted += (o, e) =>
                {
                    var fileSystem = server.RetrieveService(typeof(IFileSystem)) as IFileSystem ?? new SystemIoFileSystem();
                    bool hasSpace = true;
                    foreach (var drive in driveSpaces.Keys)
                    {
                        var freeSpace = fileSystem.GetFreeDiskSpace(drive);
                        hasSpace &= (freeSpace >= driveSpaces[drive]);
                    }
                    e.Result = hasSpace ? IntegrationStartedEventArgs.EventResult.Continue
                        : IntegrationStartedEventArgs.EventResult.Cancel;
                    if (!hasSpace)
                    {
                        Log.Warning(string.Format("Integration for '{0}' cancelled due to a lack of space.", e.ProjectName));
                    }
                };
            }
            foreach (var element in extensionConfig.Items)
            {
                if (element.Name == "drive")
                {
                    if (element.SelectNodes("*").Count > 0) throw new ArgumentException("Drive definitions cannot contain child elements");
                    AddDriveSpace(element.GetAttribute("name"), element.GetAttribute("unit"), element.InnerText);
                }
                else
                {
                    throw new ArgumentOutOfRangeException("Unknown configuration option: " + element.Name);
                }
            }
            if (driveSpaces.Count == 0)
            {
                throw new ArgumentOutOfRangeException("At least one drive must be defined to monitor");
            }
        }
        public virtual void Start()
        {
        }
        public virtual void Stop()
        {
        }
        public virtual void Abort()
        {
        }
        public long RetrieveMinimumSpaceRequired(string drive)
        {
            drive=drive.ToLowerInvariant();
            if (driveSpaces.ContainsKey(drive))
            {
                return driveSpaces[drive];
            }
            else
            {
                return -1;
            }
        }
        private void AddDriveSpace(string driveName, string unit, string amount)
        {
            if (string.IsNullOrEmpty(driveName)) throw new ArgumentNullException("name is a required attribute");
            if (string.IsNullOrEmpty(unit)) unit = "Mb";
            if (string.IsNullOrEmpty(amount)) throw new ArgumentNullException("The amount of free space has not been specified");
            double value;
            if (!double.TryParse(amount, out value)) throw new ArgumentException("Amount must be a valid number");
            unit = unit.ToLowerInvariant();
            switch (unit)
            {
                case "b":
                    break;
                case "kb":
                    value *= 1024;
                    break;
                case "mb":
                    value *= 1048576;
                    break;
                case "gb":
                    value *= 1073741824;
                    break;
                default:
                    throw new ArgumentOutOfRangeException("Unknown unit: " + unit);
            }
            long numberOfBytes = Convert.ToInt64(value);
            driveSpaces.Add(driveName.ToLowerInvariant(), numberOfBytes);
        }
    }
}

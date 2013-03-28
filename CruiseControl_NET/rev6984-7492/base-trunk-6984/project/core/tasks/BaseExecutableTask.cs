namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System.Collections;
    using System.Diagnostics;
    using System.IO;
    using ThoughtWorks.CruiseControl.Core.Config;
    using ThoughtWorks.CruiseControl.Core.Util;
    using System;
    public abstract class BaseExecutableTask
        : TaskBase, IConfigurationValidation
 {
  protected ProcessExecutor executor;
  protected BuildProgressInformation buildProgressInformation;
  protected abstract string GetProcessFilename();
  protected abstract string GetProcessArguments(IIntegrationResult result);
  protected abstract string GetProcessBaseDirectory(IIntegrationResult result);
        protected abstract ProcessPriorityClass GetProcessPriorityClass();
  protected abstract int GetProcessTimeout();
  protected virtual int[] GetProcessSuccessCodes()
  {
   return null;
  }
  protected virtual ProcessInfo CreateProcessInfo(IIntegrationResult result)
  {
   ProcessInfo info = new ProcessInfo(GetProcessFilename(), GetProcessArguments(result), GetProcessBaseDirectory(result),GetProcessPriorityClass(), GetProcessSuccessCodes());
   info.TimeOut = GetProcessTimeout();
   IDictionary properties = result.IntegrationProperties;
   foreach (string key in properties.Keys)
   {
    info.EnvironmentVariables[key] = StringUtil.IntegrationPropertyToString(properties[key]);
   }
   return info;
  }
  protected ProcessResult TryToRun(ProcessInfo info, IIntegrationResult result)
  {
   buildProgressInformation = result.BuildProgressInformation;
   try
   {
    executor.ProcessOutput += ProcessExecutor_ProcessOutput;
    return executor.Execute(info);
   }
   catch (IOException e)
   {
    throw new BuilderException(
                    this,
                    string.Format("Unable to execute: {0} {1}\n{2}", info.FileName, info.PublicArguments, e), e);
   }
   finally
   {
    executor.ProcessOutput -= ProcessExecutor_ProcessOutput;
   }
  }
  private void ProcessExecutor_ProcessOutput(object sender, ProcessOutputEventArgs e)
  {
   if (buildProgressInformation == null)
    return;
   if (e.OutputType == ProcessOutputType.ErrorOutput)
    return;
   buildProgressInformation.AddTaskInformation(e.Data);
        }
        public IFileSystem IOSystem { get; set; }
        public IFileSystem IOSystemActual
        {
            get
            {
                if (this.IOSystem == null)
                {
                    this.IOSystem = new SystemIoFileSystem();
                }
                return this.IOSystem;
            }
        }
        public void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            var canCheck = true;
            var fileName = this.GetProcessFilename();
            if (!Path.IsPathRooted(fileName))
            {
                var project = parent.GetAncestorValue<Project>();
                if (project != null)
                {
                    var result = ConfigurationValidationUtils.GenerateResultForProject(project);
                    var directory = this.GetProcessBaseDirectory(result);
                    fileName = Path.Combine(directory, fileName);
                }
                else
                {
                    canCheck = false;
                }
            }
            if (canCheck && !this.IOSystemActual.FileExists(fileName))
            {
                var fileExists = false;
                var directory = Path.GetDirectoryName(fileName);
                if (this.IOSystemActual.DirectoryExists(directory))
                {
                    var files = this.IOSystemActual.GetFilesInDirectory(directory);
                    var executableName1 = Path.GetFileNameWithoutExtension(fileName);
                    var executableName2 = Path.GetFileName(fileName);
                    foreach (var file in files)
                    {
                        var fileToTest = Path.GetFileNameWithoutExtension(file);
                        if (string.Equals(fileToTest, executableName1, StringComparison.InvariantCultureIgnoreCase) ||
                            string.Equals(fileToTest, executableName2, StringComparison.InvariantCultureIgnoreCase))
                        {
                            fileExists = true;
                            break;
                        }
                    }
                }
                if (!fileExists)
                {
                    errorProcesser.ProcessWarning("Unable to find executable '" + fileName + "'");
                }
            }
        }
    }
}

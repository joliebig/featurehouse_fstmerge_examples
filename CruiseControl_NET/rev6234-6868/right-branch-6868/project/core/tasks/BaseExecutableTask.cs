namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections;
    using System.IO;
    using ThoughtWorks.CruiseControl.Core.Util;
    using System.Text;
    public abstract class BaseExecutableTask : TaskBase
 {
  protected ProcessExecutor executor;
  protected BuildProgressInformation buildProgressInformation;
  protected abstract string GetProcessFilename();
  protected abstract string GetProcessArguments(IIntegrationResult result);
  protected abstract string GetProcessBaseDirectory(IIntegrationResult result);
  protected abstract int GetProcessTimeout();
  protected virtual int[] GetProcessSuccessCodes()
  {
   return null;
  }
  protected virtual ProcessInfo CreateProcessInfo(IIntegrationResult result)
  {
   ProcessInfo info = new ProcessInfo(GetProcessFilename(), GetProcessArguments(result), GetProcessBaseDirectory(result), GetProcessSuccessCodes());
   info.TimeOut = GetProcessTimeout();
   IDictionary properties = result.IntegrationProperties;
   foreach (string key in properties.Keys)
   {
    info.EnvironmentVariables[key] = StringUtil.IntegrationPropertyToString(properties[key]);
   }
   return info;
  }
        protected virtual void MergeHandler(Stream output, Stream[] input)
        {
            using (var writer = new StreamWriter(output, Encoding.UTF8))
            {
                writer.WriteLine("<buildresults>");
                for (var loop = 0; loop < input.Length; loop++)
                {
                    var reader = new StreamReader(input[loop]);
                    while (reader.Peek() >= 0)
                    {
                        writer.Write("<message");
                        if (loop == 1)
                        {
                            writer.Write(" level=\"Error\"");
                        }
                        writer.WriteLine(">");
                        var line = reader.ReadLine();
                        if (!string.IsNullOrEmpty(line))
                        {
                            writer.WriteLine(XmlUtil.EncodePCDATA(line));
                        }
                        writer.WriteLine("</message>");
                    }
                }
                writer.WriteLine("</buildresults>");
                writer.Flush();
            }
        }
        protected virtual Stream CreateResultStream(string name)
        {
            return this.Context.CreateResultStream(name, "Executable-temp");
        }
        [Obsolete("Use the override that accepts a task name and type.")]
        protected ProcessResult TryToRun(ProcessInfo info, IIntegrationResult result)
        {
            var name = this.GetType().Name;
            return this.TryToRun(info, result, name, name);
        }
        protected ProcessResult TryToRun(ProcessInfo info, IIntegrationResult result, string taskName, string taskType)
        {
            buildProgressInformation = result.BuildProgressInformation;
            ProcessResult processResult = null;
            Stream outputStream = null;
            Stream errorStream = null;
            try
            {
                executor.ProcessOutput += ProcessExecutor_ProcessOutput;
                using (outputStream = this.CreateResultStream("stdout"))
                {
                    using (errorStream = this.CreateResultStream("stderr"))
                    {
                        processResult = executor.Execute(info, outputStream, errorStream);
                    }
                }
                this.Context.MergeResultStreams(
                    taskName,
                    taskType,
                    this.MergeHandler,
                    outputStream,
                    errorStream);
                return processResult;
            }
            catch (IOException e)
            {
                throw new BuilderException(this, string.Format("Unable to execute: {0} {1}\n{2}", info.FileName, info.SafeArguments, e), e);
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
 }
}

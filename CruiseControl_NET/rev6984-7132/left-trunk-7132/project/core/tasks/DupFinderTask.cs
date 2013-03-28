using System;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using System.Xml;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("dupfinder")]
    public class DupFinderTask
        : BaseExecutableTask
    {
        [SuppressMessage("Microsoft.StyleCop.CSharp.DocumentationRules", "SA1600:ElementsMustBeDocumented", Justification = "Private constant")]
        private const string DefaultExecutable = "dupfinder";
        private const ProcessPriorityClass DefaultPriority = ProcessPriorityClass.Normal;
        [SuppressMessage("Microsoft.StyleCop.CSharp.DocumentationRules", "SA1600:ElementsMustBeDocumented", Justification = "Private field")]
        private string executable;
        public DupFinderTask()
            : this(new ProcessExecutor())
        {
        }
        public DupFinderTask(ProcessExecutor executor)
        {
            this.executor = executor;
            this.TimeOut = 600;
            this.Threshold = 5;
            this.Width = 2;
            this.Priority = ProcessPriorityClass.Normal;
        }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("priority", Required = false)]
        public ProcessPriorityClass Priority { get; set; }
        [ReflectorProperty("inputDir", Required = true)]
        public string InputDir { get; set; }
        [ReflectorProperty("fileMask", Required = true)]
        public string FileMask { get; set; }
        [ReflectorProperty("focus", Required = false)]
        public string Focus { get; set; }
        [ReflectorProperty("timeout", Required = false)]
        public int TimeOut { get; set; }
        [ReflectorProperty("threshold", Required = false)]
        public int Threshold { get; set; }
        [ReflectorProperty("width", Required = false)]
        public int Width { get; set; }
        [ReflectorProperty("recurse", Required = false)]
        public bool Recurse { get; set; }
        [ReflectorProperty("shortenNames", Required = false)]
        public bool ShortenFileNames { get; set; }
        [ReflectorProperty("includeCode", Required = false)]
        public bool IncludeCode { get; set; }
        [ReflectorProperty("excludeLines", Required = false)]
        public string[] LinesToExclude { get; set; }
        [ReflectorProperty("excludeFiles", Required = false)]
        public string[] FilesToExclude { get; set; }
        public ILogger logger { get; private set; }
        protected override bool Execute(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Executing DupFinder");
            this.logger = this.logger ?? new DefaultLogger();
            this.executable = string.IsNullOrEmpty(this.Executable) ? DefaultExecutable : this.Executable;
            if (!Path.IsPathRooted(this.executable))
            {
                this.executable = result.BaseFromWorkingDirectory(this.executable);
                this.logger.Debug("Executable changed to " + this.executable);
            }
            this.logger.Info("Executing DupFinder");
            var processResult = TryToRun(CreateProcessInfo(result), result);
            if (this.ShortenFileNames || this.IncludeCode)
            {
                var document = new XmlDocument();
                document.LoadXml(processResult.StandardOutput);
                if (this.IncludeCode)
                {
                    this.logger.Info("Including duplicate code lines");
                    this.ImportCode(document);
                }
                if (this.ShortenFileNames)
                {
                    this.logger.Info("Shortening filenames");
                    this.RemoveInputDir(document);
                }
                processResult = new ProcessResult(
                    document.OuterXml,
                    processResult.StandardError,
                    processResult.ExitCode,
                    processResult.TimedOut,
                    processResult.Failed);
            }
            result.AddTaskResult(new ProcessTaskResult(processResult, false));
            return !processResult.Failed;
        }
        protected override string GetProcessFilename()
        {
            var path = this.executable ?? this.Executable;
            path = string.IsNullOrEmpty(path) ? DefaultExecutable : path;
            path = this.QuoteSpaces(path);
            return path;
        }
        protected override string GetProcessBaseDirectory(IIntegrationResult result)
        {
            var path = this.QuoteSpaces(this.InputDir);
            return path;
        }
        protected override int GetProcessTimeout()
        {
            return this.TimeOut * 1000;
        }
        protected override string GetProcessArguments(IIntegrationResult result)
        {
            var buffer = new ProcessArgumentBuilder();
            buffer.AppendIf(this.Recurse, "-r");
            buffer.AppendArgument("-t" + this.Threshold.ToString());
            buffer.AppendArgument("-w" + this.Width.ToString());
            buffer.AppendArgument("-oConsole");
            if (!string.IsNullOrEmpty(this.Focus))
            {
                buffer.AppendArgument("-f" + this.QuoteSpaces(this.Focus));
            }
            foreach (var line in this.LinesToExclude ?? new string[0])
            {
                buffer.AppendArgument("-x" + this.QuoteSpaces(line));
            }
            foreach (var line in this.FilesToExclude ?? new string[0])
            {
                buffer.AppendArgument("-e" + this.QuoteSpaces(line));
            }
            buffer.AppendArgument(this.FileMask);
            return buffer.ToString();
        }
        protected override ProcessPriorityClass GetProcessPriorityClass()
        {
            return this.Priority;
        }
        protected void RemoveInputDir(XmlDocument document)
        {
            var duplicateNodes = document.SelectNodes("//Duplicate");
            var length = this.InputDir.Length + 1;
            foreach (XmlElement duplicate in duplicateNodes)
            {
                var filename = duplicate.GetAttribute("FileName");
                if (!string.IsNullOrEmpty(filename) && filename.StartsWith(this.InputDir))
                {
                    duplicate.SetAttribute("OriginalFileName", filename);
                    duplicate.SetAttribute("FileName", filename.Substring(length));
                }
            }
        }
        protected void ImportCode(XmlDocument document)
        {
            var duplicatesNodes = document.SelectNodes("//Duplicates");
            var fileNames = new Dictionary<string, List<XmlElement> >();
            this.logger.Debug("Generating file list");
            foreach (XmlElement node in duplicatesNodes)
            {
                var isFound = false;
                foreach (XmlElement duplicate in node.ChildNodes)
                {
                    var fileName = duplicate.GetAttribute("FileName");
                    if (fileNames.ContainsKey(fileName))
                    {
                        fileNames[fileName].Add(duplicate);
                        isFound = true;
                        break;
                    }
                }
                if (!isFound)
                {
                    var first = node.FirstChild as XmlElement;
                    var elementList = new List<XmlElement>();
                    elementList.Add(first);
                    fileNames.Add(first.GetAttribute("FileName"), elementList);
                }
            }
            this.logger.Debug("Importing duplicate code lines");
            foreach (var file in fileNames)
            {
                file.Value.Sort(this.CompareFileNodes);
                using (var inputFile = this.IOSystemActual.OpenInputStream(file.Key))
                {
                    using (var reader = new StreamReader(inputFile))
                    {
                        var lines = new Dictionary<int, string>();
                        var lineNumber = 1;
                        string currentLine = null;
                        foreach (var node in file.Value)
                        {
                            var parent = node.ParentNode as XmlElement;
                            var firstLine = Convert.ToInt32(node.GetAttribute("LineNumber"));
                            var blockLength = Convert.ToInt32(parent.GetAttribute("Length"));
                            var lastLine = firstLine + blockLength;
                            while (lineNumber < firstLine)
                            {
                                currentLine = reader.ReadLine();
                                lineNumber++;
                            }
                            while (lineNumber <= lastLine)
                            {
                                currentLine = reader.ReadLine();
                                lines.Add(lineNumber, currentLine);
                                lineNumber++;
                            }
                            var codeNode = document.CreateElement("code");
                            parent.AppendChild(codeNode);
                            for (var loop = firstLine; loop <= lastLine; loop++)
                            {
                                var lineNode = document.CreateElement("line");
                                codeNode.AppendChild(lineNode);
                                lineNode.InnerText = lines[loop];
                            }
                        }
                    }
                }
            }
        }
        protected int CompareFileNodes(XmlElement firstNode, XmlElement secondNode)
        {
            var firstLine = Convert.ToInt32(firstNode.GetAttribute("LineNumber"));
            var secondLine = Convert.ToInt32(secondNode.GetAttribute("LineNumber"));
            return firstLine - secondLine;
        }
        protected string QuoteSpaces(string value)
        {
            if (value.Contains(" ") && !value.StartsWith("\"") && !value.EndsWith("\""))
            {
                return "\"" + value + "\"";
            }
            else
            {
                return value;
            }
        }
    }
}

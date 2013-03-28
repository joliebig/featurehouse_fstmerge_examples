using System;
using System.IO;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Label
{
    [ReflectorType("fileLabeller")]
    public class FileLabeller
        : LabellerBase
    {
        private readonly FileReader fileReader;
        private bool allowDuplicateSubsequentLabels = true;
        private string labelFilePath = string.Empty;
        private string prefix = string.Empty;
        public FileLabeller() : this(new FileReader())
        {
        }
        public FileLabeller(FileReader fileReader)
        {
            this.fileReader = fileReader;
        }
        [ReflectorProperty("labelFilePath", Required = true)]
        public string LabelFilePath
        {
            get { return labelFilePath; }
            set { labelFilePath = value; }
        }
        [ReflectorProperty("prefix", Required = false)]
        public string Prefix
        {
            get { return prefix; }
            set { prefix = value; }
        }
        [ReflectorProperty("allowDuplicateSubsequentLabels", Required = false)]
        public bool AllowDuplicateSubsequentLabels
        {
            get { return allowDuplicateSubsequentLabels; }
            set { allowDuplicateSubsequentLabels = value; }
        }
        public override string Generate(IIntegrationResult integrationResult)
        {
            string label = fileReader.GetLabel(integrationResult.BaseFromWorkingDirectory(labelFilePath));
            string suffix = GetSuffixBasedOn(label, integrationResult.LastIntegration.Label);
            return string.Format("{0}{1}{2}", prefix, label, suffix);
        }
        private string GetSuffixBasedOn(string currentLabel, string lastIntegrationLabel)
        {
            int lastLabelSuffix = 1;
            string[] splits = lastIntegrationLabel.Split('-');
            string labelWithoutSuffix = splits[0];
            if (!allowDuplicateSubsequentLabels && currentLabel != null && Equals(currentLabel, labelWithoutSuffix))
            {
                if (splits.Length > 1)
                {
                    lastLabelSuffix = Int32.Parse(splits[splits.Length - 1]) + 1;
                }
                return "-" + lastLabelSuffix;
            }
            return string.Empty;
        }
        public class FileReader
        {
            public string GetLabel(string labelFilePath)
            {
                string label = ReadLabel(labelFilePath);
                char[] nonBlankWhiteSpace = { '\r', '\n', '\v', '\f', '\t' };
                for (int i = 0; i < nonBlankWhiteSpace.Length; i++)
                {
                    label = label.Replace(nonBlankWhiteSpace[i], ' ');
                }
                label = label.Trim();
                if (label == string.Empty)
                    throw new CruiseControlException("Label only contains whitespace.");
                return label;
            }
            public virtual string ReadLabel(string labelFilePath)
            {
                string ver;
                try
                {
                    TextReader tr = new StreamReader(labelFilePath);
                    ver = tr.ReadToEnd();
                    tr.Close();
                }
                catch (Exception e)
                {
                    throw new CruiseControlException(
                        String.Format("Error reading file {0}: {1}", labelFilePath, e.Message),
                        e);
                }
                if (ver == string.Empty)
                    throw new CruiseControlException(
                        String.Format("File {0} only contains whitespace.", labelFilePath));
                return ver;
            }
        }
    }
}

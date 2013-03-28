using System;
using System.Text.RegularExpressions;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Label
{
    [ReflectorType("lastChangeLabeller")]
    public class LastChangeLabeller
        : LabellerBase
    {
        private const int INITIAL_SUFFIX_NUMBER = 1;
        [ReflectorProperty("prefix", Required = false)]
        public string LabelPrefix = string.Empty;
        [ReflectorProperty("allowDuplicateSubsequentLabels", Required = false)]
        public bool AllowDuplicateSubsequentLabels = true;
        public override string Generate(IIntegrationResult resultFromThisBuild)
        {
            int changeNumber = 0;
            if (int.TryParse(resultFromThisBuild.LastChangeNumber, out changeNumber))
            {
                Log.Debug(
                    string.Format("LastChangeNumber retrieved - {0}",
                    changeNumber));
            }
            else
            {
                Log.Debug("LastChangeNumber defaulted to 0");
            }
            IntegrationSummary lastIntegration = resultFromThisBuild.LastIntegration;
            string firstSuffix = AllowDuplicateSubsequentLabels ? string.Empty : "." + INITIAL_SUFFIX_NUMBER.ToString();
            if (changeNumber != 0)
            {
                return LabelPrefix + changeNumber + firstSuffix;
            }
            else if (lastIntegration.IsInitial() || lastIntegration.Label == null)
            {
                return LabelPrefix + "unknown" + firstSuffix;
            }
            else if (!AllowDuplicateSubsequentLabels)
            {
                return IncrementLabel(lastIntegration.Label);
            }
            else
            {
                return lastIntegration.Label;
            }
        }
        private string IncrementLabel(string label)
        {
            int current = 0;
            Match match = Regex.Match(label, @"(.*\d+)\.(\d+)$");
            if (match.Success && match.Groups.Count >= 3)
            {
                current = Int32.Parse(match.Groups[2].Value);
                label = match.Groups[1].Value;
            }
            return String.Format("{0}.{1}", label, current + 1);
        }
    }
}

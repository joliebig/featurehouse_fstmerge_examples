namespace ThoughtWorks.CruiseControl.Core.Publishers.Statistics
{
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.IO;
    using System.Xml;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Core.Tasks;
    [ReflectorType("statistics")]
    public class StatisticsPublisher
        : TaskBase
    {
        public const string CsvFileName = "statistics.csv";
        public const string XmlFileName = "report.xml";
        [ReflectorArray("statisticList", Required=false)]
        public StatisticBase[] ConfiguredStatistics = new Statistic[0];
        protected override bool Execute(IIntegrationResult integrationResult)
        {
            StatisticsBuilder builder = new StatisticsBuilder();
            for (int i = 0; i < ConfiguredStatistics.Length; i++)
            {
                StatisticBase statistic = ConfiguredStatistics[i];
                builder.Add(statistic);
            }
            StatisticsResults stats = builder.ProcessBuildResults(integrationResult);
            UpdateXmlFile(stats, integrationResult);
            UpdateCsvFile(stats, builder.Statistics, integrationResult);
            return true;
        }
        private static StatisticsChartGenerator ChartGenerator(List<StatisticBase> statistics)
        {
            StatisticsChartGenerator chartGenerator = new StatisticsChartGenerator();
            List<string> list = new List<String>();
            statistics.ForEach(delegate(StatisticBase statistic)
                                   {
                                       if (statistic.GenerateGraph)
                                       {
                                           list.Add(statistic.Name);
                                       }
                                   });
            chartGenerator.RelevantStats = list.ToArray();
            return chartGenerator;
        }
        private static void UpdateXmlFile(IEnumerable<StatisticResult> stats,
                                                 IIntegrationResult integrationResult)
        {
            Directory.CreateDirectory(integrationResult.ArtifactDirectory);
            System.Text.StringBuilder integration = new System.Text.StringBuilder();
            DateTime now = DateTime.Now;
   integration.AppendFormat("<integration build-label=\"{0}\" status=\"{1}\" day=\"{2}\" month=\"{3}\" year=\"{4}\">",
          integrationResult.Label,
          integrationResult.Status.ToString(),
          now.Day.ToString(), now.ToString("MMM", CultureInfo.InvariantCulture), now.Year.ToString());
            integration.AppendLine(ToXml(stats));
            integration.Append("</integration>");
            string lastFile = XmlStatisticsFile(integrationResult);
            System.IO.FileStream fs = new System.IO.FileStream(lastFile, System.IO.FileMode.Append);
            fs.Seek(0, System.IO.SeekOrigin.End);
            System.IO.StreamWriter sw = new System.IO.StreamWriter(fs);
            sw.WriteLine(integration.ToString());
            sw.Flush();
            fs.Flush();
            sw.Close();
            fs.Close();
        }
        private static string ToXml(IEnumerable<StatisticResult> stats)
        {
            System.Text.StringBuilder el = new System.Text.StringBuilder();
            string result;
            foreach (StatisticResult statisticResult in stats)
            {
                if (statisticResult.Value == null)
                    result = string.Empty;
                else
                    result = statisticResult.Value.ToString();
                el.AppendLine();
                el.AppendFormat("  <statistic name=\"{0}\">{1}</statistic>",
                                    statisticResult.StatName,
                                    result);
            }
            return el.ToString();
        }
        private static string XmlStatisticsFile(IIntegrationResult integrationResult)
        {
            return Path.Combine(integrationResult.ArtifactDirectory, XmlFileName);
        }
        private static void UpdateCsvFile(StatisticsResults statisticsResults, List<StatisticBase> statistics,
                                          IIntegrationResult integrationResult)
        {
            string csvFile = CsvStatisticsFile(integrationResult);
            statisticsResults.AppendCsv(csvFile, statistics);
        }
        private static string CsvStatisticsFile(IIntegrationResult integrationResult)
        {
            return Path.Combine(integrationResult.ArtifactDirectory, CsvFileName);
        }
        public static string LoadStatistics(string artifactDirectory)
        {
            string documentLocation = Path.Combine(artifactDirectory, XmlFileName);
            System.Text.StringBuilder Result = new System.Text.StringBuilder();
            if (File.Exists(documentLocation))
            {
                System.IO.StreamReader sr = new StreamReader(documentLocation);
                Result.AppendLine("<statistics>");
                Result.AppendLine(sr.ReadToEnd());
                sr.Close();
                Result.AppendLine(AppendCurrentDateElement());
                Result.AppendLine("</statistics>");
            }
            return Result.ToString();
        }
        private static string AppendCurrentDateElement()
        {
            DateTime now = DateTime.Now;
            return string.Format("<timestamp day=\"{0}\" month=\"{1}\" year=\"{2}\" />",
                now.Day.ToString(), now.ToString("MMM"), now.Year.ToString());
        }
    }
}

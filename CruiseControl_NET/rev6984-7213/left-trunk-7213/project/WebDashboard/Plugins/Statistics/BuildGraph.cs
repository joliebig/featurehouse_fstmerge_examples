namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Statistics
{
    using System;
    using System.Collections.Generic;
    using ThoughtWorks.CruiseControl.Core;
    using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
    using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
    using ThoughtWorks.CruiseControl.WebDashboard.Plugins.BuildReport;
    using ThoughtWorks.CruiseControl.WebDashboard.Resources;
    public class BuildGraph
    {
        private IBuildSpecifier[] mybuildSpecifiers;
        private ILinkFactory mylinkFactory;
        private Int32 myHighestAmountPerDay;
        private Int32 myOKBuildAmount;
        private Int32 myFailedBuildAmount;
        private Translations translations;
        public BuildGraph(IBuildSpecifier[] buildSpecifiers, ILinkFactory linkFactory, Translations translations)
        {
            mybuildSpecifiers = buildSpecifiers;
            mylinkFactory = linkFactory;
            this.translations = translations;
        }
        public Int32 HighestAmountPerDay
        {
            get
            {
                return myHighestAmountPerDay;
            }
        }
        public Int32 AmountOfOKBuilds
        {
            get
            {
                return myOKBuildAmount;
            }
        }
        public Int32 AmountOfFailedBuilds
        {
            get
            {
                return myFailedBuildAmount;
            }
        }
        public override bool Equals(object obj)
        {
            if (obj.GetType() != this.GetType() )
                return false;
            BuildGraph Comparable = obj as BuildGraph;
            if (this.mybuildSpecifiers.Length != Comparable.mybuildSpecifiers.Length)
                {return false; }
            for (int i=0; i < this.mybuildSpecifiers.Length ; i++)
            {
                if (! this.mybuildSpecifiers[i].Equals(Comparable.mybuildSpecifiers[i]) )
                {return false; }
            }
            return true;
        }
  public override int GetHashCode()
  {
   int hashCode = 0;
   unchecked {
    if (mybuildSpecifiers != null) hashCode += 1000000007 * mybuildSpecifiers.GetHashCode();
    if (mylinkFactory != null) hashCode += 1000000009 * mylinkFactory.GetHashCode();
    hashCode += 1000000021 * myHighestAmountPerDay.GetHashCode();
    hashCode += 1000000033 * myOKBuildAmount.GetHashCode();
    hashCode += 1000000087 * myFailedBuildAmount.GetHashCode();
   }
   return hashCode;
  }
        public List<GraphBuildDayInfo> GetBuildHistory(Int32 maxAmountOfDays)
        {
            GraphBuildInfo currentBuildInfo;
            GraphBuildDayInfo currentBuildDayInfo;
            var foundDates = new Dictionary<DateTime, GraphBuildDayInfo>();
            var dateSorter = new List<DateTime>();
            foreach (IBuildSpecifier buildSpecifier in mybuildSpecifiers)
            {
                currentBuildInfo = new GraphBuildInfo(buildSpecifier, mylinkFactory);
                if (!foundDates.ContainsKey(currentBuildInfo.BuildDate()))
                {
                    foundDates.Add(currentBuildInfo.BuildDate(), new GraphBuildDayInfo(currentBuildInfo, this.translations) );
                    dateSorter.Add(currentBuildInfo.BuildDate());
                }
                else
                {
                    currentBuildDayInfo = foundDates[currentBuildInfo.BuildDate()] as GraphBuildDayInfo;
                    currentBuildDayInfo.AddBuild(currentBuildInfo);
                    foundDates[currentBuildInfo.BuildDate()] = currentBuildDayInfo;
                }
            }
            dateSorter.Sort();
            while (dateSorter.Count > maxAmountOfDays)
            {
                dateSorter.RemoveAt(0);
            }
            var result = new List<GraphBuildDayInfo>();
            myHighestAmountPerDay = 1;
            foreach (DateTime BuildDate in dateSorter)
            {
                currentBuildDayInfo = foundDates[BuildDate] as GraphBuildDayInfo;
                result.Add(currentBuildDayInfo);
                if (currentBuildDayInfo.AmountOfBuilds > myHighestAmountPerDay)
                {
                    myHighestAmountPerDay = currentBuildDayInfo.AmountOfBuilds;
                }
                myOKBuildAmount += currentBuildDayInfo.AmountOfOKBuilds;
                myFailedBuildAmount += currentBuildDayInfo.AmountOfFailedBuilds;
            }
            return result;
        }
        public class GraphBuildInfo
        {
            private IBuildSpecifier mybuildSpecifier;
            private ILinkFactory mylinkFactory;
            public GraphBuildInfo(IBuildSpecifier buildSpecifier, ILinkFactory linkFactory)
            {
                mybuildSpecifier = buildSpecifier;
                mylinkFactory = linkFactory;
            }
            public DateTime BuildDate()
            {
                return new LogFile(mybuildSpecifier.BuildName).Date.Date;
            }
            public bool IsSuccesFull()
            {
                return new LogFile(mybuildSpecifier.BuildName).Succeeded;
            }
            public string LinkTobuild()
            {
                return mylinkFactory.CreateBuildLink(
                    mybuildSpecifier,BuildReportBuildPlugin.ACTION_NAME).Url;
            }
            public string Description()
            {
                DefaultBuildNameFormatter BuildNameFormatter;
                BuildNameFormatter = new DefaultBuildNameFormatter();
                return BuildNameFormatter.GetPrettyBuildName(mybuildSpecifier);
            }
        }
        public class GraphBuildDayInfo
        {
            private DateTime myBuildDate;
            private List<GraphBuildInfo> myBuilds;
            private Int32 myOKBuildAmount;
            private Int32 myFailedBuildAmount;
            private Translations translations;
            public GraphBuildDayInfo(GraphBuildInfo buildInfo, Translations translations)
            {
                this.translations = translations;
                myBuildDate = buildInfo.BuildDate();
                myBuilds = new List<GraphBuildInfo>();
                AddBuild(buildInfo);
            }
            public DateTime BuildDate
            {
                get
                {
                    return myBuildDate;
                }
            }
            public string BuildDateFormatted
            {
                get
                {
                    return myBuildDate.Date.ToString("ddd", this.translations.UICulture)
                           + "<BR>"
                           + myBuildDate.Year.ToString("0000")
                           + "<BR>"
                           + myBuildDate.Month.ToString("00")
                           + "<BR>"
                           + myBuildDate.Day.ToString("00");
                }
            }
            public Int32 AmountOfBuilds
            {
                get
                {
                    return myBuilds.Count;
                }
            }
            public Int32 AmountOfOKBuilds
            {
                get
                {
                    return myOKBuildAmount;
                }
            }
            public Int32 AmountOfFailedBuilds
            {
                get
                {
                    return myFailedBuildAmount;
                }
            }
            public GraphBuildInfo Build(Int32 index)
            {
                return myBuilds[index] as GraphBuildInfo;
            }
            public void AddBuild(GraphBuildInfo buildInfo)
            {
                myBuilds.Insert(0, buildInfo);
                if (buildInfo.IsSuccesFull())
                {
                    myOKBuildAmount++;
                }
                else
                {
                    myFailedBuildAmount++;
                }
            }
        }
 }
}

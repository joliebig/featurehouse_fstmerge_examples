using System;
using System.Collections;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
using ThoughtWorks.CruiseControl.WebDashboard.Plugins.BuildReport;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Statistics
{
    public class BuildGraph
    {
        private IBuildSpecifier[] mybuildSpecifiers;
        private ILinkFactory mylinkFactory;
        private Int32 myHighestAmountPerDay;
        private Int32 myOKBuildAmount;
        private Int32 myFailedBuildAmount;
        public BuildGraph(IBuildSpecifier[] buildSpecifiers, ILinkFactory linkFactory)
        {
            mybuildSpecifiers = buildSpecifiers;
            mylinkFactory = linkFactory;
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
        public override int GetHashCode()
        {
            return (myHighestAmountPerDay + myFailedBuildAmount + myOKBuildAmount).GetHashCode();
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
        public ArrayList GetBuildHistory(Int32 maxAmountOfDays)
        {
            ArrayList Result;
            ArrayList DateSorter;
            Hashtable FoundDates;
            GraphBuildInfo CurrentBuildInfo;
            GraphBuildDayInfo CurrentBuildDayInfo;
            FoundDates = new Hashtable();
            DateSorter = new ArrayList();
            foreach (IBuildSpecifier buildSpecifier in mybuildSpecifiers)
            {
                CurrentBuildInfo = new GraphBuildInfo(buildSpecifier, mylinkFactory);
                if (!FoundDates.Contains(CurrentBuildInfo.BuildDate()))
                {
                    FoundDates.Add(CurrentBuildInfo.BuildDate(), new GraphBuildDayInfo(CurrentBuildInfo) );
                    DateSorter.Add(CurrentBuildInfo.BuildDate());
                }
                else
                {
                    CurrentBuildDayInfo = FoundDates[CurrentBuildInfo.BuildDate()] as GraphBuildDayInfo;
                    CurrentBuildDayInfo.AddBuild(CurrentBuildInfo);
                    FoundDates[CurrentBuildInfo.BuildDate()] = CurrentBuildDayInfo;
                }
            }
            DateSorter.Sort();
            while (DateSorter.Count > maxAmountOfDays)
            {
                DateSorter.RemoveAt(0);
            }
            Result = new ArrayList();
            myHighestAmountPerDay = 1;
            foreach (DateTime BuildDate in DateSorter)
            {
                CurrentBuildDayInfo = FoundDates[BuildDate] as GraphBuildDayInfo;
                Result.Add(CurrentBuildDayInfo);
                if (CurrentBuildDayInfo.AmountOfBuilds > myHighestAmountPerDay)
                {
                    myHighestAmountPerDay = CurrentBuildDayInfo.AmountOfBuilds;
                }
                myOKBuildAmount += CurrentBuildDayInfo.AmountOfOKBuilds;
                myFailedBuildAmount += CurrentBuildDayInfo.AmountOfFailedBuilds;
            }
            return Result;
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
            private ArrayList myBuilds;
            private Int32 myOKBuildAmount;
            private Int32 myFailedBuildAmount;
            public GraphBuildDayInfo(GraphBuildInfo buildInfo)
            {
                myBuildDate = buildInfo.BuildDate();
                myBuilds = new ArrayList();
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
                    return myBuildDate.Date.ToString("ddd")
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

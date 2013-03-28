using System;
using System.Drawing;
using NMock;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.UnitTests.Core;
using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
using ThoughtWorks.CruiseControl.WebDashboard.Plugins.ProjectReport;
using ThoughtWorks.CruiseControl.WebDashboard.ServerConnection;
namespace ThoughtWorks.CruiseControl.UnitTests.WebDashboard.Dashboard
{
 [TestFixture]
 public class ProjectGridTest
 {
  private ProjectGrid projectGrid;
  private DynamicMock urlBuilderMock;
  private DynamicMock linkFactoryMock;
  private IAbsoluteLink projectLink;
  private IServerSpecifier serverSpecifier;
  private IProjectSpecifier projectSpecifier;
  [SetUp]
  public void Setup()
  {
   urlBuilderMock = new DynamicMock(typeof(IUrlBuilder));
   linkFactoryMock = new DynamicMock(typeof(ILinkFactory));
   projectGrid = new ProjectGrid((ILinkFactory) linkFactoryMock.MockInstance);
   serverSpecifier = new DefaultServerSpecifier("server");
   projectSpecifier = new DefaultProjectSpecifier(serverSpecifier, "my project");
   projectLink = new GeneralAbsoluteLink("myLinkText", "myLinkUrl");
  }
  private void VerifyAll()
  {
   urlBuilderMock.Verify();
   linkFactoryMock.Verify();
  }
  private void SetupProjectLinkExpectation()
  {
   SetupProjectLinkExpectation(projectSpecifier);
  }
  private void SetupProjectLinkExpectation(IProjectSpecifier projectSpecifierForLink)
  {
   linkFactoryMock.ExpectAndReturn("CreateProjectLink", projectLink, projectSpecifierForLink, ProjectReportProjectPlugin.ACTION_NAME);
  }
  [Test]
  public void ShouldReturnEmptyListOfRowsWhenNoProjectStatusesAvailable()
  {
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[0];
   Assert.AreEqual(0, projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "").Length);
   VerifyAll();
  }
  [Test]
  public void ShouldCopyProjectNameToProjectRow()
  {
   ProjectStatus projectStatus1 = ProjectStatusFixture.New(projectSpecifier.ProjectName);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual(1, rows.Length);
   Assert.AreEqual(projectSpecifier.ProjectName, rows[0].Name);
   VerifyAll();
  }
  [Test]
  public void ShouldHandleResultsWithNoBuildLabel()
  {
   ProjectStatus projectStatus1 = ProjectStatusFixture.New(projectSpecifier.ProjectName, null);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual(1, rows.Length);
   Assert.AreEqual("no build available", rows[0].LastBuildLabel);
   VerifyAll();
  }
  [Test]
  public void ShouldCopyBuildStatusToProjectRow()
  {
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(ProjectStatusFixture.New(projectSpecifier.ProjectName, IntegrationStatus.Success), serverSpecifier)
    };
   SetupProjectLinkExpectation();
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("Success", rows[0].BuildStatus);
   Assert.AreEqual(Color.Green.Name, rows[0].BuildStatusHtmlColor);
   statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(ProjectStatusFixture.New(projectSpecifier.ProjectName, IntegrationStatus.Failure), serverSpecifier)
    };
   SetupProjectLinkExpectation();
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("Failure", rows[0].BuildStatus);
   Assert.AreEqual(Color.Red.Name, rows[0].BuildStatusHtmlColor);
   statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(ProjectStatusFixture.New(projectSpecifier.ProjectName, IntegrationStatus.Unknown), serverSpecifier)
    };
   SetupProjectLinkExpectation();
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("Unknown", rows[0].BuildStatus);
   Assert.AreEqual(Color.Blue.Name, rows[0].BuildStatusHtmlColor);
   statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(ProjectStatusFixture.New(projectSpecifier.ProjectName, IntegrationStatus.Exception), serverSpecifier)
    };
   SetupProjectLinkExpectation();
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("Exception", rows[0].BuildStatus);
   Assert.AreEqual(Color.Red.Name, rows[0].BuildStatusHtmlColor);
   VerifyAll();
  }
  [Test]
  public void ShouldCopyLastBuildDateToProjectRow()
  {
   DateTime date = DateTime.Today;
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(ProjectStatusFixture.New(projectSpecifier.ProjectName, IntegrationStatus.Success, date), serverSpecifier)
    };
   SetupProjectLinkExpectation();
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual(DateUtil.FormatDate(date), rows[0].LastBuildDate);
   VerifyAll();
  }
  [Test]
  public void ShouldCopyProjectStatusToProjectRow()
  {
   ProjectStatus projectStatus1 = new ProjectStatus(projectSpecifier.ProjectName, "category",
                                                             ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "my label", null, DateTime.Today, "building", "", 0);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("Running", rows[0].Status);
   VerifyAll();
   projectStatus1 = new ProjectStatus(projectSpecifier.ProjectName, "category",
                                               ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Stopped, "url", DateTime.Today, "my label", null, DateTime.Today, "", "", 0);
   statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("Stopped", rows[0].Status);
   VerifyAll();
  }
  [Test]
  public void ShouldCopyProjectActivityToProjectRow()
  {
   ProjectStatus projectStatus1 = new ProjectStatus(projectSpecifier.ProjectName, "category",
                                                             ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "my label", null, DateTime.Today, "", "", 0);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("Sleeping", rows[0].Activity);
   VerifyAll();
   projectStatus1 = new ProjectStatus(projectSpecifier.ProjectName, "category",
                                               ProjectActivity.CheckingModifications, IntegrationStatus.Success, ProjectIntegratorState.Stopped, "url", DateTime.Today, "my label", null, DateTime.Today, "", "", 0);
   statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, new DefaultServerSpecifier("server"))
    };
   SetupProjectLinkExpectation();
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("CheckingModifications", rows[0].Activity);
   VerifyAll();
  }
  [Test]
  public void ShouldCopyLastBuildLabelToProjectRow()
  {
   DateTime date = DateTime.Today;
   ProjectStatus projectStatus1 = new ProjectStatus(projectSpecifier.ProjectName, "category",
                                                             ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", date, "my label", null, DateTime.Today, "", "", 0);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("my label", rows[0].LastBuildLabel);
   VerifyAll();
  }
  [Test]
  public void ShouldCreateLinkToProjectReport()
  {
   ProjectStatus projectStatus1 = new ProjectStatus(projectSpecifier.ProjectName, "category",
                                                             ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "1", null, DateTime.Today, "", "", 0);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("myLinkUrl", rows[0].Url);
   VerifyAll();
  }
  [Test]
  public void ShouldDisplayCurrentProjectMessagesInProjectGridRow()
  {
   ProjectStatus projectStatus1 = new ProjectStatus(projectSpecifier.ProjectName, "category",
                ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "my label", null, DateTime.Today, "", "", 0);
   projectStatus1.Messages = new Message[1] {new Message("Test Message")};
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.IsNotNull(rows[0].CurrentMessage);
   Assert.AreEqual("Test Message", rows[0].CurrentMessage);
   VerifyAll();
   projectStatus1 = new ProjectStatus(projectSpecifier.ProjectName, "category",
                ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Stopped, "url", DateTime.Today, "my label", null, DateTime.Today, "", "", 0);
   projectStatus1.Messages = new Message[2] {new Message(string.Empty), new Message("Second Message")};
   statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.IsNotNull(rows[0].CurrentMessage);
   Assert.AreEqual("Second Message", rows[0].CurrentMessage);
   VerifyAll();
  }
  [Test]
  public void ShouldCopyProjectCategoryToProjectRow()
  {
   ProjectStatus projectStatus1 = new ProjectStatus(projectSpecifier.ProjectName, "category",
                ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "my label", null, DateTime.Today, "", "", 0);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("category", rows[0].Category);
   VerifyAll();
   projectStatus1 = new ProjectStatus(projectSpecifier.ProjectName, "category1",
                ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Stopped, "url", DateTime.Today, "my label", null, DateTime.Today, "", "", 0);
   statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier)
    };
   SetupProjectLinkExpectation();
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual("category1", rows[0].Category);
   VerifyAll();
  }
  [Test]
  public void ShouldReturnProjectsSortedByNameIfNameColumnSpecifiedAsSortSeed()
  {
   IProjectSpecifier projectA = new DefaultProjectSpecifier(serverSpecifier, "a");
   IProjectSpecifier projectB = new DefaultProjectSpecifier(serverSpecifier, "b");
   ProjectStatus projectStatus1 = new ProjectStatus("a", "category",
                                                             ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "1", null, DateTime.Today, "", "", 0);
   ProjectStatus projectStatus2 = new ProjectStatus("b", "category",
                                                             ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "1", null, DateTime.Today, "", "", 0);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier),
     new ProjectStatusOnServer(projectStatus2, serverSpecifier)
    };
   SetupProjectLinkExpectation(projectA);
   SetupProjectLinkExpectation(projectB);
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, true, "");
   Assert.AreEqual(2, rows.Length);
   Assert.AreEqual("a", rows[0].Name);
   Assert.AreEqual("b", rows[1].Name);
   SetupProjectLinkExpectation(projectA);
   SetupProjectLinkExpectation(projectB);
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.Name, false, "");
   Assert.AreEqual(2, rows.Length);
   Assert.AreEqual("b", rows[0].Name);
   Assert.AreEqual("a", rows[1].Name);
   VerifyAll();
  }
  [Test]
  public void ShouldReturnProjectsSortedByLastBuildDateIfLastBuildDateColumnSpecifiedAsSortSeed()
  {
   IProjectSpecifier projectA = new DefaultProjectSpecifier(serverSpecifier, "a");
   IProjectSpecifier projectB = new DefaultProjectSpecifier(serverSpecifier, "b");
   ProjectStatus projectStatus1 = new ProjectStatus("b", "category",
                                                             ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "1", null, DateTime.Today, "", "", 0);
   ProjectStatus projectStatus2 = new ProjectStatus("a", "category",
                                                             ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today.AddHours(1), "1", null, DateTime.Today, "", "", 0);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier),
     new ProjectStatusOnServer(projectStatus2, serverSpecifier)
    };
   SetupProjectLinkExpectation(projectB);
   SetupProjectLinkExpectation(projectA);
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.LastBuildDate, true, "");
   Assert.AreEqual(2, rows.Length);
   Assert.AreEqual("b", rows[0].Name);
   Assert.AreEqual("a", rows[1].Name);
   SetupProjectLinkExpectation(projectB);
   SetupProjectLinkExpectation(projectA);
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.LastBuildDate, false, "");
   Assert.AreEqual(2, rows.Length);
   Assert.AreEqual("a", rows[0].Name);
   Assert.AreEqual("b", rows[1].Name);
   VerifyAll();
  }
  [Test]
  public void ShouldReturnProjectsSortedByBuildStatusIfBuildStatusColumnSpecifiedAsSortSeed()
  {
   IProjectSpecifier projectA = new DefaultProjectSpecifier(serverSpecifier, "a");
   IProjectSpecifier projectB = new DefaultProjectSpecifier(serverSpecifier, "b");
   ProjectStatus projectStatus1 = new ProjectStatus("a", "category",
                                                             ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "1", null, DateTime.Today, "", "", 0);
   ProjectStatus projectStatus2 = new ProjectStatus("b", "category",
                                                             ProjectActivity.Sleeping, IntegrationStatus.Failure, ProjectIntegratorState.Running, "url", DateTime.Today.AddHours(1), "1", null, DateTime.Today, "", "", 0);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifier),
     new ProjectStatusOnServer(projectStatus2, serverSpecifier)
    };
   SetupProjectLinkExpectation(projectA);
   SetupProjectLinkExpectation(projectB);
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.BuildStatus, true, "");
   Assert.AreEqual(2, rows.Length);
   Assert.AreEqual("b", rows[0].Name);
   Assert.AreEqual("a", rows[1].Name);
   SetupProjectLinkExpectation(projectA);
   SetupProjectLinkExpectation(projectB);
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.BuildStatus, false, "");
   Assert.AreEqual(2, rows.Length);
   Assert.AreEqual("a", rows[0].Name);
   Assert.AreEqual("b", rows[1].Name);
   VerifyAll();
  }
  [Test]
  public void ShouldReturnProjectsSortedByServerIfServerNameColumnSpecifiedAsSortSeed()
  {
   IServerSpecifier serverSpecifierA = new DefaultServerSpecifier("Aserver");
   IServerSpecifier serverSpecifierB = new DefaultServerSpecifier("Bserver");
   IProjectSpecifier projectA = new DefaultProjectSpecifier(serverSpecifierA, "a");
   IProjectSpecifier projectB = new DefaultProjectSpecifier(serverSpecifierB, "b");
   ProjectStatus projectStatus1 = new ProjectStatus("a", "category",
                ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "1", null, DateTime.Today, "", "", 0);
   ProjectStatus projectStatus2 = new ProjectStatus("b", "category",
                ProjectActivity.Sleeping, IntegrationStatus.Failure, ProjectIntegratorState.Running, "url", DateTime.Today.AddHours(1), "1", null, DateTime.Today, "", "", 0);
   ProjectStatusOnServer[] statusses = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatus1, serverSpecifierA),
     new ProjectStatusOnServer(projectStatus2, serverSpecifierB)
    };
   SetupProjectLinkExpectation(projectA);
   SetupProjectLinkExpectation(projectB);
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.ServerName, true, "");
   Assert.AreEqual(2, rows.Length);
   Assert.AreEqual("a", rows[0].Name);
   Assert.AreEqual("b", rows[1].Name);
   SetupProjectLinkExpectation(projectA);
   SetupProjectLinkExpectation(projectB);
   rows = projectGrid.GenerateProjectGridRows(statusses, "myAction", ProjectGridSortColumn.ServerName, false, "");
   Assert.AreEqual(2, rows.Length);
   Assert.AreEqual("b", rows[0].Name);
   Assert.AreEqual("a", rows[1].Name);
   VerifyAll();
  }
  [Test]
  public void ShouldReturnProjectsSortedByCategoryIfCategoryColumnSpecifiedAsSortSeed()
  {
   IProjectSpecifier projectA = new DefaultProjectSpecifier(serverSpecifier, "A");
   IProjectSpecifier projectB = new DefaultProjectSpecifier(serverSpecifier, "B");
   IProjectSpecifier projectC = new DefaultProjectSpecifier(serverSpecifier, "C");
            ProjectStatus projectStatusA = new ProjectStatus("A", "CategoryX", ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "1", null, DateTime.Today, "", "", 0);
            ProjectStatus projectStatusB = new ProjectStatus("B", "CategoryY", ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "1", null, DateTime.Today, "", "", 0);
            ProjectStatus projectStatusC = new ProjectStatus("C", "CategoryX", ProjectActivity.Sleeping, IntegrationStatus.Success, ProjectIntegratorState.Running, "url", DateTime.Today, "1", null, DateTime.Today, "", "", 0);
   ProjectStatusOnServer[] status = new ProjectStatusOnServer[]
    {
     new ProjectStatusOnServer(projectStatusA, serverSpecifier),
     new ProjectStatusOnServer(projectStatusB, serverSpecifier),
     new ProjectStatusOnServer(projectStatusC, serverSpecifier)
    };
   SetupProjectLinkExpectation(projectA);
   SetupProjectLinkExpectation(projectB);
   SetupProjectLinkExpectation(projectC);
   ProjectGridRow[] rows = projectGrid.GenerateProjectGridRows(status, "myAction", ProjectGridSortColumn.Category, true, "");
   Assert.AreEqual(3, rows.Length);
   Assert.AreEqual("C", rows[0].Name);
   Assert.AreEqual("A", rows[1].Name);
   Assert.AreEqual("B", rows[2].Name);
   SetupProjectLinkExpectation(projectA);
   SetupProjectLinkExpectation(projectB);
   SetupProjectLinkExpectation(projectC);
   rows = projectGrid.GenerateProjectGridRows(status, "myAction", ProjectGridSortColumn.Category, false, "");
   Assert.AreEqual(3, rows.Length);
   Assert.AreEqual("B", rows[0].Name);
   Assert.AreEqual("C", rows[1].Name);
   Assert.AreEqual("A", rows[2].Name);
   VerifyAll();
  }
 }
}

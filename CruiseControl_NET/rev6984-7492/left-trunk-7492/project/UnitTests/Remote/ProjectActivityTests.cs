using NUnit.Framework;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.UnitTests.Remote
{
    [TestFixture]
    public class ProjectActivityTests
    {
        [Test]
        public void TypeGetSetTest()
        {
            ProjectActivity activity = new ProjectActivity();
            activity.Type = "testing";
            Assert.AreEqual("testing", activity.Type);
        }
        [Test]
        public void IsPendingReturnsTrueForPendingType()
        {
            ProjectActivity activity = ProjectActivity.Pending;
            Assert.IsTrue(activity.IsPending());
        }
        [Test]
        public void IsPendingReturnsFalseForNonPendingType()
        {
            ProjectActivity activity = ProjectActivity.CheckingModifications;
            Assert.IsFalse(activity.IsPending());
        }
        [Test]
        public void IsCheckingModificationsReturnsTrueWhenCheckingModifications()
        {
            Assert.IsTrue(ProjectActivity.CheckingModifications.IsCheckingModifications());
        }
        [Test]
        public void IsCheckingModificationsReturnsFalseForAllOtherStates()
        {
            Assert.IsFalse(ProjectActivity.Building.IsCheckingModifications());
            Assert.IsFalse(ProjectActivity.Pending.IsCheckingModifications());
            Assert.IsFalse(ProjectActivity.Sleeping.IsCheckingModifications());
        }
    }
}

using System;
using System.Collections;
using System.Net.Mail;
using System.Xml;
using Exortech.NetReflector;
using NMock;
using NMock.Constraints;
using NUnit.Framework;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Publishers;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.UnitTests.Core.Publishers
{
 [TestFixture]
 public class EmailPublisherTest : CustomAssertion
 {
  private EmailPublisher publisher;
  private IMock mockGateway;
  [SetUp]
  public void SetUp()
  {
   publisher = EmailPublisherMother.Create();
   mockGateway = new DynamicMock(typeof(EmailGateway));
   publisher.EmailGateway = (EmailGateway) mockGateway.MockInstance;
  }
  [Test]
  public void SendMessage()
  {
            mockGateway.Expect("Send", new MailMessageValidator());
   publisher.SendMessage("from_@foo.com", "to@bar.com", "replyto@bar.com", "test subject", "test message", "workingDir");
            mockGateway.Verify();
  }
     [Test]
  public void ShouldNotSendMessageIfRecipientIsNotSpecifiedAndBuildIsSuccessful()
  {
            mockGateway.ExpectNoCall("Send", typeof(MailMessage));
   publisher = new EmailPublisher();
   publisher.EmailGateway = (EmailGateway) mockGateway.MockInstance;
   publisher.EmailUsers.Add("bar", new EmailUser("bar", "foo", "bar@foo.com"));
            publisher.EmailGroups.Add("foo", new EmailGroup("foo", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Change }));
   publisher.Run(IntegrationResultMother.CreateStillSuccessful());
            mockGateway.Verify();
  }
  [Test]
  public void ShouldSendMessageIfRecipientIsNotSpecifiedAndBuildFailed()
  {
            mockGateway.Expect("Send", new MailMessageRecipientValidator(1));
   publisher = new EmailPublisher();
      publisher.FromAddress = "from_@foo.com";
   publisher.EmailGateway = (EmailGateway) mockGateway.MockInstance;
   publisher.EmailUsers.Add("bar", new EmailUser("bar", "foo", "bar@foo.com"));
            publisher.EmailGroups.Add("foo", new EmailGroup("foo", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Change }));
   publisher.Run(IntegrationResultMother.CreateFailed());
            mockGateway.Verify();
  }
     [Test]
  public void ShouldSendMessageIfBuildFailed()
  {
            mockGateway.Expect("Send", new MailMessageRecipientValidator(1));
            publisher = new EmailPublisher();
            publisher.FromAddress = "from_@foo.com";
   publisher.EmailGateway = (EmailGateway) mockGateway.MockInstance;
   publisher.EmailUsers.Add("bar", new EmailUser("bar", "foo", "bar@foo.com"));
            publisher.EmailGroups.Add("foo", new EmailGroup("foo", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Failed }));
   publisher.Run(IntegrationResultMother.CreateFailed() );
            mockGateway.Verify();
        }
        [Test]
  public void ShouldSendMessageIfBuildFailedAndPreviousFailed()
  {
            mockGateway.Expect("Send", new MailMessageRecipientValidator(1));
            publisher = new EmailPublisher();
            publisher.FromAddress = "from_@foo.com";
            publisher.EmailGateway = (EmailGateway)mockGateway.MockInstance;
   publisher.EmailUsers.Add("dev", new EmailUser("dev", "changing", "dev@foo.com"));
   publisher.EmailUsers.Add("admin", new EmailUser("admin", "failing", "bar@foo.com"));
            publisher.EmailGroups.Add("changing", new EmailGroup("changing", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Change }));
            publisher.EmailGroups.Add("failing", new EmailGroup("failing", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Failed }));
   publisher.Run(IntegrationResultMother.CreateFailed(IntegrationStatus.Failure) );
            mockGateway.Verify();
  }
  [Test]
  public void ShouldSendMessageIfBuildFailedAndPreviousOK()
  {
            mockGateway.Expect("Send", new MailMessageRecipientValidator(2));
            publisher = new EmailPublisher();
            publisher.FromAddress = "from_@foo.com";
            publisher.EmailGateway = (EmailGateway)mockGateway.MockInstance;
   publisher.EmailUsers.Add("dev", new EmailUser("dev", "changing", "dev@foo.com"));
   publisher.EmailUsers.Add("admin", new EmailUser("admin", "failing", "bar@foo.com"));
            publisher.EmailGroups.Add("changing", new EmailGroup("changing", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Change }));
            publisher.EmailGroups.Add("failing", new EmailGroup("failing", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Failed }));
   publisher.Run(IntegrationResultMother.CreateFailed(IntegrationStatus.Success) );
            mockGateway.Verify();
        }
        [Test]
        public void ShouldSendMessageIfBuildSuccessful()
        {
            mockGateway.Expect("Send", new MailMessageRecipientValidator(1));
            publisher = new EmailPublisher();
            publisher.FromAddress = "from_@foo.com";
            publisher.EmailGateway = (EmailGateway)mockGateway.MockInstance;
            publisher.EmailUsers.Add("bar", new EmailUser("bar", "foo", "bar@foo.com"));
            publisher.EmailGroups.Add("foo", new EmailGroup("foo", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Success }));
            publisher.Run(IntegrationResultMother.CreateSuccessful());
            mockGateway.Verify();
        }
        [Test]
        public void ShouldSendMessageIfBuildSuccessfulAndPreviousFailed()
        {
            mockGateway.Expect("Send", new MailMessageRecipientValidator(2));
            publisher = new EmailPublisher();
            publisher.FromAddress = "from_@foo.com";
            publisher.EmailGateway = (EmailGateway)mockGateway.MockInstance;
            publisher.EmailUsers.Add("dev", new EmailUser("dev", "changing", "dev@foo.com"));
            publisher.EmailUsers.Add("admin", new EmailUser("admin", "succeeding", "bar@foo.com"));
            publisher.EmailUsers.Add("fixer", new EmailUser("fixer", "fixing", "bar@foo.com"));
            publisher.EmailGroups.Add("changing", new EmailGroup("changing", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Change }));
            publisher.EmailGroups.Add("succeeding", new EmailGroup("succeeding", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Success }));
            publisher.EmailGroups.Add("fixing", new EmailGroup("fixing", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Fixed }));
            publisher.Run(IntegrationResultMother.CreateSuccessful(IntegrationStatus.Failure));
            mockGateway.Verify();
        }
        [Test]
        public void ShouldSendMessageIfBuildSuccessfulAndPreviousSuccessful()
        {
            mockGateway.Expect("Send", new MailMessageRecipientValidator(1));
            publisher = new EmailPublisher();
            publisher.FromAddress = "from_@foo.com";
            publisher.EmailGateway = (EmailGateway)mockGateway.MockInstance;
            publisher.EmailUsers.Add("dev", new EmailUser("dev", "changing", "dev@foo.com"));
            publisher.EmailUsers.Add("admin", new EmailUser("admin", "succeeding", "bar@foo.com"));
            publisher.EmailGroups.Add("changing", new EmailGroup("changing", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Change }));
            publisher.EmailGroups.Add("succeeding", new EmailGroup("succeeding", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Success }));
            publisher.Run(IntegrationResultMother.CreateSuccessful(IntegrationStatus.Success));
            mockGateway.Verify();
        }
        [Test]
        public void ShouldSendToModifiersAndFailureUsers()
        {
            mockGateway.Expect("Send", new MailMessageRecipientValidator(2));
            publisher = new EmailPublisher();
            publisher.FromAddress = "from_@foo.com";
            publisher.EmailGateway = (EmailGateway)mockGateway.MockInstance;
            publisher.EmailUsers.Add("user1", new EmailUser("user1", null, "user1@foo.com"));
            publisher.EmailUsers.Add("user2", new EmailUser("user2", null, "user2@foo.com"));
            IntegrationResult result;
            Modification modification;
            result = IntegrationResultMother.CreateFailed();
            result.FailureUsers.Add("user1");
            modification = new Modification();
            modification.UserName = "user2";
            modification.ModifiedTime = new DateTime(1973, 12, 24, 2, 30, 00);
            result.Modifications = new Modification[] { modification };
            publisher.Run(result);
            mockGateway.Verify();
        }
        [Test]
        public void ShouldSendFixedMailToFailureUsersWithModificationNotificationSetToFailedAndFixed()
        {
            mockGateway.Expect("Send", new MailMessageRecipientValidator(2));
            publisher = new EmailPublisher();
            publisher.FromAddress = "from_@foo.com";
            publisher.EmailGateway = (EmailGateway)mockGateway.MockInstance;
            publisher.ModifierNotificationTypes = new EmailGroup.NotificationType[2];
            publisher.ModifierNotificationTypes[0] = EmailGroup.NotificationType.Failed;
            publisher.ModifierNotificationTypes[1] = EmailGroup.NotificationType.Fixed;
            publisher.EmailUsers.Add("user1", new EmailUser("user1", null, "user1@foo.com"));
            publisher.EmailUsers.Add("user2", new EmailUser("user2", null, "user2@foo.com"));
            IntegrationResult result;
            Modification modification;
            result = IntegrationResultMother.CreateFixed();
            result.FailureUsers.Add("user1");
            modification = new Modification();
            modification.UserName = "user2";
            modification.ModifiedTime = new DateTime(1973, 12, 24, 2, 30, 00);
            result.Modifications = new Modification[] { modification };
            publisher.Run(result);
            mockGateway.Verify();
        }
        private static IntegrationResult CreateIntegrationResult(IntegrationStatus current, IntegrationStatus last)
  {
   IntegrationResult result = IntegrationResultMother.Create(current, last, new DateTime(1980, 1, 1));
   result.ProjectName = "Project#9";
   result.Label = "0";
   return result;
  }
  [Test]
  public void EmailMessageWithDetails()
  {
   publisher.IncludeDetails = true;
   string message = publisher.CreateMessage(CreateIntegrationResult(IntegrationStatus.Success, IntegrationStatus.Success));
   Assert.IsTrue(message.StartsWith("<html>"));
   Assert.IsTrue(message.IndexOf("CruiseControl.NET Build Results for project Project#9") > 0);
   Assert.IsTrue(message.IndexOf("Modifications since last build") > 0);
   Assert.IsTrue(message.EndsWith("</html>"));
  }
  [Test]
  public void IfThereIsAnExceptionBuildMessageShouldPublishExceptionMessage()
  {
   DynamicMock mock = new DynamicMock(typeof(IMessageBuilder));
   mock.ExpectAndThrow("BuildMessage", new Exception("oops"), new IsAnything());
   publisher = new EmailPublisher((IMessageBuilder) mock.MockInstance);
   string message = publisher.CreateMessage(new IntegrationResult());
   AssertContains("oops", message);
  }
  [Test]
  public void Publish()
  {
            mockGateway.Expect("Send", new IsAnything());
   IntegrationResult result = IntegrationResultMother.CreateStillSuccessful();
   publisher.Run(result);
            mockGateway.Verify();
  }
  [Test]
  public void UnitTestResultsShouldBeIncludedInEmailMessageWhenIncludesDetailsIsTrue()
  {
   IntegrationResult result = IntegrationResultMother.CreateStillSuccessful();
            System.IO.StreamReader reader = System.IO.File.OpenText("UnitTestResults.xml");
            string results = reader.ReadToEnd();
            reader.Close();
   result.AddTaskResult(results);
   publisher.IncludeDetails = true;
   string message = publisher.CreateMessage(result);
   Assert.IsTrue(message.IndexOf("Tests run") >= 0);
  }
        [Test]
        public void UnitTestResultsShouldNotBeIncludedInEmailMessageWhenIncludesDetailsIsTrueAndNoUnitTestXslIsDefined()
        {
            IntegrationResult result = IntegrationResultMother.CreateStillSuccessful();
            System.IO.StreamReader reader = System.IO.File.OpenText("UnitTestResults.xml");
            string results = reader.ReadToEnd();
            reader.Close();
            result.AddTaskResult(results);
            publisher.IncludeDetails = true;
            string[] xslFiles = { @"xsl\NCover.xsl" };
            publisher.XslFiles = xslFiles;
            string message = publisher.CreateMessage(result);
            Assert.IsFalse(message.IndexOf("Tests run") >= 0);
        }
  [Test]
  public void Publish_UnknownIntegrationStatus()
  {
            mockGateway.ExpectNoCall("Send", typeof(MailMessage));
   publisher.Run(new IntegrationResult());
            mockGateway.Verify();
  }
     [Test]
  public void PopulateFromConfiguration()
  {
   publisher = EmailPublisherMother.Create();
   Assert.AreEqual("smtp.telus.net", publisher.MailHost);
            Assert.AreEqual(26, publisher.MailPort);
   Assert.AreEqual("mailuser", publisher.MailhostUsername);
   Assert.AreEqual("mailpassword", publisher.MailhostPassword);
   Assert.AreEqual("ccnet@thoughtworks.com", publisher.FromAddress);
            Assert.AreEqual(2, publisher.ModifierNotificationTypes.Length);
            Assert.AreEqual(EmailGroup.NotificationType.Failed, publisher.ModifierNotificationTypes[0]);
            Assert.AreEqual(EmailGroup.NotificationType.Fixed, publisher.ModifierNotificationTypes[1]);
            Assert.AreEqual(1, publisher.Converters.Length);
            Assert.AreEqual("$", ((EmailRegexConverter) publisher.Converters[0]).Find);
            Assert.AreEqual("@TheCompany.com", ((EmailRegexConverter) publisher.Converters[0]).Replace);
   Assert.AreEqual(6, publisher.EmailUsers.Count);
   ArrayList expected = new ArrayList();
   expected.Add(new EmailUser("buildmaster", "buildmaster", "servid@telus.net"));
   expected.Add(new EmailUser("orogers", "developers", "orogers@thoughtworks.com"));
   expected.Add(new EmailUser("manders", "developers", "mandersen@thoughtworks.com"));
   expected.Add(new EmailUser("dmercier", "developers", "dmercier@thoughtworks.com"));
   expected.Add(new EmailUser("rwan", "developers", "rwan@thoughtworks.com"));
            expected.Add(new EmailUser("owjones", "successdudes", "oliver.wendell.jones@example.com"));
   for (int i = 0; i < expected.Count; i++)
   {
    Assert.IsTrue(publisher.EmailUsers.ContainsValue(expected[i]));
   }
   Assert.AreEqual(3, publisher.EmailGroups.Count);
            EmailGroup developers = new EmailGroup("developers", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Change });
            EmailGroup buildmaster = new EmailGroup("buildmaster", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Always });
            EmailGroup successdudes = new EmailGroup("successdudes", new EmailGroup.NotificationType[] { EmailGroup.NotificationType.Success });
   Assert.AreEqual(developers, publisher.EmailGroups["developers"]);
   Assert.AreEqual(buildmaster, publisher.EmailGroups["buildmaster"]);
            Assert.AreEqual(successdudes, publisher.EmailGroups["successdudes"]);
  }
        [Test]
        public void ShouldPopulateFromMinimalConfiguration()
        {
            string configXml = @"<email from_=""ccnet@example.com"" mailhost=""smtp.example.com""> <users/> <groups/> </email>";
            XmlDocument configXmlDocument = XmlUtil.CreateDocument(configXml);
            publisher = EmailPublisherMother.Create(configXmlDocument.DocumentElement);
            Assert.AreEqual("smtp.example.com", publisher.MailHost);
            Assert.AreEqual(25, publisher.MailPort);
            Assert.AreEqual(null, publisher.MailhostUsername);
            Assert.AreEqual(null, publisher.MailhostPassword);
            Assert.AreEqual(null, publisher.ReplyToAddress);
            Assert.AreEqual(false, publisher.IncludeDetails);
            Assert.AreEqual("ccnet@example.com", publisher.FromAddress);
            Assert.AreEqual(1, publisher.ModifierNotificationTypes.Length);
            Assert.AreEqual(EmailGroup.NotificationType.Always, publisher.ModifierNotificationTypes[0]);
            Assert.AreEqual(0, publisher.Converters.Length);
            Assert.AreEqual(0, publisher.EmailUsers.Count);
            Assert.AreEqual(0, publisher.EmailGroups.Count);
        }
        [Test]
        public void ShouldPopulateXslFiles()
        {
            string configXml = @"<email from_=""ccnet@example.com"" mailhost=""smtp.example.com""> <users/> <groups/>
       <xslFiles>
                        <file>xsl\NCover.xsl</file>
        <file>xsl\NCoverExplorer.xsl</file>
     </xslFiles>
                </email>";
            XmlDocument configXmlDocument = XmlUtil.CreateDocument(configXml);
            publisher = EmailPublisherMother.Create(configXmlDocument.DocumentElement);
            Assert.AreEqual(2, publisher.XslFiles.Length);
            Assert.AreEqual(@"xsl\NCover.xsl", publisher.XslFiles[0]);
            Assert.AreEqual(@"xsl\NCoverExplorer.xsl", publisher.XslFiles[1]);
        }
        [Test]
  public void SerializeToXml()
  {
   publisher = EmailPublisherMother.Create();
   string xml = NetReflector.Write(publisher);
   XmlUtil.VerifyXmlIsWellFormed(xml);
  }
  [Test]
  public void VerifyEmailSubjectAndMessageForExceptionIntegrationResult()
  {
   IntegrationResult result = CreateIntegrationResult(IntegrationStatus.Exception, IntegrationStatus.Unknown);
   result.ExceptionResult = new CruiseControlException("test exception");
   string message = publisher.CreateMessage(result);
            Assert.IsTrue(message.StartsWith("CruiseControl.NET Build Results for project Project#9"));
   publisher.IncludeDetails = true;
   string actual = publisher.CreateMessage(result);
   Assert.IsTrue(actual.IndexOf(result.ExceptionResult.Message) > 0);
   Assert.IsTrue(actual.IndexOf(result.ExceptionResult.GetType().Name) > 0);
   Assert.IsTrue(actual.IndexOf("BUILD COMPLETE") == -1);
  }
        private class MailMessageValidator : BaseConstraint
        {
            public override bool Eval(object val)
            {
                MailMessage message = (MailMessage)val;
                Assert.AreEqual("from_@foo.com", message.from_.Address);
                Assert.AreEqual("to@bar.com", message.To[0].Address);
                Assert.AreEqual("replyto@bar.com", message.ReplyTo.Address);
                Assert.AreEqual("test subject", message.Subject);
                Assert.AreEqual("test message", message.Body);
                return true;
            }
            public override string Message
            {
                get { return "MailMessage does not match!"; }
            }
        }
     private class MailMessageRecipientValidator : BaseConstraint
     {
         private readonly int recipients;
         public MailMessageRecipientValidator(int recipients)
         {
             this.recipients = recipients;
         }
         public override bool Eval(object val)
         {
             return recipients == ((MailMessage) val).To.Count;
         }
         public override string Message
         {
             get { return "Invalid number of recipients!"; }
         }
     }
 }
}

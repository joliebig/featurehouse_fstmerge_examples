namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Net.Mail;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Config;
    using ThoughtWorks.CruiseControl.Core.Tasks;
    using ThoughtWorks.CruiseControl.Core.Util;
    using ThoughtWorks.CruiseControl.Remote;
    [ReflectorType("email")]
    public class EmailPublisher
        : TaskBase, IConfigurationValidation
    {
        private EmailGateway emailGateway = new EmailGateway();
        private string fromAddress;
        private string replytoAddress;
        private string subjectPrefix;
        private IMessageBuilder messageBuilder;
        private EmailGroup.NotificationType[] modifierNotificationTypes = { EmailGroup.NotificationType.Always };
        private IEmailConverter[] converters = new IEmailConverter[0];
        private EmailSubject[] subjectSettings = new EmailSubject[0];
        private string[] xslFiles;
        public EmailPublisher()
            : this(new HtmlLinkMessageBuilder(false))
        { }
        public EmailPublisher(IMessageBuilder messageBuilder)
        {
            this.messageBuilder = messageBuilder;
            this.IndexedEmailUsers = new Dictionary<string, EmailUser>();
            this.IndexedEmailGroups = new Dictionary<string, EmailGroup>();
        }
        public EmailGateway EmailGateway
        {
            get { return emailGateway; }
            set { emailGateway = value; }
        }
        public IMessageBuilder MessageBuilder
        {
            get { return messageBuilder; }
            set { messageBuilder = value; }
        }
        [ReflectorProperty("mailhost")]
        public string MailHost
        {
            get { return EmailGateway.MailHost; }
            set { EmailGateway.MailHost = value; }
        }
        [ReflectorProperty("mailport", Required = false)]
        public int MailPort
        {
            get { return EmailGateway.MailPort; }
            set { EmailGateway.MailPort = value; }
        }
        [ReflectorProperty("mailhostUsername", Required = false)]
        public string MailhostUsername
        {
            get { return EmailGateway.MailHostUsername; }
            set { EmailGateway.MailHostUsername = value; }
        }
        [ReflectorProperty("mailhostPassword", typeof(PrivateStringSerialiserFactory), Required = false)]
        public PrivateString MailhostPassword
        {
            get { return EmailGateway.MailHostPassword; }
            set { EmailGateway.MailHostPassword = value; }
        }
        [ReflectorProperty("from_")]
        public string FromAddress
        {
            get { return fromAddress; }
            set { fromAddress = value; }
        }
        [ReflectorProperty("useSSL", Required = false)]
        public bool UseSSL
        {
            get { return EmailGateway.UseSSL; }
            set { EmailGateway.UseSSL = value; }
        }
        [ReflectorProperty("replyto", Required = false)]
        public string ReplyToAddress
        {
            get { return replytoAddress; }
            set { replytoAddress = value; }
        }
        [ReflectorProperty("xslFiles", Required = false)]
        public string[] XslFiles
        {
            get { return xslFiles; }
            set { xslFiles = value; }
        }
        [ReflectorProperty("attachments", Required = false)]
        public string[] Attachments { get; set; }
        [ReflectorProperty("includeDetails", Required = false)]
        public bool IncludeDetails
        {
            get
            {
                return messageBuilder is HtmlDetailsMessageBuilder;
            }
            set
            {
                if (value)
                {
                    messageBuilder = new HtmlDetailsMessageBuilder();
                }
                else
                {
                    messageBuilder = new HtmlLinkMessageBuilder(false);
                }
            }
        }
        [ReflectorProperty("modifierNotificationTypes", Required = false)]
        public EmailGroup.NotificationType[] ModifierNotificationTypes
        {
            get { return modifierNotificationTypes; }
            set { modifierNotificationTypes = value; }
        }
        [ReflectorProperty("users")]
        public EmailUser[] EmailUsers
        {
            get
            {
                var values = new EmailUser[this.IndexedEmailUsers.Count];
                this.IndexedEmailUsers.Values.CopyTo(values, 0);
                return values;
            }
            set
            {
                this.IndexedEmailUsers.Clear();
                foreach (var user in value)
                {
                    this.IndexedEmailUsers.Add(user.Name, user);
                }
            }
        }
        public Dictionary<string, EmailUser> IndexedEmailUsers { get; private set; }
        [ReflectorProperty("groups")]
        public EmailGroup[] EmailGroups
        {
            get
            {
                var values = new EmailGroup[this.IndexedEmailGroups.Count];
                this.IndexedEmailGroups.Values.CopyTo(values, 0);
                return values;
            }
            set
            {
                this.IndexedEmailGroups.Clear();
                foreach (var group in value)
                {
                    this.IndexedEmailGroups.Add(group.Name, group);
                }
            }
        }
        public Dictionary<string, EmailGroup> IndexedEmailGroups { get; private set; }
        [ReflectorProperty("subjectSettings", Required = false)]
        public EmailSubject[] SubjectSettings
        {
            get { return subjectSettings; }
            set { subjectSettings = value; }
        }
        [ReflectorProperty("converters", Required = false)]
        public IEmailConverter[] Converters
        {
            get { return converters; }
            set { converters = value; }
        }
        [ReflectorProperty("subjectPrefix", Required = false)]
        public string SubjectPrefix
        {
            get { return subjectPrefix; }
            set { subjectPrefix = value; }
        }
        protected override bool Execute(IIntegrationResult result)
        {
            if (result.Status == IntegrationStatus.Unknown) return false;
            result.BuildProgressInformation.SignalStartRunTask(!string.IsNullOrEmpty(Description) ? Description : "Emailing ...");
            EmailMessage emailMessage = new EmailMessage(result, this);
            string to = emailMessage.Recipients;
            string subject = emailMessage.Subject;
            string message = CreateMessage(result);
            if (IsRecipientSpecified(to))
            {
                Log.Info(string.Format("Emailing \"{0}\" to {1}", subject, to));
                SendMessage(fromAddress, to, replytoAddress, subject, message, result.WorkingDirectory);
            }
            return true;
        }
        private static bool IsRecipientSpecified(string to)
        {
            return to != null && to.Trim() != string.Empty;
        }
        public virtual void SendMessage(string from_, string to, string replyto, string subject, string message, string workingFolder)
        {
            try
            {
                using (var actualMessage = GetMailMessage(from_, to, replyto, subject, message, workingFolder, Attachments))
                {
                    emailGateway.Send(actualMessage);
                }
            }
            catch (Exception e)
            {
                throw new CruiseControlException("EmailPublisher exception: " + e, e);
            }
        }
        protected static MailMessage GetMailMessage(string from_, string to, string replyto, string subject, string messageText, string workingFolder, string[] attachments)
        {
            MailMessage mailMessage = new MailMessage();
            mailMessage.To.Add(to);
            mailMessage.from_ = new MailAddress(from_);
            if (!String.IsNullOrEmpty(replyto)) mailMessage.ReplyTo = new MailAddress(replyto);
            mailMessage.Subject = subject;
            mailMessage.IsBodyHtml = true;
            mailMessage.Body = messageText;
            if (attachments != null)
            {
                foreach (var attachment in attachments)
                {
                    var fullPath = attachment;
                    if (!Path.IsPathRooted(fullPath)) fullPath = Path.Combine(workingFolder, fullPath);
                    if (File.Exists(fullPath))
                    {
                        var mailAttachment = new Attachment(fullPath);
                        mailMessage.Attachments.Add(mailAttachment);
                    }
                }
            }
            return mailMessage;
        }
        public string CreateMessage(IIntegrationResult result)
        {
            try
            {
                messageBuilder.xslFiles = this.XslFiles;
                return messageBuilder.BuildMessage(result);
            }
            catch (Exception e)
            {
                string message = "Unable to build email message: " + e;
                Log.Error(message);
                return message;
            }
        }
        public virtual void Validate(IConfiguration configuration, ConfigurationTrace parent, IConfigurationErrorProcesser errorProcesser)
        {
            var parentProject = parent.GetAncestorValue<Project>();
            if (parentProject != null)
            {
                var isPublisher = false;
                foreach (var task in parentProject.Publishers)
                {
                    if (task == this)
                    {
                        isPublisher = true;
                        break;
                    }
                }
                if (!isPublisher)
                {
                    errorProcesser.ProcessWarning("Email publishers are best placed in the publishers section of the configuration");
                }
            }
            else
            {
                errorProcesser.ProcessError(
                    new CruiseControlException("This publisher can only belong to a project"));
            }
        }
    }
}

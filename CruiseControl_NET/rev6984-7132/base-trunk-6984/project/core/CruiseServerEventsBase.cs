using System;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Events;
namespace ThoughtWorks.CruiseControl.Core
{
    public abstract class CruiseServerEventsBase
    {
        public event EventHandler<CancelProjectEventArgs> ProjectStarting;
        public event EventHandler<ProjectEventArgs> ProjectStarted;
        public event EventHandler<CancelProjectEventArgs> ProjectStopping;
        public event EventHandler<ProjectEventArgs> ProjectStopped;
        public event EventHandler<CancelProjectEventArgs<string> > ForceBuildReceived;
        public event EventHandler<ProjectEventArgs<string> > ForceBuildProcessed;
        public event EventHandler<CancelProjectEventArgs<string> > AbortBuildReceived;
        public event EventHandler<ProjectEventArgs<string> > AbortBuildProcessed;
        public event EventHandler<CancelProjectEventArgs<Message> > SendMessageReceived;
        public event EventHandler<ProjectEventArgs<Message> > SendMessageProcessed;
        public event EventHandler<IntegrationStartedEventArgs> IntegrationStarted;
        public event EventHandler<IntegrationCompletedEventArgs> IntegrationCompleted;
        protected virtual bool FireProjectStarting(string projectName)
        {
            bool isCanceled = false;
            if (ProjectStarting != null)
            {
                CancelProjectEventArgs args = new CancelProjectEventArgs(projectName);
                ProjectStarting(this, args);
                isCanceled = args.Cancel;
            }
            return isCanceled;
        }
        protected virtual void FireProjectStarted(string projectName)
        {
            if (ProjectStarted != null)
            {
                ProjectEventArgs args = new ProjectEventArgs(projectName);
                ProjectStarted(this, args);
            }
        }
        protected virtual bool FireProjectStopping(string projectName)
        {
            bool isCanceled = false;
            if (ProjectStopping != null)
            {
                CancelProjectEventArgs args = new CancelProjectEventArgs(projectName);
                ProjectStopping(this, args);
                isCanceled = args.Cancel;
            }
            return isCanceled;
        }
        protected virtual void FireProjectStopped(string projectName)
        {
            if (ProjectStopped != null)
            {
                ProjectEventArgs args = new ProjectEventArgs(projectName);
                ProjectStopped(this, args);
            }
        }
        protected virtual bool FireForceBuildReceived(string projectName, string enforcerName)
        {
            bool isCanceled = false;
            if (ForceBuildReceived != null)
            {
                CancelProjectEventArgs<string> args = new CancelProjectEventArgs<string>(projectName, enforcerName);
                ForceBuildReceived(this, args);
                isCanceled = args.Cancel;
            }
            return isCanceled;
        }
        protected virtual void FireForceBuildProcessed(string projectName, string enforcerName)
        {
            if (ForceBuildProcessed != null)
            {
                ProjectEventArgs<string> args = new ProjectEventArgs<string>(projectName, enforcerName);
                ForceBuildProcessed(this, args);
            }
        }
        protected virtual bool FireAbortBuildReceived(string projectName, string enforcerName)
        {
            bool isCanceled = false;
            if (AbortBuildReceived != null)
            {
                CancelProjectEventArgs<string> args = new CancelProjectEventArgs<string>(projectName, enforcerName);
                AbortBuildReceived(this, args);
                isCanceled = args.Cancel;
            }
            return isCanceled;
        }
        protected virtual void FireAbortBuildProcessed(string projectName, string enforcerName)
        {
            if (AbortBuildProcessed != null)
            {
                ProjectEventArgs<string> args = new ProjectEventArgs<string>(projectName, enforcerName);
                AbortBuildProcessed(this, args);
            }
        }
        protected virtual bool FireSendMessageReceived(string projectName, Message message)
        {
            bool isCanceled = false;
            if (SendMessageReceived != null)
            {
                CancelProjectEventArgs<Message> args = new CancelProjectEventArgs<Message>(projectName, message);
                SendMessageReceived(this, args);
                isCanceled = args.Cancel;
            }
            return isCanceled;
        }
        protected virtual void FireSendMessageProcessed(string projectName, Message message)
        {
            if (SendMessageProcessed != null)
            {
                ProjectEventArgs<Message> args = new ProjectEventArgs<Message>(projectName, message);
                SendMessageProcessed(this, args);
            }
        }
        protected virtual IntegrationStartedEventArgs.EventResult FireIntegrationStarted(IntegrationRequest request, string projectName)
        {
            IntegrationStartedEventArgs.EventResult result = IntegrationStartedEventArgs.EventResult.Continue;
            if (IntegrationStarted != null)
            {
                IntegrationStartedEventArgs args = new IntegrationStartedEventArgs(request, projectName);
                IntegrationStarted(this, args);
                result = args.Result;
            }
            return result;
        }
        protected virtual void FireIntegrationCompleted(IntegrationRequest request, string projectName, IntegrationStatus status)
        {
            if (IntegrationCompleted != null)
            {
                IntegrationCompletedEventArgs args = new IntegrationCompletedEventArgs(request, projectName, status);
                IntegrationCompleted(this, args);
            }
        }
    }
}

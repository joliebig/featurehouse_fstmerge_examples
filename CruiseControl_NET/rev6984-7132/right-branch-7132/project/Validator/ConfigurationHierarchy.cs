using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Threading;
using System.Windows.Forms;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Security;
using ThoughtWorks.CruiseControl.Core.Tasks;
using ThoughtWorks.CruiseControl.Core.Triggers;
namespace Validator
{
    public partial class ConfigurationHierarchy
        : UserControl
    {
        private Dictionary<string, TreeNode> queues = new Dictionary<string, TreeNode>();
        public ConfigurationHierarchy()
        {
            InitializeComponent();
        }
        public void Initialise(string fileName)
        {
            this.hierarchy.BeginUpdate();
            try
            {
                this.hierarchy.Nodes.Clear();
                var parent = new ConfigurationDetails(fileName);
                this.AddNode(this.hierarchy, "Configuration", "configuration", parent);
            }
            finally
            {
                this.hierarchy.EndUpdate();
            }
            this.queues.Clear();
            this.itemDetails.SelectedObject = null;
        }
        public void Finalise()
        {
            this.hierarchy.Nodes[0].Expand();
            foreach (var queue in this.queues.Values)
            {
                queue.Text += " (" + queue.Nodes.Count.ToString("#,##0", Thread.CurrentThread.CurrentUICulture) + ")";
            }
        }
        public void Add(object configurationItem)
        {
            if (configurationItem is IProject)
            {
                this.AddProject(configurationItem as IProject);
            }
            else if (configurationItem is IQueueConfiguration)
            {
                this.AddQueue(configurationItem as IQueueConfiguration);
            }
            else if (configurationItem is ISecurityManager)
            {
                this.AddSecurity(configurationItem as ISecurityManager);
            }
        }
        private TreeNode AddProject(IProject value)
        {
            this.hierarchy.BeginUpdate();
            try
            {
                TreeNode queueNode;
                var queueName = string.IsNullOrEmpty(value.QueueName) ? value.Name : value.QueueName;
                if (this.queues.ContainsKey(queueName))
                {
                    queueNode = this.queues[queueName];
                }
                else
                {
                    queueNode = this.AddQueue(new DefaultQueueConfiguration(queueName));
                }
                var projectNode = this.AddNode(queueNode, value.Name, "project", value);
                if (value is Project)
                {
                    var project = value as Project;
                    this.AddSourceControl(projectNode, project.SourceControl);
                    this.AddTriggers(projectNode, project.Triggers as MultipleTrigger);
                    this.AddTasks(projectNode, "Pre-build", project.PrebuildTasks);
                    this.AddTasks(projectNode, "Tasks", project.Tasks);
                    this.AddTasks(projectNode, "Publishers", project.Publishers);
                }
                return projectNode;
            }
            finally
            {
                this.hierarchy.EndUpdate();
            }
        }
        private void AddSourceControl(TreeNode parent, ISourceControl value)
        {
            if (value != null)
            {
                var node = this.AddNode(
                    parent,
                    "Source Control: " + this.GetReflectionName(value, "(Unknown)"),
                    "sourcecontrol",
                    value);
            }
        }
        private void AddTriggers(TreeNode parent, MultipleTrigger triggers)
        {
            if ((triggers != null) && (triggers.Triggers.Length > 0))
            {
                var triggersNode = this.AddNode(parent, "Triggers" + this.CountItems(triggers.Triggers), "triggers", null);
                foreach (var trigger in triggers.Triggers)
                {
                    var nodeName = this.GetReflectionName(trigger, "(Unknown trigger)");
                    var triggerNode = this.AddNode(
                        triggersNode,
                        nodeName,
                        "trigger",
                        trigger);
                    this.AddTriggers(triggerNode, trigger as MultipleTrigger);
                }
            }
        }
        private TreeNode AddTasks(TreeNode parent, string title, ITask[] tasks)
        {
            if ((tasks != null) && (tasks.Length > 0))
            {
                var node = this.AddNode(parent, title + this.CountItems(tasks), "tasks", null);
                foreach (var task in tasks)
                {
                    this.AddTask(node, task);
                }
                return node;
            }
            else
            {
                return null;
            }
        }
        private TreeNode AddTask(TreeNode parent, ITask value)
        {
            var nodeName = this.GetReflectionName(value, "(Unknown task)");
            var node = this.AddNode(parent, nodeName, "task", value);
            if (value is TaskContainerBase)
            {
                this.AddTasks(node, "Tasks", (value as TaskContainerBase).Tasks);
            }
            return node;
        }
        private TreeNode AddQueue(IQueueConfiguration value)
        {
            this.hierarchy.BeginUpdate();
            try
            {
                TreeNode queueNode;
                if (this.queues.ContainsKey(value.Name))
                {
                    queueNode = this.queues[value.Name];
                }
                else
                {
                    queueNode = this.AddNode(this.hierarchy.Nodes[0], value.Name, "queue", value);
                    this.queues.Add(value.Name, queueNode);
                }
                queueNode.Tag = value;
                return queueNode;
            }
            finally
            {
                this.hierarchy.EndUpdate();
            }
        }
        private TreeNode AddSecurity(ISecurityManager value)
        {
            this.hierarchy.BeginUpdate();
            try
            {
                var managerNode = this.AddNode(
                    this.hierarchy.Nodes[0],
                    "Security Manager: " + this.GetReflectionName(value, "(Unknown)"),
                    "security",
                    value);
                managerNode.Tag = value;
                if (value is InternalSecurityManager)
                {
                    var manager = value as InternalSecurityManager;
                    if (manager.Users.Length > 0)
                    {
                        var usersNode = this.AddNode(
                            managerNode,
                            "Users" + this.CountItems(manager.Users),
                            "users",
                            null);
                        foreach (var user in manager.Users)
                        {
                            this.AddNode(
                                usersNode,
                                this.GetReflectionName(user, string.Empty) + ": " + (string.IsNullOrEmpty(user.DisplayName) ? user.UserName : user.DisplayName),
                                "user",
                                user);
                        }
                        var permissionsNode = this.AddNode(
                            managerNode,
                            "Permissions" + this.CountItems(manager.Permissions),
                            "permissions",
                            null);
                        foreach (var permission in manager.Permissions)
                        {
                            this.AddPermission(permissionsNode, permission);
                        }
                    }
                }
                return managerNode;
            }
            finally
            {
                this.hierarchy.EndUpdate();
            }
        }
        private TreeNode AddPermission(TreeNode parent, IPermission permission)
        {
            var node = this.AddNode(
                parent,
                this.GetReflectionName(permission, string.Empty) + ": " + permission.Identifier,
                "permission",
                permission);
            return node;
        }
        private TreeNode AddNode(TreeView parent, string text, string imageKey, object item)
        {
            return this.AddNode(parent.Nodes, text, imageKey, item);
        }
        private TreeNode AddNode(TreeNode parent, string text, string imageKey, object item)
        {
            return this.AddNode(parent.Nodes, text, imageKey, item);
        }
        private TreeNode AddNode(TreeNodeCollection parent, string text, string imageKey, object item)
        {
            var index = this.localImages.Images.IndexOfKey(imageKey);
            var node = new TreeNode(text, index, index);
            node.Tag = item;
            parent.Add(node);
            return node;
        }
        private void hierarchy_AfterSelect(object sender, TreeViewEventArgs e)
        {
            if (e.Node.Tag == null)
            {
                this.itemDetails.SelectedObject = null;
            }
            else
            {
                this.itemDetails.SelectedObject = new ConfigurationTypeDescriptor(e.Node.Tag);
            }
        }
        private string GetReflectionName(object value, string defaultName)
        {
            var valueName = defaultName;
            var reflection = value.GetType().GetCustomAttributes(typeof(ReflectorTypeAttribute), true);
            if (reflection.Length > 0)
            {
                valueName = (reflection[0] as ReflectorTypeAttribute).Name;
            }
            return valueName;
        }
        private string CountItems(object[] items)
        {
            return " (" + items.Length.ToString("#,##0", Thread.CurrentThread.CurrentUICulture) + ")";
        }
        private class ConfigurationDetails
        {
            public ConfigurationDetails(string filename)
            {
                this.Filename = filename;
                var info = new FileInfo(filename);
                this.Size = info.Length;
                this.LastModified = info.LastWriteTime;
            }
            [Description("The location of source file that the configuration was loaded from_.")]
            [Category("File")]
            public string Filename { get; set; }
            [Description("The size (in bytes) of the source file.")]
            [Category("File")]
            public long Size { get; set; }
            [Description("The the date and tie the source file was last modified.")]
            [Category("File")]
            [DisplayName("Last Modified")]
            public DateTime LastModified { get; set; }
        }
    }
}

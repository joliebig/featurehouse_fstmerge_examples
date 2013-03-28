namespace ThoughtWorks.CruiseControl.UnitTests.CodeChecks
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Reflection;
    using Exortech.NetReflector;
    using NUnit.Framework;
    using ThoughtWorks.CruiseControl.Remote;
    using ThoughtWorks.CruiseControl.Core;
    using ThoughtWorks.CruiseControl.WebDashboard.Configuration;
    [TestFixture]
    public class ReflectorChecks
    {
        [Test]
        public void RemoteHasNoObsoleteAttributes()
        {
            this.CheckForObsolete(typeof(BuildCondition).Assembly);
        }
        [Test]
        public void CoreHasNoObsoleteAttributes()
        {
            this.CheckForObsolete(typeof(Project).Assembly);
        }
        [Test]
        public void WebDashboardHasNoObsoleteAttributes()
        {
            this.CheckForObsolete(typeof(DashboardConfigurationLoader).Assembly);
        }
        [Test]
        public void RemoteHasNoFieldsForReflection()
        {
            this.CheckForFieldAsReflectorProperty(typeof(BuildCondition).Assembly);
        }
        [Test]
        public void CoreHasNoFieldsForReflection()
        {
            this.CheckForFieldAsReflectorProperty(typeof(Project).Assembly);
        }
        [Test]
        public void WebDashboardHasNoFieldsForReflection()
        {
            this.CheckForFieldAsReflectorProperty(typeof(DashboardConfigurationLoader).Assembly);
        }
        private void CheckForObsolete(Assembly assembly)
        {
            var failedTypes = new List<string>();
            foreach (var type in assembly.GetTypes().OrderBy(t => t.FullName))
            {
                if (this.HasAttribute(typeof(ReflectorTypeAttribute), type))
                {
                    foreach (var property in type.GetProperties().OrderBy(p => p.Name))
                    {
                        if (this.HasAttribute(typeof(ReflectorArrayAttribute), property) ||
                            this.HasAttribute(typeof(ReflectorCollectionAttribute), property) ||
                            this.HasAttribute(typeof(ReflectorHashAttribute), property))
                        {
                            failedTypes.Add(type.FullName);
                            break;
                        }
                    }
                }
            }
            if (failedTypes.Count > 0)
            {
                var message = "The following " +
                    failedTypes.Count.ToString() +
                    " type(s) use an obsolete NetReflector attribute " +
                    "(ReflectorArray, ReflectorCollection or ReflectorHash)" +
                    Environment.NewLine +
                    "* " +
                    string.Join(Environment.NewLine + "* ", failedTypes.ToArray());
                Assert.Fail(message);
            }
        }
        private void CheckForFieldAsReflectorProperty(Assembly assembly)
        {
            var failedTypes = new List<string>();
            foreach (var type in assembly.GetTypes().OrderBy(t => t.FullName))
            {
                if (this.HasAttribute(typeof(ReflectorTypeAttribute), type))
                {
                    var fields = type.GetFields(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance).OrderBy(p => p.Name);
                    foreach (var field in fields)
                    {
                        if (this.HasAttribute(typeof(ReflectorArrayAttribute), field) ||
                            this.HasAttribute(typeof(ReflectorCollectionAttribute), field) ||
                            this.HasAttribute(typeof(ReflectorPropertyAttribute), field) ||
                            this.HasAttribute(typeof(ReflectorHashAttribute), field))
                        {
                            failedTypes.Add(type.FullName);
                            break;
                        }
                    }
                }
            }
            if (failedTypes.Count > 0)
            {
                var message = "The following " +
                    failedTypes.Count.ToString() +
                    " type(s) expose fields for reflection (should be properties)" +
                    Environment.NewLine +
                    "* " +
                    string.Join(Environment.NewLine + "* ", failedTypes.ToArray());
                Assert.Fail(message);
            }
        }
        private bool HasAttribute(Type attribute, Type type)
        {
            var hasAttrib = type.GetCustomAttributes(attribute, true).Length > 0;
            return hasAttrib;
        }
        private bool HasAttribute(Type attribute, PropertyInfo property)
        {
            var hasAttrib = property.GetCustomAttributes(attribute, true).Length > 0;
            return hasAttrib;
        }
        private bool HasAttribute(Type attribute, FieldInfo field)
        {
            var hasAttrib = field.GetCustomAttributes(attribute, true).Length > 0;
            return hasAttrib;
        }
    }
}

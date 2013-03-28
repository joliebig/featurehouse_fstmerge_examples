namespace ThoughtWorks.CruiseControl.Core.Config
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    public static class ConfigurationValidationUtils
    {
        public static IIntegrationResult GenerateResultForProject(Project project)
        {
            var result = new IntegrationResult()
            {
                ProjectName = project.Name,
                WorkingDirectory = project.WorkingDirectory,
                ArtifactDirectory = project.ArtifactDirectory
            };
            return result;
        }
    }
}

using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
 public class NUnitArgument
 {
  public string[] assemblies;
  private readonly string outputfile;
        public string[] IncludedCategories;
        public string[] ExcludedCategories;
  public NUnitArgument(string[] assemblies, string outputfile)
  {
   if (assemblies == null || assemblies.Length == 0)
    throw new CruiseControlException(
     "No unit test assemblies are specified. Please use the <assemblies> element to specify the test assemblies to run.");
   this.assemblies = assemblies;
   this.outputfile = outputfile;
  }
  public override string ToString()
  {
   ProcessArgumentBuilder argsBuilder = new ProcessArgumentBuilder();
   argsBuilder.AddArgument("/xml", "=", outputfile);
   argsBuilder.AddArgument("/nologo");
            AppendCategoriesArg(argsBuilder);
   foreach (string assemblyName in assemblies)
   {
    argsBuilder.AddArgument(assemblyName);
   }
   return argsBuilder.ToString();
  }
        private void AppendCategoriesArg(ProcessArgumentBuilder argsBuilder)
        {
            if (ExcludedCategories != null && ExcludedCategories.Length != 0)
            {
                string[] excludedCategories = System.Array.FindAll(ExcludedCategories, IsNotWhitespace);
                argsBuilder.AddArgument("/exclude", "=", string.Join(",", excludedCategories));
            }
            if (IncludedCategories != null && IncludedCategories.Length != 0)
            {
                string[] includedCategories = System.Array.FindAll(IncludedCategories, IsNotWhitespace);
                argsBuilder.AddArgument("/include", "=", string.Join(",", includedCategories));
            }
        }
        private static bool IsNotWhitespace(string input)
        {
            return !StringUtil.IsWhitespace(input);
        }
 }
}

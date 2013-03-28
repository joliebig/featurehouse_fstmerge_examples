using System;
using System.Text;
using System.Text.RegularExpressions;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Label
{
 [ReflectorType("iterationlabeller")]
 public class IterationLabeller
        : DefaultLabeller
 {
  private readonly DateTimeProvider dateTimeProvider;
  public const int InitialLabel = 1;
  private const int DaysInWeek = 7;
  public IterationLabeller() : this(new DateTimeProvider())
  {}
  public IterationLabeller(DateTimeProvider dateTimeProvider)
  {
   this.dateTimeProvider = dateTimeProvider;
  }
  [ReflectorProperty("duration", Required=false)]
  public int Duration = 2;
  [ReflectorProperty("releaseStartDate")]
  public DateTime ReleaseStartDate;
  [ReflectorProperty("separator", Required=false)]
  public string Separator = ".";
  public override string Generate(IIntegrationResult integrationResult)
  {
   IntegrationSummary lastIntegration = integrationResult.LastIntegration;
   if (lastIntegration.Label == null || lastIntegration.IsInitial())
   {
    return NewLabel(InitialLabel);
   }
   else if (lastIntegration.Status == IntegrationStatus.Success || IncrementOnFailed)
   {
    return NewLabel(IncrementLabel(lastIntegration.Label));
   }
   else
   {
    return lastIntegration.Label;
   }
  }
  private string NewLabel(int suffix)
  {
   StringBuilder buffer = new StringBuilder();
   buffer.Append(LabelPrefix);
   if (LabelPrefix != string.Empty && ! LabelPrefix.EndsWith(Separator)) buffer.Append(Separator);
   buffer.Append(CurrentIteration());
   buffer.Append(Separator);
   buffer.Append(suffix);
   return buffer.ToString();
  }
  private int IncrementLabel(string label)
  {
   string iterationPtn = @".*?((\d+)" + Separator.Replace(".", @"\.") + "(\\d+$)).*";
   string iterationLabel = Regex.Replace(label, iterationPtn, "$2");
   int numericIteration = int.Parse(iterationLabel);
   if (numericIteration < CurrentIteration())
   {
    return InitialLabel;
   }
   else
   {
    string numericLabel = Regex.Replace(label, @".*?(\d+$)", "$1");
    int newLabel = int.Parse(numericLabel);
    return newLabel + 1;
   }
  }
  private int CurrentIteration()
  {
   return GetIteration(ReleaseStartDate);
  }
  private int GetIteration(DateTime startDate)
  {
   return GetIteration(startDate, dateTimeProvider.Today);
  }
  private int GetIteration(DateTime startDate, DateTime endDate)
  {
   double daysFromStart = (endDate - startDate).TotalDays;
   return (int) daysFromStart/(Duration*DaysInWeek);
  }
 }
}

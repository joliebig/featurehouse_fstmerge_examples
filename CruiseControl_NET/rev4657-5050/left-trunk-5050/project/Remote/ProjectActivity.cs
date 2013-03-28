using System;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
 public class ProjectActivity
 {
  private readonly string type;
  public ProjectActivity(string type)
  {
   this.type = type;
  }
  public bool IsBuilding()
  {
   return type == Building.type;
  }
  public bool IsSleeping()
  {
   return type == Sleeping.type;
  }
  public bool IsPending()
  {
   return type == Pending.type;
  }
  public override bool Equals(object obj)
  {
   ProjectActivity other = obj as ProjectActivity;
   return other != null && other.ToString() == ToString();
  }
  public override int GetHashCode()
  {
   return base.GetHashCode();
  }
  public override string ToString()
  {
   return type;
  }
  public static bool operator == (ProjectActivity left, ProjectActivity right)
  {
   return Object.Equals(left, right);
  }
  public static bool operator != (ProjectActivity left, ProjectActivity right)
  {
   return !(left == right);
  }
  public static ProjectActivity CheckingModifications = new ProjectActivity("CheckingModifications");
  public static ProjectActivity Building = new ProjectActivity("Building");
  public static ProjectActivity Sleeping = new ProjectActivity("Sleeping");
  public static ProjectActivity Pending = new ProjectActivity("Pending");
 }
}

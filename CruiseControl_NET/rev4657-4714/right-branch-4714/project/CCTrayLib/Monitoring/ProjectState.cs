namespace ThoughtWorks.CruiseControl.CCTrayLib.Monitoring
{
 public class ProjectState
 {
  public static readonly ProjectState Success = new ProjectState("Success", 1, 0);
  public static readonly ProjectState Broken = new ProjectState("Broken", 2, 100);
  public static readonly ProjectState BrokenAndBuilding = new ProjectState("Broken and building", 4, 30);
  public static readonly ProjectState Building = new ProjectState("Building", 3, 20);
  public static readonly ProjectState NotConnected = new ProjectState("Not Connected", 0, 10);
  public readonly string Name;
  public readonly int ImageIndex;
  private readonly int importance;
  private ProjectState(string name, int imageIndex, int importance)
  {
   Name = name;
   ImageIndex = imageIndex;
   this.importance = importance;
  }
  public bool IsMoreImportantThan(ProjectState state)
  {
   return importance > state.importance;
  }
  public override string ToString()
  {
   return Name;
  }
 }
}

namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    public class MergeFileInfo
    {
        public string FileName { get; set; }
        public MergeActionType MergeAction { get; set; }
        public enum MergeActionType
        {
            Merge,
            Copy,
            CData,
        }
    }
}

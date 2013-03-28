namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using Exortech.NetReflector;
    [ReflectorType("fileToMerge")]
    public class MergeFileInfo
    {
        [ReflectorProperty("path")]
        public string FileName { get; set; }
        [ReflectorProperty("action", Required = false)]
        public MergeActionType MergeAction { get; set; }
        [ReflectorProperty("deleteAfterMerge", Required = false)]
        public bool DeleteAfterMerge { get; set; }
        public enum MergeActionType
        {
            Merge,
            Copy,
            CData,
        }
    }
}

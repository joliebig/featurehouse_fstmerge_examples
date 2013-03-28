using System; 
using System.Xml; namespace  DiffPatchResources {
	
 public interface  IDiffPatchTask {
		
  void Init(XmlDocument fromSource, bool verbose); 
  int NodesAppended { get; } 
  int NodesChanged { get; } 
  int NodesRemoved { get; } 
  Exception PatchTarget(XmlDocument docToPatch);
	}
	
 public enum  CompatibleCLRVersion  {
  Version_1_0,
  Version_1_1,
  Version_2_0,
 } 
 public interface  IConversionTask {
		
  void Init(bool verbose); 
  int NodesAppended { get; } 
  int NodesChanged { get; } 
  int NodesRemoved { get; } 
  Exception ConvertTarget(XmlDocument docToConvert, CompatibleCLRVersion toVersion);
	}

}

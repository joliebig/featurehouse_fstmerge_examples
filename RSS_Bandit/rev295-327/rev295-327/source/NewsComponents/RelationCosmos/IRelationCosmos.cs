using System; 
using System.Collections.Generic; 
using NewsComponents.Collections; namespace  NewsComponents.RelationCosmos {
	
 internal sealed class  RelationCosmosFactory {
		
  public static  IRelationCosmos Create() {
            return new RelationCosmos4();
  }
 
  private  RelationCosmosFactory() {}

	}
	
 internal sealed class  RelationCosmos {
		
  public static  TimeSpan DefaultRelationTimeCorrection { get { return defaultRelationTimeCorrection; } }
 
  public static  DateTime UnknownPointInTime { get { return DateTime.MinValue; } }
 
  public static  StringTable UrlTable{ get { return urlTable; } }
 
  private readonly static  TimeSpan defaultRelationTimeCorrection = new TimeSpan(100);
 
  private readonly static  StringTable urlTable = new StringTable();
 
  private  RelationCosmos(){}

	}
	
 public interface  IRelationCosmos {
		
  void Add<T>(T relation) where T : RelationBase<T>; 
        void AddRange<T>(IEnumerable<T> relations) where T : RelationBase<T>; 
  void Remove<T>(T relation) where T : RelationBase<T>; 
        void RemoveRange<T>(IEnumerable<T> relations) where T : RelationBase<T>; 
  void Clear(); 
        IList<T> GetIncoming<T>(T relation, IList<T> excludeRelations) where T : RelationBase<T>; 
        IList<T> GetIncoming<T>(string hRef, DateTime since) where T : RelationBase<T>; 
        IList<T> GetOutgoing<T>(T relation, IList<T> excludeRelations) where T : RelationBase<T>; 
        IList<T> GetIncomingAndOutgoing<T>(T relation, IList<T> excludeRelations) where T : RelationBase<T>; 
        bool HasIncomingOrOutgoing<T>(T relation, IList<T> excludeRelations) where T : RelationBase<T>; 
  bool DeepCosmos { get; set; } 
  bool AdjustPointInTime { get; set; }
	}

}

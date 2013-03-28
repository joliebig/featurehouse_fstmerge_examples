using System;
using System.Collections.Generic;
namespace NewsComponents.RelationCosmos
{
 public abstract class RelationBase <T>: IRelation, IComparable
        where T : RelationBase<T>
    {
        protected static List<string> EmptyList = new List<string>(0);
  protected RelationBase() {
   hReference = String.Empty;
            outgoingRelationships = EmptyList;
   aPointInTime = RelationCosmos.UnknownPointInTime;
   pointInTimeIsAdjustable = true;
   externalRelations = null;
  }
     public string HRef
     {
          get { return hReference; }
             protected set
             {
                 hReference = value;
             }
     }
  private string hReference;
  public virtual string Id {
   get { return p_id; }
   set { p_id = value; }
  }
  protected string p_id;
        public IList<string> OutgoingRelations { get { return outgoingRelationships; } }
  protected internal List<string> outgoingRelationships;
  public virtual DateTime PointInTime {
   get { return this.aPointInTime; }
   set {
    this.aPointInTime = value;
    pointInTimeIsAdjustable = (this.aPointInTime == RelationCosmos.UnknownPointInTime);
   }
  }
  internal void SetInternalPointInTime (DateTime newPointInTime){
   this.aPointInTime = newPointInTime;
  }
  protected DateTime aPointInTime;
  internal virtual bool PointInTimeIsAdjustable {
   get {return pointInTimeIsAdjustable; }
  }
  protected bool pointInTimeIsAdjustable;
  public virtual bool HasExternalRelations { get { return false; } }
  public virtual IList<T> GetExternalRelations() {
            if (externalRelations == null)
                return new List<T>();
   return externalRelations;
  }
  public virtual void SetExternalRelations(IList<T> relations) {
   externalRelations = relations;
  }
  protected IList<T> externalRelations = null;
  public int CompareTo(object obj)
        {
            return CompareTo(obj as RelationBase<T>);
  }
        public int CompareTo(RelationBase<T> other)
        {
            if (ReferenceEquals(this, other))
                return 0;
            if (ReferenceEquals(other, null))
                return 1;
            return this.aPointInTime.CompareTo(other.aPointInTime);
        }
        public int CompareTo(IRelation other)
        {
            return CompareTo(other as RelationBase<T>);
        }
 }
 public class RelationProxy<T> : RelationBase<T>
        where T : RelationBase<T>
    {
  protected RelationProxy() {
   realObject = null;
  }
  public RelationProxy(string href, DateTime pointInTime):
   this(href, null, pointInTime) {}
  public RelationProxy(string href, object realObject, DateTime pointInTime):this() {
   this.realObject = realObject;
            HRef = href;
   base.PointInTime = pointInTime;
  }
  public RelationProxy(string href, DateTime pointInTime, bool adjustablePointInTime):
   this(href, null, pointInTime, adjustablePointInTime) {}
  public RelationProxy(string href, object realObject, DateTime pointInTime, bool adjustablePointInTime):this() {
   this.realObject = realObject;
            HRef = href;
   SetInternalPointInTime(pointInTime);
   pointInTimeIsAdjustable = adjustablePointInTime;
  }
  public object RealObject {
   get { return this.realObject; }
   set { this.realObject = value;}
  }
  private object realObject;
 }
}

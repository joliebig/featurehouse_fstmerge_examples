using System;
using System.Collections;
using RelationCosmos.Collections;
namespace RelationCosmos
{
 public abstract class RelationBase: IComparable {
  protected RelationBase() {
   href = String.Empty;
   outgoingRelations = new RelationHRefList(5);
   pointInTime = RelationCosmos.UnknownPointInTime;
   pointInTimeIsAdjustable = true;
   externalRelations = null;
  }
  public virtual string HRef { get { return href; } }
  protected string href;
  public virtual RelationHRefList OutgoingRelations { get { return outgoingRelations;} }
  protected RelationHRefList outgoingRelations;
  public virtual DateTime PointInTime {
   get { return this.pointInTime; }
   set {
    this.pointInTime = value;
    pointInTimeIsAdjustable = (this.pointInTime == RelationCosmos.UnknownPointInTime);
   }
  }
  internal virtual void SetInternalPointInTime (DateTime newPointInTime){
   this.pointInTime = newPointInTime;
  }
  protected DateTime pointInTime;
  internal virtual bool PointInTimeIsAdjustable {
   get {return pointInTimeIsAdjustable; }
  }
  protected bool pointInTimeIsAdjustable;
  public virtual bool HasExternalRelations { get { return false; } }
  public virtual RelationList GetExternalRelations() {
   if (externalRelations == null)
    return RelationCosmos.EmptyRelationList;
   return externalRelations;
  }
  public virtual void SetExternalRelations(RelationList relations) {
   externalRelations = relations;
  }
  protected RelationList externalRelations = null;
  public int CompareTo(object obj) {
   if (Object.ReferenceEquals(this, obj))
    return 0;
   RelationBase r = obj as RelationBase;
   if (r == null)
    return 1;
   return this.pointInTime.CompareTo(r.pointInTime);
  }
 }
 public class RelationProxy: RelationBase {
  protected RelationProxy():base() {
   realObject = null;
  }
  public RelationProxy(string href, DateTime pointInTime):
   this(href, null, pointInTime) {}
  public RelationProxy(string href, object realObject, DateTime pointInTime):this() {
   this.realObject = realObject;
   base.href = href;
   base.PointInTime = pointInTime;
  }
  public RelationProxy(string href, DateTime pointInTime, bool adjustablePointInTime):
   this(href, null, pointInTime, adjustablePointInTime) {}
  public RelationProxy(string href, object realObject, DateTime pointInTime, bool adjustablePointInTime):this() {
   this.realObject = realObject;
   base.href = href;
   base.SetInternalPointInTime(pointInTime);
   base.pointInTimeIsAdjustable = adjustablePointInTime;
  }
  public object RealObject {
   get { return this.realObject; }
   set { this.realObject = value;}
  }
  private object realObject;
 }
}

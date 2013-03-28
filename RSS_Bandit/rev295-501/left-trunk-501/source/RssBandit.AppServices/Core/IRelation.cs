using System;
using System.Collections.Generic;
namespace NewsComponents.RelationCosmos
{
    public interface IRelation : IComparable<IRelation>
    {
        string HRef { get; }
        string Id { get; set; }
        IList<string> OutgoingRelations { get; }
        DateTime PointInTime { get; set; }
        bool HasExternalRelations { get; }
        IList<IRelation> GetExternalRelations();
        void SetExternalRelations<T>(IList<T> relations) where T: IRelation;
    }
}

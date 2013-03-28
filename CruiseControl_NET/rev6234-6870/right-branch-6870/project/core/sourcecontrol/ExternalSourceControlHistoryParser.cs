using System;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using System.Xml.Serialization;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class ExternalSourceControlHistoryParser : IHistoryParser
 {
        public ExternalSourceControlHistoryParser()
  {
        }
  public Modification[] Parse(TextReader history, DateTime from_, DateTime to)
        {
         XmlSerializer serializer = new XmlSerializer(typeof (Modification[]));
   Modification[] mods = new Modification[0];
         try
         {
          if (history.Peek() == -1)
     return mods;
          mods = (Modification[]) serializer.Deserialize(history);
         }
         catch (InvalidOperationException e)
         {
    Log.Error(e);
          if (e.InnerException is XmlException)
     return mods;
          throw;
         }
            var results = new List<Modification>();
         foreach (Modification mod in mods)
         {
          if ((mod.ModifiedTime >= from_) & (mod.ModifiedTime <= to))
           results.Add(mod);
         }
         return results.ToArray();
        }
    }
}

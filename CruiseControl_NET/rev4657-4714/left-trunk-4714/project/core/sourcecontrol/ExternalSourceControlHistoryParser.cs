using System;
using System.Collections;
using System.IO;
using System.Xml;
using System.Xml.Serialization;
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
            Modification[] mods;
            try
            {
                mods = (Modification[]) serializer.Deserialize(history);
            }
            catch (InvalidOperationException e)
            {
                if ((e.InnerException is XmlException) && (e.InnerException.Message == "Root element is missing."))
                {
                    return new Modification[0];
                }
                else
                    throw;
            }
            ArrayList results = new ArrayList();
            foreach (Modification mod in mods)
            {
                if ((mod.ModifiedTime >= from_) & (mod.ModifiedTime <= to))
                    results.Add(mod);
            }
            return (Modification[])results.ToArray(typeof(Modification));
        }
    }
}

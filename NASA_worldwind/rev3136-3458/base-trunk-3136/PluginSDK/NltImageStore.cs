using WorldWind.Renderable;
using System;
using System.Globalization;
namespace WorldWind
{
 public class NltImageStore : ImageStore
 {
  string m_dataSetName;
  string m_serverUri;
  public override bool IsDownloadableLayer
  {
   get
   {
    return true;
   }
  }
  public NltImageStore(
   string dataSetName,
   string serverUri)
  {
   m_serverUri = serverUri;
   m_dataSetName = dataSetName;
  }
  public override string GetDownloadUrl(QuadTile qt)
  {
   return string.Format(CultureInfo.InvariantCulture,
    "{0}?T={1}&L={2}&X={3}&Y={4}", m_serverUri,
    m_dataSetName, qt.Level, qt.Col, qt.Row);
  }
 }
}

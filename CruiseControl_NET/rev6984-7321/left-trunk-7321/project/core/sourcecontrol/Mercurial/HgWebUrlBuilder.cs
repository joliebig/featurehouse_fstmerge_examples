namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Mercurial
{
    using Exortech.NetReflector;
    [ReflectorType("hgweb")]
    public class HgWebUrlBuilder : IModificationUrlBuilder
    {
        [ReflectorProperty("url")]
        public string Url { get; set; }
        public void SetupModification(Modification[] modifications)
        {
            foreach (Modification modification in modifications)
            {
                modification.Url = Url + "rev/" + modification.Version;
            }
        }
    }
}

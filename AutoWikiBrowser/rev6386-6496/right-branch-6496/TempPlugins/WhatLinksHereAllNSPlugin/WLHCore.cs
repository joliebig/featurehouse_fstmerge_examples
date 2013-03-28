using System.Collections.Generic;
using System.Text;
using WikiFunctions.Lists;
using WikiFunctions.Plugin;
namespace WikiFunctions.Plugins.ListMaker.WhatLinksHereAllNSPlugin
{
    public class WhatLinksHereAllNSPlugin : WhatLinksHereAllNSListProvider, IListMakerPlugin
    {
        public override List<Article> MakeList(string[] searchCriteria)
        {
            StringBuilder builder = new StringBuilder();
            foreach (int id in Variables.Namespaces.Keys)
            {
                if (id > 0)
                    builder.Append(id + "|");
            }
            return MakeList(builder.ToString().Substring(0, builder.Length - 1), searchCriteria);
        }
        public string Name
        { get { return "What Links Here All NS Plugin"; } }
        public override string DisplayText
        { get { return "What links here (p)(all NS)"; } }
    }
}

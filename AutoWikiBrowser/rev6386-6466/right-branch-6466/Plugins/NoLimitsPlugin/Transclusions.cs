using System.Collections.Generic;
using WikiFunctions.Lists.Providers;
using WikiFunctions.Plugin;
namespace WikiFunctions.Plugins.ListMaker.NoLimitsPlugin
{
    public class WhatTranscludesPageNoLimitsForAdminAndBotsPlugin : WhatTranscludesPageListProvider, IListMakerPlugin
    {
        public WhatTranscludesPageNoLimitsForAdminAndBotsPlugin()
        {
            Limit = 1000000;
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            if (Base.AWB.TheSession.User.IsBot || Base.AWB.TheSession.User.IsSysop)
                return base.MakeList(searchCriteria);
            Tools.MessageBox("Action only allowed for Admins and Bot accounts");
            return null;
        }
        public override string DisplayText
        { get { return base.DisplayText + " (NL, Admin & Bot)"; } }
        public string Name
        { get { return "WhatTranscludesPageNoLimitsForAdminAndBotsPlugin"; } }
    }
    public class WhatTranscludesPageAllNSNoLimitsForAdminAndBotsPagePlugin : WhatTranscludesPageAllNSListProvider, IListMakerPlugin
    {
        public WhatTranscludesPageAllNSNoLimitsForAdminAndBotsPagePlugin()
        {
            Limit = 1000000;
        }
        public override List<Article> MakeList(params string[] searchCriteria)
        {
            if (Base.AWB.TheSession.User.IsBot || Base.AWB.TheSession.User.IsSysop)
                return base.MakeList(searchCriteria);
            Tools.MessageBox("Action only allowed for Admins and Bot accounts");
            return null;
        }
        public override string DisplayText
        { get { return base.DisplayText + " (all NS) (NL, Admin & Bot)"; } }
        public string Name
        { get { return "WhatTranscludesPageAllNSNoLimitsForAdminAndBotsPagePlugin"; } }
    }
}

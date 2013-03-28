using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;
using Microsoft.Feeds.Interop;
using NewsComponents.Net;
using NewsComponents.Search;
namespace NewsComponents.Feed {
    class WindowsRssNewsHandler : NewsHandler
    {
        public WindowsRssNewsHandler(INewsComponentsConfiguration configuration)
        {
            this.p_configuration = configuration;
            if (this.p_configuration == null)
                this.p_configuration = NewsHandler.DefaultConfiguration;
            ValidateAndThrow(this.Configuration);
        }
        public override void LoadFeedlist()
        {
        }
    }
}

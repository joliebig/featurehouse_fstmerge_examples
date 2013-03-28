using System.Collections.Generic;
namespace WikiFunctions.Lists.Providers
{
    public interface IListProvider
    {
        List<Article> MakeList(params string[] searchCriteria);
        string DisplayText { get; }
        string UserInputTextBoxText { get; }
        bool UserInputTextBoxEnabled { get; }
        void Selected();
        bool RunOnSeparateThread { get; }
    }
    interface ISpecialPageProvider : IListProvider
    {
        List<Article> MakeList(int @namespace, params string[] searchCriteria);
        bool PagesNeeded { get; }
        bool NamespacesEnabled { get; }
    }
}

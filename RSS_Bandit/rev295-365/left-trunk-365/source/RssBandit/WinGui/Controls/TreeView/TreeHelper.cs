using System;
using System.Collections;
using System.Collections.Specialized;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Text;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Windows.Forms;
using System.Xml.Serialization;
using Infragistics.Win;
using Infragistics.Win.UltraWinTree;
using NewsComponents;
using NewsComponents.Feed;
using NewsComponents.Utils;
using RssBandit.Common.Logging;
using RssBandit.WinGui.Interfaces;
using RssBandit.WinGui.Utility;
using RssBandit.Xml;
namespace RssBandit.WinGui.Controls {
 internal class TreeHelper {
  public static TreeFeedsNodeBase CreateCategoryHive(TreeFeedsNodeBase startNode, string category, ContextMenu contextMenu) {
   return CreateCategoryHive(startNode, category, contextMenu, FeedNodeType.Category);
  }
  public static TreeFeedsNodeBase CreateCategoryHive(TreeFeedsNodeBase startNode, string category, ContextMenu contextMenu, FeedNodeType categoryNodeType) {
   if (category == null || category.Length == 0 || startNode == null) return startNode;
   string[] catHives = category.Split(FeedSource.CategorySeparator.ToCharArray());
   TreeFeedsNodeBase n = null;
   bool wasNew = false;
   int nodeImageIndex, expandedNodeImageIndex;
   switch (categoryNodeType) {
    case FeedNodeType.FinderCategory:
     nodeImageIndex = Resource.SubscriptionTreeImage.FinderCategory;
     expandedNodeImageIndex = Resource.SubscriptionTreeImage.FinderCategoryExpanded;
     break;
    default:
     nodeImageIndex = Resource.SubscriptionTreeImage.SubscriptionsCategory;
     expandedNodeImageIndex = Resource.SubscriptionTreeImage.SubscriptionsCategoryExpanded;
     break;
   }
   foreach (string catHive in catHives){
    if (!wasNew)
     n = FindChildNode(startNode, catHive, categoryNodeType);
    else
     n = null;
    if (n == null) {
     switch (categoryNodeType) {
      case FeedNodeType.FinderCategory:
       n = new FinderCategoryNode(catHive, nodeImageIndex, expandedNodeImageIndex, contextMenu);
       break;
      default:
       n = new CategoryNode(catHive, nodeImageIndex, expandedNodeImageIndex, contextMenu);
       break;
     }
     startNode.Nodes.Add(n);
     wasNew = true;
    }
    startNode = n;
   }
   return startNode;
  }
  public static TreeFeedsNodeBase FindChildNode(TreeFeedsNodeBase n, string text, FeedNodeType nType) {
   if (n == null || text == null) return null;
   text = text.Trim();
   for (TreeFeedsNodeBase t = n.FirstNode; t != null; t = t.NextNode) {
    if (t.Type == nType && String.Compare(t.Text, text, false, CultureInfo.CurrentUICulture) == 0)
     return t;
   }
   return null;
  }
        public static TreeFeedsNodeBase FindCategoryNode(TreeFeedsNodeBase startNode, string category)
        {
            if (category == null || category.Length == 0 || startNode == null) return startNode;
            string[] catHives = category.Split(FeedSource.CategorySeparator.ToCharArray());
            TreeFeedsNodeBase n = null;
            bool wasNew = false;
            foreach (string catHive in catHives)
            {
                if (startNode != null)
                {
                    startNode = FindChildNode(startNode, catHive, FeedNodeType.Category);
                }else{
                    startNode = null;
                    break;
                }
            }
            return startNode;
        }
  public static bool IsChildNode(TreeFeedsNodeBase parent, TreeFeedsNodeBase nodeToTest) {
   if (parent != null && nodeToTest != null) {
    return parent.IsAncestorOf(nodeToTest);
   }
   return false;
  }
  public static TreeFeedsNodeBase FindNode(TreeFeedsNodeBase startNode, INewsItem item) {
   return FindNode(startNode, item.Feed);
  }
  public static TreeFeedsNodeBase FindNode(TreeFeedsNodeBase startNode, INewsFeed f) {
   TreeFeedsNodeBase assocFeedsNode = f.Tag as TreeFeedsNodeBase;
   if (assocFeedsNode != null)
    return assocFeedsNode;
   return FindNode(startNode, f.link);
  }
  public static TreeFeedsNodeBase FindNode(TreeFeedsNodeBase startNode, string feedUrl) {
   if (feedUrl == null || feedUrl.Trim().Length == 0)
    return null;
   TreeFeedsNodeBase ownernode = null;
   if (startNode != null) {
    if( feedUrl.Equals(startNode.DataKey) ) {
     return startNode;
    }
    foreach(TreeFeedsNodeBase t in startNode.Nodes) {
     if( feedUrl.Equals(t.DataKey) &&
      (t.Type != FeedNodeType.Root && t.Type != FeedNodeType.Category) ) {
      ownernode = t;
      break;
     }
     if (t.Nodes.Count > 0) {
      ownernode = FindNode(t, feedUrl);
      if (ownernode != null)
       break;
     }
    }
   }
   return ownernode;
  }
  internal static TreeFeedsNodeBase GetNewNodeToActivate(TreeFeedsNodeBase current) {
   if (current == null)
    return null;
   UltraTreeNode newActive = null, n = current, sibling = null;
   while (null != (sibling = n.GetSibling(NodePosition.Next))) {
    if (sibling.Visible) {
     newActive = sibling; break;
    } else {
     n = sibling;
    }
   }
   if (newActive == null) {
    n = current;
    while (null != (sibling = n.GetSibling(NodePosition.Previous))) {
     if (sibling.Visible) {
      newActive = sibling; break;
     } else {
      n = sibling;
     }
    }
   }
   if (newActive == null)
    newActive = current.Parent;
   if (newActive == null)
    newActive = current.NextVisibleNode;
   return (TreeFeedsNodeBase)newActive;
  }
  public static DefaultableBoolean ConvertToDefaultableBoolean (bool value) {
   if (value)
    return DefaultableBoolean.True;
   else
    return DefaultableBoolean.False;
  }
  public static SortType ConvertToSortType(System.Windows.Forms.SortOrder sortOrder) {
   switch (sortOrder) {
    case System.Windows.Forms.SortOrder.None: return SortType.None;
    case System.Windows.Forms.SortOrder.Ascending: return SortType.Ascending;
    case System.Windows.Forms.SortOrder.Descending: return SortType.Descending;
    default: return SortType.Default;
   }
  }
  public static void CopyNodes(TreeFeedsNodeBase[] nodes, TreeView destinationTree, bool isChecked) {
   if (nodes == null) return;
   if (destinationTree == null) return;
   destinationTree.BeginUpdate();
   destinationTree.Nodes.Clear();
   foreach (TreeFeedsNodeBase n in nodes) {
    int imgIdx = Resource.SubscriptionTreeImage.Feed,
        selImgIdx = Resource.SubscriptionTreeImage.FeedSelected;
    if (destinationTree.ImageList != null)
    {
     if (destinationTree.ImageList.Images.Count > n.ImageIndex && n.ImageIndex != 0)
      imgIdx = n.ImageIndex;
     else if (RssHelper.IsNntpUrl(n.DataKey))
      imgIdx = Resource.SubscriptionTreeImage.Nntp;
     if (destinationTree.ImageList.Images.Count > n.SelectedImageIndex && n.SelectedImageIndex != 0)
      selImgIdx = n.SelectedImageIndex;
     else if (RssHelper.IsNntpUrl(n.DataKey))
      selImgIdx = Resource.SubscriptionTreeImage.NntpSelected;
    }
    TreeNode tn = new TreeNode(n.Text, imgIdx, selImgIdx);
    int i = destinationTree.Nodes.Add(tn);
    destinationTree.Nodes[i].Tag = n.DataKey;
    destinationTree.Nodes[i].Checked = isChecked;
    if (n.Nodes.Count > 0)
     CopyNodes(n.Nodes, destinationTree.Nodes[i], isChecked);
   }
   destinationTree.EndUpdate();
  }
  public static void CopyNodes(TreeFeedsNodeBase node, TreeView destinationTree, bool isChecked) {
   if (node == null) return;
   if (destinationTree == null) return;
   destinationTree.BeginUpdate();
   destinationTree.Nodes.Clear();
   int imgIdx = Resource.SubscriptionTreeImage.Feed,
    selImgIdx = Resource.SubscriptionTreeImage.FeedSelected;
   if (destinationTree.ImageList != null) {
    if (destinationTree.ImageList.Images.Count > node.ImageIndex)
     imgIdx = node.ImageIndex;
    else if (RssHelper.IsNntpUrl(node.DataKey))
     imgIdx = Resource.SubscriptionTreeImage.Nntp;
    if (destinationTree.ImageList.Images.Count > node.SelectedImageIndex)
     selImgIdx = node.SelectedImageIndex;
    else if (RssHelper.IsNntpUrl(node.DataKey))
     selImgIdx = Resource.SubscriptionTreeImage.NntpSelected;
   }
   TreeNode tn = new TreeNode(node.Text, imgIdx, selImgIdx);
   int i = destinationTree.Nodes.Add(tn);
   destinationTree.Nodes[i].Tag = node.DataKey;
   destinationTree.Nodes[i].Checked = isChecked;
   if (node.Nodes.Count > 0)
    CopyNodes(node.Nodes, destinationTree.Nodes[i], isChecked);
   destinationTree.EndUpdate();
  }
  private static void CopyNodes(TreeNodesCollection nodes, TreeNode parent, bool isChecked) {
   TreeView destinationTree = parent.TreeView;
   foreach (TreeFeedsNodeBase n in nodes) {
    int imgIdx = Resource.SubscriptionTreeImage.Feed,
     selImgIdx = Resource.SubscriptionTreeImage.FeedSelected;
    if (destinationTree.ImageList != null) {
     if (destinationTree.ImageList.Images.Count > n.ImageIndex && n.ImageIndex != 0)
      imgIdx = n.ImageIndex;
     else if (RssHelper.IsNntpUrl(n.DataKey))
      imgIdx = Resource.SubscriptionTreeImage.Nntp;
     if (destinationTree.ImageList.Images.Count > n.SelectedImageIndex && n.SelectedImageIndex != 0)
      selImgIdx = n.SelectedImageIndex;
     else if (RssHelper.IsNntpUrl(n.DataKey))
      selImgIdx = Resource.SubscriptionTreeImage.NntpSelected;
    }
    TreeNode tn = new TreeNode(n.Text, imgIdx, selImgIdx);
    int i = parent.Nodes.Add(tn);
    parent.Nodes[i].Tag = n.DataKey;
    parent.Nodes[i].Checked = isChecked;
    if (n.Nodes.Count > 0)
     CopyNodes(n.Nodes, parent.Nodes[i], isChecked);
   }
  }
  public static void GetCheckedNodes(TreeNode startNode, ArrayList folders, ArrayList leaveNodes) {
   if (startNode == null)
    return;
   if (startNode.Checked) {
    if (startNode.Tag == null) {
     folders.Add(startNode);
     return;
    } else
     leaveNodes.Add(startNode);
   }
   foreach (TreeNode n in startNode.Nodes) {
    GetCheckedNodes(n, folders, leaveNodes);
   }
  }
  public static void GetCheckedNodes(TreeNode startNode, ArrayList checkedNodes) {
   if (startNode == null)
    return;
   if (startNode.Checked) {
    if (startNode.Tag != null)
     checkedNodes.Add(startNode);
   }
   if (startNode.Nodes.Count > 0) {
    foreach (TreeNode n in startNode.Nodes) {
     GetCheckedNodes(n, checkedNodes);
    }
   }
  }
  public static void SetCheckedNodes(TreeNode startNode, ArrayList folders, ArrayList leaveNodeTags) {
   if (startNode == null)
    return;
   if (startNode.Tag == null) {
    string catName = BuildCategoryStoreName(startNode);
    if (catName != null && folders != null) {
     foreach (string folder in folders) {
      if (catName.Equals(folder)) {
       startNode.Checked = true;
       PerformOnCheckStateChanged(startNode);
       break;
      }
     }
    }
   } else {
    if (leaveNodeTags != null) {
     foreach (string tagString in leaveNodeTags) {
      if (tagString != null && tagString.Equals(startNode.Tag)) {
       startNode.Checked = true;
       PerformOnCheckStateChanged(startNode);
       break;
      }
     }
    }
   }
   foreach (TreeNode n in startNode.Nodes) {
    SetCheckedNodes(n, folders, leaveNodeTags);
   }
  }
  public static string BuildCategoryStoreName(TreeNode theNode) {
   if (theNode != null)
    return BuildCategoryStoreName(theNode.FullPath);
   return null;
  }
  public static string BuildCategoryStoreName(string fullPathName) {
   if (string.IsNullOrEmpty(fullPathName))
    return null;
   string s = fullPathName.Trim();
   string[] a = s.Split(FeedSource.CategorySeparator.ToCharArray());
   if (a.GetLength(0) > 1)
    return String.Join(FeedSource.CategorySeparator, a, 1, a.GetLength(0)-1);
   return null;
  }
  public static string[] BuildCategoryStoreNameArray(string fullPathName, bool ignoreLeaveNode) {
   if (string.IsNullOrEmpty(fullPathName))
    return new string[]{};
   string s = fullPathName.Trim();
   ArrayList a = new ArrayList(s.Split(FeedSource.CategorySeparator.ToCharArray()));
   if (ignoreLeaveNode)
   {
    if (a.Count > 2)
    {
     string[] ret = new string[a.Count-2];
     a.CopyTo(1, ret, 0, a.Count-2);
     return ret;
    }
   } else {
    if (a.Count > 1)
    {
     string[] ret = new string[a.Count-1];
     a.CopyTo(1, ret, 0, a.Count-1);
     return ret;
    }
   }
   return new string[]{};
  }
  public static void PerformOnCheckStateChanged(TreeNode node) {
   if (node != null) {
    if (node.Nodes.Count > 0) {
     TreeHelper.CheckChildNodes(node, node.Checked);
    }
    TreeHelper.CheckParentNodes(node, node.Checked);
   }
  }
  public static void CheckChildNodes(TreeNode parent, bool isChecked) {
   foreach (TreeNode child in parent.Nodes) {
    child.Checked = isChecked;
    if (child.Nodes.Count > 0)
     CheckChildNodes(child, isChecked);
   }
  }
  public static void CheckParentNodes(TreeNode node, bool isChecked) {
   if (node == null) return;
   if (node.Parent == null) return;
   foreach (TreeNode child in node.Parent.Nodes) {
    if (child.Checked != isChecked) {
     node.Parent.Checked = false;
     CheckParentNodes(node.Parent, isChecked);
     return;
    }
   }
   node.Parent.Checked = isChecked;
   CheckParentNodes(node.Parent, isChecked);
  }
  private static Type TreeType = typeof(UltraTree);
  private static Type ScrollbarControlType = typeof(ScrollbarControl);
  private static Type UltraTreeNodeType = typeof(UltraTreeNode);
  public static bool InvokeDoVerticalScroll(UltraTree tree, int delta) {
   if (tree == null || tree.TopNode == null)
    return false;
   try {
    if (!InvokeAllVisibleNodesAreInView(tree) && (InvokeCanScrollDown(tree) || delta >= 0) ) {
     int visTopIndex = InvokeGetVisibleIndex(tree.TopNode);
     int num1 = 0;
     if (visTopIndex <= 3)
      num1 = delta / 120;
     else
      num1 = delta / 30;
     int num2 = visTopIndex - num1;
     ScrollEventType type = (num1 < 0) ? ScrollEventType.SmallIncrement : ScrollEventType.SmallIncrement;
     ScrollEventArgs args = new ScrollEventArgs(type, num2);
     ScrollbarControl scrollCtrl = (ScrollbarControl)TreeType.InvokeMember("ScrollbarControl",
                                                                           BindingFlags.Instance | BindingFlags.NonPublic |
                                                                           BindingFlags.GetProperty, null, tree, null);
     if (scrollCtrl != null) {
      ScrollbarControlType.InvokeMember("OnVerticalScroll",
                                        BindingFlags.Instance | BindingFlags.NonPublic |
                                        BindingFlags.InvokeMethod, null, scrollCtrl,
                                        new object[]{null, args});
      return true;
     }
    }
   } catch (Exception reflectionIssue) {
    Log.Error("InvokeDoVerticalScroll() reflection issue", reflectionIssue);
   }
   return true;
  }
  private static bool InvokeAllVisibleNodesAreInView(UltraTree tree) {
   if (tree == null)
    return true;
   return (bool)TreeType.InvokeMember("AllVisibleNodesAreInView",
    BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.GetProperty,
    null, tree, null);
  }
  private static bool InvokeCanScrollDown(UltraTree tree) {
   if (tree == null)
    return false;
   return (bool)TreeType.InvokeMember("CanScrollDown",
    BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.GetProperty,
    null, tree, null);
  }
  private static int InvokeGetVisibleIndex(UltraTreeNode node) {
   if (node == null)
    throw new ArgumentNullException("node");
   return (int)UltraTreeNodeType.InvokeMember("VisibleIndex",
    BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.GetProperty,
    null, node, null);
  }
 }
 internal class TreeNodesSortHelper: IComparer {
  private System.Windows.Forms.SortOrder _sortOrder;
  public TreeNodesSortHelper():this(System.Windows.Forms.SortOrder.Ascending) {}
  public TreeNodesSortHelper(System.Windows.Forms.SortOrder sortOrder) {
   _sortOrder = sortOrder;
  }
  public void InitFromConfig(string section, Settings reader) {
   _sortOrder = (System.Windows.Forms.SortOrder)reader.GetInt32(section+"/SubscribedFeedNodes.Sorter.SortOrder", (int)System.Windows.Forms.SortOrder.Descending);
  }
  public void SaveToConfig(string section, Settings writer) {
   writer.SetProperty(section+"/SubscribedFeedNodes.Sorter.SortOrder", (int)this._sortOrder);
  }
  public System.Windows.Forms.SortOrder Sorting {
   get { return _sortOrder; }
   set { _sortOrder = value; }
  }
  public virtual int Compare(object x, object y) {
   if (_sortOrder == System.Windows.Forms.SortOrder.None)
    return 0;
   if (Object.ReferenceEquals(x, y))
    return 0;
   if (x == null)
    return -1;
   if (y == null)
    return 1;
   TreeFeedsNodeBase n1 = (TreeFeedsNodeBase)x;
   TreeFeedsNodeBase n2 = (TreeFeedsNodeBase)y;
   if (n1.Type == FeedNodeType.Root && n2.Type == FeedNodeType.Root)
    return 0;
   if (n1.Type == n2.Type)
    return (_sortOrder == System.Windows.Forms.SortOrder.Ascending ? String.Compare(n1.Text, n2.Text) : String.Compare(n2.Text, n1.Text));
   if (n1.Type == FeedNodeType.Category)
    return -1;
   if (n2.Type == FeedNodeType.Category)
    return 1;
   if (n1.Type == FeedNodeType.FinderCategory)
    return -1;
   if (n2.Type == FeedNodeType.FinderCategory)
    return 1;
   return 0;
  }
 }
 internal class TreeFeedsNodeUIElementCreationFilter: IUIElementCreationFilter
 {
  private static Graphics cachedGraphics;
  static TreeFeedsNodeUIElementCreationFilter() {
   Bitmap b = new Bitmap(1,1);
   cachedGraphics = Graphics.FromImage(b);
  }
  bool IUIElementCreationFilter.BeforeCreateChildElements(UIElement parent) {
   if( parent is NodeSelectableAreaUIElement ) {
    NodeSelectableAreaUIElement uiElement = parent as NodeSelectableAreaUIElement;
    TreeFeedsNodeBase node = uiElement.Node as TreeFeedsNodeBase;
    if (node != null) {
     SizeF sz = cachedGraphics.MeasureString("(99999)", FontColorHelper.UnreadFont);
     uiElement.Rect = new Rectangle( uiElement.Rect.X, uiElement.Rect.Y,
      uiElement.Rect.Width + (int)sz.Width, uiElement.Rect.Height );
    }
    return false;
   }
   return false;
  }
  void IUIElementCreationFilter.AfterCreateChildElements(UIElement parent) {
   if( parent is NodeSelectableAreaUIElement ) {
   }
  }
 }
 [Serializable]
 [System.Xml.Serialization.XmlTypeAttribute(Namespace=RssBanditNamespace.TreeState)]
 [System.Xml.Serialization.XmlRootAttribute("treeState", Namespace=RssBanditNamespace.TreeState, IsNullable=false)]
 public class UltraTreeNodeExpansionMemento
 {
  [System.Xml.Serialization.XmlArrayAttribute("expanded")]
  [System.Xml.Serialization.XmlArrayItemAttribute("node", Type = typeof(System.String), IsNullable = false)]
  public ArrayList expandedNodes = new ArrayList();
  [System.Xml.Serialization.XmlArrayAttribute("selected")]
  [System.Xml.Serialization.XmlArrayItemAttribute("node", Type = typeof(System.String), IsNullable = false)]
  public ArrayList selectedNodes = new ArrayList();
  private UltraTree tree;
  private UltraTreeNode node;
  public UltraTreeNodeExpansionMemento() {}
  protected UltraTreeNodeExpansionMemento(UltraTree tree) {
   this.tree = tree;
   expandedNodes = new ArrayList();
   selectedNodes = new ArrayList(1);
   if (tree != null) {
    if (tree.SelectedNodes.Count > 0) {
     foreach (TreeFeedsNodeBase n in tree.SelectedNodes) {
      selectedNodes.Add(n.TypedRootFullPath);
     }
    }
    foreach (TreeFeedsNodeBase n in tree.Nodes) {
     if (n.Expanded) {
      expandedNodes.Add(n.TypedRootFullPath);
      AddNodesRecursive(n, expandedNodes);
     }
    }
   }
  }
  protected UltraTreeNodeExpansionMemento(TreeFeedsNodeBase node) {
   this.node = node;
   expandedNodes = new ArrayList();
   selectedNodes = new ArrayList(1);
   if (node != null && node.Control != null) {
    if (node.Control.SelectedNodes.Count > 0) {
     foreach (TreeFeedsNodeBase n in node.Control.SelectedNodes) {
      selectedNodes.Add(n.TypedRootFullPath);
     }
    }
    if (node.Expanded) {
     expandedNodes.Add(node.TypedRootFullPath);
     AddNodesRecursive(node, expandedNodes);
    }
   }
  }
  public void Restore(UltraTree tree) {
   if (tree == null || tree.Nodes.Count == 0 ||
    expandedNodes == null || expandedNodes.Count == 0)
    return;
   try {
    tree.BeginUpdate();
    HybridDictionary nodes = new HybridDictionary(expandedNodes.Count);
    for (int i=0; i < expandedNodes.Count; i++) {
     string path = (string)expandedNodes[i];
     nodes.Add(path, null);
    }
    HybridDictionary selNodes = new HybridDictionary(selectedNodes.Count);
    for (int i=0; i < selectedNodes.Count; i++) {
     string path = (string)selectedNodes[i];
     selNodes.Add(path, null);
    }
    foreach (TreeFeedsNodeBase n in tree.Nodes) {
     if (! n.Expanded && nodes.Contains(n.TypedRootFullPath)) {
      n.Expanded = true;
      nodes.Remove(n.FullPath);
     }
     ExpandNodesRecursive(n, nodes, selNodes);
     if (selNodes.Contains(n.FullPath)) {
      SelectNode(n);
     }
    }
   } finally {
    tree.EndUpdate();
   }
  }
  private void SelectNode(TreeFeedsNodeBase n) {
   if (n != null && n.Control != null) {
    if (n.Control.Visible) {
     n.Selected = true;
     if (n.Control.SelectedNodes.Count == 1) {
      n.Control.ActiveNode = n;
      n.BringIntoView(true);
     }
    }
   }
  }
  public void Restore(TreeFeedsNodeBase node) {
   if (node == null || node.Nodes.Count == 0 ||
    expandedNodes == null || expandedNodes.Count == 0)
    return;
   if (node.Control == null)
    return;
   try {
    node.Control.BeginUpdate();
    HybridDictionary nodes = new HybridDictionary(expandedNodes.Count);
    for (int i=0; i < expandedNodes.Count; i++) {
     string path = (string)expandedNodes[i];
     nodes.Add(path, null);
    }
    HybridDictionary selNodes = new HybridDictionary(selectedNodes.Count);
    for (int i=0; i < selectedNodes.Count; i++) {
     string path = (string)selectedNodes[i];
     selNodes.Add(path, null);
    }
    foreach (TreeFeedsNodeBase n in node.Nodes) {
     if (! n.Expanded && nodes.Contains(n.TypedRootFullPath)) {
      n.Expanded = true;
      nodes.Remove(n.FullPath);
     }
     ExpandNodesRecursive(n, nodes, selNodes);
     if (selNodes.Contains(n.FullPath)) {
      SelectNode(n);
     }
    }
   } finally {
    node.Control.EndUpdate();
   }
  }
  public static void Save(Stream stream, UltraTree tree) {
   UltraTreeNodeExpansionMemento m = new UltraTreeNodeExpansionMemento(tree);
   XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(
    typeof(UltraTreeNodeExpansionMemento), RssBanditNamespace.TreeState);
   serializer.Serialize(stream, m);
  }
  public static UltraTreeNodeExpansionMemento Load(Stream stream) {
   XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(
    typeof(UltraTreeNodeExpansionMemento), RssBanditNamespace.TreeState);
   return (UltraTreeNodeExpansionMemento)serializer.Deserialize(stream);
  }
  private void AddNodesRecursive(TreeFeedsNodeBase node, IList nodes) {
   if (node == null)
    return;
   foreach (TreeFeedsNodeBase n in node.Nodes) {
    if (n.Expanded) {
     nodes.Add(n.TypedRootFullPath);
     AddNodesRecursive(n, nodes);
    }
   }
  }
  private void ExpandNodesRecursive(TreeFeedsNodeBase node, IDictionary expanded, IDictionary selected) {
   if (node == null)
    return;
   foreach (TreeFeedsNodeBase n in node.Nodes) {
    if (! n.Expanded && expanded.Contains(n.TypedRootFullPath)) {
     n.Expanded = true;
     expanded.Remove(n.FullPath);
     ExpandNodesRecursive(n, expanded, selected);
    }
    if (selected.Contains(n.TypedRootFullPath))
     SelectNode(n);
   }
  }
 }
 internal class NodeInfoManager
 {
  private static Graphics cachedGraphics;
  static NodeInfoManager() {
   Bitmap b = new Bitmap(1,1);
   cachedGraphics = Graphics.FromImage(b);
  }
  public static void UpdateUnreadInfo(TreeFeedsNodeBase node) {
   if (node == null)
    return;
   if (node.RightImages.Count > 0) {
    node.RightImages.RemoveAt(0);
   }
   if (node.UnreadCount > 0)
   {
    string st = String.Format("({0})", node.UnreadCount);
    SizeF sz = cachedGraphics.MeasureString("(99999)", FontColorHelper.UnreadFont);
    Size gz = new Size((int)sz.Width, (int)sz.Height);
    if (! node.Control.RightImagesSize.Equals(gz))
     node.Control.RightImagesSize = gz;
    Bitmap bmp = new Bitmap(gz.Width, gz.Height);
    using (Graphics painter = Graphics.FromImage(bmp)) {
     painter.SmoothingMode = SmoothingMode.AntiAlias;
     if (Win32.IsOSAtLeastWindowsXP) {
      painter.TextRenderingHint = TextRenderingHint.SystemDefault;
     } else {
      painter.TextRenderingHint = TextRenderingHint.AntiAliasGridFit;
     }
     using (Brush unreadColorBrush = new SolidBrush(FontColorHelper.UnreadColor)) {
      painter.DrawString(st, FontColorHelper.UnreadFont, unreadColorBrush,
       0, 0, StringFormat.GenericDefault);
     }
    }
    node.RightImages.Add(bmp);
   }
  }
 }
}

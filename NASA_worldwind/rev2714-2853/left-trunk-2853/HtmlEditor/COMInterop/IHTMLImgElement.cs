using System;
using System.Diagnostics;
using System.IO;
using System.Collections;
using System.Runtime.InteropServices;
using System.Runtime.InteropServices.ComTypes;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices.CustomMarshalers;
namespace onlyconnect
{
    [ComImport, ComVisible(true), Guid("3050f240-98b5-11cf-bb82-00aa00bdce0b"),
InterfaceTypeAttribute(ComInterfaceType.InterfaceIsDual),
TypeLibType(TypeLibTypeFlags.FDual | TypeLibTypeFlags.FDispatchable)
]
    public interface IHTMLImgElement
    {
        object temp { set; get;}
        object temp1 { set; get;}
        object temp2 { get;}
        object temp3 { get;}
        object temp4 { get;}
        object temp5 { get;}
        object temp6 {get;}
        object temp7 { get;}
        object temp8 { get;}
        object temp9 { set; get;}
        object temp10 { set; get;}
        object temp11 { set; get;}
        object temp12 { set; get;}
        [DispId(dispids.DISPID_IHTMLIMGELEMENT_SRC)]
        string src {[param: MarshalAs(UnmanagedType.BStr)] set; [return: MarshalAs(UnmanagedType.BStr)] get;}
        object temp14 { set; get;}
        object temp15 { set; get;}
        object temp16 { set; get;}
        object temp17 { get;}
        object temp18 { get;}
        object temp19 { set; get;}
        object temp20 { set; get;}
        object temp21 { set; get;}
        object temp22 { set; get;}
        object temp23 { set; get;}
        object temp24 { set; get;}
        object temp25 { set; get;}
        object temp26 { set; get;}
        object temp27 { set; get;}
    }
}

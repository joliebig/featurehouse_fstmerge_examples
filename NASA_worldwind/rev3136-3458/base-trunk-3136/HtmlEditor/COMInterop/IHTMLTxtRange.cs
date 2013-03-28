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
    [ComImport, ComVisible(true), Guid("3050f220-98b5-11cf-bb82-00aa00bdce0b"),
      InterfaceTypeAttribute(ComInterfaceType.InterfaceIsDual),
      TypeLibType(TypeLibTypeFlags.FDual | TypeLibTypeFlags.FDispatchable)
      ]
    public interface IHTMLTxtRange
    {
        [DispId(dispids.DISPID_IHTMLTXTRANGE_HTMLTEXT)]
        string htmlText
        {
            [return: MarshalAs(UnmanagedType.BStr)]
            get;
        }
        [DispId(dispids.DISPID_IHTMLTXTRANGE_TEXT)]
        string text
        {
            set;
            get;
        }
        [DispId(dispids.DISPID_IHTMLTXTRANGE_PARENTELEMENT)]
        [return: MarshalAs(UnmanagedType.Interface)]
        IHTMLElement parentElement();
        [DispId(dispids.DISPID_IHTMLTXTRANGE_DUPLICATE)]
        [return: MarshalAs(UnmanagedType.Interface)]
        IHTMLTxtRange duplicate();
        void temp5();
        void temp6();
        void temp7();
        [DispId(dispids.DISPID_IHTMLTXTRANGE_COLLAPSE)]
        HRESULT collapse(
        [In, MarshalAs(UnmanagedType.Bool)]
        bool start);
        void temp9();
        [DispId(dispids.DISPID_IHTMLTXTRANGE_MOVE)]
        [return: MarshalAs(UnmanagedType.I4)]
        int move([In] string Unit, [In] int Count);
        [DispId(dispids.DISPID_IHTMLTXTRANGE_MOVESTART)]
        [return: MarshalAs(UnmanagedType.I4)]
        int moveStart([In] string Unit, [In] int Count);
        [DispId(dispids.DISPID_IHTMLTXTRANGE_MOVEEND)]
        [return: MarshalAs(UnmanagedType.I4)]
        int moveEnd([In] string Unit, [In] int Count);
        [DispId(dispids.DISPID_IHTMLTXTRANGE_SELECT)]
        void select();
        void pasteHTML([In, MarshalAs(UnmanagedType.BStr)] string html);
        [DispId(dispids.DISPID_IHTMLTXTRANGE_MOVETOELEMENTTEXT)]
        void moveToElementText(
        [In, MarshalAs(UnmanagedType.Interface)]
        IHTMLElement element);
        [DispId(dispids.DISPID_IHTMLTXTRANGE_SETENDPOINT)]
        void setEndPoint(
        [In, MarshalAs(UnmanagedType.BStr)]
        string how,
        [In, MarshalAs(UnmanagedType.Interface)]
        IHTMLTxtRange sourceRange);
        [DispId(dispids.DISPID_IHTMLTXTRANGE_COMPAREENDPOINTS)]
        [return: MarshalAs(UnmanagedType.I4)]
        int compareEndPoints(
        [In, MarshalAs(UnmanagedType.BStr)]
        string how,
        [In, MarshalAs(UnmanagedType.Interface)]
        IHTMLTxtRange sourceRange);
        void temp18();
        void temp19();
        void temp20();
        void temp21();
        void temp22();
        void temp23();
        void temp24();
        void temp25();
        void temp26();
        void temp27();
        [DispId(dispids.DISPID_IHTMLTXTRANGE_EXECCOMMAND)]
        [return: MarshalAs(UnmanagedType.Bool)]
        bool execCommand(
        [In, MarshalAs(UnmanagedType.BStr)]
        string cmdID,
        [In, MarshalAs(UnmanagedType.Bool)]
        bool showUI,
        [In, MarshalAs(UnmanagedType.Struct)]
        Object value);
        void temp29();
    }
}

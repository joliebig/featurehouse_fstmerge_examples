import sys
import os
import shutil
import version
def sanitycheck():
    "Check all dependencies are present and at the correct version"
    print "=== Sanity check ==="
    print "python version",
    if sys.version_info[:2]!=(2,3):
       raise Exception("Should be  Python 2.3 - this is "+sys.version)
    print "  OK"
    print "wxPython version",
    import wx
    if wx.VERSION[:4]!=(2,6,2,1):
        raise Exception("Should be wxPython 2.6.2.1.  This is "+`wx.VERSION`)
    print "  OK"
    print "wxPython is unicode build",
    if not wx.USE_UNICODE:
        raise Exception("You need a unicode build of wxPython")
    print "  OK"
    if sys.platform!='win32':
        print "native.usb",
        import native.usb
        print "  OK"
    print "pycrypto version",
    expect='2.0.1'
    import Crypto
    if Crypto.__version__!=expect:
        raise Exception("Should be %s version of pycrypto - you have %s" % (expect, Crypto.__version__))
    print "  OK"
    print "paramiko version",
    expect='1.4 (oddish)'
    import paramiko
    if paramiko.__version__!=expect:
        raise Exception("Should be %s version of paramiko - you have %s" % (expect, paramiko.__version__))
    print "  OK"
    print "bitfling",
    import bitfling
    print "  OK"
    print "pyserial",
    import serial
    print "  OK"
    print "apsw",
    import apsw
    ver="3.2.7-r1"
    if apsw.apswversion()!=ver:
        raise Exception("Should be apsw version %s - you have %s" % (ver, apsw.apswversion()))
    print "  OK"
    print "sqlite",
    ver="3.2.7"
    if apsw.sqlitelibversion()!=ver:
        raise Exception("Should be sqlite version %s - you have %s" % (ver, apsw.sqlitelibversion()))
    print "  OK"
    print "jaro/winkler string matcher",
    import native.strings.jarow
    print "  OK"
    if sys.platform=="linux2":
        print "bsddb ",
        import bsddb
        print "  OK"
    if sys.platform=='win32':
        import py2exe.mf as modulefinder # in py2exe < 0.6.4 use "import modulefinder"
        import win32com
        for p in win32com.__path__[1:]:
            modulefinder.AddPackagePath("win32com", p)
        for extra in ["win32com.shell"]: #,"win32com.mapi"
            __import__(extra)
            m = sys.modules[extra]
            for p in m.__path__[1:]:
                modulefinder.AddPackagePath(extra, p)
    print "=== All checks out ==="
def resources():
    """Get a list of the resources (images, executables, sounds etc) we ship
    @rtype: dict
    @return: The key for each entry in the dict is a directory name, and the value
             is a list of files within that directory"""
    tbl={}
    exts=[ '*.xy', '*.png', '*.ttf', '*.wav', '*.jpg', '*.css', '*.pdc', '*.ids']
    if sys.platform=='win32':
        exts=exts+['*.chm', '*.manifest', '*.ico']
        exts=exts+['helpers/*.exe','helpers/*.dll']
    if sys.platform=='linux2':
        exts=exts+['helpers/*.lbin', '*.htb']
    if sys.platform=='darwin':
        exts=exts+['helpers/*.mbin', '*.htb']
    dirs=[ os.path.join('.', 'resources'), '.' ]
    dontship.append("pvconv.exe")  # Qualcomm won't answer if I can ship this
    for wildcard in exts:
        for dir in dirs:
            for file in glob.glob(os.path.join(dir, wildcard)):
                if os.path.basename(file).lower() in dontship: continue 
                d=os.path.dirname(file)
                if not tbl.has_key(d):
                    tbl[d]=[]
                tbl[d].append(file)
    files=[]
    for i in tbl.keys():
        files.append( (i, tbl[i]) )
    return files
def isofficialbuild():
    "Work out if this is an official build"
    import socket
    h=socket.gethostname().lower()
    return h in ('rh9bitpim.rogerbinns.com', "roger-sqyvr14d3",
             "smpbook.n9yty.com", "smpbook.local.", "rogerbmac.rogerbinns.com", "rogerbmac.local")
def ensureofficial():
    """If this is not an official build then ensure that version.vendor doesn't say it is"""
    version.__freeze()
    print "Reloading version"
    reload(version)
    if not isofficialbuild():
        if version.vendor=="official":
            versionpy=os.path.join(os.path.dirname(__file__), "version.py")
            out=[]
            for line in open(versionpy, "rt"):
                if line.startswith('vendor="'):
                    line='vendor="$%s %s $"\n' % ("Id:", "unofficial")
                out.append(line)
            open(versionpy, "wt").write("".join(out))
            reload(version)
def getversion():
    return version.version
import phones
def getcxfreezeoptions(defaults):
    defaults.update(
        {
        'app': [{'script': 'src/bp.py', 'dest_base': 'bitpim'}],
        }
        )
    defaults['options']['cxfreeze']['includes']=phones.getallmodulenames()
    return defaults
def getpy2appoptions(defaults):
    defaults.update(
        {
        'app': [{'script': 'src/bp.py',}],
        }
        )
    defaults['options']['py2app']['includes']=phones.getallmodulenames()
    defaults['options']['py2app']['plist']['CFBundleHelpBookFolder']="BitPim Help"
    defaults['options']['py2app']['plist']['CFBundleHelpBookName']="BitPim Help"
    return defaults
def getpy2exeoptions(defaults):
    defaults.update(
        {
        'windows': [{ 'script': 'src/bp.py', 'dest_base': 'bitpim', }],
        }
        )
    defaults['options']['py2exe']['includes']=phones.getallmodulenames()
    defaults['options']['py2exe']['compressed']=0 # make setup.exe smaller but installed code larger
    return defaults
def copyresources(destdir):
    import packageutils
    packageutils.copysvndir('resources', os.path.join(destdir, 'resources'), resourcefilter)
    packageutils.copysvndir('helpers', os.path.join(destdir, 'helpers'), resourcefilter)
def resourcefilter(srcfilename, destfilename):
    exts=[ '.xy', '.png', '.ttf', '.wav', '.jpg', '.css', '.pdc', '.ids', '.ico']
    if sys.platform=='win32':
        exts=exts+['.chm', '.exe', '.dll']
    if sys.platform=='linux2':
        exts=exts+['.lbin', '.htb']
    if sys.platform=='darwin':
        exts=exts+['.mbin', '.htb']
    if os.path.splitext(srcfilename)[1] in exts:
        return srcfilename, destfilename
    return None
def finalize(destdir):
    if sys.platform=='win32':
        for f in ("w9xpopen.exe",):
            if os.path.exists(os.path.join(destdir, f)):
                os.remove(os.path.join(destdir, f))
    if sys.platform=='darwin':
        import zipfile
        helpdir=os.path.join(destdir, "English.lproj", "BitPim Help")
        os.makedirs(helpdir)
        f=zipfile.ZipFile(os.path.join(destdir, "resources", "bitpim.htb"), "r")
        for name in f.namelist():
            if os.path.splitext(name)[1] in ('.htm', '.html', '.jpg', '.png'):
                open(os.path.join(helpdir, name), "wb").write(f.read(name))
                os.chmod(os.path.join(helpdir, name), 0444)
            else:
                print "skipping help file",name
        res=os.system("open -a \"Apple Help Indexing Tool\" \""+helpdir+"\"")
        assert res==0
        print "Waiting for indexing tool to stop by monitoring CPU consumption"
        import time
        lastval=""
        val="x"
        pid=0
        while val!=lastval:
            print ".",
            sys.stdout.flush()
            time.sleep(2)
            for line in os.popen("ps cx", "r"):
                line=line.split()
                line=line[:4]+[" ".join(line[4:])]
                if line[4]!="Apple Help Indexing Tool":
                    continue
                pid=line[0]
                lastval=val
                val=line[3]
                break
        print "\nIt would appear to be done"
        os.system("kill "+pid)
        shutil.copy2(os.path.join(destdir, "resources", "bitpim.css"), os.path.join(helpdir, ".."))
        os.remove(os.path.join(destdir, "resources", "bitpim.htb"))
    if sys.platform!='win32':
        os.system("find \""+destdir+"\" -depth -print0 | xargs -0 chmod a-w")
def getvals():
    "Return various values about this product"
    res={
        'NAME': version.name,
        'VERSION': version.version,
        'RELEASE': version.release,
        'DQVERSION': version.dqverstr,
        'COMMENTS': "Provided under the GNU Public License (GPL)",
        'DESCRIPTION': "View and manipulate data on many CDMA phones from LG, Samsung, Sanyo and other manufacturers. This includes the PhoneBook, Calendar, WallPapers, RingTones (functionality varies by phone) and the Filesystem for most Qualcomm CDMA chipset based phones.",
        'COPYRIGHT': "Copyright © 2003-2006 The BitPim developers",
        'URL': version.url,
        'SUPPORTURL': "http://www.bitpim.org/help/support.htm",
        'GUID': "{FA61D601-A0FC-48BD-AE7A-54946BCD7FB6}",
        'VENDOR': version.vendor,
        'ISSFILE': 'packaging/bitpim.iss',
        'SPECFILE': 'packaging/bitpim.spec',
        }
    if sys.platform=='win32':
        res['ICONFILE']="packaging/bitpim.ico"
    if sys.platform=="darwin":
        res['GUID']='org.bitpim.bitpim' # Java style less opaque than the guid style above!
        res['ICONFILE']="packaging/bitpim.icns"
        v=os.popen("sw_vers -productVersion", "r").read()
        if v.startswith("10.3"):
            res['OUTFILEPREFIX']='PANTHER-'
        elif v.startswith("10.4"):
            res['OUTFILEPREFIX']='TIGER-'
        elif v.startswith("10.2"):
            res['OUTFILEPREFIX']='JAGUAR-'
        elif v.startswith("10.5"):
            res['OUTFILEPREFIX']='LEOPARD-'
    return res

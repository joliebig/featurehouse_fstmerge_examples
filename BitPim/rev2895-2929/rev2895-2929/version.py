"""Information about BitPim version number"""

__FROZEN__="$Id: version.py 2897 2006-03-12 20:51:48Z rogerb $"

import os, sys

import time

name="BitPim"

vendor="$Id: version.py 2897 2006-03-12 20:51:48Z rogerb $"

release=0

contact="The BitPim home page is at http://www.bitpim.org.  You can post any " \
         "questions or feedback to the mailing list detailed on that page."

svnrevision=0

if vendor[1:].startswith("Id:"):

    if len(vendor.split())>3:

        vendor=""

    else:

        vendor=vendor.split()[1]


_headurl="$HeadURL: https://bitpim.svn.sourceforge.net/svnroot/bitpim/developer/skyjunky/trees/src/version.py $".split()[1]

_rp="https://svn.sourceforge.net/svnroot/bitpim/releases/"

if _headurl.startswith(_rp):

    def isdevelopmentversion(): return False

    version=_headurl[len(_rp):].split("/")[0]

    if len(vendor)==0:

        vendor="official"

else:

    def isdevelopmentversion(): return True

    prefix="https://svn.sourceforge.com/svnroot/bitpim/"

    version="-".join(_headurl[len(prefix):].split("/")[:-2]) 

    del prefix

    f=__FROZEN__.split()

    if len(f)==3: 

        svnrevision=int(f[1])

        version=version+"-"+`svnrevision`

    if len(vendor)==0:

        vendor="developer build"


del _headurl

del _rp

versionstring=version

if release>0:

    versionstring+="-"+`release`


if not isdevelopmentversion():

    dqver=[int(x) for x in version.split(".")]

    while len(dqver)<3:

        dqver.append(0)

    while len(dqver)<4:

        dqver.append(svnrevision)

    dqver=dqver[:4]

else:

    dqver=[0,0,0,svnrevision] 


dqverstr=".".join([`x` for x in dqver])

del x

url="http://www.bitpim.org"

description="BitPim "+versionstring

copyright="(C) 2003-2006 Roger Binns and others - see http://www.bitpim.org"

def __freeze():

    myfilename=os.path.splitext(__file__)[0]+".py"

    print "Freezing version"

    svnver=os.popen("svnversion -n .", "r").read()

    if len(svnver)<4:

        print "svnversion command doesn't appear to be working."

        sys.exit(3)

    try:

        if svnver[-1]=='M': svnver=svnver[:-1]

        [int(x) for x in svnver.split(":")]

    except:

        print "Your tree isn't pure. Do you have files not checked in (M)?"

        print svnver,"was returned by svnversion"

        sys.exit(4)

    svnver=svnver.split(":")[-1]

    print "Embedding svnrevision",svnver,"into",myfilename

    result=[]

    for line in open(myfilename, "rtU"):

        if line.startswith('__FROZEN__="$Id:'):

            line='__FROZEN__="$%s %s $"\n' % ("Id:", svnver)

        result.append(line)

    open(myfilename, "wt").write("".join(result))

    for ext in (".pyc", ".pyo"):

        try:

            os.remove(os.path.splitext(__file__)[0]+ext)

        except OSError:

            pass

if __name__=='__main__':

    import sys

    if len(sys.argv)==1:

        print "#define VERSION", versionstring

        print "#define DATENOW", time.strftime("%d %B %Y")

    elif sys.argv[1]=="freeze":

        __freeze()

    else:

        print "Unknown arguments",sys.argv[1:]


f=__FROZEN__.split()

if len(f)==3: 

    svnrevision=int(f[1])


if vendor[1:].startswith("Id:"):

    if len(vendor.split())>3:

        vendor=""

    else:

        vendor=vendor.split()[1]


if _headurl.startswith(_rp):

    def isdevelopmentversion(): return False

    version=_headurl[len(_rp):].split("/")[0]

    if len(vendor)==0:

        vendor="official"

else:

    def isdevelopmentversion(): return True

    prefix="https://svn.sourceforge.com/svnroot/bitpim/"

    version="-".join(_headurl[len(prefix):].split("/")[:-2]) 

    del prefix

    if svnrevision:

        version=version+"-"+`svnrevision`

    if len(vendor)==0:

        vendor="developer build"


if release>0:

    versionstring+="-"+`release`


if not isdevelopmentversion():

    dqver=[int(x) for x in version.split(".")]

    while len(dqver)<3:

        dqver.append(0)

    while len(dqver)<4:

        dqver.append(svnrevision)

    dqver=dqver[:4]

else:

    dqver=[0,0,0,svnrevision] 


if __name__=='__main__':

    import sys

    if len(sys.argv)==1:

        print "#define VERSION", versionstring

        print "#define DATENOW", time.strftime("%d %B %Y")

    elif sys.argv[1]=="freeze":

        __freeze()

    else:

        print "Unknown arguments",sys.argv[1:]



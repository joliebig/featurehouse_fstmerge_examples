"""Information about BitFling version number"""

import time

name="BitFling"

version="0.1"

release=0

testver=2

versionstring=version

if testver>0:

    versionstring+="-test"+`testver`


if release>0:

    versionstring+="-"+`release`


x=[int(x) for x in version.split(".")]

if x[1]<10:  

    x[1]=x[1]*10


assert x[1]>=10 and x[1]<=99

x.append(x[1]%10)

if testver:

    x.append(testver)

else:

    x.append(1000+release)


dqver=x[:]

del x

dqverstr=".".join([`x` for x in dqver])

author="Roger Binns"

author_email="rogerb@users.sourceforge.net"

url="http://bitpim.sourceforge.net"

description="BitFling "+versionstring

copyright="(C) 2004 Roger Binns <rogerb@users.sf.net> and others - see http://bitpim.sf.net"

if __name__=='__main__':

    print "#define VERSION", versionstring

    print "#define DATENOW", time.strftime("%d %B %Y")


if testver>0:

    versionstring+="-test"+`testver`


if release>0:

    versionstring+="-"+`release`


if x[1]<10:  

    x[1]=x[1]*10


if testver:

    x.append(testver)

else:

    x.append(1000+release)


if __name__=='__main__':

    print "#define VERSION", versionstring

    print "#define DATENOW", time.strftime("%d %B %Y")



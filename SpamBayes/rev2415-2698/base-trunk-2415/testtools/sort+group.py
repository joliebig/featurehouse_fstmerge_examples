"""Usage: sort+group.py
This program has no options!  Muahahahaha!
"""
import sys
import os
import glob
import time
from email.Utils import parsedate_tz, mktime_tz
loud = True
SECONDS_PER_DAY = 24 * 60 * 60
def get_time(fpath):
    fh = file(fpath, 'rb')
    lines = iter(fh)
    for line in lines:
        if line.lower().startswith("received:"):
            break
    else:
        print "\nNo Received header found."
        fh.close()
        return None
    received = line
    for line in lines:
        if line[0] in ' \t':
            received += line
        else:
            break
    fh.close()
    i = received.rfind(';')
    if i < 0:
        print "\n" + received
        print "No semicolon found in Received header."
        return None
    datestring = received[i+1:]
    datestring = ' '.join(datestring.split())
    as_tuple = parsedate_tz(datestring)
    if as_tuple is None:
        print "\n" + received
        print "Couldn't parse the date: %r" % datestring
        return None
    return mktime_tz(as_tuple)
def main():
    """Main program; parse options and go."""
    from os.path import join, split
    data = []   # list of (time_received, dirname, basename) triples
    if loud:
        print "Scanning everything"
    now = time.time()
    for name in glob.glob('Data/*/*/*'):
        if loud:
            sys.stdout.write("%-78s\r" % name)
            sys.stdout.flush()
        when_received = get_time(name) or now
        data.append((when_received,) + split(name))
    if loud:
        print ""
        print "Sorting ..."
    data.sort()
    if loud:
        print "Renaming first pass ..."
    for dummy, dirname, basename in data:
        os.rename(join(dirname, basename),
                  join(dirname, "-" + basename))
    if loud:
        print "Renaming second pass ..."
    earliest = data[0][0]  # timestamp of earliest msg received
    i = 0
    for when_received, dirname, basename in data:
        extension = os.path.splitext(basename)[-1]
        group = int((when_received - earliest) / SECONDS_PER_DAY)
        newbasename = "%04d-%06d" % (group, i)
        os.rename(join(dirname, "-" + basename),
                  join(dirname, newbasename + extension))
        i += 1
if __name__ == "__main__":
    main()

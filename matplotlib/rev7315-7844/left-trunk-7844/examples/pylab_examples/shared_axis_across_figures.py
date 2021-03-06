"""
connect the data limits on the axes in one figure with the axes in
another.  This is not the right way to do this for two axes in the
same figure -- use the sharex and sharey property in that case
"""
import numpy
from pylab import figure, show
fig1 = figure()
fig2 = figure()
ax1 = fig1.add_subplot(111)
ax2 = fig2.add_subplot(111, sharex=ax1, sharey=ax1)
ax1.plot(numpy.random.rand(100), 'o')
ax2.plot(numpy.random.rand(100), 'o')
show()

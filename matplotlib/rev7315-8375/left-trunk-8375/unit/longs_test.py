from pylab import *
x = arange(1000) + 2**32
subplot(211)
plot(x,x)
subplot(212)
loglog(x,x)
show()

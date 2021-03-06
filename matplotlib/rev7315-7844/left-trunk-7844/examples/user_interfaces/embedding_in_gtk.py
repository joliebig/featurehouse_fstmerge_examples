"""
show how to add a matplotlib FigureCanvasGTK or FigureCanvasGTKAgg widget to a
gtk.Window
"""
import gtk
from matplotlib.figure import Figure
from numpy import arange, sin, pi
from matplotlib.backends.backend_gtkagg import FigureCanvasGTKAgg as FigureCanvas
win = gtk.Window()
win.connect("destroy", lambda x: gtk.main_quit())
win.set_default_size(400,300)
win.set_title("Embedding in GTK")
f = Figure(figsize=(5,4), dpi=100)
a = f.add_subplot(111)
t = arange(0.0,3.0,0.01)
s = sin(2*pi*t)
a.plot(t,s)
canvas = FigureCanvas(f)  # a gtk.DrawingArea
win.add(canvas)
win.show_all()
gtk.main()

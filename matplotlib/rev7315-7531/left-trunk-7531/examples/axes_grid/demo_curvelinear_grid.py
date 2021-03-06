import numpy as np
import matplotlib.pyplot as plt
import matplotlib.cbook as cbook
from  mpl_toolkits.axes_grid.grid_helper_curvelinear import GridHelperCurveLinear
from mpl_toolkits.axes_grid.axislines import Subplot
from mpl_toolkits.axes_grid.parasite_axes import SubplotHost, \
     ParasiteAxesAuxTrans
def curvelinear_test1(fig):
    """
    grid for custom transform.
    """
    def tr(x, y):
        x, y = np.asarray(x), np.asarray(y)
        return x, y-x
    def inv_tr(x,y):
        x, y = np.asarray(x), np.asarray(y)
        return x, y+x
    grid_helper = GridHelperCurveLinear((tr, inv_tr))
    ax1 = Subplot(fig, 1, 2, 1, grid_helper=grid_helper)
    fig.add_subplot(ax1)
    xx, yy = tr([3, 6], [5.0, 10.])
    ax1.plot(xx, yy)
    ax1.set_aspect(1.)
    ax1.set_xlim(0, 10.)
    ax1.set_ylim(0, 10.)
    ax1.grid(True)
import  mpl_toolkits.axes_grid.angle_helper as angle_helper
from matplotlib.projections import PolarAxes
from matplotlib.transforms import Affine2D
def curvelinear_test2(fig):
    """
    polar projection, but in a rectangular box.
    """
    tr = Affine2D().scale(np.pi/180., 1.) + PolarAxes.PolarTransform()
    extreme_finder = angle_helper.ExtremeFinderCycle(20, 20,
                                                     lon_cycle = 360,
                                                     lat_cycle = None,
                                                     lon_minmax = None,
                                                     lat_minmax = (0, np.inf),
                                                     )
    grid_locator1 = angle_helper.LocatorDMS(12)
    tick_formatter1 = angle_helper.FormatterDMS()
    grid_helper = GridHelperCurveLinear(tr,
                                        extreme_finder=extreme_finder,
                                        grid_locator1=grid_locator1,
                                        tick_formatter1=tick_formatter1
                                        )
    ax1 = SubplotHost(fig, 1, 2, 2, grid_helper=grid_helper)
    ax1.axis["right"].major_ticklabels.set_visible(True)
    ax1.axis["top"].major_ticklabels.set_visible(True)
    ax1.axis["right"].get_helper().nth_coord_ticks=0
    ax1.axis["bottom"].get_helper().nth_coord_ticks=1
    fig.add_subplot(ax1)
    ax2 = ParasiteAxesAuxTrans(ax1, tr, "equal")
    ax1.parasites.append(ax2)
    intp = cbook.simple_linear_interpolation
    ax2.plot(intp(np.array([0, 30]), 50),
             intp(np.array([10., 10.]), 50))
    ax1.set_aspect(1.)
    ax1.set_xlim(-5, 12)
    ax1.set_ylim(-5, 10)
    ax1.grid(True)
if 1:
    fig = plt.figure(1, figsize=(7, 4))
    fig.clf()
    curvelinear_test1(fig)
    curvelinear_test2(fig)
    plt.draw()
    plt.show()

import matplotlib.pyplot as plt
from mpl_toolkits.axes_grid.axislines import SubplotZero
if 1:
    fig = plt.figure(1)
    fig.subplots_adjust(right=0.85)
    ax = SubplotZero(fig, 1, 1, 1)
    fig.add_subplot(ax)
    ax.axis["right"].set_visible(False)
    ax.axis["top"].set_visible(False)
    ax.axis["xzero"].set_visible(True)
    ax.axis["xzero"].label.set_text("Axis Zero")
    ax.set_ylim(-2, 4)
    ax.set_xlabel("Label X")
    ax.set_ylabel("Label Y")
    offset = (20, 0)
    new_axisline = ax.get_grid_helper().new_fixed_axis
    ax.axis["right2"] = new_axisline(loc="right",
                                     offset=offset,
                                     axes=ax)
    ax.axis["right2"].label.set_text("Label Y2")
    ax.plot([-2,3,2])
    plt.draw()
    plt.show()

import math
import warnings
import numpy as np
import matplotlib
rcParams = matplotlib.rcParams
from matplotlib.axes import Axes
import matplotlib.axis as maxis
from matplotlib import cbook
from matplotlib import docstring
from matplotlib.patches import Circle
from matplotlib.path import Path
from matplotlib.ticker import Formatter, Locator, FormatStrFormatter
from matplotlib.transforms import Affine2D, Affine2DBase, Bbox, \
    BboxTransformTo, IdentityTransform, Transform, TransformWrapper
import matplotlib.spines as mspines
class PolarAxes(Axes):
    """
    A polar graph projection, where the input dimensions are *theta*, *r*.
    Theta starts pointing east and goes anti-clockwise.
    """
    name = 'polar'
    class PolarTransform(Transform):
        """
        The base polar transform.  This handles projection *theta* and
        *r* into Cartesian coordinate space *x* and *y*, but does not
        perform the ultimate affine transformation into the correct
        position.
        """
        input_dims = 2
        output_dims = 2
        is_separable = False
        def transform(self, tr):
            xy   = np.zeros(tr.shape, np.float_)
            t    = tr[:, 0:1]
            r    = tr[:, 1:2]
            x    = xy[:, 0:1]
            y    = xy[:, 1:2]
            x[:] = r * np.cos(t)
            y[:] = r * np.sin(t)
            return xy
        transform.__doc__ = Transform.transform.__doc__
        transform_non_affine = transform
        transform_non_affine.__doc__ = Transform.transform_non_affine.__doc__
        def transform_path(self, path):
            vertices = path.vertices
            if len(vertices) == 2 and vertices[0, 0] == vertices[1, 0]:
                return Path(self.transform(vertices), path.codes)
            ipath = path.interpolated(path._interpolation_steps)
            return Path(self.transform(ipath.vertices), ipath.codes)
        transform_path.__doc__ = Transform.transform_path.__doc__
        transform_path_non_affine = transform_path
        transform_path_non_affine.__doc__ = Transform.transform_path_non_affine.__doc__
        def inverted(self):
            return PolarAxes.InvertedPolarTransform()
        inverted.__doc__ = Transform.inverted.__doc__
    class PolarAffine(Affine2DBase):
        """
        The affine part of the polar projection.  Scales the output so
        that maximum radius rests on the edge of the axes circle.
        """
        def __init__(self, scale_transform, limits):
            u"""
            *limits* is the view limit of the data.  The only part of
            its bounds that is used is ymax (for the radius maximum).
            The theta range is always fixed to (0, 2\u03c0).
            """
            Affine2DBase.__init__(self)
            self._scale_transform = scale_transform
            self._limits = limits
            self.set_children(scale_transform, limits)
            self._mtx = None
        def get_matrix(self):
            if self._invalid:
                limits_scaled = self._limits.transformed(self._scale_transform)
                ymax = limits_scaled.ymax
                affine = Affine2D() \
                    .scale(0.5 / ymax) \
                    .translate(0.5, 0.5)
                self._mtx = affine.get_matrix()
                self._inverted = None
                self._invalid = 0
            return self._mtx
        get_matrix.__doc__ = Affine2DBase.get_matrix.__doc__
    class InvertedPolarTransform(Transform):
        """
        The inverse of the polar transform, mapping Cartesian
        coordinate space *x* and *y* back to *theta* and *r*.
        """
        input_dims = 2
        output_dims = 2
        is_separable = False
        def transform(self, xy):
            x = xy[:, 0:1]
            y = xy[:, 1:]
            r = np.sqrt(x*x + y*y)
            theta = np.arccos(x / r)
            theta = np.where(y < 0, 2 * np.pi - theta, theta)
            return np.concatenate((theta, r), 1)
        transform.__doc__ = Transform.transform.__doc__
        def inverted(self):
            return PolarAxes.PolarTransform()
        inverted.__doc__ = Transform.inverted.__doc__
    class ThetaFormatter(Formatter):
        u"""
        Used to format the *theta* tick labels.  Converts the
        native unit of radians into degrees and adds a degree symbol
        (\u00b0).
        """
        def __call__(self, x, pos=None):
            if rcParams['text.usetex'] and not rcParams['text.latex.unicode']:
                return r"$%0.0f^\circ$" % ((x / np.pi) * 180.0)
            else:
                return u"%0.0f\u00b0" % ((x / np.pi) * 180.0)
    class RadialLocator(Locator):
        """
        Used to locate radius ticks.
        Ensures that all ticks are strictly positive.  For all other
        tasks, it delegates to the base
        :class:`~matplotlib.ticker.Locator` (which may be different
        depending on the scale of the *r*-axis.
        """
        def __init__(self, base):
            self.base = base
        def __call__(self):
            ticks = self.base()
            return [x for x in ticks if x > 0]
        def autoscale(self):
            return self.base.autoscale()
        def pan(self, numsteps):
            return self.base.pan(numsteps)
        def zoom(self, direction):
            return self.base.zoom(direction)
        def refresh(self):
            return self.base.refresh()
        def view_limits(self, vmin, vmax):
            vmin, vmax = self.base.view_limits(vmin, vmax)
            return 0, vmax
    def __init__(self, *args, **kwargs):
        """
        Create a new Polar Axes for a polar plot.
        The following optional kwargs are supported:
          - *resolution*: The number of points of interpolation between
            each pair of data points.  Set to 1 to disable
            interpolation.
        """
        self._rpad = 0.05
        self.resolution = kwargs.pop('resolution', None)
        if self.resolution not in (None, 1):
            warnings.warn(
                """The resolution kwarg to Polar plots is now ignored.
If you need to interpolate data points, consider running
cbook.simple_linear_interpolation on the data before passing to matplotlib.""")
        Axes.__init__(self, *args, **kwargs)
        self.set_aspect('equal', adjustable='box', anchor='C')
        self.cla()
    __init__.__doc__ = Axes.__init__.__doc__
    def cla(self):
        Axes.cla(self)
        self.title.set_y(1.05)
        self.xaxis.set_major_formatter(self.ThetaFormatter())
        angles = np.arange(0.0, 360.0, 45.0)
        self.set_thetagrids(angles)
        self.yaxis.set_major_locator(self.RadialLocator(self.yaxis.get_major_locator()))
        self.grid(rcParams['polaraxes.grid'])
        self.xaxis.set_ticks_position('none')
        self.yaxis.set_ticks_position('none')
    def _init_axis(self):
        "move this out of __init__ because non-separable axes don't use it"
        self.xaxis = maxis.XAxis(self)
        self.yaxis = maxis.YAxis(self)
        self._update_transScale()
    def _set_lim_and_transforms(self):
        self.transAxes = BboxTransformTo(self.bbox)
        self.transScale = TransformWrapper(IdentityTransform())
        self.transProjection = self.PolarTransform()
        self.transProjectionAffine = self.PolarAffine(self.transScale, self.viewLim)
        self.transData = self.transScale + self.transProjection + \
            (self.transProjectionAffine + self.transAxes)
        self._xaxis_transform = (
            self.transProjection +
            self.PolarAffine(IdentityTransform(), Bbox.unit()) +
            self.transAxes)
        self._theta_label1_position = Affine2D().translate(0.0, 1.1)
        self._xaxis_text1_transform = (
            self._theta_label1_position +
            self._xaxis_transform)
        self._theta_label2_position = Affine2D().translate(0.0, 1.0 / 1.1)
        self._xaxis_text2_transform = (
            self._theta_label2_position +
            self._xaxis_transform)
        self._yaxis_transform = (
            Affine2D().scale(np.pi * 2.0, 1.0) +
            self.transData)
        self._r_label1_position = Affine2D().translate(22.5, self._rpad)
        self._yaxis_text1_transform = (
            self._r_label1_position +
            Affine2D().scale(1.0 / 360.0, 1.0) +
            self._yaxis_transform
            )
        self._r_label2_position = Affine2D().translate(22.5, self._rpad)
        self._yaxis_text2_transform = (
            self._r_label2_position +
            Affine2D().scale(1.0 / 360.0, 1.0) +
            self._yaxis_transform
            )
    def get_xaxis_transform(self,which='grid'):
        assert which in ['tick1','tick2','grid']
        return self._xaxis_transform
    def get_xaxis_text1_transform(self, pad):
        return self._xaxis_text1_transform, 'center', 'center'
    def get_xaxis_text2_transform(self, pad):
        return self._xaxis_text2_transform, 'center', 'center'
    def get_yaxis_transform(self,which='grid'):
        assert which in ['tick1','tick2','grid']
        return self._yaxis_transform
    def get_yaxis_text1_transform(self, pad):
        return self._yaxis_text1_transform, 'center', 'center'
    def get_yaxis_text2_transform(self, pad):
        return self._yaxis_text2_transform, 'center', 'center'
    def _gen_axes_patch(self):
        return Circle((0.5, 0.5), 0.5)
    def _gen_axes_spines(self):
        return {'polar':mspines.Spine.circular_spine(self,
                                                     (0.5, 0.5), 0.5)}
    def set_rmax(self, rmax):
        self.viewLim.y0 = 0
        self.viewLim.y1 = rmax
        angle = self._r_label1_position.to_values()[4]
    def get_rmax(self):
        return self.viewLim.ymax
    def set_yscale(self, *args, **kwargs):
        Axes.set_yscale(self, *args, **kwargs)
        self.yaxis.set_major_locator(
            self.RadialLocator(self.yaxis.get_major_locator()))
    set_rscale = Axes.set_yscale
    set_rticks = Axes.set_yticks
    @docstring.dedent_interpd
    def set_thetagrids(self, angles, labels=None, frac=None, fmt=None,
                       **kwargs):
        """
        Set the angles at which to place the theta grids (these
        gridlines are equal along the theta dimension).  *angles* is in
        degrees.
        *labels*, if not None, is a ``len(angles)`` list of strings of
        the labels to use at each angle.
        If *labels* is None, the labels will be ``fmt %% angle``
        *frac* is the fraction of the polar axes radius at which to
        place the label (1 is the edge). Eg. 1.05 is outside the axes
        and 0.95 is inside the axes.
        Return value is a list of tuples (*line*, *label*), where
        *line* is :class:`~matplotlib.lines.Line2D` instances and the
        *label* is :class:`~matplotlib.text.Text` instances.
        kwargs are optional text properties for the labels:
        %(Text)s
        ACCEPTS: sequence of floats
        """
        angles = np.asarray(angles, np.float_)
        self.set_xticks(angles * (np.pi / 180.0))
        if labels is not None:
            self.set_xticklabels(labels)
        elif fmt is not None:
            self.xaxis.set_major_formatter(FormatStrFormatter(fmt))
        if frac is not None:
            self._theta_label1_position.clear().translate(0.0, frac)
            self._theta_label2_position.clear().translate(0.0, 1.0 / frac)
        for t in self.xaxis.get_ticklabels():
            t.update(kwargs)
        return self.xaxis.get_ticklines(), self.xaxis.get_ticklabels()
    @docstring.dedent_interpd
    def set_rgrids(self, radii, labels=None, angle=None, rpad=None, fmt=None,
                   **kwargs):
        """
        Set the radial locations and labels of the *r* grids.
        The labels will appear at radial distances *radii* at the
        given *angle* in degrees.
        *labels*, if not None, is a ``len(radii)`` list of strings of the
        labels to use at each radius.
        If *labels* is None, the built-in formatter will be used.
        *rpad* is a fraction of the max of *radii* which will pad each of
        the radial labels in the radial direction.
        Return value is a list of tuples (*line*, *label*), where
        *line* is :class:`~matplotlib.lines.Line2D` instances and the
        *label* is :class:`~matplotlib.text.Text` instances.
        kwargs are optional text properties for the labels:
        %(Text)s
        ACCEPTS: sequence of floats
        """
        radii = np.asarray(radii)
        rmin = radii.min()
        if rmin <= 0:
            raise ValueError('radial grids must be strictly positive')
        self.set_yticks(radii)
        if labels is not None:
            self.set_yticklabels(labels)
        elif fmt is not None:
            self.yaxis.set_major_formatter(FormatStrFormatter(fmt))
        if angle is None:
            angle = self._r_label1_position.to_values()[4]
        if rpad is not None:
            self._rpad = rpad
        rmax = self.get_rmax()
        self._r_label1_position.clear().translate(angle, self._rpad * rmax)
        self._r_label2_position.clear().translate(angle, -self._rpad * rmax)
        for t in self.yaxis.get_ticklabels():
            t.update(kwargs)
        return self.yaxis.get_gridlines(), self.yaxis.get_ticklabels()
    def set_xscale(self, scale, *args, **kwargs):
        if scale != 'linear':
            raise NotImplementedError("You can not set the xscale on a polar plot.")
    def set_xlim(self, *args, **kargs):
        self.viewLim.intervalx = (0.0, np.pi * 2.0)
    def format_coord(self, theta, r):
        """
        Return a format string formatting the coordinate using Unicode
        characters.
        """
        theta /= math.pi
        return u'\u03b8=%0.3f\u03c0 (%0.3f\u00b0), r=%0.3f' % (theta, theta * 180.0, r)
    def get_data_ratio(self):
        '''
        Return the aspect ratio of the data itself.  For a polar plot,
        this should always be 1.0
        '''
        return 1.0
    def can_zoom(self):
        """
        Return True if this axes support the zoom box
        """
        return False
    def start_pan(self, x, y, button):
        angle = self._r_label1_position.to_values()[4] / 180.0 * np.pi
        mode = ''
        if button == 1:
            epsilon = np.pi / 45.0
            t, r = self.transData.inverted().transform_point((x, y))
            if t >= angle - epsilon and t <= angle + epsilon:
                mode = 'drag_r_labels'
        elif button == 3:
            mode = 'zoom'
        self._pan_start = cbook.Bunch(
            rmax          = self.get_rmax(),
            trans         = self.transData.frozen(),
            trans_inverse = self.transData.inverted().frozen(),
            r_label_angle = self._r_label1_position.to_values()[4],
            x             = x,
            y             = y,
            mode          = mode
            )
    def end_pan(self):
        del self._pan_start
    def drag_pan(self, button, key, x, y):
        p = self._pan_start
        if p.mode == 'drag_r_labels':
            startt, startr = p.trans_inverse.transform_point((p.x, p.y))
            t, r = p.trans_inverse.transform_point((x, y))
            dt0 = t - startt
            dt1 = startt - t
            if abs(dt1) < abs(dt0):
                dt = abs(dt1) * sign(dt0) * -1.0
            else:
                dt = dt0 * -1.0
            dt = (dt / np.pi) * 180.0
            rpad = self._r_label1_position.to_values()[5]
            self._r_label1_position.clear().translate(
                p.r_label_angle - dt, rpad)
            self._r_label2_position.clear().translate(
                p.r_label_angle - dt, -rpad)
        elif p.mode == 'zoom':
            startt, startr = p.trans_inverse.transform_point((p.x, p.y))
            t, r = p.trans_inverse.transform_point((x, y))
            dr = r - startr
            scale = r / startr
            self.set_rmax(p.rmax / scale)

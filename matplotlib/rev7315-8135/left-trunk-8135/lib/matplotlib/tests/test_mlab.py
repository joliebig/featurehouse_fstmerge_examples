import numpy as np
import matplotlib.mlab as mlab
import tempfile
from nose.tools import raises
def test_colinear_pca():
    a = mlab.PCA._get_colinear()
    pca = mlab.PCA(a)
    assert(np.allclose(pca.fracs[2:], 0.))
    assert(np.allclose(pca.Y[:,2:], 0.))
def test_recarray_csv_roundtrip():
    expected = np.recarray((99,),
                          [('x',np.float),('y',np.float),('t',np.float)])
    expected['x'][0] = 1
    expected['y'][1] = 2
    expected['t'][2] = 3
    fd = tempfile.TemporaryFile(suffix='csv')
    mlab.rec2csv(expected,fd)
    fd.seek(0)
    actual = mlab.csv2rec(fd)
    fd.close()
    assert np.allclose( expected['x'], actual['x'] )
    assert np.allclose( expected['y'], actual['y'] )
    assert np.allclose( expected['t'], actual['t'] )
@raises(ValueError)
def test_rec2csv_bad_shape():
    bad = np.recarray((99,4),[('x',np.float),('y',np.float)])
    fd = tempfile.TemporaryFile(suffix='csv')
    mlab.rec2csv(bad,fd)
def test_prctile():
    x=[1,2,3]
    assert mlab.prctile(x,50)==np.median(x)
    x=[1,2,3,4]
    assert mlab.prctile(x,50)==np.median(x)
    ob1=[1,1,2,2,1,2,4,3,2,2,2,3,4,5,6,7,8,9,7,6,4,5,5]
    p        = [0,   75, 100]
    expected = [1,  5.5,   9]
    actual = mlab.prctile(ob1,p)
    assert np.allclose( expected, actual )
    for pi, expectedi in zip(p,expected):
        actuali = mlab.prctile(ob1,pi)
        assert np.allclose( expectedi, actuali )

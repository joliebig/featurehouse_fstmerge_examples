"""Resource ui_html (from file ui.html)"""
source = 'ui.html'
package = 'spambayes.core_resources'
import os
datafile = os.path.join(os.path.dirname(__file__), source)
data = open(datafile, "rb").read()

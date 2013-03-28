import sys
if sys.platform=='win32':
    raise ImportError("libusb not supported on win32")
from  usb import *
import libusb

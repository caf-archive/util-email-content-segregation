# util-email-content-segregation
A Java module that calls to the Mailgun/Talon library via the Jep interpreter to perform email content segregation.


## Install Instructions (Unix)

1.  Download and run the setup script from the jep-talon-install-script project [found here.](https://github.com/CAFDataProcessing/worker-markup/tree/develop/jep-talon-install-script)
2.  Download the split_email.py python script from the util-email-content-segregation-script project [found here.](https://github.com/CAFDataProcessing/util-email-content-segregation-script)
3.  Set the "PYTHONPATH" environment variable to the directory containing the split_email.py file.

## Install Instructions (Windows)

1.  Download and install Python 2.7 for windows [found here.](https://www.python.org/ftp/python/2.7.11/python-2.7.11.msi) Make sure to install pip as well.
2.  Download [this wheel file](http://www.lfd.uci.edu/~gohlke/pythonlibs/th4jbnf9/lxml-3.6.0-cp27-cp27m-win32.whl) ([64-bit version](http://www.lfd.uci.edu/~gohlke/pythonlibs/th4jbnf9/lxml-3.6.0-cp27-cp27m-win_amd64.whl)) and run `pip install lxml-3.6.0-cp27-cp27m-win_amd64.whl`
3.  Download [this wheel file](http://www.lfd.uci.edu/~gohlke/pythonlibs/th4jbnf9/numpy-1.11.0+mkl-cp27-cp27m-win32.whl) ([64-bit version](http://www.lfd.uci.edu/~gohlke/pythonlibs/th4jbnf9/numpy-1.11.0+mkl-cp27-cp27m-win_amd64.whl)) and run `pip install numpy-1.11.0+mkl-cp27-cp27m-win_amd64.whl`
4.  Download [this wheel file](http://www.lfd.uci.edu/~gohlke/pythonlibs/th4jbnf9/scipy-0.17.0-cp27-none-win32.whl) ([64-bit version](http://www.lfd.uci.edu/~gohlke/pythonlibs/th4jbnf9/scipy-0.17.0-cp27-none-win_amd64.whl)) and run `pip install scipy-0.17.0-cp27-none-win_amd64.whl`
5.  Run `install talon==1.2.6 jep==3.5.2`
6.  Download the split_email.py python script from the util-email-content-segregation-script project [found here.](https://github.com/CAFDataProcessing/util-email-content-segregation-script)
7.  Set the "PYTHONPATH" environment variable to the directory containing the split_email.py file.

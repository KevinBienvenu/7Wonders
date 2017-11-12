# -*- coding: utf-8 -*-
'''
Created on 5 Apr 2017

@author: KevinBienvenu
'''

import codecs
import json, os
from operator import itemgetter
import random
import requests
import time
import pandas as pd
import sys
import numpy as np
import threading
from flask import Flask, request


app = Flask(__name__)

@app.route('/', methods=['POST'])
def hello_world():
    return request.data

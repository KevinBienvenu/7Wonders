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
import sys


path = "C:/Users/Kevin/Documents/GitHub/7Wonders/"


# UTILITY FUNCTIONS
        
def saveJson(dic, filename):
    with codecs.open(filename, "w","utf8") as fichier:
        json.dump(dic, fichier, indent=1, ensure_ascii = False)

def loadJson(filename):
    with codecs.open(filename,"r","utf8") as fichier:
        dic = json.load(fichier)
    return dic

# IMPORTING DATA

def loadSpecificDB(directory):
    pathBuildings = os.path.join(path, "ressources", directory)
    dataBuildings = {}
    for filename in os.listdir(pathBuildings):
        dataBuildings[filename.split(".")[0]] = loadJson(os.path.join(pathBuildings,filename))
    return dataBuildings


def loadDBs():
    data = {}
    for directory in ["buildings","leaders","wonders"]:
        temp = loadSpecificDB(directory)
        if directory == "buildings" or directory =="leaders":
            for key in temp.keys():
                temp[temp[key]["Name"]] = temp[key]
                del temp[key]
        data[directory] = temp 
    data["cards"] = dict(data["buildings"])
    data["cards"].update(data["leaders"])
    return data

def getResourcesNeeded(building):
    needed = {}
    for key in building:
        if key.endswith("Cost") and int(building[key]):
            needed[key.split("Cost")[0].lower()] = int(building[key])
    return needed

def getRessourcesNeededWonder(state, floorNumber, data):
    needed = {}
    wonder = data["wonders"][state["wonderIdName"]]
    for key in wonder:
        if key.startswith(state["wonderFace"]) and str(floorNumber+1) in key and key.endswith("Cost"):
            needed[key[7:-4].lower()] = int(wonder[key])
    return needed
    

def getRessourceCost(res, seller, state):
    cost = 2
    basicRessources = ["bois", "argile", "pierre", "minerai"]
    rareRessources = ["tissu","verre","papyrus"]
    if (seller=="right" and res in basicRessources and "TradeCommonEastEqual1" in state["tradebuildings"]) \
        or (seller=="left" and res in basicRessources and "TradeCommonWestEqual1" in state["tradebuildings"]) \
        or (res in rareRessources and "TradeRareBothEqual1" in state["tradebuildings"]):
        cost = 1
    return cost  

def buildOption(state, option, needed, cheaper=False, cheaper_gold=0): 
    l = {key : len(key.split("-")) for key in state["ressources"]}.items()
    l.sort(key = itemgetter(1))
    coins = state["coins"]
    for res, _ in l:
        tab = res.split("-")
        for subres in tab:
            if subres in needed:
                needed[subres] -= state["ressources"][res]
                if needed[subres]<=0:
                    del needed[subres]
                break
    if "coin" in needed:
        if coins>= needed["coin"]:
            coins -= needed["coin"]
            del needed["coin"]
    # On a tout ce qu'il faut sans commerce
    if not len(needed):
        return True, option
    # On doit acheter aux autres
    resOptions = {"left" : dict(state["ressourcesleft"]), "right" : dict(state["ressourcesright"])}
    possible = True
    bonusCheaper = cheaper
    for res in needed.keys():
        while needed[res]:
            if sum(needed.values())==1 and cheaper:
                break
            minCost = 3
            minSeller = None
            minRes = None
            for seller in resOptions:
                for key in resOptions[seller]:
                    if res in key:
                        cost = getRessourceCost(res, seller, state)
                        if cost<minCost:
                            minCost = cost
                            minSeller = seller
                            minRes = key
            if minSeller and coins>=minCost:
                coins -= minCost
                resOptions[minSeller][minRes] -= 1
                if resOptions[minSeller][minRes] == 0:
                    del resOptions[minSeller][minRes]
                option["trade_"+minSeller] += minCost
            else:
                if bonusCheaper:
                    bonusCheaper = False
                    continue
                possible = False
                break
            needed[res] -= 1
        if not possible:
            break
    return possible, option
        
# STATE HANDLING

def getLeaderPossibilities(state, data):
    options = []
    for card in state["cards"]:
        option = {"card" : card, "trade_right" : 0, "trade_left" : 0, "bilkis" : "", "action" : "leader", "wonderFloor" : "0"}
        options += [option]
    return options

def getBuildingFreePossibilities(state):
    options = []
    action = "build"
    if state["state"] == "StateBuryLeaderChoice":
        action = "bury"
    for card in state["cards"]:
        option = {"card" : card, "trade_right" : 0, "trade_left" : 0, "bilkis" : "", "action" : action, "wonderFloor" : "0"}
        options += [option]
    return options

def getBuildingPossibilities(state, data):
    options = []
    # step 1 : building cards options
    for card in state["cards"]:
        option = {"card" : card, "trade_right" : 0, "trade_left" : 0, "bilkis" : "", "action" : "build", "wonderFloor" : "0"}
        needed = getResourcesNeeded(data["cards"][card])
        cheaper = False
        cheaper_gold = 0
        if card in state["cardoptions"]:
            if "free" == state["cardoptions"][card] or "chain" == state["cardoptions"][card]:
                needed = {}
            if "alreadybuilt" == state["cardoptions"][card]:
                needed["unobtainable"] = 18
            if "cheaper_ressource" == state["cardoptions"][card]:
                cheaper = True
            if "cheaper_gold_2" == state["cardoptions"][card]:
                cheaper_gold = 2
            if "cheaper_gold_1" == state["cardoptions"][card]:
                cheaper_gold = 1
        possible, option = buildOption(state, option, needed, cheaper, cheaper_gold)
        if possible:
            options += [option]
#         options += [{"card" : card, "trade_right" : 0, "trade_left" : 0, "bilkis" : "", "action" : "discard", "wonderFloor" : "0"}]
    # step 2 : building wonder options
    if "ok" in state["wonderFloors"]:
        floorNumber = state["wonderFloors"].index("ok")
        needed = getRessourcesNeededWonder(state,floorNumber,data)
        option = {"card" : state["cards"][0], "trade_right" : 0, "trade_left" : 0, "bilkis" : "", "action" : "wonder", "wonderFloor" : str(floorNumber)}
        cheaper = "CheaperWonder" in state["specialeffects"]
        possible, option = buildOption(state, option, needed, cheaper)
        if possible:
            options += [option]
    return options

def makeDecision(state, options):
    if not options:
        options = [{"card" : state["cards"][0], "trade_right" : 0, "trade_left" : 0, "bilkis" : "", "action" : "discard", "wonderFloor" : "0"}]
    index = random.randint(0,len(options)-1)
    for option in options:
        print option
    print "  ",index
    print
    return options[index]


def createFinalDecision(state, option):
    decision = {}
    for key in option:
        decision[key] = unicode(option[key])
    for key in ["idJoueur", "state", "key"]:
        decision[key] = unicode(state[key])
    decision["done"] = "true"
    return decision

# COMMUNICATION SERVICES

def postDecision(decision):
    url = "http://gameserver-kevinbienvenu.c9users.io/users/playstate"
    requests.post(url, json = decision)
    print "posting : ",decision
    
def getState(name):
    url = "http://gameserver-kevinbienvenu.c9users.io/users/userlist?time="+str(random.randint(0,10000000000))
    l = json.loads(requests.get(url).content)
    l2 = [li for li in l if "name" in li and li["name"]==name]
    state = None
    if l2 and l2[0]["done"]!="true":
        state = l2[0]
        print state
    return state
    
# MAIN PIPELINE
                
def pipeline(idJoueur, data):  
    # Creating user and connecting people
    infos = {}
    name = "IA_"+str(idJoueur)
    password = str(idJoueur)
#     urlConnect = "http://gameserver-kevinbienvenu.c9users.io/users/connect"
#     urlWonder = "http://gameserver-kevinbienvenu.c9users.io/users/launchgame"
#     while "wonder" not in infos:
#         r = requests.post(urlConnect, json={"name" :name, "password" : password})
#         infos = json.loads(r.content)[0]
#         time.sleep(1)
#     r = requests.post(urlWonder, json={"name" : name, "face" : "A"})
    
    # Playing game
    while True:
        time.sleep(1)
        state = getState(name)
        if not state:
            continue
        if state["state"] == "StateLeaderChoice":
            print "leader choice"
            options = getLeaderPossibilities(state, data)
        elif state["state"] == "StateCardChoice":
            print "card choice"
            options = getBuildingPossibilities(state, data)
        else:
            print "other choice"
            options = getBuildingFreePossibilities(state)
        option = makeDecision(state, options)
        decision = createFinalDecision(state, option)
        postDecision(decision)
        
      
      
if __name__=="__main__":
#     stateFile = os.path.join("logs","testState.json")
#     states = json.load(codecs.open(path+"/"+stateFile,"r"))
    data = loadDBs()
    if len(sys.argv)>1:
        idJoueur = int(sys.argv[1])
    else :
        idJoueur = 0
    pipeline(idJoueur, data)
    
        
#     for state in [state for state in states if state["state"]=="StateCardChoice"]:    
#         print len(state["cards"]), len(getBuildingPossibilities(state, data))
#     
        
    
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

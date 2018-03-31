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

categories = {"action" : ["build","freebuild","discard",
                          "wonder","leader","bury"],
              "ressources" : ["bois","argile","pierre","minerai",
                              "papyrus","verre","tissu"],
              "tokenNumber" : ["VictoryToken1","VictoryToken3","VictoryToken5",
                               "DefeatToken1","Engrenage","Compas",
                               "Tablette","Military"],
              "cardNumber" : ["Commercial","Scientific","Military","Civilian",
                              "Guilds","CommonRessources","RareRessources","Leader"],
              "wonder" : ["gizeh","halicarnasse","olympie","abousimbel","alexandrie",
                          "babylone","ephese","petra","rhodes","rome"]
              }


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
    if "ok" in state["wonderFloors"]:
        floorNumber = state["wonderFloors"].index("ok")
        needed = getRessourcesNeededWonder(state,floorNumber,data)
        option = {"card" : state["cards"][0], "trade_right" : 0, "trade_left" : 0, "bilkis" : "", "action" : "wonder", "wonderFloor" : str(floorNumber)}
        cheaper = "CheaperWonder" in state["specialeffects"]
        possible, option = buildOption(state, option, needed, cheaper)
        if possible:
            options += [option]
    return options

def makeDecision(state, options, isAI=False):
    if not options:
        options = [{"card" : state["cards"][0], "trade_right" : 0, "trade_left" : 0, "bilkis" : "", "action" : "discard", "wonderFloor" : "0"}]
    if isAI:
        index = 0
        score = 0
        for option, i in zip(options, range(len(options))):
            v = convertStateDecisionToVector(state, option)
            v["time"] = int(state["time"])
            df = pd.DataFrame.from_dict(v, orient="index")
            col = df.columns.values
            col.sort()
            df = df[col]
            X = df.values
            temp_score = nn.feedForward(X)
            if temp_score>score:
                index = i
                score = temp_score
    else:
        index = random.randint(0,len(options)-1)
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
    else:
        raise Exception("mythe")
    return state
    
# MAIN PIPELINE
                
def pipeline(idJoueur, isAI = False):  
    # Creating user and connecting people
    infos = {}
    name = "IA_"+str(idJoueur)
    password = str(idJoueur)
    # Playing game
    while True:
        time.sleep(0.1)
        try:
            state = getState(name)
            decision = processState(state, isAI)
            postDecision(decision)
        except:
            continue

def processState(state, isAI):
    if state["state"] == "StateLeaderChoice":
        print "leader choice"
        options = getLeaderPossibilities(state, data)
    elif state["state"] == "StateCardChoice":
        print "card choice"
        options = getBuildingPossibilities(state, data)
    else:
        print "other choice"
        options = getBuildingFreePossibilities(state)
    option = makeDecision(state, options, isAI)
    decision = createFinalDecision(state, option)
    return decision
        
def mainIA(idJoueur, isAI = False):
    state = getState(name)
    if state["state"] == "StateLeaderChoice":
        print "leader choice"
        options = getLeaderPossibilities(state, data)
    elif state["state"] == "StateCardChoice":
        print "card choice"
        options = getBuildingPossibilities(state, data)
    else:
        print "other choice"
        options = getBuildingFreePossibilities(state)
    option = makeDecision(state, options, isAI)
    decision = createFinalDecision(state, option)
    postDecision(decision)
        



def getFilesToPreprocess():
    if os.path.isfile(os.path.join(path, "logs","ia","raw_data.json")):
        data = loadJson(os.path.join(path, "logs","ia","raw_data.json"))
    else:
        data = {}
    l = [li for li in os.listdir(os.path.join(path, "logs")) if (li.startswith("test") and int(li.split("_")[-1].split(".")[0])>=infos["lastFileNumber"])]
    numberSet = set([int(li.split("_")[-1].split(".")[0]) for li in l])
    numberSet.remove(max(numberSet))
    print len(numberSet), "nouvelles parties détectées"
    for fileNumber in numberSet:
        states = loadJson(os.path.join(path, "logs","testState_"+str(fileNumber)+".json"))
        decisions = loadJson(os.path.join(path, "logs","testDecision_"+str(fileNumber)+".json"))
        data[fileNumber] = {"states" : states, "decisions" : decisions}
        os.remove(os.path.join(path, "logs","testState_"+str(fileNumber)+".json"))
        os.remove(os.path.join(path, "logs","testDecision_"+str(fileNumber)+".json"))
    saveJson(data, os.path.join(path, "logs","ia","raw_data.json"))
   
def convertStateDecisionToVector(state, decision):
    vector = {"face_A" : 0, "face_B" : 0}
    for category in categories:
        for key in categories[category]:
            vector[category+"_"+key] = 0
        if category not in state:
            continue
        for key in state[category]:
            if category == "ressources" and "-" in key:
                continue
            vector[category+"_"+key] += state[category][key]
    for card in dicIdCards:
        vector["idcard_"+card] = (1 if card == decision["card"] else 0)
    for res in [r for r in state["ressources"] if "-" in r]:
        tab = res.split("-")
        for r in tab:
            vector["ressources_"+r] += 1
    vector["trade_left"] = 0 if "trade_left" not in decision else int(decision["trade_left"])
    vector["trade_right"] = 0 if "trade_right" not in decision else int(decision["trade_right"])
    vector["coins"] = state["coins"]
    vector["action_"+decision["action"]] += 1
    vector["wonder_"+state["wonderIdName"]] += 1
    vector["face_"+state["wonderFace"]] += 1
    vector["wonderfloorbuilt"] = len([t for t in state["wonderFloors"] if t=="alreadybuilt"])
    for cat in categories["cardNumber"]:
        vector["cardtype_"+cat] = 0
    if decision["action"] == "build" or decision["action"] == "leader":
        vector["cardtype_"+data["cards"][decision["card"]]["CategoryName"]] = 1
    if "score" in decision:
        vector["y"] = decision["score"] 
    return vector

def transformRawData():
    rawData = loadJson(os.path.join(path, "logs","ia","raw_data.json"))
    nbToDo = len(rawData)
    nbDone = 0
    dicInfos = {}
    nn = neural_network(52,200,0.7,0.7)
    for game in rawData:
        scores = set([d["score"] for d in rawData[game]["decisions"]])
        idJoueurs = set([str(d["idJoueur"]) for d in rawData[game]["decisions"]])
        dicScores = {key : (1.0*(key-min(scores))/(max(scores)-min(scores)))**2 for key in scores}
        for decision in rawData[game]["decisions"]:
            decision["score"] = dicScores[decision["score"]]
        dicInfos = {str(idJoueur) : {} for idJoueur in idJoueurs}
        for idJoueur in idJoueurs:
            for state, decision in zip([s for s in rawData[game]["states"] if str(s["idJoueur"]) == idJoueur],
                                       [d for d in rawData[game]["decisions"] if str(d["idJoueur"]) == idJoueur]):
                dicInfos[idJoueur][len(dicInfos[idJoueur])] = convertStateDecisionToVector(state, decision)
            df = pd.DataFrame.from_dict(dicInfos[idJoueur], orient="index")
            df.index.name = "time"
            df.reset_index(inplace=True)
            cols = df.columns
            cols.sort()
            df = df[cols]
            df.to_csv(os.path.join(path, "logs","ia","games","game_"+game+"_"+idJoueur+".csv"),encoding="utf8")
        learnFromGame(nn, game)
        nbDone += 1
        print nbDone,"/",nbToDo
    nn.saveWeights("test0")
    
def learnAtGameEnd(game):
    nn = neural_network(169,200,0.7,0.7,filename="test0")
    states = loadJson(os.path.join(path, "logs","testState_"+str(game)+".json"))
    decisions = loadJson(os.path.join(path, "logs","testDecision_"+str(game)+".json"))
    scores = set([d["score"] for d in decisions])
    idJoueurs = set([str(d["idJoueur"]) for d in decisions])
    dicScores = {key : (1.0*(key-min(scores))/(max(scores)-min(scores)))**2 for key in scores}
    for decision in decisions:
        decision["score"] = dicScores[decision["score"]]
    dicInfos = {str(idJoueur) : {} for idJoueur in idJoueurs}
    for idJoueur in idJoueurs:
        for state, decision in zip([s for s in states if str(s["idJoueur"]) == idJoueur],
                                   [d for d in decisions if str(d["idJoueur"]) == idJoueur]):
            dicInfos[idJoueur][len(dicInfos[idJoueur])] = convertStateDecisionToVector(state, decision)
        df = pd.DataFrame.from_dict(dicInfos[idJoueur], orient="index")
        cols = df.columns.values
        cols.sort()
        df = df[cols]
        df.to_csv(os.path.join(path, "logs","ia","games","game_"+game+"_"+idJoueur+".csv"),encoding="utf8")
    learnFromGame(nn, game)
    nn.saveWeights("test0")

def sigmoid(x):
    return 1 / (1 + np.exp(-x))

def dsigmoid(y):
    return y * (1.0 - y)  

class neural_network():
    def __init__(self, nb_input, nb_hidden, alpha, lambd, filename=""):
        """
        :param input: number of input neurons
        :param hidden: number of hidden neurons
        :param output: number of output neurons
        """
        self.nb_input = nb_input+1 # add 1 for bias node
        self.nb_hidden = nb_hidden
        self.alpha = alpha
        self.lambd = lambd
        # set up array of 1s for activations
        self.ai = [1.0] * self.nb_input
        self.ah = [1.0] * self.nb_hidden
        self.ao = 1.0
        # create randomized weights
        if filename:
            self.wi = np.array(pd.read_csv(path+"logs/ia/"+filename+"_wi.csv"))
            self.wo = np.array(pd.read_csv(path+"logs/ia/"+filename+"_wo.csv"))
        else:
            self.wi = np.random.randn(self.nb_input, self.nb_hidden) 
            self.wo = np.random.randn(self.nb_hidden, 1) 
    def feedForward(self, X):
        Xbiaised = np.concatenate((X,[[1.0]]), 0)
        return sigmoid(sigmoid(Xbiaised.transpose().dot(self.wi)).dot(self.wo))[0][0]
    def updateWeights(self, X, yReal):
        si = np.zeros((self.nb_input, self.nb_hidden))
        so = np.zeros((self.nb_hidden,1))
        for t in range(X.shape[1]):
            Xbiaised = np.concatenate((X[:,t:t+1],[[1.0]]), 0)
            h = sigmoid(Xbiaised.transpose().dot(self.wi))
            y = sigmoid(h.dot(self.wo))[0][0]
            deltaWo = (sigmoid(h)*y*(1-y)).transpose()
            so = deltaWo + self.lambd * so
            deltaWi = Xbiaised.dot(dsigmoid(h) * self.wo.transpose())
            si = deltaWi + self.lambd * si
        self.wi = self.wi + self.alpha*(yReal - y)*si
        self.wo = self.wo + self.alpha*(yReal - y)*so
    def saveWeights(self, filename):
        pd.DataFrame(self.wo).to_csv(path+"logs/ia/"+filename+"_wo.csv", index=False)
        pd.DataFrame(self.wi).to_csv(path+"logs/ia/"+filename+"_wi.csv", index=False)

def learnFromGame(nn, game):
    folder = path + "logs/ia/games/"
    filenames = [f for f in os.listdir(folder) if game in f]
    for filename in filenames:
        gameData = pd.read_csv(folder+"/"+filename)
        y = gameData.y.values[0]
        del gameData["y"]
        X = gameData.values.transpose()
        nn.updateWeights(X,y)

data = loadDBs()
dicIdCards = loadJson(path+"logs/ia/dicIdCards.json").keys()
nn = neural_network(169,200,0.7,0.7,filename="test0")
app = Flask(__name__)

@app.route('/', methods=['POST'])
def IA_route():
    state = json.loads(request.data)
    decision = {}
    if "done" in state and state["done"]!="true":
        decision = processState(state, isAI=True)
    return json.dumps(decision, ensure_ascii=False).encode('utf8')
    
if __name__=="__main__":

    if len(sys.argv)>1:
        if sys.argv[1] == "learn":
            game = sys.argv[2]
            learnAtGameEnd(game)
        elif sys.argv[1] == "ia":
            l = json.loads(sys.argv[2])
            print l
            pass
        else:
            nbJoueurAI = int(sys.argv[1])
            nbJoueurRand = int(sys.argv[2])

            for i in range(nbJoueurAI):
                threading.Thread(target=pipeline, args=(i, True)).start()
            for i in range(nbJoueurAI, nbJoueurAI+nbJoueurRand):
                threading.Thread(target=pipeline, args=(i)).start()
    else:
        app.run()
        
    

                
            

 
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

# -*- coding: utf-8 -*-
'''
Created on 01 Oct 2017

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



path = "C:/Users/Kevin/Documents/GitHub/7Wonders/"

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

# UTILITY FUNCTIONS
        
def saveJson(dic, filename):
    with codecs.open(filename, "w","utf8") as fichier:
        json.dump(dic, fichier, indent=1, ensure_ascii = False)

def loadJson(filename):
    with codecs.open(filename,"r","utf8") as fichier:
        dic = json.load(fichier)
    return dic

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

data = loadDBs()


def getFilesToPreprocess():
    infos = loadJson(os.path.join(path, "logs","ia","ia_params.json"))
    if os.path.isfile(os.path.join(path, "logs","ia","raw_data.json")):
        data = loadJson(os.path.join(path, "logs","ia","raw_data.json"))
    else:
        data = {}
    l = [li for li in os.listdir(os.path.join(path, "logs")) if (li.startswith("test") and int(li.split("_")[-1].split(".")[0])>=infos["lastFileNumber"])]
    numberSet = set([int(li.split("_")[-1].split(".")[0]) for li in l])
    infos["lastFileNumber"] = max(numberSet)
    numberSet.remove(max(numberSet))
    print len(numberSet), "nouvelles parties détectées"
    for fileNumber in numberSet:
        states = loadJson(os.path.join(path, "logs","testState_"+str(fileNumber)+".json"))
        decisions = loadJson(os.path.join(path, "logs","testDecision_"+str(fileNumber)+".json"))
        data[fileNumber] = {"states" : states, "decisions" : decisions}
        os.remove(os.path.join(path, "logs","testState_"+str(fileNumber)+".json"))
        os.remove(os.path.join(path, "logs","testDecision_"+str(fileNumber)+".json"))
    saveJson(data, os.path.join(path, "logs","ia","raw_data.json"))
    saveJson(infos, os.path.join(path, "logs","ia","ia_params.json"))
   
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
    for res in [r for r in state["ressources"] if "-" in r]:
        tab = res.split("-")
        for r in tab:
            vector["ressources_"+r] += 1
    vector["coins"] = state["coins"]
    vector["action_"+decision["action"]] += 1
    vector["wonder_"+state["wonderIdName"]] += 1
    vector["face_"+state["wonderFace"]] += 1
    vector["wonderfloorbuilt"] = len([t for t in state["wonderFloors"] if t=="alreadybuilt"])
    for cat in categories["cardNumber"]:
        vector["cardtype_"+cat] = 0
    if decision["action"] == "build" or decision["action"] == "leader":
        vector["cardtype_"+data["cards"][decision["card"]]["CategoryName"]] = 1
    vector["y"] = decision["score"] 
    return vector

def transformRawData():
    rawData = loadJson(os.path.join(path, "logs","ia","raw_data.json"))
    nbToDo = len(rawData)
    nbDone = 0
    dicInfos = {}
    for game in rawData:
        scores = set([d["score"] for d in rawData[game]["decisions"]])
        idJoueurs = set([d["idJoueur"] for d in rawData[game]["decisions"]])
        dicScores = {key : (1.0*(key-min(scores))/(max(scores)-min(scores)))**2 for key in scores}
        for decision in rawData[game]["decisions"]:
            decision["score"] = dicScores[decision["score"]]
        dicInfos = {str(idJoueur) : {} for idJoueur in idJoueurs}
        for state, decision in zip(rawData[game]["states"], rawData[game]["decisions"]):
            idJoueur = str(state["idJoueur"])
            dicInfos[idJoueur][len(dicInfos[idJoueur])] = convertStateDecisionToVector(state, decision)
        for idJoueur in idJoueurs:
            df = pd.DataFrame.from_dict(dicInfos[idJoueur], orient="index")
            df.index.name = "time"
            df.to_csv(os.path.join(path, "logs","ia","games","game_"+game+"_"+idJoueur+".csv"),encoding="utf8")
        nbDone += 1
        print nbDone,"/",nbToDo
        
        
if __name__ == "__main__":
    getFilesToPreprocess()
    transformRawData()
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
#!/usr/bin/python

import os
import sys
import json
import re
import time
from zipfile import ZipFile

class HARArchiver():
    """Archive json files received by the HumanActivityRecorder App
    """
    def __init__(self):
        self.srcDir="server"
        self.dstDir=os.path.abspath("server/archive")
        self.filestozip={}
        self.deldir="/tmp"
    def archive(self):
        jsonre=re.compile("^[a-z]+\_([0-9]+)\_[0-9\:]+.+\.json$")
        currentsecs=time.mktime(time.localtime())
        zipthreshold=86400*7
        for f in os.listdir(self.srcDir):
            m = jsonre.match(f)
            if m:
                fsecs=time.mktime(time.strptime(m.group(1),"%Y%m%d"))
                with open(os.path.join(self.srcDir,f), "r") as fd:
                    self.json = json.loads(fd.read())
                if (currentsecs - fsecs > zipthreshold):
                    weekstr=m.group(1)
                    if not weekstr in self.filestozip.keys():
                        self.filestozip[weekstr] = []
                    self.filestozip[weekstr].append(f)
                    
                csv = self.getCsvName()
                #print(self.json["gyr"])
                with open(csv,"w") as fd:
                    accI = gyrI = 0
                    mycsv=[]
                    f=0.0
                    fd.write(",attitude.roll,attitude.pitch,attitude.yaw,gravity.x,gravity.y,gravity.z,rotationRate.x,rotationRate.y,rotationRate.z,userAcceleration.x,userAcceleration.y,userAcceleration.z\n")
                    for i in range(len(self.json["acc"])):
                        gyr=self.json["gyr"][i]
                        acc=self.json["acc"][i]
                        # TODO Match the right datasets to each other. - For now we assume sensors are in sync (in reality they are not)
                        try:
                            mycsv.append("%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f\n" % (i,gyr[1],gyr[2],gyr[3],f,f,f,f,f,f,acc[1],acc[2],acc[3]))
                        except:
                            pass
                    fd.writelines(mycsv)

    def getCsvName(self):
        ## Hint: wlk == 2
        activities = ["dws", "ups", "wlk", "jog", "std", "sit"]
        try:
            actStr = activities[int(self.json["activityID"])]
        except:
            print("Setting activity to unknown for activityID %s", self.json["activityID"] )
            actStr = "unk"
        # TODO Find the "right" number for activity dir
        instanceStr = "_1"
        actDir=os.path.join(self.dstDir,actStr + instanceStr)
        try:
            os.mkdir(actDir)
        except FileExistsError: 
            pass
        csvFile=os.path.join(actDir,"sub_" + str(self.json["subjectID"]) + ".csv")
        return csvFile
    def compress(self):
        os.chdir(self.srcDir)
        for zipstr in self.filestozip.keys():
            for f in self.filestozip[zipstr]:
                with ZipFile(os.path.join(self.dstDir,zipstr+".zip"),"w") as z:
                    print("Adding %s to %s.zip" % (f,zipstr))
                    z.write(f)
                os.rename(f,os.path.join(self.deldir,f))

if __name__ == "__main__":
    har = HARArchiver()
    har.archive()
    har.compress()

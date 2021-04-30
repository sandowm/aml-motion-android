#!/usr/bin/python3

# 
# Convert uploaded json files to ML friedly directory structure and csv
#

import os
import sys
import json
import re
import time
from zipfile import ZipFile
import shutil


class HARArchiver():
    """Archive json files received by the HumanActivityRecorder App
    """

    def __init__(self):
        self.srcDir="/var/www/uploads"
        self.dstDir=os.path.abspath("/var/www/unibe.sandow.cc/archive")
        self.filestozip={}
        self.deldir="/tmp"
    def archive(self):
        jsonre=re.compile("^[a-z]+\_([0-9]+)\_[0-9\:]+.+\.json$")
        currentsecs=time.mktime(time.localtime())
        zipthreshold=1000 # 86400*7
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
                    x=0.0
                    fd.write(",attitude.roll,attitude.pitch,attitude.yaw,gravity.x,gravity.y,gravity.z,rotationRate.x,rotationRate.y,rotationRate.z,userAcceleration.x,userAcceleration.y,userAcceleration.z\n")
                    if "gyr" in self.json.keys() and "acc" in self.json.keys():
                        for i in range(len(self.json["acc"])):
                          acc=self.json["acc"][i]
                          try:
                            gyr=self.json["gyr"][i]
                          except:
                            gyr=0.0
                          # TODO Match the right datasets to each other. - For now we assume sensors are in sync
                          try:
                              mycsv.append("%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f\n" % (i,gyr[1],gyr[2],gyr[3],x,x,x,x,x,x,acc[1],acc[2],acc[3]))
                          except:
                              pass
                    fd.writelines(mycsv)
                # Just so we have all files as csv
                shutil.copy(csv,os.path.join(self.dstDir,"csv",f))

    def getCsvName(self):
        ## Hint: wlk == 2
        activities = ["dws", "ups", "wlk", "jog", "std", "sit"]
        if "activityID" not in self.json.keys(): self.json["activityID"] = "99"
        if "subjectID" not in self.json.keys(): self.json["subjectID"] = "99"

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
            with ZipFile(os.path.join(self.dstDir,zipstr+".zip"),"w") as z:
                for f in self.filestozip[zipstr]:
                    print("Adding %s to %s.zip" % (f,zipstr))
                    z.write(f)
                os.rename(f,os.path.join(self.deldir,f))

if __name__ == "__main__":
    har = HARArchiver()
    har.archive()
    har.compress()


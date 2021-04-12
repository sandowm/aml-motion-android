#!/usr/bin/python

# 
# Convert uploaded json files to ML friedly directory structure and csv
#

import os
import sys
import json

class HARArchiver():
    """Archive json files received by the HumanActivityRecorder App
    """

    def __init__(self):
        self.srcDir="server"
        self.dstDir="server/archive"
    def archive(self):
        with open("server/lala.json", "r") as fd:
            self.json = json.loads(fd.read())
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
                # TODO Match the right datasets to each other. - For now we assume sensors are in sync
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

if __name__ == "__main__":
    har = HARArchiver()
    har.archive()

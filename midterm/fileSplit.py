__author__ = 'jingyu'
import os
import shutil

k = 0
startIndex = 8
trainPath = './train'
testPath = './test'
pattern = '*.jpg'

for dir, dirname, filenames in os.walk(trainPath):
    preDir = dir[startIndex:]
    for filename in filenames:
        # Find some file
        # Find some file
        trainFile = os.path.join(trainPath, preDir)
        testFile = os.path.join(testPath, preDir)
        k = k + 1

        # to different foils
        if k % 5 == 0:
            shutil.copy2(os.path.join(dir, filename), testFile)
        else:
            shutil.copy2(os.path.join(dir, filename), trainFile)



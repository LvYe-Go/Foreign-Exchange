__author__ = 'jingyu'

import scipy
import cv2
# Use sift implementation to extract feature
# Reference :
# # http://opencv-python-tutroals.readthedocs.org/en/latest/py_tutorials/py_feature2d/py_sift_intro/py_sift_intro.html
descriptors = 128
def getSift(nameFileImage):
    img = cv2.imread(nameFileImage)
    gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    sift = cv2.SIFT()
    k,des = sift.detectAndCompute(gray, None)
    # Number of keypoints * 128
    if des is None:
        return [0] * descriptors
    # get the sum if not none
    else:
        res = [0] * descriptors
        for item in des:
            for i in range(len(item)):
                res[i] += item[i]
        return res

def getSiftCount(nameFileImage):
    img = cv2.imread(nameFileImage)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    sift = cv2.SIFT()
    k, des = sift.detectAndCompute(gray, None)
    if des is None:
        return 0
    else:
        return len(k)
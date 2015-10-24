__author__ = 'jingyu'

#Import libraries for doing image analysis

from skimage.io import imread
from skimage.transform import resize
import glob
import os
from skimage import measure
from skimage import morphology
import numpy as np
import getSiftFeature
import warnings
import scipy

warnings.filterwarnings("ignore")
# get the classnames from the directory structure
directory_names = list(set(glob.glob(os.path.join( "train", "*")))
                  .difference(set(glob.glob(os.path.join( "train", "*.*")))));

# Reference:
# https://www.kaggle.com/c/datasciencebowl/details/tutorial

def getLargestRegion(props, labelmap, imagethres):
    regionmaxprop = None
    for regionprop in props:
        # check to see if the region is at least 50% nonzero
        if sum(imagethres[labelmap == regionprop.label])*1.0/regionprop.area < 0.50:
            continue
        if regionmaxprop is None:
            regionmaxprop = regionprop
        if regionmaxprop.filled_area < regionprop.filled_area:
            regionmaxprop = regionprop
    return regionmaxprop

def getMinorMajorRatio(image):
    image = image.copy()
    # Create the thresholded image to eliminate some of the background
    imagethr = np.where(image > np.mean(image),0.,1.0)

    #Dilate the image
    imdilated = morphology.dilation(imagethr, np.ones((4,4)))

    # Create the label list
    label_list = measure.label(imdilated)
    label_list = imagethr*label_list
    label_list = label_list.astype(int)

    region_list = measure.regionprops(label_list)
    maxregion = getLargestRegion(region_list, label_list, imagethr)

    # guard against cases where the segmentation fails by providing zeros
    ratio = 0.0
    if ((not maxregion is None) and  (maxregion.major_axis_length != 0.0)):
        ratio = 0.0 if maxregion is None else  maxregion.minor_axis_length*1.0 / maxregion.major_axis_length
    return ratio

# Rescale the images and create the combined metrics and training labels

#get the total training images
def getNumberofImages():
    numberofImages = 0
    for folder in directory_names:
        for fileNameDir in os.walk(folder):
            for fileName in fileNameDir[2]:
                 # Only read in the images
                if fileName[-4:] != ".jpg":
                  continue
                numberofImages += 1
    return numberofImages
                # We'll rescale the images to be 25x25

maxPixel = 25
imageSize = maxPixel * maxPixel
num_rows = getNumberofImages() # one row for each image in the training dataset
num_features = imageSize + 2 + 128 # for our ratio
X = np.zeros((num_rows, num_features), dtype=float)
# y is the numeric class label
y = np.zeros((num_rows))
files = []

print "Reading images"
def readImage():

    label = 0
    # List of string of class names
    namesClasses = list()
    i = 0
    for folder in directory_names:
        # Append the string class name for each class
        currentClass = folder.split(os.pathsep)[-1]
        namesClasses.append(currentClass)
        for fileNameDir in os.walk(folder):
            for fileName in fileNameDir[2]:
                # Only read in the images
                if fileName[-4:] != ".jpg":
                  continue

                # Read in the images and create the features
                nameFileImage = "{0}{1}{2}".format(fileNameDir[0], os.sep, fileName)

                image = imread(nameFileImage, as_grey=True)

                files.append(nameFileImage)

                axisratio = getMinorMajorRatio(image)

                image = resize(image, (maxPixel, maxPixel))
                des = getSiftFeature.getSift(nameFileImage)

                # Store the rescaled image pixels and the axis ratio
                X[i, 0:imageSize] = np.reshape(image, (1, imageSize))
                X[i, imageSize] = axisratio
                X[i, imageSize+1] = getSiftFeature.getSiftCount(nameFileImage) # store the feature count
                X[i, imageSize+2:imageSize + 2 + 128] = des # store the features

                # Store the class label
                y[i] = label
                i += 1
                # report progress for each 5% done
                report = [int((j+1)*num_rows/20.) for j in range(20)]
                if i in report: print np.ceil(i *100.0 / num_rows), "% done"
    label += 1


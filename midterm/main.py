__author__ = 'jingyu'
#Import libraries for doing image analysis
import glob
import os
import numpy as np
import parseImage
import test
import train

def main():
    print "In Main Experiment\n"
    # get the classnames from the directory structure
    directory_names = list(set(glob.glob(os.path.join("train", "*"))).difference(set(glob.glob(os.path.join("train", "*.*")))))
    # get the number of rows through image count
    numberofImages = parseImage.gestNumberofImages(directory_names)
    num_rows = numberofImages # one row for each image in the training dataset

    # We'll rescale the images to be 25x25
    maxPixel = 25
    imageSize = maxPixel * maxPixel
    num_features = imageSize + 2 + 128 # for our ratio

    X = np.zeros((num_rows, num_features), dtype=float)
    y = np.zeros((num_rows)) # numeric class label
    files = []
    namesClasses = list() #class name list

    # Get the image training data
    parseImage.readImage(True, namesClasses, directory_names,X, y, files)

    print "Training"

    # get test result
    train.train(X, y, namesClasses)

    print "Testing"
    test.test(num_rows, num_features, X, y, namesClasses = list())

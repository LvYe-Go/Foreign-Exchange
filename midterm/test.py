__author__ = 'jingyu'

from sklearn.ensemble import RandomForestClassifier as RF
import glob
import os
import numpy as np
import parseImage
import sys
sys.path.append('/usr/local/lib/python2.7/site-packages')

def test(num_rows, num_features, X, y, namesClasses = list()):
    print "Testing"
    # get the classnames from the directory structure
    directory_names = list(set(glob.glob(os.path.join("test", "*"))).difference(set(glob.glob(os.path.join("test", "*.*")))))

    # X is the feature vector with one row of features per image
    # consisting of the pixel values and our metric
    X_test = np.zeros((num_rows, num_features), dtype=float)
    # y is the numeric class label
    y_test = np.zeros((num_rows))
    true_res = list()

    files = []
    parseImage.readImage(False,list(), directory_names,
                 X_test, y_test,files,  true_res)

    # Random forest build test 
    clf = RF(n_estimators=100, n_jobs=3)
    clf.fit(X, y)

    y_predict = clf.predict(X_test)
    total = 0

    resLen = len( true_res)

    writer = open('result.txt', 'w')
    
    for cur in range(resLen): 
        real_res = namesClasses[int(y_predict[cur])]
        writer.write(real_res + "    "+ true_res[cur] + "\n")
        if  true_res[cur] == real_res :
            total += 1

    print float(total)/float(resLen)
    writer.close()
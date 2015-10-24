__author__ = 'jingyu'

from sklearn.ensemble import RandomForestClassifier as RF
from sklearn import cross_validation
from sklearn.cross_validation import StratifiedKFold as KFold
from sklearn.metrics import classification_report
import numpy as np

#reference : https://www.kaggle.com/c/datasciencebowl/details/tutorial [14]
def train(X, y, namesClasses):
    print "Training"
    # n_estimators is the number of decision trees
    # max_features also known as m_try is set to the default value of the square root of the number of features
    clf = RF(n_estimators=100, n_jobs = 2)
    scores = cross_validation.cross_val_score(clf, X, y, cv=2, n_jobs=1)
    print "Accuracy of all classes"
    print np.mean(scores)

    kf = KFold(y, n_folds=5)
    y_pred = y * 0
    for train, test in kf:
        X_train, X_test, y_train, y_test = X[train,:], X[test,:], y[train], y[test]
        clf = RF(n_estimators=100, n_jobs=3)
        clf.fit(X_train, y_train)
        y_pred[test] = clf.predict(X_test)
    print classification_report(y, y_pred, target_names=namesClasses)

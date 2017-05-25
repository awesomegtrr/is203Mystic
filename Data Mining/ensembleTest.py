import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import xgboost as xgb

import pylab
import csv
import datetime
import math
import re
import time
import random
import os

from pandas.tseries.offsets import *
from operator import *

from sklearn.cross_validation import train_test_split
from sklearn.metrics import mean_squared_error



def ToWeight(y):
    w = np.zeros(y.shape, dtype=float)
    ind = y != 0
    w[ind] = 1./(y[ind]**2)
    return w

def rmspe(yhat, y):
    w = ToWeight(y)
    rmspe = np.sqrt(np.mean( w * (y - yhat)**2 ))
    return rmspe

def rmspe_xg(yhat, y):
    # y = y.values
    y = y.get_label()
    y = np.exp(y) - 1
    yhat = np.exp(yhat) - 1
    w = ToWeight(y)
    rmspe = np.sqrt(np.mean(w * (y - yhat)**2))
    return "rmspe", rmspe



seed = 42

nrows = None
rounds = 250 #set number of rounds for algorithm to take for both training and testing

start_time = time.time()

df_train = pd.read_csv('data/train.csv', 
                       nrows=nrows,
                       parse_dates=['Date'],
                       date_parser=(lambda dt: pd.to_datetime(dt, format='%Y-%m-%d')))

nrows = nrows

df_submit = pd.read_csv('data/test.csv', 
                        nrows=nrows,
                        parse_dates=['Date'],
                        date_parser=(lambda dt: pd.to_datetime(dt, format='%Y-%m-%d')))

df_train['Set'] = 1
df_submit['Set'] = 0

frames = [df_train, df_submit]
df = pd.concat(frames)

features_x = ['Store', 'Date', 'DayOfWeek', 'Open', 'Promo', 'SchoolHoliday', 'StateHoliday','pharmaopen']
features_y = ['SalesLog']

df = df.loc[~((df['Open'] == 1) & (df['Sales'] == 0))]
df.loc[df['Set'] == 1, 'SalesLog'] = np.log1p(df.loc[df['Set'] == 1]['Sales']) # = np.log(df['Sales'] + 1)
df['StateHoliday'] = df['StateHoliday'].astype('category').cat.codes #Make state holidays into categories

#create own categories by splitting the dates into day, week, month, year and day of the year
var_name = 'Date'

df[var_name + 'Day'] = pd.Index(df[var_name]).day
df[var_name + 'Week'] = pd.Index(df[var_name]).week
df[var_name + 'Month'] = pd.Index(df[var_name]).month
df[var_name + 'Year'] = pd.Index(df[var_name]).year
df[var_name + 'DayOfYear'] = pd.Index(df[var_name]).dayofyear


#fill up blanks with zeroes
df[var_name + 'Day'] = df[var_name + 'Day'].fillna(0)
df[var_name + 'Week'] = df[var_name + 'Week'].fillna(0)
df[var_name + 'Month'] = df[var_name + 'Month'].fillna(0)
df[var_name + 'Year'] = df[var_name + 'Year'].fillna(0)
df[var_name + 'DayOfYear'] = df[var_name + 'DayOfYear'].fillna(0)

features_x.remove(var_name)
features_x.append(var_name + 'Day')
features_x.append(var_name + 'Week')
features_x.append(var_name + 'Month')
features_x.append(var_name + 'Year')
features_x.append(var_name + 'DayOfYear')

#turn date into float
df['DateInt'] = df['Date'].astype(np.int64)
for feature in features_x:
    df[feature] = df[feature].fillna(-1)

############ Split Data into Training and Test for Filling in the Outliers ######################
X_train, X_test, y_train, y_test = train_test_split(df.loc[(df['Set'] == 1) & (df['Open'] == 1)][features_x],
                                                    df.loc[(df['Set'] == 1) & (df['Open'] == 1)][features_y],
                                                    test_size=0.1, random_state=seed) #test size is on 10% of data set

# y_train['SalesLog']
# dtrain = xgb.DMatrix(X_train, y_train)
# dtrain.save_binary("train.buffer")
# dtest = xgb.DMatrix(X_test, y_test)
# dtest.save_binary("test.buffer")

num_round = 10 #20000 #number of rounds for the algorithm to take
# evallist = [(dtrain, 'train'), (dtest, 'test')]

param = {'bst:max_depth':12, #max depth of a tree. Controls overfitting as higher depth will allow model to learn relations very specific to a particular sample.
         'bst:eta':0.01, #learning rate. Makes model more robust by shrinking weights on each step
         'subsample':0.8, #denotes the fraction of observations to be randomly sampled for each tree. Lower values make algo more conservative, prevents overfitting but too small might lead to overfitting
         'colsample_bytree':0.7, #denotes fraction of columns to be randomly sampled for each tree
         'silent':1, #silent mode actiate if 1. 0 activates messages, which helps to understand the model
         'objective':'reg:linear', #defines loss function to be minimized. Could be binary:logstic (returns predicted probability, not class), multi:softprob (returns predicted probability of each data point belonging to each class), multi:softmax (returns predicted class, not probabilities). Add num_class parameter.
         # 'nthread':-1, #manually entereed, or leave this blank to run on all cores available.
         'seed':seed} #random seed number, can be used for generating reproducible results and for parameter tuning


         #gamma - split only when resulting split gives positive reduction in loss function. Makes algorithm conservative. Values can vary depending on the loss function and should be tuned.
         #lambda - L2 regularization term on weights, should be explored to reduce overfitting
         #alpha - L1 regularization (lasso), can be used in the case of very high dimensionality so that algo runs faster when implemented


         #eval metric - rmse for regression, error for classification. Has mae (mean absolute error), logloss (negative log-likelihood), error (binary classification), merror (multiclass classification error rate), mlogloss(multiclass logloss), auc (area under curve)


# plst = param.items()

# bst = xgb.train(plst, dtrain, num_round, evallist, feval=rmspe_xg, verbose_eval=250, early_stopping_rounds=250)

# dpred = xgb.DMatrix(df.loc[(df['Set'] == 1) & (df['Open'] == 1)][features_x]) #takes Open and Outliers as main 


# ypred_bst = bst.predict(dpred)


from sklearn import cross_validation
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import GaussianNB
from sklearn.ensemble import RandomForestClassifier
from sklearn import datasets
from sklearn.base import BaseEstimator
from sklearn.base import ClassifierMixin
import numpy as np
import operator

class EnsembleClassifier(BaseEstimator, ClassifierMixin):
    """
    Ensemble classifier for scikit-learn estimators.

    Parameters
    ----------

    clf : `iterable`
      A list of scikit-learn classifier objects.
    weights : `list` (default: `None`)
      If `None`, the majority rule voting will be applied to the predicted class labels.
        If a list of weights (`float` or `int`) is provided, the averaged raw probabilities (via `predict_proba`)
        will be used to determine the most confident class label.

    """
    def __init__(self, clfs, weights=None):
        self.clfs = clfs
        self.weights = weights

    def fit(self, X, y):
        """
        Fit the scikit-learn estimators.

        Parameters
        ----------

        X : numpy array, shape = [n_samples, n_features]
            Training data
        y : list or numpy array, shape = [n_samples]
            Class labels

        """
        for clf in self.clfs:
            clf.fit(X, y)

    def predict(self, X):
        """
        Parameters
        ----------

        X : numpy array, shape = [n_samples, n_features]

        Returns
        ----------

        maj : list or numpy array, shape = [n_samples]
            Predicted class labels by majority rule

        """

        self.classes_ = np.asarray([clf.predict(X) for clf in self.clfs])
        if self.weights:
            avg = self.predict_proba(X)

            maj = np.apply_along_axis(lambda x: max(enumerate(x), key=operator.itemgetter(1))[0], axis=1, arr=avg)

        else:
            maj = np.asarray([np.argmax(np.bincount(self.classes_[:,c])) for c in range(self.classes_.shape[1])])

        return maj

    def predict_proba(self, X):

        """
        Parameters
        ----------

        X : numpy array, shape = [n_samples, n_features]

        Returns
        ----------

        avg : list or numpy array, shape = [n_samples, n_probabilities]
            Weighted average probability for each class per sample.

        """
        self.probas_ = [clf.predict_proba(X) for clf in self.clfs]
        avg = np.average(self.probas_, axis=0, weights=self.weights)

        return avg



# rf = RandomForestClassifier(n_estimators=500, n_jobs=-1, criterion="entropy", random_state=456)
# rf.fit(df[features_x], y_train)

# gbm = xgb.XGBClassifier(max_depth=3, n_estimators=300, learning_rate=0.05).fit(X_train, y_train)
# predictions = gbm.predict(X_test)
import numpy as np

# X_train = list(X_train.Store.values)
colsRes = ['Store']
X_train = X_train.drop(colsRes, axis=1)
y_train = np.asarray(y_train['SalesLog'], dtype="|S6")
# y_train = list(y_train['SalesLog'].values)
# clf2 = RandomForestClassifier(n_estimators=10).fit(X_train, y_train)
clf3 = GaussianNB()

# np.random.seed(123)
eclf = EnsembleClassifier(clfs=[clf3], weights=[1,1])

for clf, label in zip([clf3, eclf], ['Random Forest', 'naive Bayes','Ensemble']):
	# clf.score(X_test, y_test)
	scores = cross_validation.cross_val_score(clf, X=X_train,y=y_train,scoring='mean_squared_error')
	print("Accuracy: %0.2f (+/- %0.2f) [%s]" % (scores.mean(), scores.std(), label))
	# print (scores)
	print mean_squared_error(y_test, clf.predict(X_test))
# 	y_pred2 = clf.predict_proba(test)
# 	y_pred = np.average(y_pred1[:,1], y_pred2[:,1], axis=1)
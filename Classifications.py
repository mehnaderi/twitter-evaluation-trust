# K-Nearest Neighbors (K-NN)

# Importing the libraries
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
from sklearn.metrics import recall_score
from sklearn.metrics import precision_score
# Importing the dataset
dataset = pd.read_csv('finalFile_v11.csv')
X = dataset.iloc[:, [2, 6]].values
y = dataset.iloc[:, 7].values

# Splitting the dataset into the Training set and Test set
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.25, random_state = 0)

# Fitting K-NN to the Training set
from sklearn.neighbors import KNeighborsClassifier
classifier1 = KNeighborsClassifier(n_neighbors = 5, metric = 'minkowski', p = 2)
classifier1.fit(X_train, y_train)

# Predicting the Test set results
y_pred1 = classifier1.predict(X_test)

# Making the Confusion Matrix
from sklearn.metrics import confusion_matrix
cmForKNN = confusion_matrix(y_test, y_pred1)
recallKNN = recall_score(y_test, y_pred1, average='macro')
precisionKNN = precision_score(y_test, y_pred1, average='macro')

# Fitting Decision Tree Classification to the Training set
from sklearn.tree import DecisionTreeClassifier
classifier2 = DecisionTreeClassifier(criterion = 'entropy', random_state = 0)
classifier2.fit(X_train, y_train)

# Predicting the Test set results
y_pred2 = classifier2.predict(X_test)

recallDT = recall_score(y_test, y_pred2, average='macro')
precisionDT = precision_score(y_test, y_pred2, average='macro')

cmForDT = confusion_matrix(y_test, y_pred2)


# Fitting Random Forest Classification to the Training set
from sklearn.ensemble import RandomForestClassifier
classifier3 = RandomForestClassifier(n_estimators = 10, criterion = 'entropy', random_state = 0)
classifier3.fit(X_train, y_train)

# Predicting the Test set results
y_pred3 = classifier3.predict(X_test)

recallRF = recall_score(y_test, y_pred3, average='macro')
precisionRF = precision_score(y_test, y_pred3, average='macro')

cmForRF = confusion_matrix(y_test, y_pred3)

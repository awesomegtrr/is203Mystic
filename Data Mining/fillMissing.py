import pandas as pd
import numpy as np

df = pd.read_csv('data/testTransformed.csv')
df = df.replace("",np.nan,regex=True)  

df['europen'] = df.europen.fillna(method='pad')
df['gdaxiopen'] = df.gdaxiopen.fillna(method='pad')
df['pharmaopen'] = df.pharmaopen.fillna(method='pad')
#df['x'] = df.groupby(['id'])['x'].transform(lambda grp: grp.fillna(method='ffill'))

df.to_csv('testTransformedTwo.csv')
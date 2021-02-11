import numpy as np
import pandas as pd
import matplotlib.pyplot as plt


from sklearn.preprocessing import StandardScaler # 연속변수의 표준화

utilities_df = pd.read_csv('harang.csv')
utilities_df.head()

utilities_df.shape

# 자료구조 살펴보기
utilities_df.keys()

utilities_df = utilities_df.drop (['percent'], axis=1)

# columns = ['eyesopen', 'emotion', 'percent', 'mstate', 'sstate']
columns = ['eyesopen', 'emotion', 'mstate', 'sstate']
columns

labels = np.array(utilities_df['time'])
labels

utilities_df = utilities_df.drop(['time'], axis=1)
utilities_df

stdsc = StandardScaler()
utilities_df = pd.DataFrame(stdsc.fit_transform(utilities_df))

utilities_df.index = labels
utilities_df.columns = columns

utilities_df

from scipy.spatial.distance import pdist, squareform

row_dist = pd.DataFrame(squareform(pdist(utilities_df,
                                         metric='euclidean')),
                        columns=labels,
                        index=labels)
row_dist

from scipy.cluster.hierarchy import linkage

row_clusters = linkage(utilities_df.values,
                       method='complete',
                       metric='euclidean')

pd.DataFrame(row_clusters,
             columns=['row label 1', 'row label 2',
                      '거리', '클러스터 샘플 갯수.'],
             index=['cluster %d' % (i + 1)
                    for i in range(row_clusters.shape[0])])

from scipy.cluster.hierarchy import dendrogram

dendr = dendrogram(row_clusters,
                   labels=labels)
plt.tight_layout()
plt.ylabel('Euclidean distance')
plt.show()

from sklearn.cluster import AgglomerativeClustering

ac = AgglomerativeClustering(n_clusters=3,Untitled1
                             affinity='euclidean',
                             linkage='complete')
labels = ac.fit_predict(utilities_df)
print('클러스터 레이블: %s' % labels)
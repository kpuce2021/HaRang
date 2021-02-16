## 기본
import numpy as np  # numpy 패키지 가져오기
import pandas as pd  # pandas 패키지 가져오기
import matplotlib.pyplot as plt  # 시각화 패키지 가져오기

## 데이터 전처리
from sklearn.preprocessing import StandardScaler  # 연속변수의 표준화

## 모델구축
from sklearn.cluster import KMeans

utilities_df = pd.read_csv('harang.csv')
utilities_df.head()

utilities_df.shape

utilities_df.keys()

utilities_df = utilities_df.drop(['percent'], axis=1)

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

km = KMeans(n_clusters=3,
            init='k-means++',
            n_init=10,
            max_iter=300,
            random_state=1)
# km.fit(utilities_df)
y_km = km.fit_predict(utilities_df)

y_km

cluster = pd.DataFrame(y_km)

cluster.index = labels
cluster.columns = ['cluster']

cluster

distortions = []
for i in range(1, 10):
    km = KMeans(n_clusters=i,
                init='k-means++',
                n_init=10,
                max_iter=300,
                random_state=0)
    km.fit(utilities_df)
    distortions.append(km.inertia_)
plt.plot(range(1, 10), distortions, marker='o')
plt.xlabel('Number of clusters')
plt.ylabel('Distortion')
plt.tight_layout()
plt.show()

from matplotlib import cm
from sklearn.metrics import silhouette_samples

cluster_labels = np.unique(y_km)
n_clusters = cluster_labels.shape[0]
silhouette_vals = silhouette_samples(utilities_df, y_km, metric='euclidean')
y_ax_lower, y_ax_upper = 0, 0
yticks = []
for i, c in enumerate(cluster_labels):
    c_silhouette_vals = silhouette_vals[y_km == c]
    c_silhouette_vals.sort()
    y_ax_upper += len(c_silhouette_vals)
    color = cm.jet(float(i) / n_clusters)
    plt.barh(range(y_ax_lower, y_ax_upper), c_silhouette_vals, height=1.0,
             edgecolor='none', color=color)

    yticks.append((y_ax_lower + y_ax_upper) / 2.)
    y_ax_lower += len(c_silhouette_vals)

silhouette_avg = np.mean(silhouette_vals)
plt.axvline(silhouette_avg, color="red", linestyle="--")

plt.yticks(yticks, cluster_labels + 1)
plt.ylabel('Cluster')
plt.xlabel('Silhouette coefficient')

plt.tight_layout()
plt.show()
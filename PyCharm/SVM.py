## 1.기본
import numpy as np  # numpy 패키지 가져오기
import matplotlib.pyplot as plt # 시각화 패키지 가져오기

## 2.데이터 가져오기
import pandas as pd # csv -> dataframe으로 전환
from sklearn import datasets # python 저장 데이터 가져오기

## 3.데이터 전처리
from sklearn.preprocessing import StandardScaler # 연속변수의 표준화
from sklearn.preprocessing import LabelEncoder # 범주형 변수 수치화

# 4. 훈련/검증용 데이터 분리
from sklearn.model_selection import train_test_split

## 5.분류모델구축 (3장.p.83~130)
# from sklearn.tree import DecisionTreeClassifier # 결정 트리
# from sklearn.naive_bayes import GaussianNB # 나이브 베이즈
# from sklearn.neighbors import KNeighborsClassifier # K-최근접 이웃
# from sklearn.ensemble import RandomForestClassifier # 랜덤 포레스트
# from sklearn.ensemble import BaggingClassifier # 앙상블
# from sklearn.linear_model import Perceptron # 퍼셉트론
# from sklearn.linear_model import LogisticRegression # 로지스틱 회귀 모델
from sklearn.svm import SVC # 서포트 벡터 머신(SVM)
# from sklearn.neural_network import MLPClassifier # 다층인공신경망

## 6.모델검정
from sklearn.metrics import confusion_matrix, classification_report # 정오분류표
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, roc_auc_score # 정확도, 민감도 등
from sklearn.metrics import roc_curve, auc # ROC 곡선 그리기

## 7.최적화
from sklearn.model_selection import cross_validate # 교차타당도
from sklearn.pipeline import make_pipeline # 파이프라인 구축
from sklearn.model_selection import learning_curve, validation_curve # 학습곡선, 검증곡선
from sklearn.model_selection import GridSearchCV # 하이퍼파라미터 튜닝

accidents_df = pd.read_csv('test.csv')
accidents_df.head()

accidents_df.keys()

X = accidents_df.drop (['time','concentration'], axis=1)
X.head()

y = accidents_df['concentration']
np.bincount(y)

X['eyesopen'] = X['eyesopen'].replace ([0,1], ['closed','open'])
X['emotion'] = X['emotion'].replace ([0,1,2], ['neutral','surprised','happy'])
X['mstate'] = X['mstate'].replace ([0,1,2], ['saccade','fixation','unknown'])
X['sstate'] = X['sstate'].replace ([0,1,2], ['OUTSIDE_OF_SCREEN','INSIDE_OF_SCREEN','unknown'])

X.head()

X.keys()

X = pd.get_dummies(X[['eyesopen', 'emotion','percent', 'mstate', 'sstate']],
                   columns=['eyesopen', 'emotion', 'mstate', 'sstate'],
                   drop_first=True)
X.head()

y

# \ 이후에 space 없어야 함
X_train, X_test, y_train, y_test = \
        train_test_split(X, y,
                         test_size=0.3,
                         random_state=1,
                         stratify=y)

stdsc = StandardScaler()
X_train.iloc[:,[0]] = stdsc.fit_transform(X_train.iloc[:,[0]])
X_test.iloc[:,[0]] = stdsc.transform(X_test.iloc[:,[0]])

svm = SVC(kernel='rbf',
          random_state=1,
          gamma=0.2,
          C=1.0)

svm.fit(X_train, y_train)

y_pred = svm.predict(X_test)

confmat = pd.DataFrame(confusion_matrix(y_test, y_pred),
                      index=['True[0]','True[1]'],
                      columns=['Predict[0]', 'Predict[1]'])
confmat

print('Classification Report')
print(classification_report(y_test, y_pred))

print('잘못 분류된 샘플 개수: %d' % (y_test != y_pred).sum())
print('정확도: %.3f' % accuracy_score(y_test, y_pred))
print('정밀도: %.3f' % precision_score(y_true=y_test, y_pred=y_pred))
print('재현율: %.3f' % recall_score(y_true=y_test, y_pred=y_pred))
print('F1: %.3f' % f1_score(y_true=y_test, y_pred=y_pred))

pipe_svm = make_pipeline(SVC(random_state=1))

pipe_svm.get_params().keys()

train_sizes, train_scores, test_scores =\
                learning_curve(estimator=pipe_svm, # 수정
                               X=X_train,
                               y=y_train,
                               train_sizes=np.linspace(0.1, 1.0, 10),
                               cv=2,
                               n_jobs=1)

train_mean = np.mean(train_scores, axis=1)
train_std = np.std(train_scores, axis=1)
test_mean = np.mean(test_scores, axis=1)
test_std = np.std(test_scores, axis=1)

plt.plot(train_sizes, train_mean,
         color='blue', marker='o',
         markersize=5, label='training accuracy')

plt.fill_between(train_sizes,
                 train_mean + train_std,
                 train_mean - train_std,
                 alpha=0.15, color='blue')

plt.plot(train_sizes, test_mean,
         color='green', linestyle='--',
         marker='s', markersize=5,
         label='validation accuracy')

plt.fill_between(train_sizes,
                 test_mean + test_std,
                 test_mean - test_std,
                 alpha=0.15, color='green')

plt.grid()
plt.xlabel('Number of training samples')
plt.ylabel('Accuracy')
plt.legend(loc='lower right')
plt.ylim([0.8, 0.9])  # 수정
plt.tight_layout()
plt.show()

param_range = [0.01, 0.1, 1.0, 10, 100]  # 수정

train_scores, test_scores = validation_curve(
                estimator=pipe_svm, # 수정
                X=X_train,
                y=y_train,
                param_name='svc__C', ## 수정
                param_range=param_range,
                cv=2)

train_mean = np.mean(train_scores, axis=1)
train_std = np.std(train_scores, axis=1)
test_mean = np.mean(test_scores, axis=1)
test_std = np.std(test_scores, axis=1)

plt.plot(param_range, train_mean,
         color='blue', marker='o',
         markersize=5, label='training accuracy')

plt.fill_between(param_range, train_mean + train_std,
                 train_mean - train_std, alpha=0.15,
                 color='blue')

plt.plot(param_range, test_mean,
         color='green', linestyle='--',
         marker='s', markersize=5,
         label='validation accuracy')

plt.fill_between(param_range,
                 test_mean + test_std,
                 test_mean - test_std,
                 alpha=0.15, color='green')

plt.grid()
plt.xlabel('Number of C') # 수정
plt.legend(loc='lower right')
plt.xlabel('Parameter C') # 수정
plt.ylabel('Accuracy')
plt.ylim([0.8, 0.9])  # 수정
plt.tight_layout()
plt.show()

param_range = [0.01, 0.1, 1.0, 10, 100]  # 수정

param_grid = [{'svc__C': param_range, # 수정
               'svc__gamma': param_range, # 수정
               'svc__kernel': ['rbf']}] # 수정

gs = GridSearchCV(estimator=pipe_svm, # 수정
                  param_grid=param_grid,
                  scoring='accuracy',
                  cv=2,
                  n_jobs=-1)

gs = gs.fit(X_train, y_train)

print(gs.best_score_)
print(gs.best_params_)

best_svm = gs.best_estimator_
best_svm.fit(X_train, y_train)
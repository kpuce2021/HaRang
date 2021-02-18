# android Application
 안드로이드 어플리케이션 부분
## Aws Account
 - aws cognito 사용자풀 서비스를 활용해서 동작.
    - CognitoSettings.java에 있는 3가지 인증키가 강희정 개인 인증키로 되어있어서 나중에 rds 연동을 위해 계정 바꿔야 될거 같아여
    - 세션관리 하는 방법을 모르겠어서 넘어가는 페이지마다 id, pw를 intent로 계속 넘겨서 매 페이지마다 로그인 되게끔 해둠
 - login 테스트
    - 테스트는 fragment 3번째 창에서 버튼 만들어둠
    - 테스트 계정 
       - id : test4   pw : Test1234
       
## 환경설정
 - SDK 버전 30 사용
 - ndk : 22.0.7026061 설치
 - cmake : 3.10.2.4988404 설치

# Project Title / EyePick

한국산업기술대학교 2021년 졸업작품
아이트래킹을 통한 동영상의 사용자 선호도 분석 및 하이라이트 생성 프로그램 입니다.


## Getting Started

Harang3 폴더 내의 android 어플리케이션 실행


### Project Introduction / 프로젝트 소개

학습자의 시선을 통해 수업 영상에 대한 집중도를 파악하여 교수자에게는 학습자가 제공된 영상을 집중해서 잘 시청하였는지에 대한 분석을 제공하고, 학습자에게는 수업 영상 중 집중하지 않은 부분을 하이라이트 영상으로 제작하여 제공한다.


## Use Language / 사용 언어 및 프로그램

Application - Java, C++, xml, css

Database - SQL, php7

Server - Amazon EC2 : Ubuntu 20.04
       - web server :  Apache2
       - DB : Amazon RDS, MySQL
       
Library - NDK, CMake, FFMPEG


외부 API - EyeTracking : [SeeSo](https://www.seeso.io/)


협업 TOOL - [TRELLO](https://trello.com/b/jbhhNMDq/harang)


## Built With

* [강희정](https://github.com/Heejeong01110) - AWS 서버, API 구현, 백엔드, 집중도 분석 알고리즘 구현
* [김지은](https://github.com/Jieun0317) - 어플리케이션 UI 구현, 아이트래킹 구현
* [김민우](https://github.com/alsdn1759) - AWS 서버, DB 및 RDS, API 구현, 백엔드


### Application Image / 어플리케이션 실행 화면

a. 교수자 로그인

![image](https://user-images.githubusercontent.com/48792627/122626722-f2d47f00-d0e6-11eb-94b9-9c796328dc2a.png)

b. 교수자 회원가입

![image](https://user-images.githubusercontent.com/48792627/122626723-f49e4280-d0e6-11eb-8690-2697cf7a3ef1.png)

c. 강의 업로드

![image](https://user-images.githubusercontent.com/48792627/122626727-f7993300-d0e6-11eb-95d4-093a320b4991.png)

d. 업로드된 강의 목록

![image](https://user-images.githubusercontent.com/48792627/122626729-fa942380-d0e6-11eb-954d-7e818440878b.png)

e. 등록된 학생 목록

![image](https://user-images.githubusercontent.com/48792627/122626737-fec04100-d0e6-11eb-893a-089d076f47e2.png)

f. 등록된 학생 수정(삭제)

![image](https://user-images.githubusercontent.com/48792627/122626738-008a0480-d0e7-11eb-8c10-90635998e145.png)

h. 등록된 학생 수정(추가)

![image](https://user-images.githubusercontent.com/48792627/122626741-04b62200-d0e7-11eb-946f-d925b4460aa0.png)

i. 교수자 정보 및 로그아웃 

![image](https://user-images.githubusercontent.com/48792627/122626743-067fe580-d0e7-11eb-965d-42f951bc29c4.png)



a. 학습자 로그인 

![image](https://user-images.githubusercontent.com/48792627/122626750-0aac0300-d0e7-11eb-81ec-53295a753a55.png)

b. 강의 목록화면

![image](https://user-images.githubusercontent.com/48792627/122626754-0da6f380-d0e7-11eb-8bb3-9eff847bd6e2.png)

c. 초점조절 페이지

![image](https://user-images.githubusercontent.com/48792627/122626758-10a1e400-d0e7-11eb-9b72-1deb7a7cb9b0.png)

d. 전체 영상 재생(영상 다운로드)

![image](https://user-images.githubusercontent.com/48792627/122626762-13043e00-d0e7-11eb-95ef-d7a936fe09ae.png)


e. 전체 영상 재생(영상 재생)

![image](https://user-images.githubusercontent.com/48792627/122626764-15669800-d0e7-11eb-954f-14c33cc79f70.png)

f. 클립 영상 재생(영상 목록)

![image](https://user-images.githubusercontent.com/48792627/122626766-17305b80-d0e7-11eb-833a-65d4e3baf043.png)

h. 클립 영상 재생(영상 재생)

![image](https://user-images.githubusercontent.com/48792627/122626775-1992b580-d0e7-11eb-929f-2992c20e6b86.png)

i. 학생 집중률 조회 페이지

![image](https://user-images.githubusercontent.com/48792627/122626778-1b5c7900-d0e7-11eb-9330-dac69b4499d9.png)

j. 학습자 정보 및 로그아웃 

![image](https://user-images.githubusercontent.com/48792627/122626782-1f889680-d0e7-11eb-9eed-857ced4e11bd.png)



## License / 라이센스

이 프로젝트는 한국산업기술대학교 컴퓨터공학부의 “종합설계”교과목에서 프로젝트“아이트래킹을 통한 동영상의 사용자 선호도 분석 및 하이라이트 생성”을 수행하는 (강희정, 김지은, 김민우)들이 작성한 것으로 사용하기 위해서는 팀원들의 허락이 필요합니다.

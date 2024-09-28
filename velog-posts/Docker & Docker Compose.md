Docker는 애플리케이션을 **컨테이너**로 배포하고 관리할 수 있는 오픈소스 플랫폼이다. 컨테이너는 애플리케이션 실행에 필요한 코드, 라이브러리, 의존성 등을 포함한 독립적인 환경을 제공한다.

왜 사용할까?
-------

### 환경 설정의 단순화

예를 들어, .jar 파일로 패키징된 Java 프로젝트를 실행한다고 가정해보자. Ubuntu에서는 sudo apt install openjdk-11-jdk 명령어로 Java를 설치한 후 java -jar myapp.jar로 애플리케이션을 실행해야 한다. Windows에서는 OpenJDK 웹사이트에서 JDK를 설치하고, 환경 변수를 설정한 후 동일하게 java -jar myapp.jar 명령어로 실행한다.

이처럼 두 운영체제에서 공통적으로 요구되는 것은 Java 환경 설정이다. 각 운영체제에 맞는 패키지를 설치하고 의존성을 관리해야 하며, 이러한 환경 설정은 애플리케이션 유지 관리에 부담이 될 수 있다.

Docker의 컨테이너는 이러한 문제를 해결한다. 컨테이너는 애플리케이션 실행에 필요한 모든 환경을 독립적으로 포함하기 때문에, 운영체제나 환경에 상관없이 동일한 방식으로 애플리케이션을 실행할 수 있다. Java가 설치된 Docker 이미지를 만들어두면, 별도의 Java 설치 없이 Docker만 있으면 애플리케이션을 실행할 수 있다.

### 더 복잡한 상황에서의 유용성

예를 들어 MySQL을 설치하려면 MySQL 공식 사이트에서 설치 파일을 다운로드하고, 운영체제에 맞게 설치해야 한다. 하지만, 다른 버전을 사용해야 한다면?, 서로 다른 버전을 동시에 써야 한다면?, 같은 환경을 다른 시스템에서 재현해야 한다면? Docker를 사용하면 이러한 문제들을 쉽게 해결할 수 있다. 원하는 버전의 MySQL 이미지를 바로 불러와 컨테이너로 실행할 수 있기 때문이다.

Docker는 이러한 복잡한 환경 설정을 단순화하고, 다양한 운영체제와 환경에서 동일한 방식으로 애플리케이션을 배포하고 실행할 수 있게 도와준다.

Docker 사용법
----------

### Image

docker는 바로 Container가 만들어지는게 아닌, Image를 기반으로 Container가 실행된다. Image에서 패키징된 환경을 기반으로 추가 설정들이 들어가 Container가 실행된다. 즉, 동일 Image를 돌려 Container를 만들게 되면 컨테이너 내부 환경, 코드 등은 동일하나, 추가 환경 변수등은 실행할 때 명령어에 따라 달라지게 된다.

#### Image pull 받기

Docker Image는 Docker Hub에 있는 Public으로 된 Image를 pull 받거나, dockerfile을 이용하여 image를 만들어야 한다.

만약 이미 만들어져 있는 MySQL image를 받고자 한다면, `docker pull mysql:latest` 혹은 특정 버전을 원한다면 `docker pull mysql:8.0` 명령어를 통해 받을 수 있다. 이러한 버전 정보는 해당 image가 공유되고 있는 [Docker Hub](https://hub.docker.com/)에서 찾아야 하며, 위 MySQL의 경우 [Docker Hub MySQL Tag 정보](https://hub.docker.com/_/mysql/tags) 이 링크를 통해 찾아볼 수 있다.

#### 만약 dockerfile을 이용해 Image를 만들고자 한다면?

먼저 기본적인 dockerfile의 예시이다.

```dockerfile
FROM openjdk:11-jre-slim

COPY ./myapp.jar /app/myapp.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "myapp.jar"]

ENV PROFILE=prod
```

먼저 `FROM`은 Docker 이미지의 기반이 되는 이미지를 지정하는 명령어다.(필수)

```dockerfile
FROM openjdk:11-jre-slim
```

`COPY`는 로컬 파일 시스템에서 컨테이너 파일 시스템으로 파일을 복사하는 명령어다.

```dockerfile
COPY ./myapp.jar /app/myapp.jar`
#`./myapp.jar`경로에 파일을 `/app/myapp.jar`로 복사한다.
```

`RUN`은 Docker 이미지가 빌드되는 동안 실행될 명령어다. 주로 패키지 설치나 빌드 작업을 수행하는 데 사용된다.

```dockerfile
RUN apt-get update && apt-get install -y curl
#apt-get을 통해 curl패키지를 설치한다.
```

`ENTRYPOINT`는 컨테이너가 실행될 때 항상 실행되는 명령어를 정의한다. 예를 들어, Java 애플리케이션의 JAR 파일을 실행하도록 설정할 수 있다.

```dockerfile
ENTRYPOINT ["java", "-jar", "myapp.jar"]
```

`CMD`는 `ENTRYPOINT`에 인자를 전달하거나, 실행될 기본 명령을 지정하는 역할을 한다. ENTRYPOINT와 함께 사용하면 CMD는 인자로 해석되고, 단독으로 사용되면 기본 실행 명령으로 동작한다.

```dockerfile
CMD ["--server.port=8080"]
```

`ENV`는 컨테이너 내에서 사용할 환경 변수를 설정하는 명령어다. 이는 애플리케이션 실행 중 필요한 환경 설정을 쉽게 관리할 수 있게 해준다.

```dockerfile
ENV PROFILE=prod
```

위 dockerfile을 이용해 image를 build해야하는데 `docker build -t [image 이름] [Dockerfile이 있는 위치]` 명령어로 실행되며 예시로는 `docker build -t myapp:1.0.0 .`이 된다.

* `-t`는 tag라는 의미로 myapp이 이미지 이름이라면 `:` 뒤로 오는 것이 태그이름이 된다. 예를들어 latest는 가장 최신 버전을 의미하며, 예시처럼 `1.0.0`을 붙여 versioning을 한다.
* 맨 뒤에 `.`는 Dockerfile의 위치로 `.`는 현재 디렉토리 위치를 의미한다. 만약 Dockerfile의 위치가 다른곳에 있다면 해당 디렉토리로 명시해줘야한다.

### Container

Image가 만들어 졌다면 이 Image를 기반으로 실행시키기만 하면 된다. 기본적인 실행 예시 이다.

```bash
docker run -p 18080:8080 myapp:1.0.0
```

`-p`는 **port** 가 아닌 **publish** 의 의미이다. 즉, 포트 자체가 아닌 포트 매핑을 의미한다. 외부에서 오는 포트 `18080`을 통해 컨테이너 내부에 `8080`으로 열린 애플리케이션에 요청을 보낸다는 의미다. 즉, `localhost:18080`을 통해 해당 컨테이너는 들어가지만 내부에서는 `8080`포트로 요청을 보내게 된다. 이 말은 하나의 컨테이너에 하나의 애플리케이션, 하나의 포트가 아닌 다양한 포트를 지정할 수 있다는 의미로 만약 여러 포트를 지정하고자 한다면 `-p 18080:8080 -p 9090:443` 과 같이 반복해서 쓰면 된다.

```bash
docker run -d -p 18080:8080 myapp:1.0.0
```

`-d`옵션을 추가할 수도 있다. `-d`는 **demon** 이 아닌 **detach** 즉, 분리하다의 의미인데 말그대로 터미널과 분리하여 백그라운드에서 실행한다는 의미다. 이 옵션을 사용하면 터미널을 차지하지 않고 컨테이너가 백그라운드에서 실행된다.

```bash
docker run -d -p 8080:80 -v ./data:/app/data myapp:1.0.0
```

`-v`옵션은 volume의 의미로, 기본적으로 컨테이너 내부 데이터는 컨테이너 삭제시 같이 삭제된다. 하지만 `-v`명령어를 위와 같이 용하면 Local에 `./data` 와 `/app/data`를 연결하여 데이터의 영속성을 유지할 수 있다.

```bash
docker run -d -p 8080:80 -v ./data:/app/data -e PROFILE=prod -e MYSQL_HOST=localhost:3006 myapp:1.0.0
```

`-e` 옵션은 환경 변수(environment variables)라는 의미로 Container 내부에서 애플리케이션 실행시 사용될 환경변수를 전달할 수 있다. 보통 sping boot에 profile이나 mysql 정보, 키값 등이 포함될 수 있다.

### 자주 사용 명령어

```bash
#이미지 pull
docker pull [이미지 이름]:[태그]

#전체 iamge 확인
docker images 

#image 삭제
docker rmi [이미지 or ID]:[태그]

#켜져있는 컨테이너 확인
docker ps

#전체 컨테이너 확인
docker ps -a

#컨테이너 정지
docker stop

#컨테이너 시작
docker start

#컨테이너 재시작
docker restart

#컨테이너 삭제(정지 후)
docker rm [컨테이너이름 OR ID]

#컨테이너 로그 확인
docker logs [컨테이너이름]

#컨테이너 실시간 로그 확인
docker logs -f [컨테이너이름]

#컨테이너 내부 접속
docker exec -it [컨테이너 이름] /bin/bash

#사용하지 않는 리소스 정리
docker system prune
```

이쁘게 사용하기
--------

위에서 Docker에 대한 기본적인 내용이다. 하지만 잠시 시나리오를 보게되면

1. 프로젝트의 Dockerfile을 만든다.
2. 프로젝트 배포시 `docker build -t myapp:1.0.0` 명령어를 이용해 Image를 만든다.
3. 배포 위치로 접속하여 `docker run -d -p 8080:80 -v ./data:/app/data -e PROFILE=prod -e MYSQL_HOST=localhost:3006 myapp:1.0.0`으로 배포한다.

자 여기서 만약 환경 변수가 추가되고 경로가 길어진다면? 만약 다른 컨테이너도 같이 배포해야 한다면? 여러개의 컨테이너를 한번에 컨트롤 해야한다면?

이때 Docker Compose를 사용하게 된다.

### Docker compose

Docker Compose는 여러개의 Container를 정의하고 이를 관리할 수 있도록 해주는 Docker의 **도구**이다. 추가로 이는 Container를 관리해주는 것으로 기본적으로 Image가 존재해야한다. 만약 Image가 없으면 자동으로 Docker Hub에서 pull을 시도하게 된다.

Docker Compose는 Container에 대한 정의를 docker-compose.yml에 하게 되는데 예시를 보면

```yaml
version: '3'
services:
    web:
        container_name: myapp
        image: myapp:1.0.0
        ports:
            - "18080:8080"
              - "9090:443"
        environment:
            - PROFILE=prod
            - MYSQL_HOST=localhost:3306
    db:
        container_name: mysql
        image: mysql:8.0
        environment:
            - MYSQL_ROOT_PASSWORD=root
```

가 되며, 이를 RUN 하기 위해서는

```bash
docker compose up
```

이며 -d 옵션을 붙여 마찬가지로 백그라운드에서 실행시킬 수 있다.

이를 종료하기 위해서는

```bash
docker compose down
```

이며, 이외에

```bash
#정지
docker compose stop

#재시작
docker compose restart

#로그 확인
docker compose logs
```

이 있다.

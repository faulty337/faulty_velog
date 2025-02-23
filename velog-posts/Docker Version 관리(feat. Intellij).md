대충 그럴싸한 서론
----------

다른 프로젝트 중 테스트 서버 릴리즈 중 버전 관리에 있어서 겪었던 불편함, 이를 해결하고자 한 작업 등에 대해 기록하고자 한다.

### 환경

Intellij Spring boot Gradle Windows Docker Docker-Compose 위 환경으로 docker image 빌드 중 버전을 확인하고 갱신하고 하는 과정이 스크립트를 직접적으로 수정해야 하며, 이후 다른 CI/CD에서 사용하기에도 문제가 된다고 생각했다.

기존 구조 및 코드
----------

### 구조

크게 필요한 현재 구조만 그리자면

    ├─ docker
    │  └─ Dockerfile
    ├─ src
    ├─ gradlew
    └─ build.sh

**Dockerfile**

```docker
FROM openjdk:17-jdk-slim

COPY ../build/libs/test.jar app.jar

---

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

* java 버전 확인
* app.jar을 사용하는 것이기에 build.gradle에서 `.jar` 파일명 주의

*** ** * ** ***

**build.gradle**

```gradle
...

tasks.named('bootJar') {
    archiveFileName = 'test.jar'
}
```

* `test.jar`로 만들어 Docker에서 `app.jar`로 복사
* 파일명은 변경되어도 되지만 , Dockerfile과 맞춰야한다.

*** ** * ** ***

**build.sh**

```shell
#! /bin/sh
./gradlew bootjar

docker compose -f ./docker/docker-compose.yml build
docker push faulty337/keepout-backend:test-0.1
```

*** ** * ** ***

일단 위에 코드만으로도 버전관리가 가능하긴 하다. 하지만 조금더 체계적이고 안전한 방법이 필요했다.

### 문제점

**version 명시시 build.sh에서 직접 수정해야 한다.** - 매번 빌드에 관련한 파일 수정 Commit이 build.sh 수정으로 되기에 안전하지 않다. - Versioning과 빌드의 대한 구분을 위해서

사용자 입력
------

Versioning 방식이 `build.sh`파일의 수정이 아닌 사용자의 입력을 받는 방식이다.

**build.sh**

```shell
#! /bin/sh

# 사용자에게 버전 입력 요청
echo "Enter version tag (or press enter for default):"
read VERSION

# 기본값 설정 (입력 없을 경우 자동 태그)
if [ -z "$VERSION" ]; then
  VERSION=$(date +%Y%m%d-%H%M%S)  # 기본값: 현재 날짜+시간
fi

IMAGE_NAME="faulty337/keepout-backend:$VERSION"

./gradlew bootjar

docker build -t $IMAGE_NAME -f ./docker/Dockerfile ..
docker push $IMAGE_NAME

echo "✅ New image pushed: $IMAGE_NAME"
```

버전 정보를 입력하여 빌드하는 방식이다.

### 문제점

**1. 이 방식은 기존 버전을 확인하고 Versioning을 해야한다.**

* 기존 몇 버전이였는지 docker images 등을 통해 버전을 확인해야 한다.

**2. 사용자의 실수로 기존 버전을 덮어 씌울 수도 있다.**

* 바로 빌드하는 방식은 사용자의 실수를 확인할 시간이 다른 방식에 비해 없기 때문에 실수가 발생할 확률이 있으며 이는 기존 버전을 덮어 씌우기 때문에 문제가 될 수 있다.

Version 파일 관리
-------------

`version.txt` 파일을 이용해 버전을 관리하는 방법이다. `version.txt`을 이용하기에 build.sh에서 Versioning을 분리하기도 하며, 기존 버전을 확인, 실수시에 이를 인지할 단계가 포함될 수 있기 때문에 안정적이라 생각했다.

**build.sh**

```shell
#! /bin/sh

VERSION=$(cat version.txt)

echo "Current version: $VERSION"
echo "Enter new version (or press enter to keep '$VERSION'):"
read NEW_VERSION


if [ ! -z "$NEW_VERSION" ]; then
  VERSION=$NEW_VERSION
  echo "$VERSION" > version.txt
fi
IMAGE_NAME="faulty337/keepout-backend:$VERSION"

./gradlew bootjar

docker build -t $IMAGE_NAME -f ./docker/Dockerfile ..

docker push $IMAGE_NAME

echo "✅ New image pushed: $IMAGE_NAME"
```

*** ** * ** ***

**version.txt**

```txt
0.0.1
```

* 버전 표기 방식은 Semantic Versioning을 사용했다.
* 해당 파일은 build.sh와 같은 경로에 두어야 한다.

### 최종 구조

    ├─ docker
    │  └─ Dockerfile
    ├─ src
    ├─ gradlew
    ├─ build.sh
    └─ version.txt

대충 그럴싸한 결론
----------

총 방법은 3가지로 썼지만 사실 맨 마지막 방법을 사용하고 있다. 여기서 원한다면 사용자 입력 방식과 파일 관리 방식을 같이 사용해도 된다.

**build.sh**

```shell
#! /bin/sh

VERSION=$(cat version.txt)

echo "Current version: $VERSION"
echo "Enter new version (or press enter to keep '$VERSION'):"
read NEW_VERSION

if [ ! -z "$NEW_VERSION" ]; then
  VERSION=$NEW_VERSION
  echo "$VERSION" > version.txt
fi

echo "VERSION=$VERSION" > .env

IMAGE_NAME="faulty337/keepout-backend:$VERSION"

./gradlew bootjar

docker build -t $IMAGE_NAME -f ./docker/Dockerfile ..
docker push $IMAGE_NAME

echo "✅ New image pushed: $IMAGE_NAME"
```

위와같이 두 방법을 사용해도 되지만, 이후 Git에서 Gitaction 등을 이용해 릴리즈할 때 해당 버전을 입력해줘야 하는데 그냥 파일관리를 통해 TAG, 버전 명시 등을 통일하는게 나을 듯 했다.

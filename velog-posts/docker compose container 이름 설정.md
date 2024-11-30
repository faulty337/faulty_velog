대충 그럴싸한 서론
----------

테스트 환경을 구성하거나 필요한 프레임워크를 간단히 실행시킬 때 docker compose를 이용해 설정 되어 있는 환경을 이용하여 간단히 열 수 있었다.

하지만 docker compose로 열었다고하더라도 docker에 기반하기에 열린 logs를 이용해 log를 읽거나, inspect를 이용해 network 등 여러 설정을 보기 위해서는 docker에서 해당 컨테이너를 명시해야한다.

```bash
docker logs [ContainerID OR ContainerName]
```

이때 보통 동적으로 변하는 ContainerID보다는 지정된 ContainerName을 많이 사용하는데 docker compose의 경우 이름을 설정하지 않으면

```bash
<프로젝트 이름>_<서비스 이름>_<인스턴스번호>
```

와 같이 정해진다.

예를 들어

```yaml
#mysql/docker-compose.yml

services:
  mysql:
    image: mysql:8.0
    restart: always
    ports:
      - "3306:3306" 
```

가 있으면 mysql_mysql 가 컨테이너 이름이 되고 아주 이쁘지 않게 된다.

때문에 docker compose 에서 컨테이너 이름 설정에 대해 간단히 적고자 한다.

docker compose Container 이름 설정 방법
---------------------------------

간단히 `container_name` 속성을 사용하면 된다.

예시

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0 # 원하는 MySQL 버전으로 변경 가능
    container_name: mysql
    restart: always
    ports:
      - "3306:3306"
```

docker Container 이름 설정 방법
-------------------------

컨테이너를 생성하는 명령어는 `run`명령어로, `--name`을 이용하면 된다.

```bash
docker run -d --name mysql --restart always -p 3306:3306 mysql:8.0
```


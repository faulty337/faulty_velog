대충 그럴싸한 서론
----------

개발 환경에서 자주 듣는 말 중 하나는 "내 컴퓨터에선 잘 되는데요?"라는 표현이다.  
이 말은 로컬 환경에선 멀쩡히 돌아가던 시스템이 운영 환경에선 예기치 못한 문제를 일으킨다는 현실을 반영한다.

우리 시스템에서도 BACnet 장비와의 통신에서 간헐적인 실패가 발생하고 있었다. 통신이 성공할 때도 있었지만, 약 30\~40%의 확률로 실패했고, Python 프로세스가 비정상적으로 종료되거나 꼬이는 경우도 빈번했다.

원인을 명확히 특정하기 어려운 상황이었기에, 재시도를 통해 해결하고자 했고, 최대 3회까지 재작동하는 구조를 고려하게 되었다. 이를 안정적으로 구현하기 위해 컨테이너화를 도입했다.

*** ** * ** ***

컨테이너화를 선택한 이유
-------------

Dockerize의 가장 큰 목적은 다음 두 가지였다:

1. `docker compose`를 활용해 프로그램을 간편하게 재시작할 수 있도록 하기 위함
2. 한 번 구성해둔 환경을 재사용할 수 있는 구조를 만들기 위함

그러나 실제 적용해보니, 기대 이상의 **안정성 확보**라는 결과도 얻게 되었다.

*** ** * ** ***

1. 필요한 라이브러리 및 패키지 정리
---------------------

가장 먼저 한 일은, 기존 시스템이 어떤 환경에서 정상 동작하는지를 파악하는 것이었다.

Python의 경우 `pip freeze > requirements.txt` 명령어를 통해 필요한 pip 패키지를 수집할 수 있다.

```Dockerfile
FROM python:3.10-slim-buster as builder

WORKDIR /opt/app

RUN apt-get update && apt-get install -y gcc

COPY ./requirements.txt .
RUN pip install --user --no-cache-dir -r requirements.txt
```

또한, OS 단에서 필요한 패키지(GCC 등)도 함께 설치해야 정상 동작하는 경우가 많다.

*** ** * ** ***

2. 소스 코드 복사 및 실행 명령어 추가
-----------------------

라이브러리 설치가 완료되었다면, 실제 작동시킬 소스 코드를 컨테이너로 복사하고 실행 명령어를 지정한다.

```Dockerfile
COPY ./thingsboard-gateway /opt/app/thingsboard-gateway

CMD ["/opt/app/thingsboard-gateway/start-gateway.sh"]
```

*** ** * ** ***

3. 이미지 빌드 및 실행
--------------

이미지는 다음과 같은 명령어로 빌드한다:

```bash
docker build -t faulty337/gateway:0.0.1 .
```

이후 이미지를 실행할 때는:

```bash
docker run faulty337/gateway:0.0.1
```

옵션이 많아지면 run 명령어가 길어지기 때문에, `docker-compose`를 활용하는 것이 유리하다.

*** ** * ** ***

4. docker-compose로 실행 자동화
-------------------------

`docker-compose.yml` 파일을 작성하여 실행을 간단하게 구성할 수 있다.

```yaml
services: 
  gateway:
    restart: always 
    container_name: gateway
    image: faulty337/gateway:0.0.1
    environment:
      - TZ=Asia/Seoul
    volumes: 
      - ./run_app/:/app/
    network_mode: host
```

이제 `docker-compose up -d` 만으로 쉽게 시작할 수 있다.

*** ** * ** ***

5. Dockerize 이후 경험한 안정성 개선
--------------------------

컨테이너화를 통해 기대 이상의 안정성 향상을 체감할 수 있었다.

기존 문제와 비교하면 다음과 같다:

### 문제점 (Before)

* BACnet 장비 연결 성공률이 30\~40% 수준
* Python 프로세스가 종종 비정상 종료되거나, 꼬이는 현상 발생
* 운영 환경의 변동 요인으로 인한 예측 불가한 장애

### 개선점 (After)

1. **통신 성공률 향상**   
   동일한 코드임에도 연결 성공률이 99% 이상으로 상승했다.  
   네트워크 초기화 환경이 항상 동일하므로 성공률이 안정적으로 유지되었다.

2. **운영 환경 일관성 확보**   
   컨테이너 내부는 항상 동일한 환경을 유지하므로 의존성 문제나 OS 이슈가 줄어들었다.

3. **장애 대응 속도 향상**   
   장애 발생 시 `docker restart`만으로 빠르게 복구 가능했고, 로그 및 설정은 volume을 통해 안전하게 보존되었다.

*** ** * ** ***

마무리하며
-----

Docker를 단순한 배포 도구로만 생각했지만, 이번 경험을 통해 **시스템 안정성을 확보하는 강력한 수단** 이 될 수 있음을 체감했다.  
특히 BACnet처럼 저수준 네트워크와 직접 통신하는 시스템에서는 `host network` 옵션을 통해 큰 효과를 볼 수 있었다.

앞으로도 Docker를 단순한 배포 이상의 전략적 도구로 활용해 나갈 계획이다.

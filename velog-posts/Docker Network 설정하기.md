### 대충 잡다한 서론

항해 플러스에서 마친 Concert 프로젝트에서 비동기 프로그래밍을 시작하기 전, 테스트 코드 정리 및 부하 테스트에 환경을 세팅하고자 했다.

기존의 테스트 방식은 k6의 run script.js이며, 부하테스트의 경우 실제 서버를 돌려보는 것이기에 토큰정보 등이 redis에 그대로 남아 있을 수 있기 때문에 flushall을 이용해 초기화를 해줘야 한다.

즉, test-docker-compose에서 k6와 이를 모니터링하는 grafana와, redis 초기화와 redis 등 각 container와의 통신이 필요했다.

이때 docker network에 대한 설정이 잘 안되어 있었고, 이에 따른 정리를 시작해보고자 했다.

Docker Network
--------------

위에서도 말했듯이 Docker에 Container는 각 다른 Container들과의 통신을 위해서는 서로 같은 network를 설정하여 통신해야 한다.
> **그러면 그냥 모조리 싹다 같은 Network에 때려박으면 다 통신되는 거지 않을까?** 맞는 말이긴 하다. 하지만 Network를 분리 했을 때에 대한 장점은 분명 존재한다.

1. 관리 용이성 : 분리된 Network는 트래픽의 흐름을 쉽게 이해할 수 있게 해주며 관리를 편리하게 해준다.
2. 성능 최적화 : 하나로 유지되는 Network는 트랙픽이 몰리면 각 패킷별 혼잡도가 올라가게 된다.
3. 문제 추적 : 분리되어 있게 되면 문제 발생시 이에 대한 추적해야할 트래픽을 줄이고 용이하게 해준다.

### Docker Network

docker의 네트워크는 컨테이너간의 통신을 관리하는 가상 네트워크이다.

1. Network가 설정되어 있지 않아도 외부에서는 포트포워딩 된 포트 접근이 가능하다. ex) `localhost:{포트이름}`
2. Network가 설정되어 있지 않으면 Container끼리는 통신할 수 없다.
3. Docker컨테이너 안에서 `localhost`는 컨테이너 내부 Localhost이기 때문에 외부 Localhost 환경을 찾을 수 없다. 즉 `외부 Localhost -> docker Localhost`는 가능해도 `docker Localhost -> 외부 Localhost`는 찾을 수 없다.

### 왜 사용할까?

보통 Docker에 이미지는 Docker의 특성상 구현가능한 환경, 코드 등이 Image에 포함된다. 하지만, Kafka와 zookeeper나 여러 모니터링 프로그램은 다른 서비스와 통신하며 써야한다. 즉, 컨테이너간 통신이 필요한 경우가 생기며 이는 동일 Network에 두어 통신이 가능토록 해야한다.

Network 사용 방법
-------------

Network사용은 필수가 아닌 Option으로 `docker`와 `docker compose`별 사용방법을 각 정리해보았다. **Network는 Container에 적용된다.**

### docker에서 Network 설정

docker network설정 전, network가 미리 생성되어 있어야 한다.

```bash
docker network create {network 타입}
```

* 이때 network 종류는 `bridge`, `host`, `none`, `custom bridge`, `overlay`가 있다.
* 저 위에서 custom bridge이외에 것으로 설정하면 전부 custom bridge로 해당 이름으로 network가 생성된다. ex) `docker network create my-network`

Container를 실행시키는 명령어인 `docker run`에서 Option으로 추가 가능하다.

```bash
docker run --network {network 타입} {컨테이너 이름}
```

* 이때 network 타입은 위와 같이 `bridge`, `host`, `none`, `custom bridge`, `overlay`가 되며 이미 생성된 network를 사용해야 한다.

### docker compose 에서 Network 설정

docker compose에서는 각 여는 container마다 network를 설정할 수 있으며, network 생성 혹은 이미 생성된걸 사용할건지에 대한 설정을 할 수 있다.

```yml
version: "3"
services:
    db:
        image: mysql:5.7
        environment:
              MYSQL_ROOT_PASSWORD: root
        networks:
              - my-network
networks:
     my-network:
           driver: bridge
```

기본적인 network 설정 방법이다. services에서 사용한 network는 반드시 밑에서 선언해줘야 하며, custom bridge의 경우 name 을 통해 이름을 커스텀할 수 있다.

```yml
networks:
     my-network:
        name: my-network
           driver: bridge
```

사용 예시
-----

내가 했던 프로젝트를 예시로 구성해보았다. 프로젝트에서 필요한 service는 전부 docker container로 구성되어 있고 이에 대한 관리는 docker compose로 관리하고 있다.

먼저 각 컨테이너, 메인 프로젝트(Server) 등 각 통신 방향이다. ![](https://velog.velcdn.com/images/faulty337/post/090620ce-5ca9-46ab-a010-d24024189633/image.jpg)

1. redis: 대기열, token, cache에 이용
2. kafka: 이벤트 드리븐용
3. zookeeper: kafka 필수
4. influxdb: 테스트 등에서 나오는 log나 데이터 저장용
5. grafana: 모니터링 UI
6. k6: 전체 서비스 이용을 가정한 테스트용
7. cleanup(redis): test전 redis flushAll 용 service

여기서 이 컨테이너들을 각 용도별 docker compose로 구분하면 ![](https://velog.velcdn.com/images/faulty337/post/9551c90e-166a-43c7-8e42-b5656c0b2e36/image.png) 와 같이 나눌 수 있다.

이에 대한 Network를 구성하면 ![](https://velog.velcdn.com/images/faulty337/post/ea516a5a-1540-4528-95e2-a8515c750eeb/image.jpg) 와 같이 구성이 가능하다.

위 구성도에 따른 docker compose는 docker-compose.yml

```yml
version: '3.8'

services:
  redis:
    image: redis:latest
    container_name: redis
    ports:
      - "6379:6379"
    networks:
      - redis-network

  zookeeper:
    image: wurstmeister/zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    restart: always
    networks:
      - kafka-network

  kafka:
    image: wurstmeister/kafka
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: 127.0.0.1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_BROKER_ID: 1
      KAFKA_LOG_RETENTION_HOURS: 24
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
    restart: always
    depends_on:
      - zookeeper
    networks:
      - kafka-network
networks:
  redis-network:
    name: redis-network
    driver: bridge
  kafka-network:
    name: kafka-network
    driver: bridge
```

monitoring-docker-compose.yml

```yml
version: '3.8'

services:
  influxdb:
    container_name: influxdb
    image: influxdb:1.8
    ports:
      - "8086:8086"
    environment:
      - INFLUXDB_DB=k6
    networks:
      - monitoring-network
  grafana:
    container_name: grafana
    image: grafana/grafana:9.3.8
    ports:
      - "3000:3000"
    environment:
      - GF_AUTH_ANONYMOUS_ORG_ROLE=Admin
      - GF_AUTH_ANONYMOUS_ENABLED=true
      - GF_AUTH_BASIC_ENABLED=false
    volumes:
      - ./grafana:/etc/grafana/provisioning/
    command: run /scripts/script.js
    networks:
      - monitoring-network

networks:
  monitoring-network:
    name: monitoring-network
    driver: bridge
```

test-docker-compose.yml

```yml
services:
  k6:
    container_name: k6
    image: grafana/k6:latest
    ports:
      - "6565:6565"
    environment:
      - K6_OUT=influxdb=http://influxdb:8086/k6
    volumes:
      - ./script:/scripts
    command: run /scripts/script.js
    networks:
      - monitoring-network

  cleanup: #redis 초기화용
    image: redis:latest
    container_name: redis-cli
    depends_on:
      - k6
    entrypoint: [ "redis-cli", "-h", "redis-server", "FLUSHALL" ]
    networks:
      - redis-network

networks:
  monitoring-network:
    external: true

  redis-network:
    external: true
```


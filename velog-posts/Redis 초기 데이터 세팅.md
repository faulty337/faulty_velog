서론은 개발 배경이니 급하다면 [본론](#%EB%B3%B8%EB%A1%A0)으로

대충 그럴싸한 서론
----------

진행중인 미니 프로젝트에서 초기 데이터 세팅이 필요한 경우가 생겼다. 이 데이터 세팅을 api를 통해 데이터를 응답해야 했다.

### 환경

1. 서비스 중일 때 데이터는 매우 정적이다.
2. 데이터의 업데이트 타이밍은 서버 업데이트 타이밍이다.
3. 정합성 체크 등으로 데이터 호출이 여러번 될 수 있다.
4. 데이터의 양이 커질 수 있다.
5. 정식 배포 전 데이터가 변경될 일이 많아 관리가 필요하다.
6. Redis 서버는 Ubuntu에서 docker로 올라가 있다.

1번으로 인해 DB보다는 특정 파일로 관리되어야 한다고 생각했다. 3번으로 인해 데이터가 빠르게 불러올 수 있어야 한다고 판단해 Redis에 넣기로 했다. 6번으로 특정 파일로 관리되어야 한다고 생각했다.

때문에 초기 데이터 세팅을 Json파일로 관리해 이를 통해 Redis에 초기 데이터 세팅과 동시에 Set하고자 했다.

### 문제

* Redis는 In-Memory이므로 초기 데이터 세팅 관련한 기능이 지원되지 않는다.

* `Redis - Server` 관계에서 Server에서 초기 데이터 세팅을 처리할 수 있지만, 서버가 분산서버가 되었을 때 Redis에 초기 데이터를 집어넣는 과정이 중복되고 Redis의 책임이 Spring Boot에 들어간다고 생각했다.

본론
---

### 해결 방법

Python과 Python redis를 이용해 data.json내용을 Redis에 넣는다. 위 내용을 init.sh 명령어로 한번에 처리가능하게 만든다.

### 코드

`init_redis.py`

```python
import json
import subprocess
import sys

# redis 라이브러리 확인 및 설치
try:
    import redis
except ImportError:
    print("❌ Redis library not found. Installing now...")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "redis"])
    import redis  # Re-import after installation


# Redis 서버 연결을 위한 환경 설정
redis_host = "localhost"
redis_port = 6379
redis_client = redis.StrictRedis(host=redis_host, port=redis_port, decode_responses=True)

with open("data.json", "r", encoding="utf-8") as file:
    data = json.load(file)

redis_client.set("itemData", json.dumps(data))

print("✅ Data insertion completed!")
```

하지만 위처럼 작성하면 너무 정적이 된다.(key가 정해져 있으며, 파일 이름 등이.. 다른 프로젝트에 써먹지를 못한다)

이를 확장성 있게 `data`폴더 안에 있는 파일들을 전부 집어넣는 것으로 바꿨으며, key는 파일 이름, value는 파일 내용이 되도록 바꿨다.

`init_redis.py`

```python
import os
import json
import subprocess
import sys

try:
    import redis
except ImportError:
    print("❌ Redis library not found. Installing now...")
    subprocess.check_call([sys.executable, "-m", "pip", "install", "redis"])
    import redis  


redis_host = "localhost"
redis_port = 6379
redis_client = redis.StrictRedis(host=redis_host, port=redis_port, decode_responses=True)

data_folder = "data"


if not os.path.exists(data_folder):
    print("⚠️ Data folder not found. Skipping data insertion.")
    sys.exit(1)


file_count = 0
for filename in os.listdir(data_folder):
    file_path = os.path.join(data_folder, filename)

    if os.path.isfile(file_path):
        with open(file_path, "r", encoding="utf-8") as file:
            value = file.read()

        redis_client.set(filename, value)
        file_count += 1
        print(f"✅ Inserted {filename} into Redis")

print(f"🎉 Successfully inserted {file_count} files into Redis!")
```

기본적인 Redis이미지를 사용한 `docker-compose.yml`이다.

`docker-compose.yml`

```yaml
version: "3.8"

services:
  redis:
    image: redis:latest
    container_name: redis-server
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - ./redis-data:/data  # Redis 데이터 영속성을 위한 볼륨 매핑
    command: [ "redis-server", "--appendonly", "yes" ]
```

위 내용을 한번에 사용할 init.sh

```bash
#!/bin/bash

docker compose up -d

python3 init_redis.py
```

### 환경

기본 `Ubuntu` 환경이며, 위 코드 들을 같은 폴더내에 두고 `data` 폴더를 같은 경로에 생성해야 한다.

실행 명령어

```bash
sh init.sh
```

마치며
---

### 더 처리해야 할 내용들

* Linux 환경이 아닌 Windows에서도 작동 가능하게 만들어야 한다.
* 파일을 읽는 작업은 매우 올래걸리는 작업으로 각 파일을 읽고 집어넣는 작업을 **비동기화** 해야 한다.
* json이 아닌 다른 파일에서도 확장자를 확인해 처리할 수 있어야 한다.
* 설정 환경에 대한 내용, 작업 내용들을 GitHub에 제대로 정리 해야 한다.

[GitHub Link](https://github.com/faulty337/redis-init)

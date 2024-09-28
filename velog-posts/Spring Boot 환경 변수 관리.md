Intro
-----

연습삼아 올리던 Git 레포가 있다. MQTT 안정성 테스트를 위해 해당 프로젝트에 추가, 연결, 배포를 했는데어느날 application.properties에 실제 사용하는 주소와 몇몇 정보가 그대로 적어 놓고 push 해버린걸 깨닳았다. 이렇게 되면 몇몇 문제가 발생한다.

1. 보안성 문제 : 내가 올린 주소와 계정 ID 등은 실제 접속할 수 있는 여지를 줄 수 있으며 이미 push한 순간 Git에 기록되어 레포가 삭제되기 전까지 계속해서 남는다.
2. 유연성 문제 : 만약 주소를 바꾸고 배포를 해야 한다면 아래와 같이 매우 불편해지며 대응에 있어 딜레이가 걸린다. 먼저 application파일을 고치고 이를 push하고 main에 merge하기 위해 pullRequest를 하고 승인을 받고 dev에 merge하고 테스트 이후 main에 적용한다.

### 해결 방법

위와 같은 문제를 막기 위해서는 이러한 환경 설정 과정이 개발 공간과 분리되어 있어야 한다.

1. 외부 파일로 관리

* Spring boot에서 application 파일은 무조건 내부에 있어야 하는 것은 아니다. 내부에 있어도 되나 외부 파일을 읽을 수 있는데 아래는 Spring boot가 application 설정 우선 순위이다.

  > 1. 개발자가 @PropertySource 어노테이션을 이용한 설정.
  > 2. .jar 파일을 돌릴 때 사용하는 명령줄에 의한 설정
  >    * ex) java -jar app.jar --server.port=9000
  >    * 설정을 일일히 전부 적어야 한다.
  > 3. .jar 외부에 있는 application 파일
  > 4. .jar 내부에 있는 application 파일
  > 5. 기본 설정
* 간단히 적용하기 쉬운 것들만 작성한 것이고 이외에 테스트, profile 등이 순위에 포함된다.

* 위와 같이 외부 파일로 설정한다는 말은 에초에 application파일을 올리지 않는다는 의미이다.

<br />

2. 환경 변수 이용

```yml
mqttSetting.mqttUrl = ${MQTT_URL}
mqttSetting.subId = ${MQTT_SUB_ID}
mqttSetting.pubId = ${MQTT_PUB_ID}
```

* 위와 같이 실제 값이 아닌 변수가 들어가는 방법이다. 그렇다면 해당 변수가 어떻게 들어가는지 알아야 한다.
* 이는 jar파일이 실행되는 위치에 설정된 환경 변수로 Ubuntu 기준

  ```shell
  export MQTT_ID=localhost:1883
  ```

  위와 같이 설정 이후에 들어간다.
* 이외에 개발하는 과정에 있어 이를 적용하는 방법이 있는데 IntelliJ에서는 이러한 환경변수를 설정한 환경으로 실행할 수 있게 해준다.

1. 실행 구성 -\> 실행 프로젝트 -\> 편집 ![](https://velog.velcdn.com/images/faulty337/post/f3588b0a-cc7a-490a-94e3-23caa009d345/image.png)

2. 옵션 수정 -\> 환경 변수 활성화 ![](https://velog.velcdn.com/images/faulty337/post/bb78cd35-5d1c-4a39-a23e-7bacd2f3a382/image.png) ![](https://velog.velcdn.com/images/faulty337/post/a40b7dea-e8b3-48e5-af29-0c2eb5ef7e5c/image.png)

3. 환경 변수 추가 ![](https://velog.velcdn.com/images/faulty337/post/568dd319-059b-48df-88b4-cf6a12fd279e/image.png) ![](https://velog.velcdn.com/images/faulty337/post/e9a69dec-3ca5-49b5-95c7-4c3e87154c1b/image.png) ![](https://velog.velcdn.com/images/faulty337/post/4df2dd47-45bc-448b-81a4-a2db0ebb5ef2/image.png)

후기
---

이러한 환경변수를 다루는 방법은 배포 시 개발환경과 실제 서비스 환경에서 사용하는 환경변수가 다르고 이를 적용하는데 있어 유연한 적용 방법이 있어야 한다. 현재에는 위와같이 환경변수로 분리하고 배포 환경에 직접 접속하여 환경변수를 만들거나 파일을 작성하여 하고 있으나, 불편함을 무시하고 지나가기엔 개발자로서 용납 못하는 부분이다. 해당부분은 이후에 다시 개선하여 올릴 예정이다.

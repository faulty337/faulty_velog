대충 그럴싸한 서론
----------

당연한 이야기지만 개발 환경과 배포 환경에서의 DB는 당연히 달라야 하며, 이를 매번 application.propertis를 변경해가며 할 수는 없다.

때문에 Spring Boot에서 Profiles을 환경을 이용해서 다른 DB를 사용한 간단한 예시를 남기고자 한다.

바로 본론
-----

먼저 DB환경에 대해 작성되는 applicaiton.properties에 대해 작성이 필요한데, 간단히 생각해봐도 Profiles마다 이 적용되는 applicaion.properties를 다르게 사용해야 한다.

### Profiles별 applicaion.properties or applicaion.yml 적용

정말 간단한데, `applicaion-{profiles}.properties` `applicaion-{profiles}.yml` 와 같이 profiles을 `-`와 같이 써주면 해당 profiles명에 따라 적용된다.

예를 들어 `applicaion-dev.properties` `applicaion-prod.properties` 와 같이 설정하면 `-Dspring.profiles.active=dev` 에서는 `applicaion-dev.properties` 가 실행되게 된다.

자 그렇다면 간단히 dev에서는 h2, prod에서는 mysql을 사용하도록 설정해보자

`applicaion-dev.properties`

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=backend
spring.datasource.password=test
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

`applicaion-prod.properties`

```properties
spring.datasource.url=jdbc:mysql://mysql:3306/testdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=backend
spring.datasource.password=test
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
```

* 당연히 외부 db를 사용하는 곳에서는 외부 db의 url을 작성해줘야 한다. 필자는 docker를 이용해 전체 관리하기 때문에 container의 이름을 작성했다.

### Spring Boot profile 설정 방법

Profile별 db를 설정했다면 profile을 설정해줘야 하는데 IntelliJ에서는 간단한 방법이 있다.

![](https://velog.velcdn.com/images/faulty337/post/800528e6-5d82-4856-b762-08ba53c12ffd/image.png)

* Configuration Edit을 이용해 수정 창을 연다.

![](https://velog.velcdn.com/images/faulty337/post/824d3aa9-5fbd-40c8-8188-1b03029b9dbb/image.png) Spring Boot Configurations를 추가한다.

![](https://velog.velcdn.com/images/faulty337/post/eae92e2d-2e31-4423-851e-f925998e9706/image.png)

* Name: 화면에서 표시될 이름이다. 알아보기 쉽도록 profile과 일치시켜 주면 좋다.
* Build and run
  * 실행시킬 classpath, 실행시킬 메인 클래스를 지정해야 한다.
    * 정 모르겠으면 기본으로 생성되는 Configuration을 참고하면 된다.
* Active profiles : 적용할 Profiles 이름을 넣으면 된다. ex) dev , prod

이후에 저장하면 되는데 결과는

![](https://velog.velcdn.com/images/faulty337/post/9cf496f8-8413-4dcc-b3de-29672fbe804d/image.png)

와 같이 나오면 된다.

대충 그럴싸한 결론
----------

개발에 있어서 환경은 매우 중요하다고 생각한다. 같은 개발속도라도 이렇게 분리된 개발환경은 더 빠른 속도와 안정성을 보장할 수 있기에 필수 과정이라고 생각한다.

다시 개발하러..

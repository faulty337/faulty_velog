Intro
-----

부트캠프 혹은 다른 프로젝트를 진행하며 ResponseDto라는 Response 의 형태를 일괄적으로 처리를 진행했다. 코드가 이쁘기는 했지만 사용하는 이유를 물어보면 막상 당당히 대답할 자신은 없었기에 정리를 시작하게 되었다.

코드
---

```java
@NoArgsConstructor
@Getter
public class CommonResponseDto {
    private Integer status;
    private String message;
    private Object data;
    private LocalDateTime timestamp = LocalDateTime.now();
}
```

목적
---

1. Controller에서 최종적으로 내보내는 Dto에 대한 형태를 통일한다.
2. 응답 시 처리 결과 뿐만이 아닌 `status`, `message`, `timestamp`에 대한 값을 추가로 내보낸다.

이유
---

1. 어떠한 형태에 대해 표준화한다는 것은 일관성 있게 처리할 수 있음을 의미하고 백엔드와 프론트간의 고정된 필드를 기대하고 처리할 수 있다.
2. timestamp와 status같은 메타데이터를 포함하여 이후 디버깅 등에 활용 가능하다.
3. 이쁘다.

Refactor
--------

### Object 지양

* Object는 모든 클래스의 조상 클래스 이기 때문에 어떠한 자료형이든 담을 수 있고 직렬화에 있어 문제될 것이 없어 Object를 사용해도 괜찮다.
* 하지만 나는 Object가 무분별하게 사용된 코드를 보며 Object에 담길 필드들을 추측하며 꺼내는 작업들을 해오며 Object알레르기가 생겼기 때문에 이때 보통 사용하는 제네릭을 이용했다.

  ```java
  @NoArgsConstructor
  @Getter
  public class CommonResponseDto<T> {
    private Integer status;
    private String message;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();
  }
  ```

  ### HttpStatus

  응답 시 CommonResponsDto를 바로 내보내는 것이 아닌 ResponseEntity를 사용할 예정이다. 이때 ResponseEntity는 HttpStatus를 필수로 지정하여 생성되므로 작성해주는데 이 HttpStatus와 CommonResponseDto에 status의 회사 특유의 코드를 사용한 것이 아니라면 통일되어야 한다. 이때 매번 생성할 때마다

  ```java
  CommonResponseDto<String> response = new CommonResponseDto<>(200, "message", "data", LocalDateTime.now())
  new ResponseEntity<>(response, HttpStaus.valueOf(response.getStatus()))
  ```

와 같이 `HttpStaus.valueOf(response.getStatus())`를 계속해서 쓰는 것 보다 CommonResponseDto가 HttpStatus값을 내보내는 함수를 따로 가지고 있는 것이 이뻐보였다.

###### ※ 당연히 HttpStatus와 매칭되지 않았을 때의 대한 예외처리는 들어가야 한다.

```java
public HttpStatus getHttpStatus() {
    try{
        return HttpStatus.valueOf(status);
    }catch (IllegalArgumentException e){
        return HttpStatus.OK;
    }
}
```

### 생성자

매번 모든 필드에 대해 정의해주며 생성하기 보단 Default로 들어가는 값이 있기에 필요 필드만 받아 처리하는 생성자를 만들었다.

```java
public CommonResponseDto(String message, T data) {
    this.status = HttpStatus.OK.value();
    this.message = message;
    this.data = data;
}

public CommonResponseDto(Integer status, String message, T data) {
    this.status = status;
    this.message = message;
    this.data = data;
}
```

### 최종 코드

```java
@NoArgsConstructor
@Getter
public class CommonResponseDto<T> {
    private Integer status;
    private String message;
    private T data;
    private LocalDateTime timestamp = LocalDateTime.now();

    public CommonResponseDto(String message, T data) {
        this.status = HttpStatus.OK.value();
        this.message = message;
        this.data = data;
    }

    public CommonResponseDto(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }


    public HttpStatus getHttpStatus() {
        try{
            return HttpStatus.valueOf(status);
        }catch (IllegalArgumentException e){
            return HttpStatus.OK;
        }

    }
}
```

후기
---

사실 특별할게 없는 코드이지만, 모든 코드는 명분을 가지고 있고 내가 의도를 알고 있어야 하기 때문에 모든 과정을 적다보니 길어진 듯 하다.

[Git 이슈](https://github.com/faulty337/WhyIsThisWorking/issues/1)

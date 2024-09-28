HTTP에서 GET과 POST로 나눈 이유는 해당 메소드가 자원을 지니는 곳이 다르기 때문이다. GET의 경우 URL에 넣지만, POST의 경우 URL, Body에 넣을 수 있기 때문에 각 방법에 대해 작성하고자 한다.

1. GET
------

기본적으로 '조회' 기능을 한다. 따라서 내부에 있는 데이터를 변경하지 않으며 단순히 데이터를 조회한다. 이를 반복적으로 진행하더라도 서버의 상태를 변경하지 않기에 멱등성을 가진다.

GET은 기본적으로 자원을 URL에 실어서 보낸다. 따라서 받는 법도 URL에서 받아야 하는데 Spring에서는 어노테이션을 지원한다.

### @PathVariable

해당 어노테이션은 URL에 붙은 데이터를 매개변수로 가져오는 어노테이션이다.

만약 `http://localhost:8080/variable1/val3333` 이라는 요청이 들어오면

```java
@GetMapping(value = "/variable1/{variable}")
public String getVariable1(@PathVariable String variable){
  return variable;
}
```

이 코드는 해당 요청에서 variable라는 변수와 매칭되어 `getVariable1()`함수의 매개변수 variable에 들어가게 된다. 이때 {}안에 변수명과 아래 변수명은 동일해야 한다.

매개변수를 다르게 지정하고 싶다면

```java
@GetMapping(value = "/variable2/{variable}")
public String getVariable2(@PathVariable("variable") String var){
  return var;
}
```

와 같이 `@PathVariable`에 명시해주면 된다.

다만 지금 url의 형태를 보면 자원을 담는다와는 조금 이질적이다. 내가 아는 자원을 담는다는 표현대로 하면 `http://localhost:8080/variable1?variable=var3333`이 될텐데.. 아마 해당 자원이 무엇을 표시하는지에 따라 다른거 같다.

만약 `http://localhost:8080/variable1/val`이라는 url에 매핑된곳이 존재하면 어떻게 될까? PathVariable이 낚아 챌까?

다행이게도 만약 다른곳에서

```java
@GetMapping(value = "/variable2/val")
public String getVariable2(){
  return "되나..?";
}
```

위와같이 선언되있으면 해당 함수가 우선적으로 실행된다. 그렇더라도 이는 충분히 햇갈릴 수 있으니 용도에 맞게 사용하자

### @RequestParam

단순히 해석하면 요청에 있는 매개변수를 의미한다. 똑같이 URL에 있는 값을 가져오는 방식이지만 조금 다르다. `@PathVariable`와는 다르게 `@GetMapping` 어노테이션에 URL에 변수를 지정하지 않아도 된다.

만약 `http://localhost:8080/variable1?name=이름&email=전화번호&phoneNumber=전화번호` 이라는 요청이 들어오면

```java
@GetMapping(value="/request1")
  public String getRequestParm1(
        @RequestParam String name,
        @RequestParam String email,
        @RequestParam String phoneNumber) {
    return name + " " + email + " " + phoneNumber;
  }
```

위와 같이 작성시 각 매개변수에 해당 값이 들어가게 된다. 위에서도 말했지만 `@PathVariable`와는 다르게 `@GetMapping(value="/request1")`부분에 따로 자원을 지정하지 않아도 되며, 요청 URL에서는 해당 자원을 요청은 `?`로 구분짓고 각 자원의 구별은 `&`로 구분한다.

위와 같은 경우는 값을 알고 있을 경우이다. 만약 값을 모른다면 이를 유연하게 받을 수 있어야 하는데 이떄 **Map**을 사용한다.

```java
@GetMapping(value = "/request2")
public String getRepustParam2(@RequestParam Map<String, String> param){
  StringBuilder sb = new StringBuilder();
  param.entrySet().forEach(map ->{
      sb.append(map.getKey()+" : " + map.getValue() + "\n");
  });
  return sb.toString();
}
```

물론 이후에는 각 보내지는 자원이 정해져야하는 것이 맞다. 다만 지금 값을 받을 때 변수명이 저절로 Map에 key로 매핑되며, 값은 value로 매핑된다는 부분은 알고 넘어가야한다.

### DTO이용

**D** ata **T** ransfer **O**bject로 계층에서의 데이터 전송시 사용되는 객체이다. 이를 이용하면 데이터를 쉽게 주고 받을 수 있게 된다.

```java
public class RequestDto {
    String name;
    String email;
    String organization;


    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getOrganization() {
        return this.organization;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
```

위와 같이 DTO를 만들면

```java
@GetMapping(value="/request3")
public RequestDto getRequestParam3(RequestDto requestDto){
    return requestDto;
}
```

위와 저절로 각 변수명에 맞게 매핑되어 값을 받을 수 있다. 다만 DTO가 가지고 있는 변수명과 자원의 변수명이 일치해야 하니 주의하자

POST
----

HTTP 메서드 중 '생성'시 사용되는 메서드이다. 데이터를 추가할 때 사용되는 메서드로 보통 추가되기 위한 필수 데이터를 담게 된다. POST는 데이터가 기본적으로 Body에 담아서 오기 때문에 url에서 찾을 수 없다 위와 같은 특성 때문에 무조건 생성에만 사용되는 것은 아닌데, 데이터가 노출되지 않고 Body에 닮겨져서 오기 때문에 Get방식 보다는 안전하기 때문에 인증 관련에서도 사용할 수 있다.

때문에 Get에서 받는 방식과는 다른 어노테이션을 사용한다.

그리고 POST의 경우 데이터를 입력 받는 방식을 크게 2가지로 나눌 수 있는데 formData와 Json이다. 보내는 방식에 따라 받는 방식도 달라지니 주의하자.

### @RequestBody

Get에서는 `@RequestParam`을 사용했었는데 여기서는 Body로 변했다. 그 이유는 위에서 말했듯이 데이터가 Body에서 닮겨져서 오기 때문이다.

```java
@PostMapping(value="/member")
public String postData(@RequestBody String data){

    return  data;
}
```

사용방법은 `@RequestParam`과 거의 동일하다. 하지만 사용처가 조금 달라지는데 `@RequestBody`은 Body에 있는 모든 데이터를 매개변수에 담는다. 따라서 변수의 지정이 어렵다는 말이다.

마찬가지로 받는 데이터가 정해지지 않았을 때 **Map**을 이용해 받을 수 있다.

```java
@PostMapping(value="/member") //위와 다르게 PostMapping로 했는데 이렇게 하면 method를 정의안해줘도 됨
public String postMember(@RequestBody Map<String, Object> postData){
    StringBuilder sb = new StringBuilder();

    postData.entrySet().forEach(map->{
        sb.append(map.getKey() + " : " + map.getValue() + "\n");
    });

    return  sb.toString();
}
```

하지만 이때 보내주는 측에서 Json의 형태로 데이터를 보내줘야 한다.

### DTO

POST에서도 마찬가지로 DTO의 형태로 받을 수 있다. **이때 `@RequestBody`의 유무에 따라 Json인지 formData형태인지 달라진다.**

```java
@PostMapping(value = "/member2")
public RequestDto postRequestDto2(@RequestBody RequestDto requestDto){
    return requestDto;
}
```

위 형태를 보면 `@RequestBody`어노테이션을 사용했는데 이경우 Json의 형태로 보내줘야 받을 수 있다.

```java
@PostMapping(value = "/member3")
public RequestDto postRequestDto2(@RequestBody RequestDto requestDto){
    return requestDto;
}
```

반대로 `@RequestBody`어노테이션이 없으면 해당 요청은 formData로 데이터가 들어가 있어야 데이터를 받을 수 있게 된다.

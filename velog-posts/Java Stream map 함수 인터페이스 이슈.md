Intro
-----

업무 중 `List<Interface>`에 `Interface`의 구현체를 넣는 작업을 진행했다. 하지만 map() 함수를 통해 구현체를 넣으려고 했으나 타입이 맞지 않는다는 에러와 함께 동작하지 않았다.

원인
---

알겠지만 `interface`를 implement한 구현체는 `interface`에 대입할 수 있다.

```java
interface Bone {
    Integer getDensity();
}

class Body implements Bone {
    @Override
    public Integer getDensity() {
        return 0;
    }
}

public class MainClass {
    public static void main(String[] args) {
        Bone bone = new Body(); // Body 클래스를 사용해서 인스턴스 생성
        Integer density = bone.getDensity();
        System.out.println("Density: " + density);
    }
}
```

그리고 `stream().map()`함수는 `List<Bone>` 와 같은 Collection 인터페이스를 구현하는 클래스에서 사용되는데 stream()은 자료형을 stream 형태로 바꾸고 이후 map(), filter(), reduce() 와 같은 함수를 통해 연산이 가능하고 그중 map()은

```java
<R> Stream<R> map(Function<? super T, ? extends R> mapper)
```

위처럼 mapper 즉, Function형태를 받는데 이때 Function은 interface로 매개변수 값을 받아 특정 자료형으로 반환하는 apply() 가 구현되어 있어야 한다. 이러한 Function은 람다식으로 간단히 구현 가능한데 이를 위 인터페이스를 이용해 작성하면

```java

List<Bone> boneList = valueList.stream().map(value -> new Body(value)).toList();
```

와 같이 구현체를 이용해 작성이 가능.. 하지 않다. 여기서 `List<Bone>`과 `List<Body>`의 타입이 다르다고 에러가 발생한다.

이유
---

이유는 `Bone`에 `Body`대입은 가능하지만 `List<Bone>`에 `List<Body>`대입은 불가능하다. 상속관계가 `Bone`과 `Body`에 있는 것이지 `List<Bone>`과 `List<Body>`사이에는 상속 관계가 없기 때문이다. 즉, stream().map() 함수는 하나하나 대입하는 것이 아닌 완전 변환 이후 대입하는 것이기에 형이 틀렸다고 말하는 것이었다.

해결 방법
-----

**for문 사용**

```java
List<Bone> boneList = new ArrayList<>();
for(value : valueList){
    boneList.add(new Body(value));
}
```

List 끼리의 대입이 안되니 대입이 가능한 Bone과 Body끼리 한하나 대입하며 집어넣어 List를 완성하는 식이다.

**형변환**

```java
List<Bone> boneList = valueList.stream().map(value -> (Bone)new Body(value)).toList();
```

집어 넣을 때 강제 형변환 `(Bone)`을 통해 에초에 `List<Bone>`에 `List<Bone>`형을 대입하는 방법이다.

후기
---

그냥 Interface와 Stream 사용 미숙이었다. 덕분에 stream의 map 구현에 대해 보기도 하고 Function 인터페이스에 대해 보기도 했기에 의미가 있다고 생각한다.

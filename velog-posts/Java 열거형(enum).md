자바를 조금 해봤지만 이 열거형에 따로 배운적이 없었다. 이번에 책을 통해 enum을 잠깐 맛보았는데 책에 있는 내용으로는 enum의 필요성이 느껴지지 않아 넘어갔다. 멘토님과 이야기중 어떻게 Stream에서 enum으로 넘어갔는지 모르지만 enum에 대해 이야기 했는데 여러 장점이 있는 놈이였다.

enum
----

일단 enum이라고 써져있지만 **클래스**이다. enum은 서로 연관된 상수들의 집합이다.

### 상수

여기서 상수라고 하면 절대 변하지 않는 값을 말한다.

```java
private final static int APPLE = 1;
```

여기서 변하지 않게 해주는 키워드는 `final`이다. 만약 이러한 상수가 여러 종류 있게 된다면

```java
private final static int APPLE = 1;
private final static int GRAPE = 2;
private final static int PEACH = 3;
private final static int PEAR = 4;
```

가 된다. 그리고 이 Fruit라는 클래스로 묶을 수 있으므로 아래와 같이 된다.

```java
class Fruit{
  private final static int APPLE = new Fruit();
  private final static int GRAPE = new Fruit();
  private final static int PEACH = new Fruit();
  private final static int PEAR = new Fruit();
  private Fruit(){}
}
```

위 상수값을 `new Fruit()`로 인스턴스 객체를 집어넣었는데 이는 1이라는 int값을 가지고 있을 때 비교문에서 다른 1이라는 값과 비교가 될 수 있으며 이는 상수라는 의미를 벗어나는 상황이다. 하지만 객체끼리는 비교할 수 없는 것을 이용해 `new Fruit()`로 인스턴스를 만들어주었다.

`private Fruit(){}`처럼 생성자를 private해서 인스턴스를 생성하지 못하도록 했다. 이렇게 되면 밖에서는 인스턴스를 생성하지 못하고 오직 안에서 생성된 APPLE이라는 인스턴스를 참조할 수 밖에 없다는 것이다.

### enum 선언

위 Fruit클래스를 enum으로 선언해보자

```java
enum Fruit{
    APPLE,
    GRAPE,
    PEACH,
    PEAR
}
```

이게 끝이다.

위에서는 APPLE, GRAPE 하나하나가 인스턴스 객체였다. 그렇다면 enum에 있는 요소들도 객체일까? 코드를 추가하면 금방 확인할 수 있다.

```java
enum Fruit{
    APPLE,
    GRAPE,
    PEACH,
    PEAR;
    Fruit(){
        System.out.println("Fruit : " + this);
    }
}
public static void main(String[] args) throws IOException{
    Fruit fruit = Fruit.APPLE;

}
```

![](https://velog.velcdn.com/images/faulty337/post/2bd3f732-c19b-413c-af2a-42a18cb5c4b5/image.png)

이는 Fruit 클래스가 호출됨과 동시에 과일 4개가 호출되었다는 뜻이다. 즉, 각 상수마다 객체 인스턴스라는 상태이며 생성자를 통해 이를 건들 수 있다는 말이다.

위와 비교하면 확실히 코드도 줄고 보기도 좋아졌다. 하지만 단순히 이런 역할이면 그냥 String을 써도 되지 않을까?

이건 enum만의 다른 장점이 있기 때문에 **상황에 맞춰 사용하는 것**이 맞다고 한다.

### 장점

#### 1. 허용한 값들을 제한할 수 있다.

위 코드에서 갑자기 Watermelon이 들어온다고 했을 때 enum선에서 제한이 가능하다.

#### 2. 리팩토링시 변경 범위가 최소화 된다.

내용에 추가가 필요하더라도 enum만 변경하면 된다고 하는데 이부분은 직접 경험해봐야 할거 같다.

#### 3. 데이터들 간의 연관관계를 표현할 수 있다.

아까 위에서는 1이 APPLE, 2가 GRAPE 이런식으로 다른 데이터들간의 관계를 가지고 있었다. 만약 이를 다른곳에서 다루게 된다면 APPLE일때 1로 변경해주는 코드가 따로 존재해야 한다. 매번 이를 거치는 것도 문제고 만약 다른 종류가 생기면 해당 관계를 매핑해줄 새로운 코드가 필요할 것이다.

이떄 enum을 사용하면!(누가보면 enum을 파는줄 알겠다)

```java
enum Fruit{
        APPLE(1),
        GRAPE(2),
        PEACH(3),
        PEAR(4);

        private int value;

        Fruit(int value){
            this.value = value;
        }
        public int getvalue(){
            return this.value;
        }
}
```

이런식으로 표현이 가능하다. 이후에 다른 데이터도 충분히 넣을 수 있다.

```java
enum Fruit{
        APPLE(1, "사과"),
        GRAPE(2, "포도"),
        PEACH(3, "복숭아"),
        PEAR(4, "배");

        private int value;
        private String name;

        Fruit(int value, String name){
            this.value = value;
            this.name = name;
        }
        public int getvalue(){
            return this.value;
        }
        public String getname(){
            return this.name;
        }
}
```


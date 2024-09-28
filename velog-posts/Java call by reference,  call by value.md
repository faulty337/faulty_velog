코딩을 할 때 중요하다고 생각되는 몇가지가 있는데 그 중 하나가 '컴퓨터처럼 생각하자'이다. ![](https://velog.velcdn.com/images/faulty337/post/b7525eb9-f6a9-4f11-970f-062f81c752b8/image.png)

사람이 생각하고 행동하는데 있어서 '당연히' 진행되고 이를 인지 못하는 부분이 차이가 있기 때문에 우리가 당연히라고 생각되는 부분이 전혀 다르게 동작되는 부분이 있다.

내가 생각했던 배열 복사나 파라미터로 객체를 넘겼을 때이다.

참조
---

Java에는 크게 두가지 타입으로 나눌 수 있다. **'기본 타입과 참조타입'** 그중 말썽을 일으키는 것이 보통 참조 변수였다.

두개로 나누는 기준은 **'JVM에서 해당 변수의 값이 Stack영역에 저장되냐 Heap영역에 저장되냐'** 이다.

그렇다고 매번 변수가 저장되는 위치를 확인하면서 구분할것이 아니니 다시 구분짓는다면 **'클래스로 만들어진 객체 혹은 배열, 인터페이스'**이다.

클래스인지 쉽게 구별하는 방법은 '해당 타입이 영어 대문자로 시작하냐'로 또 확인하면 된다.(Java 관례상 클래스의 이름은 대문자로 시작한다.)

저장되는 곳을 보면

##### Int클래스

```java
public class Int {
    private int value;

    public Int(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }
    public void setValue(int value){
        this.value = value;
    }
}
```

위와 같이 클래스를 만들었다면

```java
int var1 = 3;
Int var2 = new Int(3);
```

이 코드가

![](https://velog.velcdn.com/images/faulty337/post/4dd92a0c-9dbd-4e63-b0e0-f70f3db6d2fe/image.png)

대충 이 그림처럼 된다. 보면 기본타입인 int var1의 경우 값 자체가 Stack영역에 저장되며, 클래스인 var2 는 Stack에 해당 객체의 번지를 저장한다.

자 그렇다면 배열은 기본 타입일까? 이미 위에서 말했지만 배열은 참조 타입이다.

뭔가 생각해보면 Stack에 단순데이터를 쌓는다 하면 편하지만 만약 길이가변적이게 되고 여러 데이터가 들어온다고 생각하면 그때부터 신경써야할일이 한두가지가 아닐것이다. 값을 저장한다면, 그 값들을 하나씩 모조리 집어넣는다? 이것도 이것대로 문제가 생길것이다. 나같아도 배열류는 그냥 heap에다 던져버릴거 같다.

call by reference, call by value
--------------------------------

함수 호출 방식이다. 옛날에 배우긴 했지만 코딩하면서 딱히 신경 안쓰고 부딪히면서 작성하느라 잊어버렸다.

### call by value

함수가 호출될 때 전달되는 변수의 **'값'** 자체를 **복사**한다. 보통은 call by value의 형태를 가진 언어를 다루다보니 이미 익숙해져서 당연한거 아냐? 라고 생각할 수 있다. 함수는 기존 공간과 별개의 임시 공간을 생성하는데 이떄, 매개변수로 받는 값이 있다고 가정하자.

```java
public static void main(String[] args){
    int a = 1;
    int b = 2;
    int result = 0;
    sum(a, b, result);

}

public static void sum(int var1, int var2, int result){
    result = var1 + var2;
}
```

위 함수를 실행하면 result가 바뀔까? 당연히 바뀌지 않는다는 것을 알고 있다. 왜냐하면 call by value의 형태로 매개변수로 받는 놈은 기존 변수의 '값'을 복사한 것이기 때문이다. 복사한 친구를 변경한다고 원본이 바뀌지 않지 않은가.

### call by reference

call by reference는 함수에 변수의 참조 자체를 보내버리는 것이다. 이렇게 하면 함수에서 변수를 변경하는 행위는 참조되어 있는 값을 바꾸는 것이기에 함수 밖에 있는 변수에 영향을 준다.

그렇다면 위에 있던 [클래스](#int%ED%81%B4%EB%9E%98%EC%8A%A4)를 사용해서 코드를 작성해보면

```java
public static void main(String[] args) {
    Int var2 = new Int(3);
    System.out.println("함수 실행 전 : " + var2.getValue());
    transInt(var2);
    System.out.println("함수 실행 후 : " + var2.getValue());

}
public static void transInt(Int var1){
    var1.setValue(7);
}
```

![](https://velog.velcdn.com/images/faulty337/post/8926360c-b3a4-4e65-8fa2-fb5536c4460d/image.png)

위와 같은 결과를 얻는다. 아까와의 차이점은 타입이 int에서 Int클래스로 변경햇다는 것이 가장 큰 차이점이다. setValue는 단순히 클래스내의 필드를 변경하는 함수이다.

다른 클래스를 사용하거나 만들거나 해서 저런식으로 작성하면 위와 비슷한 결과를 볼 수 있다. 심지어 배열도 그렇다.

```java
public static void main(String[] args) {
    int[] arr1 = {3};
    System.out.println("함수 실행 전 : " + arr1[0]);
    transArr(arr1);
    System.out.println("함수 실행 후 : " + arr1[0]);

}
public static void transArr(int[] arr1){
    arr1[0] = 7;
}
```

![](https://velog.velcdn.com/images/faulty337/post/29d5ab19-d8fc-478b-a385-d4b76eb39086/image.png)

### 예시

그렇다면 Java에서 참조변수를 사용하면 call by reference가 될까? 이건또 아닌 것이

```java
public class ClassTest {
    public static void main(String[] args) {
        Int var1 = new Int(3);
        System.out.println("함수 실행 전 : " + var1.getValue());
        transInt(var1);
        System.out.println("함수 실행 후 : " + var1.getValue());
    }

    public static void transInt(Int par){
        par = new Int(7);
    }
}
```

![](https://velog.velcdn.com/images/faulty337/post/822a620b-e27b-4420-ae18-4fd31e691fba/image.png)

이 코드는 영향을 못주고 있다. 함수 내부를 보면 `new Int(7)`를 이용해 새로운 객체를 만들어 집어넣고 있다. 이를 코드 순서대로 보면

#### 1. 선언

![](https://velog.velcdn.com/images/faulty337/post/468bab60-10c5-41ca-9a2f-4ba1b6132c83/image.png)

stack영역에 va1에다가 Int객체의 참조를 넣는다.

#### 2. 함수 호출

![](https://velog.velcdn.com/images/faulty337/post/ac47ad99-d7f3-4158-a5fd-762c81c8fbb6/image.png)

함수가 호출되면서 par에 var1이 참조하고 있던 번지를 그대로 복사해간다.

#### 3. 함수 실행

![](https://velog.velcdn.com/images/faulty337/post/95e9e142-c26e-4457-95e6-f240bbe22057/image.png)

함수가 실행되면서 `par = new Int(3)`코드가 실행되고 par은 새로운 객체를 참조하게 된다.

#### 4. 함수 종료

![](https://velog.velcdn.com/images/faulty337/post/ae5bdae1-4bc4-494e-b7f3-030090b3f21b/image.png)

함수가 종료된다.

#### 결과

그림을 보면 var1은 따로 변경되지 않고 똑같은 객체를 바라보고 있다.

아직 애매할지 모른다. 얼핏 보면 지금 함수에다가 변수의 참조를 넘겨준 것이 아닌가?

처음엔 그렇게 생각했는데 call by reference는 조금 더 딥(deep)한 친구이다. 만약 call by reference라면 par에 var1이 참조하고 있는 번지를 주는 것이 아닌 var1 자체를 참조한다. 값을 변경시 var1이 가르키는 값 혹은 주소를 변경하는 것으로 결국 결과가 7로 변한다는 말이다.

하지만 결과가 3이니

Java는 call by value 이다.

C언어의 경우에도 포인터개념을 사용해서 call by reference를 구현할 수 있는거지 기본적으로는 call by value이다.

### 정리

Java가 call by value라고 무조건 단정 지어서 코딩하기엔 JVM의 구조상 어떻게 보면 call by reference와 비슷한 상황이 펼쳐지기도 한다.

Java를 배운다면 'call by value니깐', 'call by reference니까' 이라기보다는 각 영역에서 진행되는 과정을 이해하는 것이 더 중요하다고 생각한다.

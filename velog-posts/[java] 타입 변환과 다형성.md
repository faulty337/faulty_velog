책에서는
> **사용 방법은 동일하지만 다양한 객체를 이용해서 다양한 실행 결과가 나오도록 하는 성질**

이라고 되어 있는데 조금 다른 느낌으로 '여러 타입을 가질 수 있다' 라고 봐도 무방하다.

위에 말을 가능케 해주는 것이 상속과 인터페이스가 있다.

자동 타입 변환
--------

위에서 말했듯이 '**여러 타입을 가진다** ' 즉, '**여러 타입이 될 수 있다**'라는 말이다.

자동 타입 변환은 객체 이외에 일반 자료형에서도 이루어진다.

```java
int i= 3;
Long l = i;
```

위와 같이 long가 int보다 취할 수 있는 범위가 크기 때문에 자동으로 타입이 변환된다.

반대의 경우에는 `(int)` 와 같이 강제 변환 해줘야 한다.

위는 일반 자료형이였다.

클래스의 경우에는 조금 다른 느낌으로 진행된다.

클래스는 자식클래스가 부모클래스로의 자동 변환이 가능하다.

때문에 항상 받기로는 부모클래스로 받는다고 기억하자

```java
Child c = new Child();
Parents p = c;
```

간단히 예시로 들면

```java
public class ListUtil {

    public int[] listToArray(List<Integer> aList){
        int[] arr = new int[aList.size()];
        for(int i = 0; i < aList.size(); i++){
            arr[i] = aList.get(i);
        }
        return arr;
    }

}
```

```java
public class Main{
    public static void main(String[] args) {
        ListUtil listUtil = new ListUtil();
        // ArrayList<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

        // Vector<Integer> vector = Arrays.asList(5, 4, 3, 2, 1);
        Vector<Integer> vector = new Vector<>(Arrays.asList(5, 4, 3, 2, 1));

        System.out.println(vector.firstElement());

        List<Integer> l = new ArrayList<>();
        // int[] arrL = listUtil.listToArray(l);

        int[] arrList = listUtil.listToArray(list);
        int[] vecList = listUtil.listToArray(vector);        
    }
}
```

보다시피 분명 list와 vector의 자료형은 다르다. 각자 ArrayList, Vector이다. 하지만 이 둘은 List라는 인터페이스를 가지고 있다.

때문에 매개변수에 제한을 상위 클래스로 함수를 보다 자유롭게 사용할 수 있게 된다.

Override
--------

상속받은 메소드를 재작성한다.

```java
public class Parent {
    public void method1(){
        System.out.println("method1");
    }

    public void method2(){
        System.out.println("method2");
    }
}
```

위와 같은 Parent가 있고, `method1()`와 `method2()`를 가지고 있다.

```java
public class Child extends Parent{
    @Override
    public void method2() {
        System.out.println("METHOD2");
    }

    public void method3(){
        System.out.println("METHOD3");
    }

    public void method4(){
        System.out.println("METHOD4");
    }
}
```

Child는 Parent를 상속 받으며, Parnet가 가지고 있던 `method2`를 override 했다.

이를 Main.class에서 실행하면

```java
Parent p = new Parent();
Child c = new Child();
Parent p2 = c;

System.out.println("-------Parent1------");
p.method1();
p.method2();
System.out.println("-------Child------");
c.method1();
c.method2();
c.method3();
c.method4();
System.out.println("--------Parent2-----");
p2.method1();
p2.method2();
```

여기서 보면 Parent로 선언된 p2에 Child로 선언된 c를 넣는데 이때 보면, p2에 `method2()`의 내용이 달라진다.

따라서 재정의한 메서드의 경우 재정의된 상태로 부모 객체에 들어가게 된다.

단, Parent 가 가지고 있지 않은 메서드는 실행할 수 없게 된다.

이때 사용하는 것이 강제 형 변환이다.

강제 형 변환
-------

일반 변수의 경우 `(int)`를 붙여줌으로서 데이터 손실을 감안하고 강제로 형 변환이 일어난다.

클래스도 마찬가지로 `(클래스명)`을 통해 강제로 형 변환이 가능하다.

```java
Parent p = new Parent();
Child c = new Child();
Parent p2 = c;

Child c2 = (Child)p2;
c2.method1();
c2.method2();
c2.method3();
c2.method4();
```

위와 같이 강제 형변환을 진행하면 기존 자식 클래스의 메서드를 다시 사용할 수 있게 된다.

메서드 이외에도 필드 내용도 남아있게 된다.

객체 타입 확인
--------

매번 doc를 뒤지거나 해당 내용 안쪽으로 들어가면서 부모클래스인지 아닌지 확인하면서 코드를 짤 수는 없다.

조금 동적으로 움직이기 위해 이를 판단하는 연산자가 있으면 좋은데

이를 instanceof라고 한다.

책에서는 좌항 우항 이런식으로 표현되어 있는데 간단히 말하면 왼쪽에 오는 객체가 오른쪽의 인스턴스이면 true를 반환한다.

```java
if(c2 instanceof Parent){
    System.out.println("맞다!!");
}
```

보면 오른쪽에는 객체가 아닌 클래스가 오기 때문에 잘 기억해두자

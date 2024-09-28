나는 몇번 파이썬을 가르치면서 클래스와 객체에 대해 설명한 적이 있다. 물론 그때마다 막막했다.

객체
---

다른 글에서 이런말을 쓴적이 있다. "객체라는 것을 한번에 완벽히 알려줄 수 있는 사람은 없다. 다만, 스스로 공부하고 개발을 열심히 해서 어느 순간에 다다르면 신께서 따란!하고 깨우치게 해준다."

지금 보면 맞는 말인 듯 하다.

일단 적혀있는 설명으로는
> 물리적으로 존재하거나 추상적으로 생각할 수 있는 것 중에서 자신의 속성을 가지고 있으면서 식별 가능한 것을 말한다.

이걸 객체를 얼핏 알고 있다면 "으음 맞는 말이네"하지만 처음 들으면 "이게 무슨 소리지" 라는 생각을 하게 된다.

일단 객체를 알기 위해서는 클래스를 알아야 한다.

잠깐 비유를 하자면, '사람'이라는 것이 존재할까? 정확히 존재하는 것은 사람으로서 역할을 하는 '나'가 존재하는 것이다. '사람'자체가 아닌 '김범준'이라는 이름을 가지고 27살동안 산 '김범준'이 존재한다.

그렇다면 '김범준'은 사람이 아닌가? ~~말이 좀 이상하네~~ '김범준'은 사람이 할 수 있는 일을 할 수 있으면서 사람으로서의 특징을 가지고 있다. 그럼과 동시에 다른 사람과 구분되는 이름, DNA, 지문, 주민등록번호, 홍채 등이 있다. 이를 '김범준'이 가진 속성이라고 한다.

여기서 등장하는 키워드들을 보면 '사람', '김범준', '속성'이 있다. '사람'은 클래스이고, '김범준'은 객체고 '속성'은 객체가 가지고 있는 식별가능한 속성을의미한다.

자 그럼 객체에 대해 알아봤고 이 객체를 만들기 위해서는 클래스가 있어야 한다.
> 객체는 클래스로 만든다.

클래스
---

위에서 예시로 '사람'이라는 것을 클래스로 말을 했다. 좀더 비유를 하자면 설계도의 느낌을 가진다. 객체를 찍어내기 위한 설계도. 클래스를 선언할 때는 `class`라는 키워드와 클래스 이름이 필요하다.

위 예시처럼 사람(Human)이라는 클래스를 만들어보자
>
> ```java
> public class Human{
> }
> ```

참고로 자바에서는 이것이 기본 형태이다. 반든시 클래스로 구성되어 main이라는 함수를 실행시켜야 동작한다.

자 그럼 '사람'이기위해서는 사람이 가지는 것들을 지니고 있어야 한다. 예를 들어 이름, 나이, 거주지나 주민등록번호 등이 있을 것이다.
>
> ```java
> public class Human {
>     String name; //이름
>     int age; //나이
>     int Residence; //거주지
>     int registrationNumber; //등록번호
> }
> ```

하지만 이 클래스는 할줄아는게 없다. 가진게 속성으로 구분만 되지 이때 할수있는 기능을 함수 또는 메소드라고 한다.

### 클래스 함수

위에서 클래스의 속성들을 넣어서 Human이라는 클래스를 작성했었다. 하지만 아무 기능도 못하고 속성만 가지고있다면 단순히 데이터이지 않은가. 위에서 배운 함수를 클래스에 넣어 해당 클래스가 할수 있는 기능으로 만들 수 있다.
>
> ```java
> public class Human {
>     String name; //이름
>     int age; //나이
>     int Residence; //거주지
>     int registrationNumber; //등록번호
>     public void move(){
>         //히히 움직인다.
>     }
>     public void think(){
>         //히히 생각한다.
>     }
>     public void talk(){
>         System.out.println("아무말");
>     }
> }
> ```

별 영양가 없는 함수지만 이러한 식으로 기능을 추가할 수 있다. 이를 불러오는 방법은
>
> ```java
> Human 김범준 = new Human();
> 김범준.talk();
> ```

위와같이 객체를 생성하여 함수를 호출할 수 도 있다. ![](https://velog.velcdn.com/images/faulty337/post/7f8311eb-d129-4fe7-9909-caeb3b4098fa/image.png)

### 객체 생성

사실 위 코드에서 스포했다.
>
> ```java
> Human 김범준 = new Human();
> ```

이 형태 어디서 많이 봤을것이다.
>
> ```java
> Integer var1 = new Integer(1);
> String str1 = new String("히히");
> ```

그렇다 참조변수도 전부 클래스와 객체로 이루어지는 친구이다. 때문에 우리는 코딩을 했지만 클래스와 객체인지 모르고 그냥 사용했던 것이다.

### GET, SET

근대 지금 코드로는 객체를 만든다고 이름과 나이 등 속성이 정해지지는 않는다. 따라서 우리가 설정해줘야 한다.
>
> ```java
> Human 김범준 = new Human();
> 김범준.name = 김범준;
> 김범준.age = 0;
> 김범준.registrationNumber = 123456;
> ```

위 코드는 객체의 변수를 직접 건들이고 있는데 **이는 객체지향에서 권장하는 방법이 아니다.**

이때 사용하는 것이 Get과 Set이다. 이 Get과 Set은 클래스의 속성을 변경하거나 값을 가져올 때 사용한다.
>
> ```java
> public class Human {
>     String name; //이름
>     int age; //나이
>     int Residence; //거주지
>     int registrationNumber; //등록번호
>     public String getName() {
>         return name;
>     }
>     public void setName(String name) {
>         this.name = name;
>     }
>     public int getAge() {
>         return age;
>     }
>     public void setAge(int age) {
>         this.age = age;
>     }
>     public int getRegistrationNumber() {
>         return registrationNumber;
>     }
>     public void setRegistrationNumber(int registrationNumber) {
>         this.registrationNumber = registrationNumber;
>     }
>     ...
> }
> ```

이러한 식으로 함수를 이용해 객체의 속성에 접근해야 한다. 이렇게 하게 되면 속성이 변경되는 부분이 저 함수가 실행되었을 때 뿐이다. 위 속성을 직접 건들이면 이곳저곳에서 건들 수 있으며 이를 관리하기 힘들어진다.

### 생성자

그런데 보면 사람은 태어나면서부터 주민등록 번호가 정해지고 이름이 생기고, 0살부터 진행된다. 코드에서도 객체가 생성되는 당시 매개변수를 받아 속성을 설정하고 생성할 수 있다.(객체가 생성된다는 것은 new함수를 통해 객체를 만들때를 말한다.) 이때 사용하는 것이 생성자이다. 생성자는 기본적으로 클래스와 같은 이름을 가진다.
>
> ```java
> public class Human {
>     String name; //이름
>     int age; //나이
>     int Residence; //거주지
>     int registrationNumber; //등록번호
>     Human(String name, int registrationNumber){
>         this.name = name;
>         this.registrationNumber = registrationNumber;
>         age = 0;
>     }
>     ...
> }
> ```

생성자 함수를 만들었으면 선언시 객체에 매개변수로 데이터를 넣으면 해당 객체는 데이터를 속성 값으로 가지게 된다.
>
> ```java
> Human 김범준 = new Human("김범준", 123456789);
> System.out.println(김범준.getName()); //김범준
> System.out.println(김범준.getAge()); //0
> System.out.println(김범준.getRegistrationNumber()); //123456789
> ```

근대 생각해보면 이름이 나중에 지어질 수도 있으므로 이름을 따로 안정하고 주민번호만 넣을 수도 있다. 이때는 생성자 오버라이딩을 하게 되는데 이는 생성자 함수를 다른 파라미터를 받는 형식으로 따로 생성한다.
>
> ```java
> public class Human {
>     String name; //이름
>     int age; //나이
>     int Residence; //거주지
>     int registrationNumber; //등록번호
>     Human(String name, int registrationNumber){
>         this.name = name;
>         this.registrationNumber = registrationNumber;
>         age = 0;
>     }
>     Human(int registrationNumber){
>         this.registrationNumber = registrationNumber;
>         age = 0;
>     }
>     ...
> }
> ```

이렇게 작성시 이름을 넣어도, 안넣어도 세팅이 가능하다.

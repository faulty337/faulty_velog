Java의 Collections.sort() 메서드를 사용하면 간단하게 리스트를 정렬할 수 있고 이러한 부분을 정리하고자 한다.

1. Java의 Collection이란?
----------------------

Java의 Collection은 데이터를 저장하고 관리하는 인터페이스다. Collection은 데이터를 모음으로 처리하며, 이를 통해 객체들을 모아 저장할 수 있는 자료구조를 제공한다.

### 1.1 Collection 인터페이스의 주요 기능

Collection 인터페이스는 데이터를 추가, 삭제, 검색하는 기능을 정의하며, 이를 구현한 다양한 하위 클래스들이 여러 자료구조(리스트, 집합, 큐 등)를 제공한다. Collection은 크게 세 가지 하위 인터페이스로 나뉜다.

* **List**: 순서를 유지하며 데이터에 접근할 수 있는 자료구조다 (예: ArrayList, LinkedList).
* **Set**: 중복을 허용하지 않는 자료구조다 (예: HashSet, TreeSet).
* **Queue**: 먼저 입력된 데이터를 먼저 처리하는 FIFO(First-In, First-Out) 방식의 자료구조다 (예: LinkedList, PriorityQueue).

이 외에도 여러 컬렉션 클래스들이 Collection 인터페이스를 구현하여 개발자가 다양한 기능을 사용할 수 있도록 지원한다.

실제로 List의 내부 구현을 보면

```java
public interface List<E> extends Collection<E> {
```

와 같이 Collection 인터페이스를 상속 받고 있다.

2. 컬렉션 정렬 방법
------------

Java에서는 두 가지 방식으로 컬렉션을 정렬할 수 있다

1. Comparable 인터페이스에 compareTo 구현
2. Comparator 인터페이스에 compare 구현

### 2.1 Comparable 인터페이스를 통한 정렬

Comparable 인터페이스는 객체 자체에 기본적인 정렬 기준을 설정하는 방식이다. 해당 클래스가 Comparable을 구현하고, compareTo 메서드를 오버라이드하면 기본 정렬 기준을 정의할 수 있다.

```java
public class Node implements Comparable<Node> {
    private int y;
    private int x;

    public Node(int y, int x) {
        this.y = y;
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public int getX(){
        return this.x;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.x, other.getX()); // x 값을 기준으로 오름차순 정렬
    }
}


List<MyObject> list = new ArrayList<>();
list.add(new MyObject(2, 1));
list.add(new MyObject(3, 2));
list.add(new MyObject(1, 2));

Collections.sort(list); // x를 기준으로 오름차순으로 정렬됨
```

compareTo 메서드에서는 두 객체를 비교하는 로직을 작성하면 된다. 예를 들어, 위 코드에서는 Integer.compare를 사용하여 값을 기준으로 오름차순으로 정렬하고 있다.

```java
    @Override
    public int compareTo(Node other) {
        int result = Integer.compare(this.x, other.getX());
        if(result == 0){
            return Integer.compare(this.y, other.getY());
        }
        return result;
    }
```

위와 같이 x가 같을 때 다른 조건을 걸 수도 있다. 여기서 중요한건 return이 고 양수, 0, 음수에 따라 달라진다.
> **그렇다면 그냥 `return this.x-other.getX()` 하면 더 간단하지 않을까?** -\> 정수 오버플로가 나면 다른 결과를 내뱉기 때문에 Integer.compare()가 안전하다. ex) `this.x = Integer.MIN_VALUE, other.getX() = Integer.MAX_VALUE` 인 상황이면 `this.x - other.getX()`는 오버플로로 원래 +여야 하지만 -값이 나오게 된다.

### 2.2 Comparator 인터페이스를 통한 정렬

Comparator 인터페이스는 클래스에 정렬 기준을 고정시키지 않고, 필요할 때마다 원하는 정렬 기준을 동적으로 적용할 수 있다. Comparator를 사용하면 다양한 방식으로 객체를 정렬할 수 있다.

```java
List<Node> list = new ArrayList<>();
list.add(new Node(2, 1));
list.add(new Node(3, 2));
list.add(new Node(1, 3));

// 오름차순 정렬
Collections.sort(list, new Comparator<Node>() {
    @Override
    public int compare(Node o1, Node o2) {
        return Integer.compare(o1.getX(), o2.getX());
    }
});

// 내림차순 정렬 (람다 이용)
Collections.sort(list, (o1, o2) -> Integer.compare(o2.getX(), o1.getX()));
```

Comparator를 사용하면 오름차순뿐만 아니라 내림차순 등 다양한 정렬 방식을 적용할 수 있다.

알고리즘을 C#,C++, Python, Java 등으로 풀어오면서 현재는 Java로 정착했다. Java가 문제를 풀기에는 그렇게 좋은 언어는 아니다. ~~Python이 있는데 다른 좋은 언어가 있을까~~

Java로 푸는데는 언어에 익숙해지기 위함이며 이에 자주 사용되는 것들을 작성하려고 한다.

입출력
---

Java에서 쓰이는 입출력으로는 가장 간단히 쓰는 Scanner가 있는데 이보다 더 빠른 게 있다.

### BufferedReader

말그대로 버퍼를 이용하는 방식인데 상대적으로 느린 하드 디스크나 입력에 대한 부분을 버퍼에 두어서 한번에 옮기는 방식으로 `Scanner`보다 `BufferedReader`이 크기가 커서 많은 문자에 대해 `BufferedReader`이 더 빠르다.

```java
BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
String str = br.readline();
```

대신 타입이 String로 고정되어 있기 때문에 사용처에 맞게 변환해줘야 한다.

### StringTokenizer

입력의 형태가 `줄내림`으로 구분되어 있을 때 한줄에서 데이터를 읽어야 하는 형태라면 자주 쓰이는 게 `split()`이 있다. 이는 String을 매개변수를 기준으로 잘라서 배열의 형태로 만들어준다. 만약 String을 배열형태로 사용해야 한다면 `split()`을 사용하는 것이 좋지만 문장을 특정 기준으로 혹은, 한 글자씩 잘라서 읽고 바로 다른 데이터로 가공한다면 `StringTokenizer`을 사용하는것이 좋다.

`StringTokenizer`는 String을 받고 해당 String을 특정 기준 혹은, 한글자씩 자를 수 있다.

```java
StringTokenizer st = new StirngTokenizer("123111232123");
while(st.hasMoreTokens()){
    System.out.print(st.nextToken());
}
```

생성당시 매개변수를 하나 더 주게 되면 해당 매개변수를 기준으로 구분하게 된다.

```java
StringTokenizer st = new StringTokenizer("1, 2, 3, 4, 5, 6, 7", ", ");
while(st.hasMoreTokens(){
    System.out.println(st.nextToken());
}
```

주의해야할점은 해당 매개변수를 기준으로 자르되 ',' 와 ' '을 따로 보고 문자열 전체에 ' '를 기준으로도 잘라버린다는 것이다. `,` 가 아닌 `,`와 를 기준으로 잘라버린다는 말이다.

### StringBuilder

출력 관련 문제를 풀 때, 출력할 문자 양이 100만줄이 상이 넘어가고 이를 `System.out.println()`을 이용하여 100만번 실행하게 되면 시간초과가 뜰 확률이 매우 높다. 이러한 입출력은 단순 연산이나 객체를 참조한는 것 보다 오래 걸리는 일이기 때문이다. 때문에 이를 한번에 출력하는 방법이 필요한데 이때 `StringBuilder`을 사용한다. 출력할 문자열 + \\n을 활용하여 해당 출력문을 `System.out.println()`을 단 한번으로 출력이 가능하다.

```java
StringBuilder sb = new StringBuilder();
for(int i = 0; i < 10000000; i++){
    sb.append(i + "\n");
}
System.out.println(sb.toString());
```

형 변환
----

알고리즘을 풀면 대부분이 String의 형태이고 이를 사용하는 곳은 단순 String이 아닌 정수형태나, 배열, 리스트의 형태로 요구될 때가 있다.

들어가기에 앞서 알고 있어야 하는 부분 함수가 해당 데이터 자체를 변환하고 반환을 안하는지, 데이터를 복사해서 변환하고 그 값을 반환하는지 알아야 한다.

### String ↔ int

String to int

```java
int var = Integer.parseInt("123");
```

int to String

```java
String str = Integer.toString(123);
String str2 = 123 + "";
```

### 진수 변환

`toString()`안에 매개변수를 정수 형태로 넣게 되면 해당 진수로 변환되어 String값을 반환한다.

```java
String str = Integer.toString(123, 16);
System.out.println(str); //7b
```

### 대소문자 변환

대문자로 변환

```java
String str = "abcdef";
String upperCase = str.toUpperCase();
```

소문자로 변환

```java
String str = "ABCDEF";
String lowerCase = str.toLowerCase();
```

위 변환에서 변환이 안되는 숫자형태가 들어가 있어도 이를 무시하고 변환이 가능한 문자를 변환한다.

### List\<Integer\> ↔ int\[\]

Programmers의 몇몇 문제 제출 유형이 int\[\] 등으로 배열의 형태로 고정될때가 있다. 사실 굳이 `stream()`안쓰고 `for문`을 돌리면 된다. 하지만 실무에서는 `for문`보다 collection에 친숙한 `stream`을 자주 쓴다고 하니 익숙해질겸 사용해봤다. ~~무엇보다 한줄로 끝나는게 멋있다.~~

List\<Integer\> to int\[\]

```java
List<Integer> list = new ArrayList<>();
list.add(1);
list.add(2);
list.add(3);
int[] arr = list.stream().mapToInt(i->i).toArray();
```

int\[\] to List\<Integer\>

```java
int[] arr = {1,2,3};
List<Integer> list = Arrays.stream(arr).boxed().collect(Collectors.toList());
```

정렬
---

알고리즘상 정렬은 여러곳에서 쓰인다. 정렬한 데이터를 비교, 정렬한 상황을 이용한 답 유추, 출력시 정렬 등 여러곳에서 사용된다.

### 배열(Array) 정렬

```java
int[] arr = {3, 1, 5, 2, 6, 4};
Arrays.sort(arr);
```

이전함수와는 다르게 이놈은 반환값이 없는 놈이다. 즉, 해당 배열 자체를 수정하는 함수라 주의해서 사용해야 한다.

### List(Array) 정렬

기본적으로 List에는 `sort()`가 존재한다. 단순 Integer형태라면 default값인 Comparator이 알아서 오름차순으로 정렬한다.

```java
List<Integer> list = Arrays.asList(3, 1, 5, 2, 6, 4);
list.sort(null);
```

만약 내림차순으로 정렬하려면

```java
List<Integer> list = Arrays.asList(3, 1, 5, 2, 6, 4);
list.sort(Comparator.reverseOrder());
```

로 하면 된다.

하지만 매번 Integer형태만 크기순으로 오름차순, 내림차순으로 정렬하는 상황만 오지는 않는다. 특정 조건에따라 정렬해야 할수도 있는데 이때 람다식을 사용한다. 이전에 푼 문제중
> 문자열로 구성된 리스트 strings와, 정수 n이 주어졌을 때, 각 문자열의 인덱스 n번째 글자를 기준으로 오름차순 정렬하려 합니다. 예를 들어 strings가 \["sun", "bed", "car"\]이고 n이 1이면 각 단어의 인덱스 1의 문자 "u", "e", "a"로 strings를 정렬합니다.

이런 문제가 있었다. 단순 정렬이 아닌 특정 조건에 따른 정렬이여야 했는데 이때 간단히 람다식으로 해결했다.

```java
int n = 1;
List<String> list = Arrays.asList("sun", "bed", "car");
list.sort((s1, s2)->{
        if(s1.charAt(n) - s2.charAt(n) == 0){
        return s1.compareTo(s2);
        }
        return s1.charAt(n) - s2.charAt(n);
        });
```

sort함수에서 필요한 리턴값은 -1, 0, 1이다. 따라서 각 조건에 맞게 해당 값을 리턴해주면 마음데로 조절이 가능하다.

steam도 형태는 비슷하나 이후 list로 반환할 것인지, array로 반환할 것인지 따로 명시해줘야 한다.

```java
int n = 1;
List<String> list = Arrays.asList("sun", "bed", "car");
list = list.stream()
        .sorted((s1, s2)->{
            if(s1.charAt(n) - s2.charAt(n) == 0){
                return s1.compareTo(s2);
            }
                return s1.charAt(n) - s2.charAt(n);
            })
        .collect(Collectors.toList());
```


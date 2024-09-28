최근 친구들과 Valheim게임을 멀티로 하고자 했다. 여기서 발헤임 서버를 상시 열어줘야 하는 누군가가 필요하고 이를 만족하는 Valheim좋아하는 백수는 내 주위에 없었다.

때문에 24시간 가동하기 위해 클라우드 서버를 열고자 했고 이에 대한 문서가 존재해 여는데는 큰 무리가 없었다.

<https://aws.amazon.com/ko/tutorials/valheim-on-aws/>

하지만 월 약 20달러 정도의 서버 비용이 나왔고 누군가 내 치킨 한 마리를 매달 뺏어먹는 다는 생각에 얼른 엔딩을 보고 서버를 껐다. ![](https://velog.velcdn.com/images/faulty337/post/3ec07da0-c506-4eb4-a47e-db170ae4d987/image.png)

이후 회사에서 감지기의 데이터를 PLC에 RS-485으로 보내야 하는 작업이 있었고 이에 미니 PC를 구매해 디지털 데이터를 RS-485보내는 작업을 진행했다.

이때 미니PC면 집에 나만의 서버로 가동 가능하다는 생각을 했고 개인 서버를 미니PC를 이용해 열고자 했다.

기기
---

정작 미니PC를 샀는데 역할을 못하면 돈을 날리는 것이니 최소 스펙을 정리했다.

1. Ubunt OS 지원
2. 최소 t3.nano급 CPU
3. RAM 16GB 이상
4. Lan 포트 1개 이상
5. SSD 256GB 이상

위 조건을 만족하는 미니PC를 15만원에 구매 했으니 뽕을 뽑으려면 최소 5달은 굴려야 한다. 12V/2.5A 로 한달 풀로 켜놓는다는 가정하에 약 2000원 정도 전기세가 나온다.

환경
---

당연히 Window로는 안돌리고 Ubuntu server로 OS를 맞췄다.

* GUI가 빠져 있어 시스템 자원을 적게 소모하기에 성능 활용면에서 압도적으로 좋다.
* 무엇보다 AWS 인스턴스를 자주 사용하다보니 익숙한 맛이 있다.

<https://ubuntu.com/download/server>

24.04 LTS 버전을 설치 했다.

초기 설정
-----

### ssh 설정

미니PC에 매번 HDMI, 키보드 마우스를 꽂아서 사용할 수는 없으니 원격 접속에 대해 허용해줘야 한다.

ssh원격 접속에는 여러 접속 방식이 있는데 ssh에 대한 접속은 나만 할 것이기 때문에 공개키 접속 방법을 사용했다.

공개키 접속 방식에 대해 간단히 설명하자면 키는 개인키와 공개키로 구성된다.

* 개인키로 암호화 한 것은 무조건 공개키로만 복호화가 가능하다.
* local에서 서버로 ssh 접속 요청시 서버에서는 특정 데이터를 보낸다.
* local은 개인키를 이용해 암호화 하고 서버로 보낸다.
* 서버는 암호화된 내용을 받고 공개키를 가지고 복호화 한다.
* 복호화한 내용과 초기 접속 시도시 보낸 데이터와 같은지 비교하고 접속을 허용한다.

때문에 접속시 SSH 개인키와 공개키가 필요하고 이에 대한 방법을 추가하고자 한다.

1. 패키지 업데이트

   ```bash
    sudo apt update
    sudo apt upgrade
   ```

2. Open SSH 설치

   ```bash
    sudo apt install openssh-server
   ```

3. 자동 부팅 설정

   미니pc가 꺼지더라도 다시 자동으로 켜질 수 있게 세팅한다.

   ```bash
    sudo systemctl enable ssh
   ```

4. SSH 키 생성

   ```bash
    ssh-keygen -t rsa -b 4096 -C "email@google.com"
   ```

   * email을 넣은 부분은 해당 키에 대한 주석을 넣는 부분이고 보통 이메일을 넣는다고 한다.

   * 키 저장 위치

     ```bash
       Enter file in which to save the key (/home/your_username/.ssh/id_rsa):
     ```

     * 키 생성시 키는 `id_rsa`, `id_rsa.pub` 과 같이 나오게 되는데 이에 대한 저장 위치로 그냥 `Enter`키를 치면 기본 저장 위치에 저장된다.
   * PassPhrase

     ```bash
       Enter passphrase (empty for no passphrase):
     ```

     * 단순 키 이외에 접근 비밀번호를 입력하라고 나오는데 마찬가지로 `Enter`를 치면 없지만 보안을 위해 입력하고 이후 접속 시 키와 해당 비밀번호로 접속하는 것이 보안상으로 좋다.

       ```bash
       Enter same passphrase again:
       ```

     * 키 재입력

5. 공개키 저장

   위 키 생성을 마치면 파일이 `id_rsa`, `id_rsa.pub` 이렇게 파일이 두 개가 나오게 된다.

   `id_rsa`는 개인키로 접속하고자 하는 컴퓨터로 보내야 한다.

   `id_rsa.pub`는 공개키로 따로 등록하고 저장해야 한다.

   ```bash
    chmod 700 ~/.ssh
    cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
    chmod 600 ~/.ssh/authorized_keys
   ```

   * 경로 잘 확인할 것
6. 설정 확인

   1. ssh 설치시 관련 config 파일 경로는 `/etc/ssh/` 가 된다.

   2. 지금 설정하고자 하는 것은 아래 두가지이다.

      1. **Authorized_keys 파일 경로 확인**

         ```bash
          sudo grep AuthorizedKeysFile /etc/ssh/sshd_config
         ```

         ```bash
          #결과
          AuthorizedKeysFile    .ssh/authorized_keys
         ```

         위와 같이 설정되어 있어야 정상적인 인증이 가능해진다.
      2. **비밀번호 접근 차단**

         ```bash
          sudo grep PasswordAuthentication /etc/ssh/sshd_config
         ```

         ```bash
          #결과
          PasswordAuthentication no
          # PasswordAuthentication.  Depending on your PAM configuration,
          # PAM authentication, then enable this but set PasswordAuthentication
         ```

         만약 `PasswordAuthentication yes` 라고 되어있다고 해도 키 접근이 안되는 것은 아니다. 다만, 비밀번호 접근 허용이기 때문에 브루트 포스 공격(Brute Force Attack) 등에 취약해질 수 있다.

         만약 바꾸고자 한다면 `PasswordAuthentication no` 로 바꿔주면 된다.

    설정을 바꿨다면 `sudo systemctl restart ssh` 를 통해 재시작 해야 설정이 반영된다.

### 포트 허용

위와 같이 설정이 되었다면 이제 실제로 ssh 포트를 열어줄 차례이다.

ssh의 기본 포트는 22이다. 이말은 기본 default값이 22이고 누구나 유추 가능하다는 말이다. 때문에 이러한 포트에 대해 변경해줄 필요가 있는데 이는 `/etc/ssh/sshd_config` 파일에서 `Port` 옵션으로 설정이 가능하다.

1. ufw 활성화

   Ubuntu에서 기본으로 설치되어 있을 수도 있지만 만약 설치가 안되어 있을 수도 있다.

   ```bash
    sudo ufw status
   ```

   위 명령어를 통해 ufw가 허용되었는지 확인하고 만약 `inactive` 라고 뜨면 활성화가 안된 것이므로

   ```bash
    sudo ufw enable
   ```

   로 활성화 한다.

<!-- -->

1. 포트 열기

   ```bash
    sudo ufw allow 22/tcp
   ```

   만약 기본 ssh 포트를 사용한다면 위 설정이며 다른 포트로 설정했다면, `22` 부분을 변경해서 날리면 된다.

위 설정으로 서버는 ssh server를 열었으며, 포트를 열어줬기 때문에 해당 공개키를 이용한 서버 접속이 가능해졌을 것이다.

이후에 xshell 을 이용한 접속방법, iptime에서 포트 포워딩, gabia domain 연결까지 해볼 예정이다.

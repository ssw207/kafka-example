# 카프카 설치(윈도우 환경)
1. WSL2 설치
  - https://dcrunch.dev/blog/kafka/set-up-and-run-apache-kafka-on-windows-wsl-2/
2. 카프카 다운로드 (2.5.0)
   - `wget https://archive.apache.org/dist/kafka/2.5.0/kafka_2.12-2.5.0.tgz`
   
# 카프카 특징

# 카프카의 장점
1. 고가용성
2. 유연성
 
# 주요 카프카 설정
```
broker.id=0 # 브로커의 아이디 0 부터 커진다
log.dirs=/home/mypc/app/kafka/data # 레코드가 저장되는 파일의 경로
num.partitions=3 # 디폴트 파티션 개수
log.retention.hours=168 # 168시간이 지난 데이터는 삭제
log.segment.bytes=1073741824 # segment 파일당 최대용량?
log.retention.check.interval.ms=300000 # 삭제 체크 간격
```

# 실행 명령어
- 주키퍼 실행 (로컬 테스트용으로 1대만 실행하는 명령어), 운영환경에서는 3대이상 앙상블로 묶어야한다.
  ```shell
  nohup ./bin/zookeeper-server-start.sh config/zookeeper.properties &
  ```
- 카프카 브로커 실행
  ```shell
  nohup ./bin/kafka-server-start.sh config/server.properties &
  ```
  
- localhost:9092 주소에 떠있는 카프카 브로커의 정보 확인 
  ```shell
  ./bin/kafka-broker-api-versions.sh --bootstrap-server localhost:9092
  ```
  ```shell
  localhost:9092 (id: 0 rack: null) -> (
        Produce(0): 0 to 8 [usable: 8],
        Fetch(1): 0 to 11 [usable: 11],
        ListOffsets(2): 0 to 5 [usable: 5],
        Metadata(3): 0 to 9 [usable: 9],
        LeaderAndIsr(4): 0 to 4 [usable: 4],
        
        ```
  ```
- 카프카 브로커의 topic 리스트 조회
  ```shell
  ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
  ```
  
# 카프카 shell script
## kafka-topics.sh
- 토픽 생성
    ```shell
    ./bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic hello.kafka
    ```
- 토픽 속성 확인
    ```shell
    ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic hello.kafka --describe
    ```
    ```shell
    # hello.kafka 토픽 정보, 파티션3개 복제 1개 리더 파티션이 있는 브로커 id는 0
    Topic: hello.kafka      PartitionCount: 3       ReplicationFactor: 1    Configs: segment.bytes=1073741824
            Topic: hello.kafka      Partition: 0    Leader: 0       Replicas: 0     Isr: 0
            Topic: hello.kafka      Partition: 1    Leader: 0       Replicas: 0     Isr: 0
            Topic: hello.kafka      Partition: 2    Leader: 0       Replicas: 0     Isr: 0
    ```
- 토픽 파티션 개수 늘리기 (줄이는건 불가능)
    ```shell
    ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic test --alter --partitions 10
    ```
  
## kafka-configs.sh
- 토픽 설정 변경
    ```shell
    ./bin/kafka-configs.sh --bootstrap-server localhost:9092 --alter --add-config min.insync.replicas=2 --topic test
    ```
- 카프카 브로커 설정정보 확인
    ```shell
    ./bin/kafka-configs.sh --bootstrap-server localhost:9092 --broker 0 --all --describe
    ```
    ```shell
    All configs for broker 0 are:
      log.cleaner.min.compaction.lag.ms=0 sensitive=false synonyms={DEFAULT_CONFIG:log.cleaner.min.compaction.lag.ms=0}
      offsets.topic.num.partitions=50 sensitive=false synonyms={DEFAULT_CONFIG:offsets.topic.num.partitions=50}
      log.flush.interval.messages=9223372036854775807 sensitive=false synonyms={DEFAULT_CONFIG:log.flush.interval.messages=9223372036854775807}
      controller.socket.timeout.ms=30000 sensitive=false synonyms={DEFAULT_CONFIG:controller.socket.timeout.ms=30000}
      
      ```
    ```
  
## kafka-console-producer.sh
테스트 용도로 데이터를 넣을수 있는 명령어. 기본적으로 value만 전송 한다.
- 데이터 콘솔로 입력
    ```shell
    # 데이터 입력시 key값은 null로 들어가며 파티션에는 key값이 없으므로 라운드로로빈 방식으로 입력한다
    ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic hello.kafka
    ```
    ```shell
    # 키값을 지정해서 입력한다 
    ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic hello.kafka --property "parse.key=true" --property "key.separator=:"
    ```
    ```shell
    >k2:kk
    >k3:hi
    ```
- 데이터 읽기
  ```shell
  # localhost:9092 브로커에 hello.kafka 토픽을 처음부터 다 읽는다
  ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic hello.kafka --from-beginning
  ```
  ```shell
  # 레코드의 key를 조회
  ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic hello.kafka --property print.key=true  --property key.separator="-" --from-beginning
  ```
  ```shell
  # 가장 먼저 저장된 데이터 1개만 조회
  ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic hello.kafka --property print.key=true  --property key.separator="-" --from-beginning --max-ages 1
  ```
  ```shell
  # 특정 파티션의 데이터만 조회
  ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic hello.kafka --property print.key=true  --property key.separator="-" --from-beginning --partition 1
  ```
  ```shell
  # 컨슈머 그룹명 hello-group 으로 읽는다. 읽은뒤 커밋을 수행한다. 커밋된 정보는 __consumer_offsets 토픽에 저장된다
  ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic hello.kafka --property print.key=true  --property key.separator="-" --group hello-group  --from
  -beginning
  ```
  ```shell
  # producer는 정보를 입력한다.
  ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test
  # producer가 입력한 정보를 읽는다.
  ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test
  ```
  
## kafka-consumer-groups.sh
- 컨슈머 그룹 정보 확인
```shell
# 
./bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group hello-group --describe
```
```shell
# LOG-END-OFFSET : 가장 오래된 오프셋
# CURRENT-OFFSET : 현재 컨슈머가 가져간 오프셋
# LAG : 컨슈머가 처리하지 못한 데이터 (지연)
GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
hello-group     hello.kafka     2          1               1               0               -               -               -
hello-group     hello.kafka     1          7               7               0               -               -               -
hello-group     hello.kafka     0          5               5               0               -       
```
```shell
# 컨슈머 그룹의 오프셋을 리셋한다 -> 데이터를 처음부터 다시 읽음
./bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group hello-group --topic hello.kafka --reset-offsets -to-earliest  --execute
```

## kafka-consumer-perf-test.sh
카프카 컨슈머로 퍼포먼스를 측정할때 사용한다. 브로커 - 컨슈머간 네트워크를 체크할때 사용가능
```shell
./bin/kafka-consumer-perf-test.sh --bootstrap-server localhost:9092 --topic hello.kafka --messages 10 --show-detailed-stats
```

## kafka-reassign-partitions.sh
토픽의 리더파티션이 특정 브로커에 몰릴경우 리더파티션을 고르게 분산 하기 위해 사용한다.

## kafka-delete-record.sh
지정한 파티션의 처음부터 오프셋 까지 레코드를 모두 삭제한다 

# 토픽을 생성하는 두가지 방법
## 토픽을 명시적으로 생성
- CLI툴등 으로 명령어를 날려 토픽을 생성한다
## 토픽을 자동으로 생성
- 컨슈머, 프로듀서가 생성되지 않은 토픽을 브로커에 요청할때 사전에 설정된 값에 따라 자동으로 생성된다 \
- 설정을 통해 자동생성을 못하게 할수도 있다 (추천)

# 카프카 프로듀서
## 프로듀서 내부 구조
1. ProducerRecord
   - 토픽, 파티션, 타임스탬프, 메시지키, 메시지 값등 전송할 레코드 정보
   - 오프셋은 프로듀서가 보내는게 아니라 브로커에 저장될때 생성됨
2. Partitioner 
   - 어느 파티션으로 전송할지 결정
3. Accumulator
   - send를 할때마다 항상 보내는게 아니라 accumulator에 담아놨다가 배치로 전송한다
4. sender
   - 브로커에 레코드를 전송한다

## 프로듀서의 기본 파티셔너
- 파티셔너 종류
  - UniformStickyPartitioner 
    - 디폴트 파티셔너 
  - RoundRobinPartitioner
- 메시지 키가 있을때 동작
  - 메시지 키의 해시값과 파티션을 매칭한다
  - 동일한 메시지키는 동일한 파티션 번호에 전달된다 
  - 파티션의 개수가 변경되면 파티션 번호 매칭은 깨질수 있다 (프로듀서의 초당 전송량보다 파티션을 널널하게 생성하는게 좋다)
- 메시지 키가 없을떄 동작
  - UniformStickyPartitioner
    - 여러 파티션에 균등하게 전송
    - 배치로 묶일때 까지 기다렸다 전송.
    - RoundRobinPartitioner 보다 성능이 좋다
  - RoundRobinPartitioner
    - 레코드가 들어오는대로 순회하면서 전송한다
    - accumulator에서 묶이는 정도가 적기 때문에 전송성능이 낮다
## 프로듀서의 커스텀 파티셔너
- Partitioner 인터페이스를 구현해 커스텀 파티셔너를 만들수 있다
- 키뿐만 아니라 특정 데이터를 확인해서 특정 파티션으로 보내는등 동작 지정가능

## 프로듀서 주요옵션(필수)
- bootstrap.server : 데이터를 전송할 브로커의 호스트 이름:포트를 1개이상 작성(2개이상 추천)
- key.serializer : 키를 직렬화 하는 클래스 지정
- value,serializer : 값을 직렬화 하는 클래스 지정
  - String 직렬화가 아니면 kafka-console-consumer로 조회가 안될수 있음 특별한 일이 없으면 String 직렬화를 사는게 좋다

## 프로듀서 주요옵션(선택)
- acks : 전송한 데이커가 브로커들에 정상적으로 저장되었는지 전송여부를 확인. 복제개수가 클수록 성능차이가 많아짐(1인경우 별차이 없다)
  - 0 : 전송만 하면 성공으로 판단
  - 1(기본) : 리더 파티션에 적재되면 성공. 일반적으로 사용한다 
  - -1 : 모든 브로커에 저장되면 성공
- in.insync.replicas : 2인경우 리더 + 파티선1개만 확인. 3개인경우 3번째 파티션은 확인하지 않는다. 2만 사용해도 충분하다
- linger.ms : 배치를 전송하기전까지 기다리는 시간 (기본값 0), 옵션을 지정하면 속도는 느려지지만 모았다 보내기 때문에 성능은 좋아진다
- retries: 브로커에서 에러를 받았을때 재전송을 시도하는 횟수. 재전송할 필요가 없다면 0으로 설정
- max.in.flight.requests.per.connection: 한번에 요청하는 최대 커넥션 갯수(쓰레드와 비슷) 기본값은5. 데이터 쳐리량이 많다면 높인다
- partitioner.class: 파티셔너 클래스 지정. 커스텀 파티셔너 클래스를 사용시 지정
- enable.idempotence: 중복전송을 막기위해 사용. 디폴트 false (3.0에서 디폴트 true)

## ISR
- 리더 파티션과 팔로우 파티션의 오프셋이 같을때 싱크가 같은것을 뜻한다

# 컨슈머
토픽의 데이터를 읽는다. 커밋을 통해 어디까지 컨슈머가 읽었는지 저장한다

## 컨슈머의 구조
- Fetcher : 리더 파티션으로 부터 레코드를 미리 가져와서 대기한다
- poll() : Fetcher에 있는 레코드를 리턴한다
- ConsumerRecords : 처리하려는 레코드들의 모음으로 오프셋이 포함되어 있다

## 컨슈머 그룹
컨슈머들의 그룹. 토픽의 파티션갯수만큼 컨슈머 갯수를 맞출때 가장 성능이 좋다. 파티션보다 컨슈머가 더 많은경우 남는 컨슈머는 유휴 상태가 된다

## 리벨런싱
컨슈머의 장애가 발생하는경우 파티션은 장애가 발생하지 않은 컨슈머로 재할당한다. 리벨런싱 발생시 파티션의 갯수가 많으면 10초 ~ 분단위로 지연이 발생할수 있다.

## 커밋
컨슈머가 브로커에 어디까지 데이터를 알려주는 행위. __consumer_offsets 토픽에 커밋정보를 저장한다

## 어사이너
컨슈머에 파티션을 할당하는 정책. 보통 파티션, 컨슈머는 1:1로 매칭되기 때문에 일반적으로는 별 차이없다
- RangeAssignor(디폴트) :  파티션을 숫자로 정렬, 컨슈머를 사전순으로 정렬후 매칭
- RoundRobinAssignor : 모든 파티션을 번갈아가며 할당
- StickyAssignor : 최대한 파티션을 균등하게 배분

## 컨슈머 주요옵션(선택)
- group.id : 컨슈머 그룹 아이디 지정. 기본값 null
- auto.offset.rest : 컨슈머 오프셋이 없는경우 어디부터 읽을지 결정. (맨처음부터 읽을지 마지막 데이터 부터 읽을지) 기본값은 최신
- enable.auto.commit : 자동커밋 여부 설정. 기본 true
- auto.commit.interval.ms : 자동커밋일때 얼마 주기로 커밋할지. 기본값 5초
- max.poll.records : poll() 메서드로 반환되는 레코드 갯수. 기본값500. 더많은 데이터를 한번에 처리하려면 숫자를 늘린다
- session.timeout.ms : 하트비트 전송이후 지정된 시간이상 응답이 없으면 타임아웃 처리. 기본값 10초
- heartbeat.interval.ms : 하트비트(헬스체크)를 전송하는 시간간격. 기본 3초
- max.poll.interval.ms : poll() 메서드를 호출하는 간격. 호출후 지정된 시간이상 응답이 없으면 문제가 생긴것으로 판단해 리벨런싱을 한다. 기본값 5분. 데이터 하나당 처리시간이 길다면 숫자를 올린다

## 컨슈머 랙
- 파티션의 최신 오프셋과 컨슈머의 오프셋 차이. 이 차이가 클수록 처리에 지연이 발생

## 컨슈머 랙 모니터링툴 
- burrow 추천
- 모니터링 아키텍쳐
  - 버로우, 텔레그래프, 엘라스틱서치, 그라파나

# 멱등성 프로듀서
- 이슈가 생겼을때 중복 적재를 막는 프로듀서. enable.idempotence를 true로 변경해 운영. 이값을 true 바꾸면 acks=all 로 설정되기 때문에 성능에 문제가 있을수 있다
- 데이터를 브로커로 전달할때 프로듀서 PID, 시퀀스 번호를 전달해 중복된값은 브로커에서 저장하지 않게한다
- 프로듀서가 재시작되면 PID가 달라지기때문에 장애가 발생하지 않은경우만 보장한다
- 시퀀스가 순차적으로 적재되지 않으면 에러가 발생할수있다 (드뭄)

# 트렌젝션 프로듀서
- 다수의 파티션에 저장할때 원자성을 보장
- 고유한 트렌젝션 id 설정필요

# 트렌젝션 컨슈머
- 커밋된 데이터만 가져간다
- isolation레벨 read committed 로 설정 

# 카프카 커넥트
- 구조 : 커넥트 > 커넥터 > 테스크
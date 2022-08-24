# kafa 도커실행
1. cd kafka-docker 
2. docker-compose up -d

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
  ```
  nohup ./bin/zookeeper-server-start.sh config/zookeeper.properties &
  ```
- 카프카 브로커 실행
  ```
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
  
# 카프가 shell script
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
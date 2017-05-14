# README #

This project provides a Datadog Statsd reporter which could be added to Quantifind's Kafka Offset Monitor.
 

# Command line startup

```bash
java -cp "/path/to/kafka_offset_monitor.jar:/path/to/reporter.jar" \
        "com.quantifind.kafka.offsetapp.OffsetGetterWeb" \
        --dbName "offsetdb_zookeeper" \
        --zk "$ZOOKEEPER" \
        --port "8082" \
        --offsetStorage zookeeper \
        --refresh "60.seconds" \
        --retain "12.hours" \
        --pluginsArgs "statsdHost=localhost,port=8125"
```
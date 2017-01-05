package com.simplaex.pepoffsets

final case class KafkaMetric(metricName: String, group: String, topic: String, partition: Int) {

  def metricWithTags: String = metricName +
    "[" +
    KafkaMetric.groupTag + ":" + group + "," +
    KafkaMetric.topicTag + ":" + topic + "," +
    KafkaMetric.partitionTag + ":" + partition +
    "]"

}

object KafkaMetric {

  private val groupTag: String = "group"
  private val topicTag: String = "topic"
  private val partitionTag: String = "partition"

}

package com.github.ywilkof.kafkadatadogreporter

import java.util.concurrent.TimeUnit

import com.codahale.metrics._
import com.quantifind.kafka.OffsetGetter.OffsetInfo
import org.coursera.metrics.datadog.DatadogReporter
import org.coursera.metrics.datadog.transport.UdpTransport
import org.slf4j.LoggerFactory


final class DatadogOffsetReporter(pluginsArgs: String) extends com.quantifind.kafka.offsetapp.OffsetInfoReporter {

  private[this] val logger = LoggerFactory.getLogger(classOf[DatadogOffsetReporter])
  private[this] val lagLabel = "lag"
  private[this] val lastSeenLabel = "lastSeen"

  private[this] val arguments = new DatadogOffsetReporterArgumentParser(pluginsArgs)
  logger.info("arguments=" + arguments.toString)

  private[this] val registry: MetricRegistry = new MetricRegistry()

  private[this] val transport: UdpTransport = new UdpTransport
  .Builder()
    .withPort(arguments.port)
    .withStatsdHost(arguments.statsDHost)
    .withPrefix(arguments.prefix)
    .build

  private[this] val reporter: DatadogReporter = DatadogReporter
    .forRegistry(registry)
    .withTransport(transport)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build

  reporter.start(arguments.reportPeriod, TimeUnit.SECONDS)

  val gaugeValues = scala.collection.mutable.HashMap.empty[String,Long]

  private def getGauge(metric: String) = new Gauge[Long] {
    override def getValue: Long = gaugeValues(metric)
  }

  // not run in parallel, but sequentially by scheduled interval
  override def report(info: IndexedSeq[OffsetInfo]): Unit = {
    info.foreach(offsetInfo => {

      val lagMetric = KafkaMetric(
        metricName = lagLabel,
        group = offsetInfo.group,
        topic = offsetInfo.topic,
        partition = offsetInfo.partition
      ).metricWithTags

      gaugeValues.put(lagMetric, offsetInfo.lag)

      if (!gaugeValues.contains(lagMetric)) {
        registry.register(lagMetric, getGauge(lagMetric))
      }

      val lastConsumedMetric = KafkaMetric(
        metricName = lastSeenLabel,
        group = offsetInfo.group,
        topic = offsetInfo.topic,
        partition = offsetInfo.partition
      ).metricWithTags

      gaugeValues.put(lastConsumedMetric, offsetInfo.modified.inSeconds)
      if (!gaugeValues.contains(lastConsumedMetric)) {
        registry.register(lastConsumedMetric, getGauge(lastConsumedMetric))
      }
    })
  }


}

package com.simplaex.pepoffsets

import java.util.concurrent.TimeUnit

import com.codahale.metrics._
import com.quantifind.kafka.OffsetGetter.OffsetInfo
import org.coursera.metrics.datadog.DatadogReporter
import org.coursera.metrics.datadog.transport.UdpTransport
import org.slf4j.LoggerFactory


final class DatadogOffsetReporter(pluginsArgs: String) extends com.quantifind.kafka.offsetapp.OffsetInfoReporter {

  private[this] val logger = LoggerFactory.getLogger(classOf[DatadogOffsetReporter])
  private[this] val lagLabel = "lag"

  private[this] val arguments = new DatadogOffsetReporterArgumentParser(pluginsArgs)
  logger.info("arguments=" + arguments.toString)

  private[this] val registry: MetricRegistry = new MetricRegistry()

  private[this] val transport: UdpTransport = new UdpTransport
  .Builder()
    .withPort(arguments.port)
    .withStatsdHost(arguments.statsDHost)
    .withPrefix(arguments.prefix)
    .build

  sealed private class DatadogReporterListener extends {

  }

  private[this] val reporter: DatadogReporter = DatadogReporter
    .forRegistry(registry)
    .withTransport(transport)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build

  reporter.start(arguments.reportPeriod, TimeUnit.SECONDS)

  override def report(info: IndexedSeq[OffsetInfo]): Unit = {
    info.foreach(offsetInfo => {
      val metricName = KafkaMetric(lagLabel,offsetInfo.group,offsetInfo.topic,offsetInfo.partition).metricWithTags
      registry.remove(metricName)
      registry.register(metricName, new Gauge[Long] {
        override def getValue: Long = offsetInfo.lag
      })
    })
  }

}

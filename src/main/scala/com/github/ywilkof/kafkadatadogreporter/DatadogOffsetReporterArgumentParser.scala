package com.github.ywilkof.kafkadatadogreporter

class DatadogOffsetReporterArgumentParser(args: String) {

  private[this] val argsMap: Map[String, String] = args
    .split(",")
    .map(_.split("=", 2))
    .filter(_.length > 1)
    .map(arg => {arg(0).toLowerCase -> arg(1)})
    .toMap

  def statsDHost: String = argsMap.getOrElse("statsdhost", "localhost")

  def port: Int = argsMap.get("port").map(_.toInt).getOrElse(8125)

  def prefix: String = argsMap.getOrElse("prefix", "stats.kafka.monitor")

  def reportPeriod: Int = argsMap.get("reportperiod").map(_.toInt).getOrElse(30)

  override def toString = s"DatadogOffsetReporterArgumentParser($statsDHost, $port, $prefix, $reportPeriod)"
}

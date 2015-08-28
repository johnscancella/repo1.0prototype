import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

import static ch.qos.logback.classic.Level.*

appender("STDOUT", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%date %level [%thread] %logger{10} [%file:%line] %msg%n"
  }
}

root(INFO, ["STDOUT"])

//these two are set to ERROR since they output unneeded logging at the info level
logger("gov.loc.rdc.app.Main", ERROR)
logger("org.springframework", ERROR)
logger("org.hibernate", WARN) //otherwise prints out validator version
logger("com.github.fakemongo", WARN)
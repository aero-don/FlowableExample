package example.micronaut.flowable

import example.micronaut.flowable.emitters.SensorMeasurementEmitter
import example.micronaut.flowable.jobs.SensorMeasurementJob
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic
import io.micronaut.scheduling.TaskScheduler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.util.concurrent.ScheduledFuture

@CompileStatic
class Application {

    static void main(String[] args) {
        Micronaut.run(Application, args)
    }
}

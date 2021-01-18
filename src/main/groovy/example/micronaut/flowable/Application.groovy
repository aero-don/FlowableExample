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
    static final Logger logger = LoggerFactory.getLogger(Application.class)

    static void main(String[] args) {
        try {
            ApplicationContext applicationContext = Micronaut.run(Application, args)

            Optional<Integer> sensorMeasurementRate = applicationContext.getProperty('sensor.measurement.rate', Integer.class)

            if (sensorMeasurementRate.isPresent()) {

                TaskScheduler taskScheduler = applicationContext.getBean(TaskScheduler.class)
                SensorMeasurementEmitter sensorMeasurementEmitter = applicationContext.getBean(SensorMeasurementEmitter.class)

                if (sensorMeasurementEmitter) {

                    logger.info("Starting sensorMeasurementJob sensorMeasurementRate is ${sensorMeasurementRate.get()} ms")
                    SensorMeasurementJob sensorMeasurementJob = new SensorMeasurementJob(sensorMeasurementEmitter)

                    if (sensorMeasurementJob) {

                        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(
                                Duration.ofSeconds(0),
                                Duration.ofMillis(sensorMeasurementRate.get()),
                                sensorMeasurementJob)

                        if (!future) {
                            logger.error('future is null')
                        }

                    } else {
                        logger.error('sensorMeasurementJob is null')
                    }

                } else {
                    logger.error('sensorMeasurementEmitter is null')
                }

            } else {
                logger.error('sensor.measurement.rate is not present')
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

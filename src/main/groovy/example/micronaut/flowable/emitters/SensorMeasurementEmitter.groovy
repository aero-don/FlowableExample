package example.micronaut.flowable.emitters

import example.micronaut.flowable.jobs.SensorMeasurementJob
import example.micronaut.flowable.messages.SensorMeasurement
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.scheduling.TaskScheduler
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.annotations.NonNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.PostConstruct
import javax.inject.Singleton
import java.time.Duration
import java.util.concurrent.ScheduledFuture

@CompileStatic
@Singleton
@Context
@Requires(property = 'sensor.measurement.rate')
class SensorMeasurementEmitter {
    static final Logger logger = LoggerFactory.getLogger(SensorMeasurementEmitter.class)

    @Property(name = 'sensor.measurement.rate')
    Integer sensorMeasurementRate;

    @Property(name = 'sensor.measurement.log.rate', value = '1')
    Integer sensorMeasurementLogRate;

    Set<FlowableEmitter<SensorMeasurement>> sensorMeasurementEmitters = []

    TaskScheduler taskScheduler

    SensorMeasurementEmitter(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler
    }

    @PostConstruct
    void startSensorMeasurementJob() {
        logger.info("Starting sensorMeasurementJob sensorMeasurementRate is ${sensorMeasurementRate} ms")
        ScheduledFuture<?> sensorMeasurementJobFuture = taskScheduler.scheduleAtFixedRate(
                Duration.ofSeconds(0),
                Duration.ofMillis(sensorMeasurementRate),
                new SensorMeasurementJob(this))

        if (!sensorMeasurementJobFuture) {
            logger.error('sensorMeasurementJobFuture is null')
        }
    }

    FlowableOnSubscribe<SensorMeasurement> sensorMeasurementSource = new FlowableOnSubscribe<SensorMeasurement>() {
        @Override
        void subscribe(@NonNull FlowableEmitter<SensorMeasurement> emitter) throws Exception {
            logger.info("sensorMeasurementFlowableOnSubscribe subscribe called sensorMeasurementRate = ${sensorMeasurementRate} ms")
            sensorMeasurementEmitters << emitter
        }
    }

    void publishSensorMeasurementEvent(SensorMeasurement sensorMeasurement) {
        Set<FlowableEmitter<SensorMeasurement>> cancelledSensorMeasurementEmitters = []

        if (sensorMeasurement.value as Integer % (((sensorMeasurementLogRate * 1000) / sensorMeasurementRate) as Integer) == 0) {
            logger.info("${sensorMeasurementEmitters.size()} SensorMeasurementEmitters, Published sensorMeasurement event: ${sensorMeasurement} ")
        }

        sensorMeasurementEmitters.each { sensorMeasurementEmitter ->
            if (sensorMeasurementEmitter.isCancelled()) {
                cancelledSensorMeasurementEmitters << sensorMeasurementEmitter
            } else {
                sensorMeasurementEmitter.onNext(sensorMeasurement)
            }
        }
        if (cancelledSensorMeasurementEmitters.size()) {
            sensorMeasurementEmitters -= cancelledSensorMeasurementEmitters
        }
    }

}

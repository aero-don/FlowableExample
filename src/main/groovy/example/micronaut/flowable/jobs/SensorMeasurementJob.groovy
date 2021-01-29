package example.micronaut.flowable.jobs

import example.micronaut.flowable.emitters.SensorMeasurementEmitter
import example.micronaut.flowable.messages.SensorMeasurement
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Instant


@CompileStatic
//@Singleton
class SensorMeasurementJob implements Runnable {
    static final Logger logger = LoggerFactory.getLogger(SensorMeasurementJob.class)

    // For now constant values for sensor id and type
    static final String SENSOR_ID = UUID.randomUUID().toString()
    static final String SENSOR_TYPE = 'counter'

    Integer numberEmitted = 0

    SensorMeasurementEmitter sensorMeasurementEmitter

    SensorMeasurementJob(SensorMeasurementEmitter sensorMeasurementEmitter) {
        this.sensorMeasurementEmitter = sensorMeasurementEmitter
    }


    // @Scheduled(fixedRate = "10ms")
    @Override
    void run() {
        sensorMeasurementEmitter.publishSensorMeasurement(
                new SensorMeasurement(SENSOR_ID, SENSOR_TYPE, ++numberEmitted as Double, Instant.now().toEpochMilli()))
    }
}
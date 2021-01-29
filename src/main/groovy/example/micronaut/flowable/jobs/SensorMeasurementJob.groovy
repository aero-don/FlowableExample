package example.micronaut.flowable.jobs

import example.micronaut.flowable.services.SensorMeasurementService
import example.micronaut.flowable.messages.SensorMeasurement
import groovy.transform.CompileStatic
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Singleton
import java.time.Instant


@CompileStatic
@Singleton
class SensorMeasurementJob {
    static final Logger logger = LoggerFactory.getLogger(SensorMeasurementJob.class)

    // For now constant values for sensor id and type
    static final String SENSOR_ID = UUID.randomUUID().toString()
    static final String SENSOR_TYPE = 'counter'

    Integer numberEmitted = 0

    @Inject
    SensorMeasurementService sensorMeasurementEmitter

    SensorMeasurementJob(SensorMeasurementService sensorMeasurementEmitter) {
        this.sensorMeasurementEmitter = sensorMeasurementEmitter
    }

    @Scheduled(fixedRate = '${sensor.measurement.rate}ms')
    void takeSensorMeasurement() {
        sensorMeasurementEmitter.publishSensorMeasurement(
                new SensorMeasurement(SENSOR_ID, SENSOR_TYPE, ++numberEmitted as Double, Instant.now().toEpochMilli()))
    }
}
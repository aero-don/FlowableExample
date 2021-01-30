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

    Integer counterValue = 0

    @Inject
    SensorMeasurementService sensorMeasurementService

    SensorMeasurementJob(SensorMeasurementService sensorMeasurementService) {
        this.sensorMeasurementService = sensorMeasurementService
    }

    @Scheduled(fixedRate = '${sensor.measurement.rate}ms')
    void takeSensorMeasurement() {
        sensorMeasurementService.publishSensorMeasurement(
                new SensorMeasurement(SENSOR_ID, SENSOR_TYPE, ++counterValue as Double, Instant.now().toEpochMilli()))
    }
}
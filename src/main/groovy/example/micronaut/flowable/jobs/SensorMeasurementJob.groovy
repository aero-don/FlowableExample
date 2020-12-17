package example.micronaut.flowable.jobs

import example.micronaut.flowable.emitters.SensorMeasurementEmitter
import example.micronaut.flowable.messages.SensorMeasurement
import groovy.transform.CompileStatic

import javax.inject.Singleton
import io.micronaut.scheduling.annotation.Scheduled

@CompileStatic
@Singleton
class SensorMeasurementJob {

    // For now constant values for sensor id and type
    static final String SENSOR_ID = UUID.randomUUID().toString()
    static final String SENSOR_TYPE = 'counter'
    Integer numberEmitted = 0

    SensorMeasurementEmitter sensorMeasurementEmitter

    SensorMeasurementJob(SensorMeasurementEmitter sensorMeasurementEmitter) {
        this.sensorMeasurementEmitter = sensorMeasurementEmitter
    }

    @Scheduled(fixedRate = "2s")
    void takeSensorMeasurement() {
        sensorMeasurementEmitter.publishSensorMeasurementEvent(
                new SensorMeasurement(SENSOR_ID, SENSOR_TYPE, ++numberEmitted as Double))
    }
}
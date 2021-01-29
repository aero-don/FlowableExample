package example.micronaut.flowable.services

import example.micronaut.flowable.messages.SensorMeasurement
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.reactivex.processors.PublishProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Singleton

@CompileStatic
@Singleton
@Context
@Requires(property = 'sensor.measurement.rate')
class SensorMeasurementService {
    static final Logger logger = LoggerFactory.getLogger(SensorMeasurementService.class)

    @Property(name = 'sensor.measurement.rate')
    Integer sensorMeasurementRate;

    @Property(name = 'sensor.measurement.log.rate', value = '1')
    Integer sensorMeasurementLogRate;

    PublishProcessor<SensorMeasurement> sensorMeasurementProcessor = PublishProcessor.create()

    void publishSensorMeasurement(SensorMeasurement sensorMeasurement) {
        sensorMeasurementProcessor.onNext(sensorMeasurement)

        if (logger.isInfoEnabled() && sensorMeasurement.value as Integer % (((sensorMeasurementLogRate * 1000) / sensorMeasurementRate) as Integer) == 0) {
            logger.info("Published sensorMeasurement: ${sensorMeasurement} ")
        }
    }
}

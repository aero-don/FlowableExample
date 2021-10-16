package example.micronaut.flowable.services

import example.micronaut.flowable.messages.SensorMeasurement
import groovy.transform.CompileStatic

import jakarta.inject.Singleton

import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.SignalType
import reactor.core.publisher.Sinks
import reactor.util.concurrent.Queues

@CompileStatic
@Singleton
@Context
@Requires(property = 'sensor.measurement.rate')
class SensorMeasurementService {
    static final Logger logger = LoggerFactory.getLogger(SensorMeasurementService.class)

    @Property(name = 'sensor.measurement.rate')
    Integer sensorMeasurementRate

    @Property(name = 'sensor.measurement.log.rate', value = '1')
    Integer sensorMeasurementLogRate

    Sinks.Many<SensorMeasurement> sensorMeasurementSink = Sinks.many().multicast().onBackpressureBuffer(Queues.XS_BUFFER_SIZE, false)
    Flux<SensorMeasurement> sensorMeasurementFlux = sensorMeasurementSink.asFlux()

    void publishSensorMeasurement(SensorMeasurement sensorMeasurement) {

        sensorMeasurementSink.emitNext(sensorMeasurement,
                (SignalType signalType, Sinks.EmitResult emitResult) -> {
                    if (emitResult != Sinks.EmitResult.FAIL_ZERO_SUBSCRIBER) {
                        logger.error("Failed: sensorMeasurementSink.emitNext(sensorMeasurement): ${sensorMeasurement}, signalType=${signalType}, emitResult=${emitResult}")
                    }
                })

        if (logger.infoEnabled && (sensorMeasurement.value as Integer % (((sensorMeasurementLogRate * 1000) / sensorMeasurementRate) as Integer) == 0)) {
            logger.info("Published sensorMeasurement: ${sensorMeasurement}")
        }
    }
}

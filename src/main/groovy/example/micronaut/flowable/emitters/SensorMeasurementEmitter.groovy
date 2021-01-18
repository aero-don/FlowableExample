package example.micronaut.flowable.emitters

import example.micronaut.flowable.messages.SensorMeasurement
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.annotations.NonNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Singleton

@CompileStatic
@Singleton
@Requires(property = 'sensor.measurement.rate')
class SensorMeasurementEmitter {
    static final Logger logger = LoggerFactory.getLogger(SensorMeasurementEmitter.class)

    @Property(name='sensor.measurement.rate')
    Integer sensorMeasurementRate;

    Set<FlowableEmitter<SensorMeasurement>> sensorMeasurementEmitters = []

    FlowableOnSubscribe<SensorMeasurement> sensorMeasurementSource = new FlowableOnSubscribe<SensorMeasurement>() {
        @Override
        void subscribe(@NonNull FlowableEmitter<SensorMeasurement> emitter) throws Exception {
            logger.info("sensorMeasurementFlowableOnSubscribe subscribe called sensorMeasurementRate = ${sensorMeasurementRate} ms")
            sensorMeasurementEmitters << emitter
        }
    }

    void publishSensorMeasurementEvent(SensorMeasurement sensorMeasurement) {
        Set<FlowableEmitter<SensorMeasurement>> cancelledSensorMeasurementEmitters = []

        if (sensorMeasurement.value as Integer % (1000/sensorMeasurementRate as Integer) == 0) {
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

package example.micronaut.flowable.emitters

import example.micronaut.flowable.messages.SensorMeasurement
import groovy.transform.CompileStatic
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.annotations.NonNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Singleton

@CompileStatic
@Singleton
class SensorMeasurementEmitter {
    static final Logger logger = LoggerFactory.getLogger(SensorMeasurementEmitter.class);

    Set<FlowableEmitter<SensorMeasurement>> sensorMeasurmentEmitters = []

    FlowableOnSubscribe<SensorMeasurement> sensorMeasurementSource = new FlowableOnSubscribe<SensorMeasurement>() {
        @Override
        void subscribe(@NonNull FlowableEmitter<SensorMeasurement> emitter) throws Exception {
            logger.info("sensorMeasurementFlowableOnSubscribe subscribe called")
            sensorMeasurmentEmitters << emitter
        }
    }

    void publishSensorMeasurementEvent(SensorMeasurement SensorMeasurement) {
        Set<FlowableEmitter<SensorMeasurement>> cancelledSensorMeasurementEmitters = []
        logger.info("${sensorMeasurmentEmitters.size()} SensorMeasurementEmitters, Published SensorMeasurement event: ${SensorMeasurement} ")
        sensorMeasurmentEmitters.each { sensorMeasurementEmitter ->
            if (sensorMeasurementEmitter.isCancelled()) {
                cancelledSensorMeasurementEmitters << sensorMeasurementEmitter
            } else {
                sensorMeasurementEmitter.onNext(SensorMeasurement)
            }
        }
        if (cancelledSensorMeasurementEmitters.size()) {
            sensorMeasurmentEmitters -= cancelledSensorMeasurementEmitters
        }
    }


}

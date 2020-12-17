package example.micronaut.flowable.controllers

import example.micronaut.flowable.emitters.SensorMeasurementEmitter
import example.micronaut.flowable.messages.SensorMeasurement
import groovy.transform.CompileStatic
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

@CompileStatic
@Controller("/sensors")
class SensorController {

    SensorMeasurementEmitter sensorMeasurementEmitter

    SensorController(SensorMeasurementEmitter sensorMeasurementEmitter) {
        this.sensorMeasurementEmitter = sensorMeasurementEmitter
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get(uri = '/measurements', produces = MediaType.APPLICATION_JSON_STREAM)
    Flowable<SensorMeasurement> index() {
        Flowable.create(sensorMeasurementEmitter.sensorMeasurementSource, BackpressureStrategy.BUFFER)
    }

}
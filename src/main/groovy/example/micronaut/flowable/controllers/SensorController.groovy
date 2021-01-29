package example.micronaut.flowable.controllers

import example.micronaut.flowable.services.SensorMeasurementService
import example.micronaut.flowable.messages.SensorMeasurement
import groovy.transform.CompileStatic
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.reactivex.Flowable

@CompileStatic
@Controller("/sensors")
class SensorController {

    SensorMeasurementService sensorMeasurementService

    SensorController(SensorMeasurementService sensorMeasurementService) {
        this.sensorMeasurementService = sensorMeasurementService
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get(uri = '/measurements', produces = MediaType.APPLICATION_JSON_STREAM)
    Flowable<SensorMeasurement> index() {
        sensorMeasurementService.sensorMeasurementProcessor.onBackpressureBuffer()
    }

}
package com.example.sumpdata.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.io.Serializable;
import java.time.LocalDateTime;

/***
 * Representation of a row of sumppump data.
 * deviceID : ID of the sump pump monitor device.
 * measuredOn: The date time of the measurement
 * value: An integer value of the sump pump data. Intentionally defines it as an Integer that represents
 *        the depth to the water line in the sump, measure in mm. Double or Float would be overkill as the
 *        precision of the depth is not that great (it doesn't have mm precision, probably a few mm at most)
 */
@Entity
@Schema(description = "Date model for water level data")
@IdClass(DataEntryId.class)
public class DataEntry implements Serializable {
    @Schema(description = "Device ID", example = "1")
    @Id
    private Integer deviceID;
    @Schema(description = "Local date time for the value (water level)", type = "string", example = "2023-11-14T02:31:27")
    @Id
    private LocalDateTime measuredOn;

    @Schema(description = "Water level measurement in mm.", example = "127")
    private Integer value;

    public Integer getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(Integer deviceID) {
        this.deviceID = deviceID;
    }

    public LocalDateTime getMeasuredOn() {
        return measuredOn;
    }

    public void setMeasuredOn(LocalDateTime measuredOn) {
        this.measuredOn = measuredOn;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

}

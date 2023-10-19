package com.example.sumpdata.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/***
 * This class is the representation of the primary keys for DataEntry records, where the table name
 * is data_entry, which is automatically created by the JPA framework.
 */
public class DataEntryId implements Serializable {
    private Integer deviceID;
    private LocalDateTime measuredOn;

    // Default constructor is required for DataEntry entity to find this id class.
    public DataEntryId() {
    }

    public DataEntryId(Integer deviceID, LocalDateTime measuredOn) {
        this.deviceID = deviceID;
        this.measuredOn = measuredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DataEntryId that = (DataEntryId) o;

        if (!Objects.equals(deviceID, that.deviceID))
            return false;
        return Objects.equals(measuredOn, that.measuredOn);
    }

    @Override
    public int hashCode() {
        int result = deviceID != null ? deviceID.hashCode() : 0;
        result = 31 * result + (measuredOn != null ? measuredOn.hashCode() : 0);
        return result;
    }
}

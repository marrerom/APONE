package tudelft.dds.irep.data.schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EventAggregation {
		COUNT,
		MIN,
		MAX,
		AVG;
		
		 @JsonValue
		 final String value() {
		    return this.value();
		 }
}

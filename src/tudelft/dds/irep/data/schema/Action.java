package tudelft.dds.irep.data.schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Action {
	ADD,
	REMOVE;
	
	 @JsonValue
	 final String value() {
	    return this.value();
	 }
}

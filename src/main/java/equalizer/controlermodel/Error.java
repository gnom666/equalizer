package equalizer.controlermodel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import equalizer.controlermodel.Constants.ErrorCode;
import equalizer.controlermodel.Constants.ErrorType;

public class Error {
	public ErrorCode code;
	public ErrorType type;
	public String description;
	public String timestamp;
	
	public Error(ErrorCode code, ErrorType type, String description) {
		this.code = code;
		this.type = type;
		this.description = description;
		this.timestamp = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(Calendar.getInstance().getTime());
	}
	
	public Error updateError(ErrorCode code, ErrorType type, String description) {
		this.code = code;
		this.type = type;
		this.description = description;
		this.timestamp = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(Calendar.getInstance().getTime());
		System.err.println(this.toString());
		return this;
	}
	
	@Override
	public String toString() {
		return "Error [code=" + Constants.errorCodeName(code) 
				+ ", type=" + Constants.errorTypeName(type) 
				+ ", description=" + description
				+ ", timestamp=" + timestamp
				+ "]";
	}
}

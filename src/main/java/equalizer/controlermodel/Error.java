package equalizer.controlermodel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import equalizer.controlermodel.Constants.ErrorCode;
import equalizer.controlermodel.Constants.ErrorType;

public class Error {
	public ErrorCode code;
	public ErrorType type;
	public String description;
	
	public Integer status;
    public String error;
    public String message;
    public String timeStamp;
    public String trace;
	
	public Error(ErrorCode code, ErrorType type, String description) {
		this.code = code;
		this.type = type;
		this.description = description;
		this.timeStamp = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(Calendar.getInstance().getTime());
	}
	
	public Error(int status, Map<String, Object> errorAttributes) {
        this.status = status;
        this.error = (String) errorAttributes.get("error");
        this.message = (String) errorAttributes.get("message");
        this.timeStamp = errorAttributes.get("timestamp").toString();
        this.trace = (String) errorAttributes.get("trace");
    }
	
	public Error(Error er) {
		this.code = er.code;
		this.type = er.type;
		this.description = er.description;
		this.timeStamp = er.timeStamp;
		this.status = er.status;
		this.error = er.error;
		this.message = er.message;
		this.trace = er.trace;
	}
	
	public Error updateError(ErrorCode code, ErrorType type, String description) {
		this.code = code;
		this.type = type;
		this.description = description;
		this.timeStamp = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(Calendar.getInstance().getTime());
		System.err.println(this.toString());
		return this;
	}
	
	@Override
	public String toString() {
		return "Error [code=" + code + ", type=" + type + ", description=" + description + ", status=" + status
				+ ", error=" + error + ", message=" + message + ", timeStamp=" + timeStamp + ", trace=" + trace + "]";
	}
}

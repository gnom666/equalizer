package equalizer.controlermodel;

import java.security.SecureRandom;

public final class Constants {
	
	public static String IP_HOME = "192.168.1.109";
    public static String IP_WORK = "10.23.51.45";
    public static String IP_EC2 = "52.47.157.126";
    public static String DNS_GLOBAL = "ec2-52-47-157-126.eu-west-3.compute.amazonaws.com";
    public static String IP = DNS_GLOBAL;
    public static String PORT = "9003";
    
    
	
	// Maxim: Copied from UUID implementation :)
    private static volatile SecureRandom numberGenerator = null;
    private static final long MSB = 0x8000000000000000L;

    public static String unique() {
        SecureRandom ng = numberGenerator;
        if (ng == null) {
            numberGenerator = ng = new SecureRandom();
        }

        return Long.toHexString(MSB | ng.nextLong()) + Long.toHexString(MSB | ng.nextLong());
    } 
	
	public static enum LogLevel {
		NONE,
		DEBUG,
		INFO,
		ERROR
	}
	
	public static LogLevel loglevel = LogLevel.INFO;
	
	public static enum ErrorCode {
		UNKNOWN,
		ACTIVITY_SERVICES,
		PAYMENTS_SERVICES,
		PERSON_SERVICES,
		TASKS_SERVICES,
		ATTACHMENT_SERVICES
	}
	
	public static String errorCodeName (ErrorCode code) {
		
		String codeName = "";
		switch (code) {
		case ACTIVITY_SERVICES:
			codeName = "ACTIVITY_SERVICES";
			break;
		case PAYMENTS_SERVICES:
			codeName = "PAYMENTS_SERVICES";
			break;
		case PERSON_SERVICES:
			codeName = "PERSON_SERVICES";
			break;
		case TASKS_SERVICES:
			codeName = "TASKS_SERVICES";
			break;
		case ATTACHMENT_SERVICES:
			codeName = "ATTACHMENT_SERVICES";
			break;
		default:
			codeName = "UNKNOWN";
		}
		return codeName;
	}
	
	public static enum ErrorType {
		UNKNOWN,
	    ACTIVITY_NOT_FOUND,
	    PAYMENT_NOT_FOUND,
		PERSON_NOT_FOUND,
		REGISTRATION_NOT_FOUND,
		TASK_NOT_FOUND,
		ATTACHMENT_NOT_FOUND,
		ACTIVITYOWNER_MISSMATCH,
		PAYMENTSLISTS_MISSMATCH,
		PAYMENT_USERMISSMATCH,
		PAYMENT_CLOSED,
		ZERO_PERSONS,
		INCORRECT_PASSWORD,
		BAD_DATE_FORMAT,
		EXISTENT_DATA,
		PERSON_DISABLED,
		WRONG_TOKEN,
		TOKEN_EXPIRED,
		USER_ENABLED
	}
	
	public static String errorTypeName (ErrorType type) {
		
		String typeName = "";
		switch (type) {
		case ACTIVITY_NOT_FOUND:
			typeName = "ACTIVITY_NOT_FOUND";
			break;
		case PAYMENT_NOT_FOUND:
			typeName = "PAYMENT_NOT_FOUND";
			break;
		case PERSON_NOT_FOUND:
			typeName = "PERSON_NOT_FOUND";
			break;
		case REGISTRATION_NOT_FOUND:
			typeName = "REGISTRATION_NOT_FOUND";
			break;
		case TASK_NOT_FOUND:
			typeName = "TASK_NOT_FOUND";
			break;
		case ACTIVITYOWNER_MISSMATCH:
			typeName = "ACTIVITYOWNER_MISSMATCH";
			break;
		case PAYMENTSLISTS_MISSMATCH:
			typeName = "PAYMENTSLISTS_MISSMATCH";
			break;
		case PAYMENT_USERMISSMATCH:
			typeName = "PAYMENT_USERMISSMATCH";
			break;
		case PAYMENT_CLOSED:
			typeName = "PAYMENT_CLOSED";
			break;
		case ZERO_PERSONS:
			typeName = "ZERO_PERSONS";
			break;
		case INCORRECT_PASSWORD:
			typeName = "INCORRECT_PASSWORD";
			break;
		case BAD_DATE_FORMAT:
			typeName = "BAD_DATE_FORMAT";
			break;
		case EXISTENT_DATA:
			typeName = "EXISTENT_DATA";
			break;
		case PERSON_DISABLED:
			typeName = "PERSON_DISABLED";
			break;
		case ATTACHMENT_NOT_FOUND:
			typeName = "ATTACHMENT_NOT_FOUND";
			break;
		case TOKEN_EXPIRED:
			typeName = "TOKEN_EXPIRED";
			break;
		case WRONG_TOKEN:
			typeName = "WRONG_TOKEN";
			break;
		case USER_ENABLED:
			typeName = "USER_ENABLED";
			break;
		default:
			typeName = "UNKNOWN";
		}
		return typeName;
	}
	
	public static enum PaymentStatus {
		PENDING,
		REQUESTED,
		PAID,
		CONFLICT
	}
	
	public static String paymentStatusName (PaymentStatus status) {
		
		String statusName = "";
		switch (status) {
		case PENDING:
			statusName = "PENDING";
			break;
		case REQUESTED:
			statusName = "REQUESTED";
			break;
		case PAID:
			statusName = "PAID";
			break;
		case CONFLICT:
			statusName = "CONFLICT";
			break;
		default:
			statusName = "UNKNOWN";
		}
		return statusName;
	}
	
	public static PaymentStatus intToStatus (int status) {
		switch (status) {
			case 1: return PaymentStatus.REQUESTED;
			case 2: return PaymentStatus.PAID;
			case 3: return PaymentStatus.CONFLICT;
			default: return PaymentStatus.PENDING;
		}
	}
	
	public static enum RoleType {
		ADMINISTRATOR,
		COMMON_USER,
		ADVANCED_USER,
		GUEST
	}
	
	public static String roleTypeName (RoleType role) {
		
		String roleTypeName = "";
		switch (role) {
		case ADMINISTRATOR:
			roleTypeName = "ADMINISTRATOR";
			break;
		case COMMON_USER:
			roleTypeName = "COMMON_USER";
			break;
		case ADVANCED_USER:
			roleTypeName = "ADVANCED_USER";
			break;
		case GUEST:
			roleTypeName = "GUEST";
			break;
		default:
			roleTypeName = "UNKNOWN";
		}
		return roleTypeName;
	}
	
	public static RoleType intToRoleType (int role) {
		switch (role) {
			case 1: return RoleType.ADMINISTRATOR;
			case 2: return RoleType.COMMON_USER;
			case 3: return RoleType.ADVANCED_USER;
			default: return RoleType.GUEST;
		}
	}
	
	public static enum RegistrationStatus {
		PENDING,
		SENT,
		FORGOTTEN,
		OK,
		ERROR
	}
	
	public static String registrationStatusName (RegistrationStatus status) {
		
		String statusName = "";
		switch (status) {
			case PENDING:
				statusName = "PENDING";
				break;
			case SENT:
				statusName = "SENT";
				break;
			case OK:
				statusName = "OK";
				break;
			case ERROR:
			default:
				statusName = "ERROR";
		}
		return statusName;
	}
}

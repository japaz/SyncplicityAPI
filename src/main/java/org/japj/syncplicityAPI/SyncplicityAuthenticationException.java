package org.japj.syncplicityAPI;

public class SyncplicityAuthenticationException extends Exception {
	// 400 (bad request) errors
	public static final String INVALID_BASIC_AUTH = "Invalid Basic Auth Credentials"; //	Email and/or password were missing or were badly formatted.
	public static final String ONLY_ONE_MACHINE_ALLOWED = "Only One Machine Registration Allowed"; // More than one <Machine> entity was present in the request.
	public static final String BAD_SYSTEMNAME = "SystemName Not Windows Or Mac OS"; // Only 'Windows' or 'Mac OS' values allowed in <SystemName>.
	public static final String BAD_SYSTEMVERSION = "SystemVersion Not In major.minor.build Format"; // <SystemVersion> must be formatted in standard version format.
	public static final String UNKNOWN_LOGIN_ERROR = "Unknown Login Error"; // An unknown error occured during authentication.
	// 401 (unathorized) errors
	public static final String BASIC_AUTH_REQUIRED = "Basic Auth Required"; // Basic HTTP authorization information was missing.
	public static final String EMAIL_OR_PASSWROD_INVALID = "Email or Password Invalid"; // Provided email or password were not recognized or did not match.
	// 403 (forbidden) errors
	public static final String ACCOUNT_DISABLED = "Account Disabled"; // The account is disabled and cannot be logged into.
	public static final String USER_DOES_NOT_OWN_MACHINE = "User Does Not Own Machine"; // The machine_id provided does not belong to this user.
	// 409 errors
	public static final String FRIENDLY_NAME_ALREADY_EXISTS = "Friendly Name Already Exists"; // A machine with this <FriendlyName> already exists in this account.
	// 414 errors
	public static final String FRIENDLY_NAME_TOO_LONG = "Friendly Name Too Long"; // The machine's <FriendlyName> was longer than 50 characters.
	// 418 errors
	public static final String TOO_MANY_PASSWORD_FAILURES = "Too Many Password Failures"; // The account was disabled because of too many failed login attempts and requires a password reset.
	// 419 errors
	public static final String CLIENT_VERSION_TOO_OLD = "Client Version Too Old"; // The client version is no longer supported.
	// 420 errors
	public static final String INVALID_MACHINE_ID = "Invalid Machine ID"; // The machine GUID provided was not recognized or was invalid.
	
    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public SyncplicityAuthenticationException() {
	super();
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
    public SyncplicityAuthenticationException(String message) {
	super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public SyncplicityAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public SyncplicityAuthenticationException(Throwable cause) {
        super(cause);
    }
	
}

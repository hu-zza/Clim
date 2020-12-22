package hu.zza.clim;

public enum Message
{
    PROCESSING_FAILED("The menu can not process the given input: %s%nCause: %s%n%n"),
    INITIALIZATION_FAILED("Object initialization fails because of parameter invalidity:%s%n%n"),
    
    INVALID_NONNULL_ARGUMENT("Parameter '%s' can not be null."),
    INVALID_NONEMPTY_ARGUMENT("Parameter '%s' can not be null or empty."),
    INVALID_NONEMPTY_FIELD("Field '%s' can not be null or empty. Method '%s' failed."),
    MISSING_MENU_ENTRY("%s does not contain MenuEntry '%s'."),
    
    UNKNOWN_COMMAND("Unknown command: '%s'."), INVALID_ARGUMENT("Invalid command arguments."),
    INVALID_POSITION("Invalid or unavailable menu position: '%s'.");
    
    private final String message;
    
    
    Message(String message)
    {
        this.message = message;
    }
    
    
    public String getMessage(Object... context)
    {
        return String.format(message, context);
    }
}

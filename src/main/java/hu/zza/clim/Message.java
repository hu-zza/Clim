package hu.zza.clim;

public enum Message
{
    INVALID_COMMAND("Unknown command."), INVALID_ARGUMENT("Illegal command arguments"),
    INVALID_POSITION("Incorrect or unavailable menu position."),
    PROCESSING_EXCEPTION("The menu can not process the given input: %s%nCause: %s%n%n"),
    INITIALIZATION_EXCEPTION("Incorrect parameters. Object initialization fails.");
    
    private final String message;
    
    
    Message(String message)
    {
        this.message = message;
    }
    
    
    public String getMessage()
    {
        return message;
    }
}

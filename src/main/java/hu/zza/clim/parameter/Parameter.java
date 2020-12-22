package hu.zza.clim.parameter;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;


public class Parameter
{
    private final String                regex;
    private final UnaryOperator<String> parsingOperator;
    private final boolean               optional;
    private final Supplier<String>      defaultValueSupplier;
    private       boolean               present;
    private       String                value;
    
    
    private Parameter(String regex, UnaryOperator<String> parsingOperator, Supplier<String> defaultValueSupplier)
    {
        if (regex == null || regex.isBlank())
        {
            throw new IllegalArgumentException("Parameter 'pattern' can not be null.");
        }
        
        this.regex                = regex;
        this.parsingOperator      = parsingOperator;
        this.optional             = defaultValueSupplier != null;
        this.defaultValueSupplier = defaultValueSupplier;
        this.present              = true;
    }
    
    
    String getRegex()
    {
        return regex;
    }
    
    
    boolean isOptional()
    {
        return optional;
    }
    
    
    boolean isPresent()
    {
        return !isOptional() || present; // = isOptional() ? present : true
    }
    
    
    void setPresent(boolean present)
    {
        this.present = present;
    }
    
    
    public String getValue()
    {
        return value;
    }
    
    
    void setValue(String value)
    {
        this.value = parsingOperator == null ? value : parsingOperator.apply(value);
    }
    
    
    /**
     * It returns the field <code>value</code> of the <code>Parameter</code> or an object by its <code>defaultValueSupplier</code>
     * if the former is null. (For optional <code>Parameter</code> objects.)
     * <p>
     * If this Parameter is not optional, the <code>defaultValueSupplier</code> is null, so it returns an empty string.
     *
     * @return A String object: The value of the Parameter / by the defaultValueSupplier / "".
     */
    public String getOrDefault()
    {
        return value != null ? value : defaultValueSupplier != null ? defaultValueSupplier.get() : "";
    }
    
    
    public static Parameter of(String regex)
    {
        return new Parameter(regex, null, null);
    }
    
    
    public static Parameter of(String regex, String defaultValue)
    {
        return new Parameter(regex, null, () -> defaultValue);
    }
    
    
    public static Parameter of(String regex, UnaryOperator<String> parsingOperator)
    {
        return new Parameter(regex, parsingOperator, null);
    }
    
    
    public static Parameter of(String regex, Supplier<String> defaultValueSupplier)
    {
        return new Parameter(regex, null, defaultValueSupplier);
    }
    
    
    public static Parameter of(String regex,
                               UnaryOperator<String> parsingOperator,
                               Supplier<String> defaultValueSupplier
    )
    {
        return new Parameter(regex, parsingOperator, defaultValueSupplier);
    }
    
    
    @Override
    protected Parameter clone()
    {
        return new Parameter(regex, parsingOperator, defaultValueSupplier);
    }
}

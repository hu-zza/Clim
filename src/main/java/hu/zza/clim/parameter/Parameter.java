package hu.zza.clim.parameter;

import java.util.function.Supplier;


public class Parameter
{
    private final String           regex;
    private final boolean          optional;
    private final Supplier<String> defaultValueSupplier;
    private       boolean          present;
    private       String           value;
    
    
    public Parameter(String regex)
    {
        this(regex, null);
    }
    
    
    public Parameter(String regex, Supplier<String> defaultValueSupplier
    )
    {
        if (regex == null || regex.isBlank())
        {
            throw new IllegalArgumentException("Parameter 'pattern' can not be null.");
        }
        
        
        this.regex                = regex;
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
        this.value = value;
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
    
    
    public Parameter with(Supplier<String> defaultValueSupplier)
    {
        return new Parameter(regex, defaultValueSupplier);
    }
    
    
    @Override
    protected Parameter clone()
    {
        return new Parameter(regex, defaultValueSupplier);
    }
}

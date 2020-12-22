package hu.zza.clim.parameter;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static hu.zza.clim.Message.INVALID_NONEMPTY_ARGUMENT;
import static hu.zza.clim.Message.INVALID_NONNULL_ARGUMENT;


public final class ParameterPattern
{
    private final String              delimiter;
    private final List<ParameterName> parameterNameList;
    private final List<Parameter>     parameterList;
    
    
    public ParameterPattern(String delimiter, List<ParameterName> parameterNameList, Parameter... parameters)
    {
        if (delimiter == null)
        {
            throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("delimiter"));
        }
        
        if (parameterNameList == null || parameterNameList.isEmpty())
        {
            throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("parameterNameList"));
        }
        
        if (parameters == null || parameters.length == 0)
        {
            throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("parameters"));
        }
        
        this.delimiter         = delimiter;
        this.parameterNameList = List.copyOf(parameterNameList);
        this.parameterList     = List.of(parameters);
    }
    
    
    String getDelimiter()
    {
        return delimiter;
    }
    
    
    List<ParameterName> getParameterNameList()
    {
        return List.copyOf(parameterNameList);
    }
    
    
    List<Parameter> getParameterClonesList()
    {
        return parameterList.stream().map(Parameter::clone).collect(Collectors.toList());
    }
    
    
    static String getRegex(String delimiter, List<Parameter> parameterList)
    {
        var stringJoiner = new StringJoiner(delimiter);
        parameterList.stream().filter(Parameter::isPresent).map(Parameter::getRegex).forEach(stringJoiner::add);
        
        return stringJoiner.toString();
    }
}

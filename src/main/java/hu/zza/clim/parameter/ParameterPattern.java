package hu.zza.clim.parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;


public class ParameterPattern
{
    private final String              delimiter;
    private final List<ParameterName> parameterNames;
    private final List<Parameter>     parameters;
    private final int                 optionalCount;
    
    
    public ParameterPattern(String delimiter, List<ParameterName> parameterNames, List<Parameter> parameters)
    {
        this.delimiter      = delimiter;
        this.parameterNames = new ArrayList<>(parameterNames);
        this.parameters     = new ArrayList<>(parameters);
        this.optionalCount  = (int) this.parameters.stream().filter(Parameter::isOptional).count();
    }
    
    
    String getDelimiter()
    {
        return delimiter;
    }
    
    
    List<ParameterName> getParameterNames()
    {
        return new ArrayList<>(parameterNames);
    }
    
    
    List<Parameter> getParameterClonesList()
    {
        return parameters.stream().map(Parameter::clone).collect(Collectors.toList());
    }
    
    
    int getOptionalCount()
    {
        return optionalCount;
    }
    
    
    static String getRegex(String delimiter, List<Parameter> parameterList)
    {
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        parameterList.stream().filter(Parameter::isPresent).map(Parameter::getRegex).forEach(stringJoiner::add);
        
        return stringJoiner.toString();
    }
    
}

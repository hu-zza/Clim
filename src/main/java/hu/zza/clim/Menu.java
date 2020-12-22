package hu.zza.clim;

import hu.zza.clim.parameter.Parameter;
import hu.zza.clim.parameter.ParameterMatcher;
import hu.zza.clim.parameter.ParameterName;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static hu.zza.clim.Message.INITIALIZATION_FAILED;
import static hu.zza.clim.Message.INVALID_NONEMPTY_ARGUMENT;
import static hu.zza.clim.Message.INVALID_NONNULL_ARGUMENT;
import static hu.zza.clim.Message.MISSING_MENU_ENTRY;
import static hu.zza.clim.Message.PROCESSING_FAILED;
import static hu.zza.clim.Message.UNKNOWN_COMMAND;


public final class Menu
{
    private final MenuStructure             menuStructure;
    private final ControlType               controlType;
    private final Map<String, NodePosition> nodeNameMap;
    private final Map<String, LeafPosition> leafNameMap;
    private final ParameterMatcher          parameterMatcher;
    private       Position                  position;
    private       Position                  command;
    private       Position[]                options;
    
    
    private Menu(MenuStructure menuStructure,
                 ControlType controlType,
                 Class<? extends NodePosition> nodeEnum,
                 Class<? extends LeafPosition> leafEnum,
                 Position initialPosition,
                 ParameterMatcher parameterMatcher
    )
    {
        
        if (menuStructure == null || menuStructure.isEmpty())
        {
            throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("menuStructure"));
        }
    
        if (controlType == null)
        {
            throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("controlType"));
        }
        
        if (nodeEnum == null)
        {
            throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("nodeEnum"));
        }
        
        if (leafEnum == null)
        {
            throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("leafEnum"));
        }
        
        if (initialPosition == null)
        {
            throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("initialPosition"));
        }
        
        if (controlType == ControlType.PARAMETRIC && parameterMatcher == null)
        {
            throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("parameterMatcher"));
        }
    
        
        this.menuStructure    = menuStructure;
        this.controlType      = controlType;
        this.position         = initialPosition;
        this.parameterMatcher = parameterMatcher;
        
        
        Map<String, NodePosition> tmpNodeMap = new HashMap<>();
        for (var node : nodeEnum.getEnumConstants())
        {
            if (!menuStructure.containsKey(node))
            {
                throw new IllegalStateException(MISSING_MENU_ENTRY.getMessage("MenuStructure", node.name()));
            }
            
            tmpNodeMap.put(node.name(), node);
        }
        this.nodeNameMap = Map.copyOf(tmpNodeMap);
        
        
        Map<String, LeafPosition> tmpLeafMap = new HashMap<>();
        for (var leaf : leafEnum.getEnumConstants())
        {
            if (!menuStructure.containsKey(leaf))
            {
                throw new IllegalStateException(MISSING_MENU_ENTRY.getMessage("MenuStructure", leaf.name()));
            }
            
            if (!parameterMatcher.containsKeyInPatternMap(leaf))
            {
                throw new IllegalArgumentException(MISSING_MENU_ENTRY.getMessage("PatternMap", leaf.name()));
            }
            
            tmpLeafMap.put(leaf.name(), leaf);
        }
        this.leafNameMap = Map.copyOf(tmpLeafMap);
        
        
        refreshOptions();
    }
    
    
    public static Menu of(MenuStructure menuStructure,
                          Class<? extends NodePosition> nodeEnum,
                          Class<? extends LeafPosition> leafEnum
    )
    {
        return of(menuStructure, ControlType.ORDINAL, nodeEnum, leafEnum);
    }
    
    
    public static Menu of(MenuStructure menuStructure,
                          ControlType controlType,
                          Class<? extends NodePosition> nodeEnum,
                          Class<? extends LeafPosition> leafEnum
    )
    {
        return of(menuStructure, controlType, nodeEnum, leafEnum, null);
    }
    
    
    public static Menu of(MenuStructure menuStructure,
                          ControlType controlType,
                          Class<? extends NodePosition> nodeEnum,
                          Class<? extends LeafPosition> leafEnum,
                          ParameterMatcher parameterMatcher
    )
    {
        return of(menuStructure, controlType, nodeEnum, leafEnum, null, parameterMatcher);
    }
    
    
    public static Menu of(MenuStructure menuStructure,
                          ControlType controlType,
                          Class<? extends NodePosition> nodeEnum,
                          Class<? extends LeafPosition> leafEnum,
                          Position initialPosition,
                          ParameterMatcher parameterMatcher
    )
    {
        try
        {
            if (initialPosition == null) initialPosition = nodeEnum.getEnumConstants()[0];
            return new Menu(menuStructure, controlType, nodeEnum, leafEnum, initialPosition, parameterMatcher);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(INITIALIZATION_FAILED.getMessage(e.getMessage()));
        }
        
    }
    
    
    private static void warnAboutInput(String input, Exception e)
    {
        System.err.print(PROCESSING_FAILED.getMessage(input, e.getMessage()));
    }
    
    
    public void listOptions()
    {
        refreshOptions();
        if (options.length == 0) return;
        switch (controlType)
        {
            case ORDINAL:
            case ORDINAL_TRAILING_ZERO:
                printOrdinalMenu();
                break;
            
            case NOMINAL:
            case PARAMETRIC:
                Arrays.stream(options).map(menuStructure::get).map(MenuEntry::getName).forEach(System.out::println);
                break;
            
            default:
                break;
        }
    }
    
    
    public void chooseOption(String input)
    {
        if (input == null || input.isBlank()) return;
        
        refreshOptions();
        try
        {
            switch (controlType)
            {
                case NOMINAL:
                    Position nominal = getPositionByName(input);
                    setMenuPosition(getValidatedPositionOrThrow(nominal), Map.of());
                    break;
                
                case ORDINAL:
                case ORDINAL_TRAILING_ZERO:
                    int ordinal = Integer.parseInt(input);
                    setMenuPosition(getValidatedPositionOrThrow(ordinal), Map.of());
                    break;
                
                case PARAMETRIC:
                    extractAndUpdateCommandField(input);
                    getValidatedPositionOrThrow(command);
                    parameterMatcher.setText(input);
                    setMenuPosition(command, parameterMatcher.processText(command));
                    break;
                
                default:
                    break;
            }
            refreshOptions();
        }
        catch (Exception e)
        {
            warnAboutInput(input, e);
        }
    }
    
    
    private void refreshOptions()
    {
        options = menuStructure.get(position).getLinks();
    }
    
    
    private void printOrdinalMenu()
    {
        boolean trailingZero = controlType == ControlType.ORDINAL_TRAILING_ZERO;
        
        int i = trailingZero ? 1 : 0;
        
        for (; i < options.length; i++) printMenuEntry(menuStructure.get(options[i]), i);
        
        if (trailingZero) printMenuEntry(menuStructure.get(options[0]), 0);
    }
    
    
    private void printMenuEntry(MenuEntry menuEntry, Integer ordinal)
    {
        if (menuEntry != null)
        {
            if (ordinal != null)
            {
                System.out.printf("%d. %s%n", ordinal, menuEntry.getName());
            }
            else
            {
                System.out.printf("%s%n", menuEntry.getName());
            }
        }
    }
    
    
    private Position getPositionByName(String name)
    {
        String upperCaseName = name.toUpperCase();
        
        if (nodeNameMap.containsKey(upperCaseName))
        {
            return nodeNameMap.get(upperCaseName);
        }
        else if (leafNameMap.containsKey(upperCaseName))
        {
            return leafNameMap.get(upperCaseName);
        }
        else
        {
            throw new IllegalArgumentException(UNKNOWN_COMMAND.getMessage(name));
        }
    }
    
    
    private void extractAndUpdateCommandField(String commandString)
    {
        parameterMatcher
                .getCommandRegex()
                .matcher(commandString)
                .results()
                .findFirst()
                .ifPresent(m -> command = getPositionByName(m.group(1)));
    }
    
    
    private Position getValidatedPositionOrThrow(Object choosenOption)
    {
        String notValid = "";
        
        switch (controlType)
        {
            case ORDINAL:
            case ORDINAL_TRAILING_ZERO:
                int ordinal = (Integer) choosenOption;
                
                if (0 <= ordinal || ordinal < options.length) {
                    return options[ordinal];
                } else {
                    notValid = String.valueOf(ordinal);
                }
                break;
                
            case NOMINAL:
            case PARAMETRIC:
                Position nominal = (Position) choosenOption;
                if (Arrays.asList(options).contains(nominal)) {
                    return nominal;
                } else {
                    notValid = String.valueOf(nominal);
                }
                break;
        }

        throw new IllegalArgumentException(Message.INVALID_POSITION.getMessage(notValid));
    }
    
    
    private void setMenuPosition(Position key, Map<ParameterName, Parameter> parameterMap)
    {
        position = menuStructure.get(key).select(parameterMap);
    }
}

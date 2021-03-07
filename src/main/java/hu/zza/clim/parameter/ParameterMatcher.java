/*
 *     clim   // Command Line Interface Menu
 *
 *     Copyright (C) 2020-2021 Szabó László András <hu@zza.hu>
 *
 *     This file is part of clim.
 *
 *     clim is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     clim is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package hu.zza.clim.parameter;

import hu.zza.clim.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static hu.zza.clim.Message.INVALID_ARGUMENT;
import static hu.zza.clim.Message.INVALID_NONEMPTY_ARGUMENT;
import static hu.zza.clim.Message.INVALID_NONEMPTY_FIELD;


public final class ParameterMatcher {
    private final Pattern                             commandRegex;
    private final HashMap<Position, ParameterPattern> patternMap;
    private       String                              text;
    
    
    public ParameterMatcher(String commandRegex, HashMap<Position, ParameterPattern> patternMap) {
        this(commandRegex, 0, null, patternMap);
    }
    
    
    public ParameterMatcher(String commandRegex,
                            int flags,
                            String text,
                            HashMap<Position, ParameterPattern> patternMap) {
        if (commandRegex == null || commandRegex.isBlank()) {
            throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("commandRegex"));
        }
        
        if (patternMap == null || patternMap.isEmpty()) {
            throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("patternMap"));
        }
        
        this.commandRegex = Pattern.compile(commandRegex, flags);
        this.text         = text;
        this.patternMap   = patternMap;
    }
    
    
    public Pattern getCommandRegex() {
        return commandRegex;
    }
    
    
    public void setText(String text) {
        this.text = text;
    }
    
    
    public boolean containsKeyInPatternMap(Position position) {
        return patternMap.containsKey(position);
    }
    
    
    public Map<ParameterName, Parameter> processText(Position command) {
        if (text == null || text.isEmpty()) {
            throw new IllegalStateException(INVALID_NONEMPTY_FIELD.getMessage("text", "processText"));
        }
        
        ParameterPattern    parameterPattern = patternMap.get(command);
        List<ParameterName> parameterNames   = parameterPattern.getParameterNameList();
        List<Parameter>     parameterList    = getAndPrepareParameterList(parameterPattern);
        
        String regex = ParameterPattern.getRegex(parameterPattern.getDelimiter(), parameterList);
        
        Matcher matcher = Pattern.compile(regex)
                                 .matcher(text);
        
        if (matcher.find()) {
            var matchResult = matcher.toMatchResult();
            
            var updateList = parameterList.stream()
                                          .filter(Parameter::isPresent)
                                          .collect(Collectors.toList());
            
            for (int i = 1; i <= matchResult.groupCount(); i++) {
                try {
                    updateList.get(i - 1)
                              .setValue(matchResult.group(i));
                } catch (IllegalArgumentException exception) {
                    throw new IllegalArgumentException(String.format(exception.getMessage(), parameterNames.get(i)
                                                                                                           .toString()));
                }
            }
        } else {
            throw new IllegalArgumentException(INVALID_ARGUMENT.getMessage());
        }
        
        Map<ParameterName, Parameter> result = new HashMap<>();
        
        for (int i = 0; i < parameterNames.size(); i++) {
            result.put(parameterNames.get(i), parameterList.get(i));
        }
        
        return result;
    }
    
    
    private List<Parameter> getAndPrepareParameterList(ParameterPattern parameterPattern) {
        String          delimiter     = parameterPattern.getDelimiter();
        List<Parameter> parameterList = parameterPattern.getParameterClonesList();
        
        int[] positionsOfOptional = parameterList.stream()
                                                 .filter(Parameter::isOptional)
                                                 .mapToInt(parameterList::indexOf)
                                                 .toArray();
        
        if (positionsOfOptional.length == 0) {
            return parameterList;
        } else {
            return getFittingParameterListVariant(delimiter, parameterList, positionsOfOptional);
        }
        
    }
    
    
    private List<Parameter> getFittingParameterListVariant(String delimiter,
                                                           List<Parameter> parameterList,
                                                           int[] positionsOfOptional) {
        // First try to match with all (positionsOfOptional.length) optionals ( + all non-optionals),
        // then with optionalCount - 1, and so on... At least without any optional (all non-optionals only).
        // The inner cycle iterates through all combinations with given count (i) of optionals.
        for (int i = positionsOfOptional.length; 0 <= i; i--) {
            for (int[] selectedIndices : generateCombinations(positionsOfOptional.length, i)) {
                var selectedOptional = new int[i];
                for (int j = 0; j < i; j++) {
                    selectedOptional[j] = positionsOfOptional[selectedIndices[j]];
                }
                
                setPresentFields(parameterList, positionsOfOptional, selectedOptional);
    
                if (Pattern.matches(ParameterPattern.getRegex(delimiter, parameterList), text)) {
                    return parameterList;
                }
            }
        }
        throw new IllegalArgumentException(INVALID_ARGUMENT.getMessage());
    }
    
    
    private void setPresentFields(List<Parameter> parameterList, int[] optionalIndices, int[] selectedIndices) {
        for (int i : optionalIndices) {
            parameterList.get(i)
                         .setPresent(false);
        }
        
        for (int i : selectedIndices) {
            parameterList.get(i)
                         .setPresent(true);
        }
    }
    
    
    /**
     * Generates all <code>r</code> sized combinations for range 0..<code>n</code> (included, excluded).
     *
     * @param n Upper boundary for the generation. (excluded)
     * @param r Size of a generated set.
     *
     * @return Combinations in lexicographic order.
     */
    private List<int[]> generateCombinations(int n, int r) {
        List<int[]> combinations = new ArrayList<>();
        int[]       combination  = new int[r];
        
        // Initialize with lowest lexicographic combination
        for (int i = 0; i < r; i++) {
            combination[i] = i;
        }
        
        // PATCH...
        if (r == 0) { return List.of(new int[] {0}); }
        if (r == n) { return List.of(combination); }
        
        
        while (combination[r - 1] < n) {
            combinations.add(combination.clone());
            
            // Generate next combination in lexicographic order
            int t = r - 1;
            while (t != 0 && combination[t] == n - r + t) {
                t--;
            }
            combination[t]++;
            for (int i = t + 1; i < r; i++) {
                combination[i] = combination[i - 1] + 1;
            }
        }
        
        return combinations;
    }
}

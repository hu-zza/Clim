/*
 *
 * clim  //  Command Line Interface Menu
 *       //  https://git.zza.hu/clim
 *
 * Copyright (C) 2020-2021 Szabó László András // hu-zza
 *
 * This file is part of clim.
 *
 * clim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * clim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package hu.zza.clim;

import hu.zza.clim.input.ProcessedInput;
import hu.zza.clim.parameter.Parameter;
import hu.zza.clim.parameter.ParameterName;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.json.JSONObject;

public class MenuBuilder {
  private JSONObject menuStructure;
  private JSONObject hiddenStructure;
  private Class<? extends ParameterName> parameterNameEnum;
  private final Map<String, Leaf> leafMap = new HashMap<>();
  private final Map<String, String> delimiterMap = new HashMap<>();
  private final Map<String, Map<ParameterName, Parameter>> parametersMap = new HashMap<>();
  private ControlType controlType;
  private String initialPosition;
  private String commandRegex;

  public Menu build() {
    // return Menu.of(menuStructure, controlType, parameterMatcher, initialPosition);
    return null;
  }

  public MenuBuilder clear() {
    menuStructure = null;
    hiddenStructure = null;
    parameterNameEnum = null;
    leafMap.clear();
    delimiterMap.clear();
    parametersMap.clear();
    controlType = null;
    initialPosition = null;
    commandRegex = null;
    return this;
  }

  public MenuBuilder setMenuStructure(JSONObject menuStructure) {
    this.menuStructure = menuStructure;
    return this;
  }

  public MenuBuilder setHiddenStructure(JSONObject hiddenStructure) {
    this.hiddenStructure = hiddenStructure;
    return this;
  }

  public MenuBuilder setParameterNameEnum(Class<? extends ParameterName> parameterNameEnum) {
    this.parameterNameEnum = parameterNameEnum;
    return this;
  }

  public MenuBuilder setLeaf(
      String name, Function<ProcessedInput, Integer> function, String... links) {
    leafMap.put(name, new Leaf(name, function, links));
    return this;
  }

  public MenuBuilder setLeafParameters(
      String leafName, String delimiter, Map<ParameterName, Parameter> parameters) {
    delimiterMap.put(leafName, delimiter);
    parametersMap.put(leafName, parameters);
    return this;
  }

  public MenuBuilder setControlType(ControlType controlType) {
    this.controlType = controlType;
    return this;
  }

  public MenuBuilder setInitialPosition(String initialPosition) {
    this.initialPosition = initialPosition;
    return this;
  }

  public MenuBuilder setCommandRegex(String commandRegex) {
    this.commandRegex = commandRegex;
    return this;
  }

  private class Leaf {
    private final String name;
    private final Function<ProcessedInput, Integer> function;
    private final String[] links;

    public Leaf(String name, Function<ProcessedInput, Integer> function, String[] links) {
      this.name = name;
      this.function = function;
      this.links = links.clone();
    }
  }
}

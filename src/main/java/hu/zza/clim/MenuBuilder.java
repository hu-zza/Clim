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

import static hu.zza.clim.menu.Message.INITIALIZATION_FAILED;

import hu.zza.clim.input.ProcessedInput;
import hu.zza.clim.menu.MenuStructureBuilder;
import hu.zza.clim.menu.NodePosition;
import hu.zza.clim.menu.Util;
import hu.zza.clim.parameter.Parameter;
import hu.zza.clim.parameter.ParameterMatcher;
import hu.zza.clim.parameter.ParameterName;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuBuilder {
  private ControlType controlType = ControlType.ORDINAL;
  private String initialPosition = "root";
  private String commandRegex = "\\s*(\\w+)\\s*";
  private JSONObject menuStructure = new JSONObject("{\"root\":\"\"}");
  private Class<? extends ParameterName> parameterNameEnum;
  private final Set<Leaf> leaves = new HashSet<>();
  private final Map<String, List<ParameterName>> parameterNameMap = new HashMap<>();
  private final Map<String, List<Parameter>> parameterMap = new HashMap<>();
  private final Map<String, String> delimiterMap = new HashMap<>();
  private ParameterMatcher parameterMatcher;

  public Menu build() {
    MenuStructureBuilder msb = new MenuStructureBuilder().setRawMenuStructure(menuStructure);

    leaves.forEach(e -> msb.setLeaf(e.name, e.function, e.links));

    try {
      Menu menu =
          new Menu(msb.build(), controlType, new NodePosition(initialPosition), parameterMatcher);
      menu.printShortLicence();
      return menu;
    } catch (Exception e) {
      throw new IllegalArgumentException(INITIALIZATION_FAILED.getMessage(e.getMessage()));
    }
  }

  public MenuBuilder clear() {
    controlType = ControlType.ORDINAL;
    initialPosition = "root";
    commandRegex = "\\s*(\\w+)\\s*";
    menuStructure = new JSONObject("{\"root\":\"\"}");
    parameterNameEnum = null;
    leaves.clear();
    parameterNameMap.clear();
    parameterMap.clear();
    delimiterMap.clear();
    return this;
  }

  public MenuBuilder setControlType(ControlType controlType) {
    Util.assertNonNull("controlType", controlType);
    this.controlType = controlType;
    return this;
  }

  // TODO: 2021. 05. 09. check validity at build()
  public MenuBuilder setInitialPosition(String initialPosition) {
    Util.assertNonNull("initialPosition", initialPosition);
    this.initialPosition = initialPosition;
    return this;
  }

  public MenuBuilder setCommandRegex(String commandRegex) {
    Util.assertNonNull("commandRegex", commandRegex);
    this.commandRegex = commandRegex;
    return this;
  }

  public MenuBuilder setMenuStructure(String menuStructureJSON) throws JSONException {
    Util.assertNonNull("menuStructureJSON", menuStructureJSON);
    this.menuStructure = new JSONObject(menuStructureJSON);
    return this;
  }

  public MenuBuilder setMenuStructure(JSONObject menuStructure) {
    Util.assertNonNull("menuStructure", menuStructure);
    this.menuStructure = menuStructure;
    return this;
  }

  public MenuBuilder setParameterNameEnum(Class<? extends ParameterName> parameterNameEnum) {
    Util.assertNonNull("parameterNameEnum", parameterNameEnum);
    this.parameterNameEnum = parameterNameEnum;
    return this;
  }

  public MenuBuilder setLeaf(
      String name, Function<ProcessedInput, Integer> function, String... links) {

    Util.assertNonNull(Map.of("name", name, "function", function, "links", links));

    leaves.add(new Leaf(name, function, links));
    return this;
  }

  public MenuBuilder setLeafParameters(
      String leafName,
      String delimiter,
      List<ParameterName> parameterNames,
      List<Parameter> parameters) {

    Util.assertNonNull(
        Map.of(
            "leafName",
            leafName,
            "delimiter",
            delimiter,
            "parameterNames",
            parameterNames,
            "parameters",
            parameters));

    parameterNameMap.put(leafName, parameterNames);
    parameterMap.put(leafName, parameters);
    delimiterMap.put(leafName, delimiter);
    return this;
  }

  public MenuBuilder clearLeafParameters(String leafName) {
    Util.assertNonNull("leafName", leafName);
    parameterNameMap.remove(leafName);
    parameterMap.remove(leafName);
    delimiterMap.remove(leafName);
    return this;
  }

  private static class Leaf {
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

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

import hu.zza.clim.menu.Util;
import hu.zza.clim.parameter.Parameter;
import hu.zza.clim.parameter.ParameterMatcher;
import hu.zza.clim.parameter.ParameterName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ParameterMatcherBuilder {
  private String commandRegex = "\\s*(\\w+)\\s*";
  private Class<? extends ParameterName> parameterNameEnum;
  private final Map<String, List<ParameterName>> parameterNameMap = new HashMap<>();
  private final Map<String, List<Parameter>> parameterMap = new HashMap<>();
  private final Map<String, String> delimiterMap = new HashMap<>();
  private ParameterMatcher parameterMatcher;

  public void clear() {
    commandRegex = "\\s*(\\w+)\\s*";
    parameterNameEnum = null;
    parameterNameMap.clear();
    parameterMap.clear();
    delimiterMap.clear();
  }

  public ParameterMatcherBuilder setCommandRegex(String commandRegex) {
    Util.assertNonNull("commandRegex", commandRegex);
    this.commandRegex = commandRegex;
    return this;
  }



  public ParameterMatcherBuilder setParameterNameEnum(Class<? extends ParameterName> parameterNameEnum) {
    Util.assertNonNull("parameterNameEnum", parameterNameEnum);
    this.parameterNameEnum = parameterNameEnum;
    return this;
  }

  public ParameterMatcherBuilder setLeafParameters(
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

  public ParameterMatcherBuilder clearLeafParameters(String leafName) {
    Util.assertNonNull("leafName", leafName);
    parameterNameMap.remove(leafName);
    parameterMap.remove(leafName);
    delimiterMap.remove(leafName);
    return this;
  }

}

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

import hu.zza.clim.menu.LeafPosition;
import hu.zza.clim.menu.Position;
import hu.zza.clim.menu.Util;
import hu.zza.clim.parameter.Parameter;
import hu.zza.clim.parameter.ParameterMatcher;
import hu.zza.clim.parameter.ParameterName;
import hu.zza.clim.parameter.ParameterPattern;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder class for {@link ParameterMatcher}.
 *
 * @since 0.1
 */
public final class ParameterMatcherBuilder {
  private final Map<Position, ParameterPattern> patternMap = new HashMap<>();
  private String commandRegex = "";

  public ParameterMatcher build() {
    return new ParameterMatcher(commandRegex, patternMap);
  }

  public void clear() {
    commandRegex = "";
    patternMap.clear();
  }

  public ParameterMatcherBuilder setCommandRegex(String commandRegex) {
    Util.assertNonNull("commandRegex", commandRegex);
    this.commandRegex = commandRegex;
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

    Position position = Position.getByName(leafName);

    if (position instanceof LeafPosition) {
      patternMap.put(position, new ParameterPattern(delimiter, parameterNames, parameters));
    }
    return this;
  }

  public ParameterMatcherBuilder clearLeafParameters(String leafName) {
    Util.assertNonNull("leafName", leafName);
    patternMap.remove(Position.getByName(leafName));
    return this;
  }
}

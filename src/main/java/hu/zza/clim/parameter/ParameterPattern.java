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

package hu.zza.clim.parameter;

import static hu.zza.clim.menu.Message.INVALID_NONEMPTY_ARGUMENT;
import static hu.zza.clim.menu.Message.INVALID_NONNULL_ARGUMENT;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Represents a complex pattern of {@link Parameter parameters} that is used for evaluating user
 * input.
 */
public final class ParameterPattern {

  private final String delimiter;
  private final Map<ParameterName, Parameter> parameters = new LinkedHashMap<>();

  /**
   * Creates a {@link ParameterPattern}. A {@link ParameterPattern} object has only {@code
   * delimiter}, but no regex. The reason is that every {@link Parameter} has its own, so only a
   * delimiter is needed to concatenate them.
   *
   * @param delimiter character sequence delimiter
   * @param parameterNames names of {@link Parameter parameters} in sequential order
   * @param parameters {@link Parameter} objects
   */
  public ParameterPattern(
      String delimiter, List<ParameterName> parameterNames, List<Parameter> parameters) {
    if (delimiter == null) {
      throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("delimiter"));
    }

    if (parameterNames == null || parameterNames.isEmpty()) {
      throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("parameterNames"));
    }

    if (parameters == null || parameters.isEmpty()) {
      throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("parameters"));
    }

    this.delimiter = delimiter;

    for(int i = 0; i < parameterNames.size(); i++) {
      this.parameters.put(parameterNames.get(i), parameters.get(i));
    }
  }

  static String getRegex(String delimiter, List<Parameter> parameterList) {
    var stringJoiner = new StringJoiner(delimiter);
    parameterList.stream()
        .filter(Parameter::isPresent)
        .map(Parameter::getRegex)
        .forEach(stringJoiner::add);

    return stringJoiner.toString();
  }

  String getDelimiter() {
    return delimiter;
  }

  List<ParameterName> getParameterNameList() {
    return new ArrayList<>(parameters.keySet());
  }

  List<Parameter> getParameterClonesList() {
    return parameters.values().stream().map(Parameter::clone).collect(Collectors.toList());
  }
}

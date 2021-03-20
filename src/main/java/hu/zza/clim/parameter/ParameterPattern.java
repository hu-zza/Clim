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

import static hu.zza.clim.Message.INVALID_NONEMPTY_ARGUMENT;
import static hu.zza.clim.Message.INVALID_NONNULL_ARGUMENT;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public final class ParameterPattern {

  private final String delimiter;
  private final List<ParameterName> parameterNameList;
  private final List<Parameter> parameterList;

  public ParameterPattern(
      String delimiter, List<ParameterName> parameterNameList, Parameter... parameters) {
    if (delimiter == null) {
      throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("delimiter"));
    }

    if (parameterNameList == null || parameterNameList.isEmpty()) {
      throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("parameterNameList"));
    }

    if (parameters == null || parameters.length == 0) {
      throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("parameters"));
    }

    this.delimiter = delimiter;
    this.parameterNameList = List.copyOf(parameterNameList);
    this.parameterList = List.of(parameters);
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
    return List.copyOf(parameterNameList);
  }

  List<Parameter> getParameterClonesList() {
    return parameterList.stream().map(Parameter::clone).collect(Collectors.toList());
  }
}

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

package hu.zza.clim.menu;

import hu.zza.clim.parameter.Parameter;
import hu.zza.clim.parameter.ParameterName;
import java.util.Map;
import java.util.regex.Pattern;

public class ProcessedInput {

  public static final ProcessedInput NULL = new ProcessedInput(Map.of());
  private final String original;
  private String commandString;
  private Map<ParameterName, Parameter> parameterMap;

  public ProcessedInput(String nominalInput) {
    this.original = nominalInput;
  }

  public ProcessedInput(Map<ParameterName, Parameter> parameterMap) {
    this.original = "";
    this.parameterMap = parameterMap;
  }

  public String getCommandString() {
    return commandString != null ? commandString : original;
  }

  public int getCommandOrdinal() {
    if (parsableToInteger()) {
      return Integer.parseInt(getCommandString());
    }
    throw new IllegalStateException();
  }

  public Map<ParameterName, Parameter> getParameterMap() {
    return parameterMap;
  }

  public boolean parsableToInteger() {
    return Pattern.matches("^\\d+$", getCommandString());
  }
}

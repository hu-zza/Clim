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

import hu.zza.clim.ClimException;
import java.util.List;

public abstract class Util {

  public static void assertNonNull(String variableName, Object variable) {
    assertNonNull(variableName, variable, Message.INVALID_NONNULL_ARGUMENT);
  }

  public static void assertNonNull(String variableName, Object variable, Message errorMessage) {
    if (variable == null) {
      throw new ClimException(errorMessage.getMessage(variableName));
    }
  }

  public static void assertNonNull(List<String> variableNames, Object... variables) {
    assertNonNull(Message.INVALID_NONNULL_ARGUMENT, variableNames, variables);
  }

  public static void assertNonNull(
      Message errorMessage, List<String> variableNames, Object... variables) {
    for (int i = 0; i < variableNames.size(); i++) {
      if (variables[i] == null) {
        throw new ClimException(errorMessage.getMessage(variableNames.get(i)));
      }
    }
  }
}

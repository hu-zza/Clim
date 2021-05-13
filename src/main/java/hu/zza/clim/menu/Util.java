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

import java.util.Map;

public abstract class Util {

  public static void assertNonNull(String parameterName, Object parameter) {
    assertNonNull(parameterName, parameter, Message.INVALID_NONNULL_ARGUMENT);
  }

  public static void assertNonNull(String parameterName, Object parameter, Message errorMessage) {
    if (parameter == null) {
      throw new IllegalArgumentException(errorMessage.getMessage(parameterName));
    }
  }

  public static void assertNonNull(Map<String, Object> parameters) {
    assertNonNull(parameters, Message.INVALID_NONNULL_ARGUMENT);
  }

  public static void assertNonNull(Map<String, Object> parameters, Message errorMessage) {
    for (var entry : parameters.entrySet()) {
      if (entry.getValue() == null) {
        throw new IllegalArgumentException(errorMessage.getMessage(entry.getKey()));
      }
    }
  }
}

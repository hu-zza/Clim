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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface ClimOption {
  Map<Class<? extends ClimOption>, ? extends ClimOption> DEFAULT_VALUES =
      Map.of(
          UserInterface.class,
          UserInterface.ORDINAL,
          HeaderStyle.class,
          HeaderStyle.STANDARD,
          NavigationMode.class,
          NavigationMode.STANDARD);

  static Map<Class<? extends ClimOption>, ClimOption> getClimOptionMap(ClimOption... climOptions) {
    return getClimOptionMap(new HashSet<>(Arrays.asList(climOptions)));
  }

  static Map<Class<? extends ClimOption>, ClimOption> getClimOptionMap(
      Set<ClimOption> climOptions) {
    Map<Class<? extends ClimOption>, ClimOption> result = new HashMap<>(DEFAULT_VALUES);

    climOptions.stream()
        .collect(
            Collectors.groupingBy(
                ClimOption::getClass, Collectors.reducing(ClimOption::reduceOptions)))
        .forEach((k, v) -> result.put(k, v.orElseThrow()));

    return result;
  }

  private static ClimOption reduceOptions(ClimOption optionA, ClimOption optionB) {
    if (optionA != null) {
      if (optionB == null) {
        return optionA;
      } else {
        return DEFAULT_VALUES.get(optionA.getClass());
      }
    } else {
      throw new IllegalStateException();
    }
  }
}

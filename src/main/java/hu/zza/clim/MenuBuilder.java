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

import hu.zza.clim.menu.MenuStructure;
import hu.zza.clim.menu.Util;
import hu.zza.clim.parameter.ParameterMatcher;
import java.util.Arrays;
import java.util.List;

/**
 * Builder class for {@link Menu}.
 *
 * @since 0.1
 */
public final class MenuBuilder {
  private static final List<UserInterface> hasParameterMatcherDependency =
      List.of(UserInterface.PARAMETRIC);

  private MenuStructure menuStructure = new MenuStructure();
  private ParameterMatcher parameterMatcher;
  private ClimOption[] climOptions = new ClimOption[0];

  public Menu build() {
    try {
      return buildMenu();
    } catch (Exception e) {
      throw new IllegalArgumentException(INITIALIZATION_FAILED.getMessage(e.getMessage()), e);
    }
  }

  private Menu buildMenu() {
    checkParametersBeforeBuild();
    return new Menu(menuStructure, parameterMatcher, climOptions);
  }

  private void checkParametersBeforeBuild() {
    checkMenuStructure();
    checkParameterMatcher();
  }

  private void checkMenuStructure() {
    if (menuStructure.isEmpty()) {
      throw new IllegalStateException("'menuStructure' is empty!");
    }

    if (!menuStructure.isFinalized()) {
      throw new IllegalStateException("'menuStructure' isn't finalized!");
    }
  }

  private void checkParameterMatcher() {
    if (parameterMatcher == null && isParameterMatcherRequired()) {
      throw new IllegalStateException("'parameterMatcher' is required, but it is null!");
    }
  }

  private boolean isParameterMatcherRequired() {
    return Arrays.stream(climOptions).anyMatch(hasParameterMatcherDependency::contains);
  }

  public MenuBuilder setMenuStructure(MenuStructure menuStructure) {
    Util.assertNonNull("menuStructure", menuStructure);
    this.menuStructure = menuStructure;
    return this;
  }

  public MenuBuilder setParameterMatcher(ParameterMatcher parameterMatcher) {
    Util.assertNonNull("parameterMatcher", parameterMatcher);
    this.parameterMatcher = parameterMatcher;
    return this;
  }

  public MenuBuilder setClimOptions(ClimOption... climOptions) {
    Util.assertNonNull("climOptions", climOptions);
    this.climOptions = climOptions;
    return this;
  }

  public MenuBuilder clear() {
    menuStructure = new MenuStructure();
    parameterMatcher = null;
    climOptions = new ClimOption[0];
    return this;
  }
}

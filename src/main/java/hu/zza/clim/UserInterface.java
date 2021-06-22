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

import hu.zza.clim.parameter.ParameterMatcher;

/** Represents the {@link Menu} main behavior and controlling capabilities.
 * @since 0.1
 */
public enum UserInterface implements ClimOption {
  /** Simple menu, navigating with nominals (names of options). */
  NOMINAL,

  /** Simple menu, navigating with ordinals (serial numbers of options). */
  ORDINAL,

  /**
   * Simple menu, navigating with ordinals (serial numbers of options). Exactly same as {@link
   * UserInterface#ORDINAL}, but the first option (index and ordinal: 0) is displayed last.
   */
  ORDINAL_TRAILING_ZERO,

  /**
   * Complex menu, navigating with nominals (names of options) and parameters. In order to use this,
   * a {@link ParameterMatcher} is needed.
   */
  PARAMETRIC
}

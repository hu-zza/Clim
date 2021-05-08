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

import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface Parameters {
  /**
   * Returns a concrete, non-parser {@link Parameter}.
   *
   * @param regex the regex pattern for value extraction from the raw input
   * @return the concrete, non-parser {@link Parameter}
   */
  static Parameter of(String regex) {
    return new Parameter(regex, null, null);
  }

  /**
   * Returns an optional, parser {@link Parameter}.
   *
   * @param regex the regex pattern for value extraction from the raw input
   * @param parsingOperator the preprocessor before saving the extracted as the value of the {@link
   *     Parameter}
   * @param defaultValueSupplier the source of the value of the {@link Parameter} if it is null
   * @return the optional, parser {@link Parameter}
   */
  static Parameter of(
      String regex, UnaryOperator<String> parsingOperator, Supplier<String> defaultValueSupplier) {
    return new Parameter(regex, parsingOperator, defaultValueSupplier);
  }

  /**
   * Returns a concrete, parser {@link Parameter}.
   *
   * @param regex the regex pattern for value extraction from the raw input
   * @param parsingOperator the preprocessor before saving the extracted as the value of the {@link
   *     Parameter}
   * @return the concrete, parser {@link Parameter}
   */
  static Parameter of(String regex, UnaryOperator<String> parsingOperator) {
    return new Parameter(regex, parsingOperator, null);
  }

  /**
   * Returns an optional, non-parser {@link Parameter}. Works same as {@link Parameters#of(String,
   * Supplier)} is called with constant {@link Supplier}: {@code Parameter.of(<regex>, () ->
   * <defaultValue>)}
   *
   * @param regex the regex pattern for value extraction from the raw input
   * @param defaultValue the {@link String} value of the {@link Parameter} if its value is null
   * @return the optional, non-parser {@link Parameter}
   */
  static Parameter of(String regex, String defaultValue) {
    return new Parameter(regex, null, () -> defaultValue);
  }

  /**
   * Returns an optional, non-parser {@link Parameter}.
   *
   * @param regex the regex pattern for value extraction from the raw input
   * @param defaultValueSupplier the source of the value of the {@link Parameter} if it is null
   * @return the optional, non-parser {@link Parameter}
   */
  static Parameter of(String regex, Supplier<String> defaultValueSupplier) {
    return new Parameter(regex, null, defaultValueSupplier);
  }

  /**
   * Creates a {@link ParameterPattern}. A {@link ParameterPattern} object has only {@code *
   * delimiter}, but no regex. The reason is that every {@link Parameter} has its own, so only a *
   * delimiter is needed to concatenate them.
   *
   * @param delimiter character sequence delimiter
   * @param parameters {@link ParameterName} and {@link Parameter} bindings in sequential order
   * @return a {@link ParameterPattern} object
   */
  static ParameterPattern makePattern(
      String delimiter, List<ParameterName> parameterNames, List<Parameter> parameters) {
    return new ParameterPattern(delimiter, parameterNames, parameters);
  }
}

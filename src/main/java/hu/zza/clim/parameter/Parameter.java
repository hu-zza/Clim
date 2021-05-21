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

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/** Represents a single parameter which can be concrete or optional. */
public final class Parameter implements Cloneable {

  public static final Parameter NULL = new Parameter("()", null, null);

  static {
    NULL.setValue("");
  }

  private final String regex;
  private final UnaryOperator<String> parsingOperator;
  private final Supplier<String> defaultValueSupplier;
  private boolean present;
  private String value;

  Parameter(
      String regex, UnaryOperator<String> parsingOperator, Supplier<String> defaultValueSupplier) {
    if (regex == null || regex.isEmpty()) {
      throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("regex"));
    }

    this.regex = regex;
    this.parsingOperator = parsingOperator;
    this.defaultValueSupplier = defaultValueSupplier;
    this.present = true;
  }

  String getRegex() {
    return regex;
  }

  boolean isPresent() {
    return !isOptional() || present; // = isOptional() ? present : true
  }

  boolean isOptional() {
    return defaultValueSupplier != null;
  }

  void setPresent(boolean present) {
    this.present = present;
  }

  /**
   * Returns the {@link Parameter parameters} value as a {@link String}.
   *
   * @return the {@link Parameter parameters} value as a {@link String}
   */
  public String getValue() {
    return value;
  }

  void setValue(String value) {
    this.value = parsingOperator == null ? value : parsingOperator.apply(value);
  }

  /**
   * It returns the field {@code value} of the {@link Parameter} or an object by its {@code
   * defaultValueSupplier} if the former is null. (For optional {@code Parameter} objects.)
   *
   * <p>If this {@link Parameter} is not optional, the {@code defaultValueSupplier} is null, so it
   * returns an empty string.
   *
   * @return a {@link String}: The value of the {@link Parameter} / by the {@code
   *     defaultValueSupplier} / "".
   */
  public String getOrDefault() {
    return value != null ? value : defaultValueSupplier != null ? defaultValueSupplier.get() : "";
  }

  /**
   * Returns a copy of this {@link Parameter} with the specified {@code parsingOperator}.
   *
   * @param parsingOperator the {@link UnaryOperator} to use
   * @return a {@link Parameter} based on this instance with the specified {@code parsingOperator}
   */
  public Parameter with(UnaryOperator<String> parsingOperator) {
    return new Parameter(regex, parsingOperator, defaultValueSupplier);
  }

  /**
   * Returns a copy of this {@link Parameter} with the specified {@code defaultValue}. Works same as
   * {@link Parameter#with(Supplier)} is called with constant {@link Supplier}: {@code
   * Parameter.with(() -> <defaultValue>)}
   *
   * @param defaultValue the {@link String} to use
   * @return a {@link Parameter} based on this instance with the specified {@code defaultValue}
   */
  public Parameter with(String defaultValue) {
    return new Parameter(regex, parsingOperator, () -> defaultValue);
  }

  /**
   * Returns a copy of this {@link Parameter} with the specified {@code defaultValueSupplier}.
   *
   * @param defaultValueSupplier the {@link Supplier} to use
   * @return a {@link Parameter} based on this instance with the specified {@code
   *     defaultValueSupplier}
   */
  public Parameter with(Supplier<String> defaultValueSupplier) {
    return new Parameter(regex, parsingOperator, defaultValueSupplier);
  }

  @Override
  protected Parameter clone() {
    try {
      return (Parameter) super.clone();
    } catch (CloneNotSupportedException e) {
      return new Parameter(regex, parsingOperator, defaultValueSupplier);
    }
  }
}

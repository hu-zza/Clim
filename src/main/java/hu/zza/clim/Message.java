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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Message pool for the whole module. This will be refactored with a ResourceBundle in the near
 * future.
 */
public enum Message {
  PROCESSING_FAILED,
  INITIALIZATION_FAILED,

  INVALID_NONNULL_ARGUMENT,
  INVALID_NONEMPTY_ARGUMENT,
  INVALID_NONEMPTY_FIELD,
  MISSING_MENU_ENTRY,

  UNKNOWN_COMMAND,
  INVALID_ARGUMENT,
  INVALID_POSITION,
  INVALID_TYPE,
  MISSING_ANNOTATION,
  MISCONFIGURED_ANNOTATION,

  GNU_GPL;

  private final String message;

  ResourceBundle resourceBundle =
      ResourceBundle.getBundle("MessageTemplate", Locale.ENGLISH);

  Message() {
    this.message =
        resourceBundle.containsKey(this.name()) ? resourceBundle.getString(this.name()) : "";
  }

  /**
   * Returns the message complemented with the {@code context}.
   *
   * @param context vararg to add more detail for the message
   * @return the message complemented with the {@code context}
   */
  public String getMessage(Object... context) {
    return String.format(message, context);
  }
}

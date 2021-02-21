/*
 *     clim   // Command Line Interface Menu
 *
 *     Copyright (C) 2020-2021 Szabó László András
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package hu.zza.clim;

public enum Message
{
    PROCESSING_FAILED("The menu can not process the given input: '%s'%nCause: %s%n%n"),
    INITIALIZATION_FAILED("Object initialization fails because of parameter invalidity:%n%s%n%n"),
    
    INVALID_NONNULL_ARGUMENT("Parameter '%s' can not be null."),
    INVALID_NONEMPTY_ARGUMENT("Parameter '%s' can not be null or empty."),
    INVALID_NONEMPTY_FIELD("Field '%s' can not be null or empty. Method '%s' failed."),
    MISSING_MENU_ENTRY("%s does not contain MenuEntry '%s'."),
    
    UNKNOWN_COMMAND("Unknown command: '%s'"), INVALID_ARGUMENT("Invalid command arguments."),
    INVALID_POSITION("Invalid or unavailable menu position: '%s'");
    
    private final String message;
    
    
    Message(String message)
    {
        this.message = message;
    }
    
    
    public String getMessage(Object... context)
    {
        return String.format(message, context);
    }
}

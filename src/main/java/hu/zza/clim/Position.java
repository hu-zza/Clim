/*
 *     clim   // Command Line Interface Menu
 *
 *     Copyright (C) 2020-2021 Szabó László András <hu@zza.hu>
 *
 *     This file is part of clim.
 *
 *     clim is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     clim is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package hu.zza.clim;


/**
 * Object that represents menu's current position.
 * A position could symbolize a node or a leaf.
 */

public interface Position {
    String name();
}

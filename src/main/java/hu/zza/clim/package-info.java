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

/**
 * Provides the fundamental classes for building a {@link hu.zza.clim.Menu}. Building it is quite
 * easy with {@link hu.zza.clim.MenuBuilder} which is a top-level builder. There are other builders
 * for components, like {@link hu.zza.clim.MenuStructureBuilder} and {@link
 * hu.zza.clim.ParameterMatcherBuilder}. {@link hu.zza.clim.ClimOption} provides a common interface
 * for various enums used to setup {@link hu.zza.clim.Menu} at building.
 *
 * @since 0.1
 */
package hu.zza.clim;

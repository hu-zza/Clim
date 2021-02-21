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

import hu.zza.clim.parameter.Parameter;
import hu.zza.clim.parameter.ParameterName;

import java.util.Map;
import java.util.function.Function;

import static hu.zza.clim.Message.INVALID_NONEMPTY_ARGUMENT;
import static hu.zza.clim.Message.INVALID_NONNULL_ARGUMENT;


public abstract class MenuEntry
{
    private final String                                           name;
    private final Position                                         position;
    private final Position[]                                       links;
    private final Function<Map<ParameterName, Parameter>, Integer> function;
    private final NodePosition[]                                   functionLinks;
    
    
    // Full-fledged, low-level constructor
    private MenuEntry(Position position,
                      String name,
                      Position[] links,
                      Function<Map<ParameterName, Parameter>, Integer> function,
                      NodePosition... functionLinks
    )
    {
        if (position == null)
        {
            throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("position"));
        }
        
        if (name == null || name.isBlank())
        {
            throw new IllegalArgumentException(INVALID_NONEMPTY_ARGUMENT.getMessage("name"));
        }
        
        if (links == null)
        {
            throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("links"));
        }
        
        if (function == null)
        {
            throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("function"));
        }
        
        if (functionLinks == null)
        {
            throw new IllegalArgumentException(INVALID_NONNULL_ARGUMENT.getMessage("functionLinks"));
        }
        
        this.position      = position;
        this.name          = name;
        this.links         = links.clone();
        this.function      = function;
        this.functionLinks = functionLinks.clone();
    }
    
    
    ///////////////////////////////////////////////////
    // GETTERS ONLY FOR THE PACKAGE-PRIVATE PROCESSING
    
    
    String getName()
    {
        return name;
    }
    
    
    Position getPosition()
    {
        return position;
    }
    
    
    Position[] getLinks()
    {
        return links;
    }
    
    
    Function<Map<ParameterName, Parameter>, Integer> getFunction()
    {
        return function;
    }
    
    
    Position[] getFunctionLinks()
    {
        return functionLinks;
    }
    
    
    /**
     * Menu performs this method on every selected MenuEntry, then redirects itself to the returning Position.
     * Nodes returns only with the Position of themselves, because selecting a Node means only this (navigating).
     * Leaves performs a function, the returning Position depends on the returning value of its function.
     *
     * @return The Position where the Menu redirects itself after selecting a MenuEntry.
     */
    Position select(Map<ParameterName, Parameter> parameterMap)
    {
        return getFunctionLinks()[getFunction().apply(parameterMap)];
    }
    
    
    ///////////////////////////
    // INNER STATIC SUBCLASSES
    
    
    public static final class Node extends MenuEntry
    {
        public Node(NodePosition position, String name, Position... links)
        {
            /*
            Constructor parameters in order:
            
            position        -   The position of this MenuEntry.
            name            -   Human-friendly name of this MenuEntry.
            links           -   An array of Positions of other reachable MenuEntries (Nodes and Leaves).
            
            function        -   "Placeholder" function, always returns 0.
            functionLinks   -   An array of Positions with only one element: The Position of this MenuEntry.
             */
            super(position, name, links, parameterMap -> 0, position);
        }
    }
    
    
    public static final class Leaf extends MenuEntry
    {
        public Leaf(LeafPosition position,
                    String name,
                    Function<Map<ParameterName, Parameter>, Integer> function,
                    NodePosition... functionLinks
        )
        {
            /*
            Constructor parameters in order:

            position        -   The position of this MenuEntry
            name            -   Human-friendly name of this MenuEntry
            
            links           -   There is no link, because it's not a Node. You can not jump from the Position of a Leaf
                                to other Positions, because you never jump on that. You can choose the Position of a
                                Leaf from the links of a Node, then it's function performs and returns with an integer.
                                After that your position will be functionLinks[returnValue]. So Position of a Leaf is
                                never used as real position, it's only a reference.
           
            function        -   The essence of a Leaf. Performs a task and returns an integer (index for forwarding).
                                Return values: 0 is for the success, others are error codes.
                                
            functionLinks   -   An array of Positions: the forwarding options for a Leaf. The outcome of a Leaf's
                                function controls the forwarding: functionLinks[returnValue].
             */
            super(position, name, new Position[0], function, functionLinks);
        }
    }
}

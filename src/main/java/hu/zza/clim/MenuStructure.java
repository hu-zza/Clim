package hu.zza.clim;

import java.util.HashMap;


public final class MenuStructure
{
    private final HashMap<Position, MenuEntry> menu = new HashMap<>();
    private       boolean                      finalized;
    
    
    public void setFinalized()
    {
        finalized = true;
    }
    
    
    public MenuEntry put(MenuEntry menuEntry)
    {
        return finalized ? null : menu.put(menuEntry.getPosition(), menuEntry);
    }
    
    
    boolean containsKey(Position position)
    {
        return menu.containsKey(position);
    }
    
    
    boolean isEmpty()
    {
        return menu.isEmpty();
    }
    
    
    MenuEntry get(Position position)
    {
        return menu.get(position);
    }
}

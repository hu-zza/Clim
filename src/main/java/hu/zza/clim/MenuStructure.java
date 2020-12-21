package hu.zza.clim;

import java.util.HashMap;


public class MenuStructure
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
    
    
    MenuEntry get(Position position)
    {
        return menu.get(position);
    }
}

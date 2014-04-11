package com.pauldavdesign.mineauz.minigames.minigame.regions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.pauldavdesign.mineauz.minigames.MinigamePlayer;
import com.pauldavdesign.mineauz.minigames.MinigameUtils;
import com.pauldavdesign.mineauz.minigames.menu.InteractionInterface;
import com.pauldavdesign.mineauz.minigames.menu.Menu;
import com.pauldavdesign.mineauz.minigames.menu.MenuItem;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemCustom;
import com.pauldavdesign.mineauz.minigames.menu.MenuItemPage;
import com.pauldavdesign.mineauz.minigames.minigame.modules.RegionModule;

public class MenuItemRegion extends MenuItem{
	
	private Region region;
	private RegionModule rmod;

	public MenuItemRegion(String name, Material displayItem, Region region, RegionModule rmod) {
		super(name, displayItem);
		this.region = region;
		this.rmod = rmod;
	}

	public MenuItemRegion(String name, List<String> description, Material displayItem, Region region, RegionModule rmod) {
		super(name, description, displayItem);
		this.region = region;
		this.rmod = rmod;
	}
	
	@Override
	public ItemStack onClick(){
		createMenu(getContainer().getViewer(), getContainer(), region);
		return null;
	}
	
	@Override
	public ItemStack onRightClick(){
		rmod.removeRegion(region.getName());
		getContainer().removeItem(getSlot());
		return null;
	}
	
	public static void createMenu(MinigamePlayer viewer, Menu previousPage, Region region){
		Menu m = new Menu(3, "Regions", viewer);
		m.setPreviousPage(previousPage);
		List<MenuItem> items = new ArrayList<MenuItem>();
		int c = 1;
		final Region fregion = region;
		for(RegionExecutor ex : region.getExecutors()){
			List<String> des = MinigameUtils.stringToList(ChatColor.GREEN + "Trigger: " + ChatColor.GRAY + 
					MinigameUtils.capitalize(ex.getTrigger().toString()) + ";" +
					ChatColor.GREEN + "Action: " + ChatColor.GRAY + 
					MinigameUtils.capitalize(ex.getAction().getName()) + ";" + 
					ChatColor.DARK_PURPLE + "(Right click to delete)");
			if(!ex.getArguments().isEmpty())
				des.add(ChatColor.DARK_PURPLE + "(Left click to edit)");
			MenuItemCustom cmi = new MenuItemCustom("Executor ID: " + c, 
					des, Material.ENDER_PEARL);
			final RegionExecutor cex = ex;
			final MenuItem fcmi = cmi;
			final MinigamePlayer fviewer = viewer;
			final Menu fm = m;
			cmi.setRightClick(new InteractionInterface() {
				
				@Override
				public Object interact() {
					fregion.removeExecutor(cex);
					fcmi.getContainer().removeItem(fcmi.getSlot());
					return null;
				}
			});
			cmi.setClick(new InteractionInterface() {
				
				@Override
				public Object interact() {
					if(cex.getAction().displayMenu(fviewer, cex.getArguments(), fm))
						return null;
					return fcmi.getItem();
				}
			});
			items.add(cmi);
			c++;
		}
		if(previousPage != null){
			m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previousPage), m.getSize() - 9);
		}
		m.addItem(new MenuItemExecutor("Add Executor", Material.ITEM_FRAME, region), m.getSize() - 1);
		m.addItems(items);
		m.displayMenu(viewer);
	}

}

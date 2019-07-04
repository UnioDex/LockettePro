package me.crafter.mc.lockettepro;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.Iterator;

public class BlockEnvironmentListener implements Listener{

	// Prevent explosion break block
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityExplode(EntityExplodeEvent event){
		if (Config.isProtectionExempted("explosion")) return;
		Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (LocketteProAPI.isProtected(block)) it.remove();
        }
	}
	
	// Prevent bed break block
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockExplode(BlockExplodeEvent event){
		if (Config.isProtectionExempted("explosion")) return;
		Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (LocketteProAPI.isProtected(block)) it.remove();
        }
	}
	
	// Prevent tree break block
	@EventHandler(priority = EventPriority.LOWEST)
	public void onStructureGrow(StructureGrowEvent event){
		if (Config.isProtectionExempted("growth")) return;
		for (BlockState blockstate : event.getBlocks()){
			if (LocketteProAPI.isProtected(blockstate.getBlock())){
				event.setCancelled(true);
				return;
			}
		}
	}

	// Prevent piston extend break lock
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPistonExtend(BlockPistonExtendEvent event){
		if (Config.isProtectionExempted("piston")) return;
		for (Block block : event.getBlocks()){
			if (LocketteProAPI.isProtected(block)){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	// Prevent piston retract break lock
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPistonRetract(BlockPistonRetractEvent event){
		if (Config.isProtectionExempted("piston")) return;
		for (Block block : event.getBlocks()){
			if (LocketteProAPI.isProtected(block)){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	// Prevent redstone current open doors
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockRedstoneChange(BlockRedstoneEvent event){
		if (Config.isProtectionExempted("redstone")) return;
		if (LocketteProAPI.isProtected(event.getBlock())){
			event.setNewCurrent(event.getOldCurrent());
		}
	}
	
	// Prevent villager open door
	@EventHandler(priority = EventPriority.LOWEST)
	public void onVillagerOpenDoor(EntityInteractEvent event){
		if (Config.isProtectionExempted("villager")) return;
		// Explicitly to villager vs all doors
		if (event.getEntity() instanceof Villager &&
				(LocketteProAPI.isSingleDoorBlock(event.getBlock()) || LocketteProAPI.isDoubleDoorBlock(event.getBlock())) && 
				LocketteProAPI.isProtected(event.getBlock())){
			event.setCancelled(true);
		}
	}
	
	// Prevent Enderman take block
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEndermanGreif(EntityInteractEvent event){
		if (Config.isProtectionExempted("enderman")) return;
		if (event.getEntity() instanceof Enderman && LocketteProAPI.isProtected(event.getBlock())){
			event.setCancelled(true);
		}
	}

}

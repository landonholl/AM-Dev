package com.birdsprime.aggressivemobs.MobBehavs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.birdsprime.aggressivemobs.AggressiveMobsConfig;
import com.birdsprime.aggressivemobs.GetNearestTarget;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.schedule.Activity;

public class MobTargetPlayer {

	private static final Logger LOGGER = LogManager.getLogger();

	public static final String[] Whitelist = GetWhitelist();

	static String[] GetWhitelist() {
		String[] Val_List = AggressiveMobsConfig.SiegeEntityWhitelist.get().toLowerCase().split(",");
		for(int i = 0 ; i < Val_List.length; i++)
		{
			String Curr_Val = Val_List[i];
			Curr_Val = Curr_Val.trim();
			Val_List[i] = Curr_Val;
		}
		
		return Val_List;
	}
	
	// Make given AI attack a random player on the server
	public MobTargetPlayer(Entity Attacker) {

		// If this entity is of a type that can besiege the player, then
		if (isAllowedMobType(Attacker)) {

			//LOGGER.info("ATTACKER " + Attacker.getName().getString() + " IS APPROVED");
			
			// Cast to generic monster type
			Mob Mob_Class = (Mob) Attacker;
		
			// Set move block
			Entity Entity_Target = Mob_Class.getTarget();

			boolean isMonsterInfighting = AggressiveMobsConfig.AllowMonsterInfighting.get();

			if (isMonsterInfighting && Entity_Target != null)
				return;

			// Get server player
			Entity Targeted_Entity = new GetNearestTarget().Get(Attacker);

			if (Targeted_Entity != null) {

				if (WithinRange(Targeted_Entity, Attacker)) {

					// LOGGER.info("SETTING TGT AS " + Targeted_Entity.getName());
					Mob_Class.setTarget((LivingEntity) Targeted_Entity);
				}
			}
			//If mob expired, then, kill
			new MobMaxLife(Attacker);
		}else {
			//LOGGER.info("ATTACKER " + Attacker.getName().getString() + " IS NOT APPROVED");
		}

	}

	// Can this monster track the player?
	boolean isAllowedMobType(Entity Entity_Class) {
		
		boolean WasFound = false;
		for(int i = 0; i < Whitelist.length; i++)
		{
			String Val = Whitelist[i];
			String ChkName = Entity_Class.getName().getString().toLowerCase();
			if(Val.compareTo(ChkName) == 0)
			{
				WasFound = true;
			}
		}

		return WasFound;


	}

	// Is this entity within range of the player?
	// Targeted_Player - The entity targeted by this attacker
	// Entity_Class - The entity targeting this player
	boolean WithinRange(Entity Targeted_Entity, Entity Entity_Class) {

		// Get target distances
		float Target_Dist_AboveGround = AggressiveMobsConfig.MonsterAttackDistanceAboveGround.get();
		float Target_Dist_UnderGround = AggressiveMobsConfig.MonsterAttackDistanceUnderground.get();

		double X_To = Math.abs(Math.abs(Targeted_Entity.getX()) - Math.abs(Entity_Class.getX()));
		double Z_To = Math.abs(Math.abs(Targeted_Entity.getZ()) - Math.abs(Entity_Class.getZ()));

		// Get entity Y Pos
		float Y_Pos = Entity_Class.blockPosition().getY();

		// If (generally) above ground, then
		if (Y_Pos < 50) {
			return Targeted_Entity.distanceTo(Entity_Class) < Target_Dist_UnderGround;
		} else {
			return ((X_To < Target_Dist_AboveGround) && (Z_To < Target_Dist_AboveGround));
		}
	}
}

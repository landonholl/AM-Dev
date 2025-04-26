package com.birdsprime.aggressivemobs.MobBehavs;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

//Sets an entity as "Angry" in that they're at 150% speed, and play health boost effect
public class MobBloodlust {

	EntityType[] WhiteList = { EntityType.HOGLIN, EntityType.HUSK, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER,
			EntityType.ZOMBIFIED_PIGLIN };
    
	// Make the given entity have bloodlust
	public MobBloodlust(Entity Entity_Class) {

		if (isOnWhitelist(Entity_Class.getType())) {
			// Entity as living entity
			LivingEntity Living_Entity = (LivingEntity) Entity_Class;

			// Set health as 150%
			Living_Entity.getAttribute(Attributes.MAX_HEALTH)
					.addPermanentModifier(new AttributeModifier("Bloodlust",
							Living_Entity.getHealth() + (Living_Entity.getHealth() * 0.5F),
							AttributeModifier.Operation.MULTIPLY_BASE));
			Living_Entity.setHealth(Living_Entity.getHealth() + (Living_Entity.getHealth() * 0.5F));

			// Get current movement speed of movement
			Living_Entity.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(
					new AttributeModifier("Bloodlust", 1.03F, AttributeModifier.Operation.MULTIPLY_BASE));
			
			//Add effect
			Living_Entity.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1000, 1));
			
			//LOGGER.info(Entity_Class.UUID_TAG + " ::: MADE ANGRY");
			
			//Set name for blood lusting entity
			//Living_Entity.setCustomName(new MutableComponent("Bloodlusting " + Entity_Class.getName().getString()));
			//Living_Entity.setCustomNameVisible(true);
		}

	}

	// Is the given entity on the whitelist?
	boolean isOnWhitelist(EntityType Current_Entity_Type) {
		for (int i = 0; i < WhiteList.length; i++) {
			EntityType T = WhiteList[i];
			if (T == Current_Entity_Type) {
				// This entity is on the whitelist
				return true;
			}
		}

		// This entity is not on the white list
		return false;
	}

}

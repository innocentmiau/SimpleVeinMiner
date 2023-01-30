# SimpleVeinMiner
A minecraft simple plugin that allows players to instantly mine the entire vein.

## A simple to use Vein Miner plugin, just add on the server and it will work right away with nothing to config with:
- [x] With this plugin, it will automatically mine the entire vein upon breaking one of the blocks.
- [x] Will work with Silk Touch.
- [x] Will work fine with Durability enchants.(It will reduce the durability of the item used just like vanilla mechanics)
- [x] Will ignore if player is in creative.
- [x] Option for permission to use(read below config.yml information).
- [x] Option to check for diagonal ores(read below config.yml information).
- [x] Option to cancel if the player is sneaking(read below config.yml information).
- [x] Option to enable a blacklist of pickaxes that cannot be used towards mining entire veins(read below config.yml information).
- [x] Option to enable a blacklist of worlds where the plugin won't work at all..

## **config.yml** contents:
```
needsPermission: false
checkForDiagonalOres: false
cancelIfSneaking: false
blacklistPickaxes:
  enabled: false
  list:
   - 'wooden_pickaxe'
   - 'stone_pickaxe'
blacklistWorlds:
  enabled: false
  list:
    - 'world_the_end'
```

- [ ] Permission for use if 'needsPermission' on the config is set to **true**: simpleveinminer.use
- [ ] Option 'checkForDiagonalOres' if set to **true** will check the entirety of a vein even when blocks are not directyly touching(in diagonal directions). (This comes by default set to false)
- [ ] Option 'cancelIfSneaking' if set to **true** will allow players to not mine the entire vein when they are sneaking. (useful for mining ores used in builds or when a player doesn't want to break the entire vein with the same pickaxe, or other reasons)
- [ ] Option 'blacklistPickaxes' if enabled you can costumize which pickaxes are blacklisted for choosing which pickaxes are not to allowed to mine entire veins.
- [ ] Option 'blacklistWorlds' if enable you can setup a list of worlds where the plugin won't work on.

## Please let me know if something is missing on it.
I tried to code it as simple and working as possible, below is my discord details for ideas or bug fixes(support):
> My discord: **Miau#0307** (This may change, read below for server reason).

> My discord server: https://discord.gg/Yg3Dbs2uVG - Can be used for just chatting or a way to find me in case my discord discriminator(tag, the #) changes because of discord nitro or just me swapping it.)

> Let me know if there's any bug, since plugin is coded to allow all the versions, this may cause some bug. (I tested it myself on 1.9.4 version and 1.19.3 and it worked fine but I can't test every single scenario myself).

### Good for simple SMPs that require such plugins to not overload server.
*If you need this plugin in a older version, please do message me and I will update it.*

DO NOT copy/download my code and release as your own.

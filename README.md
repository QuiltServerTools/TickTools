# TickTools
A Minecraft mod for managing tick-related things

[![discord](https://img.shields.io/discord/764543203772334100?label=discord)](https://discord.gg/UxHnDWr)
## Config

```toml
splitTickDistance = true
tickDistance = 8
itemDespawnTicks = 6000

[dynamic]
dynamicTickDistance = false
minTickDistance = 4
dynamicRenderDistance = false
minRenderDistance = 6
maxRenderDistance = 10
```

#### Quick overview of each config option

`splitTickDistance` controls whether you want to have a separate tick distance from your render distance

`tickDistance` controls your normal tick distance if `splitTickDistance` is enabled

`itemDespawnTicks` controls the number of ticks an item entity will take to despawn after being dropped. Vanilla is 6000 ticks

`dynamicTickDistance` controls whether the tick distance should fluctuate between `tickDistance` and `minTickDistance` depending on server MSPT

`minTickDistance` controls the minimum value the tick distance will be if `dynamicTickDistance
 is enabled

`dynamicRenderDistance` controls whether the render distance should fluctuate between `minRenderDistance` and `maxRenderDistance` depending on server MSPT

`minRenderDistance` controls the minimum render distance when `dynamicRenderDistance` is enabled

`mixRenderDistance` controls the maximum render distance when `dynamicRenderDistance` is enabled


## Discuss

Support, discussion and development takes place on our discord, found at [https://discord.gg/UxHnDWr](https://discord.gg/UxHnDWr)

You can also sign up for release pings there should you be interested
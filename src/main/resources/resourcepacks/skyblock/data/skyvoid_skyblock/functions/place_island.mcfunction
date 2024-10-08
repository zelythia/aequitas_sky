# places the starter skyblock island
# @s = none
# located at world spawn
# run from skyvoid_worldgen:load via #skyvoid_worldgen:initialize

forceload add ~ ~
place jigsaw skyvoid_skyblock:starter_island skyvoid:starter_island 1 ~ 64 ~
tp @a ~0.5 72 ~0.5
forceload remove ~ ~

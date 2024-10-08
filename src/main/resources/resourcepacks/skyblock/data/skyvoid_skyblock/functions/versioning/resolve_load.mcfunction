execute if score skyvoid_worldgen load.status matches 1 unless score skyvoid_vanilla_oneblock load.status matches 1.. run scoreboard players set skyvoid_skyblock load.status 1
execute unless score skyvoid_skyblock load.status matches 1 run schedule function skyvoid_skyblock:versioning/send_error 2t

# capstone_project
Alright here is the first version with the most barely functional features

Basic flow to how the app works:

from the main menu, press create level 
press select photo
select target image from gallery
press generate level
click once on screen to set player spawn
click again to set goal position
click the up left hand corner to rotate the image to desired functionality
then click upper right hand corner to generate the level
wait for that to process, it will automatically spit you out to the main menu
generated level is stored as "level_output.txt" in files directory of app data
press go to game
navigate green rectangle to the flag, press grey rectangles to go left or right, grey square to jump


Known problems/ things to fix:

program runs out of memory and crashes when trying to generate more than one level per session
need to force landscape mode during gameplay and level generation sketches
currently no way to add enemies to levels
no local database and no way to select levels
occasional error when loading background during gameplay (can be fixed during runtime by rotating the device a few times)
hardcoded for 1280x720 screens, need to add code to allow asset scaling
add multitouch support during gameplay so can run and jump at same time
add art and animation for player character (and enemies if they get implemented)
splash screen and menu styling

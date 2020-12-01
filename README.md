# textAdventureMVVM

## What is it? ##
A currently in progress, randomly generated, pick-up put-down style text based adventure game. This means that all encounters within the app (combat, trap, reward, etc..) are all randomly pulled form a local file, as well as all loot and enemies. It also saves exactly where you left off, saving the encounter and the player character whenever the app is closed. <br>

## How it's made ##

The app makes use of MVVM pattern with databinding and observers, and liveData to supplement the viewModels. <br>
For storing data, it uses JSON files to store all possible encounters, as well as the saved data of the encounter and character. <br>
For the loot and enemies, they are retrieved from a local SQLite database.

## Here's a small preview of navigating the player inventory and moving through the encounters.

![Inventory](https://media.giphy.com/media/LRY38uiMnhxihA8kqe/giphy.gif) ![Game](https://media.giphy.com/media/M4Ezjf0orJZdO80JP6/giphy.gif) ![](https://media.giphy.com/media/djoFdG46XbOjJaoi0u/giphy.gif)

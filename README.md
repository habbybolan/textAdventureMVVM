# textAdventureMVVM

## What is it? ##
A currently in progress, randomly generated, pick-up put-down style text based adventure game. This means that all encounters within the app (combat, trap, reward, etc..) are all randomly pulled from a local file, as well as all loot and enemies. It also saves exactly where you left off, saving the encounter and the player character whenever the app is closed. <br>

## How it's made ##

The app makes use of MVVM architecture with databinding, observableFields, and viewModels. <br>
For storing data, it uses JSON files to store all possible encounters, as well as the saved data of the current encounter and player character. <br>
For the loot and enemies, they are retrieved from a local SQLite database.

## Preview of navigating different parts of the app.

![Inventory](https://media.giphy.com/media/LRY38uiMnhxihA8kqe/giphy.gif) ![Game](https://media.giphy.com/media/M4Ezjf0orJZdO80JP6/giphy.gif) ![](https://media.giphy.com/media/djoFdG46XbOjJaoi0u/giphy.gif)

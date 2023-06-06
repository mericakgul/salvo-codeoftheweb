# Salvo Game Web Application
This is a full-stack web application for the Salvo game, developed using 
JavaScript on the frontend and Java with Spring Boot on the backend. 
The application uses an H2 database.

## Prerequisites
Here's a list of tools you need to install in order to run this application:
* [Java 17](https://www.oracle.com/java/technologies/downloads/)
* An IDE (ex. [Intellij IDEA](https://www.jetbrains.com/idea/download/))
* [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Installation
1. Clone the repository
```
git clone https://github.com/mericakgul/salvo-codeoftheweb.git
```

2. Open the project
Open the cloned project in the IDE that you installed.


3. Start the application
Run the following command in the directory of the project in the terminal. 
```
./gradlew bootRun
```
Or you can use the IntelliJ bootrun configuration on right top corner. 

Or you can open the Gradle tab on the right side of IntelliJ, click on 'Execute Gradle Task' button and click on 'gradle bootrun' option. 

The application will be accessible at `localhost:8080`.

Go to `http://localhost:8080/web/games.html` to play the game.

### Usage / Rules of the Game

1. This game can be played by only two players.
2. You can see the list of the games and the scoreboard on `/web/games.html` page even if you are not logged in.
3. You have to log in / sign up to be able to view, join or create games on `/web/games.html` page. You might see an empty game list and scoreboard if there is no created games yet.
4. You can create your own game by clicking on 'Create New Game' button and wait for you opponent.
5. If there are some created games listed, then you can join one of the games that have only one player by clicking 'Join' button next to the game. This one player cannot be you, you cannot play against yourself.
6. You can also view the games that you are one of the players by clicking on 'View' button. Therefore, you can play more than one game at the same time.
7. When you create, join or view a game, you will be directed to `/web/game.html?gp=` page.
8. If you are the only player in the game, then you have to wait for your opponent to play. You can place and save your ships meanwhile though.
9. Select a ship and a direction on the left menu and place it on the 'Ship Grid' map. The ships placed on the map will be shown in the list of 'Placed Ship' on the left bottom side.
10. You can either save(sending the ship data to database so no change can be done after saving) or remove the ship by using checkboxes and 'Remove' and 'Save' buttons. Removing will remove the checked ships from the map, so you can replace them.
11. After you and your opponent save all the ships, you can start making salvo.
12. The creator player can make the first salvo of each turn. If you joined to a game created by anyone else, then you have to wait for your opponent's salvo.
13. You can select at most 5 locations on the 'Salvo Grid' map at the beginning. You can select as many locations as the number of your ships that are not sunk.
14. You can send the salvo by clicking 'Fire Salvo' button on the left side.
15. If you hit any of your opponent's ships then the hit locations will be shown in purple on the 'Salvo Grid' map. Also, same for your ships on 'Ship Grid' map.
16. You can make salvo only once for each turn. When both players make their salvo, then the next turn can start. Again, the creator can fire first salvo of the next turn.
17. You can see the game history at the bottom of the page after each salvo (yours or opponent's).
18. The game can end with three possible results: Win, Lose and Tie. And the game can end if all the players played same amount of turns.
19. If you sink all of your opponent's ship, and you still have floating ship at the end of a turn, then you win. If other way around, then you lose. If both players' ships are sunk at the end of a turn, then it is tie.
20. The winner gets 1 point, the loser gets 0 point. If it is tie, both players get 0.5 point. The points are added to the scoreboard on page `/web/games.html`.

### Help
Any recommendation is welcome. Please contact me if you have any idea or improvement or suggestion.

### Authors

Contributors names and contact info

Meriç Akgül

meric.ext@gmail.com

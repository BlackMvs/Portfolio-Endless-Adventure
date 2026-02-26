# Endless Adventure
An endless combat game where you fight waves of enemies.

## Specifications 
Tools used: IntelliJ IDEA  
JDK: GraalVM JDK 21 (21.0.7 - VM 23.1.7)  
Java Version: 21  

## Demo 
The game is designed to be a roguelike experience. You begin on a tutorial map where you learn the controls and core mechanics. After completing the tutorial, you enter the main game, where all levels are randomly generated and may repeat.  
The objective is to survive for as long as possible. Enemy numbers are low and weak at first, but with each level, they grow stronger and more numerous. Defeating enemies grants experience, allowing the player to level up and improve their stats and gain new skills (stat upgrades and skills were not implemented in this version).
If you die, you start over. 


### Fight. Kill. Progress. Repeat.
![CoreGameplay](https://github.com/user-attachments/assets/e5b862da-f020-470b-9401-16fa34b5103a)


### Attack types 
Light attack - fast but weak  
Heavy attack - slow but strong 
![AttackTypes](https://github.com/user-attachments/assets/aa14c50b-d4e7-4894-86f9-94f34932caaa)


### Enemy movement 
Enemy will patrol and move left and right till they will see you, then they will start chasing you 
![EnemyMovement](https://github.com/user-attachments/assets/8d8c3203-8c14-441b-8e8b-c52216483caf)


## Instructions
1. Clone the repository
2. Open the project in IntelliJ IDEA
3. Make sure your SDK is set to GraalVM JDK 21
4. In the `main` package, run `GameLauncher`


## Technical Highlights

### Player Character
The player uses a state system (falling, grounded, attacking, etc.) to manage what actions are available at any time. This keeps the logic clean and makes extending the player with new actions straightforward just define the state, set the rules, done.

### Level Design
Levels are randomly selected from a folder. To add a new level, just drop the file in. No code changes needed. Enemies, the player, and the portal all spawn in valid random positions, so no two runs feel the same.

### Enemy System
Built with modularity in mind. To add a new enemy, create a class, set up the animations, and it's ready. Collision, patrol, chase, and attack behaviour all adapt automatically regardless of the enemy's size or type.

### Game States
Each state (menu, gameplay, death screen, etc.) lives in its own class. Adding a new screen is as simple as creating a class and registering it. Keeps the codebase clean and easy to navigate.

### UI
Fully scalable as all UI elements respond dynamically to whatever screen size the player sets at launch.

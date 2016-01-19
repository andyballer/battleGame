#OOGASalad Sub-Team Questions

3/28/14

###Game Engine
1) We are most excited to work on creating a framework for making game characters (called 
"units" in our API).

2) We are most concerned about creating a framework for objectives, and figuring out how to
best organize the different types of objects we have in mind in some sort of inheritance
hierarchy. 

3) As the game engine subteam, our goal is to create characters, objects, and objectives that
can be incorporated into a game in various combinations. That said, our API is designed to be
flexible about creating new characters, objects, and objectives to use in a game; we want to
make it as easy as possible to add characters/objects/objectives with reasonable variability
in several of their features.

4) So far, our API consists of three major packages; engine, objects, and objectives. The
engine package will contain a class of constants for myriad game features, such as display
width/height, label names, fonts, and team IDs. The objects package contains the objects that
can exist, which include movable/immovable objects, collectible objects, and game characters
("units"). The objects and objectives packages will contain inheritance hierarchies that 
allow for us to group certain objects/objectives together.

5) Through the controller, we allow for the game environment to create new objectives and
objects, and we allow the game environment to retrieve information about certain objects and
objectives.

6) Our API keeps different elements of the game in different packages, and we will have minimal
communication between these types of elements.

7) The first thing we plan to implement is the object (or the "unit"), so that we can build
objectives with the attributes of objects in mind.
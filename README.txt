README
========

/*======================================================================
 * 
 * Yarhar : A 2D map editor made in Java
 * 
 * Copyright (c) 2012 by Stephen Lindberg (sllindberg21@students.tntech.edu)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
======================================================================*/

Yarhar is a 2D map editor developed with Java, but designed for creating maps usable in 
any language. This is possible because all maps created with Yarhar are saved as JSON text. 
Yarhar supports multiple layers, tiled sprites, grouped sprite libraries, and layer opacity. 
Yarhar is powered by the Pwnee2D Java game engine.

This project is still undergoing a lot of development, but it is now in a state where it
is possible to make maps and save/load them as json. If you don't mind the current 
incompleteness of this application, then feel free to use it.

I'll eventually get around to expanding this README and creating some example applications. 
Hang in there until then!

All components and examples of Yarhar unless otherwise specified, have been developed and 
tested on a 64-bit computer running Windows 7, but it should also be able to run on any 
computer with at least Java 6 properly installed.



Requirements:
----------------
You'll need the following tools to be able to compile and run Yarhar:
* At least Java 6 JDK and JRE
* Adobe Ant



Building yarhar.jar from the source code:
-------------------
Open a command terminal in the "development" directory and enter the command
"ant all"

This will create the yarhar.jar in the "latest" directory and it will also create the API docs
for Yarhar in the "docs" directory.



Building the examples from source:
---------------------
In the root directory for the each example, just open a terminal and enter
"ant all"

The executable jar file for the example will be created in its "latest" directory.



Frequently Asked Questions:
----------------

Q: What's with the weird name for this project?
A: It's a map editor. Pirates use treasure maps. Since you are using this 
   map editing program, you might be a pirate!
   
   ~Yar! Har! Fiddle dee dee! Being a pirate is alright to be!
   Do what you want 'cause a pirate is free!
   YOU are a PIRATE!~
   
   So yeah, I really just gave the name "Yarhar" to make a lame pirate joke.

Q: Is it done yet? 
A: Yes, but there's a bunch of features I'm still working on for it. Feel free to go ahead and
   make maps with it though!

Q: How do I read my map's json?
A: I should probably write up a json specification for this. I'll let you know when that's done. 
   
   For now, know that a map's json includes its sprite library and its layer list at the top.
   
   The sprite library has a list of sprite types which are used to reference the types of sprites 
   used in your level. All the sprite types for a map are uniquely identified by name.
   
   Each of the layers have their own lists of sprite instances. 
   
   So, to recreate your map for a level inside your game's code, just iterate through the 
   layers and iterate through each layer's sprite instance list to create the actual layers and 
   sprites for your level. 
   







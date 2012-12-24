README
========

/*======================================================================
 * 
 * Yarhar v2.0 : A 2D map editor made in Java
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

Yarhar was been developed and tested on a 64-bit computer running Windows 7, but it should 
also be able to run on any computer with at least Java 6 properly installed.


Version info:
----------------
v2.0 : Map and image data are now stored in one file.

v1.0 : Original completed release. Saved maps as json data. Didn't save image data.


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


Controls:
-------------------
The following are the available mouse controls for using the editor area.

Click and drag from empty space : 	pan the camera.

Mouse wheel : 				zoom the camera in or out from 
					the mouse pointer.

Click a sprite : 			Select a sprite

Shift + click a sprite : 		Add another sprite to the selection.

Drag sprite(s) : 			Moves all the currently selected 
					sprites with the mouse.

Shift + drag : 				Add all sprites in rectangle to 
					the selection.

Ctrl + drag sprite(s) : 		Clones the currently selected sprites and 
					moves the clones with the mouse.

Space + drag : 				Pan the camera, even if you're 
					clicking on a sprite.

r + drag : 				rotate the currently selected sprites.

s + drag :				scale the currently selected sprites.

t + drag : 				tile the currently selected sprites.

Right-click :				Opens up the right click menu.

Several menu options also have keyboard shortcuts available.


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
A: Yes it is! Go make some maps, ye scurvy landlubber, you! :D

Q: How do I read my map's json?
A: See the jsonSpecification.docx/pdf file for documentation on 
   how the maps' json is structured. 

   How these maps are interpretted into actual game levels is very 
   dependent on the game and the language it is written with. So
   it is up to you to implement your own algorithms for interpretting
   the json into levels! It's not hard as long as you have code ready 
   for your sprite objects and layers.
   







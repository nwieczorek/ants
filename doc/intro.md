# Introduction to ants

Ants was inspired by [Langton's Ant](http://en.wikipedia.org/wiki/Langtons_ant). 


## Rule File Reference

### Special Syntax

#### Comments
A single hash will cause the remainder of the line to be treated as a comment.

#### The as Block
A line starting with the world 'as' and followed by a valid clojure identifier can be used as a shorthand to define several similarly named rules.  The identifier following the 'as' will be appended to the identifier used in the rule definitions that are inside the block.  The block is ended with a line starting with the world 'end'.  For example rules are defined:

as ant-rule
0 : (move f f)
1 : (set 1 0)
2 : (turn r l)
end ant-rule

Will define rules named ant-rule-0, ant-rule-1 and ant-rule-2.

#### Colors
Colors are defined as a list of three integers in the range 0-255, for example:
state-color-1: (0 100 255)

### Rule Glossary

title:  The title displayed in the application window titlebar.
timer-milliseconds:  The timer controls the updates of the simulation.
world-width:  The width of the world, in cells.
world-height:  The height of the world, in cells.

cell-width:  The width of each cell, in pixels.
cell-height:  The height of each cell, in pixels.
num-states:  The number of states that each cell of the world may be in.

state-color-n:  Defines the color used to display an empty cell of state n.
ant-color:  Defines the color used to display an ant.
ant-rule-n:  Defines an ant rule.  

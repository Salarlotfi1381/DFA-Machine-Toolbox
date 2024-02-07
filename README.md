# DFA-Machine-Toolbox
Here we have a simple DFA machine toolbox with two operations:
- DFA string detector
- NFA to DFA conversion
### Intro
This project is a part of Automata & Lanuages Theory course in Persian gulf University of Technology

Let's see how these codes work
### How it's work
Each of these codes read machine attributes from a file.

> The file name can be changed in code by your wish

Here is the format of machines in file:
- In first line we have alphabets
- In second line we have states
- In thied line we have starter state
- In fourth line we have final states
- And in the other lines we have transition functions in each line
#### Example of input
```
a b
Q0 Q1 Q2
Q0
Q1
Q0 a Q1
Q0 b Q1
Q1 a Q2
Q1 b Q2
Q2 a Q2
Q2 b Q2
```
> **Important** in conversion part, the result DFA machine will save in a file
> #### Example of input2
> ![enter image description here](https://github.com/Salarlotfi1381/DFA-Machine-Toolbox/blob/main/Photo/Example.jpg)
```
a b
q0 q1 q2 q3 q4 q5 q6
q0
q5
q0 ~ q1
q0 ~ q3
q1 a q1
q1 b q2
q2 ~ q5
q3 a q4
q4 b q4
q4 ~ q5
q6 ~ q0
q6 a q3
q6 b q4

```
> **Important** in conversion part, the result DFA machine will save in a file

**Hope you enjoy**

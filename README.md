# Java-chess

It is an implementation of program that applies custom chess logic rules and comes up with the results.

## Input

The input file (input.txt) consists of N
 (3≤N≤1000), the chess board size, followed on the next line by M
 (2≤M≤N2), the number of pieces on the board. 
 Next M lines follow the next format:

 - PieceType_i
 - Color_i
 - X_i
 - Y_i

### where:

**PieceType_i**

> represents the type of the i'th piece and should be a string from the following set: {"Pawn", "King", "Knight", "Rook", "Queen", "Bishop"}
 
 
**Color_i**

> represents if the i'th piece is colored white or black and should be from the following set {"White", "Black"}
 
 
**X_i**

> represents the horizontal position of the i'th piece on the board and should be a positive integer number in the range [1,N]
 
 
**Y_i**

> represents the Vertical position of the i'th piece on the board and should be a positive integer number in the range [1,N]
 
 
 
## Output

The output file consists of the M lines in the following format 
(unless there is an error) corresponding to the piece entered in the same order given in the input: 

 - P_i
 - K_i
 
 
### where:

**P_i**

> is the number of possible moves the i'th piece can make from the same cell supposing that it's the turn of the player who owns this piece (including captures). 
> Considering that moves are independent, meaning they are in parallel.
 
 
**K_i**

> is the number of captures the i'th piece can potentially do from the same cell. 
> Considering that captures are independent, meaning they are in parallel.


## The following possible exceptions (going by priority):

 1. Invalid board size
 2. Invalid number of pieces
 3. Invalid piece name
 4. Invalid piece color
 5. Invalid piece position
 6. Invalid given Kings
 7. Invalid input

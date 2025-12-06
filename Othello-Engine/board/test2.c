#include <stdio.h>
#include <stdlib.h>
#include "board.h"

int main() {

    Board b = (Board){
        .black = 0,
        .white = 0,
        .turn = BLACK_TURN
    };

    board_display(&b);

    Board b2 = (Board){
        .black = 1,
        .white = 0,
        .turn = BLACK_TURN
    };

    board_display(&b2);

    b2.black = 0;

    int x = 0, y = 0;
    bit_to_square(0, &x, &y);
    printf("x: %d, y: %d\n", x, y);

    bit_to_square(1, &x, &y);
    printf("x: %d, y: %d\n", x, y);

    bit_to_square(2, &x, &y);
    printf("x: %d, y: %d\n", x, y);

    return 0;
}

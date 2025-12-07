#define PY_SSIZE_T_CLEAN
#include <Python.h>
#include "../bot/bot.h"
#include <time.h>


void print_uint64_binary(uint64_t x) {
    for (int i = 63; i >= 0; i--) {
        putchar((x & (1ULL << i)) ? '1' : '0');

        if (i % 8 == 0 && i != 0) {
            putchar(' ');
        }
    }
    putchar('\n');
}


/////////// Board Module ///////////

static void board_destructor(PyObject *capsule) {
    Board* b = PyCapsule_GetPointer(capsule, "Board");
    if (b) free(b);
}

static PyObject* py_board_create(PyObject* self, PyObject* args) {
    Board* b = malloc(sizeof(Board));
    if (!b) return PyErr_NoMemory();

    *b = board_create();

    return PyCapsule_New(b, "Board", board_destructor);
}

static PyObject* py_board_create_custom(PyObject* self, PyObject* args) {
    bitboard black_bb, white_bb;
    int turn;
    if (!PyArg_ParseTuple(args, "KKi", &black_bb, &white_bb, &turn)) {
        return NULL;
    }

    /* Debugging output
    puts("===============================");
    printf("c board create custom:\n");
    print_uint64_binary((uint64_t)black_bb);
    print_uint64_binary((uint64_t)white_bb);
    puts("===============================");
    */
    Board* b = malloc(sizeof(Board));
    if (!b) return PyErr_NoMemory();

    *b = (Board){
        .black = (bitboard)black_bb,
        .white = (bitboard)white_bb,
        .turn = (Turn)turn
    };

    return PyCapsule_New(b, "Board", board_destructor);
}

static PyObject* py_board_display(PyObject* self, PyObject* args) {
    PyObject* capsule;

    if (!PyArg_ParseTuple(args, "O", &capsule)) return NULL;

    Board* b = PyCapsule_GetPointer(capsule, "Board");
    if (!b) return NULL;

    board_display(b);
    Py_RETURN_NONE;
}

static PyObject* py_board_play(PyObject* self, PyObject* args) {
    PyObject* capsule;
    int x, y;

    if (!PyArg_ParseTuple(args, "Oii", &capsule, &x, &y))
        return NULL;

    Board* b = PyCapsule_GetPointer(capsule, "Board");
    if (!b) return NULL;

    bool ok = play_xy(b, x, y);

    if (ok) Py_RETURN_TRUE;
    Py_RETURN_FALSE;
}

static PyObject* py_board_get_turn(PyObject* self, PyObject* args) {
    PyObject* capsule;
    if (!PyArg_ParseTuple(args, "O", &capsule)) return NULL;

    Board* b = PyCapsule_GetPointer(capsule, "Board");
    if (!b) return NULL;

    if (b->turn) Py_RETURN_TRUE;
    Py_RETURN_FALSE;
}

static PyObject* py_board_game_over(PyObject* self, PyObject* args) {
    PyObject* capsule;
    if (!PyArg_ParseTuple(args, "O", &capsule))
        return NULL;

    Board* b = PyCapsule_GetPointer(capsule, "Board");
    if (!b)
        return NULL;

    if (board_game_over(b))
        Py_RETURN_TRUE;
    else
        Py_RETURN_FALSE;
}

static PyObject* py_board_count_discs(PyObject* self, PyObject* args) {
    PyObject* capsule;
    if (!PyArg_ParseTuple(args, "O", &capsule)) return NULL;

    Board* b = PyCapsule_GetPointer(capsule, "Board");
    if (!b) return NULL;

    int black = 0, white = 0;
    count_discs(b, &black, &white);

    return Py_BuildValue("(ii)", black, white);
}



//////////// Bot Module ///////////


static void bot_destructor(PyObject *capsule) {
    Bot* bot = PyCapsule_GetPointer(capsule, "Bot");
    if (bot) free(bot);
}

static PyObject* py_pengwin_create(PyObject* self, PyObject* args) {
    PyObject* board_capsule;
    int side;

    if (!PyArg_ParseTuple(args, "Oi", &board_capsule, &side)) return NULL;

    Board* b = PyCapsule_GetPointer(board_capsule, "Board");
    if (!b) return NULL;

    Bot* bot = malloc(sizeof(Bot));
    if (!bot) return PyErr_NoMemory();

    *bot = Pengwin_create(b, side != 0);
    return PyCapsule_New(bot, "Bot", bot_destructor);
}

static PyObject* py_greedy_create(PyObject* self, PyObject* args) {
    PyObject* board_capsule;
    int side;

    if (!PyArg_ParseTuple(args, "Oi", &board_capsule, &side)) return NULL;

    Board* b = PyCapsule_GetPointer(board_capsule, "Board");
    if (!b) return NULL;

    Bot* bot = malloc(sizeof(Bot));
    if (!bot) return PyErr_NoMemory();

    *bot = Greedy_create(b, side != 0);
    return PyCapsule_New(bot, "Bot", bot_destructor);
}

static PyObject* py_bot_select_move(PyObject* self, PyObject* args) {
    PyObject* bot_capsule;
    int depth;

    if (!PyArg_ParseTuple(args, "Oi", &bot_capsule, &depth)) return NULL;

    Bot* bot = PyCapsule_GetPointer(bot_capsule, "Bot");
    if (!bot) return NULL;

    Move move = Bot_select_move(bot, depth);
    // printf("{\"x\": %d, \"y\": %d, \"score\": %d}\n", move.x, move.y, move.score);
    return Py_BuildValue("(iii)", move.x, move.y, move.score);
}


//////////// Definition ///////////


static PyMethodDef BoardMethods[] = {
    /// Board methods
    {"create", py_board_create, METH_NOARGS, "Create a board"},
    {"create_custom", py_board_create_custom, METH_VARARGS, "Create a custom board"},
    {"display", py_board_display, METH_VARARGS, "Display board"},
    {"play", py_board_play, METH_VARARGS, "Play a move"},
    {"get_turn", py_board_get_turn, METH_VARARGS, "Get current turn"},
    {"game_over", py_board_game_over, METH_VARARGS, "Check if the game is over"},
    {"count_discs", py_board_count_discs, METH_VARARGS, "Count discs on board"},

    /// Bot methods
    {"pengwin_create", py_pengwin_create, METH_VARARGS, "Create Pengwin bot"},
    {"greedy_create", py_greedy_create, METH_VARARGS, "Create Greedy bot"},
    {"bot_select_move", py_bot_select_move, METH_VARARGS, "Select a move for a bot"},

    {NULL, NULL, 0, NULL}
};

static struct PyModuleDef boardmodule = {
    PyModuleDef_HEAD_INIT,
    "board",
    "Reversi board module",
    -1,
    BoardMethods
};

PyMODINIT_FUNC PyInit_board(void) {
    srand((unsigned int)time(NULL));
    return PyModule_Create(&boardmodule);
}
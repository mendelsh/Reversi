#define PY_SSIZE_T_CLEAN
#include <Python.h>
#include "../board/board.h"

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


static PyMethodDef BoardMethods[] = {
    {"create", py_board_create, METH_NOARGS, "Create a board"},
    {"display", py_board_display, METH_VARARGS, "Display board"},
    {"play", py_board_play, METH_VARARGS, "Play a move"},
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
    return PyModule_Create(&boardmodule);
}

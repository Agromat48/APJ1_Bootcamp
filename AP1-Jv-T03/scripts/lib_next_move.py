#!/usr/bin/env python3
"""Читает JSON-ответ сервера из stdin, возвращает следующий валидный запрос.
Аргументы: <mark:1|-1> <strategy:smart|naive>
"""
import json
import sys

LINES = [
    [(0, 0), (0, 1), (0, 2)],
    [(1, 0), (1, 1), (1, 2)],
    [(2, 0), (2, 1), (2, 2)],
    [(0, 0), (1, 0), (2, 0)],
    [(0, 1), (1, 1), (2, 1)],
    [(0, 2), (1, 2), (2, 2)],
    [(0, 0), (1, 1), (2, 2)],
    [(0, 2), (1, 1), (2, 0)],
]


def find_line_move(board, mark):
    for line in LINES:
        vals = [board[r][c] for r, c in line]
        if vals.count(mark) == 2 and vals.count(0) == 1:
            return line[vals.index(0)]
    return None


def next_move(board, mark, strategy):
    if strategy == "smart":
        pos = find_line_move(board, mark)          # 1. выиграть, если можно
        if pos:
            return pos
        pos = find_line_move(board, -mark)          # 2. заблокировать угрозу
        if pos:
            return pos
        if board[1][1] == 0:                        # 3. центр
            return 1, 1
        for r, c in [(0, 0), (0, 2), (2, 0), (2, 2)]:  # 4. угол
            if board[r][c] == 0:
                return r, c
        for r, c in [(0, 1), (1, 0), (1, 2), (2, 1)]:  # 5. край
            if board[r][c] == 0:
                return r, c
        return None
    else:
        for r in range(3):                           # naive: первая пустая
            for c in range(3):
                if board[r][c] == 0:
                    return r, c
        return None


def main():
    mark = int(sys.argv[1])
    strategy = sys.argv[2]
    data = json.load(sys.stdin)
    board = data["gameField"]["matrix"]
    pos = next_move(board, mark, strategy)
    if pos is None:
        print("NONE")
        return
    r, c = pos
    board[r][c] = mark
    print(json.dumps({"uuid": data["uuid"], "gameField": {"matrix": board}}))


if __name__ == "__main__":
    main()
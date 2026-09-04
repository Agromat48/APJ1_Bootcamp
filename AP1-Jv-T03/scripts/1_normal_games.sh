#!/bin/bash
# Игра A: "умная" стратегия игрока -> ожидаем ничью.
# Игра B: "глупая" стратегия (первая пустая клетка) -> ожидаем проигрыш игроку.

set -e
BASE_URL="${BASE_URL:-http://127.0.0.1:8080}"
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"
NEXT_MOVE="$DIR/lib_next_move.py"

run_game() {
    local label="$1" strategy="$2"
    local uuid mark=1 move=0
    uuid=$(python3 -c 'import uuid;print(uuid.uuid4())')

    echo
    print_header "$label"
    echo -e "  UUID:    ${DIM}$uuid${NC}"
    echo -e "  Стратегия: ${BOLD}$strategy${NC}"
    print_separator

    # первый ход — вручную (пустого предыдущего ответа ещё нет)
    if [ "$strategy" = "smart" ]; then
        body=$(jq -n --arg u "$uuid" '{uuid:$u, gameField:{matrix:[[0,0,0],[0,1,0],[0,0,0]]}}')
    else
        body=$(jq -n --arg u "$uuid" '{uuid:$u, gameField:{matrix:[[1,0,0],[0,0,0],[0,0,0]]}}')
    fi

    # показать начальное поле
    init_matrix=$(echo "$body" | jq -c '.gameField.matrix')
    echo -e "\n  ${BOLD}Начальное поле:${NC}"
    print_board "$init_matrix"
    echo

    while true; do
        move=$((move + 1))
        echo -e "  ${BOLD}── Ход #$move ──${NC}"
        echo -e "  ${DIM}запрос: $(echo "$body" | jq -c .)${NC}"

        resp=$(curl -s -w '\n%{http_code}' -X POST "$BASE_URL/game/$uuid" \
            -H "Content-Type: application/json" -d "$body")
        code=$(echo "$resp" | tail -n1)
        json=$(echo "$resp" | sed '$d')

        if [ "$code" != "200" ]; then
            echo -e "  ${RED}HTTP $code — $(echo "$json" | jq -c .)${NC}"
            break
        fi

        echo -e "  ${GREEN}HTTP $code${NC} — ответ сервера:"
        print_board_compact "$(echo "$json" | jq -c '.gameField.matrix')"

        empty=$(echo "$json" | jq '[.gameField.matrix[][] | select(.==0)] | length')
        if [ "$empty" -eq 0 ]; then
            echo
            print_result "draw"
            break
        fi

        # проверка победы
        winner=$(echo "$json" | jq -c '.gameField.matrix' | check_winner)
        if [ "$winner" = "win_x" ]; then
            echo
            print_result "win_x"
            break
        elif [ "$winner" = "win_o" ]; then
            echo
            print_result "win_o"
            break
        fi

        body=$(echo "$json" | python3 "$NEXT_MOVE" "$mark" "$strategy")
        if [ "$body" = "NONE" ]; then
            echo -e "  ${DIM}Нет доступных ходов.${NC}"
            break
        fi

        echo
    done

    print_separator
    echo
}

run_game "Игра A: ожидаем ничью" "smart"
run_game "Игра B: ожидаем проигрыш" "naive"

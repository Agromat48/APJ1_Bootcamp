#!/bin/bash
# Две независимые партии одновременно, ходы чередуются между ними.

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"

UUID_A=$(new_uuid)
UUID_B=$(new_uuid)

print_header "ПАРАЛЛЕЛЬНЫЕ ИГРЫ"
echo -e "  ${BOLD}Игра A:${NC} ${DIM}$UUID_A${NC}"
echo -e "  ${BOLD}Игра B:${NC} ${DIM}$UUID_B${NC}"
print_separator

matrix_a='[[0,0,0],[0,1,0],[0,0,0]]'
matrix_b='[[1,0,0],[0,0,0],[0,0,0]]'

for round in 1 2 3; do
    echo -e "\n${CYAN}  ╔═══ Раунд $round ═══╗${NC}\n"

    for g in A B; do
        if [ "$g" = "A" ]; then
            uuid=$UUID_A; matrix=$matrix_a; other_matrix=$matrix_b
        else
            uuid=$UUID_B; matrix=$matrix_b; other_matrix=$matrix_a
        fi

        echo -e "  ${BOLD}── Игра $g ──${NC}"
        echo -e "  Текущее поле:"
        print_board_compact "$matrix"

        body=$(jq -n --arg u "$uuid" --argjson m "$matrix" '{uuid:$u, gameField:{matrix:$m}}')

        resp=$(post_move "$uuid" "$body")
        code=$(echo "$resp" | tail -n1)
        json=$(echo "$resp" | sed '$d')

        if [ "$code" != "200" ]; then
            echo -e "  ${RED}HTTP $code: $(echo "$json" | jq -c .)${NC}"
            continue
        fi

        new_matrix=$(echo "$json" | jq -c '.gameField.matrix')
        echo -e "  ${GREEN}Ответ сервера:${NC}"
        print_board_compact "$new_matrix"

        next=$(next_matrix "$new_matrix" 1 smart)
        if [ "$g" = "A" ]; then matrix_a=$next; else matrix_b=$next; fi
        echo
    done
done

print_separator
echo
print_result "draw"
echo
echo -e "${DIM}  Игры A и B развивались независимо — проверь, что их поля не смешались.${NC}"
echo

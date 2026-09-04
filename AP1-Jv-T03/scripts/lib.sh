#!/bin/bash
# Общие функции: HTTP-запрос, отрисовка поля, вычисление следующего хода.

BASE_URL="${BASE_URL:-http://127.0.0.1:8080}"

# ── Цвета ──────────────────────────────────────────────
RED='\033[1;31m'
BLUE='\033[1;34m'
CYAN='\033[1;36m'
GREEN='\033[1;32m'
YELLOW='\033[1;33m'
DIM='\033[2m'
BOLD='\033[1m'
NC='\033[0m'

new_uuid() {
    cat /proc/sys/kernel/random/uuid
}

# ── Разделители / заголовки ────────────────────────────
print_separator() {
    echo -e "${DIM}───────────────────────────────────────${NC}"
}

print_header() {
    local title="$1"
    local len=${#title}
    local pad=$(( (37 - len) / 2 ))
    local line=""
    for (( i=0; i<pad; i++ )); do line+="═"; done
    echo -e "${CYAN}╔${line}╗${NC}"
    printf "${CYAN}║${NC}%*s${BOLD}%s${NC}%*s${CYAN}║${NC}\n" "$pad" "" "$title" "$((37 - len - pad))" ""
    echo -e "${CYAN}╚${line}╝${NC}"
}

print_result() {
    local result="$1"
    case "$result" in
        draw)   echo -e "${YELLOW}  ╔═══════════════════════════╗${NC}"
                echo -e "${YELLOW}  ║        Н И Ч Ь Я         ║${NC}"
                echo -e "${YELLOW}  ╚═══════════════════════════╝${NC}" ;;
        win_x)  echo -e "${RED}  ╔═══════════════════════════╗${NC}"
                echo -e "${RED}  ║   ПОБЕДА КРЕСТИКОВ (X)   ║${NC}"
                echo -e "${RED}  ╚═══════════════════════════╝${NC}" ;;
        win_o)  echo -e "${BLUE}  ╔═══════════════════════════╗${NC}"
                echo -e "${BLUE}  ║   ПОБЕДА НОЛИКОВ  (O)    ║${NC}"
                echo -e "${BLUE}  ╚═══════════════════════════╝${NC}" ;;
    esac
}

# ── Красивое поле 3×3 ──────────────────────────────────
# Аргумент: JSON-массив 3×3. Пустые клетки (0) показывают
# номер хода (1-9), занятые — X (красный) или O (синий).
print_board() {
    local matrix="$1"
    echo "$matrix" | jq -r '
        def sym(i):
            if . == 1 then "\u001b[1;31mX\u001b[0m"
            elif . == -1 then "\u001b[1;34mO\u001b[0m"
            else "\(i)"
            end;
        "      1     2     3",
        "   \u250c\u2500\u2500\u2500\u2500\u2500\u252c\u2500\u2500\u2500\u2500\u2500\u252c\u2500\u2500\u2500\u2500\u2500\u2510",
        " 1 \u2502 \(.[0][0] | sym(1)) \u2502 \(.[0][1] | sym(2)) \u2502 \(.[0][2] | sym(3)) \u2502",
        "   \u251c\u2500\u2500\u2500\u2500\u2500\u253c\u2500\u2500\u2500\u2500\u2500\u253c\u2500\u2500\u2500\u2500\u2500\u2524",
        " 2 \u2502 \(.[1][0] | sym(4)) \u2502 \(.[1][1] | sym(5)) \u2502 \(.[1][2] | sym(6)) \u2502",
        "   \u251c\u2500\u2500\u2500\u2500\u2500\u253c\u2500\u2500\u2500\u2500\u2500\u253c\u2500\u2500\u2500\u2500\u2500\u2524",
        " 3 \u2502 \(.[2][0] | sym(7)) \u2502 \(.[2][1] | sym(8)) \u2502 \(.[2][2] | sym(9)) \u2502",
        "   \u2514\u2500\u2500\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2500\u2500\u2518"
    '
}

# ── Простая отрисовка (без номеров, для compact-вывода) ─
print_board_compact() {
    local matrix="$1"
    echo "$matrix" | jq -r '
        def sym:
            if . == 1 then "\u001b[1;31mX\u001b[0m"
            elif . == -1 then "\u001b[1;34mO\u001b[0m"
            else "·"
            end;
        "  \u250c\u2500\u2500\u2500\u252c\u2500\u2500\u2500\u252c\u2500\u2500\u2500\u2510",
        "  \u2502 " + (.[0] | map(sym) | join(" \u2502 ")) + " \u2502",
        "  \u251c\u2500\u2500\u2500\u253c\u2500\u2500\u2500\u253c\u2500\u2500\u2500\u2524",
        "  \u2502 " + (.[1] | map(sym) | join(" \u2502 ")) + " \u2502",
        "  \u251c\u2500\u2500\u2500\u253c\u2500\u2500\u2500\u253c\u2500\u2500\u2500\u2524",
        "  \u2502 " + (.[2] | map(sym) | join(" \u2502 ")) + " \u2502",
        "  \u2514\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2518"
    '
}

post_move() {
    local uuid="$1" body="$2"
    curl -s -w '\n%{http_code}' -X POST "$BASE_URL/game/$uuid" \
        -H "Content-Type: application/json" -d "$body"
}

# $1=matrix(json) $2=mark(1|-1) $3=strategy(smart|naive)
next_matrix() {
    local matrix="$1" mark="$2" strategy="$3"
    echo "$matrix" | jq -c --argjson mark "$mark" --arg strategy "$strategy" '
        def lines: [
            [[0,0],[0,1],[0,2]], [[1,0],[1,1],[1,2]], [[2,0],[2,1],[2,2]],
            [[0,0],[1,0],[2,0]], [[0,1],[1,1],[2,1]], [[0,2],[1,2],[2,2]],
            [[0,0],[1,1],[2,2]], [[0,2],[1,1],[2,0]]
        ];
        def find_win($m; $mrk):
            [ lines[] as $line
              | ($line | map($m[.[0]][.[1]])) as $vals
              | select(($vals | map(select(.==$mrk)) | length) == 2)
              | select(($vals | map(select(.==0)) | length) == 1)
              | $line[($vals | index(0))]
            ] | first;
        def find_center($m):
            if $m[1][1] == 0 then [1,1] else null end;
        def find_corner($m):
            [[0,0],[0,2],[2,0],[2,2]] | map(select($m[.[0]][.[1]]==0)) | first;
        def find_side($m):
            [[0,1],[1,0],[1,2],[2,1]] | map(select($m[.[0]][.[1]]==0)) | first;
        def naive_move($m):
            [ range(0;3) as $i | range(0;3) as $j
              | select($m[$i][$j]==0) | [$i,$j] ] | first;

        . as $m
        | (if $strategy == "smart" then
             (find_win($m;$mark) // find_win($m;-$mark) // find_center($m)
              // find_corner($m) // find_side($m))
           else
             naive_move($m)
           end) as $pos
        | if $pos == null then "NONE"
          else ($m | .[$pos[0]][$pos[1]] = $mark)
          end
    '
}

# ── Проверка победителя ────────────────────────────────
# Возвращает: "win_x", "win_o", "draw", ""
check_winner() {
    local matrix="$1"
    echo "$matrix" | jq -r '
        def lines: [
            [[0,0],[0,1],[0,2]], [[1,0],[1,1],[1,2]], [[2,0],[2,1],[2,2]],
            [[0,0],[1,0],[2,0]], [[0,1],[1,1],[2,1]], [[0,2],[1,2],[2,2]],
            [[0,0],[1,1],[2,2]], [[0,2],[1,1],[2,0]]
        ];
        (lines | map(map(. as $p | .[$p[0]][$p[1]]))) as $all
        | if ($all | any(. == [1,1,1])) then "win_x"
          elif ($all | any(. == [-1,-1,-1])) then "win_o"
          elif ([.[][] | select(.==0)] | length) == 0 then "draw"
          else ""
          end
    '
}

# Позиция клетки по номеру 1-9 → [row,col]
cell_pos() {
    local n=$1
    local row=$(( (n - 1) / 3 ))
    local col=$(( (n - 1) % 3 ))
    echo "[$row,$col]"
}

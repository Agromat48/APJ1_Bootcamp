#!/bin/bash
# 6 тестов на обработку ошибок — цветной PASS/FAIL.

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/lib.sh"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m'
PASS=0
FAIL=0
TOTAL=6

check() {
    local num="$1" name="$2" expected="$3" code="$4" body="$5"
    if [ "$code" = "$expected" ]; then
        echo -e "  ${GREEN}✓ PASS${NC}  [${num}] ${name}"
        echo -e "        HTTP ${GREEN}$code${NC} — $body"
        PASS=$((PASS + 1))
    else
        echo -e "  ${RED}✗ FAIL${NC}  [${num}] ${name}"
        echo -e "        ожидали ${YELLOW}$expected${NC}, получили ${RED}$code${NC} — $body"
        FAIL=$((FAIL + 1))
    fi
}

print_header "ТЕСТЫ ОШИБОК"
echo

# ── Подготовка ─────────────────────────────────────────
echo -e "  ${BOLD}Подготовка:${NC}"
UUID=$(new_uuid)
FIRST_BODY=$(jq -n --arg u "$UUID" '{uuid:$u, gameField:{matrix:[[0,0,0],[0,1,0],[0,0,0]]}}')
resp=$(post_move "$UUID" "$FIRST_BODY")
code=$(echo "$resp" | tail -n1)
json=$(echo "$resp" | sed '$d')
matrix=$(echo "$json" | jq -c '.gameField.matrix')
echo -e "  Первый ход выполнен, HTTP $code"
print_board "$matrix"
print_separator
echo

# ── 3.1 Изменение предыдущего хода ─────────────────────
flipped=$(echo "$matrix" | jq -c '
    [range(0;3) as $i | range(0;3) as $j | select(.[$i][$j]!=0) | [$i,$j]][0] as $p
    | .[$p[0]][$p[1]] = -(.[$p[0]][$p[1]])
')
body=$(jq -n --arg u "$UUID" --argjson m "$flipped" '{uuid:$u, gameField:{matrix:$m}}')
resp=$(post_move "$UUID" "$body")
code=$(echo "$resp" | tail -n1); body_txt=$(echo "$resp" | sed '$d')
check "3.1" "Изменение предыдущего хода" 400 "$code" "$body_txt"

# ── 3.2 Два новых хода за раз ──────────────────────────
two_new=$(echo "$matrix" | jq -c '
    [range(0;3) as $i | range(0;3) as $j | select(.[$i][$j]==0) | [$i,$j]] as $empty
    | .[$empty[0][0]][$empty[0][1]] = 1
    | .[$empty[1][0]][$empty[1][1]] = -1
')
body=$(jq -n --arg u "$UUID" --argjson m "$two_new" '{uuid:$u, gameField:{matrix:$m}}')
resp=$(post_move "$UUID" "$body")
code=$(echo "$resp" | tail -n1); body_txt=$(echo "$resp" | sed '$d')
check "3.2" "Два новых хода за раз" 400 "$code" "$body_txt"

# ── 3.3 Ход после конца игры ───────────────────────────
UUID2=$(new_uuid)
m2='[[1,0,0],[0,0,0],[0,0,0]]'
last_json=""
for i in 1 2 3 4 5 6 7 8 9; do
    body=$(jq -n --arg u "$UUID2" --argjson m "$m2" '{uuid:$u, gameField:{matrix:$m}}')
    resp=$(post_move "$UUID2" "$body")
    code=$(echo "$resp" | tail -n1); last_json=$(echo "$resp" | sed '$d')
    [ "$code" != "200" ] && break
    m2=$(echo "$last_json" | jq -c '.gameField.matrix')
    empty=$(echo "$m2" | jq '[.[][] | select(.==0)] | length')
    [ "$empty" -eq 0 ] && break
    m2=$(next_matrix "$m2" 1 naive)
    [ "$m2" = "NONE" ] && break
done
body=$(jq -n --arg u "$UUID2" --argjson m "$m2" '{uuid:$u, gameField:{matrix:$m}}')
resp=$(post_move "$UUID2" "$body")
code=$(echo "$resp" | tail -n1); body_txt=$(echo "$resp" | sed '$d')
check "3.3" "Ход после конца игры" 400 "$code" "$body_txt"

# ── 3.4 Повторная отправка старого состояния ────────────
resp=$(post_move "$UUID" "$FIRST_BODY")
code=$(echo "$resp" | tail -n1); body_txt=$(echo "$resp" | sed '$d')
check "3.4" "Повторная отправка старого состояния" 400 "$code" "$body_txt"

# ── 3.5 Невалидный JSON ───────────────────────────────
resp=$(post_move "33333333-3333-3333-3333-333333333333" 'это не json {{{')
code=$(echo "$resp" | tail -n1); body_txt=$(echo "$resp" | sed '$d')
check "3.5" "Невалидный JSON" 400 "$code" "$body_txt"

# ── 3.6 Заполненное поле как первый ход ────────────────
UUID3=$(new_uuid)
body=$(jq -n --arg u "$UUID3" '{uuid:$u, gameField:{matrix:[[1,-1,1],[1,-1,-1],[-1,1,1]]}}')
resp=$(post_move "$UUID3" "$body")
code=$(echo "$resp" | tail -n1); body_txt=$(echo "$resp" | sed '$d')
check "3.6" "Заполненное поле как первый ход" 400 "$code" "$body_txt"

# ── Итоги ──────────────────────────────────────────────
echo
print_separator
echo -e "  ${BOLD}Итог:${NC} ${GREEN}PASS=$PASS${NC}  ${RED}FAIL=$FAIL${NC}  всего=$TOTAL"
print_separator
echo

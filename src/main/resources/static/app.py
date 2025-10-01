import os
import requests
import streamlit as st

# ===== 설정 =====
BASE_URL = os.getenv("BACKEND_BASE_URL", "http://localhost:9080")  # 스프링 부트 게이트웨이/포트에 맞춰 변경

# 세션 유지용 (쿠키 저장)
if "session" not in st.session_state:
    st.session_state.session = requests.Session()
if "logged_in" not in st.session_state:
    st.session_state.logged_in = False
if "player_id" not in st.session_state:
    st.session_state.player_id = ""

s = st.session_state.session  # 짧게 별칭

# ===== API 래퍼 =====
def api_get(path, **kwargs):
    url = f"{BASE_URL}{path}"
    r = s.get(url, **kwargs)
    r.raise_for_status()
    return r.json()

def api_post(path, json=None, **kwargs):
    url = f"{BASE_URL}{path}"
    r = s.post(url, json=json, **kwargs)
    r.raise_for_status()
    return r.json()

def api_delete(path, json=None, **kwargs):
    url = f"{BASE_URL}{path}"
    r = s.delete(url, json=json, **kwargs)
    r.raise_for_status()
    return r.json()

# 공통: 우리 백엔드 응답 포맷 Response{ result, code, message, body }
def body_of(resp):
    if isinstance(resp, dict):
        return resp.get("body")
    return None

# ---- 성공/실패 판정 & 메시지 헬퍼 ----
def is_success(resp: dict) -> bool:
    return isinstance(resp, dict) and resp.get("result") == 0 and resp.get("code") == 0

def get_message(resp: dict) -> str:
    if not isinstance(resp, dict):
        return "요청 실패"
    msg = resp.get("message")
    code = resp.get("code")
    return f"{msg or '실패'} (code={code})"

def require_nonempty(*vals):
    return all(v is not None and str(v).strip() != "" for v in vals)

# ---- 사용자 친화 에러 메시지 매핑 ----
ERROR_TEXT = {
    9008: "해당 ID로 가입된 계정을 찾을 수 없습니다. 회원가입 후 다시 시도해 주세요.",
    9009: "비밀번호가 일치하지 않습니다. 다시 입력해 주세요.",  # 실제 코드 값에 맞게 조정하세요
}
def pretty_fail(resp: dict) -> str:
    if not isinstance(resp, dict):
        return "로그인에 실패했습니다. 잠시 후 다시 시도해 주세요."
    code = resp.get("code")
    if code in ERROR_TEXT:
        return ERROR_TEXT[code]
    # code 없이 message만 오는 경우도 대비
    msg = str(resp.get("message", "")).upper()
    if "NOT_AUTHENTICATED" in msg:
        return "비밀번호가 일치하지 않습니다. 다시 입력해 주세요."
    if "DATA_NOT_FOUND" in msg:
        return "해당 ID로 가입된 계정을 찾을 수 없습니다. 회원가입 후 다시 시도해 주세요."
    return "로그인에 실패했습니다. 입력 정보를 다시 확인해 주세요."

# ===== Streamlit 시작 =====
st.set_page_config(page_title="Skala Stock Demo", layout="wide")
st.title("📈 Skala Stock")

# -------------------- 사이드바 --------------------
with st.sidebar:
    st.subheader("계정 관리")

    # 로그인 여부에 따라 다른 UI 표시
    if st.session_state.logged_in:
        # 로그인된 상태
        st.markdown(f"### {st.session_state.player_id}님 환영합니다!")
        if st.button("로그아웃", use_container_width=True):
            s.cookies.clear()
            st.session_state.logged_in = False
            st.session_state.player_id = ""
            st.success("로그아웃되었습니다.")
            st.rerun()  # 즉시 화면 갱신
    else:
        # 로그인 폼
        with st.form("login_form", clear_on_submit=False):
            pid = st.text_input("Player ID", value=st.session_state.player_id, key="login_pid")
            pw  = st.text_input("Password", type="password", key="login_pw")
            do_login = st.form_submit_button("로그인", use_container_width=True)

        if do_login:
            if not pid.strip() or not pw.strip():
                st.warning("ID와 비밀번호를 입력해 주세요.")
            else:
                try:
                    resp = api_post("/api/players/login",
                                    json={"playerId": pid, "playerPassword": pw})
                    if is_success(resp):
                        st.session_state.logged_in = True
                        st.session_state.player_id = pid
                        st.success(f"{pid}님 환영합니다!")
                        st.rerun()  # 로그인 후 즉시 UI 갱신
                    else:
                        st.session_state.logged_in = False
                        st.error(pretty_fail(resp))
                except requests.HTTPError:
                    st.session_state.logged_in = False
                    st.error("서버와 통신 중 문제가 발생했어요. 잠시 후 다시 시도해 주세요.")

    st.divider()

    # 회원가입은 로그인 여부와 관계없이 노출
    st.subheader("📝 회원가입")
    with st.form("signup_form"):
        new_id = st.text_input("새로운 Player ID", key="signup_pid")
        new_pw = st.text_input("비밀번호", type="password", key="signup_pw")
        new_money = st.number_input("초기 자산", min_value=0, value=100000, step=1000, key="signup_money")
        do_signup = st.form_submit_button("회원가입", use_container_width=True)

    if do_signup:
        if not new_id.strip() or not new_pw.strip():
            st.warning("ID와 비밀번호를 입력해 주세요.")
        else:
            try:
                resp = api_post("/api/players", json={
                    "playerId": new_id,
                    "playerPassword": new_pw,
                    "playerMoney": new_money
                })
                if is_success(resp):
                    st.success("가입이 완료되었습니다. 이제 로그인해 주세요.")
                else:
                    st.error("회원가입에 실패했습니다. 입력 정보를 다시 확인해 주세요.")
            except Exception:
                st.error("회원가입 중 문제가 발생했어요. 잠시 후 다시 시도해 주세요.")

# -------------------- 메인 탭 --------------------
tab_rank, tab_players, tab_stocks, tab_watch = st.tabs(
    ["🏆 Ranking","🧑 Players","🏭 Stocks","⭐ Watchlist"]
)

# -------------------- Ranking --------------------
with tab_rank:
    st.subheader("랭킹 (/api/ranking)")
    try:
        resp = api_get("/api/ranking")
        data = body_of(resp) or []
        st.dataframe(data, use_container_width=True)
    except Exception as e:
        st.info("랭킹 API가 없다면 이 탭은 건너뛰어도 됩니다.")
        st.write(e)

# -------------------- Players --------------------
with tab_players:
    st.subheader("플레이어 목록")
    if st.button("불러오기", key="btn_players_load"):
        try:
            resp = api_get("/api/players/list")
            players = (body_of(resp) or {}).get("list", [])
            st.session_state["players"] = players
            st.success(f"{len(players)}명 로드")
        except Exception as e:
            st.error(e)

    players = st.session_state.get("players", [])
    # 비밀번호 필드 가리기(보호)
    sanitized = []
    for p in players:
        if isinstance(p, dict):
            q = dict(p)
            q.pop("playerPassword", None)
            q.pop("password", None)
            sanitized.append(q)
        else:
            sanitized.append(p)
    st.dataframe(sanitized, use_container_width=True)

# -------------------- Stocks --------------------
with tab_stocks:
    st.subheader("주식 목록")
    if st.button("불러오기", key="btn_stocks_load"):
        try:
            resp = api_get("/api/stocks/list")
            stocks = (body_of(resp) or {}).get("list", [])
            st.session_state["stocks"] = stocks
            st.success(f"{len(stocks)}개 로드")
        except Exception as e:
            st.error(e)

    stocks = st.session_state.get("stocks", [])
    st.dataframe(stocks, use_container_width=True)

    # ---- 카드 스타일 (선택사항) ----
    st.markdown("""
    <style>
      .card {padding: 1rem; border: 1px solid #333; border-radius: 12px; background: #1b1e23;}
      .tight {margin-bottom: .5rem;}
    </style>
    """, unsafe_allow_html=True)

    if st.session_state.logged_in and stocks:
        st.markdown("### 거래 / 관심종목")

        left, right = st.columns([2, 1], gap="large")

        # ============ 거래 카드 ============
        with left:
            st.markdown('<div class="card">', unsafe_allow_html=True)
            st.markdown("#### 💹 거래")

            with st.form("trade_form", clear_on_submit=False):
                c1, c2 = st.columns([3, 1])
                stock_id = c1.selectbox("종목", [s["id"] for s in stocks], key="trade_stock")
                qty = c2.number_input("수량", min_value=1, value=1, step=1, key="trade_qty")

                b1, b2 = st.columns(2)
                buy_clicked = b1.form_submit_button("매수", use_container_width=True)
                sell_clicked = b2.form_submit_button("매도", use_container_width=True)

                if buy_clicked:
                    try:
                        resp = api_post("/api/players/buy", json={"stockId": stock_id, "quantity": qty})
                        st.success("매수 완료" if is_success(resp) else get_message(resp))
                    except Exception as e:
                        st.error(e)
                if sell_clicked:
                    try:
                        resp = api_post("/api/players/sell", json={"stockId": stock_id, "quantity": qty})
                        st.success("매도 완료" if is_success(resp) else get_message(resp))
                    except Exception as e:
                        st.error(e)

            st.markdown('</div>', unsafe_allow_html=True)

        # ============ 관심종목 카드 ============
        with right:
            st.markdown('<div class="card">', unsafe_allow_html=True)
            st.markdown("#### ⭐ 관심종목")

            with st.form("watch_form", clear_on_submit=False):
                w_stock = st.selectbox("종목", [s["id"] for s in stocks], key="watch_stock")
                add_clicked = st.form_submit_button("관심종목 추가", use_container_width=True)

                if add_clicked:
                    try:
                        resp = api_post("/api/watchlist/add", json={"stockId": w_stock})
                        st.success("추가되었습니다." if is_success(resp) else get_message(resp))
                    except Exception as e:
                        st.error(e)

            st.markdown('</div>', unsafe_allow_html=True)
    else:
        st.info("로그인 후 거래/관심종목 기능 사용 가능")

# -------------------- Watchlist --------------------
with tab_watch:
    st.subheader("관심종목")
    if st.button("관심종목 불러오기", key="btn_watch_load"):
        try:
            resp = api_get("/api/watchlist")
            items = body_of(resp) or []
            st.session_state["watchlist"] = items
            st.success(f"{len(items)}개 로드")
        except Exception as e:
            st.error(e)

    items = st.session_state.get("watchlist", [])
    st.dataframe(items, use_container_width=True)

    if st.session_state.logged_in and items:
        target_id = st.selectbox(
            "삭제할 Stock ID",
            [i["stock"]["id"] if isinstance(i.get("stock"), dict) else i.get("stockId") for i in items],
            key="watch_remove_select"
        )
        if st.button("관심종목 삭제", key="btn_watch_remove"):
            try:
                resp = api_delete("/api/watchlist/remove", json={"stockId": target_id})
                if is_success(resp):
                    st.success("삭제 완료")
                else:
                    st.error(get_message(resp))
            except Exception as e:
                st.error(e)
    elif not st.session_state.logged_in:
        st.info("로그인 후 확인 가능합니다.")

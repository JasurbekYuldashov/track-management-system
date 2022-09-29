export const changeRole = (role) => {
    return (dispatch) => dispatch({ type: "CHANGE_ROLE", userRole: role });
};

export const log_in = (username, password) => {
    let data = `{ "username": "${username}", "password": "${password}" }`;
    return (dispatch) => {
        dispatch({ type: "FULL_SCREEN_ANIMATION_START" });
        fetch(process.env.REACT_APP_BASE_URL + "/login", {
            headers: {
                "Content-Type": "application/json",
            },
            body: data,
            method: "post",
        }).then((response) => {
            dispatch({ type: "FULL_SCREEN_ANIMATION_END" });
            let token = response.headers.get("authorization");
            let role =
                response.headers.get("authority") === "ROLE_ADMIN"
                    ? "admin"
                    : response.headers.get("authority") === "ROLE_ACCOUNTANT"
                    ? "accountant"
                    : "dispatcher";
            if (!response.ok) {
                dispatch({ type: "USER_LOG_IN_FAILURE" });
                dispatch({ type: "ERROR_NULL" });
            } else {
                dispatch({
                    type: "USER_LOG_IN_SUCCESS",
                    username,
                    token,
                    role,
                });
            }
        });
    };
};

export const cookiesAuth = (token, role, username) => {
    return (dispatch) => {
        if (token && role && username) {
            dispatch({
                type: "LOG_IN_BY_COOKIES_SUCCESS",
                token,
                role,
                username,
            });
        } else {
            dispatch({ type: "LOG_IN_BY_COOKIES_FAILED" });
        }
    };
};

export const logout = () => {
    return (dispatch) => {
        dispatch({ type: "LOG_OUT" });
    };
};

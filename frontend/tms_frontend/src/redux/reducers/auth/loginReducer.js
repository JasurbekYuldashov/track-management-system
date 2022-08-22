export const login = (
  state = {
    userRole: "admin",
    is_logged_in: true,
    username: null,
    token: null,
    error: null,
    cookiesAuthFinished: false,
    fullScreenAnimation: false
  },
  action
) => {
  switch (action.type) {
    case "LOG_IN_BY_COOKIES_SUCCESS":
      return {
        ...state,
        is_logged_in: true,
        username: action.username,
        token: action.token,
        userRole: action.role,
        cookiesAuthFinished: true,
      };
    case "LOG_IN_BY_COOKIES_FAILED":
      return {
        ...state,
        is_logged_in: false,
        username: null,
        token: null,
        userRole: null,
        cookiesAuthFinished: true,
      };
    case "USER_LOG_IN_SUCCESS":
      return {
        ...state,
        is_logged_in: true,
        username: action.username,
        token: action.token,
        userRole: action.role,
      };
    case "FULL_SCREEN_ANIMATION_START":
      return {
        ...state,
        fullScreenAnimation: true
      };
    case "FULL_SCREEN_ANIMATION_END":
      return {
        ...state,
        fullScreenAnimation: false
      };
    case "USER_LOG_IN_FAILURE":
      return {
        ...state,
        is_logged_in: false,
        error: "Authorisation Error",
      };
    case "ERROR_NULL":
      return {
        ...state,
        error: null,
      };
    case "CHANGE_ROLE": {
      return { ...state, userRole: action.userRole };
    }
    default: {
      return state;
    }
  }
};

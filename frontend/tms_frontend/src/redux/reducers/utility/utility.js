export const data = (
  state = {
    dashboardTab: 0,
    dashboardTeam: 0,
  },
  action
) => {
  switch (action.type) {
    case "CHANGE_DASHBOARD_STATE":
      return {
        ...state,
        dashboardTab: action.dashboardTab,
        dashboardTeam: action.dashboardTeam,
      };
    default: {
      return state;
    }
  }
};

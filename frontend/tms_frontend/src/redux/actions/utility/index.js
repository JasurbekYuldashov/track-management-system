export const changeDashboardState = (tab, team) => {
  return (dispatch) =>
    dispatch({
      type: "CHANGE_DASHBOARD_STATE",
      dashboardTab: tab,
      dashboardTeam: team,
    });
};

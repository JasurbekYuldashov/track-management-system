import React, { Suspense, lazy } from "react";
import { Router, Switch, Route, Redirect } from "react-router-dom";
import { history } from "./history";
import { connect } from "react-redux";
import { ContextLayout } from "./utility/context/Layout";
import Cookies from "js-cookie";
import { cookiesAuth } from "./redux/actions/auth/loginActions";
import { Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
// Route-based code splitting
const Trips = lazy(() => import("./views/pages/Trips/List"));
const EditTrip = lazy(() => import("./views/pages/Trips/Edit"));
const ViewTrip = lazy(() => import("./views/pages/Trips/View"));
const Dashboard = lazy(() => import("./views/pages/Dashboard/List"));
const Dispatchers = lazy(() => import("./views/pages/Dispatchers/List"));
const DispatcherNew = lazy(() => import("./views/pages/Dispatchers/New"));
const DispatcherEdit = lazy(() => import("./views/pages/Dispatchers/Edit"));
const NewTrip = lazy(() => import("./views/pages/Trips/New"));
const DriverNew = lazy(() => import("./views/pages/Drivers/New"));
const DriverEdit = lazy(() => import("./views/pages/Drivers/Edit"));
const DriverView = lazy(() => import("./views/pages/Drivers/View"));
const DriverList = lazy(() => import("./views/pages/Drivers/List"));
const UnitNew = lazy(() => import("./views/pages/Units/New"));
const UnitEdit = lazy(() => import("./views/pages/Units/Edit"));
const UnitList = lazy(() => import("./views/pages/Units/List"));
const FleetList = lazy(() => import("./views/pages/Fleet/List"));
const Reports = lazy(() => import("./views/pages/Reports"));
const UnitView = lazy(() => import("./views/pages/Units/View"));
const Loads = lazy(() => import("./views/pages/Loads/List"));
const LoadNew = lazy(() => import("./views/pages/Loads/New"));
const LoadEdit = lazy(() => import("./views/pages/Loads/Edit"));
const CompaniesList = lazy(() => import("./views/pages/Companies/List"));
const CompaniesNew = lazy(() => import("./views/pages/Companies/New"));
const CompaniesEdit = lazy(() => import("./views/pages/Companies/Edit"));
const CustomersList = lazy(() => import("./views/pages/Customers/List"));
const CustomersNew = lazy(() => import("./views/pages/Customers/New"));
const CustomersEdit = lazy(() => import("./views/pages/Customers/Edit"));
const Locations = lazy(() => import("./views/pages/Locations/List"));
const LocationsNew = lazy(() => import("./views/pages/Locations/New"));
const Logs = lazy(() => import("./views/pages/Log/List"));
const login = lazy(() => import("./views/pages/authentication/login/Login"));

const antIcon = <LoadingOutlined style={{ fontSize: 44 }} spin />;
// Set Layout and Component Using App Route
const RouteConfig = ({
  component: Component,
  fullLayout,
  permission,
  user,
  ...rest
}) => (
  <Route
    {...rest}
    render={(props) => {
      return (
        <ContextLayout.Consumer>
          {(context) => {
            let LayoutTag =
              fullLayout === true
                ? context.fullLayout
                : context.state.activeLayout === "horizontal"
                ? context.horizontalLayout
                : context.VerticalLayout;
            return (
              <LayoutTag {...props} permission={props.user}>
                <Suspense
                  fallback={
                    <Spin
                      indicator={antIcon}
                      style={{
                        height: "calc(100vh - 20rem)",
                        width: "100%",
                        display: "flex",
                        justifyContent: "center",
                        alignItems: "center",
                      }}
                    />
                  }
                >
                  <Component {...props} />
                </Suspense>
              </LayoutTag>
            );
          }}
        </ContextLayout.Consumer>
      );
    }}
  />
);
const mapStateToProps = (state) => {
  return {
    user: state.auth.login.userRole,
  };
};

const AppRoute = connect(mapStateToProps)(RouteConfig);

class AppRouter extends React.Component {
  componentDidMount() {
    let session_id = Cookies.get("session_id");
    let token;
    if (session_id !== undefined) {
      fetch("/session/get_by_session_id")
        .then((res) => {
          token = res.headers.get("authorization");
          return res.json();
        })
        .then((data) => {
          if (token) {
            this.props.cookiesAuth(token, data.role, data.username);
          } else {
            this.props.cookiesAuth();
          }
        })
        .catch((err) => {
          this.props.cookiesAuth();
        });
    } else {
      this.props.cookiesAuth();
    }
  }

  componentDidUpdate(prevProps, prevState) {
    const el = document.getElementById("loading-screen");
    if (
      this.props.fullScreenAnimation &&
      this.props.fullScreenAnimation !== prevProps.fullScreenAnimation
    ) {
      el && el.classList.remove("loaded");
    } else if (
      !this.props.fullScreenAnimation &&
      this.props.fullScreenAnimation !== prevProps.fullScreenAnimation
    ) {
      el && setTimeout(() => el.classList.add("loaded"), 2000);
    }
    if (
      this.props.cookiesAuthFinished &&
      this.props.cookiesAuthFinished !== prevProps.cookiesAuthFinished
    ) {
      el && setTimeout(() => el.classList.add("loaded"), 2000);
    }
  }

  render() {
    if (this.props.is_logged_in === false) {
      return (
        <Router history={history}>
          <Redirect to="/login" />
          <Switch>
            <AppRoute path="/login" component={login} fullLayout />
            <Route render={() => <Redirect to="/login" />} />
          </Switch>
        </Router>
      );
    }
    return (
      // Set the directory path if you are deploying in sub-folder
      <Router history={history}>
        <Switch>
          <AppRoute exact path="/dashboard/list" component={Dashboard} />
          <AppRoute exact path="/trips/list" component={Trips} />
          <AppRoute exact path="/trips/new" component={NewTrip} />
          <AppRoute exact path="/trips/view/:id" component={ViewTrip} />
          <AppRoute exact path="/trips/edit/:id" component={EditTrip} />
          <AppRoute path="/loads/list" component={Loads} />
          <AppRoute path="/loads/new" component={LoadNew} />
          <AppRoute path="/loads/edit/:id" component={LoadEdit} />
          <AppRoute exact path="/trips/list" component={Trips} />
          <AppRoute exact path="/trips/new" component={NewTrip} />
          <AppRoute exact path="/users/list" component={Dispatchers} />
          <AppRoute exact path="/users/new" component={DispatcherNew} />
          <AppRoute exact path="/users/edit/:id" component={DispatcherEdit} />
          <AppRoute exact path="/drivers/list" component={DriverList} />
          <AppRoute path="/driver/view/:id" component={DriverView} />
          <AppRoute path="/driver/edit/:id" component={DriverEdit} />
          <AppRoute path="/drivers/new" component={DriverNew} />
          <AppRoute path="/fleet/list" component={FleetList} />
          <AppRoute path="/reports" component={Reports} />
          <AppRoute path="/units/list" component={UnitList} />
          <AppRoute path="/units/new" component={UnitNew} />
          <AppRoute path="/unit/view/:id" component={UnitView} />
          <AppRoute path="/unit/edit/:id" component={UnitEdit} />
          <AppRoute path="/companies/list" component={CompaniesList} />
          <AppRoute exact path="/locations" component={Locations} />
          <AppRoute path="/locations/new" component={LocationsNew} />
          <AppRoute path="/logs" component={Logs} />
          <AppRoute path="/companies/new" component={CompaniesNew} />
          <AppRoute path="/companies/edit/:id" component={CompaniesEdit} />
          <AppRoute path="/customers/list" component={CustomersList} />
          <AppRoute path="/customers/new" component={CustomersNew} />
          <AppRoute path="/customers/edit/:id" component={CustomersEdit} />
          <AppRoute
            path="/exit"
            component={() => {
              Cookies.remove("session_id");
              document.location = "/login";
            }}
          />
          <Route render={() => <Redirect to="/dashboard/list" />} />
        </Switch>
      </Router>
    );
  }
}

const AppMapStateToProps = (state) => {
  return {
    cookiesAuthFinished: state.auth.login.cookiesAuthFinished,
    user: state.auth.login.userRole,
    is_logged_in: state.auth.login.is_logged_in,
    fullScreenAnimation: state.auth.login.fullScreenAnimation,
    error: state.auth.login.error,
  };
};
export default connect(AppMapStateToProps, {
  cookiesAuth,
})(AppRouter);

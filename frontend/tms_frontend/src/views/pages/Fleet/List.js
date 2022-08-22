import React from "react";
import {
  Card,
  CardBody,
  Input,
  UncontrolledDropdown,
  DropdownMenu,
  DropdownItem,
  DropdownToggle,
  Media,
  Badge,
  Modal,
  ModalHeader,
  ModalBody,
} from "reactstrap";
import PerfectScrollbar from "react-perfect-scrollbar";
import { AgGridReact } from "ag-grid-react";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import { history } from "../../../history";
import {  Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
import Moment from "react-moment";
class Drivers extends React.Component {
  state = {
    notifications: [],
    allNotifications: [],
    deleteAlert: false,
    data: [],
    states: [],
    loading: false,
    notificationsLoading: false,
    notificationsModal: false,
    paginationPageSize: 10,
    defaultColDef: {
      sortable: true,
      resizable: true,
      suppressMenu: false,
      wrapText: true,
      autoHeight: true,
      filter: true,
      tooltip: (params) => {
        return params.value;
      },
    },
    page: null,
    total: 1,
    columnDefs: [
      {
        headerName: "#",
        field: "index",
        maxWidth: 50,
        flex: 1,
        filter: false,
        cellStyle: function (params) {
          return {
            fontWeight: "500",
          };
        },
      },
      {
        headerName: "Unit Number",
        field: "number",
        maxWidth: 120,
        flex: 2,
      },
      {
        headerName: "VIN",
        field: "vin",
        maxWidth: 170,
        flex: 2,
      },
      {
        headerName: "Status",
        field: "status",
        maxWidth: 120,
        flex: 1,
        cellStyle: function (params) {
          return {
            fontSize: "13px",
            color: params.data.unitStatusColor ? "white" : "black",
            backgroundColor: params.data.unitStatusColor
              ? params.data.unitStatusColor
              : "white",
            textTransform: "uppercase",
            textAlign: "center",
          };
        },
      },
      {
        headerName: "Ownership type",
        field: "ownership",
        minWidth: 100,
        cellStyle: function (params) {
          return { textAlign: "center" };
          // if (params.value == "TEAM" || params.value == "OWNER TEAM") {
          //   return { color: "white", backgroundColor: "#606060" };
          // } else {
          //   return { color: "white", backgroundColor: "#808080" };
          // }
        },
      },
      {
        headerName: "License Expiration",
        field: "licenseExpiration",
        minWidth: 100,
        flex: 1,
      },
      {
        headerName: "PM By Millage",
        field: "pmByMillage",
        minWidth: 100,
        flex: 1,
      },
      {
        headerName: "PM By Date",
        field: "pmByDate",
        minWidth: 100,
        flex: 1,
      },
      {
        maxWidth: 50,
        flex: 1,
        headerName: "",
        field: "actions",
        sortable: false,
        filter: false,
        editable: false,
        suppressMenu: false,
        cellRendererFramework: (params) => {
          return (
            <div>
              <Link to={`/unit/edit/${params.data.number}`}>
                <Icon.Edit
                  onClick={() => history.push()}
                  className="icon-button ml-1"
                  size={20}
                  color="darkgray"
                />
              </Link>
            </div>
          );
        },
      },
    ],
  };
  pageChanged = (page) => {
    this.setState({
      page: page - 1,
    });
  };
  nominateToDelete = (id) => {
    this.setState({
      deletingId: id,
      deleteAlert: true,
    });
  };
  updateInfo = (pageNumber) => {
    let number =
      document.getElementById("number") &&
      document.getElementById("number").value;
    let vin =
      document.getElementById("number") && document.getElementById("vin").value;
    this.setState({
      loading: true,
    });
    fetch(
      `/fleet/list?${number && `&number=${number}`}${vin && `&vin=${vin}`}`,
      {
        headers: {
          Authorization: this.props.token,
        },
      }
    )
      .then((res) => res.json())
      .then((data) => {
        if (document.getElementById("number"))
          if (
            document.getElementById("number").value !== number ||
            document.getElementById("vin").value !== vin
          )
            return;
        let dataToShow = [];
        data.data.forEach((el, i) => {
          // let stateId = this.state.states.filter((obj) => {
          //   if (el.initialLocation !== null)
          //     return obj.id == el.initialLocation.stateProvince;
          // });
          let date = null;
          if (el.licenseExpiration) date = new Date(el.licenseExpiration);
          date && date.toLocaleDateString();
          let elToShow = {
            index: i + 1,
            id: el.id,
            number: el.number,
            vin: el.vinNumber,
            status: el.status,
            ownership: el.ownershipType,
            licenseExpiration: date ? date.toLocaleDateString() : null,
            actions: el.id,
            unitStatusColor: el.statusColor,
            pmByMillage: el.pmByMillage,
            pmByDate: el.pmByDate,
          };
          dataToShow.push(elToShow);
        });
        this.setState({
          data: dataToShow,
          total: data.totalElements,
          loading: false,
        });
      });
  };
  updateAllNotifications = () => {
    fetch(`/fleet/expiration_notifications?size=10000`, {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        let allNotifications = data.page.content;
        allNotifications.sort((a, b) => a.expirationTime - b.expirationTime);
        this.setState({ allNotifications, notificationsLoading: false });
      });
  };
  componentDidUpdate(prevProps, prevState) {
    if (this.state.page !== prevState.page) {
      this.updateInfo(this.state.page);
    }
    if (
      this.state.notificationsModal !== prevState.notificationsModal &&
      this.state.notificationsModal
    ) {
      this.setState({ notificationsLoading: true });
      this.updateAllNotifications();
    }
  }
  updateNotifications = () => {
    fetch(`/fleet/expiration_notifications?was_seen=false&size=10000`, {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        let notifications = data.page.content;
        notifications.sort((a, b) => a.expirationTime - b.expirationTime);
        this.setState({ notifications, notificationsLoading: false });
      });
  };
  readNotification = (id) => {
    this.setState({ notificationsLoading: true });
    fetch(`/fleet/expiration_notification/${id}`, {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        this.updateNotifications();
      });
  };
  componentDidMount() {
    this.setState({
      loading: true,
    });
    this.updateNotifications();
    fetch("/state_province/all", {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        this.setState({
          states: data,
        });
        this.updateInfo(0);
      });
  }
  antIcon = (<LoadingOutlined style={{ fontSize: 44 }} spin />);

  render() {
    const { columnDefs, defaultColDef, notifications, notificationsModal } =
      this.state;
    return (
      <>
        <Card className="agGrid-card">
          <div className="d-flex justify-content-between align-items-center mt-2 mx-2 mb-1">
            <h3 className="mb-0">Fleet list</h3>
            <div className="d-flex align-items-center">
              <h4 className="mx-2 mb-0 text-nowrap">Global search</h4>
              <Input
                type="text"
                placeholder="number"
                onInput={(e) => this.updateInfo(0)}
                id="number"
              />
              <Input
                className="mx-1"
                type="text"
                placeholder="vin"
                onInput={(e) => this.updateInfo(0)}
                id="vin"
              />
            </div>
            <ul className="nav navbar-nav navbar-nav-user float-right">
              <UncontrolledDropdown
                tag="li"
                className="dropdown-notification nav-item"
              >
                <DropdownToggle tag="p" className="nav-link-label">
                  <Icon.Bell size={30} />
                  {!!notifications.length && (
                    <Badge pill color="primary" className="badge-fleet">
                      {notifications.length}
                    </Badge>
                  )}
                </DropdownToggle>
                <DropdownMenu tag="ul" right className="dropdown-menu-media">
                  <li className="dropdown-menu-header">
                    <div className="dropdown-header mt-0">
                      <h3 className="text-white">{notifications.length} New</h3>
                      <span className="notification-title">
                        Fleet Notifications
                      </span>
                    </div>
                  </li>
                  <Spin
                    spinning={this.state.notificationsLoading}
                    indicator={this.antIcon}
                    style={{
                      height: "calc(100vh - 20rem)",
                      width: "100%",
                      display: "flex",
                      justifyContent: "center",
                      alignItems: "center",
                    }}
                  >
                    {" "}
                    <PerfectScrollbar
                      style={{ maxHeight: 300, minHeight: 300 }}
                      className="media-list overflow-auto position-relative"
                      options={{
                        wheelPropagation: true,
                      }}
                    >
                      {this.state.notifications.map((item) => {
                        return (
                          <div className="d-flex justify-content-between border-bottom">
                            <Media className="d-flex align-items-start">
                              <Media
                                left
                                tag="a"
                                onClick={() => this.readNotification(item.id)}
                              >
                                <Icon.XCircle
                                  className="font-medium-5 danger"
                                  size={21}
                                />
                              </Media>
                              <Media body>
                                <Media
                                  heading
                                  className="primary media-heading"
                                  tag="h6"
                                >
                                  {item.expirationFieldName}
                                </Media>
                                <p className="notification-text">
                                  {item.expirationEntityLink}
                                </p>
                              </Media>
                              <small>
                                <time
                                  className="media-meta"
                                  dateTime="2015-06-11T18:29:20+08:00"
                                >
                                  <Moment fromNow>{item.expirationTime}</Moment>
                                </time>
                              </small>
                            </Media>
                          </div>
                        );
                      })}
                      {this.state.notifications.length === 0 && (
                        <p className="text-center mt-2">No notifications</p>
                      )}
                    </PerfectScrollbar>
                  </Spin>

                  <li className="dropdown-menu-footer border-top">
                    <DropdownItem
                      tag="a"
                      className="p-1 text-center"
                      onClick={() =>
                        this.setState({ notificationsModal: true })
                      }
                    >
                      <span className="align-middle">
                        Read all notifications
                      </span>
                    </DropdownItem>
                  </li>
                </DropdownMenu>
              </UncontrolledDropdown>
            </ul>
          </div>

          <CardBody className="py-0 no-pagination">
            {this.state.loading ? (
              <Spin
                indicator={this.antIcon}
                style={{
                  height: "calc(100vh - 20rem)",
                  width: "100%",
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                }}
              />
            ) : (
              <>
                <div className="ag-theme-material w-100 ag-grid-table mb-1">
                  <AgGridReact
                    enableCellTextSelection="true"
                    reactNext={true}
                    rowSelection="multiple"
                    defaultColDef={defaultColDef}
                    columnDefs={columnDefs}
                    rowData={this.state.data}
                    colResizeDefault={"shift"}
                    animateRows={true}
                    floatingFilter={true}
                    pagination={false}
                    pivotPanelShow="always"
                  />
                </div>
              </>
            )}
          </CardBody>
        </Card>
        <Modal
          isOpen={notificationsModal}
          toggle={() =>
            this.setState({ notificationsModal: !notificationsModal })
          }
          className="modal-dialog-centered"
        >
          <ModalHeader
            toggle={() =>
              this.setState({ notificationsModal: !notificationsModal })
            }
          >
            All notifications
          </ModalHeader>
          <ModalBody>
            <Spin
              spinning={this.state.notificationsLoading}
              indicator={this.antIcon}
              style={{
                height: "calc(100vh - 20rem)",
                width: "100%",
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
              }}
            >
              {" "}
              <PerfectScrollbar
                style={{ maxHeight: 300, minHeight: 300 }}
                className="media-list overflow-auto position-relative"
                options={{
                  wheelPropagation: true,
                }}
              >
                {this.state.allNotifications.map((item) => {
                  return (
                    <div className="d-flex justify-content-between border-bottom">
                      <Media className="d-flex align-items-start">
                        <Media left tag="p">
                          <Icon.Clock
                            className="font-medium-5 primary"
                            size={21}
                          />
                        </Media>
                        <Media body>
                          <Media
                            heading
                            className="primary media-heading"
                            tag="h6"
                          >
                            {item.expirationFieldName}
                          </Media>
                          <p className="notification-text">
                            {item.expirationEntityLink}
                          </p>
                        </Media>
                        <small>
                          <time
                            className="media-meta"
                            dateTime="2015-06-11T18:29:20+08:00"
                          >
                            <Moment fromNow>{item.expirationTime}</Moment>
                          </time>
                        </small>
                      </Media>
                    </div>
                  );
                })}
                {this.state.allNotifications.length === 0 && (
                  <p className="text-center mt-2">No notifications</p>
                )}
              </PerfectScrollbar>
            </Spin>
          </ModalBody>
        </Modal>
      </>
    );
  }
}
const mapStateToProps = (state) => {
  return {
    token: state.auth.login.token,
  };
};
export default connect(mapStateToProps)(Drivers);

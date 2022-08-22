import React from "react";
import { Card, CardBody, Nav, NavItem, NavLink, Input } from "reactstrap";
import { AgGridReact } from "ag-grid-react";
import { Button } from "reactstrap";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import * as Icon from "react-feather";
import classnames from "classnames";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import { Pagination, Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
import SweetAlert from "react-bootstrap-sweetalert";
class Trips extends React.Component {
  state = {
    active: 0,
    trips: [],
    statuses: [],
    deletingId: null,
    deleteAlert: false,
    truckNumber: null,
    page: null,
    total: 1,
    loading: false,
    paginationPageSize: 20,
    defaultColDef: {
      sortable: true,
      resizable: true,
      filter: true,
      tooltip: (params) => {
        return params.value;
      },
    },
    columnDefs: [
      {
        headerName: "Trip",
        field: "id",
        maxWidth: 80,
        flex: 1,
      },
      {
        headerName: "Load №",
        field: "loadNumber",
        maxWidth: 150,
        flex: 1,
        cellRendererFramework: function (params) {
          return (
            <div className="drivers-col">
              <Link
                className="link-in-table"
                to={`/loads/edit/${params.data.loadId}`}
              >
                {params.value}
              </Link>
            </div>
          );
        },
      },
      {
        headerName: "Pickup",
        field: "pickup",
        maxWidth: 140,
        minWidth: 140,
        flex: 1,
      },
      {
        headerName: "Delivery",
        field: "delivery",
        maxWidth: 140,
        minWidth: 140,
        flex: 1,
      },
      {
        headerName: "Driver",
        field: "driver",
        minWidth: 300,
        maxWidth: 300,
        flex: 1,
        cellRendererFramework: function (params) {
          return (
            <div className="drivers-col">
              <Link
                className="link-in-table"
                to={`/driver/view/${params.data.driverId}`}
              >
                {params.value}
              </Link>
              {params.data.teammateId !== null && (
                <Link
                  className="link-in-table"
                  to={`/driver/view/${params.data.teammateId}`}
                >
                  {params.data.teammateName}
                </Link>
              )}
            </div>
          );
        },
      },
      {
        headerName: "Status",
        field: "statusName",
        maxWidth: 120,
        flex: 1,
        cellStyle: function (params) {
          return {
            fontSize: "13px",
            color: params.data.statusColor ? "white" : "black",
            backgroundColor: params.data.statusColor
              ? params.data.statusColor
              : "white",
            textAlign: "center",
            textTransform: "uppercase",
          };
        },
      },
      {
        headerName: "Accessory Driver Pay",
        field: "accessoryDriverPay",
        maxWidth: 170,
        flex: 1,
      },
      {
        headerName: "Driver Advance",
        field: "driverAdvance",
        maxWidth: 140,
        flex: 1,
      },
      {
        headerName: "To",
        field: "to",
        minWidth: 100,
        flex: 1,
        tooltip: (params) => {
          return params.value;
        },
      },
      {
        maxWidth: 100,
        flex: 1,
        headerName: "",
        field: "actions",
        sortable: false,
        editable: false,
        suppressMenu: false,
        cellRendererFramework: (params) => {
          return (
            <div>
              <Link to={`/trips/view/${params.data.id}`}>
                <Icon.Eye className="icon-button" size={20} color="darkgray" />
              </Link>
              <Link to={`/trips/edit/${params.data.id}`}>
                <Icon.Edit
                  className="icon-button ml-1"
                  size={20}
                  color="darkgray"
                />
              </Link>
              {this.props.userRole === "admin" && (
                <Icon.Delete
                  onClick={() => this.nominateToDelete(params.data.id)}
                  className="icon-button ml-1"
                  size={20}
                  color="darkgray"
                />
              )}
            </div>
          );
        },
      },
    ],
  };
  componentDidUpdate(prevProps, prevState) {
    if (
      this.state.page !== prevState.page ||
      this.state.truckNumber !== prevState.truckNumber
    ) {
      if (
        this.state.truckNumber !== prevState.truckNumber &&
        this.state.page !== 0
      ) {
        this.setState({ page: 0 });
        return;
      }
      this.setState({
        loading: true,
      });
      let truckNumber = this.state.truckNumber;
      fetch(
        `/trip/list?${
          this.state.active && `status_id=${this.state.active}`
        }&sort=id,DESC&size=40&page=${this.state.page}${
          truckNumber ? `&truck_number=${truckNumber}` : ""
        }`,
        {
          headers: {
            Authorization: this.props.token,
          },
        }
      )
        .then((res) => res.json())
        .then((data) => {
          if (truckNumber !== this.state.truckNumber) return;
          let dataToShow = [];
          data.content.forEach((el) => {
            let elToShow = {
              id: el.id,
              loadNumber: el.loadNumber,
              loadId: el.loadId,
              driverId: el.driverId,
              driver: el.driverName,
              accessoryDriverPay: el.accessoryDriverPay,
              primaryPhone: el.phone,
              driverAdvance: el.driverAdvance,
              to: el.to,
              statusName: el.statusName,
              statusColor: el.statusColor,
              hiredOn: el.hireDate,
              delivery: el.deliveryDateFormatted,
              pickup: el.pickDateFormatted,
              teammateName: el.teammateName,
              teammateId: el.teammateId,
            };
            dataToShow.push(elToShow);
          });
          this.setState({
            trips: dataToShow,
            loading: false,
            total: data.total_elements,
          });
        });
    }
  }
  componentDidMount() {
    this.setState({
      loading: true,
    });
    fetch("/trip/list?sort=id,DESC&size=40", {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        let dataToShow = [];
        data.content.forEach((el) => {
          let elToShow = {
            id: el.id,
            loadNumber: el.loadNumber,
            loadId: el.loadId,
            driverId: el.driverId,
            driver: el.driverName,
            accessoryDriverPay: el.accessoryDriverPay,
            primaryPhone: el.phone,
            driverAdvance: el.driverAdvance,
            to: el.to,
            statusName: el.statusName,
            statusColor: el.statusColor,
            hiredOn: el.hireDate,
            delivery: el.deliveryDateFormatted,
            pickup: el.pickDateFormatted,
            teammateName: el.teammateName,
            teammateId: el.teammateId,
          };
          dataToShow.push(elToShow);
        });
        this.setState({
          trips: dataToShow,
          loading: false,
          total: data.total_elements,
          statuses: data.trip_statuses,
        });
      });
  }
  toggle = (tab) => {
    if (this.state.active !== tab) {
      this.setState({ active: tab });
      if (tab == 0) {
        this.setState({
          loading: true,
        });
        fetch("/trip/list?sort=id,DESC&size=40&page=0", {
          headers: {
            Authorization: this.props.token,
          },
        })
          .then((res) => res.json())
          .then((data) => {
            let dataToShow = [];
            data.content.forEach((el) => {
              let elToShow = {
                id: el.id,
                loadNumber: el.loadNumber,
                loadId: el.loadId,
                driverId: el.driverId,
                driver: el.driverName,
                accessoryDriverPay: el.accessoryDriverPay,
                primaryPhone: el.phone,
                driverAdvance: el.driverAdvance,
                to: el.to,
                statusName: el.statusName,
                statusColor: el.statusColor,
                hiredOn: el.hireDate,
                delivery: el.deliveryDateFormatted,
                pickup: el.pickDateFormatted,
                teammateName: el.teammateName,
                teammateId: el.teammateId,
              };
              dataToShow.push(elToShow);
            });
            this.setState({
              trips: dataToShow,
              loading: false,
              total: data.total_elements,
              statuses: data.trip_statuses,
              page: 0,
            });
          });
      } else {
        this.setState({
          loading: true,
        });
        fetch(`/trip/list?status_id=${tab}&sort=id,DESC&size=40&page=0`, {
          headers: {
            Authorization: this.props.token,
          },
        })
          .then((res) => res.json())
          .then((data) => {
            let dataToShow = [];
            data.content.forEach((el) => {
              let elToShow = {
                id: el.id,
                loadNumber: el.loadNumber,
                loadId: el.loadId,
                driverId: el.driverId,
                driver: el.driverName,
                accessoryDriverPay: el.accessoryDriverPay,
                primaryPhone: el.phone,
                driverAdvance: el.driverAdvance,
                to: el.to,
                statusName: el.statusName,
                statusColor: el.statusColor,
                hiredOn: el.hireDate,
                delivery: el.deliveryDateFormatted,
                pickup: el.pickDateFormatted,
                teammateName: el.teammateName,
                teammateId: el.teammateId,
              };
              dataToShow.push(elToShow);
            });
            this.setState({
              trips: dataToShow,
              loading: false,
              total: data.total_elements,
            });
          });
      }
    }
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
  deleteTrip = () => {
    fetch(`/trip/${this.state.deletingId}`, {
      headers: {
        Authorization: this.props.token,
      },
      method: "DELETE",
    }).then((res) => {
      fetch(
        `/trip/list?${
          this.state.active && `status_id=${this.state.active}`
        }&sort=id,DESC&size=40&page=${this.state.page}`,
        {
          headers: {
            Authorization: this.props.token,
          },
        }
      )
        .then((res) => res.json())
        .then((data) => {
          let dataToShow = [];
          data.content.forEach((el) => {
            let elToShow = {
              id: el.id,
              loadNumber: el.loadNumber,
              loadId: el.loadId,
              driverId: el.driverId,
              driver: el.driverName,
              accessoryDriverPay: el.accessoryDriverPay,
              primaryPhone: el.phone,
              driverAdvance: el.driverAdvance,
              to: el.to,
              statusName: el.statusName,
              statusColor: el.statusColor,
              hiredOn: el.hireDate,
              delivery: el.deliveryDateFormatted,
              pickup: el.pickDateFormatted,
              teammateName: el.teammateName,
              teammateId: el.teammateId,
            };
            dataToShow.push(elToShow);
          });
          this.setState({
            trips: dataToShow,
            loading: false,
            total: data.total_elements,
          });
        });
    });
    this.setState({
      deleteAlert: false,
      deletingId: null,
    });
  };
  antIcon = (<LoadingOutlined style={{ fontSize: 44 }} spin />);

  render() {
    const { columnDefs, defaultColDef } = this.state;
    return (
      <>
        <SweetAlert
          title="Are you sure?"
          warning
          show={this.state.deleteAlert}
          showCancel
          reverseButtons
          cancelBtnBsStyle="danger"
          confirmBtnText="Yes, delete it!"
          cancelBtnText="Cancel"
          onConfirm={() => {
            this.deleteTrip(this.state.deletingId);
          }}
          onCancel={() => this.setState({ deleteAlert: false })}
        >
          You won't be able to revert this!
        </SweetAlert>
        <Card className="overflow-hidden agGrid-card">
          <div className="d-flex justify-content-between align-items-start mt-2 mx-2 ">
            <div className="d-flex justify-content-between align-items-start">
              <Nav pills className="dashboard-tabs">
                <NavItem>
                  <NavLink
                    style={{ height: 37 }}
                    className={classnames(
                      {
                        customActive: this.state.active === 0,
                      },
                      "d-flex align-items-center"
                    )}
                    onClick={() => {
                      this.toggle(0);
                    }}
                  >
                    <h4 className="mb-0 ml-1 mr-1">All</h4>
                  </NavLink>
                </NavItem>
                {this.state.statuses.map((item) => (
                  <NavItem>
                    <NavLink
                      style={{ height: 37 }}
                      className={classnames(
                        {
                          customActive: this.state.active === item.id,
                        },
                        "d-flex align-items-center"
                      )}
                      onClick={() => {
                        this.toggle(item.id);
                      }}
                    >
                      <h4 className="mb-0 ml-1 mr-1">{item.name}</h4>
                    </NavLink>
                  </NavItem>
                ))}
              </Nav>

              <div className="d-flex align-items-center">
                <h4 className="mx-1 mb-0 text-nowrap">Global search</h4>
                <Input
                  type="text"
                  placeholder="truck number"
                  onInput={(e) =>
                    this.setState({ truckNumber: e.target.value })
                  }
                  id="number"
                />
              </div>
            </div>

            <Link
              to="/trips/new"
              className="d-flex align-items-start text-white"
            >
              <Button
                size="sm"
                color="success"
                className="d-flex align-items-center"
                type="button"
              >
                <Icon.Plus size={20} /> Add new trip
              </Button>
            </Link>
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
                <div className="ag-theme-material w-100 ag-grid-table with-pagination">
                  <AgGridReact
                    enableCellTextSelection="true"
                    rowSelection="multiple"
                    defaultColDef={defaultColDef}
                    columnDefs={columnDefs}
                    rowData={this.state.trips}
                    colResizeDefault={"shift"}
                    animateRows={true}
                    pagination={false}
                    floatingFilter={true}
                  />
                </div>
                <Pagination
                  current={this.state.page + 1}
                  total={this.state.total}
                  onChange={this.pageChanged}
                  pageSize={40}
                  style={{
                    textAlign: "center",
                    margin: "20px",
                    marginBottom: "50px",
                  }}
                />
              </>
            )}
          </CardBody>
        </Card>
      </>
    );
  }
}
const mapStateToProps = (state) => {
  return {
    token: state.auth.login.token,
    userRole: state.auth.login.userRole,
  };
};
export default connect(mapStateToProps)(Trips);

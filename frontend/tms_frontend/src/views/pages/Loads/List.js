import React from "react";
import { Card, CardBody, Input } from "reactstrap";
import { AgGridReact } from "ag-grid-react";
import { ChevronDown } from "react-feather";
import { connect } from "react-redux";
import { Button } from "reactstrap";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import { Link } from "react-router-dom";
import * as Icon from "react-feather";
import { history } from "../../../history";
import { Pagination, Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
import SweetAlert from "react-bootstrap-sweetalert";

class List extends React.Component {
  state = {
    data: [],
    page: null,
    total: 1,
    loading: false,
    number: null,
    paginationPageSize: 20,
    defaultColDef: {
      sortable: true,
      resizable: true,
      suppressMenu: true,
      tooltip: (params) => {
        return params.value;
      },
    },
    columnDefs: [
      {
        headerName: "Load #",
        field: "load",
        minWidth: 170,
        minWidth: 170,
        flex: 1,
        filter: true,
      },
      {
        headerName: "Trip #",
        field: "trip",
        filter: true,
        maxWidth: 80,
        flex: 1,
        cellRendererFramework: function (params) {
          return (
            <Link className="link-in-table" to={`/trips/view/${params.value}`}>
              {params.value}
            </Link>
          );
        },
      },
      {
        headerName: "Status",
        field: "status",
        filter: true,
        maxWidth: 150,
        flex: 1,
        cellStyle: function (params) {
          if (params.value == "NOT DISPATCHED") {
            //mark police cells as red
            return { color: "white", backgroundColor: "rgb(24, 128, 56)" };
          } else {
            return { color: "white", backgroundColor: "#ea5455" };
          }
        },
      },
      {
        headerName: "Pickup",
        field: "pickup",
        filter: true,
        maxWidth: 140,
        minWidth: 140,
        flex: 1,
      },
      {
        headerName: "Delivery",
        field: "delivery",
        filter: true,
        maxWidth: 140,
        minWidth: 140,
        flex: 1,
      },
      // {
      //   headerName: "Driver",
      //   field: "driver",
      //   filter: true,
      //   minWidth: 150,
      //   flex: 1,
      //   cellRendererFramework: (params) => {
      //     return (
      //       <Link
      //         className="link-in-table"
      //         to={`/driver/view/${params.data.driverId}`}
      //       >
      //         {params.value}
      //       </Link>
      //     );
      //   },
      // },
      // {
      //   headerName: "Truck",
      //   field: "truck",
      //   filter: true,
      //   minWidth: 130,
      //   flex: 1,
      //   cellRendererFramework: (params) => {
      //     return (
      //       <Link
      //         className="link-in-table"
      //         to={`/unit/edit/${params.value}`}
      //       >
      //         {params.value}
      //       </Link>
      //     );
      //   },
      // },
      {
        headerName: "Customer",
        field: "customer",
        filter: "agNumberColumnFilter",
        minWidth: 130,
        flex: 1,
        tooltip: (params) => {
          return params.value;
        },
      },
      {
        headerName: "From",
        field: "from",
        filter: "agNumberColumnFilter",
        minWidth: 180,
        flex: 1,
        tooltip: (params) => {
          return params.value;
        },
      },
      {
        headerName: "To",
        field: "to",
        filter: "agNumberColumnFilter",
        minWidth: 180,
        flex: 1,
        tooltip: (params) => {
          return params.value;
        },
      },
      {
        maxWidth: 80,
        flex: 1,
        headerName: "",
        field: "actions",
        sortable: false,
        editable: false,
        suppressMenu: false,
        cellRendererFramework: (params) => {
          return (
            <div>
              <Link to={`/loads/edit/${params.data.id}`}>
                <Icon.Edit className="icon-button" size={20} color="darkgray" />
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
      // {
      //   minWidth: 230,
      //   flex: 1,
      //   headerName: "Actions",
      //   field: "actions",
      //   sortable: false,
      //   editable: false,
      //   suppressMenu: false,
      //   cellRendererFramework: function (params) {
      //     return (
      //       <div>
      //         <Button.Ripple color="warning" className="mr-1" size="sm">
      //           <Icon.Eye size={16} />
      //         </Button.Ripple>
      //         <Button.Ripple color="success" className="mr-1" size="sm">
      //           <Icon.Edit size={16} />
      //         </Button.Ripple>
      //         <Button.Ripple color="danger" size="sm">
      //           <Icon.Delete size={16} />
      //         </Button.Ripple>
      //       </div>
      //     );
      //   },
      // },
    ],
    deleteAlert: false,
    deletingId: null,
  };
  nominateToDelete = (id) => {
    this.setState({
      deletingId: id,
      deleteAlert: true,
    });
  };
  deleteLoad = () => {
    fetch(`/load/${this.state.deletingId}`, {
      headers: {
        Authorization: this.props.token,
      },
      method: "DELETE",
    }).then((res) => {
      fetch("/load/list?sort=id,DESC&size=40&tripId=-1", {
        headers: {
          Authorization: this.props.token,
        },
      })
        .then((res) => res.json())
        .then((data) => {
          let dataToShow = [];
          data.content.forEach((el, i) => {
            let elToShow = {
              id: el.id,
              load: el.customLoadNumber,
              trip: el.tripId,
              status: el.tripId == null ? "NOT DISPATCHED" : "DISPATCHED",
              pickup: el.pickupDateFormatted,
              delivery: el.deliveryDateFormatted,
              customer: el.customer,
              from: el.from,
              to: el.to,
              driverId: el.driverId,
              driver: el.driverName,
              truck: el.truckNumber,
            };
            dataToShow.push(elToShow);
          });
          this.setState({
            data: dataToShow,
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

  componentDidUpdate(prevProps, prevState) {
    if (
      this.state.page !== prevState.page ||
      this.state.number !== prevState.number
    ) {
      let number = this.state.number;
      if (this.state.number !== prevState.number && this.state.page !== 0) {
        this.setState({ page: 0 });
        return;
      }
      this.setState({
        loading: true,
      });
      fetch(
        `/load/list?sort=id,DESC&size=40&tripId=-1&page=${this.state.page}${
          number ? `&number=${number}` : ""
        }`,
        {
          headers: {
            Authorization: this.props.token,
          },
        }
      )
        .then((res) => res.json())
        .then((data) => {
          if (number !== this.state.number) return;
          let dataToShow = [];
          data.content.forEach((el, i) => {
            let elToShow = {
              id: el.id,
              load: el.customLoadNumber,
              trip: el.tripId,
              status: el.tripId == null ? "NOT DISPATCHED" : "DISPATCHED",
              pickup: el.pickupDateFormatted,
              delivery: el.deliveryDateFormatted,
              customer: el.customer,
              from: el.from,
              to: el.to,
              driverId: el.driverId,
              driver: el.driverName,
              truck: el.truckNumber,
            };
            dataToShow.push(elToShow);
          });
          this.setState({
            data: dataToShow,
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
    fetch("/load/list?sort=id,DESC&size=40&tripId=-1", {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        let dataToShow = [];
        data.content.forEach((el, i) => {
          let elToShow = {
            id: el.id,
            load: el.customLoadNumber,
            trip: el.tripId,
            status: el.tripId == null ? "NOT DISPATCHED" : "DISPATCHED",
            pickup: el.pickupDateFormatted,
            delivery: el.deliveryDateFormatted,
            customer: el.customer,
            from: el.from,
            to: el.to,
            driverId: el.driverId,
            driver: el.driverName,
            truck: el.truckNumber,
          };
          dataToShow.push(elToShow);
        });
        this.setState({
          data: dataToShow,
          loading: false,
          total: data.total_elements,
        });
      });
  }
  pageChanged = (page) => {
    this.setState({
      page: page - 1,
    });
  };
  render() {
    const { rowData, columnDefs, defaultColDef } = this.state;
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
            this.deleteLoad(this.state.deletingId);
          }}
          onCancel={() => this.setState({ deleteAlert: false })}
        >
          You won't be able to revert this!
        </SweetAlert>
        <Card className="overflow-hidden agGrid-card">
          <div className="d-flex justify-content-between align-items-center mt-2 mx-2 mb-1">
            <h3>All loads list</h3>
            <div className="d-flex align-items-center">
              <h4 className="mx-1 mb-0 text-nowrap">Global search</h4>
              <Input
                type="text"
                placeholder="number"
                onInput={(e) => this.setState({ number: e.target.value })}
                id="number"
              />
            </div>
            <div>
              <Link to="/loads/new">
                <Button
                  color="success"
                  className="d-flex align-items-center"
                  type="button"
                  size="sm"
                >
                  <Icon.Plus size={20} /> Add new Load
                </Button>
              </Link>
            </div>
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
                    rowData={this.state.data}
                    colResizeDefault={"shift"}
                    animateRows={true}
                    floatingFilter={true}
                    pagination={false}
                    pivotPanelShow="always"
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
export default connect(mapStateToProps)(List);

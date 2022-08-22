import React from "react";
import { Card, CardBody } from "reactstrap";
import { AgGridReact } from "ag-grid-react";
import { Button } from "reactstrap";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import SweetAlert from "react-bootstrap-sweetalert";
import { history } from "../../../history";
import { LoadingOutlined } from "@ant-design/icons";
import { Spin } from "antd";

class Dispatchers extends React.Component {
  state = {
    deleteAlert: false,
    data: [],
    states: [],
    paginationPageSize: 10,
    defaultColDef: {
      sortable: true,
      resizable: true,
      suppressMenu: true,
      wrapText: true,
      autoHeight: true,
      tooltip: (params) => {
        return params.value;
      },
    },
    loading: false,
    columnDefs: [
      {
        headerName: "ID",
        field: "id",
        minWidth: 100,
        flex: 1,
      },
      {
        headerName: "Username",
        field: "username",
        minWidth: 100,
        flex: 2,
      },
      {
        headerName: "Name",
        field: "name",
        minWidth: 100,
        flex: 2,
      },
      {
        headerName: "Phone Number",
        field: "phone",
        minWidth: 100,
        flex: 1,
      },
      {
        headerName: "Role",
        field: "role",
        minWidth: 100,
        flex: 1,
      },
      {
        maxWidth: 130,
        flex: 1,
        headerName: "Actions",
        field: "actions",
        sortable: false,
        editable: false,
        suppressMenu: false,
        cellRendererFramework: (params) => {
          return (
            <div>
              <Link to={`/users/edit/${params.data.id}`}>
                <Icon.Edit
                  onClick={() => history.push()}
                  className="icon-button ml-1"
                  size={20}
                  color="darkgray"
                />
              </Link>
              <Icon.Delete
                onClick={() => this.nominateToDelete(params.data.id)}
                className="icon-button ml-1"
                size={20}
                color="darkgray"
              />
            </div>
          );
        },
      },
    ],
  };
  nominateToDelete = (id) => {
    this.setState({
      deletingId: id,
      deleteAlert: true,
    });
  };
  deleteDispatcher = () => {
    fetch(`/admin/${this.state.deletingId}`, {
      headers: {
        Authorization: this.props.token,
      },
      method: "DELETE",
    }).then((res) => {
      this.updateInfo();
    });
    this.setState({
      deleteAlert: false,
      deletingId: null,
    });
  };
  updateInfo = () => {
    this.setState({
      loading: true,
    });
    fetch("/admin/users?sort=id,DESC&size=10000", {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        let dataToShow = [];
        data.content.forEach((el, i) => {
          let elToShow = {
            name: el.name,
            id: el.id,
            username: el.username,
            role: el.role,
            phone: el.phone,
          };
          dataToShow.push(elToShow);
        });
        this.setState({
          data: dataToShow,
          loading: false,
        });
      });
  };

  antIcon = (<LoadingOutlined style={{ fontSize: 44 }} spin />);

  componentDidMount() {
    this.setState({
      loading: true,
    });
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
        this.updateInfo();
      });
  }

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
            this.deleteDispatcher(this.state.deletingId);
          }}
          onCancel={() => this.setState({ deleteAlert: false })}
        >
          You won't be able to revert this!
        </SweetAlert>
        <Card className="overflow-hidden agGrid-card">
          <div className="d-flex justify-content-between mt-2 mx-2 mb-1">
            <h3>All users list</h3>
            <div>
              <Link
                to="/users/new"
                className="d-flex align-items-center text-white"
              >
                <Button
                  color="success"
                  className="d-flex align-items-center"
                  type="button"
                  size="sm"
                >
                  <Icon.Plus size={20} /> Add new user
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
                  pagination={false}
                  pivotPanelShow="always"
                />
              </div>
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
  };
};
export default connect(mapStateToProps)(Dispatchers);

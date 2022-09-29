import React from "react";
import { Card, CardBody } from "reactstrap";
import { AgGridReact } from "ag-grid-react";
import { Button } from "reactstrap";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import { Link } from "react-router-dom";
import SweetAlert from "react-bootstrap-sweetalert";
import { Pagination, Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";

class Customers extends React.Component {
    state = {
        deleteAlert: false,
        page: null,
        total: 1,
        loading: false,
        data: [],
        states: [],
        paginationPageSize: 10,
        defaultColDef: {
            sortable: true,
            resizable: true,
            suppressMenu: true,
            wrapText: true,
            autoHeight: true,
            filter: true,
            tooltip: (params) => {
                return params.value;
            },
        },
        columnDefs: [
            {
                headerName: "#",
                field: "index",
                maxWidth: 70,
                flex: 1,
                filter: false,
                cellStyle: function (params) {
                    return {
                        fontWeight: "500",
                    };
                },
            },
            {
                headerName: "Name",
                field: "name",
                minWidth: 100,
                flex: 2,
            },
            {
                headerName: "Email",
                field: "email",
                minWidth: 100,
                flex: 1,
                tooltip: (params) => {
                    return params.value;
                },
            },
            {
                headerName: "Phone",
                field: "phone",
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "City",
                field: "city",
                minWidth: 200,
                flex: 1,
            },
            {
                maxWidth: 100,
                flex: 1,
                headerName: "Actions",
                field: "actions",
                sortable: false,
                editable: false,
                suppressMenu: false,
                cellRendererFramework: (params) => {
                    return (
                        <div>
                            <Link to={`/customers/edit/${params.data.id}`}>
                                <Icon.Edit
                                    className="icon-button ml-1"
                                    size={20}
                                    color="darkgray"
                                />
                            </Link>
                            <Icon.Delete
                                onClick={() =>
                                    this.nominateToDelete(params.data.id)
                                }
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
    deleteCompany = () => {
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/company/${this.state.deletingId}`,
            {
                headers: {
                    Authorization: this.props.token,
                },
                method: "DELETE",
            }
        ).then((res) => {
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
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/company/list?sort=id,DESC&size=10&page=${this.state.page}`,
            {
                headers: {
                    Authorization: this.props.token,
                },
            }
        )
            .then((res) => res.json())
            .then((data) => {
                let dataToShow = [];
                data.data.forEach((el, i) => {
                    let stateId = this.state.states.filter((obj) => {
                        if (el.initialLocation !== null)
                            return obj.id === el.stateProvinceId;
                    });
                    let elToShow = {
                        index: i + 1 + this.state.page * 10,
                        id: el.id,
                        state: stateId[0] === undefined ? "" : stateId[0].name,
                        name: el.companyName,
                        phone: el.phoneNumber,
                        city: el.city,
                        email: el.email,
                    };
                    dataToShow.push(elToShow);
                });
                this.setState({
                    data: dataToShow,
                    loading: false,
                    total: data.total_elements,
                });
            });
    };
    componentDidMount() {
        this.setState({ loading: true });
        fetch(process.env.REACT_APP_BASE_URL + "/state_province/all", {
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

    componentDidUpdate(prevProps, prevState) {
        if (this.state.page !== prevState.page) {
            this.updateInfo(this.state.page);
        }
    }

    pageChanged = (page) => {
        this.setState({
            page: page - 1,
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
                        this.deleteCompany(this.state.deletingId);
                    }}
                    onCancel={() => this.setState({ deleteAlert: false })}
                >
                    You won't be able to revert this!
                </SweetAlert>
                <Card className="overflow-hidden agGrid-card">
                    <div className="d-flex justify-content-between mt-2 mx-2 mb-1">
                        <h3>All customers list</h3>
                        <div>
                            <Link
                                to="/customers/new"
                                className="d-flex align-items-center text-white"
                            >
                                <Button
                                    size="sm"
                                    color="success"
                                    className="d-flex align-items-center"
                                    type="button"
                                >
                                    <Icon.Plus size={20} /> Add new customer
                                </Button>
                            </Link>
                        </div>
                    </div>

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
                            <CardBody className="pt-0 pb-1 no-pagination">
                                <div className="ag-theme-material w-100 ag-grid-table">
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
                                        floatingFilter={true}
                                        pivotPanelShow="always"
                                    />
                                </div>
                                <Pagination
                                    current={this.state.page + 1}
                                    total={this.state.total}
                                    onChange={this.pageChanged}
                                    pageSize={10}
                                    style={{
                                        textAlign: "center",
                                        margin: "20px",
                                        marginBottom: "50px",
                                    }}
                                />
                            </CardBody>
                        </>
                    )}
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
export default connect(mapStateToProps)(Customers);

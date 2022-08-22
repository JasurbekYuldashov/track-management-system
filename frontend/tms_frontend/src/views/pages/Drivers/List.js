import React from "react";
import {Card, CardBody, Input} from "reactstrap";
import {AgGridReact} from "ag-grid-react";
import {Button} from "reactstrap";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import * as Icon from "react-feather";
import {connect} from "react-redux";
import {Link} from "react-router-dom";
import SweetAlert from "react-bootstrap-sweetalert";
import {history} from "../../../history";
import {Spin} from "antd";
import {LoadingOutlined} from "@ant-design/icons";

class Drivers extends React.Component {
    state = {
        paymentTypes: [],
        page: null,
        total: 1,
        loading: false,
        states: [],
        deleteAlert: false,
        driverStatuses: [],
        data: [],
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
                headerName: "Name",
                field: "name",
                minWidth: 100,
                filter: true,
                flex: 2,
                cellRendererFramework: function (params) {
                    return (
                        <Link
                            className="link-in-table"
                            to={`/driver/view/${params.data.id}`}
                        >
                            {params.value}
                        </Link>
                    );
                },
                cellStyle: {"white-space": "normal"},
            },
            {
                headerName: "Address",
                field: "address",
                filter: true,
                minWidth: 100,
                flex: 1,
                tooltip: (params) => {
                    return params.value;
                },
            },
            {
                headerName: "PrimaryPhone",
                field: "primaryPhone",
                filter: true,
                minWidth: 130,
                flex: 1,
            },
            {
                headerName: "Alternate phone",
                field: "alternatePhone",
                filter: true,
                minWidth: 130,
                flex: 1,
            },
            {
                headerName: "Payment Type",
                field: "paymentType",
                filter: true,
                minWidth: 100,
                cellStyle: {"white-space": "normal"},
            },
            {
                headerName: "Status",
                field: "status",
                filter: true,
                width: 120,
                cellStyle: function (params) {
                    return {
                        fontSize: "13px",
                        color: params.data.driverStatusColor ? "white" : "black",
                        backgroundColor: params.data.driverStatusColor
                            ? params.data.driverStatusColor
                            : "white",
                        textTransform: "uppercase",
                        textAlign: "center",
                    };
                },
                cellRendererFramework: (params) => {
                    let el = this.state.driverStatuses.find(
                        (item) => item.id === params.value
                    );
                    return <div>{el && el.name}</div>;
                },
            },
            {
                headerName: "Hired on",
                field: "hiredOn",
                filter: true,
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "License exp",
                field: "licenseExp",
                filter: "agNumberColumnFilter",
                minWidth: 100,
                flex: 1,
            },
            {
                headerName: "Medical Card Exp",
                field: "medicalCardExp",
                filter: "agNumberColumnFilter",
                minWidth: 100,
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
                            <Link to={`/driver/view/${params.data.id}`}>
                                <Icon.Eye className="icon-button" size={20} color="darkgray"/>
                            </Link>
                            {this.props.userRole !== "dispatcher" && <Link to={`/driver/edit/${params.data.id}`}>
                                <Icon.Edit
                                    onClick={() => history.push()}
                                    className="icon-button ml-1"
                                    size={20}
                                    color="darkgray"
                                />
                            </Link>}
                            {this.props.userRole !== "dispatcher" &&
                            <Icon.Delete
                                onClick={() => this.nominateToDelete(params.data.id)}
                                className="icon-button ml-1"
                                size={20}
                                color="darkgray"
                            />
                            }
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
    deleteDriver = () => {
        fetch(`/driver/${this.state.deletingId}`, {
            headers: {
                Authorization: this.props.token,
            },
            method: "DELETE",
        }).then((res) => {
            this.updateInfo(0);
        });
        this.setState({
            deleteAlert: false,
            deletingId: null,
        });
    };

    componentDidUpdate(prevProps, prevState) {
        if (this.state.page !== prevState.page) {
            this.setState({
                loading: true,
            });
            this.updateInfo(this.state.page);
        }
    }

    updateInfo = (pageNumber) => {
        let name =
            document.getElementById("name") && document.getElementById("name").value;
        let phone =
            document.getElementById("phone") &&
            document.getElementById("phone").value;
        this.setState({
            loading: true,
        });
        fetch(
            `/driver/list?sort=id,DESC&size=10000&page=${pageNumber}${
                name && `&searchNameText=${name}`
            }${phone && `&phone=${phone}`}`,
            {
                headers: {
                    Authorization: this.props.token,
                },
            }
        )
            .then((res) => res.json())
            .then((data) => {
                if (
                    document.getElementById("number") &&
                    document.getElementById("phone")
                )
                    if (
                        document.getElementById("name").value !== name ||
                        document.getElementById("phone").value !== phone
                    )
                        return;
                let dataToShow = [];
                data.content.forEach((el, i) => {
                    let paymentTypeId = this.state.paymentTypes.filter((obj) => {
                        return obj.id === el.defaultPaymentTypeId;
                    });
                    let stateId = this.state.states.filter((obj) => {
                        return obj.id === el.stateProvinceId;
                    });
                    let elToShow = {
                        index: i + 1,
                        id: el.id,
                        name: el.lastName + " " + el.firstName,
                        address: stateId[0] && stateId[0].name,
                        primaryPhone: el.phone,
                        alternatePhone: el.alternatePhone,
                        paymentType: paymentTypeId.length > 0 && paymentTypeId[0].name,
                        status: el.driverStatusId,
                        hiredOn: el.hireDateFormatted,
                        licenseExp: el.licenseExpirationFormatted,
                        medicalCardExp: el.medicalCardRenewalFormatted,
                        driverStatusColor: el.driverStatusColor,
                    };
                    dataToShow.push(elToShow);
                });
                this.setState({
                    data: dataToShow,
                    loading: false,
                    total: data.totalElements,
                });
            });
    };

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
            .then((data) =>
                this.setState({
                    states: data,
                })
            );
        fetch("/driver/context", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    paymentTypes: data.payment_types,
                    driverStatuses: data.driver_statuses,
                });
                this.updateInfo(0);
            });
    }

    antIcon = (<LoadingOutlined style={{fontSize: 44}} spin/>);

    pageChanged = (page) => {
        this.setState({
            page: page - 1,
        });
    };

    render() {
        const {columnDefs, defaultColDef} = this.state;
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
                        this.deleteDriver(this.state.deletingId);
                    }}
                    onCancel={() => this.setState({deleteAlert: false})}
                >
                    You won't be able to revert this!
                </SweetAlert>
                <Card className="overflow-hidden agGrid-card">
                    <div className="d-flex justify-content-between align-items-center mt-2 mx-2 mb-1">
                        <h3 className="mb-0">All drivers list</h3>
                        <div className="d-flex align-items-center">
                            <h4 className="mx-1 mb-0 text-nowrap">Global search</h4>
                            <Input
                                type="text"
                                placeholder="name"
                                onInput={(e) => this.updateInfo(0)}
                                id="name"
                            />
                            <Input
                                className="mx-1"
                                type="text"
                                placeholder="phone"
                                onInput={(e) => this.updateInfo(0)}
                                id="phone"
                            />
                        </div>
                        <div>
                            <Link
                                to="/drivers/new"
                                className="d-flex align-items-center text-white"
                            >
                                <Button
                                    size="sm"
                                    color="success"
                                    className="d-flex align-items-center"
                                    type="button"
                                >
                                    <Icon.Plus size={20}/> Add new driver
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
                                        floatingFilter={true}
                                        pagination={false}
                                        pivotPanelShow="always"
                                    />
                                </div>
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
        userRole: state.auth.login.userRole
    };
};
export default connect(mapStateToProps)(Drivers);

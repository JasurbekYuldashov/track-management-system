import React from "react";
import {Card, CardBody, Input} from "reactstrap";
import {AgGridReact} from "ag-grid-react";
import {Button} from "reactstrap";
import "../../../assets/scss/plugins/tables/_agGridStyleOverride.scss";
import * as Icon from "react-feather";
import {connect} from "react-redux";
import {Link} from "react-router-dom";
import SweetAlert from "react-bootstrap-sweetalert";
import {Spin} from "antd";
import {LoadingOutlined} from "@ant-design/icons";

class Drivers extends React.Component {
    state = {
        deleteAlert: false,
        data: [],
        states: [],
        loading: false,
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
                headerName: "Type",
                field: "type",
                maxWidth: 100,
                flex: 1,
            },
            {
                headerName: "State",
                field: "state",
                minWidth: 150,
                flex: 1,
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
                    return {textAlign: "center"};
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
                headerName: "Inspection Exp",
                field: "inspectation",
                minWidth: 100,
                flex: 1,
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
                            <Link to={`/unit/view/${params.data.number}`}>
                                <Icon.Eye className="icon-button" size={20} color="darkgray"/>
                            </Link>
                            {this.props.userRole !== "dispatcher" && <Link to={`/unit/edit/${params.data.number}`}>
                                <Icon.Edit
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
    deleteUnit = () => {
        fetch(`/unit/${this.state.deletingId}`, {
            headers: {
                Authorization: this.props.token,
            },
            method: "DELETE",
        }).then((res) => {
            this.updateInfo(this.state.page);
        });
        this.setState({
            deleteAlert: false,
            deletingId: null,
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
            `/unit/list?size=10000&page=${pageNumber}${
                number && `&number=${number}`
            }${vin && `&vin=${vin}`}`,
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
                data.content.forEach((el, i) => {
                    let stateId = this.state.states.filter((obj) => {
                        if (el.initialLocation !== null)
                            return obj.id == el.initialLocation.stateProvince;
                    });
                    let elToShow = {
                        index: i + 1,
                        state: stateId[0] == null ? "" : stateId[0].name,
                        id: el.id,
                        number: el.number,
                        vin: el.vin,
                        type: el.unitName,
                        status: el.statusName,
                        ownership: el.ownershipName,
                        licenseExpiration: el.licenseExpirationFormatted,
                        inspectation: el.inspectionStickerExpirationFormatted,
                        actions: el.id,
                        unitStatusColor: el.unitStatusColor,
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

    componentDidUpdate(prevProps, prevState) {
        if (this.state.page !== prevState.page) {
            this.updateInfo(this.state.page);
        }
    }

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
                this.updateInfo(0);
            });
    }

    antIcon = (<LoadingOutlined style={{fontSize: 44}} spin/>);

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
                        this.deleteUnit(this.state.deletingId);
                    }}
                    onCancel={() => this.setState({deleteAlert: false})}
                >
                    You won't be able to revert this!
                </SweetAlert>
                <Card className="overflow-hidden agGrid-card">
                    <div className="d-flex justify-content-between align-items-center mt-2 mx-2 mb-1">
                        <h3 className="mb-0">All units list</h3>
                        <div className="d-flex align-items-center">
                            <h4 className="mx-1 mb-0 text-nowrap">Global search</h4>
                            <Input
                                type="text"
                                placeholder="number"
                                onInput={(e) => this.updateInfo(0)}
                                id="number"
                            />
                            <Input
                                type="text"
                                className="mx-1"
                                placeholder="vin"
                                onInput={(e) => this.updateInfo(0)}
                                id="vin"
                            />
                        </div>
                        <div>
                            <Link
                                to="/units/new"
                                className="d-flex align-items-center text-white"
                            >
                                <Button
                                    size="sm"
                                    color="success"
                                    className="d-flex align-items-center"
                                    type="button"
                                >
                                    <Icon.Plus size={20}/> Add new unit
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
                        {/* <Pagination
                  current={this.state.page + 1}
                  total={this.state.total}
                  onChange={this.pageChanged}
                  pageSize={40}
                  style={{
                    textAlign: "center",
                    margin: "20px",
                    marginBottom: "50px",
                  }}
                /> */}
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

import React from "react";
import {
    Card,
    CardHeader,
    CardTitle,
    CardBody,
    Media,
    Row,
    Col,
    Button,
} from "reactstrap";
import { AgGridReact } from "ag-grid-react";
import { Link } from "react-router-dom";
import { connect } from "react-redux";
import Flatpickr from "react-flatpickr";
import { toast, Flip } from "react-toastify";
import "../../../assets/scss/pages/users.scss";
import { LoadingOutlined } from "@ant-design/icons";
import { Spin, Pagination } from "antd";

class View extends React.Component {
    state = {
        id: null,
        driverStatuses: [],
        status: null,
        data: {},
        states: [],
        trips: null,
        unitStatuses: [],
        page: 0,
        prevFiles: [],
        eldUnTil: null,
        statusChanged: false,
        total: 1,
        defaultColDef: {
            sortable: true,
            resizable: true,
            suppressMenu: false,
            tooltip: (params) => {
                return params.value;
            },
        },
        columnDefs: [
            {
                headerName: "Trip",
                field: "id",
                maxWidth: 70,
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
                maxWidth: 150,
                flex: 1,
            },
            {
                headerName: "Delivery",
                field: "delivery",
                maxWidth: 150,
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
                headerName: "From",
                field: "from",
                minWidth: 250,
                flex: 1,
            },
            {
                headerName: "To",
                field: "to",
                minWidth: 250,
                flex: 1,
            },
            {
                headerName: "Booked",
                field: "rcPrice",
                minWidth: 150,
                flex: 1,
            },
            {
                headerName: "Revised RC Price",
                field: "revisedRcPrice",
                minWidth: 150,
                flex: 1,
            },
        ],
    };
    updateTripsList = () => {
        this.setState({ loading: true });
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/trip/list?&truck_number=${this.props.match.params.id}&sort=id,DESC&size=40&page=${this.state.page}`,
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
                        driverId: el.driverId,
                        driver: el.driverName,
                        loadNumber: el.loadNumber,
                        loadId: el.loadId,
                        primaryPhone: el.phone,
                        driverAdvance: el.driverAdvance,
                        from: el.from,
                        to: el.to,
                        statusName: el.statusName,
                        statusColor: el.statusColor,
                        hiredOn: el.hireDate,
                        delivery: el.deliveryDateFormatted,
                        pickup: el.pickDateFormatted,
                        teammateName: el.teammateName,
                        teammateId: el.teammateId,
                        rcPrice: el.rcPrice,
                        revisedRcPrice: el.revisedRcPrice,
                    };
                    dataToShow.push(elToShow);
                });

                this.setState({
                    trips: dataToShow,
                    loading: false,
                    total: data.total_elements,
                });
            });
    };
    setStatus = (id) => {
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/unit/update_status/${this.state.id}/${id}`,
            {
                headers: {
                    Authorization: this.props.token,
                    "Content-Type": "application/json",
                },
                method: "PUT",
                body: JSON.stringify({
                    eldUnTil:
                        this.state.status === 7 &&
                        document.querySelector("#eldUnTil")
                            ? Date.parse(
                                  document.querySelector("#eldUnTil").value
                              )
                            : null,
                }),
            }
        ).then((res) => {
            if (res.ok) {
                toast.success("Status successfuly changed", {
                    transition: Flip,
                });
                this.setState({
                    status: id,
                    statusChanged: false,
                });
            } else {
                toast.error("Something went wrong", { transition: Flip });
                res.text();
            }
        });
    };
    pageChanged = (page) => {
        this.setState({
            page: page - 1,
        });
    };

    componentDidUpdate(prevProps, prevState) {
        if (this.state.page !== prevState.page) {
            this.updateTripsList();
        }
    }

    antIcon = (<LoadingOutlined style={{ fontSize: 44 }} spin />);

    componentDidMount() {
        this.updateTripsList();
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
                fetch(process.env.REACT_APP_BASE_URL + "/unit/context", {
                    headers: {
                        Authorization: this.props.token,
                    },
                })
                    .then((res) => res.json())
                    .then((data) => {
                        this.setState({
                            unitStatuses: data.unit_statuses,
                        });
                        fetch(
                            process.env.REACT_APP_BASE_URL +
                                `/unit/${this.props.match.params.id}`,
                            {
                                headers: {
                                    Authorization: this.props.token,
                                },
                            }
                        )
                            .then((res) => res.json())
                            .then((data) => {
                                if (data.files !== null) {
                                    let fileIds = new Map();
                                    let prevFiles = [];
                                    for (let key in data.files) {
                                        prevFiles.push({
                                            id: parseInt(key),
                                            name: data.files[key],
                                        });
                                        fileIds.set(
                                            parseInt(key),
                                            data.files[key]
                                        );
                                    }
                                    this.setState({
                                        prevFiles,
                                        id: data.id,
                                    });
                                }

                                this.setState({
                                    data: data,
                                    status: data.unitStatusId,
                                    eldUnTil: data.eldUnTil,
                                    initialLocation: data.initialLocation,
                                });
                            });
                    });
            });
    }

    render() {
        let { data } = this.state;
        return (
            <React.Fragment>
                <Row>
                    <Col sm="12">
                        <Card>
                            <CardHeader>
                                <CardTitle>Unit</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <Row className="mx-0" col="12">
                                    <Col className="pl-0" sm="12">
                                        <Media className="d-sm-flex d-block">
                                            <Media body>
                                                <Row>
                                                    <Col sm="9" md="6" lg="6">
                                                        <div className="users-page-view-table">
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    Number
                                                                </div>
                                                                <div>
                                                                    {
                                                                        data.number
                                                                    }
                                                                </div>
                                                            </div>
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    Type
                                                                </div>
                                                                <div className="text-truncate">
                                                                    <span>
                                                                        {
                                                                            data.unitTypeName
                                                                        }
                                                                    </span>
                                                                </div>
                                                            </div>
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    Ownership
                                                                    Type
                                                                </div>
                                                                <div className="text-truncate">
                                                                    <span>
                                                                        {
                                                                            data.unitName
                                                                        }
                                                                    </span>
                                                                </div>
                                                            </div>
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    Description
                                                                </div>
                                                                <div>
                                                                    {
                                                                        data.description
                                                                    }
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </Col>
                                                    <Col md="12" lg="6">
                                                        <div className="users-page-view-table">
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    Initial
                                                                    Location
                                                                </div>
                                                                <div>
                                                                    {this.state
                                                                        .initialLocation &&
                                                                        this.state.states.find(
                                                                            (
                                                                                item
                                                                            ) => {
                                                                                return (
                                                                                    item.id ===
                                                                                    parseInt(
                                                                                        this
                                                                                            .state
                                                                                            .initialLocation
                                                                                            .stateProvince
                                                                                    )
                                                                                );
                                                                            }
                                                                        ).name}
                                                                </div>
                                                            </div>
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    City
                                                                </div>
                                                                <div>
                                                                    <span>
                                                                        {this
                                                                            .state
                                                                            .initialLocation &&
                                                                            this
                                                                                .state
                                                                                .initialLocation
                                                                                .city}
                                                                    </span>
                                                                </div>
                                                            </div>
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    Street
                                                                </div>
                                                                <div>
                                                                    <span>
                                                                        {data.initialLocation &&
                                                                            data
                                                                                .initialLocation
                                                                                .street}
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </Col>
                                                </Row>
                                            </Media>
                                        </Media>
                                    </Col>
                                    <Col className="mt-1 pl-0 d-flex" sm="12">
                                        {this.props.userRole !==
                                            "dispatcher" && (
                                            <Link
                                                to={`/unit/edit/${this.props.match.params.id}`}
                                                className="d-flex align-items-center text-white mr-1"
                                            >
                                                <Button
                                                    color="primary"
                                                    className="d-flex align-items-center"
                                                    type="button"
                                                >
                                                    Edit Unit
                                                </Button>
                                            </Link>
                                        )}
                                        {this.state.status === 7 && (
                                            <>
                                                <Flatpickr
                                                    id="eldUnTil"
                                                    className="form-control eldUntil ml-1"
                                                    placeholder="ELD Until"
                                                    data-enable-time
                                                    options={{
                                                        dateFormat: "Z",
                                                        altInput: true,
                                                        altFormat: "m-d-Y H:i",
                                                    }}
                                                    value={this.state.eldUnTil}
                                                    onChange={(e) => {
                                                        this.setState({
                                                            eldUnTil:
                                                                Date.parse(
                                                                    e[0]
                                                                ),
                                                        });
                                                    }}
                                                />
                                            </>
                                        )}
                                        {this.state.statusChanged && (
                                            <Button
                                                color="success"
                                                className="d-flex align-items-center ml-1"
                                                type="button"
                                                onClick={() =>
                                                    this.setStatus(
                                                        this.state.status
                                                    )
                                                }
                                            >
                                                Submit
                                            </Button>
                                        )}
                                    </Col>
                                </Row>
                            </CardBody>
                        </Card>
                    </Col>
                    <Col sm="6">
                        <Card>
                            <CardHeader>
                                <CardTitle>Extra Info 1</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <div className="users-page-view-table">
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            VIN
                                        </div>
                                        <div>{data.vin}</div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Make
                                        </div>
                                        <div>{data.make}</div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Model
                                        </div>
                                        <div className="text-truncate">
                                            <span>{data.model}</span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Year Purchased
                                        </div>
                                        <div className="text-truncate">
                                            <span>{data.yearPurchased}</span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Year
                                        </div>
                                        <div className="text-truncate">
                                            <span>{data.year}</span>
                                        </div>
                                    </div>
                                </div>
                            </CardBody>
                        </Card>
                    </Col>

                    <Col sm="6">
                        <Card>
                            <CardHeader>
                                <CardTitle>Extra Info 2</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <div className="users-page-view-table">
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Make
                                        </div>
                                        <div>{data.make}</div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Purchase Price
                                        </div>
                                        <div className="text-truncate">
                                            <span>{data.purchasedPrice}</span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            License Plate Number
                                        </div>
                                        <div className="text-truncate">
                                            <span>
                                                {data.licensePlateNumbe}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            License Plate Expiration
                                        </div>
                                        <div className="text-truncate">
                                            <span>
                                                {
                                                    data.licenseExpirationFormatted
                                                }
                                            </span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Inspection Sticker Expiration
                                        </div>
                                        <div className="text-truncate">
                                            <span>
                                                {
                                                    data.inspectionStickerExpirationFormatted
                                                }
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </CardBody>
                        </Card>
                    </Col>
                    <Col sm="6">
                        <Card>
                            <CardHeader>
                                <CardTitle>Files</CardTitle>
                            </CardHeader>
                            <CardBody>
                                {this.state.prevFiles &&
                                    this.state.prevFiles.map((item) => (
                                        <div
                                            style={{
                                                flex: "30%",
                                                maxWidth: "30%",
                                                display: "flex",
                                                alignItems: "center",
                                            }}
                                        >
                                            <div
                                                style={{
                                                    width: 225,
                                                    cursor: "pointer",
                                                }}
                                                className="mt-1"
                                                href={`${window.location.origin}/file/${item.id}`}
                                                onClick={() =>
                                                    window.open(
                                                        `${window.location.origin}/file/${item.id}`,
                                                        "_blank"
                                                    )
                                                }
                                            >
                                                {item.name}
                                            </div>
                                        </div>
                                    ))}
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
                <Card className="overflow-hidden agGrid-card">
                    <div className="d-flex justify-content-between m-2">
                        <h3>Trips list</h3>
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
                                        defaultColDef={this.state.defaultColDef}
                                        columnDefs={this.state.columnDefs}
                                        rowData={this.state.trips}
                                        colResizeDefault={"shift"}
                                        animateRows={true}
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
            </React.Fragment>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth.login.token,
        userRole: state.auth.login.userRole,
    };
};
export default connect(mapStateToProps)(View);

import React, { Component } from "react";
import { connect } from "react-redux";
import Moment from "react-moment";
import {
    Row,
    Col,
    Card,
    CardBody,
    Button,
    Form,
    FormGroup,
    Label,
    CardHeader,
} from "reactstrap";
import { Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
import { AgGridReact } from "ag-grid-react";
import AsyncSelect from "react-select/async";
import Select from "react-select";
import Flatpickr from "react-flatpickr";
import * as Icon from "react-feather";
import { MDBRadio } from "mdb-react-ui-kit";

class Reports extends Component {
    state = {
        isActive: false,
        driver: null,
        unit: null,
        teams: [],
        team: null,
        companies: [],
        companysTruck: null,
        company: null,
        start: [],
        end: [],
        weekly: false,
        monthly: false,
        buttonLoading: false,
        data: null,
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
        columnDefs: [
            {
                headerName: "#",
                field: "serialNumber",
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
                headerName: "Carrier",
                children: [
                    {
                        headerName: "Name",
                        field: "carrierName",
                        minWidth: 90,
                        flex: 1,
                        filter: true,
                    },
                ],
            },
            {
                headerName: "Booked",
                children: [
                    {
                        headerName: "RC NO",
                        field: "rc",
                        filter: true,
                        minWidth: 160,
                        maxWidth: 170,
                        flex: 1,
                    },
                    {
                        headerName: "Company",
                        field: "shipperCompanyName",
                        filter: true,
                        minWidth: 200,
                        flex: 1,
                    },
                ],
            },
            {
                headerName: "Start",
                children: [
                    {
                        headerName: "Date/Time",
                        field: "timeStart",
                        filter: true,
                        minWidth: 130,
                        maxWidth: 140,
                        flex: 1,
                        cellRendererFramework: (params) => {
                            return (
                                <Moment format="YYYY-MM-DD HH:mm">
                                    {params.value}
                                </Moment>
                            );
                        },
                    },
                    {
                        headerName: "Location",
                        field: "shipperCompanyLocation",
                        filter: true,
                        minWidth: 240,
                        flex: 2,
                    },
                ],
            },
            {
                headerName: "End",
                children: [
                    {
                        headerName: "Date/Time",
                        field: "endTime",
                        filter: true,
                        minWidth: 130,
                        maxWidth: 140,
                        flex: 1,
                        cellRendererFramework: (params) => {
                            return (
                                <Moment format="YYYY-MM-DD HH:mm">
                                    {params.value}
                                </Moment>
                            );
                        },
                    },
                    {
                        headerName: "Location",
                        field: "endLocation",
                        filter: true,
                        minWidth: 240,
                        flex: 2,
                    },
                ],
            },
            {
                headerName: "Truck",
                children: [
                    {
                        headerName: "Number",
                        field: "truckNumber",
                        filter: true,
                        minWidth: 120,
                        flex: 1,
                    },
                ],
            },
            {
                headerName: "Price",
                children: [
                    {
                        headerName: "Booked",
                        field: "booked",
                        filter: true,
                        minWidth: 100,
                        flex: 1,
                        cellRendererFramework: function (params) {
                            return `${params.value} $`;
                        },
                    },
                    {
                        headerName: "Dispute",
                        field: "dispute",
                        filter: true,
                        minWidth: 100,
                        flex: 1,
                        cellRendererFramework: function (params) {
                            return `${params.value} $`;
                        },
                    },
                    {
                        headerName: "Detention",
                        field: "detention",
                        filter: true,
                        minWidth: 100,
                        flex: 1,
                        cellRendererFramework: function (params) {
                            return `${params.value} $`;
                        },
                    },
                    {
                        headerName: "Additional",
                        field: "additional",
                        filter: true,
                        minWidth: 100,
                        flex: 1,
                        cellRendererFramework: function (params) {
                            return `${params.value} $`;
                        },
                    },
                    {
                        headerName: "Fine",
                        field: "fine",
                        filter: true,
                        minWidth: 100,
                        flex: 1,
                        cellRendererFramework: function (params) {
                            return `${params.value} $`;
                        },
                    },
                    {
                        headerName: "Revised/Invoice",
                        field: "revisedInvoice",
                        filter: true,
                        minWidth: 140,
                        flex: 1,
                        cellRendererFramework: function (params) {
                            return `${params.value} $`;
                        },
                    },
                    {
                        headerName: "K - O",
                        field: "ko",
                        filter: true,
                        minWidth: 100,
                        flex: 1,
                        cellRendererFramework: function (params) {
                            return `${params.value} $`;
                        },
                    },
                ],
            },
            {
                headerName: "Factoring",
                children: [
                    {
                        headerName: "Factoring",
                        field: "amount",
                        filter: true,
                        minWidth: 100,
                        flex: 1,
                        cellRendererFramework: function (params) {
                            return `${params.value} $`;
                        },
                    },
                    {
                        headerName: "Tafs",
                        field: "service",
                        filter: true,
                        minWidth: 100,
                        flex: 1,
                        cellRendererFramework: function (params) {
                            return `${params.value} $`;
                        },
                    },
                    {
                        headerName: "Net Paid",
                        field: "netPaid",
                        filter: true,
                        minWidth: 100,
                        flex: 1,
                        cellRendererFramework: function (params) {
                            return `${params.value} $`;
                        },
                    },
                ],
            },
            {
                headerName: "Team",
                children: [
                    {
                        headerName: "Name",
                        field: "team",
                        filter: true,
                        minWidth: 100,
                        flex: 1,
                    },
                ],
            },
            {
                headerName: "Note",
                children: [
                    {
                        headerName: "Note",
                        field: "note",
                        filter: true,
                        minWidth: 200,
                        flex: 1,
                    },
                ],
            },
        ],
    };
    componentDidMount() {
        fetch(
            process.env.REACT_APP_BASE_URL +
                "/owned_company/all_by_visibility?sort=id,DESC&size=10000",
            {
                headers: {
                    Authorization: this.props.token,
                    "Content-Type": "application/json",
                },
                method: "GET",
            }
        )
            .then((res) => res.json())
            .then((data) => {
                let companies = data.map((item) => {
                    return {
                        value: item.id,
                        label: item.name,
                    };
                });
                this.setState({ companies });
            });
        fetch(process.env.REACT_APP_BASE_URL + "/team/all_by_visibility", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "GET",
        })
            .then((res) => res.json())
            .then((data) => {
                let teams = data.data.map((item) => {
                    return {
                        value: item.id,
                        label: item.name,
                    };
                });
                this.setState({ teams });
            });
    }

    loadOptions = (inputValue, callback) => {
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/unit/search_by_number?q=${inputValue}`,
            {
                headers: {
                    Authorization: this.props.token,
                    "Content-Type": "application/json",
                },
                method: "GET",
            }
        )
            .then((res) => res.json())
            .then((data) =>
                callback(
                    data.map((el) => {
                        return {
                            ...el,
                            value: el.id,
                            label: el.number,
                        };
                    })
                )
            );
    };
    loadDrivers = (inputValue, callback) => {
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/driver/list?sort=id,DESC&size=10000&page=0&searchNameText=${inputValue}`
        )
            .then((res) => res.json())
            .then((data) =>
                callback(
                    data.content.map((el) => {
                        return {
                            ...el,
                            value: el.id,
                            label: `${el.firstName} ${el.lastName}`,
                        };
                    })
                )
            );
    };
    getTable = () => {
        this.setState({ loading: true, data: [] });

        let { driver, unit, team, company, companysTruck, start, end } =
            this.state;

        fetch(
            process.env.REACT_APP_BASE_URL +
                `/accounting/info?${
                    start ? `startTime=${Date.parse(start)}` : ""
                }${end ? `&endTime=${Date.parse(end)}` : ""}${
                    driver ? `&driver_id=${driver}` : ""
                }${unit ? `&truck_number=${unit}` : ""}${
                    team ? `&team_id=${team}` : ""
                }${company ? `&carrier=${company}` : ""}${
                    companysTruck
                        ? `&all_by_companys_truck=${companysTruck}`
                        : ""
                }`,
            {
                headers: {
                    Authorization: this.props.token,
                },
            }
        )
            .then((res) => res.json())
            .then((data) => {
                // console.log(data);
                this.setState({ data, loading: false });
            })
            .catch((err) => {
                console.log(err);
            });
    };

    componentDidUpdate(prevProps, prevState) {
        let { driver, unit, team, company, companysTruck, start, end } =
            this.state;
        if (
            start !== prevState.start ||
            end !== prevState.end ||
            driver !== prevState.driver ||
            unit !== prevState.unit ||
            team !== prevState.team ||
            company !== prevState.company ||
            companysTruck !== prevState.companysTruck
        ) {
            if (
                start.length &&
                end.length &&
                (driver || unit || team || company || companysTruck)
            ) {
                this.getTable();
            }
        }
    }

    getFile = () => {
        this.setState({
            buttonLoading: true,
        });
        let { driver, unit, team, company, companysTruck, start, end } =
            this.state;

        let weekly = document.getElementById("weekly").checked;

        fetch(
            process.env.REACT_APP_BASE_URL +
                `/accounting?${start ? `startTime=${Date.parse(start)}` : ""}${
                    end ? `&endTime=${Date.parse(end)}` : ""
                }${driver ? `&driver_id=${driver}` : ""}${
                    unit ? `&truck_number=${unit}` : ""
                }${team ? `&team_id=${team}` : ""}${
                    company ? `&carrier=${company}` : ""
                }${
                    companysTruck
                        ? `&all_by_companys_truck=${companysTruck}`
                        : ""
                }${weekly ? `&weekly=${weekly}` : ""}`,
            {
                headers: {
                    Authorization: this.props.token,
                },
            }
        )
            .then((res) => res.blob())
            .then((blob) => {
                // let blobUrl = URL.createObjectURL(blob);
                // window.open(blobUrl);
                let anchor = document.createElement("a");

                anchor.download = "report.xlsx";
                anchor.href = (window.webkitURL || window.URL).createObjectURL(
                    blob
                );
                anchor.dataset.downloadurl = [
                    "text/plain",
                    anchor.download,
                    anchor.href,
                ].join(":");
                anchor.click();
                this.setState({
                    buttonLoading: false,
                });
            })
            .catch((err) => {
                this.setState({
                    buttonLoading: false,
                });
            });
    };
    antIcon = (<LoadingOutlined style={{ fontSize: 44 }} spin />);

    render() {
        let { driver, unit, team, company, companysTruck, start, end } =
            this.state;
        let isActive =
            start.length &&
            end.length &&
            (driver || unit || team || company || companysTruck);
        return (
            <>
                <div className="invoice-preview-wrapper">
                    <Row className="invoice-preview">
                        <Col xl={9} md={8} sm={12}>
                            <Card className="invoice-preview-card">
                                <CardHeader
                                    style={{
                                        paddingBottom: "1.5rem",
                                        borderBottom: "1px solid #ededed",
                                    }}
                                >
                                    <h1 className="invoice-logo mb-0 mr-1">
                                        Get Report
                                    </h1>
                                    <Icon.Clipboard size={25} />
                                </CardHeader>
                                <CardBody className="invoice-padding pb-2">
                                    <Form>
                                        <Row>
                                            <Col md="6" sm="12">
                                                <FormGroup>
                                                    <Label for="from">
                                                        Start
                                                    </Label>
                                                    <Flatpickr
                                                        id="from"
                                                        className="form-control"
                                                        data-enable-time
                                                        options={{
                                                            dateFormat: "Z",
                                                            altInput: true,
                                                            altFormat:
                                                                "m-d-Y H:i",
                                                        }}
                                                        value={this.state.start}
                                                        onChange={(val) =>
                                                            this.setState({
                                                                start: val,
                                                            })
                                                        }
                                                    />
                                                </FormGroup>
                                            </Col>
                                            <Col md="6" sm="12">
                                                <FormGroup>
                                                    <Label for="lastNameMulti">
                                                        End
                                                    </Label>
                                                    <Flatpickr
                                                        id="until"
                                                        className="form-control"
                                                        data-enable-time
                                                        options={{
                                                            dateFormat: "Z",
                                                            altInput: true,
                                                            altFormat:
                                                                "m-d-Y H:i",
                                                        }}
                                                        value={this.state.end}
                                                        onChange={(val) =>
                                                            this.setState({
                                                                end: val,
                                                            })
                                                        }
                                                    />
                                                </FormGroup>
                                            </Col>
                                            <Col md="6" sm="12">
                                                <FormGroup>
                                                    <Label for="cityMulti">
                                                        Truck
                                                    </Label>
                                                    <AsyncSelect
                                                        noOptionsMessage={(
                                                            value
                                                        ) =>
                                                            !value.inputValue
                                                                ? "type something to search"
                                                                : "nothing to show"
                                                        }
                                                        placeholder="Search"
                                                        isClearable={true}
                                                        defaultValue={null}
                                                        loadOptions={
                                                            this.loadOptions
                                                        }
                                                        onChange={(value) => {
                                                            if (
                                                                value !== null
                                                            ) {
                                                                this.setState({
                                                                    unit: value.value,
                                                                });
                                                            } else
                                                                this.setState({
                                                                    unit: null,
                                                                });
                                                        }}
                                                        theme={(theme) => ({
                                                            ...theme,
                                                            colors: {
                                                                ...theme.colors,
                                                                primary50:
                                                                    "#fe810b",
                                                                primary25:
                                                                    "rgb(253, 179, 46)",
                                                                primary:
                                                                    "rgb(253, 179, 46)",
                                                            },
                                                        })}
                                                    />
                                                </FormGroup>
                                            </Col>
                                            <Col md="6" sm="12">
                                                <FormGroup>
                                                    <Label for="driver">
                                                        Drivers
                                                    </Label>
                                                    <AsyncSelect
                                                        noOptionsMessage={(
                                                            value
                                                        ) =>
                                                            !value.inputValue
                                                                ? "type something to search"
                                                                : "nothing to show"
                                                        }
                                                        placeholder="Search"
                                                        isClearable={true}
                                                        defaultValue={null}
                                                        loadOptions={
                                                            this.loadDrivers
                                                        }
                                                        onChange={(value) => {
                                                            if (
                                                                value !== null
                                                            ) {
                                                                this.setState({
                                                                    driver: value.value,
                                                                });
                                                            } else
                                                                this.setState({
                                                                    driver: null,
                                                                });
                                                        }}
                                                        theme={(theme) => ({
                                                            ...theme,
                                                            colors: {
                                                                ...theme.colors,
                                                                primary50:
                                                                    "#fe810b",
                                                                primary25:
                                                                    "rgb(253, 179, 46)",
                                                                primary:
                                                                    "rgb(253, 179, 46)",
                                                            },
                                                        })}
                                                    />
                                                </FormGroup>
                                            </Col>
                                            <Col md="6" sm="12">
                                                <FormGroup>
                                                    <Label for="company">
                                                        Company
                                                    </Label>
                                                    <Select
                                                        id="company"
                                                        className="React"
                                                        classNamePrefix="select"
                                                        name="color"
                                                        options={
                                                            this.state.companies
                                                        }
                                                        isClearable={true}
                                                        onChange={(value) => {
                                                            if (
                                                                value !== null
                                                            ) {
                                                                this.setState({
                                                                    company:
                                                                        value.value,
                                                                });
                                                            } else {
                                                                this.setState({
                                                                    company:
                                                                        null,
                                                                });
                                                            }
                                                        }}
                                                        theme={(theme) => ({
                                                            ...theme,
                                                            colors: {
                                                                ...theme.colors,
                                                                primary50:
                                                                    "#fe810b",
                                                                primary25:
                                                                    "rgb(253, 179, 46)",
                                                                primary:
                                                                    "rgb(253, 179, 46)",
                                                            },
                                                        })}
                                                    />
                                                </FormGroup>
                                            </Col>
                                            <Col md="6" sm="12">
                                                <FormGroup>
                                                    <Label for="team">
                                                        Team
                                                    </Label>
                                                    <Select
                                                        id="team"
                                                        className="React"
                                                        classNamePrefix="select"
                                                        name="color"
                                                        options={
                                                            this.state.teams
                                                        }
                                                        isClearable={true}
                                                        onChange={(value) => {
                                                            if (
                                                                value !== null
                                                            ) {
                                                                this.setState({
                                                                    team: value.value,
                                                                });
                                                            } else {
                                                                this.setState({
                                                                    team: null,
                                                                });
                                                            }
                                                        }}
                                                        theme={(theme) => ({
                                                            ...theme,
                                                            colors: {
                                                                ...theme.colors,
                                                                primary50:
                                                                    "#fe810b",
                                                                primary25:
                                                                    "rgb(253, 179, 46)",
                                                                primary:
                                                                    "rgb(253, 179, 46)",
                                                            },
                                                        })}
                                                    />
                                                </FormGroup>
                                            </Col>
                                            <Col md="6" sm="12">
                                                <FormGroup>
                                                    <Label for="company">
                                                        Company's truck
                                                    </Label>
                                                    <Select
                                                        id="company"
                                                        className="React"
                                                        classNamePrefix="select"
                                                        name="color"
                                                        options={
                                                            this.state.companies
                                                        }
                                                        isClearable={true}
                                                        onChange={(value) => {
                                                            if (
                                                                value !== null
                                                            ) {
                                                                this.setState({
                                                                    companysTruck:
                                                                        value.value,
                                                                });
                                                            } else {
                                                                this.setState({
                                                                    companysTruck:
                                                                        null,
                                                                });
                                                            }
                                                        }}
                                                        theme={(theme) => ({
                                                            ...theme,
                                                            colors: {
                                                                ...theme.colors,
                                                                primary50:
                                                                    "#fe810b",
                                                                primary25:
                                                                    "rgb(253, 179, 46)",
                                                                primary:
                                                                    "rgb(253, 179, 46)",
                                                            },
                                                        })}
                                                    />
                                                </FormGroup>
                                            </Col>
                                            <Col>
                                                <FormGroup>
                                                    <Label for="team">
                                                        Type
                                                    </Label>
                                                    <MDBRadio
                                                        name="flexRadioDefault"
                                                        id="weekly"
                                                        label="weekly"
                                                        defaultChecked
                                                    />
                                                    <MDBRadio
                                                        name="flexRadioDefault"
                                                        id="monthly"
                                                        label="monthly"
                                                    />
                                                </FormGroup>
                                            </Col>
                                        </Row>
                                    </Form>
                                </CardBody>
                            </Card>
                        </Col>
                        <Col xl={3} md={4} sm={12}>
                            <Card className="invoice-action-wrapper">
                                <CardBody className="d-flex justify-content-center align-items-center">
                                    <Button.Ripple
                                        onClick={() => this.getFile()}
                                        color={
                                            isActive &&
                                            !this.state.buttonLoading
                                                ? "primary"
                                                : "secondary"
                                        }
                                        disabled={
                                            isActive &&
                                            !this.state.buttonLoading
                                                ? false
                                                : true
                                        }
                                        outline={
                                            isActive &&
                                            !this.state.buttonLoading
                                                ? false
                                                : true
                                        }
                                    >
                                        {this.state.buttonLoading
                                            ? "Loading..."
                                            : "Download"}
                                    </Button.Ripple>
                                </CardBody>
                            </Card>
                        </Col>
                    </Row>
                </div>
                {this.state.data && (
                    <Card className="agGrid-card">
                        <div className="d-flex justify-content-between align-items-center mt-2 mx-2 mb-1">
                            <h3 className="mb-0">Report</h3>
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
                                    <div className="ag-theme-material w-100 ag-grid-table mb-1 table-with-header-left">
                                        <AgGridReact
                                            enableCellTextSelection="true"
                                            reactNext={true}
                                            rowSelection="multiple"
                                            defaultColDef={
                                                this.state.defaultColDef
                                            }
                                            columnDefs={this.state.columnDefs}
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
                )}
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
export default connect(mapStateToProps)(Reports);

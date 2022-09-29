import React from "react";
import {
    // Form,
    Form,
    Button,
    FormGroup,
    Input,
    Col,
    Card,
    CardBody,
    CardHeader,
    // Button
} from "reactstrap";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import { toast, Flip } from "react-toastify";
import Select from "react-select";
import Checkbox from "../../../components/@vuexy/checkbox/CheckboxesVuexy";
import DataTable from "react-data-table-component";
import { Link } from "react-router-dom";
import AsyncSelect from "react-select/async";
import { Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
class NewTrip extends React.Component {
    state = {
        defaultChecked: [],
        states: [],
        drivers: [],
        driver: null,
        driverId: null,
        ownedCompanies: [],
        secondDriver: null,
        trucks: [],
        truckId: null,
        truckInfo: null,
        loads: [],
        loadsOfTrip: [],
        prevLoads: [],
        loading: true,
        choosedLoadsByNumber: [],
        loadsColumns: [
            {
                name: "Custom Load Number",
                selector: "customLoadNumber",
                sortable: true,
            },
            {
                name: "Customer",
                selector: "customer",
                sortable: true,
            },
            {
                name: "Pickup",
                selector: "pickupDateFormatted",
                sortable: true,
            },
            {
                name: "Delivery",
                selector: "deliveryDateFormatted",
                sortable: true,
            },
            {
                name: "From",
                selector: "from",
                sortable: true,
            },
            {
                name: "To",
                selector: "to",
                sortable: true,
            },
        ],
    };
    chooseLoad = (value) => {
        this.setState({
            choosedLoadsByNumber: value.selectedRows.map((item) => item.id),
        });
    };
    secondDriverChange = (val) => {
        if (val == null) {
            this.setState({
                secondDriver: null,
            });
        } else {
            this.setState({
                secondDriver: val,
            });
        }
    };
    newTrip = () => {
        let secondDriver =
            this.state.secondDriver == null
                ? null
                : this.state.secondDriver.value;
        let data = {
            id: parseInt(this.props.match.params.id),
            driverId: this.state.driverId,
            loadIds: this.state.choosedLoadsByNumber,
            truckId: this.state.truckId,
            driverInstructions: document.querySelector("#driverInstructions")
                .value,
            secondDriverId: secondDriver,
        };
        fetch(process.env.REACT_APP_BASE_URL + "/trip", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "PUT",
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.ok) {
                toast.success("Trip successfuly added", { transition: Flip });
                window.history.back();
            } else {
                toast.error("Something went wrong", { transition: Flip });
                res.text();
            }
        });
    };

    loadOptions = (inputValue, callback) => {
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/unit/search_by_number?q=${inputValue}`
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

    componentDidMount() {
        fetch(process.env.REACT_APP_BASE_URL + "/state_province/all", {
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
        fetch(
            process.env.REACT_APP_BASE_URL +
                "/load/list?sort=id,DESC&size=10000",
            {
                headers: {
                    Authorization: this.props.token,
                },
            }
        )
            .then((res) => res.json())
            .then((data) => {
                this.setState({ prevLoads: data.content });
            });
        fetch(process.env.REACT_APP_BASE_URL + "/trip/context", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    drivers: data.drivers,
                    ownedCompanies: data.owned_companies,
                });
                let drivers = [];
                data.drivers.forEach((el) => {
                    let elToShow = {
                        value: el.id,
                        label: el.lastName + " " + el.firstName,
                    };
                    drivers.push(elToShow);
                });
                this.setState({
                    drivers,
                });
                fetch(
                    process.env.REACT_APP_BASE_URL +
                        `/trip/edit_context/${this.props.match.params.id}`,
                    {
                        headers: {
                            Authorization: this.props.token,
                        },
                    }
                )
                    .then((res) => res.json())
                    .then((data) => {
                        this.setState({
                            loading: false,
                        });
                        let driver = this.state.drivers.filter((obj) => {
                            return obj.value === parseInt(data.trip.driverId);
                        });

                        let secondDriver = this.state.drivers.filter((obj) => {
                            return obj.value === parseInt(data.trip.teammateId);
                        });

                        let loads = this.state.prevLoads;
                        data.loads.forEach((item) => {
                            item.isSelected = true;
                            loads.push(item);
                        });
                        if (document.getElementById("driverInstructions"))
                            document.getElementById(
                                "driverInstructions"
                            ).value = data.trip.driverInstructions;

                        this.setState({
                            secondDriver: secondDriver[0],
                            truckInfo: {
                                value: data.trip.truckId,
                                label: data.unit_number,
                            },
                            truckId: data.trip.truckId,
                            driver: driver[0],
                            driverId: data.trip.driverId,
                            loads: loads,
                            loadsOfTrip: data.loads,
                        });
                    });
            });
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevState.truckInfo !== this.state.truckInfo) {
            if (this.state.truckInfo && this.state.truckInfo.driverId) {
                let result = this.state.drivers.filter((obj) => {
                    return (
                        obj.value === parseInt(this.state.truckInfo.driverId)
                    );
                });
                if (result)
                    this.setState({
                        driver: result[0],
                        driverId: this.state.truckInfo.driverId,
                    });
            }
            if (this.state.truckInfo && this.state.truckInfo.secondDriverId) {
                let result = this.state.drivers.filter((obj) => {
                    return (
                        obj.value ===
                        parseInt(this.state.truckInfo.secondDriverId)
                    );
                });
                if (result) this.setState({ secondDriver: result[0] });
            }
        }
    }
    render() {
        return (
            <Card>
                <CardHeader>
                    <h3 className="mb-0">Editing Trip</h3>
                </CardHeader>
                <CardBody>
                    {this.state.loading ? (
                        <Spin
                            indicator={
                                <LoadingOutlined
                                    style={{ fontSize: 44 }}
                                    spin
                                />
                            }
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
                            <Form className="d-flex">
                                <div style={{ width: "50%", marginRight: 20 }}>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Truck*</span>
                                        </Col>
                                        <Col md="8">
                                            <AsyncSelect
                                                noOptionsMessage={(value) =>
                                                    !value.inputValue
                                                        ? "type something to search"
                                                        : "nothing to show"
                                                }
                                                placeholder="Search"
                                                isClearable={true}
                                                value={this.state.truckInfo}
                                                loadOptions={this.loadOptions}
                                                onChange={(value) => {
                                                    if (value !== null) {
                                                        this.setState({
                                                            truckId:
                                                                value.value,
                                                            truckInfo: value,
                                                        });
                                                    } else
                                                        this.setState({
                                                            truckId: null,
                                                            truckInfo: null,
                                                        });
                                                }}
                                                theme={(theme) => ({
                                                    ...theme,
                                                    colors: {
                                                        ...theme.colors,
                                                        primary50: "#fe810b",
                                                        primary25:
                                                            "rgb(253, 179, 46)",
                                                        primary:
                                                            "rgb(253, 179, 46)",
                                                    },
                                                })}
                                            />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup row>
                                        <Col md="4">
                                            <span>Driver Instructions</span>
                                        </Col>
                                        <Col md="8">
                                            <Input
                                                type="textarea"
                                                id="driverInstructions"
                                                placeholder=""
                                                maxLength="1000"
                                            />
                                        </Col>
                                    </FormGroup>
                                </div>
                                <div style={{ flex: 1 }}>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Driver*</span>
                                        </Col>
                                        <Col md="8">
                                            <Select
                                                className="React"
                                                classNamePrefix="select"
                                                name="color"
                                                options={this.state.drivers}
                                                value={this.state.driver}
                                                isClearable={true}
                                                onChange={(value) => {
                                                    if (value !== null) {
                                                        this.setState({
                                                            driverId:
                                                                value.value,
                                                            driver: value,
                                                        });
                                                    } else {
                                                        this.setState({
                                                            driverId: null,
                                                            driver: null,
                                                        });
                                                    }
                                                }}
                                                theme={(theme) => ({
                                                    ...theme,
                                                    colors: {
                                                        ...theme.colors,
                                                        primary50: "#fe810b",
                                                        primary25:
                                                            "rgb(253, 179, 46)",
                                                        primary:
                                                            "rgb(253, 179, 46)",
                                                    },
                                                })}
                                            />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Second driver(optimal)</span>
                                        </Col>
                                        <Col md="8">
                                            <Select
                                                className="React"
                                                classNamePrefix="select"
                                                name="color"
                                                value={this.state.secondDriver}
                                                options={this.state.drivers}
                                                isClearable={true}
                                                onChange={(val) => {
                                                    if (val == null) {
                                                        this.setState({
                                                            secondDriver: null,
                                                        });
                                                    } else {
                                                        this.setState({
                                                            secondDriver: val,
                                                        });
                                                    }
                                                }}
                                                theme={(theme) => ({
                                                    ...theme,
                                                    colors: {
                                                        ...theme.colors,
                                                        primary50: "#fe810b",
                                                        primary25:
                                                            "rgb(253, 179, 46)",
                                                        primary:
                                                            "rgb(253, 179, 46)",
                                                    },
                                                })}
                                            />
                                        </Col>
                                    </FormGroup>
                                </div>
                            </Form>
                            <DataTable
                                data={this.state.loads}
                                columns={this.state.loadsColumns}
                                noHeader
                                selectableRows
                                onSelectedRowsChange={this.chooseLoad}
                                selectableRowSelected={(row) => row.isSelected}
                                selectableRowsPreSelectedField="selected"
                                selectableRowsComponent={Checkbox}
                                selectableRowsComponentProps={{
                                    color: "primary",
                                    icon: (
                                        <Icon.Check
                                            className="vx-icon"
                                            size={12}
                                        />
                                    ),
                                    label: "",
                                    size: "sm",
                                }}
                            />
                            <div className="mt-3">Edit Load</div>
                            {this.state.loadsOfTrip &&
                                this.state.loadsOfTrip.map((item) => {
                                    return (
                                        <>
                                            &nbsp;
                                            <Link to={`/loads/edit/${item.id}`}>
                                                {item.customLoadNumber}
                                            </Link>
                                            &nbsp;
                                        </>
                                    );
                                })}
                            <Button
                                color="success"
                                className="d-flex align-items-center mt-4"
                                type="button"
                                onClick={() => this.newTrip()}
                            >
                                <Icon.Check size={22} /> Save Trip
                            </Button>
                        </>
                    )}
                </CardBody>
            </Card>
        );
    }
}
const mapStateToProps = (state) => {
    return {
        token: state.auth.login.token,
    };
};
export default connect(mapStateToProps)(NewTrip);
